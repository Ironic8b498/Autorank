package me.armar.plugins.autorank.config;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class AbstractConfig {
    private SimpleYamlConfiguration configFile;
    private Autorank plugin;
    private String fileName;
    private boolean isLoaded = false;

    public AbstractConfig() {
    }

    public void createNewFile() throws InvalidConfigurationException {
        this.configFile = new SimpleYamlConfiguration(this.plugin, this.fileName, this.fileName);
        this.plugin.debugMessage("File loaded (" + this.fileName + ")");
    }

    public FileConfiguration getConfig() {
        return this.configFile != null ? this.configFile : null;
    }

    public void reloadConfig() {
        if (this.configFile != null) {
            this.configFile.reloadFile();
        }

    }

    public void saveConfig() {
        if (this.configFile != null) {
            this.configFile.saveFile();
        }
    }

    public boolean loadConfig() {
        try {
            this.createNewFile();
            this.isLoaded = true;
            return true;
        } catch (Exception var2) {
            this.isLoaded = false;
            return false;
        }
    }

    public boolean isLoaded() {
        return this.isLoaded;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Autorank getPlugin() {
        return this.plugin;
    }

    public void setPlugin(Autorank plugin) {
        this.plugin = plugin;
    }
}
