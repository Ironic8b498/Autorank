package me.armar.plugins.autorank.config;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ConcurrentModificationException;

public class SimpleYamlConfiguration extends YamlConfiguration {
    File file;

    public SimpleYamlConfiguration(Autorank plugin, String fileName, String name) throws InvalidConfigurationException {
        String folderPath = plugin.getDataFolder().getAbsolutePath() + File.separator;
        this.file = new File(folderPath + fileName);
        if (!this.file.exists()) {
            if (plugin.getResource(fileName) != null) {
                plugin.saveResource(fileName, false);
                plugin.debugMessage("New " + name + " file copied from jar");

                try {
                    this.load(this.file);
                } catch (Exception var7) {
                    throw new InvalidConfigurationException(var7.getMessage());
                }
            }
        } else {
            try {
                this.load(this.file);
                plugin.debugMessage(name + " file loaded");
            } catch (Exception var6) {
                throw new InvalidConfigurationException(var6.getMessage());
            }
        }

    }

    public File getInternalFile() {
        return this.file;
    }

    public void loadFile() {
        try {
            this.load(this.file);
        } catch (FileNotFoundException var2) {
        } catch (IOException var3) {
        } catch (InvalidConfigurationException var4) {
        }

    }

    public void reloadFile() {
        this.loadFile();
        this.saveFile();
    }

    public void saveFile() {
        try {
            if (this.file == null) {
                Autorank.getInstance().debugMessage("Can't save file, because it's null!");
                return;
            }

            this.save(this.file);
        } catch (ConcurrentModificationException var2) {
            this.saveFile();
        } catch (NullPointerException var3) {
            Autorank.getInstance().debugMessage("Save file thrown NPE:" + var3.getMessage());
            Autorank.getInstance().debugMessage("FILE TO SAVE: " + this.file);
            var3.printStackTrace();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }
}
