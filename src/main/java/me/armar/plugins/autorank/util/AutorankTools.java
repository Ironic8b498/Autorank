package me.armar.plugins.autorank.util;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.armar.plugins.autorank.Autorank.autorank;

public class AutorankTools {
    public static final int TICKS_PER_SECOND = 20;
    public static final int TICKS_PER_MINUTE = 1200;
    private static final Set<String> reqTypes = new HashSet();
    private static final Set<String> resTypes = new HashSet();
    private final Autorank plugin;

    public AutorankTools(Autorank autorank) {
        this.plugin = autorank;
    }

    public static boolean isExcludedFromRanking(Player player) {
        if (player.hasPermission("autorank.askdjaslkdj")) {
            return !player.isOp();
        } else {
            return player.hasPermission("autorank.exclude");
        }
    }

    public static boolean containsAtLeast(Player player, ItemStack item, int amount, String displayName) {
        int count = 0;
        displayName = displayName.replace("&", "§");
        ItemStack[] var5 = player.getInventory().getContents();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            ItemStack itemFound = var5[var7];
            if (itemFound != null && itemFound.getType().equals(item.getType()) && itemFound.hasItemMeta() && itemFound.getItemMeta().hasDisplayName() && itemFound.getItemMeta().getDisplayName().equals(displayName)) {
                count += itemFound.getAmount();
            }
        }

        return count >= amount;
    }

    public static String createStringFromList(Collection<?> c) {
        StringBuilder builder = new StringBuilder();
        Object[] array = c.toArray();

        for(int i = 0; i < c.size(); ++i) {
            if (i == 0) {
                builder.append(ChatColor.GRAY + array[i].toString() + ChatColor.RESET);
            } else if (i == c.size() - 1) {
                builder.append(" and " + ChatColor.GRAY + array[i].toString() + ChatColor.RESET);
            } else {
                builder.append(", " + ChatColor.GRAY + array[i].toString() + ChatColor.RESET);
            }
        }

        return builder.toString();
    }

    public static int editDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int[] costs = new int[b.length() + 1];

        int i;
        for(i = 0; i < costs.length; costs[i] = i++) {
        }

        for(i = 1; i <= a.length(); ++i) {
            costs[0] = i;
            int nw = i - 1;

            for(int j = 1; j <= b.length(); ++j) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }

        return costs[b.length()];
    }

    public static String findClosestSuggestion(String input, Collection<String> list) {
        int lowestDistance = 2147483647;
        String bestSuggestion = null;
        Iterator var4 = list.iterator();

        while(var4.hasNext()) {
            String possibility = (String)var4.next();
            int dist = editDistance(input, possibility);
            if (dist < lowestDistance) {
                lowestDistance = dist;
                bestSuggestion = possibility;
            }
        }

        return bestSuggestion + ";" + lowestDistance;
    }

    public static String findMatchingRequirementName(String oldName) {
        oldName = oldName.replaceAll("[^a-zA-Z\\s]", "").trim();
        Iterator var1 = reqTypes.iterator();

        String type;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            type = (String)var1.next();
        } while(!oldName.contains(type) || type.length() != oldName.length());

        return type;
    }

    public static String findMatchingResultName(String oldName) {
        oldName = oldName.replaceAll("[^a-zA-Z\\s]", "").trim();
        Iterator var1 = resTypes.iterator();

        String type;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            type = (String)var1.next();
        } while(!oldName.contains(type) || type.length() != oldName.length());

        return type;
    }

    public static String getStringFromSplitString(String splitString, String splitterCharacter, int element) {
        String[] split = splitString.split(splitterCharacter);
        String returnString = null;

        try {
            returnString = split[element];
            return returnString.trim().equals("") ? null : returnString;
        } catch (ArrayIndexOutOfBoundsException var6) {
            return null;
        }
    }

    public static int stringToTime(String string, TimeUnit time) {
        int res = 0;
        string = string.trim();
        Pattern pattern = Pattern.compile("((\\d+)d)?((\\d+)h)?((\\d+)m)?");
        Matcher matcher = pattern.matcher(string);
        matcher.find();
        String days = matcher.group(2);
        String hours = matcher.group(4);
        String minutes = matcher.group(6);
        if (days == null && hours == null && minutes == null) {
            minutes = string;
        }

        int intDays = (int)stringToDouble(days);
        int intHours = (int)stringToDouble(hours);
        int intMinutes = (int)stringToDouble(minutes);
        if (intDays < 0 && intHours < 0 && intMinutes < 0) {
            return -1;
        } else {
            if (intDays < 0) {
                intDays = 0;
            }

            if (intHours < 0) {
                intHours = 0;
            }

            if (intMinutes < 0) {
                intMinutes = 0;
            }

            res = res + intMinutes;
            res += intHours * 60;
            res += intDays * 60 * 24;
            if (time.equals(TimeUnit.SECONDS)) {
                return res * 60;
            } else if (time.equals(TimeUnit.MINUTES)) {
                return res;
            } else if (time.equals(TimeUnit.HOURS)) {
                return res / 60;
            } else {
                return time.equals(TimeUnit.DAYS) ? res / 1440 : 0;
            }
        }
    }

    public static String makeProgressString(Collection<?> c, String wordBetween, Object currentValue) {
        Object[] array = c.toArray();
        String extraSpace = " ";
        if (wordBetween == null || wordBetween.equals("")) {
            extraSpace = "";
        }

        StringBuilder progress = new StringBuilder();

        for(int i = 0; i < c.size(); ++i) {
            String object = array[i].toString();
            if (i == 0) {
                progress.append(currentValue).append(extraSpace).append(wordBetween).append("/").append(object).append(extraSpace).append(wordBetween);
            } else {
                progress.append(" or ").append(currentValue).append(extraSpace).append(wordBetween).append("/").append(object).append(extraSpace).append(wordBetween);
            }
        }

        return progress.toString();
    }

    public static HashMap<String, Object> makeStatsInfo(Object... strings) {
        HashMap<String, Object> hashmap = new HashMap();

        for(int i = 0; i < strings.length; i += 2) {
            Object string = strings[i];
            if (string != null && strings[i + 1] != null) {
                try {
                    int value = Integer.parseInt(strings[i + 1].toString());
                    if (value < 0) {
                        continue;
                    }
                } catch (NumberFormatException var5) {
                }

                hashmap.put(string.toString(), strings[i + 1]);
            }
        }

        return hashmap;
    }

    public static void registerRequirement(String type) {
        reqTypes.add(type);
    }

    public static void registerResult(String type) {
        resTypes.add(type);
    }

    public static String gsonSerialize(String msg){
        var mm = MiniMessage.miniMessage();
        Component send_msg = mm.deserialize(msg);
        String serialize = GsonComponentSerializer.gson().serialize(send_msg);
        return serialize;
    }

    public static void sendDeserialize(CommandSender sender, String msg){
        var mm = MiniMessage.miniMessage();
        Component send_msg = mm.deserialize(msg);
        autorank.adventure().player((Player) sender).sendMessage(send_msg);
    }

    public static void playersDeserialize(String msg){
        var mm = MiniMessage.miniMessage();
        Component send_msg = mm.deserialize(msg);
        autorank.adventure().players().sendMessage(send_msg);
    }

    public static void consoleDeserialize(String msg){
        var mm = MiniMessage.miniMessage();
        Component send_msg = mm.deserialize(msg);
        autorank.adventure().console().sendMessage(send_msg);
    }
    public static void allDeserialize(String msg){
        var mm = MiniMessage.miniMessage();
        Component send_msg = mm.deserialize(msg);
        autorank.adventure().all().sendMessage(send_msg);
    }

    public static String getFinalArg(final String[] args, final int start)
    {
        final StringBuilder bldr = new StringBuilder();
        for (int i = start; i < args.length; i++)
        {
            if (i != start)
            {
                bldr.append(" ");
            }
            bldr.append(args[i]);
        }
        return bldr.toString();
    }

    public static void sendColoredMessage(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GREEN + msg));
    }

    public static String seperateList(Collection<?> c, String endDivider) {
        Object[] array = c.toArray();
        if (array.length == 1) {
            return array[0].toString();
        } else if (array.length == 0) {
            return null;
        } else {
            StringBuilder string = new StringBuilder();

            for(int i = 0; i < array.length; ++i) {
                if (i == array.length - 1) {
                    string.append(array[i]);
                } else if (i == array.length - 2) {
                    string.append(array[i] + " " + endDivider + " ");
                } else {
                    string.append(array[i] + ", ");
                }
            }

            return string.toString();
        }
    }

    public static double stringToDouble(String string) {
        double res = -1.0D;
        if (string == null) {
            return res;
        } else {
            try {
                res = Double.parseDouble(string);
                return res;
            } catch (NumberFormatException var4) {
                return -1.0D;
            }
        }
    }

    public static String timeToString(int count, TimeUnit time) {
        StringBuilder b = new StringBuilder();
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        if (time.equals(TimeUnit.DAYS)) {
            days = count;
        } else if (time.equals(TimeUnit.HOURS)) {
            days = count / 24;
            hours = count - days * 24;
        } else if (time.equals(TimeUnit.MINUTES)) {
            days = count / 1440;
            count -= days * 1440;
            hours = count / 60;
            minutes = count - hours * 60;
        } else if (time.equals(TimeUnit.SECONDS)) {
            days = count / 86400;
            count -= days * 86400;
            hours = count / 3600;
            count -= hours * 3600;
            minutes = count / 60;
            seconds = count - minutes * 60;
        }

        if (days != 0) {
            b.append(days);
            b.append(" ");
            if (days != 1) {
                b.append(Lang.DAY_PLURAL.getConfigValue());
            } else {
                b.append(Lang.DAY_SINGULAR.getConfigValue());
            }

            if (hours != 0 || minutes != 0) {
                b.append(", ");
            }
        }

        if (hours != 0) {
            b.append(hours);
            b.append(" ");
            if (hours != 1) {
                b.append(Lang.HOUR_PLURAL.getConfigValue());
            } else {
                b.append(Lang.HOUR_SINGULAR.getConfigValue());
            }

            if (minutes != 0) {
                b.append(", ");
            }
        }

        if (minutes != 0 || hours == 0 && days == 0) {
            b.append(minutes);
            b.append(" ");
            if (minutes != 1) {
                b.append(Lang.MINUTE_PLURAL.getConfigValue());
            } else {
                b.append(Lang.MINUTE_SINGULAR.getConfigValue());
            }

            if (seconds != 0) {
                b.append(", ");
            }
        }

        if (seconds != 0) {
            b.append(seconds);
            b.append(" ");
            if (seconds != 1) {
                b.append(Lang.SECOND_PLURAL.getConfigValue());
            } else {
                b.append(Lang.SECOND_SINGULAR.getConfigValue());
            }
        }

        int index = b.lastIndexOf(",");
        if (index != -1) {
            b.replace(index, index + 1, " " + Lang.AND.getConfigValue());
        }

        return b.toString();
    }

    public static Integer largestK(List<Integer> array, int k) {
        PriorityQueue<Integer> queue = new PriorityQueue(k + 1);

        int i;
        for(i = 0; i <= k; ++i) {
            try {
                queue.add(array.get(i));
            } catch (IndexOutOfBoundsException var5) {
                return null;
            }
        }

        for(; i < array.size(); ++i) {
            Integer value = queue.peek();
            if (array.get(i) > value) {
                queue.poll();
                queue.add(array.get(i));
            }
        }

        return queue.peek();
    }

    public static int readTimeInput(String[] args, int offset) {
        StringBuilder builder = new StringBuilder();

        for(int i = offset; i < args.length; ++i) {
            builder.append(args[i]);
        }

        int value;
        if (!builder.toString().contains("m") && !builder.toString().contains("h") && !builder.toString().contains("d")) {
            value = (int)stringToDouble(builder.toString().trim());
        } else {
            if (builder.toString().contains("s")) {
                return -1;
            }

            value = stringToTime(builder.toString(), TimeUnit.MINUTES);
        }

        return value;
    }
}
