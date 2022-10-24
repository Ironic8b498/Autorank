package me.armar.plugins.autorank.commands.manager;

import com.google.common.collect.Lists;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.*;
import me.armar.plugins.autorank.util.AutorankTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

public class CommandsManager implements TabExecutor {
    private final Autorank plugin;
    private final Map<List<String>, AutorankCommand> registeredCommands = new LinkedHashMap();

    public CommandsManager(Autorank plugin) {
        this.plugin = plugin;
        this.registeredCommands.put(Arrays.asList("add"), new AddCommand(plugin));
        this.registeredCommands.put(Arrays.asList("help"), new HelpCommand(plugin));
        this.registeredCommands.put(Arrays.asList("set"), new SetCommand(plugin));
        this.registeredCommands.put(Arrays.asList("leaderboard", "leaderboards", "top"), new LeaderboardCommand(plugin));
        this.registeredCommands.put(Arrays.asList("remove", "rem"), new RemoveCommand(plugin));
        this.registeredCommands.put(Arrays.asList("debug"), new DebugCommand(plugin));
        this.registeredCommands.put(Arrays.asList("sync"), new SyncCommand(plugin));
        this.registeredCommands.put(Arrays.asList("syncstats"), new SyncStatsCommand(plugin));
        this.registeredCommands.put(Arrays.asList("reload"), new ReloadCommand(plugin));
        this.registeredCommands.put(Arrays.asList("import"), new ImportCommand(plugin));
        this.registeredCommands.put(Arrays.asList("complete"), new CompleteCommand(plugin));
        this.registeredCommands.put(Arrays.asList("check"), new CheckCommand(plugin));
        this.registeredCommands.put(Arrays.asList("archive", "arch"), new ArchiveCommand(plugin));
        this.registeredCommands.put(Arrays.asList("gcheck", "globalcheck"), new GlobalCheckCommand(plugin));
        this.registeredCommands.put(Arrays.asList("fcheck", "forcecheck"), new ForceCheckCommand(plugin));
        this.registeredCommands.put(Arrays.asList("convert"), new ConvertCommand(plugin));
        this.registeredCommands.put(Arrays.asList("track"), new TrackCommand(plugin));
        this.registeredCommands.put(Arrays.asList("gset", "globalset"), new GlobalSetCommand(plugin));
        this.registeredCommands.put(Arrays.asList("hooks", "hook"), new HooksCommand(plugin));
        this.registeredCommands.put(Arrays.asList("gadd", "globaladd"), new GlobalAddCommand(plugin));
        this.registeredCommands.put(Arrays.asList("view", "preview"), new ViewCommand(plugin));
        this.registeredCommands.put(Arrays.asList("choose", "activate"), new ChooseCommand(plugin));
        this.registeredCommands.put(Arrays.asList("deactivate"), new DeactivateCommand(plugin));
        this.registeredCommands.put(Arrays.asList("times", "time"), new TimesCommand(plugin));
        this.registeredCommands.put(Arrays.asList("reset"), new ResetCommand(plugin));
        this.registeredCommands.put(Arrays.asList("backup"), new BackupCommand(plugin));
        this.registeredCommands.put(Arrays.asList("info"), new InfoCommand(plugin));
        this.registeredCommands.put(Arrays.asList("editor"), new EditorCommand(plugin));
        this.registeredCommands.put(Arrays.asList("migrate"), new MigrateCommand(plugin));
    }

    public Map<List<String>, AutorankCommand> getRegisteredCommands() {
        return this.registeredCommands;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.BLUE + "-----------------------------------------------------");
            sender.sendMessage(ChatColor.GOLD + "Developed by: " + ChatColor.GRAY + this.plugin.getDescription().getAuthors());
            sender.sendMessage(ChatColor.GOLD + "Version: " + ChatColor.GRAY + this.plugin.getDescription().getVersion());
            sender.sendMessage(ChatColor.YELLOW + "Type /ar help for a list of commands.");
            TextComponent component = Component.text("-----------------------------------------------------")
                    .append(Component.text("Developed by: "));
           plugin.adventure().player((Player) sender).sendMessage(component);
            return true;
        } else {
            String action = args[0];
            List<String> suggestions = new ArrayList();
            List<String> bestSuggestions = new ArrayList();
            Iterator var8 = this.registeredCommands.entrySet().iterator();

            while(var8.hasNext()) {
                Entry<List<String>, AutorankCommand> entry = (Entry)var8.next();
                String suggestion = AutorankTools.findClosestSuggestion(action, entry.getKey());
                if (suggestion != null) {
                    suggestions.add(suggestion);
                }

                Iterator var11 = ((List)entry.getKey()).iterator();

                while(var11.hasNext()) {
                    String actionString = (String)var11.next();
                    if (actionString.equalsIgnoreCase(action)) {
                        return entry.getValue().onCommand(sender, cmd, label, args);
                    }
                }
            }

            var8 = suggestions.iterator();

            while(var8.hasNext()) {
                String suggestion = (String)var8.next();
                String[] split = suggestion.split(";");
                int editDistance = Integer.parseInt(split[1]);
                if (editDistance <= 2) {
                    bestSuggestions.add(split[0]);
                }
            }

            sender.sendMessage(ChatColor.RED + "Command not recognised!");
            if (!bestSuggestions.isEmpty()) {
                BaseComponent[] builder = (new ComponentBuilder("Did you perhaps mean ")).color(net.md_5.bungee.api.ChatColor.DARK_AQUA).append("/ar ").color(net.md_5.bungee.api.ChatColor.GREEN).append(AutorankTools.seperateList(bestSuggestions, "or")).event(new HoverEvent(Action.SHOW_TEXT, (new ComponentBuilder("These are suggestions based on your input.")).create())).append("?").color(net.md_5.bungee.api.ChatColor.DARK_AQUA).create();
                if (sender instanceof Player) {
                    Player p = (Player)sender;
                    p.spigot().sendMessage(builder);
                } else {
                    sender.sendMessage(ChatColor.DARK_AQUA + "Did you perhaps mean " + ChatColor.GREEN + "/ar " + AutorankTools.seperateList(bestSuggestions, "or") + ChatColor.DARK_AQUA + "?");
                }
            }

            sender.sendMessage(ChatColor.YELLOW + "Use '/ar help' for a list of commands.");
            return true;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Iterator var12;
        Entry entry;
        if (args.length <= 1) {
            List<String> commands = new ArrayList();
            var12 = this.registeredCommands.entrySet().iterator();

            while(var12.hasNext()) {
                entry = (Entry)var12.next();
                List<String> list = (List)entry.getKey();
                commands.addAll(list);
            }

            return this.findSuggestedCommands(commands, args[0]);
        } else {
            String subCommand = args[0].trim();
            if (!subCommand.equalsIgnoreCase("set") && !subCommand.equalsIgnoreCase("add") && !subCommand.equalsIgnoreCase("remove") && !subCommand.equalsIgnoreCase("rem") && !subCommand.equalsIgnoreCase("gadd") && !subCommand.equalsIgnoreCase("gset")) {
                var12 = this.registeredCommands.entrySet().iterator();

                while(var12.hasNext()) {
                    entry = (Entry)var12.next();
                    Iterator var8 = ((List)entry.getKey()).iterator();

                    while(var8.hasNext()) {
                        String alias = (String)var8.next();
                        if (subCommand.trim().equalsIgnoreCase(alias)) {
                            return ((AutorankCommand)entry.getValue()).onTabComplete(sender, cmd, commandLabel, args);
                        }
                    }
                }

                return null;
            } else if (args.length > 2) {
                String arg = args[2];
                boolean var7 = false;

                int count;
                try {
                    count = Integer.parseInt(arg);
                } catch (NumberFormatException var10) {
                    count = 0;
                }

                return Lists.newArrayList("" + (count + 5));
            } else {
                return null;
            }
        }
    }

    private List<String> findSuggestedCommands(List<String> list, String string) {
        if (string.equals("")) {
            return list;
        } else {
            List<String> returnList = new ArrayList();
            Iterator var4 = list.iterator();

            while(var4.hasNext()) {
                String item = (String)var4.next();
                if (item.toLowerCase().startsWith(string.toLowerCase())) {
                    returnList.add(item);
                }
            }

            return returnList;
        }
    }
}
