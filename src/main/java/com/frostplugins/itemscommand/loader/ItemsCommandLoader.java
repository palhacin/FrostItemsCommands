package com.frostplugins.itemscommand.loader;

import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ItemsCommandLoader {

    private static List<ItemsCommandObject> itemsList = new ArrayList<>();

    public static List<ItemsCommandObject> loadAll(FileConfiguration config) {
        itemsList = new ArrayList<>();

        if (config.contains("items")) {
            for (String key : config.getConfigurationSection("items").getKeys(false)) {
                ItemsCommandObject item = loadItem(config, key);
                itemsList.add(item);
            }
        }

        return itemsList;
    }

    public static List<ItemsCommandObject> getItemsList() {
        return itemsList;
    }

    private static ItemsCommandObject loadItem(FileConfiguration config, String key) {
        String path = "items." + key + ".";

        String displayName = getConfigString(config, path + "display-name");
        List<String> subItems = getConfigStringList(config, path + "sub-items");
        List<String> attributes = getConfigStringList(config, path + "attributes");
        List<String> description = getConfigStringList(config, path + "description");
        List<String> messageWhenUse = getConfigStringList(config, path + "messageWhenUse");

        String material = config.getString(path + "material");
        boolean isHead = config.getBoolean(path + "head");
        String headUrl = config.getString(path + "head-url");
        List<String> commands = getConfigStringList(config, path + "commands");

        return new ItemsCommandObject(key, displayName, subItems, attributes, description, messageWhenUse, material, isHead, headUrl, commands);
    }

    private static String getConfigString(FileConfiguration config, String path) {
        return config.getString(path, "").replace("&", "§");
    }

    private static List<String> getConfigStringList(FileConfiguration config, String path) {
        List<String> list = config.getStringList(path);
        return list.isEmpty() ? new ArrayList<>() : new ArrayList<>(list);
    }
}
