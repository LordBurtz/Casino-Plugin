package me.lordburtz.Casino.data;

import me.lordburtz.Casino.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class Data {
    private  Main plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public Data(Main plugin, String file) {
        this.plugin = plugin;
        saveDefaultConfig(file);
    }

    public void reloadConfig(String file) {
        if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), file);

        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaulStream = this.plugin.getResource(file);
        if (defaulStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaulStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig(String file) {
        if (this.dataConfig == null) reloadConfig(file);
        return this.dataConfig;
    }

    public void saveConfig(String file) {
        if (this.dataConfig == null || this.configFile == null) return;
        try {
            this.getConfig(file).save(this.configFile);
        } catch (IOException exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Couldnt save config to " + this.configFile, exception);
        }
    }

    public void saveDefaultConfig(String file) {
        if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), file);

        if (!this.configFile.exists()) {
            this.plugin.saveResource(file, false);
        }
    }


}