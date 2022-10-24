package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.VaultHook;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class MoneyResult extends AbstractResult {
    private long money = -1L;

    public MoneyResult() {
    }

    public boolean applyResult(Player player) {
        if (!this.getAutorank().getDependencyManager().isAvailable(Library.VAULT)) {
            return false;
        } else {
            EconomyResponse res = VaultHook.getEconomy().depositPlayer(player, (double)this.money);
            return res.transactionSuccess();
        }
    }

    public String getDescription() {
        if (this.hasCustomDescription()) {
            return this.getCustomDescription();
        } else {
            String currencyName = "";
            if (this.getAutorank().getDependencyManager().isAvailable(Library.VAULT) && VaultHook.getEconomy() != null) {
                currencyName = VaultHook.getEconomy().currencyNamePlural().trim();
            }

            return Lang.MONEY_RESULT.getConfigValue(this.money + " " + currencyName);
        }
    }

    public boolean setOptions(String[] options) {
        if (options.length > 0) {
            this.money = Long.parseLong(options[0]);
        }

        return this.money >= 0L;
    }
}
