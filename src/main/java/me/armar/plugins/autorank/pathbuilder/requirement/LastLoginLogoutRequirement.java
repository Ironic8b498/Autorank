package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.LastLoginAPIHook;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.UUID;

public class LastLoginLogoutRequirement extends AbstractRequirement {
    private LastLoginAPIHook handler = null;
    private int lastlogout = -1;

    public LastLoginLogoutRequirement() {
    }

    public String getDescription() {
          return Lang.LAST_LOGIN_LOGOUT_REQUIREMENT.getConfigValue(this.lastlogout);
    }

    public String getProgressString(UUID uuid) {
        LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.handler.getlastLogout(uuid)), TimeZone.getDefault().toZoneId());
        LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.handler.getlastLogin(uuid)), TimeZone.getDefault().toZoneId());
        Duration duration = Duration.between(from, to);
        return duration.toMinutes() + "/" + this.lastlogout;
    }


    public boolean meetsRequirement(UUID uuid) {
        LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.handler.getlastLogout(uuid)), TimeZone.getDefault().toZoneId());
        LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.handler.getlastLogin(uuid)), TimeZone.getDefault().toZoneId());
        Duration duration = Duration.between(from, to);
        return duration.toMinutes() >= this.lastlogout;
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.LASTLOGINAPI);
        this.handler = (LastLoginAPIHook) this.getAutorank().getDependencyManager().getLibraryHook(Library.LASTLOGINAPI).orElse(null);

        try {
            this.lastlogout = Integer.parseInt(options[0]);
        } catch (NumberFormatException var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.lastlogout < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        } else if (this.handler != null && this.handler.isHooked()) {
            return true;
        } else {
            this.registerWarningMessage("Last Login is not available");
            return false;
        }
    }

    public double getProgressPercentage(UUID uuid) {
        LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.handler.getlastLogout(uuid)), TimeZone.getDefault().toZoneId());
        LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.handler.getlastLogin(uuid)), TimeZone.getDefault().toZoneId());
        Duration duration = Duration.between(from, to);
        return (double) duration.toMinutes() * 1.0D / (double) this.lastlogout;
    }
}
