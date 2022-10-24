package me.armar.plugins.autorank.hooks.download;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DependencyDownloader {
    private final Autorank plugin;
    private boolean hasLoaded;

    public DependencyDownloader(Autorank plugin) {
        this.plugin = plugin;
        this.hasLoaded = false;
    }

    public void downloadDependency(String name, String id) {
        File file = new File(this.plugin.getDataFolder().getParent(), name + ".jar");
        if (!file.exists() && Bukkit.getPluginManager().getPlugin(name) == null) {
            try {
                URL url = new URL("https://api.spiget.org/v2/resources/" + id + "/download");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                InputStream in = connection.getInputStream();
                OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                byte[] buffer = new byte[1024];

                while(true) {
                    int numRead;
                    if ((numRead = in.read(buffer)) == -1) {
                        in.close();
                        out.close();
                        break;
                    }

                    out.write(buffer, 0, numRead);
                }
            } catch (FileNotFoundException var10) {
                Bukkit.getConsoleSender().sendMessage("[Autorank] " + ChatColor.RED + "The dependency " + ChatColor.AQUA + name + ChatColor.RED + " could not be downloaded!");
                return;
            } catch (IOException var11) {
                var11.printStackTrace();
                return;
            }

            this.loadPlugin(file);
            Bukkit.getConsoleSender().sendMessage("[Autorank] " + ChatColor.GREEN + "The dependency " + ChatColor.AQUA + name + ChatColor.GREEN + " was successfully downloaded!");
            this.hasLoaded = true;
        }
    }

    public Autorank getPlugin() {
        return this.plugin;
    }

    public boolean hasLoaded() {
        return this.hasLoaded;
    }

    private void loadPlugin(File file) {
        try {
            Bukkit.getPluginManager().loadPlugin(file);
        } catch (InvalidPluginException | InvalidDescriptionException var3) {
            var3.printStackTrace();
        }

    }
}
