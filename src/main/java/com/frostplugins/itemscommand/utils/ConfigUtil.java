package com.frostplugins.itemscommand.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ConfigUtil {

    private final String fileName;
    private final JavaPlugin plugin;
    private File configFile;
    private FileConfiguration fileConfiguration;

    @SuppressWarnings("deprecation")
    public ConfigUtil(JavaPlugin plugin, String fileName) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }
        if (!plugin.isEnabled()) {
            throw new IllegalArgumentException("plugin must be enabled");
        }
        this.plugin = plugin;
        this.fileName = fileName;
        File dataFolder = plugin.getDataFolder();
        if (dataFolder == null) {
            throw new IllegalStateException();
        }
        this.configFile = new File(plugin.getDataFolder(), fileName);
    }

    public void saveDefaultConfig() {
        if (!this.configFile.exists())
            this.plugin.saveResource(this.fileName, false);
    }



    @SuppressWarnings("deprecation")
    public void reloadConfig() {
        this.fileConfiguration = (FileConfiguration) YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defConfigStream = this.plugin.getResource(this.fileName);
        if (defConfigStream != null) {
        }
    }

    public void saveConfig() {
        if (fileConfiguration == null || configFile == null) return;

        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Não foi possível salvar o arquivo de configuração " + fileName);
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        if (this.fileConfiguration == null) {
            reloadConfig();
        }
        return this.fileConfiguration;
    }




}