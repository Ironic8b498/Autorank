package me.armar.plugins.autorank.language;

import org.bukkit.configuration.file.FileConfiguration;

public enum Lang {
    ABOUT_LINE("ABOUT-LINE", "<BLUE>-----------------------------------------------------"),
    ACROSS_ALL_SERVERS("ACROSS-ALL-SERVERS", " <GREEN>across all servers."),
    ACTIVE("ACTIVE", "<LIGHT_PURPLE>Active paths (<GOLD>{0}<LIGHT_PURPLE>): <GOLD>{1}"),
    ADVANCEMENT_MULTIPLE_REQUIREMENT("ADVANCEMENT-MULTIPLE-REQUIREMENT", "Obtain at least {0} advancements"),
    ADVANCEMENT_SINGLE_REQUIREMENT("ADVANCEMENT-SINGLE-REQUIREMENT", "Obtain Advancement '{0}'"),
    ALREADY_COMPLETED_PATH("ALREADY-COMPLETED-PATH", "but has already completed this path before."),
    ALREADY_COMPLETED_REQUIREMENT("ALREADY-COMPLETED-REQUIREMENT", "<RED>You have already completed this requirement!"),
    ALREADY_ON_THIS_PATH("ALREADY-ON-THIS-PATH", "<RED>You're already on this path!"),
    AR("AR", "<GREEN>/ar "),
    AND("AND", "and"),
    AND_YOUR_PROGRESS("AND-YOUR-PROGRESS", "<RED>and your progress for this path has been reset."),
    ANIMALS_BRED_REQUIREMENT("ANIMALS-BRED-REQUIREMENT", "Breed at least {0} animals"),
    ASSIGNED("ASSIGNED", "<GREEN>Assigned '<GOLD>{1}<GREEN>' to {0}"),
    AURELIUM_SKILLS_MANA_REQUIREMENT("AURELIUM-SKILLS-MANA-REQUIREMENT", "Have at least {0} mana."),
    AURELIUM_SKILLS_SKILL_REQUIREMENT("AURELIUM-SKILLS-SKILL-REQUIREMENT", "Reach level {0} in {1}."),
    AURELIUM_SKILLS_STAT_REQUIREMENT("AURELIUM-SKILLS-STAT-REQUIREMENT", "Reach level {0} in {1}."),
    AURELIUM_SKILLS_XP_REQUIREMENT("AURELIUM-SKILLS-XP-REQUIREMENT", "Have {0} xp in {1}."),
    AUTOMATICALLY_ASSIGNED_PATH("AUTOMATICALLY-ASSIGNED-PATH", "<DARK_GREEN>You have automatically been assigned the path '<GOLD>{0}<DARK_GREEN>'."),
    AUTORANK_NUMBER_OF_ACTIVE_PATHS_REQUIREMENT("AUTORANK-NUMBER-OF-ACTIVE-PATHS-REQUIREMENT", "Have at least {0} active paths."),
    AUTORANK_NUMBER_OF_COMPLETED_PATHS_REQUIREMENT("AUTORANK-NUMBER-OF-COMPLETED-PATHS-REQUIREMENT", "Have at least completed {0} paths."),
    AUTORANK_RELOADED("AUTORANK-RELOADED", "<DARK_AQUA>Autorank's files have been reloaded."),
    AUTORANK_SPECIFIC_ACTIVE_PATH_REQUIREMENT("AUTORANK-SPECIFIC-ACTIVE-PATH-REQUIREMENT", "Have '{0}' set as an active path."),
    AUTORANK_SPECIFIC_COMPLETED_PATH_REQUIREMENT("AUTORANK-SPECIFIC-COMPLETED-PATH-REQUIREMENT", "Have completed path '{0}'."),
    AUTORANK_TIMES_HEADER("AUTORANK-TIMES-HEADER", "<GREEN>----------- [Times of <GOLD>{0}<GREEN>] -----------"),
    AUTORANK_TIMES_PLAYER_PLAYED("AUTORANK-TIMES-PLAYER-PLAYED", "<GOLD>{0}<BLUE> played:"),
    AUTORANK_TIMES_THIS_MONTH("AUTORANK-TIMES-THIS-MONTH", "<YELLOW>This month: <LIGHT_PURPLE>{0}"),
    AUTORANK_TIMES_THIS_WEEK("AUTORANK-TIMES-THIS-WEEK", "<RED>This week: <LIGHT_PURPLE>{0}"),
    AUTORANK_TIMES_TODAY("AUTORANK-TIMES-TODAY", "<DARK_AQUA>Today: <LIGHT_PURPLE>{0}"),
    AUTORANK_TIMES_TOTAL("AUTORANK-TIMES-TOTAL", "<GREEN>Total: <LIGHT_PURPLE>{0}"),
    BENTOBOX_LEVEL_REQUIREMENT("BENTOBOX-LEVEL-REQUIREMENT", "Have at least an island level of {0} on {1}."),
    BLOCKS("BLOCKS", " blocks"),
    BLOCKS_MOVED_REQUIREMENT("BLOCKS-MOVED-REQUIREMENT", "Travel at least {0} {1}"),
    BRACKET_LEFT("BRACKET-LEFT", "<GRAY>["),
    BRACKET_RIGHT("BRACKET-RIGHT", "<GRAY>]"),
    BROKEN_BLOCKS_REQUIREMENT("BROKEN-BLOCKS-REQUIREMENT", "Break at least {0}"),
    BUT_YOUR_PROGRESS("BUT-YOUR-PROGRESS", "<GOLD>but your progress for this path has been stored."),
    CAKESLICES_EATEN_REQUIREMENT("CAKESLICES-EATEN-REQUIREMENT", "Eat at least {0} slices of cake"),
    CANNOT_CHECK_CONSOLE("CANNOT-CHECK-CONSOLE", "<RED>Cannot check for console!"),
    CHECKED("CHECKED", " <GREEN>checked!"),
    CHOSEN_PATH("CHOSEN-PATH", "<DARK_GREEN>You have chosen <GRAY>'{0}'<DARK_GREEN>."),
    COLON("COLON", ": "),
    COMMAND_NOT_RECOGNISED("COMMAND-NOT-RECOGNISED", "<RED>Command not recognised!"),
    COMMAND_RESULT("COMMAND-RESULT", "Perform the following command(s): {0}"),
    COMPLETED("COMPLETED", "<LIGHT_PURPLE>Completed paths (<GOLD>{0}<LIGHT_PURPLE>): <GOLD>{1}"),
    COMPLETED_PATH_NOW("COMPLETED-PATH-NOW", " and now completed his path."),
    COMPLETED_REQUIREMENT("COMPLETED-REQUIREMENT", "<DARK_GREEN>You completed requirement <GOLD>{0}<DARK_GREEN>: <DARK_AQUA>{1}"),
    COULD_NOT_ASSIGN("COULD-NOT-ASSIGN", "<DARK_RED>Could not assign '<GOLD>{1}<DARK_RED>' to {0}"),
    COULD_NOT_FIND("COULD-NOT-FIND", "<RED>Could not find a migration plugin for the type you specified."),
    COULD_NOT_IMPORT("COULD-NOT-IMPORT", "<RED>Could not import any players for {0}! Are you sure you put any files in the imports folder?"),
    COULD_NOT_SYNC("COULD-NOT-SYNC", "<GREEN>Could not sync stats. Run command again!"),
    CURRENT("CURRENT", "<GREEN>Current: <GOLD>{0}"),
    DAMAGE_TAKEN_REQUIREMENT("DAMAGE-TAKEN-REQUIREMENT", "Take at least {0} damage"),
    DAY_PLURAL("DAY-PLURAL", "days"),
    DAY_SINGULAR("DAY-SINGULAR", "day"),
    DEBUG_FILE("DEBUG-FILE", "<GREEN>Debug file '{0}' created!"),
    DEBUG_MODE("DEBUG-MODE", "<GOLD>Debug mode of Autorank has been "),
    DEPENDENCIES("DEPENDENCIES", "<RED>Cannot show dependencies as PluginLibrary is not installed"),
    DEPRECATED_COMMAND("DEPRECATED-COMMAND", "This command is not used anymore and will be deprecated."),
    DESCRIPTION("DESCRIPTION", "<AQUA>{0}"),
    DEVELOPED("DEVELOPED", "<GOLD>Developed by: <GRAY>{0}"),
    DID_YOU("DID-YOU", "<DARK_AQUA>Did you perhaps mean "),
    DISABLED("DISABLED", " <RED>disabled"),
    DOES_NOT_HAVE_ACTIVE("DOES-NOT-HAVE-ACTIVE", "<GOLD>{0} <RED>does not have any active paths."),
    DOES_NOT_HAVE_AS_ACTIVE("DOES-NOT-HAVE-AS-ACTIVE", "<GOLD>{0} <DARK_RED>does not have <GOLD>{1} <DARK_RED>as an active path!"),
    DONE_MARKER("DONE-MARKER", "Done"),
    DO_NOT_MEET_REQUIREMENTS_FOR("DO-NOT-MEET-REQUIREMENTS-FOR", "<RED>You do not meet requirements for #<GOLD>{0}<RED>:"),
    EFFECT_RESULT("EFFECT-RESULT", "Play '{0}' effect on you."),
    ENABLED("ENABLED", " <GREEN>enabled"),
    ESSENTIALS_GEOIP_LOCATION_REQUIREMENT("ESSENTIALS-GEOIP-LOCATION-REQUIREMENT", "Be from area {0}"),
    EXP_RANGE_REQUIREMENT("EXP-RANGE-REQUIREMENT", "Have range of level between {0} and {1} in exp"),
    EXP_REQUIREMENT("EXP-REQUIREMENT", "Have at least level {0} in exp"),
    FACTIONS_POWER_REQUIREMENT("FACTIONS-POWER-REQUIREMENT", "Have at least {0} power in your faction"),
    FIRST_LOGIN_DATE("FIRST-LOGIN-DATE", "First Login Date {0}"),
    FISH_CAUGHT_REQUIREMENT("FISH-CAUGHT-REQUIREMENT", "Catch at least {0} fish"),
    FOOD_EATEN_REQUIREMENT("FOOD-EATEN-REQUIREMENT", "Eat at least {0}"),
    GAMEMODE_REQUIREMENT("GAMEMODE-REQUIREMENT", "Be in gamemode {0}"),
    GLOBAL("GLOBAL", "<LIGHT_PURPLE>Global playtime: <GOLD>{0}"),
    GLOBAL_TIME_REQUIREMENT("GLOBAL-TIME-REQUIREMENT", "Play for at least {0} on any of the servers"),
    GRIEF_PREVENTION_BONUS_BLOCKS_REQUIREMENT("GRIEF-PREVENTION-BONUS-BLOCKS-REQUIREMENT", "Have at least {0} bonus blocks in GriefPrevention"),
    GRIEF_PREVENTION_CLAIMED_BLOCKS_REQUIREMENT("GRIEF-PREVENTION-CLAIMED-BLOCKS-REQUIREMENT", "Have at least {0} claimed blocks in GriefPrevention"),
    GRIEF_PREVENTION_CLAIMS_COUNT_REQUIREMENT("GRIEF-PREVENTION-CLAIMS-COUNT-REQUIREMENT", "Have at least {0} claims in GriefPrevention"),
    GRIEF_PREVENTION_REMAINING_BLOCKS_REQUIREMENT("GRIEF-PREVENTION-REMAINING-BLOCKS-REQUIREMENT", "Have at least {0} remaining blocks to use in claims in GriefPrevention"),
    GROUP_REQUIREMENT("GROUP-REQUIREMENT", "Be in group {0}."),
    HAS_PLAYED("HAS-PLAYED", "<GOLD>{0} <GREEN>has played for <GOLD>"),
    HAS_PLAYED_FOR("HAS-PLAYED-FOR", "<GOLD>{0}<GRAY> has played for {1}."),
    HOOKED_PLUGINS("HOOKED-PLUGINS", "<GRAY>- <GREEN>{0}"),
    HOOKS("HOOKS", "<GOLD>Autorank Hooks:"),
    HOUR_PLURAL("HOUR-PLURAL", "hours"),
    HOUR_SINGULAR("HOUR-SINGULAR", "hour"),
    ILL_LET("ILL-LET", "<GOLD>I'll let you know when the leaderboard is updated."),
    IMPORTED_DATA("IMPORTED-DATA", "<GREEN>Imported data of {0} players from Minecraft statistics!"),
    IMPORTED_OPERATION("IMPORTED-OPERATION", "<RED>Import operation cancelled by user."),
    IMPORTING_DATA_ADDING_GLOBAL("IMPORTING-DATA-ADDING-GLOBAL", "Importing data and adding to global database."),
    IMPORTING_DATA_ADDING_LOCAL("IMPORTING-DATA-ADDING-LOCAL", "Importing data and adding to local database."),
    IMPORTING_DATA_BOTH("IMPORTING-DATA-BOTH", "<GOLD>Importing data and overriding both the global and local database."),
    IMPORTING_DATA_OVERRIDING_GLOBAL("IMPORTING-DATA-OVERRIDING-GLOBAL", "<GOLD>Importing data and overriding global database."),
    IMPORTING_DATA_OVERRIDING_LOCAL("IMPORTING-DATA-OVERRIDING-LOCAL", "<GOLD>Importing data and overriding local database."),
    INFO_HEADER("INFO-HEADER", "<DARK_AQUA>---------- [<GOLD>{0}<DARK_AQUA>] ----------"),
    INVALID_FORMAT("INVALID-FORMAT", "<RED>Invalid format, use {0}."),
    INVALID_LEADERBOARD_TYPE("INVALID-LEADERBOARD-TYPE", "<RED>You have not specified a valid leaderboard type! <YELLOW>Only 'total', 'daily', 'weekly' and 'monthly' are allowed."),
    INVALID_NUMBER("INVALID-NUMBER", "<RED>{0} is not a valid number!"),
    INVALID_STORAGE_FILE("INVALID-STORAGE-FILE", "<RED>Invalid storage file. You can only backup 'playerdata' or 'storage'."),
    IN_BIOME_REQUIREMENT("IN-BIOME-REQUIREMENT", "Be in biome {0}"),
    IS_EXEMPTED_CHECKING("IS-EXEMPTED-CHECKING", "<LIGHT_PURPLE>Is exempted from checking: "),
    IS_EXEMPTED_LEADERBOARD("IS-EXEMPTED-LEADERBOARD", "<LIGHT_PURPLE>Is exempted from leaderboard: "),
    IS_EXEMPTED_OBTAINING("IS-EXEMPTED-OBTAINING", "<LIGHT_PURPLE>Is exempted from obtaining time: "),
    ITEMS_CRAFTED_REQUIREMENT("ITEMS-CRAFTED-REQUIREMENT", "Craft at least {0} item(s)"),
    ITEMS_ENCHANTED_REQUIREMENT("ITEMS-ENCHANTED-REQUIREMENT", "Enchant at least {0} items."),
    ITEM_CRAFTED_REQUIREMENT("ITEM-CRAFTED-REQUIREMENT", "Craft at least {0} {1}."),
    ITEM_REQUIREMENT("ITEM-REQUIREMENT", "Obtain {0}"),
    ITEM_THROWN_REQUIREMENT("ITEM-THROWN-REQUIREMENT", "Throw at least {0} {1}."),
    JOBS_CURRENT_POINTS_REQUIREMENT("JOBS-CURRENT-POINTS-REQUIREMENT", "Have at least {0} points in Jobs."),
    JOBS_EXPERIENCE_REQUIREMENT("JOBS-EXPERIENCE-REQUIREMENT", "Have at least {0} experience in the job '{1}'."),
    JOBS_LEVEL_REQUIREMENT("JOBS-LEVEL-REQUIREMENT", "Have at least level {0} in the job '{1}'"),
    JOBS_TOTAL_POINTS_REQUIREMENT("JOBS-TOTAL-POINTS-REQUIREMENT", "Have at least {0} points in total in Jobs"),
    LAST_LOGIN_LOGIN_REQUIREMENT("LAST-LOGIN-LOGIN-REQUIREMENT", "Been at least {0} minutes since last login."),
    LAST_LOGIN_LOGOUT_REQUIREMENT("LAST-LOGIN-LOGOUT-REQUIREMENT", "Been at least {0} minutes since last logout."),
    LEADERBOARD("LEADERBOARD", "<YELLOW>Leaderboard updated!"),
    LEADERBOARD_FOOTER("LEADERBOARD-FOOTER", "------------------------------------"),
    LEADERBOARD_HEADER_ALL_TIME("LEADERBOARD-HEADER-ALL-TIME", "-------- Leaderboard (All time) --------"),
    LEADERBOARD_HEADER_DAILY("LEADERBOARD-HEADER-DAILY", "-------- Leaderboard (Daily time) --------"),
    LEADERBOARD_HEADER_MONTHLY("LEADERBOARD-HEADER-MONTHLY", "-------- Leaderboard (Monthly time) --------"),
    LEADERBOARD_HEADER_WEEKLY("LEADERBOARD-HEADER-WEEKLY", "-------- Leaderboard (Weekly time) --------"),
    LIST_OF_COMMANDS("LIST-OF-COMMANDS", "<YELLOW>Type /ar help for a list of commands."),
    LOCAL("LOCAL", "<LIGHT_PURPLE>Local playtime: <GOLD>{0}"),
    LOCATION_REQUIREMENT("LOCATION-REQUIREMENT", "Be at {0}"),
    MCMMO_POWER_LEVEL_REQUIREMENT("MCMMO-POWER-LEVEL-REQUIREMENT", "Have at least power level {0}"),
    MCMMO_SKILL_LEVEL_REQUIREMENT("MCMMO-SKILL-LEVEL-REQUIREMENT", "Have at least level {0} in {1}"),
    MCRPG_POWER_LEVEL_REQUIREMENT("MCRPG-POWER-LEVEL-REQUIREMENT", "Have at least power level {0}"),
    MCRPG_SKILL_LEVEL_REQUIREMENT("MCRPG-SKILL-LEVEL-REQUIREMENT", "Have at least level {0} in {1}"),
    MEETS_ALL_REQUIREMENTS("MEETS-ALL-REQUIREMENTS", "meets all the requirements for path {0}"),
    MEETS_ALL_REQUIREMENTS_WITHOUT_RANK_UP("MEETS-ALL-REQUIREMENTS-WITHOUT-RANK-UP", "meets all the requirements"),
    MESSAGE_RESULT("MESSAGE-RESULT", "Send you the following message: {0}"),
    MINUTE_PLURAL("MINUTE-PLURAL", "minutes"),
    MINUTE_SINGULAR("MINUTE-SINGULAR", "minute"),
    MONEY_REQUIREMENT("MONEY-REQUIREMENT", "Have at least {0}"),
    MONEY_RESULT("MONEY-RESULT", "Reward you with {0}."),
    MYSQL_IS_NOT_ENABLED("MYSQL-IS-NOT-ENABLED", "<RED>MySQL is not enabled!"),
    NCC_ARE_YOU_SURE_RESET("NCC-ARE-YOU-SURE-RESET", "Are you sure you want to reset the %type% of {0} ? Please confirm or deny."),
    NCC_ARE_YOU_SURE_PERFORM("NCC-ARE-YOU-SURE-PERFORM", "Are you sure you want to perform this action? Type "),
    NCC_ACTIVE("NCC-ACTIVE", "active paths"),
    NCC_ACTIVE_PROGRESS("NCC-ACTIVE-PROGRESS", "active progress"),
    NCC_ALL_PROGRESS("NCC-ALL-PROGRESS", "all progress"),
    NCC_COMPLETED("NCC-COMPLETED", "completed paths"),
    NCC_CONVERSATION_HAS_BEEN("NCC-CONVERSATION-HAS-BEEN", "This conversation has been abandoned by you."),
    NCC_CONVERSATION_HAS_ENDED("NCC-CONVERSATION-HAS-ENDED", "This conversation has ended."),
    NCC_CONVERSATION_HAS_ENDED_BECAUSE("NCC-CONVERSATION-HAS-ENDED-BECAUSE", "This conversation has ended because you didn't reply in time."),
    NCC_HAS_ALREADY("NCC-HAS-ALREADY", "{0} has already completed this requirement"),
    NCC_IS_ALREADY("NCC-IS-ALREADY", "{0} is already on that path."),
    NCC_IS_NOT("NCC-IS-NOT", "{0} is not on that path."),
    NCC_OF_WHICH_PLAYER_ACTIVE("NCC-OF-WHICH-PLAYER-ACTIVE", "Of which player do you want to reset the active paths?"),
    NCC_OF_WHICH_PLAYER_ACTIVE_PROGRESS("NCC-OF-WHICH-PLAYER-ACTIVE-PROGRESS", "Of which player do you want to reset the active progress?"),
    NCC_OF_WHICH_PLAYER_ALL_PROGRESS("NCC-OF-WHICH-PLAYER-ALL-PROGRESS", "Of which player do you want to reset the all progress?"),
    NCC_OF_WHICH_PLAYER_COMPLETED("NCC-OF-WHICH-PLAYER-COMPLETED", "Of which player do you want to reset the completed paths?"),
    NCC_THAT_REQUIREMENT("NCC-THAT-REQUIREMENT", "That requirement id does not exist for {0}"),
    NCC_THE_PATH("NCC-THE-PATH", "The path {0} does not exist!"),
    NCC_WHAT_DO_YOU("NCC-WHAT-DO-YOU", "What do you want to reset? You can reset "),
    NCC_WHAT_PATH("NCC-WHAT-PATH", "What path does the requirement belong to?"),
    NCC_WHAT_PATH_ASSIGN("NCC_WHAT_PATH_ASSIGN", "What path do you want to assign to {0}?"),
    NCC_WHAT_PATH_COMPLETE("NCC-WHAT-PATH-COMPLETE", "What path do you want to complete for {0}?"),
    NCC_WHAT_PATH_UNASSIGN("NCC_WHAT_PATH_UNASSIGN", "What path do you want to unassign from {0}?"),
    NCC_WHAT_PLAYER("NCC-WHAT-PLAYER", "What player do you want to edit?"),
    NCC_WHAT_REQUIREMENT_ID("NCC-WHAT-REQUIREMENT-ID", "What requirement id do you want to complete?"),
    NCC_WHAT_TYPE("NCC-WHAT-TYPE", "What type of edit do you want to make? "),
    NCC_YOUR_ARE_ALREADY_IN("NCC-YOU-ARE-ALREADY-IN", "You are already in a conversation."),
    NONE("NONE", "none"),
    NO_FURTHER_PATH_FOUND("NO-FURTHER-PATH-FOUND", "none (no path found)"),
    NO_PATHS_TO_CHOOSE("NO-PATHS-TO-CHOOSE", "<RED>There are no paths that you can choose."),
    NO_PATH_FOUND_WITH_THAT_NAME("NO-PATH-FOUND-WITH-THAT-NAME", "<RED>There was no ranking path found with that name."),
    NO_PATH_LEFT_TO_CHOOSE("NO-PATH-LEFT-TO-CHOOSE", "<DARK_GREEN>{0} has played for {1} and currently has no path. There are no paths left to choose."),
    NO_PERMISSION("NO-PERMISSION", "<RED>You need <YELLOW>{0} <RED>for that!"),
    OR("OR", "<GREEN>or"),
    OPTIONAL_LEFT_BRACKET("OPTIONAL-LEFT-BRACKET", "<AQUA> ("),
    OPTIONAL_RIGHT_BRACKET("OPTIONAL-RIGHT-BRACKET", "<AQUA> )"),
    OPTIONAL_MARKER("OPTIONAL-MARKER", "Optional"),
    PARENTHESIS_LEFT("PARENTHESIS-LEFT", "<LIGHT_PURPLE> ("),
    PARENTHESIS_RIGHT("PARENTHESIS-RIGHT", "<LIGHT_PURPLE>): "),
    PARENTHESIS_LEFT_PERCENT("PARENTHESIS-LEFT-PERCENT", "<GOLD> ("),
    PARENTHESIS_RIGHT_PERCENT("PARENTHESIS-RIGHT-PERCENT", "<GOLD>%)"),
    PARTIAL_COMPLETION_NOT_ENABLED("PARTIAL-COMPLETION-NOT-ENABLED", "<RED>You cannot use this command as this server has not enabled partial completion!"),
    PATH_HAS_BEEN("PATH-HAS-BEEN", "<GREEN>Path '<GOLD>{1}<GREEN>' has been completed for {0}"),
    PATH_IS_DEACTIVATED("PATH-IS-DEACTIVATED", "<GREEN>Path '{0}' is deactivated "),
    PATH_IS_NOT_ACTIVE("PATH-IS-NOT-ACTIVE", "You are not active on path {0}."),
    PATH_NOT_ALLOWED_TO_RETAKE("PATH-NOT-ALLOWED-TO-RETAKE", "<RED>You already completed this path before. You are not allowed to retake it!"),
    PERMISSION_REQUIREMENT("PERMISSION-REQUIREMENT", "Have permission {0}"),
    PLACED_BLOCKS_REQUIREMENT("PLACED-BLOCKS-REQUIREMENT", "Place at least {0}"),
    PLACEHOLDERAPI_INTEGER_REQUIREMENT("PLACEHOLDERAPI-INTEGER-REQUIREMENT", "Have at least {0} {1}"),
    PLACEHOLDERAPI_STRING_REQUIREMENT("PLACEHOLDERAPI-STRING-REQUIREMENT", "Have at least {0} {1}"),
    PLANTS_POTTED_REQUIREMENT("PLANTS-POTTED-REQUIREMENT", "Pot at least {0} plants"),
    PLAYERPOINTS_POINTS_REQUIREMENT("PLAYERPOINTS-POINTS-REQUIREMENT", "Obtain at least {0} player points."),
    PLAYERS_HAVE_BEEN_MIGRATED("PLAYERS-HAVE-BEEN-MIGRATED", "{0} players have been migrated to Autorank."),
    PLAYER_IS_EXCLUDED("PLAYER-IS-EXCLUDED", "<GOLD>{0} <RED>is excluded from ranking!"),
    PLAYER_IS_INVALID("PLAYER-IS-INVALID", "<GOLD>{0} <RED>has never been logged before."),
    PLAYER_KILLS_REQUIREMENT("PLAYER-KILLS-REQUIREMENT", "Kill at least {0} player(s)"),
    PLAYER_NOT_ONLINE("PLAYER-NOT-ONLINE", "<GOLD>{0} <RED>is not online!"),
    PLAYTIME_CHANGED("PLAYTIME-CHANGED", "<GREEN>Changed playtime of {0} to {1}."),
    PREREQUISITES_OF_PATH("PREREQUISITES", "<GREEN>Prerequisites of path '<GRAY>{0}<GREEN>':"),
    PROGRESS_OF("PROGRESS-OF", "<GRAY>Progress of '<BLUE>{0}<GRAY>': "),
    PROGRESS_OF_PATHS("PROGRESS-OF-PATHS", "<GREEN>----- <GRAY>[Progress of paths for <GOLD>{0}<GRAY>]<GREEN> -----"),
    PROGRESS_RESET("PROGRESS-RESET", "<YELLOW>Your progress for the path is reset."),
    PROGRESS_RESTORED("PROGRESS-RESTORED", "<YELLOW>Your progress for the path has been restored."),
    QUESTION_MARK("QUESTION-MARK","<DARK_AQUA>?"),
    QUESTS_ACTIVE_QUESTS_REQUIREMENT("QUESTS-ACTIVE-QUESTS-REQUIREMENT", "Have {0} quests active at the same time."),
    QUESTS_COMPLETED_QUESTS_REQUIREMENT("QUESTS-COMPLETED-QUESTS-REQUIREMENT", "Complete at least {0} quests."),
    QUESTS_COMPLETE_SPECIFIC_QUEST_REQUIREMENT("QUESTS-COMPLETE-SPECIFIC-QUEST-REQUIREMENT", "Complete the quest '{0}'."),
    QUESTS_QUEST_POINTS_REQUIREMENT("QUESTS-QUEST-POINTS-REQUIREMENT", "Obtain at least {0} quest points."),
    RANK_CHANGE_RESULT("RANK-CHANGE-RESULT", "Change rank to {0}."),
    REMOVED_ALL_ACTIVE("REMOVED-ALL-ACTIVE", "<GREEN>Removed all active paths of <YELLOW>{0}"),
    REMOVED_ALL_COMPLETED("REMOVED-ALL-COMPLETED", "<GREEN>Removed all completed paths of <YELLOW>{0}"),
    REQUIREMENT("REQUIREMENT", "<GREEN>Requirement '<GOLD>{1}<GREEN>' has been completed for {0}"),
    REQUIREMENT_DISCRIPTION("REQUIREMENT-DISCRIPTION", "<DARK_AQUA>"),
    REQUIREMENT_MEET("REQUIREMENT-MEET", "<GREEN>"),
    REQUIREMENT_NOT_MET("REQUIREMENT-NOT-MET", "<RED>"),
    REQUIREMENT_NUMBER("REQUIREMENT-NUMBER", "     <GOLD>"),
    REQUIREMENT_PATH("REQUIREMENT-PATH", "<GRAY> ------------ "),
    REQUIREMENTS("REQUIREMENTS", "<GRAY>Requirements:"),
    REQUIREMENTS_OF_PATH("REQUIREMENTS_OF_PATH", "<GREEN>Requirements of path '<GRAY>{0}<GREEN>':"),
    REQUIREMENT_PROGRESS("REQUIREMENT-PROGRESS", "<RED>Progress of requirement #<GOLD>{0}<RED>:"),
    RESET_DAILY_TIME("RESET-DAILY-TIME", "<GOLD>[Autorank] <DARK_PURPLE>A new day has arrived! <YELLOW>All daily times have been reset."),
    RESET_MONTHLY_TIME("RESET-MONTHLY-TIME", "<GOLD>[Autorank] <DARK_PURPLE>A new month has arrived! <YELLOW>All monthly times have been reset."),
    RESET_PROGRESS("RESET-PROGRESS", "<GREEN>Reset progress on all active paths of <YELLOW>{0}"),
    RESET_PROGRESS_ON_ALL("RESET-PROGRESS-ON-ALL", "<GREEN>Reset progress on all paths (active AND completed) of <YELLOW>{0}"),
    RESET_WEEKLY_TIME("RESET-WEEKLY-TIME", "<GOLD>[Autorank] <DARK_PURPLE>A new week has arrived! <YELLOW>All weekly times have been reset."),
    RESULTS_DESCRIPTION("RESULTS-DISCRIPTION", "<RED>"),
    RESULTS_OF_PATH("RESULTS-OF-PATH", "<GREEN>Results of path '<GRAY>{0}<GREEN>':"),
    RPGME_COMBAT_LEVEL_REQUIREMENT("RPGME-COMBAT-LEVEL-REQUIREMENT", "Have at least a combat level of {0} in RPGme."),
    RPGME_POWER_LEVEL_REQUIREMENT("RPGME-POWER-LEVEL-REQUIREMENT", "Have at least a combined level {0} of RPGMe."),
    RPGME_SKILL_LEVEL_REQUIREMENT("RPGME-SKILL-LEVEL-REQUIREMENT", "Have at least level {0} in {1} in RPGme."),
    SECOND_PLURAL("SECOND-PLURAL", "seconds"),
    SECOND_SINGULAR("SECOND-SINGULAR", "second"),
    SPAWN_FIREWORK_RESULT("SPAWN-FIREWORK-RESULT", "Spawn firework at {0}."),
    SPECIFIC_PATH("SPECIFIC-PATH", "<DARK_AQUA>-----------------------"),
    STORAGE_IMPORTED("STORAGE-IMPORTED", "<GREEN>New storage has been imported!"),
    SUCCESSFULLY_COMPLETED_REQUIREMENT("SUCCESSFULLY-COMPLETED-REQUIREMENT", "You have successfully completed requirement <GOLD>{0}<GREEN>:"),
    SUCCESSFULLY_CREATED_PLAYERDATA("SUCCESSFULLY-CREATED-PLAYERDATA", "<GREEN>Successfully created a backup of playerdata!"),
    SUCCESSFULLY_CREATED_STORAGE("SUCCESSFULLY-CREATED-STORAGE", "<GREEN>Successfully created a backup of regular time storage!"),
    SUCCESSFULLY_UPDATED("SUCCESSFULLY-UPDATED", "<GREEN>Successfully updated {0} items in data.yml from MySQL database records!"),
    SUCCESSFULLY_UPDATED_MYSQL("SUCCESSFULLY-UPDATED-MYSQL", "<GREEN>Successfully updated MySQL records!"),
    TELEPORT_RESULT("TELEPORT-RESULT", "Get teleported to {0}."),
    THERE_ARE_NO_ACTIVE("THERE-ARE-NO-ACTIVE", "<RED>There are no active storage providers, so I can't store the imported data!"),
    THERE_IS_NO_ACTIVE_FLATFILE("THERE-IS-NO-ACTIVE-FLATFILE", "<RED>here is no active storage provider that supports flatfile data."),
    THERE_IS_NO_PLAYER("THERE-IS-NO-PLAYER", "<RED>There is no player or path named '<GOLD>{0}<RED>'."),
    THESE_ARE("THESE-ARE", "These are suggestions based on your input."),
    THE_FOLLOWING_PATHS("THE-FOLLOWING-PATHS", "<GREEN>The following paths are possible: <WHITE>"),
    THIS_COMMAND("THIS-COMMAND", "<RED>This command has been deprecated and can therefore not be used anymore."),
    THIS_IS_NOT("THIS-IS-NOT", "<RED>This is not a valid type of migration!"),
    THIS_MIGRATION("THIS-MIGRATION", "<RED>This migration plugin is not ready to be used. Are the plugins that depend on it active?"),
    THIS_PATH_DOES_NOT("THIS-PATH-DOES-NOT", "<RED>This path does not allow you to complete requirements one by one. You need to meet all requirements simultaneously."),
    TIMES_DIED_REQUIREMENT("TIMES-DIED-REQUIREMENT", "Die at least {0} times"),
    TIMES_SHEARED_REQUIREMENT("TIMES-SHEARED-REQUIREMENT", "Shear at least {0} sheep"),
    TIMES_TRADED_WITH_VILLAGERS_REQUIREMENT("TIMES-TRADED-WITH-VILLAGERS-REQUIREMENT", "Trade at least {0} with villagers"),
    TIME_DAILY_REQUIREMENT("TIME-DAILY-REQUIREMENT", " Play for at least {0} in a single day."),
    TIME_HAS("TIME-HAS", "<GREEN>Time has succesfully been updated for all entries."),
    TIME_MONTHLY_REQUIREMENT("TIME-MONTHLY-REQUIREMENT", " Play for at least {0} in a month."),
    TIME_REQUIREMENT("TIME-REQUIREMENT", "Play for at least {0}"),
    TIME_WEEKLY_REQUIREMENT("TIME-WEEKLY-REQUIREMENT", " Play for at least {0} in a week."),
    TOTAL_MOBS_KILLED_REQUIREMENT("TOTAL-MOBS-KILLED-REQUIREMENT", "Kill at least {0} {1} {2}"),
    TOTAL_TIME_REQUIREMENT("TOTAL-TIME-REQUIREMENT", "Be with this server for at least {0} mobs"),
    TOWNY_HAS_NATION_REQUIREMENT("TOWNY-HAS-NATION-REQUIREMENT", "Need to be part of a nation."),
    TOWNY_HAS_TOWN_REQUIREMENT("TOWNY-HAS-TOWN-REQUIREMENT", "Need to be part of a town."),
    TOWNY_IS_KING_REQUIREMENT("TOWNY-IS-KING-REQUIREMENT", "Need to be king of a nation."),
    TOWNY_IS_MAYOR_REQUIREMENT("TOWNY-IS-MAYOR-REQUIREMENT", "Need to be mayor of a town."),
    TOWNY_NEED_NUMBER_OF_TOWN_BLOCKS("TOWNY-NEED-NUMBER-OF-TOWN-BLOCKS", "Need to have at least {0} town blocks."),
    TO_VIEW_THE_PROGRESS("TO-VIEW-THE-PROGRESS", "<GOLD>To view the progress of a specific path, use /ar check <path name>."),
    TYPE("TYPE", "<DARK_RED>Type <GOLD>/ar view {0} <DARK_RED>to see a list of prerequisites."),
    UHC_STATS_DEATHS_REQUIREMENT("UHC-STATS-DEATHS-REQUIREMENT", "Get at least {0} deaths."),
    UHC_STATS_KILLS_REQUIREMENT("UHC-STATS-KILLS-REQUIREMENT", "Get at least {0} kills."),
    UHC_STATS_WINS_REQUIREMENT("UHC-STATS-WINS-REQUIREMENT", "Get at least {0} wins."),
    UNASSIGNED("UNASSIGNED", "<DARK_RED>Unassigned '<GOLD>{1}<DARK_RED>' from {0}"),
    UNKNOWN_PLAYER("UNKNOWN-PLAYER", "<RED>Player {0} is unknown and couldn't be identified."),
    UPDATING("UPDATING", "<GREEN>Updating the leaderboard. This could take a while!"),
    VERSION("VERSION", "<GOLD>Version: <GRAY>{0}"),
    VERTICAL_COMPLETED("VERTICAL-COMPLETED", "<GREEN>|"),
    VERTICAL_NOT_COMPLETED("VERTICAL-NOT-COMPLETED", "<RED>|"),
    VOTE_REQUIREMENT("VOTE-REQUIREMENT", "Vote at least {0} times"),
    WORLD_GUARD_REGION_REQUIREMENT("WORLD-GUARD-REGION-REQUIREMENT", "Be in region {0}"),
    WORLD_REQUIREMENT("WORLD-REQUIREMENT", "Be in {0}"),
    YOU_ARE_A_ROBOT("YOU-ARE-A-ROBOT", "<RED>You are a robot, you can't run this command from console, silly.."),
    YOU_ARE_A_ROBOT_CHOOSE("YOU-ARE-A-ROBOT-CHOOSE", "<RED>You are a robot, you can't choose ranking paths, silly.."),
    YOU_ARE_A_ROBOT_COMPLETE("YOU-ARE-A-ROBOT-COMPLETE", "<RED>You are a robot, you can't rank up, silly.."),
    YOU_ARE_A_ROBOT_DEACTIVATE("YOU-ARE-A-ROBOT-DEACTIVATE", "<RED>You are a robot, you can't deactivate paths, silly.."),
    YOU_ARE_A_ROBOT_TRACK("YOU-ARE-A-ROBOT-TRACK", "<RED>You are a robot, you don't make progress, silly.."),
    YOU_ARE_ON_COOLDOWN("YOU-ARE-ON-COOLDOWN", "You are on cooldown for this path!"),
    YOU_ARE_VIEWING("YOU-ARE-VIEWING", "<DARK_GREEN>You are viewing the path <GOLD>{1}<DARK_GREEN> for {0}."),
    YOU_DONT_HAVE("YOU-DONT-HAVE", "<RED>You don't have any requirements left."),
    YOU_DONT_HAVE_ANY("YOU-DONT-HAVE-ANY", "<RED>You don't have any requirements!"),
    YOU_DO_NOT_HAVE("YOU-DO-NOT-HAVE", "<GREEN>You do not have to use this command regularly."),
    YOU_DO_NOT_MEET("YOU-DO-NOT-MEET", "<RED>You do not meet the prerequisites of this path!"),
    YOU_NEED_TO("YOU-NEED-TO", "<RED>You need to wait <GOLD>"),
    YOU_PROBARLY("YOU-PROBARLY", "<RED>You probably meant /ar syncstats or /ar sync!"),
    YOU_SHOULD_SPECIFY("YOU-SHOULD-SPECIFY", "<RED>You should specify a player to check."),
    YOU_WANT("YOU-WANT", "<RED>You want to store the imported data to the global database, but no database is active.");

    private static FileConfiguration LANG;
    private final String def;
    private String path;

    Lang(String path, String start) {
        this.path = path;

        this.def = start;
    }

    Lang(String string) {
        this.def = string;
    }

    public static void setFile(FileConfiguration config) {
        LANG = config;
    }

    public String getConfigValue(Object... args) {
        String value = (LANG.getString(this.getPath(), this.getDefault()));
        if (args != null && args.length != 0) {
            if (args[0] == null) {
                return value;
            } else {
                for (int i = 0; i < args.length; ++i) {
                    value = value.replace("{" + i + "}", args[i].toString());
                }

                return value;
            }
        } else {
            return value;
        }
    }

    public String getDefault() {
        return this.def;
    }

    public String getPath() {
        return this.path == null ? this.toString().replace("_", "-") : this.path;
    }
}
