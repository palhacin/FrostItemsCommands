package com.frostplugins.itemscommand.listeners;

import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import com.frostplugins.itemscommand.loader.ItemsCommandLoader;
import com.frostplugins.itemscommand.utils.ItemsCommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemInteract implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {

            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInHand();

            if (item == null || item.getType().equals(Material.AIR) || !item.hasItemMeta()) {
                return;
            }

            ItemsCommandObject itemCommand = ItemsCommandUtil.getItemsCommandByItem(item, ItemsCommandLoader.getItemsList());
            if (itemCommand == null) return;

            String subItem = ItemsCommandUtil.getCurrentSubItemFromItem(item);

            Integer attribute = null;
            if (itemCommand.getAttributes() != null && !itemCommand.getAttributes().isEmpty()) {
                for (String attr : itemCommand.getAttributes()) {

                    if ("integer".equalsIgnoreCase(attr)) {
                        attribute = ItemsCommandUtil.getAttributeFromItem(item);

                        break;
                    }
                }
            }

            String command = itemCommand.getCommand()
                    .replace("{player}", player.getName());

            if (subItem != null) {
                command = command.replace("{selected_sub_item}", subItem);
            }

            if (attribute != null) {
                command = command.replace("{attributes_integer}", String.valueOf(attribute));
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

            event.setCancelled(true);

            int amount = item.getAmount();
            if (amount > 1) {
                item.setAmount(amount - 1);
            } else {
                player.getInventory().setItemInHand(null);
            }

            player.updateInventory();

            if (itemCommand.getMessageWhenUse() != null && !itemCommand.getMessageWhenUse().isEmpty()) {
                for (String line : itemCommand.getMessageWhenUse()) {
                    if (line == null || line.isEmpty()) continue;

                    if (subItem != null) {
                        line = line.replace("{item}", ItemsCommandUtil.formatSubItem(subItem));
                    }
                    if (attribute != null) {
                        line = line.replace("{attributes_integer}", String.valueOf(attribute));
                    }

                    player.sendMessage(line.replace("&", "§"));
                }
            }
        }
    }
}
