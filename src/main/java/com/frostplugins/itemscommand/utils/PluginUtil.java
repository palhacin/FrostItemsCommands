package com.frostplugins.itemscommand.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.bukkit.Bukkit.getName;

public class PluginUtil {

    public static void registerListeners(JavaPlugin instance, Class<?>... listeners) {
        Arrays.asList(listeners).forEach(listenerClass -> {
            try {
                Listener listener = (Listener) listenerClass.getConstructor().newInstance();

                instance.getServer().getPluginManager().registerEvents(listener, instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void registerCommands(Class<?>... commands) {
        Arrays.asList(commands).forEach(commandClass -> {
            try {
                Command command = (Command) commandClass.getConstructor().newInstance();
                commandMap(getName(), command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
