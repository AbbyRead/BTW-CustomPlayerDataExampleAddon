package btw.community.customplayerdata;

import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.EntityPlayerMP;

/**
 * Handles player join tracking and sending a localized welcome message.
 */
public class PlayerJoinTracker {

	/**
	 * Reads, increments, and updates the player's join count, then sends a localized welcome message.
	 * <p>
	 * The message is sent as a translation key using ChatMessageComponent, which allows
	 * the client to translate it based on the player's language settings.
	 *
	 * @param player The player who joined.
	 */
	public static void sendWelcomeMessage(EntityPlayerMP player) {
		if (player == null) return;

		// Safely read join count
		Integer joinCount = player.getData(CustomPlayerDataAddon.JOIN_COUNT_ENTRY);
		if (joinCount == null) {
			joinCount = 0;
		}

		// Increment and save
		joinCount++;
		player.setData(CustomPlayerDataAddon.JOIN_COUNT_ENTRY, joinCount);

		// Get the appropriate pluralization suffix based on the player's language and the count
		String pluralSuffix = PluralizationHelper.getPluralizationKeySuffix(player, joinCount);

		// Construct the full translation key (e.g., "message.customplayerdata.welcome.singular")
		String translationKey = "message.customplayerdata.welcome." + pluralSuffix;

		// Send the localized message using ChatMessageComponent with substitution
		// The client will translate this based on the player's language, replacing %d with joinCount
		ChatMessageComponent msg = ChatMessageComponent.createFromTranslationWithSubstitutions(translationKey, joinCount);
		player.sendChatToPlayer(msg);
	}
}