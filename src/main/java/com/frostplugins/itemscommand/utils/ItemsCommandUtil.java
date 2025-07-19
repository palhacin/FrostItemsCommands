package com.frostplugins.itemscommand.utils;

import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemsCommandUtil {

    public static ItemsCommandObject getItemsCommandByItem(ItemStack item, List<ItemsCommandObject> items) {
        if (item == null || item.getType() == Material.AIR) return null;

        String key = NBTUtil.getString(item, "key");
        if (key == null || key.isEmpty()) return null;

        for (ItemsCommandObject obj : items) {
            if (obj.getKey().equals(key)) {
                return obj;
            }
        }
        return null;
    }

    public static String getCurrentSubItemFromItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;

        String subItem = NBTUtil.getString(item, "sub_item");
        if (subItem == null) return null;

        if (subItem.contains(":")) {
            return subItem.split(":", 2)[0];
        }

        return subItem;
    }


    public static Integer getAttributeFromItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;

        String valStr = NBTUtil.getString(item, "attribute");
        if (valStr == null || valStr.isEmpty()) return null;

        try {
            return Integer.parseInt(valStr);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static String formatSubItem(String subItem) {
        if (subItem == null) return "";

        if (subItem.contains(":")) {
            String[] parts = subItem.split(":", 2);
            String name = parts[0];
            String colorCode = parts[1].replace('&', '§');

            return colorCode + name;
        }

        return subItem;
    }



}
