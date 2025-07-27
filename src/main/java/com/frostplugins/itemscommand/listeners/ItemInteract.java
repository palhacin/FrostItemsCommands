package com.frostplugins.itemscommand.listeners;

import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import com.frostplugins.itemscommand.services.ItemsCommandService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemInteract implements Listener {

    private static final String CURRENT_SUB_ITEM_PLACEHOLDER = "{current_sub_item}";
    private static final String ATTRIBUTES_INTEGER_PLACEHOLDER = "{attributes_integer}";

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT_CLICK")) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInHand();

        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return;

        ItemsCommandObject itemCommand = ItemsCommandService.getItemsCommandByItem(item);
        if (itemCommand == null) return;

        String subItem = ItemsCommandService.getCurrentSubItemFromItem(item);
        Integer attribute = ItemsCommandService.getAttributeFromItem(item);

        for (String command : itemCommand.getCommands()) {
            if (subItem != null) {
                command = command.replace(CURRENT_SUB_ITEM_PLACEHOLDER, subItem);
            }
            if (attribute != null) {
                command = command.replace(ATTRIBUTES_INTEGER_PLACEHOLDER, String.valueOf(attribute));
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
        }

        updateInventory(player, item);

        sendItemUsageMessages(player, itemCommand, subItem, attribute);

        event.setCancelled(true);
    }

    private void updateInventory(Player player, ItemStack item) {
        int amount = item.getAmount();
        if (amount > 1) {
            item.setAmount(amount - 1);
        } else {
            player.getInventory().setItemInHand(null);
        }
        player.updateInventory();
    }

    private void sendItemUsageMessages(Player player, ItemsCommandObject itemCommand, String subItem, Integer attribute) {
        if (itemCommand.getMessageWhenUse() != null && !itemCommand.getMessageWhenUse().isEmpty()) {
            for (String line : itemCommand.getMessageWhenUse()) {
                if (line == null || line.isEmpty()) continue;

                if (subItem != null) {
                    line = line.replace("{item}", ItemsCommandService.formatSubItem(subItem));
                }
                if (attribute != null) {
                    line = line.replace(ATTRIBUTES_INTEGER_PLACEHOLDER, String.valueOf(attribute));
                }
                player.sendMessage(line.replace("&", "§"));
            }
        }
    }
}
