package me.armar.plugins.autorank.commands.manager;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public abstract class AutorankCommand implements TabExecutor {

    private final Autorank plugin;

    public AutorankCommand(Autorank instance) {
        this.plugin = instance;
    }

    protected AutorankCommand() {
        plugin = null;
    }

    public abstract String getDescription();

    public abstract String getPermission();

    public abstract String getUsage();

    public abstract boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4);

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return null;
    }

    public boolean hasPermission(String permission, CommandSender sender) {
        var mm = MiniMessage.miniMessage();
        if (!sender.hasPermission(permission)) {
            Component no_permission = mm.deserialize(Lang.NO_PERMISSION.getConfigValue(new Object[]{permission}));
            plugin.adventure().player((Player) sender).sendMessage(no_permission);
            return false;
        } else {
            return true;
        }
    }

    public static List<String> getArgumentOptions(String[] strings) {
        List<String> arguments = new ArrayList();
        Arrays.stream(strings).forEach((string) -> {
            if (string.matches("[-]{2}[a-zA-Z_-]+")) {
                arguments.add(string.replace("--", "").toLowerCase());
            }

        });
        return arguments;
    }

    public void runCommandTask(CompletableFuture<?> task) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Autorank");
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                task.get();
            } catch (ExecutionException | InterruptedException var2) {
                var2.printStackTrace();
            }

        });
    }

    public static List<String> getOptionsStartingWith(Collection<String> options, String started) {
        return options.stream().filter((s) -> {
            return s.toLowerCase().startsWith(started.toLowerCase());
        }).collect(Collectors.toList());
    }

    public static String getStringFromArgs(String[] args, int startArg) {
        StringBuilder string = new StringBuilder();

        for(int i = startArg; i < args.length; ++i) {
            if (i == startArg) {
                string.append(args[i]);
            } else {
                string.append(" ").append(args[i]);
            }
        }

        return string.toString();
    }
}
