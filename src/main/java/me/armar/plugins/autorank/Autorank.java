package me.armar.plugins.autorank;

import me.armar.plugins.autorank.addons.AddOnManager;
import me.armar.plugins.autorank.api.API;
import me.armar.plugins.autorank.backup.BackupManager;
import me.armar.plugins.autorank.commands.manager.CommandsManager;
import me.armar.plugins.autorank.config.DefaultBehaviorConfig;
import me.armar.plugins.autorank.config.InternalPropertiesConfig;
import me.armar.plugins.autorank.config.PathsConfig;
import me.armar.plugins.autorank.config.SettingsConfig;
import me.armar.plugins.autorank.converter.DataConverter;
import me.armar.plugins.autorank.debugger.Debugger;
import me.armar.plugins.autorank.hooks.DependencyManager;
import me.armar.plugins.autorank.language.LanguageHandler;
import me.armar.plugins.autorank.leaderboard.LeaderboardHandler;
import me.armar.plugins.autorank.listeners.PlayerJoinListener;
import me.armar.plugins.autorank.listeners.PlayerQuitListener;
import me.armar.plugins.autorank.logger.LoggerManager;
import me.armar.plugins.autorank.migration.MigrationManager;
import me.armar.plugins.autorank.pathbuilder.PathManager;
import me.armar.plugins.autorank.pathbuilder.builders.RequirementBuilder;
import me.armar.plugins.autorank.pathbuilder.builders.ResultBuilder;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataManager;
import me.armar.plugins.autorank.pathbuilder.playerdata.global.GlobalPlayerDataStorage;
import me.armar.plugins.autorank.pathbuilder.playerdata.local.LocalPlayerDataStorage;
import me.armar.plugins.autorank.pathbuilder.requirement.*;
import me.armar.plugins.autorank.pathbuilder.result.*;
import me.armar.plugins.autorank.permissions.PermissionsPluginManager;
import me.armar.plugins.autorank.placeholders.AutorankPlaceholder;
import me.armar.plugins.autorank.playerchecker.PlayerChecker;
import me.armar.plugins.autorank.playtimes.PlayTimeManager;
import me.armar.plugins.autorank.statsmanager.StatisticsManager;
import me.armar.plugins.autorank.storage.PlayTimeStorageManager;
import me.armar.plugins.autorank.storage.flatfile.FlatFileStorageProvider;
import me.armar.plugins.autorank.storage.mysql.MySQLStorageProvider;
import me.armar.plugins.autorank.tasks.TaskManager;
import me.armar.plugins.autorank.updater.UpdateHandler;
import me.armar.plugins.autorank.util.uuid.UUIDStorage;
import me.armar.plugins.autorank.validations.ValidateHandler;
import me.armar.plugins.autorank.warningmanager.WarningManager;
import me.armar.plugins.utils.pluginlibrary.Library;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static me.armar.plugins.utils.pluginlibrary.hooks.LibraryHook.isPluginAvailable;

public class Autorank extends JavaPlugin {
    private BukkitAudiences adventure;
    public @NonNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }
    private static Autorank autorank;
    private PathManager pathManager;
    private AddOnManager addonManager;
    private BackupManager backupManager;
    private CommandsManager commandsManager;
    private DependencyManager dependencyManager;
    private LeaderboardHandler leaderboardManager;
    private API api;
    private LanguageHandler languageHandler;
    private PermissionsPluginManager permPlugHandler;
    private UpdateHandler updateHandler;
    private PlayerChecker playerChecker;
    private PlayTimeManager playTimeManager;
    private DataConverter dataConverter;
    private MigrationManager migrationManager;
    private StatisticsManager statisticsManager;
    private UUIDStorage uuidStorage;
    private PlayTimeStorageManager playTimeStorageManager;
    private TaskManager taskManager;
    private ValidateHandler validateHandler;
    private WarningManager warningManager;
    private Debugger debugger;
    private SettingsConfig settingsConfig;
    private InternalPropertiesConfig internalPropertiesConfig;
    private PathsConfig pathsConfig;
    private DefaultBehaviorConfig defaultBehaviorConfig;
    private PlayerDataManager playerDataManager;
    private LoggerManager loggerManager;

    public Autorank() {
    }

    public static Autorank getInstance() {
        return autorank;
    }



    public void onDisable() {
        this.debugMessage("Shutting down all pending tasks.");
        this.getServer().getScheduler().cancelTasks(this);
        this.debugMessage("Saving storage files of play time");
        this.getPlayTimeStorageManager().saveAllStorageProviders();
        this.debugMessage("Saving storage files of UUIDs");
        if (this.getUUIDStorage() != null) {
            this.getUUIDStorage().saveAllFiles();
        }
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        this.getLogger().info(String.format("Autorank %s has been disabled!", this.getDescription().getVersion()));
        this.getLoggerManager().logMessage("Stopped Autorank");
    }

    public void onEnable() {
        autorank = this;
        getLogger().info("mainclass getHumanPluginName = " + this.getServer().getPluginManager().getPlugin(Library.AURELIUM_SKILLS.getHumanPluginName()));
        getLogger().info("mainclass getInternalPluginName = " + this.getServer().getPluginManager().getPlugin(Library.AURELIUM_SKILLS.getInternalPluginName()));
        getLogger().info("mainclass isPluginAvailable = " + isPluginAvailable(Library.AURELIUM_SKILLS));
        this.setLoggerManager(new LoggerManager(this));
        this.setWarningManager(new WarningManager(this));
        this.setPathsConfig(new PathsConfig(this));
        this.setSettingsConfig(new SettingsConfig(this));
        this.setInternalPropertiesConfig(new InternalPropertiesConfig(this));
        this.setDefaultBehaviorConfig(new DefaultBehaviorConfig(this));
        this.setPlayerDataManager(new PlayerDataManager(this));
        this.getDefaultBehaviorConfig().loadConfig();
        if (!this.getPathsConfig().loadConfig()) {
            this.getWarningManager().registerWarning("Paths.yml file could not be loaded! Please check your syntax.", 10);
        }

        if (!this.getSettingsConfig().loadConfig()) {
            this.getWarningManager().registerWarning("Settings.yml file could not be loaded! Please check your syntax.", 10);
        }

        if (this.getSettingsConfig().updateConfigWithNewOptions()) {
            this.getLogger().info("The settings.yml is up-to-date");
        } else {
            this.getLogger().info("Your settings.yml file might be outdated. Check the wiki if all the options are present.");
        }

        if (!autorank.getSettingsConfig().isLoggingEnabled()) {
            autorank.getLogger().info("Logging is not enabled.");
        }

        this.getInternalPropertiesConfig().loadConfig();
        this.getPlayerDataManager().addDataStorage(new LocalPlayerDataStorage(this));
        this.setBackupManager(new BackupManager(this));
        this.setPlayTimeStorageManager(new PlayTimeStorageManager(this));
        this.setDependencyManager(new DependencyManager(this));
        // Initialize an audiences instance for the plugin
        //this.adventure = BukkitAudiences.create(autorank);
        this.setCommandsManager(new CommandsManager(this));
        this.setAddonManager(new AddOnManager(this));
        this.setPathManager(new PathManager(this));
        this.setTaskManager(new TaskManager(this));
        this.setStatisticsManager(new StatisticsManager(this));
        this.setUpdateHandler(new UpdateHandler(this));
        this.setLanguageHandler(new LanguageHandler(this));
        this.setPermPlugHandler(new PermissionsPluginManager(this));
        this.setValidateHandler(new ValidateHandler(this));
        this.setLeaderboardManager(new LeaderboardHandler(this));
        this.setUUIDStorage(new UUIDStorage(this));
        this.setPlayTimeManager(new PlayTimeManager(this));
        this.setPlayerChecker(new PlayerChecker(this));
        this.setDebugger(new Debugger(this));
        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            Autorank.this.debugMessage("Loading UUID storage");
            Autorank.this.getUUIDStorage().loadStorageFiles();
        });
        this.setDataConverter(new DataConverter(this));
        this.languageHandler.createNewFile();
        FlatFileStorageProvider flatFileStorageProvider = new FlatFileStorageProvider(this);
        CompletableFuture<Void> loadFlatFileTask = flatFileStorageProvider.initialiseProvider().thenAccept((loaded) -> {
            if (!loaded) {
                this.debugMessage("Could not load flatfile storage.");
                this.getWarningManager().registerWarning("Could not initialise flatfile storage!", 10);
            } else {
                this.debugMessage("Successfully loaded flatfile storage.");
                this.getPlayTimeStorageManager().registerStorageProvider(flatFileStorageProvider);
            }
        });
        CompletableFuture<Void> loadMySQLTask = CompletableFuture.runAsync(() -> {
        });
        if (this.getSettingsConfig().useMySQL()) {
            MySQLStorageProvider mysqlStorageProvider = new MySQLStorageProvider(this);
            this.getPlayerDataManager().addDataStorage(new GlobalPlayerDataStorage(this));
            loadMySQLTask = mysqlStorageProvider.initialiseProvider().thenAccept((loaded) -> {
                if (loaded) {
                    this.debugMessage("Successfully loaded MySQL storage.");
                    this.getPlayTimeStorageManager().registerStorageProvider(mysqlStorageProvider);
                    if (this.getSettingsConfig().getPrimaryStorageProvider().equalsIgnoreCase("mysql")) {
                        this.getPlayTimeStorageManager().setPrimaryStorageProvider(mysqlStorageProvider);
                    }

                    this.debugMessage("Checking for old data in exotic databases...");
                    mysqlStorageProvider.updateFromOldTables();
                } else {
                    this.debugMessage("Could not load MySQL storage.");
                    this.getWarningManager().registerWarning("The MySQL storage provider could not be started. Check for errors!", 10);
                }

            });
        }

        CompletableFuture<Void> finalLoadMySQLTask = loadMySQLTask;//added this line to fix error below
        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                loadFlatFileTask.thenCompose((v) -> finalLoadMySQLTask.thenRun(() -> {
                    this.getLogger().info("Primary storage provider of Autorank: " + this.getPlayTimeStorageManager().getPrimaryStorageProvider().getName());
                })).get();
            } catch (ExecutionException | InterruptedException var4) {
                var4.printStackTrace();
            }

        });
        this.initializeReqsAndRes();
        this.getWarningManager().startWarningTask();
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        this.getServer().getScheduler().runTaskLater(this, () -> {
            try {
                this.dependencyManager.loadDependencies();
            } catch (Throwable var4) {
                this.getLogger().severe("Could not hook into a dependency: \nCause: ");
                var4.printStackTrace();
            }

            this.getPathManager().initialiseFromConfigs();
            if (!this.getValidateHandler().startValidation()) {
                this.getServer().getConsoleSender().sendMessage("[Autorank] " + ChatColor.RED + "Detected errors in your Autorank configuration. Log in to your server to see the problems!");
            }

            HashMap<String, Integer> warnings = this.getWarningManager().getWarnings();
            if (warnings.size() > 0) {
                this.getLogger().warning("Autorank has some warnings for you: ");
            }

            Iterator var2 = warnings.entrySet().iterator();

            while(var2.hasNext()) {
                Entry<String, Integer> entry = (Entry)var2.next();
                this.getLogger().warning("(Priority " + entry.getValue() + ") '" + entry.getKey() + "'");
            }

        }, 1L);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            this.debugMessage("Periodically run task to remove purge old entries");
            if (!this.getSettingsConfig().shouldRemoveOldEntries()) {
                this.debugMessage("Purging entries is forbidden by configuration, so aborting.");
            } else if (!this.getInternalPropertiesConfig().isConvertedToNewFormat()) {
                this.debugMessage("Autorank isn't using newest formatting of UUIDs yet, so aborting.");
            } else {
                int removed = this.getPlayTimeStorageManager().getPrimaryStorageProvider().purgeOldEntries();
                this.getLogger().info("Removed " + removed + " old storage entries from database!");
            }
        }, 0L, 1728000L);
        this.getCommand("autorank").setExecutor(this.getCommandsManager());
        this.setMigrationManager(new MigrationManager(this));
        this.debugMessage("Autorank debug is turned on!");
        if (this.isDevVersion()) {
            this.getLogger().warning("You're running a DEV version, be sure to backup your Autorank folder!");
            this.getLogger().warning("DEV versions are not guaranteed to be stable and generally shouldn't be used on big production servers with lots of players.");
        }

        this.getBackupManager().startBackupSystem();
        this.getLeaderboardManager().updateAllLeaderboards();
        this.getUUIDStorage().transferUUIDs();
        this.api = new API(this);
        this.getLogger().info(String.format("Autorank %s has been enabled!", this.getDescription().getVersion()));
        this.getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
            public void run() {
                Autorank.this.debugMessage("Trying to convert data to new format (if needed)");
                if (!Autorank.this.getInternalPropertiesConfig().isConvertedToNewFormat()) {
                    Autorank.this.getDataConverter().convertData();
                }

            }
        }, 100L);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            this.debugMessage("Checking if new a day has arrived so the playerdata should be reset.");
            this.getPlayTimeStorageManager().doCalendarCheck();
        }, 1200L, 12000L);
        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            (new AutorankPlaceholder(this)).register();
            this.getLoggerManager().logMessage("Exposed Autorank placeholder extension.");
            this.getLogger().info("Registered placeholders, so you can use them!");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("AdvancedAchievements")) {
            Plugin pluginInstance = Bukkit.getPluginManager().getPlugin("AdvancedAchievements");
        }

        this.getLoggerManager().logMessage("Started Autorank");
    }

    private void initializeReqsAndRes() {
        RequirementBuilder.registerRequirement("active paths", AutorankActivePathsRequirement.class);
        RequirementBuilder.registerRequirement("animals bred", AnimalsBredRequirement.class);
        RequirementBuilder.registerRequirement("aurelium skills mana", AureliumSkillsManaRequirement.class);
        RequirementBuilder.registerRequirement("aurelium skills skill level", AureliumSkillsSkillRequirement.class);
        RequirementBuilder.registerRequirement("aurelium skills stat level", AureliumSkillsStatRequirement.class);
        RequirementBuilder.registerRequirement("aurelium skills xp", AureliumSkillsXPRequirement.class);
        RequirementBuilder.registerRequirement("bentobox level", BentoBoxLevelRequirement.class);
        RequirementBuilder.registerRequirement("blocks broken", BlocksBrokenRequirement.class);
        RequirementBuilder.registerRequirement("blocks moved", BlocksMovedRequirement.class);
        RequirementBuilder.registerRequirement("blocks placed", BlocksPlacedRequirement.class);
        RequirementBuilder.registerRequirement("cake slices eaten", CakeSlicesEatenRequirement.class);
        RequirementBuilder.registerRequirement("completed paths", AutorankCompletedPathsRequirement.class);
        RequirementBuilder.registerRequirement("daily time", TimeDailyRequirement.class);
        RequirementBuilder.registerRequirement("damage taken", DamageTakenRequirement.class);
        RequirementBuilder.registerRequirement("essentials geoip location", EssentialsGeoIPRequirement.class);
        RequirementBuilder.registerRequirement("exp", ExpRequirement.class);
        RequirementBuilder.registerRequirement("faction power", FactionPowerRequirement.class);
        RequirementBuilder.registerRequirement("factionX faction power", FactionsXPowerRequirement.class);
        RequirementBuilder.registerRequirement("fish caught", FishCaughtRequirement.class);
        RequirementBuilder.registerRequirement("food eaten", FoodEatenRequirement.class);
        RequirementBuilder.registerRequirement("gamemode", GamemodeRequirement.class);
        RequirementBuilder.registerRequirement("global time", GlobalTimeRequirement.class);
        RequirementBuilder.registerRequirement("grief prevention bonus blocks", GriefPreventionBonusBlocksRequirement.class);
        RequirementBuilder.registerRequirement("grief prevention claimed blocks", GriefPreventionClaimedBlocksRequirement.class);
        RequirementBuilder.registerRequirement("grief prevention claims", GriefPreventionClaimsCountRequirement.class);
        RequirementBuilder.registerRequirement("grief prevention remaining blocks", GriefPreventionRemainingBlocksRequirement.class);
        RequirementBuilder.registerRequirement("has advancement", AdvancementRequirement.class);
        RequirementBuilder.registerRequirement("has item", HasItemRequirement.class);
        RequirementBuilder.registerRequirement("in biome", InBiomeRequirement.class);
        RequirementBuilder.registerRequirement("in group", InGroupRequirement.class);
        RequirementBuilder.registerRequirement("item thrown", ItemThrownRequirement.class);
        RequirementBuilder.registerRequirement("items crafted", ItemsCraftedRequirement.class);
        RequirementBuilder.registerRequirement("items enchanted", ItemsEnchantedRequirement.class);
        RequirementBuilder.registerRequirement("javascript", JavaScriptRequirement.class);
        RequirementBuilder.registerRequirement("jobs current points", JobsCurrentPointsRequirement.class);
        RequirementBuilder.registerRequirement("jobs experience", JobsExperienceRequirement.class);
        RequirementBuilder.registerRequirement("jobs level", JobsLevelRequirement.class);
        RequirementBuilder.registerRequirement("jobs total points", JobsTotalPointsRequirement.class);
        RequirementBuilder.registerRequirement("last login", LastLoginLoginRequirement.class);
        RequirementBuilder.registerRequirement("last logout", LastLoginLogoutRequirement.class);
        RequirementBuilder.registerRequirement("location", LocationRequirement.class);
        RequirementBuilder.registerRequirement("mcmmo power level", McMMOPowerLevelRequirement.class);
        RequirementBuilder.registerRequirement("mcmmo skill level", McMMOSkillLevelRequirement.class);
        RequirementBuilder.registerRequirement("mcrpg power level", McRPGPowerLevelRequirement.class);
        RequirementBuilder.registerRequirement("mcrpg skill level", McRPGSkillLevelRequirement.class);
        RequirementBuilder.registerRequirement("mobs killed", MobKillsRequirement.class);
        RequirementBuilder.registerRequirement("money", MoneyRequirement.class);
        RequirementBuilder.registerRequirement("monthly time", TimeMonthlyRequirement.class);
        RequirementBuilder.registerRequirement("not in group", NotInGroupRequirement.class);
        RequirementBuilder.registerRequirement("permission", PermissionRequirement.class);
        RequirementBuilder.registerRequirement("placeholder integer", PlaceholderapiIntegerRequirement.class);
        RequirementBuilder.registerRequirement("placeholder string", PlaceholderapiStringRequirement.class);
        RequirementBuilder.registerRequirement("plants potted", PlantsPottedRequirement.class);
        RequirementBuilder.registerRequirement("playerpoints points", PlayerPointsPointsRequirement.class);
        RequirementBuilder.registerRequirement("players killed", PlayerKillsRequirement.class);
        RequirementBuilder.registerRequirement("quests active quests", QuestsActiveQuestsRequirement.class);
        RequirementBuilder.registerRequirement("quests complete quest", QuestsCompleteSpecificQuestRequirement.class);
        RequirementBuilder.registerRequirement("quests completed quests", QuestsCompletedQuestsRequirement.class);
        RequirementBuilder.registerRequirement("quests quest points", QuestsQuestPointsRequirement.class);
        RequirementBuilder.registerRequirement("time", TimeRequirement.class);
        RequirementBuilder.registerRequirement("times died", TimesDiedRequirement.class);
        RequirementBuilder.registerRequirement("times sheared", TimesShearedRequirement.class);
        RequirementBuilder.registerRequirement("total time", TotalTimeRequirement.class);
        RequirementBuilder.registerRequirement("towny has nation", TownyHasANationRequirement.class);
        RequirementBuilder.registerRequirement("towny has town", TownyHasATownRequirement.class);
        RequirementBuilder.registerRequirement("towny is king", TownyIsKingRequirement.class);
        RequirementBuilder.registerRequirement("towny is mayor", TownyIsMayorRequirement.class);
        RequirementBuilder.registerRequirement("towny town blocks", TownyNumberOfTownBlocksRequirement.class);
        RequirementBuilder.registerRequirement("traded with villagers", TradedWithVillagersRequirement.class);
        RequirementBuilder.registerRequirement("votes", TotalVotesRequirement.class);
        RequirementBuilder.registerRequirement("weekly time", TimeWeeklyRequirement.class);
        RequirementBuilder.registerRequirement("world", WorldRequirement.class);
        RequirementBuilder.registerRequirement("worldguard region", WorldGuardRegionRequirement.class);
        ResultBuilder.registerResult("command", CommandResult.class);
        ResultBuilder.registerResult("effect", EffectResult.class);
        ResultBuilder.registerResult("firework", SpawnFireworkResult.class);
        ResultBuilder.registerResult("message", MessageResult.class);
        ResultBuilder.registerResult("money", MoneyResult.class);
        ResultBuilder.registerResult("tp", TeleportResult.class);
    }

    public void debugMessage(String message) {
        if (this.getSettingsConfig().getConfig() != null) {
            if (this.getSettingsConfig().useDebugOutput() || Debugger.debuggerEnabled) {
                this.getServer().getConsoleSender().sendMessage("[Autorank DEBUG] " + ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    public boolean isDevVersion() {
        return this.getDescription().getVersion().toLowerCase().contains("dev") || this.getDescription().getVersion().toLowerCase().contains("project") || this.getDescription().getVersion().toLowerCase().contains("snapshot");
    }

    public void registerRequirement(String name, Class<? extends AbstractRequirement> requirement) {
        RequirementBuilder.registerRequirement(name, requirement);
    }

    public void registerResult(String name, Class<? extends AbstractResult> result) {
        ResultBuilder.registerResult(name, result);
    }

    public void reload() {
        this.getServer().getPluginManager().disablePlugin(this);
        this.getServer().getPluginManager().enablePlugin(this);
    }

    public LanguageHandler getLanguageHandler() {
        return this.languageHandler;
    }

    private void setLanguageHandler(LanguageHandler lHandler) {
        this.languageHandler = lHandler;
    }

    public PermissionsPluginManager getPermPlugHandler() {
        return this.permPlugHandler;
    }

    public void setPermPlugHandler(PermissionsPluginManager permPlugHandler) {
        this.permPlugHandler = permPlugHandler;
    }

    public PlayerChecker getPlayerChecker() {
        return this.playerChecker;
    }

    private void setPlayerChecker(PlayerChecker playerChecker) {
        this.playerChecker = playerChecker;
    }

    public PlayTimeManager getPlayTimeManager() {
        return this.playTimeManager;
    }

    private void setPlayTimeManager(PlayTimeManager playTimeManager) {
        this.playTimeManager = playTimeManager;
    }

    public UpdateHandler getUpdateHandler() {
        return this.updateHandler;
    }

    public void setUpdateHandler(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    public UUIDStorage getUUIDStorage() {
        return this.uuidStorage;
    }

    public void setUUIDStorage(UUIDStorage uuidStorage) {
        this.uuidStorage = uuidStorage;
    }

    public ValidateHandler getValidateHandler() {
        return this.validateHandler;
    }

    public void setValidateHandler(ValidateHandler validateHandler) {
        this.validateHandler = validateHandler;
    }

    public WarningManager getWarningManager() {
        return this.warningManager;
    }

    public void setWarningManager(WarningManager warningManager) {
        this.warningManager = warningManager;
    }

    public AddOnManager getAddonManager() {
        return this.addonManager;
    }

    public void setAddonManager(AddOnManager addonManager) {
        this.addonManager = addonManager;
    }

    public API getAPI() {
        return this.api;
    }

    public BackupManager getBackupManager() {
        return this.backupManager;
    }

    public void setBackupManager(BackupManager backupManager) {
        this.backupManager = backupManager;
    }

    public CommandsManager getCommandsManager() {
        return this.commandsManager;
    }

    public void setCommandsManager(CommandsManager commandsManager) {
        this.commandsManager = commandsManager;
    }

    public Debugger getDebugger() {
        return this.debugger;
    }

    public void setDebugger(Debugger debugger) {
        this.debugger = debugger;
    }

    public DependencyManager getDependencyManager() {
        return this.dependencyManager;
    }

    public void setDependencyManager(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }

    public InternalPropertiesConfig getInternalPropertiesConfig() {
        return this.internalPropertiesConfig;
    }

    public void setInternalPropertiesConfig(InternalPropertiesConfig internalPropertiesConfig) {
        this.internalPropertiesConfig = internalPropertiesConfig;
    }

    public SettingsConfig getSettingsConfig() {
        return this.settingsConfig;
    }

    public void setSettingsConfig(SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;
    }

    public PathsConfig getPathsConfig() {
        return this.pathsConfig;
    }

    public void setPathsConfig(PathsConfig pathsConfig) {
        this.pathsConfig = pathsConfig;
    }

    public PathManager getPathManager() {
        return this.pathManager;
    }

    public void setPathManager(PathManager pathManager) {
        this.pathManager = pathManager;
    }

    public LeaderboardHandler getLeaderboardManager() {
        return this.leaderboardManager;
    }

    public void setLeaderboardManager(LeaderboardHandler leaderboardManager) {
        this.leaderboardManager = leaderboardManager;
    }

    public DataConverter getDataConverter() {
        return this.dataConverter;
    }

    public void setDataConverter(DataConverter dataConverter) {
        this.dataConverter = dataConverter;
    }

    public DefaultBehaviorConfig getDefaultBehaviorConfig() {
        return this.defaultBehaviorConfig;
    }

    public void setDefaultBehaviorConfig(DefaultBehaviorConfig defaultBehaviorConfig) {
        this.defaultBehaviorConfig = defaultBehaviorConfig;
    }

    public PlayTimeStorageManager getPlayTimeStorageManager() {
        return this.playTimeStorageManager;
    }

    public void setPlayTimeStorageManager(PlayTimeStorageManager playTimeStorageManager) {
        this.playTimeStorageManager = playTimeStorageManager;
    }

    public TaskManager getTaskManager() {
        return this.taskManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }

    public void setPlayerDataManager(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public MigrationManager getMigrationManager() {
        return this.migrationManager;
    }

    public void setMigrationManager(MigrationManager migrationManager) {
        this.migrationManager = migrationManager;
    }

    public LoggerManager getLoggerManager() {
        return this.loggerManager;
    }

    public void setLoggerManager(LoggerManager loggerManager) {
        this.loggerManager = loggerManager;
    }

    public StatisticsManager getStatisticsManager() {
        return this.statisticsManager;
    }

    public void setStatisticsManager(StatisticsManager statisticsManager) {
        this.statisticsManager = statisticsManager;
    }
}
