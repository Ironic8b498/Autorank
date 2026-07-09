package me.armar.plugins.autorank.language;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class LanguageHandler {
    private FileConfiguration langConfig;
    private FileConfiguration langGEConfig;
    private FileConfiguration langFRConfig;
    private File langConfigFile;
    private File langGEConfigFile;
    private File langFRConfigFile;
    private final Autorank plugin;

    public LanguageHandler(Autorank plugin) {
        this.plugin = plugin;
    }

    public void createNewLangFile() {
        this.saveConfig();
        this.reloadLangConfig();
        Lang.setFile(this.langConfig);
        this.loadConfig();
        this.plugin.debugMessage("Language file loaded (lang.yml)");
    }

    public void createNewLangFRFile() {
        this.reloadLangFRConfig();
        this.saveLangFRConfig();
        LangFR.setFile(this.langFRConfig);
        this.loadLangFRConfig();
        this.plugin.debugMessage("Language file loaded (langFR.yml)");
    }

    public void createNewLangGEFile() {
        this.reloadLangGEConfig();
        this.saveLangGEConfig();
        LangGE.setFile(this.langGEConfig);
        this.loadLangGEConfig();
        this.plugin.debugMessage("Language file loaded (langGE.yml)");
    }

    public FileConfiguration getConfig() {
        if (this.langConfig == null) {
            this.reloadLangConfig();
        }

        return this.langConfig;
    }

    public FileConfiguration getLangFRConfig() {
        if (this.langFRConfig == null) {
            this.reloadLangFRConfig();
        }

        return this.langFRConfig;
    }

    public FileConfiguration getLangGEConfig() {
        if (this.langGEConfig == null) {
            this.reloadLangGEConfig();
        }

        return this.langGEConfig;
    }

    public void loadConfig() {
        this.langConfig.options().header("Language file");

        for(Lang value : Lang.values()) {
            this.langConfig.addDefault(value.getPath(), value.getDefault());
        }

        this.langConfig.options().copyDefaults(true);
        this.saveConfig();
    }

    public void loadLangFRConfig() {
        this.langFRConfig.options().header("French Language file");

        for(LangFR value : LangFR.values()) {
            this.langFRConfig.addDefault(value.getPath(), value.getDefault());
        }

        this.langFRConfig.options().copyDefaults(true);
        this.saveLangFRConfig();
    }

    public void loadLangGEConfig() {
        this.langGEConfig.options().header("German Language file");

        for(LangGE value : LangGE.values()) {
            this.langGEConfig.addDefault(value.getPath(), value.getDefault());
        }

        this.langGEConfig.options().copyDefaults(true);
        this.saveLangGEConfig();
    }

    public void reloadLangConfig() {
        if (this.langConfigFile == null) {
            this.langConfigFile = new File(this.plugin.getDataFolder() + "/lang", "lang.yml");
        }

        this.langConfig = YamlConfiguration.loadConfiguration(this.langConfigFile);
    }

    public void reloadLangFRConfig() {
        if (this.langFRConfigFile == null) {
            this.langFRConfigFile = new File(this.plugin.getDataFolder() + "/lang", "langFR.yml");
        }

        this.langFRConfig = YamlConfiguration.loadConfiguration(this.langFRConfigFile);
    }

    public void reloadLangGEConfig() {
        if (this.langGEConfigFile == null) {
            this.langGEConfigFile = new File(this.plugin.getDataFolder() + "/lang", "langGE.yml");
        }

        this.langGEConfig = YamlConfiguration.loadConfiguration(this.langGEConfigFile);
    }

    public void saveLangFRConfig() {
        if (this.langFRConfig != null && this.langFRConfigFile != null) {
            try {
                this.getLangFRConfig().save(this.langFRConfigFile);
            } catch (IOException var2) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.langFRConfigFile, var2);
            }
        }

    }

    public void saveLangGEConfig() {
        if (this.langGEConfig != null && this.langGEConfigFile != null) {
            try {
                this.getLangGEConfig().save(this.langGEConfigFile);
            } catch (IOException var2) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.langGEConfigFile, var2);
            }
        }

    }

    public void saveConfig() {
        if (this.langConfig != null && this.langConfigFile != null) {
            try {
                this.getConfig().save(this.langConfigFile);
            } catch (IOException var2) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.langConfigFile, var2);
            }
        }

    }
}
