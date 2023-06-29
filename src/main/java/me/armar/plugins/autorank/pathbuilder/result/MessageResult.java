package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.entity.Player;

public class MessageResult extends AbstractResult {
    String msg = null;

    public MessageResult() {
    }

    public boolean applyResult(Player player) {
        if (player == null) {
            return false;
        } else {
            this.msg = this.msg.replace("&p", player.getName());
            this.msg = this.msg.replace("@p", player.getName());
            AutorankTools.sendDeserialize(player, this.msg);
            if (this.msg.startsWith("deserialize ") ){
                this.msg = msg.replace("deserialize ", "");
                AutorankTools.sendDeserialize(player, this.msg);
                return true;
            }

            //this.msg = this.msg.replaceAll("(&([a-z0-9]))", "§$2");
           // player.sendMessage(this.msg);
            return this.msg != null;
        }
    }

    public String getDescription() {
        return this.hasCustomDescription() ? this.getCustomDescription() : Lang.MESSAGE_RESULT.getConfigValue(this.msg);
    }

    public boolean setOptions(String[] options) {
        if (options.length > 0) {
            this.msg = options[0];
        }

        return this.msg != null;
    }
}
