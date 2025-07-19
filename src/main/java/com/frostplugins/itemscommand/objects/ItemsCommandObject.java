package com.frostplugins.itemscommand.objects;

import com.frostplugins.itemscommand.utils.ItemBuilder;
import com.frostplugins.itemscommand.utils.ItemsCommandUtil;
import com.frostplugins.itemscommand.utils.NBTUtil;
import com.frostplugins.itemscommand.utils.SkullUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemsCommandObject {

    private final String key;
    private final String displayName;
    private final List<String> subItems;
    private final List<String> attributes;
    private final List<String> description;
    private final List<String> messageWhenUse;
    private final String material;
    private final boolean isHead;
    private final String headUrl;
    private final String command;

    public ItemsCommandObject(String key, String displayName, List<String> subItems, List<String> attributes,
                              List<String> description, List<String> messageWhenUse, String material, boolean isHead,
                              String headUrl, String command) {
        this.key = key;
        this.displayName = displayName;
        this.subItems = subItems;
        this.attributes = attributes;
        this.description = description;
        this.messageWhenUse = messageWhenUse;
        this.material = material;
        this.isHead = isHead;
        this.headUrl = headUrl;
        this.command = command;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getSubItems() {
        return subItems;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public List<String> getDescription() {
        return description;
    }

    public List<String> getMessageWhenUse() {
        return messageWhenUse;
    }

    public String getMaterial() {
        return material;
    }

    public boolean isHead() {
        return isHead;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public String getCommand() {
        return command;
    }

    public static ItemStack createItem(ItemsCommandObject itemObj, String subItem, String attribute) {
        ItemStack baseItem;

        if (itemObj.isHead()) {
            baseItem = SkullUtils.getSkull(itemObj.getHeadUrl());
        } else {
            Material material = Material.getMaterial(itemObj.getMaterial().toUpperCase());
            if (material == null) return null;
            baseItem = new ItemStack(material);
        }

        String formattedSubItem = ItemsCommandUtil.formatSubItem(subItem);

        List<String> replacedLore = new ArrayList<>();
        if (itemObj.getDescription() != null) {
            for (String line : itemObj.getDescription()) {
                if (line != null) {
                    String replacedLine = line
                            .replace("{selected_sub_item}", subItem != null ? formattedSubItem : "")
                            .replace("{attributes_integer}", attribute != null ? attribute : "");
                    replacedLore.add(replacedLine);
                }
            }
        }

        ItemBuilder builder = new ItemBuilder(baseItem)
                .setName(itemObj.getDisplayName()
                        .replace("{selected_sub_item}", subItem != null ? formattedSubItem : "")
                        .replace("{attributes_integer}", attribute != null ? attribute : ""))
                .setAmount(1)
                .setLore(replacedLore);

        ItemStack item = builder.build();

        item = NBTUtil.setString(item, "key", itemObj.getKey());

        if (subItem != null && !subItem.isEmpty()) {
            item = NBTUtil.setString(item, "sub_item", subItem);
        }

        if (attribute != null && !attribute.isEmpty()) {
            item = NBTUtil.setString(item, "attribute", attribute);
        }

        return item;
    }


}
