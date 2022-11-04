package me.armar.plugins.autorank.util;
import java.util.function.Predicate;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.sound.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * An audience provider for {@link org.bukkit.Bukkit}.
 *
 * @see AudienceProvider
 * @since 4.0.0
 */
public interface BukkitAudiences extends AudienceProvider {
    /**
     * Creates an audience provider for a plugin.
     *
     * <p>There will only be one provider for each plugin.</p>
     *
     * @param plugin a plugin
     * @return an audience provider
     * @since 4.0.0
     */
    static @NotNull BukkitAudiences create(final @NotNull Plugin plugin) {
        return BukkitAudiencesImpl.instanceFor(plugin);
    }

    /**
     * Creates an audience provider builder for a plugin.
     *
     * <p>There will only be one provider for each plugin.</p>
     *
     * @param plugin a plugin
     * @return an audience provider
     * @since 4.0.0
     */
    static @NotNull Builder builder(final @NotNull Plugin plugin) {
        return BukkitAudiencesImpl.builder(plugin);
    }

    /**
     * Represent an entity as an emitter of sound.
     *
     * @param entity the entity to represent
     * @return an emitter
     * @since 4.0.0
     */
    static Sound.@NotNull Emitter asEmitter(final @NotNull Entity entity) {
        return new BukkitEmitter(entity);
    }

    /**
     * Gets an audience for a command sender.
     *
     * @param sender a command sender
     * @return an audience
     * @since 4.0.0
     */
    @NotNull Audience sender(final @NotNull CommandSender sender);

    /**
     * Gets an audience for a player.
     *
     * @param player a player
     * @return an audience
     * @since 4.0.0
     */
    @NotNull Audience player(final @NotNull Player player);

    /**
     * Creates an audience based on a filter.
     *
     * @param filter a filter
     * @return an audience
     * @since 4.0.0
     */
    @NotNull Audience filter(final @NotNull Predicate<CommandSender> filter);

    /**
     * A builder for {@link BukkitAudiences}.
     *
     * @since 4.0.0
     */
    interface Builder extends AudienceProvider.Builder<BukkitAudiences, Builder> {
    }
}