package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommandResult extends AbstractResult {
    private List<String> commands = null;
    private Server server = null;

    public CommandResult() {
    }

    public boolean applyResult(Player player) {
        if (this.server != null) {
            Iterator var2 = this.commands.iterator();

            while(var2.hasNext()) {
                String command = (String)var2.next();
                String cmd = command.replace("&p", player.getName());
                cmd = cmd.replace("@p", player.getName());
                String finalCmd = cmd;
                Bukkit.getScheduler().callSyncMethod(this.getAutorank(), () -> {
                    return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
                });
            }
        }

        return this.server != null;
    }

    public String getDescription() {
        return this.hasCustomDescription() ? this.getCustomDescription() : Lang.COMMAND_RESULT.getConfigValue(AutorankTools.createStringFromList(this.commands));
    }

    public boolean setOptions(String[] commands) {
        this.server = this.getAutorank().getServer();
        List<String> replace = new ArrayList();
        String[] var3 = commands;
        int var4 = commands.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String command = var3[var5];
            replace.add(command.trim());
        }

        this.commands = replace;
        return true;
    }
}
