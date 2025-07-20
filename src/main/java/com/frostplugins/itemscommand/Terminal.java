package com.frostplugins.itemscommand;

import com.frostplugins.itemscommand.commands.ItemsCommand;
import com.frostplugins.itemscommand.listeners.InterfaceInteract;
import com.frostplugins.itemscommand.listeners.ItemInteract;
import com.frostplugins.itemscommand.loader.ItemsCommandLoader;
import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import com.frostplugins.itemscommand.utils.ConfigUtil;
import com.frostplugins.itemscommand.utils.PluginUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Terminal extends JavaPlugin {

    public ConfigUtil itemsConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        itemsConfig = PluginUtil.loadConfig("items.yml", this);

        List<ItemsCommandObject> itemsList = ItemsCommandLoader.loadAll(itemsConfig.getConfig());

        getLogger().info(" Loaded " + itemsList.size() + " items command");

        PluginUtil.registerCommands(
                new ItemsCommand(this.getConfig(), itemsConfig)
        );

        PluginUtil.registerListeners(this,
                new ItemInteract(),
                new InterfaceInteract()
        );

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
