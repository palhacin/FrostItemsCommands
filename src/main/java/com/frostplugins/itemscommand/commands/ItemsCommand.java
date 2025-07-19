package com.frostplugins.itemscommand.commands;

import com.frostplugins.itemscommand.Terminal;
import com.frostplugins.itemscommand.interfaces.Interface;
import com.frostplugins.itemscommand.loader.ItemsCommandLoader;
import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import com.frostplugins.itemscommand.utils.ItemBuilder;
import com.frostplugins.itemscommand.utils.SkullUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemsCommand extends Command {

    public ItemsCommand() {
        super(Terminal.getInstance().getConfig().getString("command.name"));
        setAliases(Terminal.getInstance().getConfig().getStringList("command.aliases"));
        setUsage(Terminal.getInstance().getConfig().getString("command.usage").replace("&", "§"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(sender instanceof Player){
            if(args.length==0){
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
        if (itemCommand.getAttributes().contains("integer")) {
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
                sender.sendMessage("§cAtributo invalido, deve ser um número inteiro");
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

        String displayName = itemCommand.getDisplayName();
        if (subItem != null) {
            displayName = displayName.replace("{selected_sub_item}", subItem);
        }
        if (integerStr != null) {
            displayName = displayName.replace("{attributes_integer}", integerStr);
        }

        List<String> formattedLore = new ArrayList<>();
        for (String line : itemCommand.getDescription()) {
            if (subItem != null) {
                line = line.replace("{selected_sub_item}", subItem);
            }
            if (integerStr != null) {
                line = line.replace("{attributes_integer}", integerStr);
            }
            formattedLore.add(line);
        }

        Material material = Material.getMaterial(itemCommand.getMaterial().toUpperCase());
        if (material == null) {
            return true;
        }

        ItemStack item;

        if(itemCommand.isHead()){
            item=new ItemBuilder(SkullUtils.getSkull(itemCommand.getHeadUrl()))
                    .setName(displayName)
                    .setAmount(amount)
                    .setLore(formattedLore).build();
        }else{
            item=new ItemBuilder(material)
                    .setName(displayName)
                    .setAmount(amount)
                    .setLore(formattedLore).build();
        }

        Player target = Bukkit.getPlayer(playerName);
        if (target != null && target.isOnline()) {
            target.getInventory().addItem(item);
            if(Terminal.getInstance().getConfig().getString("messages.received-item") != null
                    && !Terminal.getInstance().getConfig().getString("messages.received-item").isEmpty()) {
                target.sendMessage(Terminal.getInstance().getConfig().getString("messages.received-item")
                        .replace("&", "§")
                        .replace("{player}", sender.getName())
                        .replace("{item}", displayName)
                        .replace("{amount}", String.valueOf(amount)));
            }

            if(target != sender){
                if(Terminal.getInstance().getConfig().getString("messages.gave-item") != null
                        && !Terminal.getInstance().getConfig().getString("messages.gave-item").isEmpty()) {
                    sender.sendMessage(Terminal.getInstance().getConfig().getString("messages.gave-item")
                            .replace("&", "§")
                            .replace("{player}", target.getDisplayName())
                            .replace("{item}", displayName)
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
