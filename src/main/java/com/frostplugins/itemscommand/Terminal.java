package com.frostplugins.itemscommand;

import com.frostplugins.itemscommand.commands.ItemsCommand;
import com.frostplugins.itemscommand.listeners.InterfaceInteract;
import com.frostplugins.itemscommand.listeners.ItemInteract;
import com.frostplugins.itemscommand.loader.ItemsCommandLoader;
import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import com.frostplugins.itemscommand.utils.ConfigUtil;
import com.frostplugins.itemscommand.utils.PluginUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class Terminal extends JavaPlugin {

    private static Terminal instance;

    public static ConfigUtil itemsConfig;

    public static Terminal getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance=this;
        saveDefaultConfig();
        itemsConfig = PluginUtil.loadConfig("items.yml", Terminal.getInstance());

        List<ItemsCommandObject> itemsList = ItemsCommandLoader.loadAll(itemsConfig.getConfig());

        getLogger().info(" Loaded " + itemsList.size() + " items command");

        PluginUtil.registerCommands(ItemsCommand.class);
        PluginUtil.registerListeners(this, ItemInteract.class, InterfaceInteract.class);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
