package me.armar.plugins.utils.pluginlibrary;

import me.armar.plugins.utils.pluginlibrary.hooks.*;
import org.bukkit.Bukkit;

import java.util.Optional;

public enum Library {
    ADVANCEDACHIEVEMENTS("AdvancedAchievements", AdvancedAchievementsHook.class, "DarkPyves"),
    AURA_SKILLS("AuraSkills","AuraSkills", AuraSkillsHook.class, "Archyx", "dev.aurelium.auraskills.bukkit.AuraSkills"),
    AUTORANK("Autorank", AutorankHook.class, "Staartvin"),
    BENTOBOX("BentoBox", BentoBoxHook.class, "tastybento"),
    CMI("CMI", CMIHook.class, "Zrips"),
    ESSENTIALSX("Essentials", "EssentialsX", EssentialsXHook.class, "drtshock"),
    FACTIONSX("FactionsX", FactionXHook.class, "ProSavage", "net.prosavage.factionsx.FactionsX"),
    GRIEFPREVENTION("GriefPrevention", GriefPreventionHook.class, "RoboMWM"),
    JOBS("Jobs", JobsHook.class, "Zrips"),
    LASTLOGINAPI("LastLoginAPI", LastLoginAPIHook.class, "AlessioDP"),
    MCMMO("mcMMO", McMMOHook.class, "t00thpick1"),
    MCRPG("McRPG", McRPGHook.class, "Eunoians"),
    NUVOTIFIER("Votifier", "NuVotifier", NuVotifierHook.class, "Ichbinjoe", "com.vexsoftware.votifier.NuVotifierBukkit"),
    PLACEHOLDERAPI("PlaceholderAPI", PlaceholderAPIHook.class, "HelpChat", "me.clip.placeholderapi.PlaceholderAPIPlugin"),
    PLAYERPOINTS("PlayerPoints", PlayerPointsHook.class, "Blackixx"),
    QUESTS("Quests", QuestsHook.class, "PikaMug", "me.blackvein.quests.Quests"),
    QUESTS_ALTERNATIVE("Quests", QuestsAlternative.class, "LMBishop & contributors", "com.leonardobishop.quests.bukkit.BukkitQuestsPlugin"),
    STATZ("Statz", StatzHook.class, "Staartvin"),
    TOWNY_ADVANCED("Towny", TownyAdvancedHook.class, "Shade"),
    ULTIMATECORE("UltimateCore", UltimateCoreHook.class, "Bammerbom"),
    VAULT("Vault", VaultHook.class, "Kainzo"),
    WORLDGUARD("WorldGuard", WorldGuardHook.class, "sk89q");

    private final String internalPluginName;
    private final String authorName;
    private final Class<? extends LibraryHook> libraryClass;
    private LibraryHook hook;
    private String humanPluginName;
    private String mainClass;

    Library(String pluginName, String humanPluginName, Class<? extends LibraryHook> libraryClass, String authorName) {
        this.internalPluginName = pluginName;
        this.humanPluginName = humanPluginName;
        this.libraryClass = libraryClass;
        this.authorName = authorName;
    }

    Library(String pluginName, Class<? extends LibraryHook> libraryClass, String authorName) {
        this.internalPluginName = pluginName;
        this.libraryClass = libraryClass;
        this.authorName = authorName;
    }

    Library(String pluginName, Class<? extends LibraryHook> libraryClass, String authorName, String mainClass) {
        this.internalPluginName = pluginName;
        this.libraryClass = libraryClass;
        this.authorName = authorName;
        this.mainClass = mainClass;
    }

    Library(String pluginName, String humanPluginName, Class<? extends LibraryHook> libraryClass, String authorName, String mainClass) {
        this.internalPluginName = pluginName;
        this.humanPluginName = humanPluginName;
        this.libraryClass = libraryClass;
        this.authorName = authorName;
        this.mainClass = mainClass;
    }

    public static Library getEnum(String value) throws IllegalArgumentException {
        Library[] var1 = values();

        for (Library e : var1) {
            if (e.getInternalPluginName().equalsIgnoreCase(value)) {
                return e;
            }
        }

        throw new IllegalArgumentException("There is no library called '" + value + "'!");
    }

    public String getInternalPluginName() {
        return this.internalPluginName;
    }

    public Optional<LibraryHook> getHook() {
        if (hook == null) {
            try {
                hook = libraryClass.getDeclaredConstructor().newInstance();
            } catch (Exception | NoClassDefFoundError var2) {
                Bukkit.getConsoleSender().sendMessage("Could not grab hook of " + this.getHumanPluginName());
                var2.printStackTrace();
                return Optional.empty();
            }
        }

        return Optional.of(hook);
    }

    public String getAuthor() {
        return this.authorName;
    }

    public String getHumanPluginName() {
        return this.humanPluginName == null ? this.internalPluginName : this.humanPluginName;
    }

    public String getMainClass() {
        return this.mainClass;
    }

    public boolean hasMainClass() {
        return this.mainClass != null;
    }

}
