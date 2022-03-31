package me.armar.plugins.autorank.language;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class LanguageHandler {
    private FileConfiguration languageConfig;
    private File languageConfigFile;
    private final Autorank plugin;

    public LanguageHandler(Autorank autorank) {
        this.plugin = autorank;
    }

    public void createNewFile() {
        this.reloadConfig();
        this.saveConfig();
        Lang.setFile(this.languageConfig);
        this.loadConfig();
        this.plugin.debugMessage("Language file loaded (lang.yml)");
    }

    public FileConfiguration getConfig() {
        if (this.languageConfig == null) {
            this.reloadConfig();
        }

        return this.languageConfig;
    }

    public void loadConfig() {
        this.languageConfig.options().header("Language file");
        Lang[] var1 = Lang.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Lang value = var1[var3];
            this.languageConfig.addDefault(value.getPath(), value.getDefault());
        }

        this.languageConfig.options().copyDefaults(true);
        this.saveConfig();
    }

    public void reloadConfig() {
        if (this.languageConfigFile == null) {
            this.languageConfigFile = new File(this.plugin.getDataFolder() + "/lang", "lang.yml");
        }

        this.languageConfig = YamlConfiguration.loadConfiguration(this.languageConfigFile);
    }

    public void saveConfig() {
        if (this.languageConfig != null && this.languageConfigFile != null) {
            try {
                this.getConfig().save(this.languageConfigFile);
            } catch (IOException var2) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.languageConfigFile, var2);
            }

        }
    }
}
