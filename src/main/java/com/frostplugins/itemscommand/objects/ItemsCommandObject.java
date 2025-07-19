package com.frostplugins.itemscommand.objects;

import com.frostplugins.itemscommand.utils.ItemBuilder;
import com.frostplugins.itemscommand.services.ItemsCommandService;
import com.frostplugins.itemscommand.utils.NBTUtil;
import com.frostplugins.itemscommand.utils.SkullUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemsCommandObject {

    private static final String CURRENT_SUB_ITEM_PLACEHOLDER = "{current_sub_item}";
    private static final String ATTRIBUTES_INTEGER_PLACEHOLDER = "{attributes_integer}";

    private final String key;
    private final String displayName;
    private final List<String> subItems;
    private final List<String> attributes;
    private final List<String> description;
    private final List<String> messageWhenUse;
    private final String material;
    private final boolean isHead;
    private final String headUrl;
    private final List<String> commands;

    public ItemsCommandObject(String key, String displayName, List<String> subItems, List<String> attributes,
                              List<String> description, List<String> messageWhenUse, String material, boolean isHead,
                              String headUrl, List<String> commands) {
        this.key = key;
        this.displayName = displayName;
        this.subItems = subItems;
        this.attributes = attributes;
        this.description = description;
        this.messageWhenUse = messageWhenUse;
        this.material = material;
        this.isHead = isHead;
        this.headUrl = headUrl;
        this.commands = commands;
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

    public List<String> getCommands() {
        return commands;
    }

    public static ItemStack createItem(ItemsCommandObject itemObj, String subItem, String attribute) {
        ItemStack baseItem = createBaseItem(itemObj);

        if (baseItem == null) return null;

        String formattedSubItem = ItemsCommandService.formatSubItem(subItem);
        List<String> replacedLore = buildLore(itemObj, subItem, attribute, formattedSubItem);

        ItemBuilder builder = new ItemBuilder(baseItem)
                .setName(formatDisplayName(itemObj, subItem, attribute, formattedSubItem))
                .setAmount(1)
                .setLore(replacedLore);

        ItemStack item = builder.build();
        item = NBTUtil.setString(item, "key", itemObj.getKey());

        item = addNBTTagIfPresent(item, "sub_item", subItem);
        item = addNBTTagIfPresent(item, "attribute", attribute);

        return item;
    }

    private static ItemStack createBaseItem(ItemsCommandObject itemObj) {
        if (itemObj.isHead()) {
            return SkullUtils.getSkull(itemObj.getHeadUrl());
        } else {
            Material material = Material.getMaterial(itemObj.getMaterial().toUpperCase());
            return (material == null) ? null : new ItemStack(material);
        }
    }

    private static List<String> buildLore(ItemsCommandObject itemObj, String subItem, String attribute, String formattedSubItem) {
        List<String> replacedLore = new ArrayList<>();

        if (itemObj.getDescription() != null) {
            for (String line : itemObj.getDescription()) {
                if (line != null) {
                    String replacedLine = line
                            .replace(CURRENT_SUB_ITEM_PLACEHOLDER, Optional.ofNullable(subItem).map(s -> formattedSubItem).orElse(""))
                            .replace(ATTRIBUTES_INTEGER_PLACEHOLDER, Optional.ofNullable(attribute).orElse(""))
                            .replace("&", "§");
                    replacedLore.add(replacedLine);
                }
            }
        }

        return replacedLore;
    }

    private static String formatDisplayName(ItemsCommandObject itemObj, String subItem, String attribute, String formattedSubItem) {
        return itemObj.getDisplayName()
                .replace(CURRENT_SUB_ITEM_PLACEHOLDER, Optional.ofNullable(subItem).map(s -> formattedSubItem).orElse(""))
                .replace(ATTRIBUTES_INTEGER_PLACEHOLDER, Optional.ofNullable(attribute).orElse(""));
    }

    private static ItemStack addNBTTagIfPresent(ItemStack item, String tagName, String tagValue) {
        return (tagValue != null && !tagValue.isEmpty()) ? NBTUtil.setString(item, tagName, tagValue) : item;
    }
}
