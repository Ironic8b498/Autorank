package me.armar.plugins.autorank.pathbuilder.result;

import java.util.ArrayList;
import java.util.List;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class CommandResult extends AbstractResult {
    private List<String> commands = null;
    private Server server = null;

    public CommandResult() {
    }

    public boolean applyResult(Player player) {
        if (this.server != null) {
            for(String command : this.commands) {
                String cmd = command.replace("&p", player.getName());
                cmd = cmd.replace("@p", player.getName());
                cmd = cmd.replace("&u", player.getUniqueId().toString());
                cmd = cmd.replace("@u", player.getUniqueId().toString());
                String finalCmd = cmd;
                Bukkit.getScheduler().callSyncMethod(this.getAutorank(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd));
            }
        }

        return this.server != null;
    }

    public String getDescription() {
        return this.hasCustomDescription() ? this.getCustomDescription() : Lang.COMMAND_RESULT.getConfigValue(AutorankTools.createStringFromList(this.commands));
    }

    public void setOptions(String[] commands) {
        this.server = this.getAutorank().getServer();
        List<String> replace = new ArrayList<>();

        for(String command : commands) {
            replace.add(command.trim());
        }

        this.commands = replace;
    }
}
