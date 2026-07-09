package me.armar.plugins.autorank.commands.manager;

import com.google.common.collect.Lists;
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
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandsManager implements TabExecutor {
    private final Autorank plugin;
    private final Map<List<String>, AutorankCommand> registeredCommands = new LinkedHashMap<>();

    public CommandsManager(Autorank plugin) {
        this.plugin = plugin;
        this.registeredCommands.put(List.of("add"), new AddCommand(plugin));
        this.registeredCommands.put(List.of("backup"), new BackupCommand(plugin));
        this.registeredCommands.put(List.of("broadcast"), new BroadcastCommand());
        this.registeredCommands.put(List.of("check"), new CheckCommand(plugin));
        this.registeredCommands.put(Arrays.asList("choose", "activate"), new ChooseCommand(plugin));
        this.registeredCommands.put(List.of("complete"), new CompleteCommand(plugin));
        this.registeredCommands.put(List.of("deactivate"), new DeactivateCommand(plugin));
        this.registeredCommands.put(List.of("debug"), new DebugCommand(plugin));
        this.registeredCommands.put(List.of("editor"), new EditorCommand(plugin));
        this.registeredCommands.put(Arrays.asList("fcheck", "forcecheck"), new ForceCheckCommand(plugin));
        this.registeredCommands.put(Arrays.asList("gadd", "globaladd"), new GlobalAddCommand(plugin));
        this.registeredCommands.put(Arrays.asList("gcheck", "globalcheck"), new GlobalCheckCommand(plugin));
        this.registeredCommands.put(Arrays.asList("gset", "globalset"), new GlobalSetCommand(plugin));
        this.registeredCommands.put(List.of("help"), new HelpCommand(plugin));
        this.registeredCommands.put(Arrays.asList("hooks", "hook"), new HooksCommand(plugin));
        this.registeredCommands.put(List.of("import"), new ImportCommand(plugin));
        this.registeredCommands.put(List.of("info"), new InfoCommand(plugin));
        this.registeredCommands.put(Arrays.asList("leaderboard", "leaderboards", "top"), new LeaderboardCommand(plugin));
        this.registeredCommands.put(List.of("login"), new LoginCommand());
        this.registeredCommands.put(List.of("migrate"), new MigrateCommand(plugin));
        this.registeredCommands.put(List.of("reload"), new ReloadCommand(plugin));
        this.registeredCommands.put(Arrays.asList("remove", "rem"), new RemoveCommand(plugin));
        this.registeredCommands.put(List.of("reset"), new ResetCommand(plugin));
        this.registeredCommands.put(List.of("set"), new SetCommand(plugin));
        this.registeredCommands.put(List.of("sync"), new SyncCommand(plugin));
        this.registeredCommands.put(List.of("syncstats"), new SyncStatsCommand(plugin));
        this.registeredCommands.put(Arrays.asList("times", "time"), new TimesCommand(plugin));
        this.registeredCommands.put(List.of("track"), new TrackCommand(plugin));
        this.registeredCommands.put(Arrays.asList("view", "preview"), new ViewCommand(plugin));
    }

    public Map<List<String>, AutorankCommand> getRegisteredCommands() {
        return this.registeredCommands;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        MiniMessage mm = MiniMessage.miniMessage();
        if (args.length == 0) {
            Component var10000 = mm.deserialize(Lang.ABOUT_LINE.getConfigValue());
            Lang var10002 = Lang.DEVELOPED;
            Object[] var10003 = new Object[]{this.plugin.getDescription().getAuthors()};
            var10000 = var10000.append(mm.deserialize("<NEWLINE>" + var10002.getConfigValue(var10003)));
            var10002 = Lang.VERSION;
            var10003 = new Object[]{this.plugin.getDescription().getVersion()};
            var10000 = var10000.append(mm.deserialize("<NEWLINE>" + var10002.getConfigValue(var10003)));
            var10002 = Lang.LIST_OF_COMMANDS;
            Component about = var10000.append(mm.deserialize("<NEWLINE>" + var10002.getConfigValue()));
            this.plugin.adventure().player((Player)sender).sendMessage(about);
        } else {
            String action = args[0];
            List<String> suggestions = new ArrayList<>();
            List<String> bestSuggestions = new ArrayList<>();

            for(Map.Entry<List<String>, AutorankCommand> entry : this.registeredCommands.entrySet()) {
                String suggestion = AutorankTools.findClosestSuggestion(action, entry.getKey());
                suggestions.add(suggestion);

                for(Object o : entry.getKey()) {
                    String actionString = (String)o;
                    if (actionString.equalsIgnoreCase(action)) {
                        return entry.getValue().onCommand(sender, cmd, label, args);
                    }
                }
            }

            for(String suggestion : suggestions) {
                String[] split = suggestion.split(";");
                int editDistance = Integer.parseInt(split[1]);
                if (editDistance <= 2) {
                    bestSuggestions.add(split[0]);
                }
            }

            Component list_of_command = mm.deserialize(Lang.COMMAND_NOT_RECOGNISED.getConfigValue());
            this.plugin.adventure().player((Player)sender).sendMessage(list_of_command);
            if (!bestSuggestions.isEmpty()) {
                Component did_you = mm.deserialize(Lang.DID_YOU.getConfigValue()).append(mm.deserialize(Lang.AR.getConfigValue()).append(mm.deserialize(Objects.requireNonNull(AutorankTools.seperateList(bestSuggestions, Lang.OR.getConfigValue()))).hoverEvent(HoverEvent.showText(mm.deserialize(Lang.THESE_ARE.getConfigValue()).append(mm.deserialize(Lang.QUESTION_MARK.getConfigValue()))))));
                this.plugin.adventure().player((Player)sender).sendMessage(did_you);
            }

            Component list_of_command2 = mm.deserialize(Lang.LIST_OF_COMMANDS.getConfigValue());
            this.plugin.adventure().player((Player)sender).sendMessage(list_of_command2);
        }

        return true;
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (args.length <= 1) {
            List<String> commands = new ArrayList<>();

            for(Map.Entry entry : this.registeredCommands.entrySet()) {
                List list = (List)entry.getKey();
                commands.addAll(list);
            }

            return this.findSuggestedCommands(commands, args[0]);
        } else {
            String subCommand = args[0].trim();
            if (!subCommand.equalsIgnoreCase("set") && !subCommand.equalsIgnoreCase("add") && !subCommand.equalsIgnoreCase("remove") && !subCommand.equalsIgnoreCase("rem") && !subCommand.equalsIgnoreCase("gadd") && !subCommand.equalsIgnoreCase("gset")) {
                for(Map.Entry entry : this.registeredCommands.entrySet()) {
                    for(Object o : (List)entry.getKey()) {
                        String alias = (String)o;
                        if (subCommand.trim().equalsIgnoreCase(alias)) {
                            return ((AutorankCommand)entry.getValue()).onTabComplete(sender, cmd, commandLabel, args);
                        }
                    }
                }

                return null;
            } else if (args.length > 2) {
                String arg = args[2];

                int count;
                try {
                    count = Integer.parseInt(arg);
                } catch (NumberFormatException var11) {
                    count = 0;
                }

                return Lists.newArrayList("" + (count + 5));
            } else {
                return null;
            }
        }
    }

    private List<String> findSuggestedCommands(List<String> list, String string) {
        if (string.isEmpty()) {
            return list;
        } else {
            List<String> returnList = new ArrayList<>();

            for(String item : list) {
                if (item.toLowerCase().startsWith(string.toLowerCase())) {
                    returnList.add(item);
                }
            }

            return returnList;
        }
    }
}
