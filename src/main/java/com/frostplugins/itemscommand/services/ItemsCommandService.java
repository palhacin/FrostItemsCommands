package com.frostplugins.itemscommand.services;

import com.frostplugins.itemscommand.loader.ItemsCommandLoader;
import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import com.frostplugins.itemscommand.utils.NBTUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ItemsCommandService {

    private static boolean isItemInvalid(ItemStack item) {
        return item == null || item.getType() == Material.AIR || !item.hasItemMeta();
    }

    public static ItemsCommandObject getItemsCommandByItem(ItemStack item) {
        if (isItemInvalid(item)) return null;

        String key = NBTUtil.getString(item, "key");
        if (key == null || key.isEmpty()) return null;

        return ItemsCommandLoader.getItemsList().stream()
                .filter(obj -> obj.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    public static ItemsCommandObject getItemCommandsByKey(String itemKey) {
        return ItemsCommandLoader.getItemsList().stream()
                .filter(item -> itemKey.equalsIgnoreCase(item.getKey()))
                .findFirst()
                .orElse(null);
    }

    public static String getCurrentSubItemFromItem(ItemStack item) {
        if (isItemInvalid(item)) return null;

        String subItem = NBTUtil.getString(item, "sub_item");
        if (subItem == null) return null;

        return subItem.contains(":") ? subItem.split(":", 2)[0] : subItem;
    }

    public static Integer getAttributeFromItem(ItemStack item) {
        if (isItemInvalid(item)) return null;

        String valStr = NBTUtil.getString(item, "attribute");
        if (valStr == null || valStr.isEmpty()) return null;

        try {
            return Integer.parseInt(valStr);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static String formatSubItem(String subItem) {
        return Optional.ofNullable(subItem)
                .filter(s -> s.contains(":"))
                .map(s -> {
                    String[] parts = s.split(":", 2);
                    String name = parts[0];
                    String colorCode = parts[1].replace('&', '§');
                    return colorCode + name;
                })
                .orElse(subItem);
    }
}
