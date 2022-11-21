package me.armar.plugins.autorank.language;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class LanguageHandler {
    private FileConfiguration languageConfig;
    private FileConfiguration langGEConfig;
    private FileConfiguration langFRConfig;
    private File languageConfigFile;
    private File langGEConfigFile;
    private File langFRConfigFile;
    private final Autorank plugin;

    public LanguageHandler(Autorank plugin) {
        this.plugin = plugin;
    }

    public void createNewFile() {
        this.reloadConfig();
        this.saveConfig();
        Lang.setFile(this.languageConfig);
        this.loadConfig();
        this.plugin.debugMessage("Language file loaded (lang.yml)");
    }

    public void createNewlangFRFile() {
        this.reloadLangFRConfig();
        this.saveLangFRConfig();
        LangFR.setFile(this.langFRConfig);
        this.loadLangFRConfig();
        this.plugin.debugMessage("Language file loaded (langFR.yml)");
    }
    public void createNewlangGEFile() {
        this.reloadLangGEConfig();
        this.saveLangGEConfig();
        LangGE.setFile(this.langGEConfig);
        this.loadLangGEConfig();
        this.plugin.debugMessage("Language file loaded (langGE.yml)");
    }

    public FileConfiguration getConfig() {
        if (this.languageConfig == null) {
            this.reloadConfig();
        }

        return this.languageConfig;
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

    public void loadLangFRConfig() {
        this.langFRConfig.options().header("French Language file");
        LangFR[] var1 = LangFR.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            LangFR value = var1[var3];
            this.langFRConfig.addDefault(value.getPath(), value.getDefault());
        }

        this.langFRConfig.options().copyDefaults(true);
        this.saveLangFRConfig();
    }

    public void loadLangGEConfig() {
        this.langGEConfig.options().header("German Language file");
        LangGE[] var1 = LangGE.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            LangGE value = var1[var3];
            this.langGEConfig.addDefault(value.getPath(), value.getDefault());
        }

        this.langGEConfig.options().copyDefaults(true);
        this.saveLangGEConfig();
    }

    public void reloadConfig() {
        if (this.languageConfigFile == null) {
            this.languageConfigFile = new File(this.plugin.getDataFolder() + "/lang", "lang.yml");
        }

        this.languageConfig = YamlConfiguration.loadConfiguration(this.languageConfigFile);
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
        if (this.languageConfig != null && this.languageConfigFile != null) {
            try {
                this.getConfig().save(this.languageConfigFile);
            } catch (IOException var2) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.languageConfigFile, var2);
            }

        }
    }
}
