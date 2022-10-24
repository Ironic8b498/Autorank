package me.armar.plugins.autorank.warningmanager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import me.armar.plugins.autorank.Autorank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class WarningManager {
    private final Autorank plugin;
    private final HashMap<String, Integer> warnings = new HashMap();
    public static final int LOW_PRIORITY_WARNING = 1;
    public static final int MEDIUM_PRIORITY_WARNING = 5;
    public static final int HIGH_PRIORITY_WARNING = 10;

    public WarningManager(Autorank plugin) {
        this.plugin = plugin;
    }

    private String findHighestPriorityWarning() {
        String highestWarning = null;
        int highestPriority = 0;
        Iterator var3 = this.warnings.entrySet().iterator();

        while(var3.hasNext()) {
            Entry<String, Integer> entry = (Entry)var3.next();
            if (entry.getValue() > highestPriority) {
                highestPriority = entry.getValue();
                highestWarning = entry.getKey();
            }
        }

        return highestWarning;
    }

    public String getHighestWarning() {
        return this.findHighestPriorityWarning();
    }

    public void registerWarning(String message, int priority) {
        if (priority > 10) {
            priority = 10;
        } else if (priority < 1) {
            priority = 1;
        }

        this.plugin.getLoggerManager().logMessage("Warning occurred: " + message);
        this.warnings.put(message, priority);
    }

    public void registerWarning(String message) {
        this.registerWarning(message, 1);
    }

    public void startWarningTask() {
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new WarningNoticeTask(this.plugin), 0L, 600L);
    }

    public HashMap<String, Integer> getWarnings() {
        return this.warnings;
    }

    public void sendWarnings(CommandSender sender) {
        Entry warning;
        String priorityString;
        for(Iterator var2 = this.getWarnings().entrySet().iterator(); var2.hasNext(); sender.sendMessage(String.format(ChatColor.DARK_AQUA + "<Autorank warning> " + ChatColor.RED + "(%s priority): " + ChatColor.GREEN + "%s ", priorityString, warning.getKey()))) {
            warning = (Entry)var2.next();
            priorityString = "Low";
            int warningValue = (Integer)warning.getValue();
            if (warningValue > 3 && warningValue < 7) {
                priorityString = "Medium";
            } else if (warningValue > 6) {
                priorityString = "High";
            }
        }

    }

    public void clearWarnings() {
        this.warnings.clear();
    }
}
