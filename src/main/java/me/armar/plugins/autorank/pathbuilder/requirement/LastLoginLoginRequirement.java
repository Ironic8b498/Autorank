package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.hooks.LastLoginAPIHook;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.UUID;

public class LastLoginLoginRequirement extends AbstractRequirement {
    private LastLoginAPIHook handler = null;
    private int lastlogin = -1;

    public LastLoginLoginRequirement() {
    }

    public String getDescription() {
        return Lang.LAST_LOGIN_LOGIN_REQUIREMENT.getConfigValue(this.lastlogin);
    }

    public String getProgressString(UUID uuid) {
        LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.handler.getlastLogin(uuid)), TimeZone.getDefault().toZoneId());
        LocalDateTime to = LocalDateTime.now();
        Duration duration = Duration.between(from, to);
        return duration.toMinutes() + "/" + this.lastlogin;
    }


    public boolean meetsRequirement(UUID uuid) {
        LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.handler.getlastLogin(uuid)), TimeZone.getDefault().toZoneId());
        LocalDateTime to = LocalDateTime.now();
        Duration duration = Duration.between(from, to);
        return duration.toMinutes() >= this.lastlogin;
    }

    public boolean initRequirement(String[] options) {
        this.addDependency(Library.LASTLOGINAPI);
        this.handler = (LastLoginAPIHook) this.getAutorank().getDependencyManager().getLibraryHook(Library.LASTLOGINAPI).orElse(null);

        try {
            this.lastlogin = Integer.parseInt(options[0]);
        } catch (NumberFormatException var3) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (this.lastlogin < 0) {
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
        LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.handler.getlastLogin(uuid)), TimeZone.getDefault().toZoneId());
        LocalDateTime to = LocalDateTime.now();
        Duration duration = Duration.between(from, to);
        return (double) duration.toMinutes() * 1.0D / (double) this.lastlogin;
    }
}
