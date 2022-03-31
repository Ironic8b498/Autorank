package me.armar.plugins.autorank.converter;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SimpleYamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

public class DataConverter {
    private final Autorank plugin;

    public DataConverter(Autorank instance) {
        this.plugin = instance;
    }

    public boolean convertData() {
        if (this.plugin.getInternalPropertiesConfig().isConvertedToNewFormat()) {
            return false;
        } else {
            this.plugin.getLogger().info("Autorank detected that you upgraded from an older version. It will need to convert your folders.");
            this.plugin.getLogger().info("Started converting folders of Autorank...");
            String folderPath = this.plugin.getDataFolder().getAbsolutePath() + File.separator;
            if (!(new File(folderPath + "/storage/daily_time.yml")).renameTo(new File(folderPath + "/storage/Daily_time.yml"))) {
                this.plugin.getLogger().info("Could not rename daily_time.yml to Daily_time.yml!");
            } else {
                this.plugin.getLogger().info("Successfully converted Daily_time.yml!");
            }

            if (!(new File(folderPath + "/storage/weekly_time.yml")).renameTo(new File(folderPath + "/storage/Weekly_time.yml"))) {
                this.plugin.getLogger().info("Could not rename weekly_time.yml to Weekly_time.yml!");
            } else {
                this.plugin.getLogger().info("Successfully converted Weekly_time.yml!");
            }

            if (!(new File(folderPath + "/storage/monthly_time.yml")).renameTo(new File(folderPath + "/storage/Monthly_time.yml"))) {
                this.plugin.getLogger().info("Could not rename monthly_time.yml to Monthly_time.yml!");
            } else {
                this.plugin.getLogger().info("Successfully converted Monthly_time.yml!");
            }

            File totalTimeFile = new File(folderPath + "/storage/Total_time.yml");
            if (totalTimeFile.exists()) {
                this.plugin.getLogger().info("Deleting Total_time.yml");
                totalTimeFile.delete();
            }

            if (!(new File(folderPath + "Data.yml")).renameTo(totalTimeFile)) {
                this.plugin.getLogger().info("Could not rename Data.yml to Total_time.yml!");
            } else {
                this.plugin.getLogger().info("Successfully converted Data.yml!");
            }

            if (!(new File(folderPath + "/playerdata/playerdata.yml")).renameTo(new File(folderPath + "/playerdata/PlayerData.yml"))) {
                this.plugin.getLogger().info("Could not rename playerdata.yml to PlayerData.yml!");
            } else {
                this.plugin.getLogger().info("Successfully converted playerdata.yml!");
            }

            this.plugin.getLogger().info("Conversion of Autorank is complete!");
            this.plugin.getInternalPropertiesConfig().setConvertedToNewFormat(true);
            return true;
        }
    }

    public boolean convertSimpleConfigToPaths() {
        SimpleYamlConfiguration simpleConfig = null;

        try {
            simpleConfig = new SimpleYamlConfiguration(this.plugin, "SimpleConfig.yml", "SimpleConfig");
        } catch (InvalidConfigurationException var13) {
            var13.printStackTrace();
        }

        String newPathsFileName = "Paths_from_SimpleConfig.yml";
        SimpleYamlConfiguration newPathsFile = null;

        try {
            newPathsFile = new SimpleYamlConfiguration(this.plugin, newPathsFileName, "Converted paths file");
        } catch (InvalidConfigurationException var12) {
            var12.printStackTrace();
        }

        Map<String, String> paths = new HashMap();
        Iterator var5 = simpleConfig.getKeys(false).iterator();

        String groupFrom;
        String[] tempArray;
        String groupTo;
        String timePeriod;
        while(var5.hasNext()) {
            String fromGroup = (String)var5.next();
            groupFrom = simpleConfig.getString(fromGroup);
            if (groupFrom != null && groupFrom.contains("after")) {
                tempArray = groupFrom.split("after");
                groupTo = tempArray[0].trim();
                timePeriod = tempArray[1].trim();
                paths.put(fromGroup, groupTo + ";" + timePeriod);
            }
        }

        var5 = paths.entrySet().iterator();

        while(var5.hasNext()) {
            Entry<String, String> path = (Entry)var5.next();
            groupFrom = path.getKey();
            tempArray = path.getValue().split(";");
            groupTo = tempArray[0];
            timePeriod = tempArray[1];
            String pathName = groupFrom + " to " + groupTo;
            newPathsFile.set(pathName + ".prerequisites.in group.value", groupFrom);
            newPathsFile.set(pathName + ".requirements.time.value", timePeriod);
            newPathsFile.set(pathName + ".results.rank change", groupTo);
        }

        newPathsFile.options().indent(4);
        newPathsFile.options().header("This is a Paths.yml generated from your SimpleConfig.yml. \nBeware that there can be errors made by the automatic transferring of formats.\n\nTo test this file with Autorank, perform the following steps:\n1. Stop your server (very important);\n2. Rename this file to 'Paths.yml' (without the quotation marks);\n3. Restart your server and voilá!");
        newPathsFile.saveFile();
        return true;
    }

    public boolean convertAdvancedConfigToPaths() {
        SimpleYamlConfiguration advancedConfig = null;

        try {
            advancedConfig = new SimpleYamlConfiguration(this.plugin, "AdvancedConfig.yml", "AdvancedConfig");
        } catch (InvalidConfigurationException var17) {
            var17.printStackTrace();
        }

        String newPathsFileName = "Paths_from_AdvancedConfig.yml";
        SimpleYamlConfiguration newPathsFile = null;

        try {
            newPathsFile = new SimpleYamlConfiguration(this.plugin, newPathsFileName, "Converted paths file");
        } catch (InvalidConfigurationException var16) {
            var16.printStackTrace();
        }

        List<ConvertiblePath> paths = new ArrayList();
        Iterator var5 = advancedConfig.getConfigurationSection("ranks").getKeys(false).iterator();

        while(true) {
            String fromGroup;
            String reqString;
            String resString;
            ConfigurationSection reqSection;
            ConfigurationSection resSection;
            do {
                do {
                    if (!var5.hasNext()) {
                        var5 = paths.iterator();

                        while(var5.hasNext()) {
                            ConvertiblePath path = (ConvertiblePath)var5.next();
                            reqString = path.getFromGroup();
                            resString = path.getPathName();
                            newPathsFile.set(resString + ".prerequisites.in group.value", reqString);
                            Iterator var19 = path.getRequirements().entrySet().iterator();

                            Entry entry;
                            String resName;
                            String resValue;
                            while(var19.hasNext()) {
                                entry = (Entry)var19.next();
                                resName = (String)entry.getKey();
                                resValue = (String)entry.getValue();
                                if (resValue.matches("^-?\\d+$")) {
                                    newPathsFile.set(resString + ".requirements." + resName + ".value", Integer.parseInt(resValue));
                                } else {
                                    newPathsFile.set(resString + ".requirements." + resName + ".value", resValue);
                                }
                            }

                            var19 = path.getResults().entrySet().iterator();

                            while(var19.hasNext()) {
                                entry = (Entry)var19.next();
                                resName = (String)entry.getKey();
                                resValue = (String)entry.getValue();
                                newPathsFile.set(resString + ".results." + resName, resValue);
                            }
                        }

                        newPathsFile.options().indent(4);
                        newPathsFile.options().header("This is a Paths.yml generated from your AdvancedConfig.yml. \nBeware that there can be errors made by the automatic transferring of formats.\n\nTo test this file with Autorank, perform the following steps:\n1. Stop your server (very important);\n2. Rename this file to 'Paths.yml' (without the quotation marks);\n3. Restart your server and voilá!");
                        newPathsFile.saveFile();
                        return true;
                    }

                    fromGroup = (String)var5.next();
                    reqString = "ranks." + fromGroup + ".requirements";
                    resString = "ranks." + fromGroup + ".results";
                    reqSection = advancedConfig.getConfigurationSection(reqString);
                    resSection = advancedConfig.getConfigurationSection(resString);
                } while(reqSection == null);
            } while(resSection == null);

            ConvertiblePath newPath = new ConvertiblePath();
            newPath.setFromGroup(fromGroup);
            newPath.setPathName(fromGroup);

            Iterator var12;
            String result;
            String valueString;
            Object tempObject;
            for(var12 = reqSection.getKeys(false).iterator(); var12.hasNext(); newPath.addRequirement(result, valueString)) {
                result = (String)var12.next();
                valueString = "";
                tempObject = advancedConfig.get(reqString + "." + result + ".value");
                if (tempObject != null) {
                    valueString = tempObject.toString();
                } else {
                    valueString = advancedConfig.get(reqString + "." + result).toString();
                }
            }

            for(var12 = resSection.getKeys(false).iterator(); var12.hasNext(); newPath.addResult(result, valueString)) {
                result = (String)var12.next();
                valueString = "";
                tempObject = advancedConfig.get(resString + "." + result + ".value");
                if (tempObject != null) {
                    valueString = tempObject.toString();
                } else {
                    valueString = advancedConfig.get(resString + "." + result).toString();
                }
            }

            paths.add(newPath);
        }
    }
}
