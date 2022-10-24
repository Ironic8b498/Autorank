package me.armar.plugins.autorank.warningmanager;

import java.util.Iterator;
import me.armar.plugins.autorank.Autorank;
import org.bukkit.entity.Player;

public class WarningNoticeTask implements Runnable {
    private final Autorank plugin;

    public WarningNoticeTask(Autorank instance) {
        this.plugin = instance;
    }

    public void run() {
        this.plugin.debugMessage("Run task to show warnings");
        this.plugin.getWarningManager().sendWarnings(this.plugin.getServer().getConsoleSender());
        if (this.plugin.getSettingsConfig().showWarnings()) {
            Iterator var1 = this.plugin.getServer().getOnlinePlayers().iterator();

            while(true) {
                Player p;
                do {
                    if (!var1.hasNext()) {
                        return;
                    }

                    p = (Player)var1.next();
                } while(!p.hasPermission("autorank.noticeonwarning") && !p.isOp());

                this.plugin.getWarningManager().sendWarnings(p);
            }
        }
    }
}
