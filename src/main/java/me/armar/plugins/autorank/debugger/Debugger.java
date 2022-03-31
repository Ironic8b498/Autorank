package me.armar.plugins.autorank.debugger;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.builders.RequirementBuilder;
import me.armar.plugins.autorank.pathbuilder.builders.ResultBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class Debugger {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final DateFormat humanDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Autorank plugin;
    public static boolean debuggerEnabled = false;

    public Debugger(Autorank instance) {
        this.plugin = instance;
    }

    public String createDebugFile() {
        String dateFormatSave = dateFormat.format(new Date());
        File txt = new File(this.plugin.getDataFolder() + "/debugger", "debug-" + dateFormatSave + ".txt");

        try {
            txt.getParentFile().mkdirs();
            txt.createNewFile();
        } catch (IOException var9) {
            var9.printStackTrace();
            return dateFormatSave;
        }

        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new FileWriter(txt));
        } catch (IOException var8) {
            var8.printStackTrace();
            return dateFormatSave;
        }

        try {
            out.write("This is a debug file of Autorank. You should give this to an author or ticket manager of Autorank.");
            out.newLine();
            out.write("You can go to http://pastebin.com/ and paste this file. Then, give the link and state the problems you're having in a ticket on the Autorank page.");
            out.newLine();
            out.write("");
            out.newLine();
            out.write("Date created: " + humanDateFormat.format(new Date()));
            out.newLine();
            out.write("");
            out.newLine();
            out.write("Autorank version: " + this.plugin.getDescription().getVersion());
            out.newLine();
            out.write("");
            out.newLine();
            out.write("Server implementation: " + this.plugin.getServer().getVersion());
            out.newLine();
            out.write("");
            out.newLine();
            out.write("Server version: " + this.plugin.getServer().getBukkitVersion());
            out.newLine();
            out.write("");
            out.newLine();
            out.write("Server warning state: " + this.plugin.getServer().getWarningState());
            out.newLine();
            out.write("");
            out.newLine();
            out.write("Paths defined: ");
            out.newLine();
            out.write("");
            out.newLine();
            Iterator var4 = this.plugin.getPathManager().debugPaths().iterator();

            while(var4.hasNext()) {
                String change = (String)var4.next();
                out.write(change);
                out.newLine();
            }

            out.write("");
            out.newLine();
            out.write("Using MySQL: " + this.plugin.getSettingsConfig().useMySQL());
            out.newLine();
            out.write("");
            out.newLine();
            out.write("Java version: " + System.getProperty("java.version"));
            out.newLine();
            out.write("");
            out.newLine();
            out.write("Operating system: " + System.getProperty("os.name"));
            out.newLine();
            out.write("");
            out.newLine();
            out.write("OS version: " + System.getProperty("os.version"));
            out.newLine();
            out.write("");
            out.newLine();
            out.write("OS architecture: " + System.getProperty("os.arch"));
            out.newLine();
            out.write("");
            out.newLine();
            out.write("Loaded addons: " + this.plugin.getAddonManager().getLoadedAddons().toString());
            out.newLine();
            out.write("");
            out.newLine();
            out.write("Requirements registered: ");
            out.newLine();
            var4 = RequirementBuilder.getRegisteredRequirements().iterator();

            Class result;
            while(var4.hasNext()) {
                result = (Class)var4.next();
                out.write(result.getName());
                out.newLine();
            }

            out.write("");
            out.newLine();
            out.write("Results registered: ");
            out.newLine();
            var4 = ResultBuilder.getRegisteredResults().iterator();

            while(true) {
                if (!var4.hasNext()) {
                    out.write("");
                    out.newLine();
                    break;
                }

                result = (Class)var4.next();
                out.write(result.getName());
                out.newLine();
            }
        } catch (IOException var10) {
            var10.printStackTrace();

            try {
                out.close();
            } catch (IOException var6) {
                var6.printStackTrace();
            }

            return dateFormatSave;
        }

        try {
            out.close();
            return dateFormatSave;
        } catch (IOException var7) {
            var7.printStackTrace();
            return dateFormatSave;
        }
    }
}
