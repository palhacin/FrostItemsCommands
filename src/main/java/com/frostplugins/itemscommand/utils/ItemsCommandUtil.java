package com.frostplugins.itemscommand.utils;

import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemsCommandUtil {

    public static ItemsCommandObject getItemsCommandByItem(ItemStack item, List<ItemsCommandObject> items) {
        if (item == null || item.getType() == Material.AIR) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() || !meta.hasLore()) return null;

        String itemDisplayName = meta.getDisplayName();
        List<String> itemLore = meta.getLore();

        for (ItemsCommandObject itemCommand : items) {
            String rawDisplayName = itemCommand.getDisplayName();
            List<String> configLore = itemCommand.getDescription();
            List<String> subItems = itemCommand.getSubItems();
            List<String> attributes = itemCommand.getAttributes();

            if (subItems != null && !subItems.isEmpty()) {
                for (String sub : subItems) {
                    String tempDisplay = rawDisplayName.replace("{selected_sub_item}", sub);

                    if (attributes != null) {
                        for (String attr : attributes) {
                            if (attr.equals("integer")) {
                                Integer duration = getAttributeIntegerFromItem(item, itemCommand);
                                if (duration == null) continue;
                                tempDisplay = tempDisplay.replace("{attributes_integer}", String.valueOf(duration));
                            } else {
                                tempDisplay = tempDisplay.replace("{attributes_" + attr + "}", "");
                            }
                        }
                    }

                    if (!tempDisplay.equals(itemDisplayName)) continue;

                    return itemCommand;
                }
            } else {
                String tempDisplay = rawDisplayName;

                if (attributes != null) {
                    for (String attr : attributes) {
                        if (attr.equals("integer")) {
                            Integer duration = getAttributeIntegerFromItem(item, itemCommand);
                            if (duration == null) continue;
                            tempDisplay = tempDisplay.replace("{attributes_integer}", String.valueOf(duration));
                        } else {
                            tempDisplay = tempDisplay.replace("{attributes_" + attr + "}", "");
                        }
                    }
                }

                tempDisplay = tempDisplay.replace("{selected_sub_item}", "");

                if (!tempDisplay.equals(itemDisplayName)) continue;

                if (loreMatches(itemLore, configLore, null, attributes, itemCommand)) {
                    return itemCommand;
                }
            }
        }

        return null;
    }

    private static boolean loreMatches(List<String> itemLore, List<String> configLore, String subItem, List<String> attributes, ItemsCommandObject itemCommand) {
        if (configLore == null || itemLore == null || configLore.size() != itemLore.size()) return false;

        for (int i = 0; i < configLore.size(); i++) {
            String configLine = configLore.get(i);
            String itemLine = itemLore.get(i);

            if (subItem != null) {
                configLine = configLine.replace("{selected_sub_item}", subItem);
            } else {
                configLine = configLine.replace("{selected_sub_item}", "");
            }

            if (attributes != null) {
                for (String attr : attributes) {
                    if (attr.equals("integer")) {
                        Integer integer = getAttributeIntegerFromItem(new ItemStack(Material.AIR), itemCommand);
                        if (integer != null) {
                            configLine = configLine.replace("{attributes_integer}", String.valueOf(integer));
                        } else {
                            configLine = configLine.replace("{attributes_integer}", "");
                        }
                    } else {
                        configLine = configLine.replace("{attributes_" + attr + "}", "");
                    }
                }
            }

            if (!itemLine.equals(configLine)) {
                return false;
            }
        }

        return true;
    }

    public static String getCurrentSubItemFromItem(ItemStack item, ItemsCommandObject itemCommand) {
        if (item == null || item.getType() == Material.AIR) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return null;

        List<String> itemLore = meta.getLore();
        List<String> configLore = itemCommand.getDescription();

        if (configLore == null || itemLore == null || configLore.size() != itemLore.size()) return null;

        for (int i = 0; i < configLore.size(); i++) {
            String configLine = configLore.get(i);
            String itemLine = itemLore.get(i);

            if (configLine.contains("{selected_sub_item}")) {
                String[] parts = configLine.split("\\{selected_sub_item\\}", -1);

                String before = parts[0];
                String after = parts.length > 1 ? parts[1] : "";

                if (itemLine.startsWith(before) && itemLine.endsWith(after)) {
                    String value = itemLine.substring(before.length(), itemLine.length() - after.length());
                    return value;
                }
            }
        }
        return null;
    }

    public static ItemStack replaceItemAttributes(ItemStack item, int attributeValue) {
        String value = String.valueOf(attributeValue);

        ItemStack cloned = item.clone();
        if (!cloned.hasItemMeta()) return cloned;

        ItemMeta meta = cloned.getItemMeta();

        if (meta.hasDisplayName()) {
            String name = meta.getDisplayName().replace("{attributes_integer}", value);
            meta.setDisplayName(name);
        }

        if (meta.hasLore()) {
            List<String> updatedLore = new ArrayList<>();
            for (String line : meta.getLore()) {
                updatedLore.add(line.replace("{attributes_integer}", value));
            }
            meta.setLore(updatedLore);
        }

        cloned.setItemMeta(meta);

        return new ItemBuilder(cloned).build();
    }

    public static Integer getAttributeIntegerFromItem(ItemStack item, ItemsCommandObject itemCommand) {
        if (item == null || item.getType() == Material.AIR) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return null;

        List<String> itemLore = meta.getLore();
        List<String> configLore = itemCommand.getDescription();

        if (configLore.size() != itemLore.size()) return null;

        for (int i = 0; i < configLore.size(); i++) {
            String configLine = configLore.get(i);
            String itemLine = itemLore.get(i);

            if (configLine.contains("{attributes_integer}")) {
                String[] parts = configLine.split("\\{attributes_integer\\}", -1);

                String before = parts[0];
                String after = parts.length > 1 ? parts[1] : "";

                if (itemLine.startsWith(before) && itemLine.endsWith(after)) {
                    String valueStr = itemLine.substring(before.length(), itemLine.length() - after.length());

                    if (valueStr.matches("\\d+")) {
                        return Integer.parseInt(valueStr);
                    }
                }
            }
        }
        return null;
    }

}
