package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

public class EffectResult extends AbstractResult {
    private int data;
    private Effect effect;

    public EffectResult() {
    }

    public boolean applyResult(Player player) {
        if (this.effect != null) {
            player.getWorld().playEffect(player.getLocation(), this.effect, this.data);
        }

        return this.effect != null;
    }

    public String getDescription() {
        return this.hasCustomDescription() ? this.getCustomDescription() : Lang.EFFECT_RESULT.getConfigValue(this.effect.name());
    }

    public boolean setOptions(String[] options) {
        if (options.length > 0) {
            this.effect = Effect.valueOf(options[0]);
        }

        if (options.length > 1) {
            this.data = Integer.parseInt(options[1]);
        }

        return this.effect != null;
    }
}
