package com.frostplugins.itemscommand.objects;

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
}
