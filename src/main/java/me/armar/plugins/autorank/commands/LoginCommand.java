package me.armar.plugins.autorank.commands;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LoginCommand extends AutorankCommand {
    public LoginCommand() {
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        Player player = (Player)sender;
        DateTimeFormatter MMMddyyyyformatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
        DateTimeFormatter MMMddformatter = DateTimeFormatter.ofPattern("MMM dd");
        DateTimeFormatter yyyyformatter = DateTimeFormatter.ofPattern("yyyy");
        String firstLoginDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(player.getFirstPlayed()), ZoneId.systemDefault()).format(MMMddyyyyformatter);
        AutorankTools.sendDeserialize(player, Lang.FIRST_LOGIN_DATE.getConfigValue(firstLoginDate));
        return true;
    }

    public String getDescription() {
        return "Shows first login of server";
    }

    public String getPermission() {
        return "autorank.login";
    }

    public String getUsage() {
        return "/ar login";
    }
}
