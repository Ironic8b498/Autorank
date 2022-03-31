package me.armar.plugins.utils.pluginlibrary.hooks;

import com.gmail.nossr50.api.*;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.mcMMO;
import me.armar.plugins.utils.pluginlibrary.Library;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class McMMOHook extends LibraryHook {
    private mcMMO api;

    public McMMOHook() {
    }

    public boolean isHooked() {
        return this.api != null;
    }

    public boolean hook() {
        if (!isPluginAvailable(Library.MCMMO)) {
            return false;
        } else {
            this.api = (mcMMO)this.getServer().getPluginManager().getPlugin(Library.MCMMO.getInternalPluginName());
            return this.api != null;
        }
    }

    public boolean isValidSkillType(String skillType) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return ExperienceAPI.isValidSkillType(skillType);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public boolean isNonChildSkill(String skillType) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return ExperienceAPI.isNonChildSkill(skillType);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public void addRawXP(Player player, String skillType, float XP, String xpGainReason, boolean isUnshared) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.addRawXP(player, skillType, XP, xpGainReason, isUnshared);
            } catch (Exception var7) {
            }
        }
    }

    public void addRawXPOffline(UUID uuid, String skillType, float XP) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.addRawXPOffline(uuid, skillType, XP);
            } catch (Exception var5) {
            }
        }
    }

    public void addModifiedXP(Player player, String skillType, int XP, String xpGainReason, boolean isUnshared) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.addModifiedXP(player, skillType, XP, xpGainReason, isUnshared);
            } catch (Exception var7) {
            }
        }
    }

    public void addMultipliedXP(Player player, String skillType, int XP, String xpGainReason) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.addMultipliedXP(player, skillType, XP, xpGainReason);
            } catch (Exception var6) {
            }
        }
    }

    /** @deprecated */
    @Deprecated
    public void addMultipliedXPOffline(String playerName, String skillType, int XP) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.addMultipliedXPOffline(playerName, skillType, XP);
            } catch (Exception var5) {
            }
        }
    }

    /** @deprecated */
    @Deprecated
    public void addModifiedXPOffline(String playerName, String skillType, int XP) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.addModifiedXPOffline(playerName, skillType, XP);
            } catch (Exception var5) {
            }
        }
    }

    public void addXP(Player player, String skillType, int XP, String xpGainReason, boolean isUnshared) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.addXP(player, skillType, XP, xpGainReason, isUnshared);
            } catch (Exception var7) {
            }
        }
    }

    public int getXP(Player player, String skillType) {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getXP(player, skillType);
            } catch (Exception var4) {
                return -1;
            }
        }
    }

    public int getOfflineXP(UUID uuid, String skillType) {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getOfflineXP(uuid, skillType);
            } catch (Exception var4) {
                return -1;
            }
        }
    }

    public float getXPRaw(Player player, String skillType) {
        if (!this.isHooked()) {
            return -1.0F;
        } else {
            try {
                return ExperienceAPI.getXPRaw(player, skillType);
            } catch (Exception var4) {
                return -1.0F;
            }
        }
    }

    public float getOfflineXPRaw(UUID uuid, String skillType) {
        if (!this.isHooked()) {
            return -1.0F;
        } else {
            try {
                return ExperienceAPI.getOfflineXPRaw(uuid, skillType);
            } catch (Exception var4) {
                return -1.0F;
            }
        }
    }

    public int getXPToNextLevel(Player player, String skillType) {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getXPToNextLevel(player, skillType);
            } catch (Exception var4) {
                return -1;
            }
        }
    }

    public int getOfflineXPToNextLevel(UUID uuid, String skillType) {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getOfflineXPToNextLevel(uuid, skillType);
            } catch (Exception var4) {
                return -1;
            }
        }
    }

    public int getXPRemaining(Player player, String skillType) {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getXPRemaining(player, skillType);
            } catch (Exception var4) {
                return -1;
            }
        }
    }

    public float getOfflineXPRemaining(UUID uuid, String skillType) {
        if (!this.isHooked()) {
            return -1.0F;
        } else {
            try {
                return ExperienceAPI.getOfflineXPRemaining(uuid, skillType);
            } catch (Exception var4) {
                return -1.0F;
            }
        }
    }

    public void addLevel(Player player, String skillType, int levels) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.addLevel(player, skillType, levels);
            } catch (Exception var5) {
            }
        }
    }

    public void addLevelOffline(UUID uuid, String skillType, int levels) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.addLevelOffline(uuid, skillType, levels);
            } catch (Exception var5) {
            }
        }
    }

    public int getLevel(Player player, String skillType) {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getLevel(player, skillType);
            } catch (Exception var4) {
                return -1;
            }
        }
    }

    public int getLevelOffline(UUID uuid, String skillType) {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getLevelOffline(uuid, skillType);
            } catch (Exception var4) {
                return -1;
            }
        }
    }

    public int getPowerLevel(Player player) {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getPowerLevel(player);
            } catch (Exception var3) {
                return -1;
            }
        }
    }

    public int getPowerLevelOffline(UUID uuid) {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getPowerLevelOffline(uuid);
            } catch (Exception var3) {
                return -1;
            }
        }
    }

    public int getLevelCap(String skillType) {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getLevelCap(skillType);
            } catch (Exception var3) {
                return -1;
            }
        }
    }

    public int getPowerLevelCap() {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getPowerLevelCap();
            } catch (Exception var2) {
                return -1;
            }
        }
    }

    public int getPlayerRankSkill(UUID uuid, String skillType) {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getPlayerRankSkill(uuid, skillType);
            } catch (Exception var4) {
                return -1;
            }
        }
    }

    public int getPlayerRankOverall(UUID uuid) {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getPlayerRankOverall(uuid);
            } catch (Exception var3) {
                return -1;
            }
        }
    }

    public void setLevel(Player player, String skillType, int skillLevel) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.setLevel(player, skillType, skillLevel);
            } catch (Exception var5) {
            }
        }
    }

    public void setLevelOffline(UUID uuid, String skillType, int skillLevel) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.setLevelOffline(uuid, skillType, skillLevel);
            } catch (Exception var5) {
            }
        }
    }

    public void setXP(Player player, String skillType, int newValue) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.setXP(player, skillType, newValue);
            } catch (Exception var5) {
            }
        }
    }

    public void setXPOffline(UUID uuid, String skillType, int newValue) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.setXPOffline(uuid, skillType, newValue);
            } catch (Exception var5) {
            }
        }
    }

    public void removeXP(Player player, String skillType, int xp) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.removeXP(player, skillType, xp);
            } catch (Exception var5) {
            }
        }
    }

    public void removeXPOffline(UUID uuid, String skillType, int xp) {
        if (this.isHooked()) {
            try {
                ExperienceAPI.removeXPOffline(uuid, skillType, xp);
            } catch (Exception var5) {
            }
        }
    }

    public int getXpNeededToLevel(int level) {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getXpNeededToLevel(level);
            } catch (Exception var3) {
                return -1;
            }
        }
    }

    public int getXpNeededToLevel(int level, String formulaType) {
        if (!this.isHooked()) {
            return -1;
        } else {
            try {
                return ExperienceAPI.getXpNeededToLevel(level, formulaType);
            } catch (Exception var4) {
                return -1;
            }
        }
    }

    public boolean berserkEnabled(Player player) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return AbilityAPI.berserkEnabled(player);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public boolean gigaDrillBreakerEnabled(Player player) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return AbilityAPI.gigaDrillBreakerEnabled(player);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public boolean greenTerraEnabled(Player player) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return AbilityAPI.greenTerraEnabled(player);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public boolean serratedStrikesEnabled(Player player) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return AbilityAPI.serratedStrikesEnabled(player);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public boolean skullSplitterEnabled(Player player) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return AbilityAPI.skullSplitterEnabled(player);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public boolean superBreakerEnabled(Player player) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return AbilityAPI.superBreakerEnabled(player);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public boolean treeFellerEnabled(Player player) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return AbilityAPI.treeFellerEnabled(player);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public boolean isAnyAbilityEnabled(Player player) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return AbilityAPI.isAnyAbilityEnabled(player);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public void resetCooldowns(Player player) {
        if (this.isHooked()) {
            try {
                AbilityAPI.resetCooldowns(player);
            } catch (Exception var3) {
            }
        }
    }

    public void setBerserkCooldown(Player player, long cooldown) {
        if (this.isHooked()) {
            try {
                AbilityAPI.setBerserkCooldown(player, cooldown);
            } catch (Exception var5) {
            }
        }
    }

    public void setGigaDrillBreakerCooldown(Player player, long cooldown) {
        if (this.isHooked()) {
            try {
                AbilityAPI.setGigaDrillBreakerCooldown(player, cooldown);
            } catch (Exception var5) {
            }
        }
    }

    public void setGreenTerraCooldown(Player player, long cooldown) {
        if (this.isHooked()) {
            try {
                AbilityAPI.setGreenTerraCooldown(player, cooldown);
            } catch (Exception var5) {
            }
        }
    }

    public void setSerratedStrikesCooldown(Player player, long cooldown) {
        if (this.isHooked()) {
            try {
                AbilityAPI.setSerratedStrikesCooldown(player, cooldown);
            } catch (Exception var5) {
            }
        }
    }

    public void setSkullSplitterCooldown(Player player, long cooldown) {
        if (this.isHooked()) {
            try {
                AbilityAPI.setSkullSplitterCooldown(player, cooldown);
            } catch (Exception var5) {
            }
        }
    }

    public void setSuperBreakerCooldown(Player player, long cooldown) {
        if (this.isHooked()) {
            try {
                AbilityAPI.setSuperBreakerCooldown(player, cooldown);
            } catch (Exception var5) {
            }
        }
    }

    public void setTreeFellerCooldown(Player player, long cooldown) {
        if (this.isHooked()) {
            try {
                AbilityAPI.setTreeFellerCooldown(player, cooldown);
            } catch (Exception var5) {
            }
        }
    }

    public boolean isBleeding(LivingEntity entity) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return AbilityAPI.isBleeding(entity);
            } catch (Exception var3) {
                return false;
            }
        }
    }

/*    public void sendPartyChat(Plugin plugin, String sender, String displayName, String party, String message) {
        if (this.isHooked()) {
            try {
                ChatAPI.sendPartyChat(plugin, sender, displayName, party, message);
            } catch (Exception var7) {
            }
        }
    }*/

/*    public void sendPartyChat(Plugin plugin, String sender, String party, String message) {
        if (this.isHooked()) {
            try {
                ChatAPI.sendPartyChat(plugin, sender, party, message);
            } catch (Exception var6) {
            }
        }
    }*/

/*
    public void sendAdminChat(Plugin plugin, String sender, String displayName, String message) {
        if (this.isHooked()) {
            try {
                ChatAPI.sendAdminChat(plugin, sender, displayName, message);
            } catch (Exception var6) {
            }
        }
    }
*/

/*    public void sendAdminChat(Plugin plugin, String sender, String message) {
        if (this.isHooked()) {
            try {
                ChatAPI.sendAdminChat(plugin, sender, message);
            } catch (Exception var5) {
            }
        }
    }*/

    public boolean isUsingPartyChat(Player player) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return ChatAPI.isUsingPartyChat(player);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public boolean isUsingPartyChat(String playerName) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return ChatAPI.isUsingPartyChat(playerName);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public boolean isUsingAdminChat(Player player) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return ChatAPI.isUsingAdminChat(player);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public boolean isUsingAdminChat(String playerName) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return ChatAPI.isUsingAdminChat(playerName);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public void togglePartyChat(Player player) {
        if (this.isHooked()) {
            try {
                ChatAPI.togglePartyChat(player);
            } catch (Exception var3) {
            }
        }
    }

    public void togglePartyChat(String playerName) {
        if (this.isHooked()) {
            try {
                ChatAPI.togglePartyChat(playerName);
            } catch (Exception var3) {
            }
        }
    }

    public void toggleAdminChat(Player player) {
        if (this.isHooked()) {
            try {
                ChatAPI.toggleAdminChat(player);
            } catch (Exception var3) {
            }
        }
    }

    public void toggleAdminChat(String playerName) {
        if (this.isHooked()) {
            try {
                ChatAPI.toggleAdminChat(playerName);
            } catch (Exception var3) {
            }
        }
    }

    public String getPartyName(Player player) {
        if (!this.isHooked()) {
            return null;
        } else {
            try {
                return PartyAPI.getPartyName(player);
            } catch (Exception var3) {
                return null;
            }
        }
    }

    public boolean inParty(Player player) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return PartyAPI.inParty(player);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public boolean inSameParty(Player playera, Player playerb) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return PartyAPI.inSameParty(playera, playerb);
            } catch (Exception var4) {
                return false;
            }
        }
    }

    public List<Party> getParties() {
        if (!this.isHooked()) {
            return new ArrayList();
        } else {
            try {
                return PartyAPI.getParties();
            } catch (Exception var2) {
                return new ArrayList();
            }
        }
    }

    public void addToParty(Player player, String partyName) {
        if (this.isHooked()) {
            try {
                PartyAPI.addToParty(player, partyName);
            } catch (Exception var4) {
            }
        }
    }

    public void removeFromParty(Player player) {
        if (this.isHooked()) {
            try {
                PartyAPI.removeFromParty(player);
            } catch (Exception var3) {
            }
        }
    }

    public String getPartyLeader(String partyName) {
        if (!this.isHooked()) {
            return null;
        } else {
            try {
                return PartyAPI.getPartyLeader(partyName);
            } catch (Exception var3) {
                return null;
            }
        }
    }

    /** @deprecated */
    @Deprecated
    public void setPartyLeader(String partyName, String playerName) {
        if (this.isHooked()) {
            try {
                PartyAPI.setPartyLeader(partyName, playerName);
            } catch (Exception var4) {
            }
        }
    }

    /** @deprecated */
    @Deprecated
    public List<OfflinePlayer> getOnlineAndOfflineMembers(Player player) {
        if (!this.isHooked()) {
            return new ArrayList();
        } else {
            try {
                return PartyAPI.getOnlineAndOfflineMembers(player);
            } catch (Exception var3) {
                return new ArrayList();
            }
        }
    }

    /** @deprecated */
    @Deprecated
    public LinkedHashSet<String> getMembers(Player player) {
        if (!this.isHooked()) {
            return null;
        } else {
            try {
                return PartyAPI.getMembers(player);
            } catch (Exception var3) {
                return null;
            }
        }
    }

    public LinkedHashMap<UUID, String> getMembersMap(Player player) {
        if (!this.isHooked()) {
            return null;
        } else {
            try {
                return PartyAPI.getMembersMap(player);
            } catch (Exception var3) {
                return null;
            }
        }
    }

    public List<Player> getOnlineMembers(String partyName) {
        if (!this.isHooked()) {
            return new ArrayList();
        } else {
            try {
                return PartyAPI.getOnlineMembers(partyName);
            } catch (Exception var3) {
                return new ArrayList();
            }
        }
    }

    public List<Player> getOnlineMembers(Player player) {
        if (!this.isHooked()) {
            return new ArrayList();
        } else {
            try {
                return PartyAPI.getOnlineMembers(player);
            } catch (Exception var3) {
                return new ArrayList();
            }
        }
    }

    public boolean hasAlly(String partyName) {
        if (!this.isHooked()) {
            return false;
        } else {
            try {
                return PartyAPI.hasAlly(partyName);
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public String getAllyName(String partyName) {
        if (!this.isHooked()) {
            return null;
        } else {
            try {
                return PartyAPI.getAllyName(partyName);
            } catch (Exception var3) {
                return null;
            }
        }
    }

    public List<String> getSkills() {
        if (!this.isHooked()) {
            return new ArrayList();
        } else {
            try {
                return SkillAPI.getSkills();
            } catch (Exception var2) {
                return new ArrayList();
            }
        }
    }

    public List<String> getNonChildSkills() {
        if (!this.isHooked()) {
            return new ArrayList();
        } else {
            try {
                return SkillAPI.getNonChildSkills();
            } catch (Exception var2) {
                return new ArrayList();
            }
        }
    }

    public List<String> getChildSkills() {
        if (!this.isHooked()) {
            return new ArrayList();
        } else {
            try {
                return SkillAPI.getChildSkills();
            } catch (Exception var2) {
                return new ArrayList();
            }
        }
    }

    public List<String> getCombatSkills() {
        if (!this.isHooked()) {
            return new ArrayList();
        } else {
            try {
                return SkillAPI.getCombatSkills();
            } catch (Exception var2) {
                return new ArrayList();
            }
        }
    }

    public List<String> getGatheringSkills() {
        if (!this.isHooked()) {
            return new ArrayList();
        } else {
            try {
                return SkillAPI.getGatheringSkills();
            } catch (Exception var2) {
                return new ArrayList();
            }
        }
    }

    public List<String> getMiscSkills() {
        if (!this.isHooked()) {
            return new ArrayList();
        } else {
            try {
                return SkillAPI.getMiscSkills();
            } catch (Exception var2) {
                return new ArrayList();
            }
        }
    }
}
