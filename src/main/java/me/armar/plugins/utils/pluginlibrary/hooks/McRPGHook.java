package me.armar.plugins.utils.pluginlibrary.hooks;

import me.armar.plugins.utils.pluginlibrary.Library;
import us.eunoians.mcrpg.players.McRPGPlayer;
import us.eunoians.mcrpg.players.PlayerManager;
import us.eunoians.mcrpg.types.Skills;

import java.util.Optional;
import java.util.UUID;

public class McRPGHook extends LibraryHook {
    public McRPGHook() {
    }

    public boolean isHooked() {
        return isPluginAvailable(Library.MCRPG);
    }

    public boolean hook() {
        return isPluginAvailable(Library.MCRPG);
    }

    public Optional<McRPGPlayer> getPlayer(UUID uuid) {
        if (!this.isHooked()) {
            return Optional.empty();
        } else {
            try {
                return Optional.ofNullable(PlayerManager.getPlayer(uuid));
            } catch (Exception var3) {
                return Optional.of(new McRPGPlayer(uuid));
            }
        }
    }

    public int getPowerLevel(UUID uuid) {
        Optional<McRPGPlayer> player = this.getPlayer(uuid);
        return player.map(McRPGPlayer::getPowerLevel).orElse(0);
    }

    public int getSkillLevel(UUID uuid, String skillName) {
        Optional<McRPGPlayer> player = this.getPlayer(uuid);
        if (!player.isPresent()) {
            return 0;
        } else {
            Skills matchingSkill = Skills.fromString(skillName);
            return matchingSkill == null ? 0 : player.map((play) -> {
                return play.getSkill(matchingSkill).getCurrentLevel();
            }).orElse(0);
        }
    }
}
