package com.frostplugins.itemscommand.loader;

import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ItemsCommandLoader {

    private static List<ItemsCommandObject> itemsList = new ArrayList<>();

    public static List<ItemsCommandObject> loadAll(FileConfiguration config) {
        itemsList.clear();

        if (config.contains("items")) {
            for (String key : config.getConfigurationSection("items").getKeys(false)) {
                String path = "items." + key + ".";

                String displayName = config.getString(path + "display-name", "").replace("&", "§");

                List<String> subItems = config.getStringList(path + "sub-items");
                List<String> attributes = config.getStringList(path + "attributes");

                List<String> description = new ArrayList<>();
                for (String line : config.getStringList(path + "description")) {
                    description.add(line.replace("&", "§"));
                }

                List<String> messageWhenUse = new ArrayList<>();
                for (String line : config.getStringList(path + "messageWhenUse")) {
                    messageWhenUse.add(line.replace("&", "§"));
                }

                String material = config.getString(path + "material");
                boolean isHead = config.getBoolean(path + "head");
                String headUrl = config.getString(path + "head-url");
                String command = config.getString(path + "command");

                ItemsCommandObject item = new ItemsCommandObject(
                        key,
                        displayName,
                        subItems,
                        attributes,
                        description,
                        messageWhenUse,
                        material,
                        isHead,
                        headUrl,
                        command
                );

                itemsList.add(item);
            }
        }

        return itemsList;
    }

    public static List<ItemsCommandObject> getItemsList() {
        return itemsList;
    }
}
