package com.conaxgames.libraries.sound;

import com.conaxgames.libraries.util.VersioningChecker;
import com.cryptomorin.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

/**
 * The sound effects every plugin shares, so the same moment sounds the same anywhere on the network.
 * <p>
 * Each plays to its player alone and is skipped while they have sound effects turned off, which the plugin
 * owning that setting marks with {@link #MUTED} metadata reading true. Metadata travels with the player, so
 * the setting holds for every plugin, even one that shades or relocates its own copy of this library.
 */
public enum Sfx {

    /** A neutral menu selection, such as picking an option or cycling a value. */
    CLICK(XSound.UI_BUTTON_CLICK, 0.4f, 1f),
    /** A menu opened with no other menu on screen, played by the menu itself. */
    MENU_OPEN(XSound.ENTITY_LLAMA_CHEST, 0.5f, 1f),
    /** A paginated menu moved to another page, played by its own buttons. */
    PAGE_TURN(XSound.ITEM_BOOK_PAGE_TURN, 0.6f, 1f),
    /** A setting was switched on. */
    TOGGLE_ON(XSound.BLOCK_LEVER_CLICK, 0.5f, 0.6f),
    /** A setting was switched off. */
    TOGGLE_OFF(XSound.BLOCK_LEVER_CLICK, 0.5f, 0.5f),
    /** An action went through, such as saving, buying or accepting. */
    SUCCESS(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 1f),
    /** An action was refused, such as not affording it, an invalid target or a cooldown. */
    ERROR(XSound.ENTITY_VILLAGER_NO, 0.5f, 1f),
    /** Something arrived for the player, such as a request, an invite, mail or a payment. */
    NOTIFY(XSound.BLOCK_NOTE_BLOCK_PLING, 0.6f, 1.4f),
    /** The player earned something or progressed, such as a claimed reward, a payout or a level. */
    REWARD(XSound.ENTITY_PLAYER_LEVELUP, 0.6f, 1f),
    /** A big moment, such as winning an event or completing a quest or collection. */
    ACHIEVEMENT(XSound.UI_TOAST_CHALLENGE_COMPLETE, 0.6f, 1f),
    /** The player was moved, such as by a warp, home, spawn or accepted teleport request. */
    TELEPORT(XSound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1f),
    /** One second of a countdown. */
    COUNTDOWN(XSound.BLOCK_NOTE_BLOCK_HAT, 0.8f, 1f),
    /** A countdown finished and play begins. */
    START(XSound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f),
    /** The player lost, such as being eliminated from an event. */
    DEFEAT(XSound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f),
    /** A server-wide announcement worth noticing, such as an event opening. */
    ANNOUNCE(XSound.BLOCK_NOTE_BLOCK_CHIME, 0.7f, 1f);

    /** Metadata a player carries while their sound effects are off. */
    public static final String MUTED = "sfx-muted";

    // From 1.19 a sound can be bound to the player, so one played as they teleport travels with them and still reaches them.
    private static final boolean FOLLOWS = VersioningChecker.supports("1.19");

    private final XSound sound;
    private final float volume;
    private final float pitch;

    Sfx(XSound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void play(Player player) {
        play(player, sound, volume, pitch);
    }

    /** Plays a sound the palette has no effect for, under the same rules. */
    public static void play(Player player, XSound sound, float volume, float pitch) {
        if (player == null || sound == null || !sound.isSupported() || !enabled(player)) {
            return;
        }
        if (FOLLOWS) {
            player.playSound(player, sound.get(), SoundCategory.MASTER, volume, pitch);
        } else {
            sound.play(player, volume, pitch);
        }
    }

    /** Plays a sound read from config with {@link #parse(String)}, under the same rules. */
    public static void play(Player player, XSound.Record sound) {
        if (player != null && sound != null && enabled(player)) {
            sound.soundPlayer().forPlayers(player).play();
        }
    }

    /**
     * Reads a config sound once, such as "BLOCK_NOTE_BLOCK_PLING, 1, 2", or null for none.
     * A sound this server does not know is logged and skipped, so a typo never stops a plugin from loading.
     */
    public static XSound.Record parse(String sound) {
        try {
            return XSound.parse(sound);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Skipping sound '" + sound + "': " + e.getMessage());
            return null;
        }
    }

    private static boolean enabled(Player player) {
        for (MetadataValue value : player.getMetadata(MUTED)) {
            if (value.asBoolean()) {
                return false;
            }
        }
        return true;
    }
}
