package com.frostplugins.itemscommand.listeners;

import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import com.frostplugins.itemscommand.loader.ItemsCommandLoader;
import com.frostplugins.itemscommand.utils.ItemsCommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemInteract implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().toString().contains("RIGHT_CLICK")) {

            Player player = event.getPlayer();

            ItemStack item = player.getInventory().getItemInHand();
            if (item == null || !item.hasItemMeta())return;

            ItemsCommandObject itemCommand = ItemsCommandUtil.getItemsCommandByItem(item, ItemsCommandLoader.getItemsList());
            if (itemCommand == null)return;

            String subItem = ItemsCommandUtil.getCurrentSubItemFromItem(item, itemCommand);
            Integer attribute = ItemsCommandUtil.getAttributeIntegerFromItem(item, itemCommand);

            String command = itemCommand.getCommand()
                    .replace("{player}", player.getName());

            if (subItem != null)
                command = command.replace("{selected_sub_item}", subItem);

            if (attribute != null)
                command = command.replace("{attributes_integer}", String.valueOf(attribute));

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

            event.setCancelled(true);
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().removeItem(item);
            }

            player.updateInventory();

            if (itemCommand.getMessageWhenUse() != null && !itemCommand.getMessageWhenUse().isEmpty()) {
                for (String line : itemCommand.getMessageWhenUse()) {
                    if (line == null || line.isEmpty()) continue;

                    if (subItem != null) {
                        line = line.replace("{item}", subItem);
                    }
                    if (attribute != null) {
                        line = line.replace("{attribute_integer}", String.valueOf(attribute));
                    }

                    player.sendMessage(line.replace("&", "§"));
                }
            }
        }
    }
}
