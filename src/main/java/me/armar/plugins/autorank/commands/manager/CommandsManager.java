package me.armar.plugins.autorank.commands.manager;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.*;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.Map.Entry;

public class CommandsManager implements TabExecutor {
    private final Autorank plugin;
    private final Map<List<String>, AutorankCommand> registeredCommands = new LinkedHashMap();

    public CommandsManager(Autorank plugin) {
        this.plugin = plugin;
        this.registeredCommands.put(List.of("add"), new AddCommand(plugin));
        this.registeredCommands.put(List.of("help"), new HelpCommand(plugin));
        this.registeredCommands.put(List.of("set"), new SetCommand(plugin));
        this.registeredCommands.put(Arrays.asList("leaderboard", "leaderboards", "top"), new LeaderboardCommand(plugin));
        this.registeredCommands.put(Arrays.asList("remove", "rem"), new RemoveCommand(plugin));
        this.registeredCommands.put(List.of("debug"), new DebugCommand(plugin));
        this.registeredCommands.put(List.of("sync"), new SyncCommand(plugin));
        this.registeredCommands.put(List.of("syncstats"), new SyncStatsCommand(plugin));
        this.registeredCommands.put(List.of("reload"), new ReloadCommand(plugin));
        this.registeredCommands.put(List.of("import"), new ImportCommand(plugin));
        this.registeredCommands.put(List.of("complete"), new CompleteCommand(plugin));
        this.registeredCommands.put(List.of("check"), new CheckCommand(plugin));
        this.registeredCommands.put(Arrays.asList("archive", "arch"), new ArchiveCommand(plugin));
        this.registeredCommands.put(Arrays.asList("gcheck", "globalcheck"), new GlobalCheckCommand(plugin));
        this.registeredCommands.put(Arrays.asList("fcheck", "forcecheck"), new ForceCheckCommand(plugin));
        this.registeredCommands.put(List.of("convert"), new ConvertCommand(plugin));
        this.registeredCommands.put(List.of("track"), new TrackCommand(plugin));
        this.registeredCommands.put(Arrays.asList("gset", "globalset"), new GlobalSetCommand(plugin));
        this.registeredCommands.put(Arrays.asList("hooks", "hook"), new HooksCommand(plugin));
        this.registeredCommands.put(Arrays.asList("gadd", "globaladd"), new GlobalAddCommand(plugin));
        this.registeredCommands.put(Arrays.asList("view", "preview"), new ViewCommand(plugin));
        this.registeredCommands.put(Arrays.asList("choose", "activate"), new ChooseCommand(plugin));
        this.registeredCommands.put(List.of("deactivate"), new DeactivateCommand(plugin));
        this.registeredCommands.put(Arrays.asList("times", "time"), new TimesCommand(plugin));
        this.registeredCommands.put(List.of("reset"), new ResetCommand(plugin));
        this.registeredCommands.put(List.of("backup"), new BackupCommand(plugin));
        this.registeredCommands.put(List.of("info"), new InfoCommand(plugin));
        this.registeredCommands.put(List.of("editor"), new EditorCommand(plugin));
        this.registeredCommands.put(List.of("migrate"), new MigrateCommand(plugin));
        this.registeredCommands.put(List.of("broadcast"), new BroadcastCommand(plugin));
    }

    public Map<List<String>, AutorankCommand> getRegisteredCommands() {
        return this.registeredCommands;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        var mm = MiniMessage.miniMessage();
        if (sender.hasPermission("autorank.noabout")) {
            return true;
        }
        if (args.length == 0) {
            Component about = mm.deserialize(Lang.ABOUT_LINE.getConfigValue())
                    .append(mm.deserialize( "<NEWLINE>" + Lang.DEVELOPED.getConfigValue(this.plugin.getDescription().getAuthors())))
                    .append(mm.deserialize("<NEWLINE>" + Lang.VERSION.getConfigValue(this.plugin.getDescription().getVersion())))
                    .append(mm.deserialize("<NEWLINE>" + Lang.LIST_OF_COMMANDS.getConfigValue()));
            plugin.adventure().player((Player) sender).sendMessage(about);
            return true;
        } else {
            String action = args[0];
            List<String> suggestions = new ArrayList();
            List<String> bestSuggestions = new ArrayList();
            Iterator var8 = this.registeredCommands.entrySet().iterator();

            while (var8.hasNext()) {
                Entry<List<String>, AutorankCommand> entry = (Entry) var8.next();
                String suggestion = AutorankTools.findClosestSuggestion(action, entry.getKey());
                if (suggestion != null) {
                    suggestions.add(suggestion);
                }

                Iterator var11 = ((List) entry.getKey()).iterator();

                while (var11.hasNext()) {
                    String actionString = (String) var11.next();
                    if (actionString.equalsIgnoreCase(action)) {
                        return entry.getValue().onCommand(sender, cmd, label, args);
                    }
                }
            }

            var8 = suggestions.iterator();

            while (var8.hasNext()) {
                String suggestion = (String) var8.next();
                String[] split = suggestion.split(";");
                int editDistance = Integer.parseInt(split[1]);
                if (editDistance <= 2) {
                    bestSuggestions.add(split[0]);
                }
            }

            Component list_of_command = mm.deserialize(Lang.COMMAND_NOT_RECOGNISED.getConfigValue());
            plugin.adventure().player((Player) sender).sendMessage(list_of_command);
            if (!bestSuggestions.isEmpty()) {
                Component did_you = mm.deserialize(Lang.DID_YOU.getConfigValue())
                        .append(mm.deserialize(Lang.AR.getConfigValue())
                                .append(mm.deserialize(AutorankTools.seperateList(bestSuggestions, Lang.OR.getConfigValue()))
                                        .hoverEvent(HoverEvent.showText(mm.deserialize(Lang.THESE_ARE.getConfigValue())
                                                .append(mm.deserialize(Lang.QUESTION_MARK.getConfigValue()))))));
                if (sender instanceof Player p) {
                    plugin.adventure().player((Player) sender).sendMessage(did_you);
                } else {
                    Component did_you2 = mm.deserialize(Lang.DID_YOU.getConfigValue())
                            .append(mm.deserialize(Lang.AR.getConfigValue())
                                    .append(mm.deserialize(AutorankTools.seperateList(bestSuggestions, Lang.OR.getConfigValue())))
                                    .append(mm.deserialize(Lang.QUESTION_MARK.getConfigValue())));
                    plugin.adventure().player((Player) sender).sendMessage(did_you2);
                }
            }

            Component list_of_command2 = mm.deserialize(Lang.LIST_OF_COMMANDS.getConfigValue());
            plugin.adventure().player((Player) sender).sendMessage(list_of_command2);
            return true;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 1) {
            List<String> commands = new ArrayList();
            if (sender.hasPermission("autorank.add")) {
                commands.add("add");
            }
            if (sender.hasPermission("autorank.archive")) {
                commands.add("arch");
                commands.add("archive");
            }
            if (sender.hasPermission("autorank.backup.storage")) {
                commands.add("backup");
            }
            if (sender.hasPermission("autorank.admin")) {
                commands.add("broadcast");
            }
            if (sender.hasPermission("autorank.check")) {
                commands.add("check");
            }
            if (sender.hasPermission("autorank.choose")) {
                commands.add("activate");
                commands.add("choose");
            }
            if (sender.hasPermission("autorank.complete")) {
                commands.add("complete");
            }
            if (sender.hasPermission("autorank.convert.storage")) {
                commands.add("convert");
            }
            if (sender.hasPermission("autorank.deactivate")) {
                commands.add("deactivate");
            }
            if (sender.hasPermission("autorank.debug")) {
                commands.add("debug");
            }
            if (sender.hasPermission("autorank.editor")) {
                commands.add("editor");
            }
            if (sender.hasPermission("autorank.forcecheck")) {
                commands.add("fcheck");
                commands.add("forcecheck");
            }
            if (sender.hasPermission("autorank.gadd")) {
                commands.add("gadd");
                commands.add("globaladd");
            }
            if (sender.hasPermission("autorank.gcheck")) {
                commands.add("gcheck");
                commands.add("globalcheck");
            }
            if (sender.hasPermission("autorank.gset")) {
                commands.add("gset");
                commands.add("globalset");
            }
            if (sender.hasPermission("autorank.help")) {
                commands.add("help");
            }
            if (sender.hasPermission("autorank.hooks")) {
                commands.add("hook");
                commands.add("hooks");
            }
            if (sender.hasPermission("autorank.import")) {
                commands.add("import");
            }
            if (sender.hasPermission("autorank.info.*")) {
                commands.add("info");
            }
            if (sender.hasPermission("autorank.leaderboard")) {
                commands.add("leaderboard");
                commands.add("leaderboards");
            }
            if (sender.hasPermission("autorank.migrate")) {
                commands.add("migrate");
            }
            if (sender.hasPermission("autorank.reload")) {
                commands.add("reload");
            }
            if (sender.hasPermission("autorank.remove")) {
                commands.add("rem");
                commands.add("remove");
            }
            if (sender.hasPermission("autorank.reset")) {
                commands.add("reset");
            }
            if (sender.hasPermission("autorank.set")) {
                commands.add("set");
            }
            if (sender.hasPermission("autorank.sync")) {
                commands.add("sync");
                commands.add("syncstats");
            }
            if (sender.hasPermission("autorank.time")) {
                commands.add("time");
                commands.add("times");
            }
            if (sender.hasPermission("autorank.top")) {
                commands.add("top");
            }
            if (sender.hasPermission("autorank.track")) {
                commands.add("track");
            }
            if (sender.hasPermission("autorank.view")) {
                commands.add("view");
            }

            //create new array
            List<String> completions = new ArrayList<>();
            if (!(args[0] == "")) {
                //copy matches of first argument from list (ex: if first arg is 'm' will return just string that starts with m)
                StringUtil.copyPartialMatches(args[0], commands, completions);
            } else {
                //if arg is null return whole list
                completions = commands;
            }
            //sort the list
            Collections.sort(completions);
            return completions;
        }
        return null;
    }
}