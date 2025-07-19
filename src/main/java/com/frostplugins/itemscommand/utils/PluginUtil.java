package com.frostplugins.itemscommand.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

import static org.bukkit.Bukkit.getName;

public class PluginUtil {

    public static ConfigUtil loadConfig(String name, JavaPlugin plugin) {
        ConfigUtil config = new ConfigUtil(plugin, name);
        config.saveDefaultConfig();

        File folder = plugin.getDataFolder();
        if (!folder.exists()) {
            try {
                folder.mkdirs();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        return config;
    }

    public static void registerListeners(JavaPlugin instance, Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, instance);
        }
    }

    public static void registerCommands(Command... commands) {
        for (Command command : commands) {
            commandMap(getName(), command);
        }
    }


    private static void commandMap(String prefix, Command command) {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Bukkit.getServer());

            if (commandMap != null) {
                commandMap.register(prefix, command);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
