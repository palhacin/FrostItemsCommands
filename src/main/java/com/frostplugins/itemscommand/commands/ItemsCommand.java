package com.frostplugins.itemscommand.commands;

import com.frostplugins.itemscommand.interfaces.Interface;
import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import com.frostplugins.itemscommand.services.ItemsCommandService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemsCommand extends Command {

    private static FileConfiguration config;;

    public ItemsCommand(FileConfiguration config) {
        super(config.getString("command.name"));
        setAliases(config.getStringList("command.aliases"));
        setUsage(config.getString("command.usage").replace("&", "§"));
        setPermission(config.getString("command.permission"));
        setPermissionMessage(config.getString("command.permission-message").replace("&", "§"));
        ItemsCommand.config = config;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getPermissionMessage());
            return true;
        }

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
        ItemsCommandObject itemCommand = getItemCommand(itemKey, sender);

        if (itemCommand == null) return true;

        String subItem = getSubItem(args, itemCommand, sender);
        if (subItem == null) return true;

        String attributeStr = getIntegerAttribute(args, itemCommand, sender);
        if (attributeStr == null) return true;

        int amount = getItemAmount(args, sender);
        if (amount == -1) return true;

        return giveItemToPlayer(sender, playerName, itemCommand, subItem, attributeStr, amount);
    }

    private ItemsCommandObject getItemCommand(String itemKey, CommandSender sender) {
        ItemsCommandObject itemCommand = ItemsCommandService.getItemCommandsByKey(itemKey);
        if (itemCommand == null) {
            sender.sendMessage("§cEste item não existe na config 'items.yml'");
        }
        return itemCommand;
    }

    private String getSubItem(String[] args, ItemsCommandObject itemCommand, CommandSender sender) {
        if (itemCommand.getSubItems() == null || itemCommand.getSubItems().isEmpty()) return null;

        if (args.length <= 2) {
            sender.sendMessage(getUsage());
            sender.sendMessage("§cVocê precisa especificar o sub-item para este item");
            return null;
        }

        String subItem = args[2];
        if (!itemCommand.getSubItems().contains(subItem)) {
            sender.sendMessage(getUsage());
            sender.sendMessage("§cEste sub-item não existe, utilize um dos: §f" + itemCommand.getSubItems());
            return null;
        }
        return subItem;
    }

    private String getIntegerAttribute(String[] args, ItemsCommandObject itemCommand, CommandSender sender) {
        if (itemCommand.getAttributes() == null || !itemCommand.getAttributes().contains("integer")) return null;

        if (args.length <= 3) {
            sender.sendMessage(getUsage());
            sender.sendMessage("§cVocê precisa especificar o valor do atributo");
            return null;
        }

        String integerStr = args[3];
        if (!integerStr.matches("\\d+")) {
            sender.sendMessage("§cAtributo inválido, deve ser um número inteiro");
            return null;
        }
        return integerStr;
    }

    private int getItemAmount(String[] args, CommandSender sender) {
        if (args.length <= 4) {
            sender.sendMessage(getUsage());
            sender.sendMessage("§cVocê precisa especificar a quantidade de item");
            return -1;
        }

        try {
            return Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cQuantidade inválida, coloque um número inteiro");
            return -1;
        }
    }

    private boolean giveItemToPlayer(CommandSender sender, String playerName, ItemsCommandObject itemCommand,
                                     String subItem, String integerStr, int amount) {
        ItemStack item = ItemsCommandObject.createItem(itemCommand, subItem, integerStr);
        if (item == null) {
            sender.sendMessage("§cHouve uma falha ao criar o item.");
            return false;
        }

        item.setAmount(amount);
        Player target = Bukkit.getPlayer(playerName);

        if (target != null && target.isOnline()) {
            target.getInventory().addItem(item);
            sendItemReceivedMessage(target, sender, item, amount);
            if (target != sender) {
                sendItemGaveMessage(sender, target, item, amount);
            }
        } else {
            sender.sendMessage("§cEste jogador não está online");
        }
        return true;
    }

    private void sendItemReceivedMessage(Player target, CommandSender sender, ItemStack item, int amount) {
        String message = config.getString("messages.received-item");
        if (message != null && !message.isEmpty()) {
            target.sendMessage(message.replace("&", "§")
                    .replace("{player}", sender.getName())
                    .replace("{item}", item.getItemMeta().getDisplayName())
                    .replace("{amount}", String.valueOf(amount)));
        }
    }

    private void sendItemGaveMessage(CommandSender sender, Player target, ItemStack item, int amount) {
        String message = config.getString("messages.gave-item");
        if (message != null && !message.isEmpty()) {
            sender.sendMessage(message.replace("&", "§")
                    .replace("{player}", target.getDisplayName())
                    .replace("{item}", item.getItemMeta().getDisplayName())
                    .replace("{amount}", String.valueOf(amount)));
        }
    }
}
