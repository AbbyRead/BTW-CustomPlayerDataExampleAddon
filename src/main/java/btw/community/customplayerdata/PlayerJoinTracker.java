package btw.community.customplayerdata;

import net.minecraft.src.EntityPlayerMP;

/**
 * Handles player join tracking and sending a localized welcome message.
 */
public class PlayerJoinTracker {

    /**
     * Reads, increments, and updates the player's join count, then sends a localized welcome message.
     *
     * @param player The player who joined.
     */
    public static void sendWelcomeMessage(EntityPlayerMP player) {
        if (player == null) return;

        // Safely read join count
        Integer joinCount = player.getData(CustomPlayerDataAddon.JOIN_COUNT_ENTRY);
        if (joinCount == null) {
            joinCount = 0;
            player.setData(CustomPlayerDataAddon.JOIN_COUNT_ENTRY, joinCount);
        }

        // Increment and save
        joinCount++;
        player.setData(CustomPlayerDataAddon.JOIN_COUNT_ENTRY, joinCount);

        // Get localized pluralized word
        String timesWord = LocalizationHelper.getTimesWord(joinCount);

        // Fetch localized message. Regional fallback does not occur in MC 1.6.4.
        String messageTemplate = LocalizationHelper.getLocalizedString("message.customplayerdata.welcome");

        // Send message
        player.addChatMessage(String.format(messageTemplate, joinCount, timesWord));
    }
}