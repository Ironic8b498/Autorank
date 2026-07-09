package me.armar.plugins.autorank.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigUpdater {
    public static void update(Plugin plugin, String resourceName, File toUpdate, List<String> ignoredSections) throws IOException {
        BufferedReader newReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(plugin.getResource(resourceName)), StandardCharsets.UTF_8));
        List<String> newLines = newReader.lines().collect(Collectors.toList());
        newReader.close();
        FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(toUpdate);
        FileConfiguration newConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(plugin.getResource(resourceName))));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(toUpdate), StandardCharsets.UTF_8));
        List<String> ignoredSectionsArrayList = new ArrayList<>(ignoredSections);
        ignoredSectionsArrayList.removeIf((ignoredSection) -> !newConfig.isConfigurationSection(ignoredSection));
        Yaml yaml = new Yaml();
        Map<String, String> comments = parseComments(newLines, ignoredSectionsArrayList, oldConfig, yaml);
        write(newConfig, oldConfig, comments, ignoredSectionsArrayList, writer, yaml);
    }

    private static void write(FileConfiguration newConfig, FileConfiguration oldConfig, Map<String, String> comments, List<String> ignoredSections, BufferedWriter writer, Yaml yaml) throws IOException {
        label45:
        for(String key : newConfig.getKeys(true)) {
            String[] keys = key.split("\\.");
            String actualKey = keys[keys.length - 1];
            String comment = comments.remove(key);
            StringBuilder prefixBuilder = new StringBuilder();
            int indents = keys.length - 1;
            appendPrefixSpaces(prefixBuilder, indents);
            String prefixSpaces = prefixBuilder.toString();
            if (comment != null) {
                writer.write(comment);
            }

            for(String ignoredSection : ignoredSections) {
                if (key.startsWith(ignoredSection)) {
                    continue label45;
                }
            }

            Object newObj = newConfig.get(key);
            Object oldObj = oldConfig.get(key);
            if (newObj instanceof ConfigurationSection && oldObj instanceof ConfigurationSection) {
                writeSection(writer, actualKey, prefixSpaces, (ConfigurationSection)oldObj);
            } else if (newObj instanceof ConfigurationSection) {
                writeSection(writer, actualKey, prefixSpaces, (ConfigurationSection)newObj);
            } else if (oldObj != null) {
                write(oldObj, actualKey, prefixSpaces, yaml, writer);
            } else {
                write(newObj, actualKey, prefixSpaces, yaml, writer);
            }
        }

        String danglingComments = comments.get(null);
        if (danglingComments != null) {
            writer.write(danglingComments);
        }

        writer.close();
    }

    private static void write(Object obj, String actualKey, String prefixSpaces, Yaml yaml, BufferedWriter writer) throws IOException {
        if (obj instanceof ConfigurationSerializable) {
            writer.write(prefixSpaces + actualKey + ": " + yaml.dump(((ConfigurationSerializable)obj).serialize()));
        } else if (!(obj instanceof String) && !(obj instanceof Character)) {
            if (obj instanceof List) {
                writeList((List)obj, actualKey, prefixSpaces, yaml, writer);
            } else {
                writer.write(prefixSpaces + actualKey + ": " + yaml.dump(obj));
            }
        } else {
            if (obj instanceof String s) {
                obj = s.replace("\n", "\\n");
            }

            writer.write(prefixSpaces + actualKey + ": " + yaml.dump(obj));
        }

    }

    private static void writeSection(BufferedWriter writer, String actualKey, String prefixSpaces, ConfigurationSection section) throws IOException {
        if (section.getKeys(false).isEmpty()) {
            writer.write(prefixSpaces + actualKey + ": {}");
        } else {
            writer.write(prefixSpaces + actualKey + ":");
        }

        writer.write("\n");
    }

    private static void writeList(List list, String actualKey, String prefixSpaces, Yaml yaml, BufferedWriter writer) throws IOException {
        writer.write(getListAsString(list, actualKey, prefixSpaces, yaml));
    }

    private static String getListAsString(List list, String actualKey, String prefixSpaces, Yaml yaml) {
        StringBuilder builder = (new StringBuilder(prefixSpaces)).append(actualKey).append(":");
        if (list.isEmpty()) {
            builder.append(" []\n");
            return builder.toString();
        } else {
            builder.append("\n");

            for(int i = 0; i < list.size(); ++i) {
                Object o = list.get(i);
                if (!(o instanceof String) && !(o instanceof Character)) {
                    if (o instanceof List) {
                        builder.append(prefixSpaces).append("- ").append(yaml.dump(o));
                    } else {
                        builder.append(prefixSpaces).append("- ").append(o);
                    }
                } else {
                    builder.append(prefixSpaces).append("- '").append(o).append("'");
                }

                if (i != list.size()) {
                    builder.append("\n");
                }
            }

            return builder.toString();
        }
    }

    private static Map<String, String> parseComments(List<String> lines, List<String> ignoredSections, FileConfiguration oldConfig, Yaml yaml) {
        Map<String, String> comments = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        StringBuilder keyBuilder = new StringBuilder();
        int lastLineIndentCount = 0;

        label51:
        for(String line : lines) {
            if (line == null || !line.trim().startsWith("-")) {
                if (line != null && !line.trim().isEmpty() && !line.trim().startsWith("#")) {
                    lastLineIndentCount = setFullKey(keyBuilder, line, lastLineIndentCount);

                    for(String ignoredSection : ignoredSections) {
                        if (keyBuilder.toString().equals(ignoredSection)) {
                            Object value = oldConfig.get(keyBuilder.toString());
                            if (value instanceof ConfigurationSection) {
                                appendSection(builder, (ConfigurationSection)value, new StringBuilder(getPrefixSpaces(lastLineIndentCount)), yaml);
                            }
                            continue label51;
                        }
                    }

                    if (!keyBuilder.isEmpty()) {
                        comments.put(keyBuilder.toString(), builder.toString());
                        builder.setLength(0);
                    }
                } else {
                    builder.append(line).append("\n");
                }
            }
        }

        if (!builder.isEmpty()) {
            comments.put(null, builder.toString());
        }

        return comments;
    }

    private static void appendSection(StringBuilder builder, ConfigurationSection section, StringBuilder prefixSpaces, Yaml yaml) {
        builder.append(prefixSpaces).append(getKeyFromFullKey(Objects.requireNonNull(section.getCurrentPath()))).append(":");
        Set<String> keys = section.getKeys(false);
        if (keys.isEmpty()) {
            builder.append(" {}\n");
        } else {
            builder.append("\n");
            prefixSpaces.append("  ");

            for(String key : keys) {
                Object value = section.get(key);
                String actualKey = getKeyFromFullKey(key);
                if (value instanceof ConfigurationSection) {
                    appendSection(builder, (ConfigurationSection)value, prefixSpaces, yaml);
                    prefixSpaces.setLength(prefixSpaces.length() - 2);
                } else if (value instanceof List) {
                    builder.append(getListAsString((List)value, actualKey, prefixSpaces.toString(), yaml));
                } else {
                    builder.append(prefixSpaces).append(actualKey).append(": ").append(yaml.dump(value));
                }
            }
        }

    }

    private static int countIndents(String s) {
        int spaces = 0;

        for(char c : s.toCharArray()) {
            if (c != ' ') {
                break;
            }

            ++spaces;
        }

        return spaces / 2;
    }

    private static void removeLastKey(StringBuilder keyBuilder) {
        String temp = keyBuilder.toString();
        String[] keys = temp.split("\\.");
        if (keys.length == 1) {
            keyBuilder.setLength(0);
        } else {
            temp = temp.substring(0, temp.length() - keys[keys.length - 1].length() - 1);
            keyBuilder.setLength(temp.length());
        }

    }

    private static String getKeyFromFullKey(String fullKey) {
        String[] keys = fullKey.split("\\.");
        return keys[keys.length - 1];
    }

    private static int setFullKey(StringBuilder keyBuilder, String configLine, int lastLineIndentCount) {
        int currentIndents = countIndents(configLine);
        String key = configLine.trim().split(":")[0];
        if (keyBuilder.isEmpty()) {
            keyBuilder.append(key);
        } else if (currentIndents == lastLineIndentCount) {
            removeLastKey(keyBuilder);
            if (!keyBuilder.isEmpty()) {
                keyBuilder.append(".");
            }

            keyBuilder.append(key);
        } else if (currentIndents > lastLineIndentCount) {
            keyBuilder.append(".").append(key);
        } else {
            int difference = lastLineIndentCount - currentIndents;

            for(int i = 0; i < difference + 1; ++i) {
                removeLastKey(keyBuilder);
            }

            if (!keyBuilder.isEmpty()) {
                keyBuilder.append(".");
            }

            keyBuilder.append(key);
        }

        return currentIndents;
    }

    private static String getPrefixSpaces(int indents) {

        return "  ".repeat(Math.max(0, indents));
    }

    private static void appendPrefixSpaces(StringBuilder builder, int indents) {
        builder.append(getPrefixSpaces(indents));
    }
}
