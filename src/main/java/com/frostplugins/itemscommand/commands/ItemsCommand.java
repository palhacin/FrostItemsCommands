package com.frostplugins.itemscommand.commands;

import com.frostplugins.itemscommand.Terminal;
import com.frostplugins.itemscommand.interfaces.Interface;
import com.frostplugins.itemscommand.loader.ItemsCommandLoader;
import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemsCommand extends Command {

    private final Terminal instance;

    public ItemsCommand(Terminal instance) {
        super(instance.getConfig().getString("command.name"));
        setAliases(instance.getConfig().getStringList("command.aliases"));
        setUsage(instance.getConfig().getString("command.usage").replace("&", "§"));
        this.instance = instance;
    }


    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                ((Player) sender).openInventory(Interface.get(0));
                return true;
            }
        }

        if (args.length < 2) {
            sender.sendMessage(getUsage());
            return false;
        }

        String playerName = args[0];
        String itemKey = args[1];
        ItemsCommandObject itemCommand = getItemByKey(itemKey);

        if (itemCommand == null) {
            sender.sendMessage("§cEste item não existe na config 'items.yml'");
            return true;
        }

        int argIndex = 2;

        String subItem = null;
        if (itemCommand.getSubItems() != null && !itemCommand.getSubItems().isEmpty()) {
            if (args.length <= argIndex) {
                sender.sendMessage(getUsage());
                sender.sendMessage("");
                sender.sendMessage("§cVocê precisa especificar o sub-item para este item");
                sender.sendMessage("");
                return true;
            }

            subItem = args[argIndex++];
            if (!itemCommand.getSubItems().contains(subItem)) {
                sender.sendMessage(getUsage());
                sender.sendMessage("");
                sender.sendMessage("§cEste sub-item não existe, utilize um dos: §f" + itemCommand.getSubItems());
                sender.sendMessage("");
                return true;
            }
        }

        String integerStr = null;
        if (itemCommand.getAttributes() != null && itemCommand.getAttributes().contains("integer")) {
            if (args.length <= argIndex) {
                sender.sendMessage(getUsage());
                sender.sendMessage("");
                sender.sendMessage("§cVocê precisa especificar o valor do atributo");
                sender.sendMessage("");
                return true;
            }

            integerStr = args[argIndex++];
            if (!integerStr.matches("\\d+")) {
                sender.sendMessage("");
                sender.sendMessage("§cAtributo inválido, deve ser um número inteiro");
                sender.sendMessage("");
                return true;
            }
        }

        if (args.length <= argIndex) {
            sender.sendMessage(getUsage());
            sender.sendMessage("");
            sender.sendMessage("§cVocê precisa especificar a quantidade de item");
            sender.sendMessage("");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[argIndex]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cQuantidade inválida, coloque um número inteiro");
            return true;
        }

        ItemStack item = ItemsCommandObject.createItem(itemCommand, subItem, integerStr);
        if (item == null) {
            sender.sendMessage("§cHouve uma falha ao criar o item.");
            return true;
        }

        item.setAmount(amount);

        Player target = Bukkit.getPlayer(playerName);
        if (target != null && target.isOnline()) {
            target.getInventory().addItem(item);
            if (instance.getConfig().getString("messages.received-item") != null
                    && !instance.getConfig().getString("messages.received-item").isEmpty()) {
                target.sendMessage(instance.getConfig().getString("messages.received-item")
                        .replace("&", "§")
                        .replace("{player}", sender.getName())
                        .replace("{item}", item.getItemMeta().getDisplayName())
                        .replace("{amount}", String.valueOf(amount)));
            }

            if (target != sender) {
                if (instance.getConfig().getString("messages.gave-item") != null
                        && !instance.getConfig().getString("messages.gave-item").isEmpty()) {
                    sender.sendMessage(instance.getConfig().getString("messages.gave-item")
                            .replace("&", "§")
                            .replace("{player}", target.getDisplayName())
                            .replace("{item}", item.getItemMeta().getDisplayName())
                            .replace("{amount}", String.valueOf(amount)));
                }
            }
        } else {
            sender.sendMessage("§cEste jogador não está online");
        }

        return true;
    }

    private ItemsCommandObject getItemByKey(String itemKey) {
        for (ItemsCommandObject item : ItemsCommandLoader.getItemsList()) {
            if (itemKey.equalsIgnoreCase(item.getKey())) {
                return item;
            }
        }
        return null;
    }
}
