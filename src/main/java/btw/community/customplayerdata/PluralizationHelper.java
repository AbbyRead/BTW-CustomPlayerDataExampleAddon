package btw.community.customplayerdata;

import net.minecraft.src.EntityPlayerMP;

/**
 * Helper class for determining the correct pluralization form based on count and player language.
 * This works server-side by checking the player's language preference from their client settings.
 */
public class PluralizationHelper {

	/**
	 * Returns the appropriate translation key suffix for the given count and player's language.
	 * <p>
	 * For languages with simple singular/plural (English, Spanish, German, etc.):
	 *   - Returns "singular" for count == 1
	 *   - Returns "plural" for count != 1
	 * <p>
	 * For Russian (complex 3-form pluralization):
	 *   - Returns "singular" for count ending in 1 (but not 11): 1, 21, 31, 41, etc.
	 *   - Returns "few" for count ending in 2-4 (but not 12-14): 2, 3, 4, 22, 23, 24, etc.
	 *   - Returns "plural" for everything else: 0, 5-20, 25-30, etc.
	 * <p>
	 * For languages without pluralization (Japanese, Chinese, Korean, etc.):
	 *   - Always returns "plural" (both translation keys should be identical in these languages)
	 *
	 * @param player The player whose language preference to check
	 * @param count The number to pluralize
	 * @return The translation key suffix ("singular", "few", or "plural")
	 */
	public static String getPluralizationKeySuffix(EntityPlayerMP player, int count) {
		// Access the player's language via reflection to avoid direct field access
		// The translator field stores language codes like "en_US", "ru_RU", "es_ES", etc.
		String languageCode = getPlayerLanguage(player);

		// Extract just the language part (e.g., "en" from "en_US")
		String language = languageCode.contains("_") ? languageCode.split("_")[0] : languageCode;

		// Russian has complex 3-form pluralization
		if ("ru".equals(language)) {
			return getRussianPluralizationForm(count);
		}

		// Polish also has 3-form pluralization similar to Russian
		if ("pl".equals(language)) {
			return getPolishPluralizationForm(count);
		}

		// Czech and Slovak have similar rules to Polish
		if ("cs".equals(language) || "sk".equals(language)) {
			return getCzechSlovakPluralizationForm(count);
		}

		// Languages without pluralization (Japanese, Chinese, Korean, Vietnamese, Thai, etc.)
		// Always return "plural" - both keys should be identical in the language files
		if ("ja".equals(language) || "zh".equals(language) || "ko".equals(language) ||
				"vi".equals(language) || "th".equals(language)) {
			return "plural";
		}

		// Default behavior for most languages (English, Spanish, German, French, Portuguese, etc.)
		// Simple singular/plural distinction
		return count == 1 ? "singular" : "plural";
	}

	/**
	 * Russian pluralization rules:
	 * - Form 1 (singular): numbers ending in 1, except those ending in 11
	 * - Form 2 (few): numbers ending in 2-4, except those ending in 12-14
	 * - Form 3 (plural): everything else
	 */
	private static String getRussianPluralizationForm(int count) {
		int rem10 = count % 10;
		int rem100 = count % 100;

		// Numbers ending in 1, but not 11: 1, 21, 31, 41, etc.
		if (rem10 == 1 && rem100 != 11) {
			return "singular";
		}

		// Numbers ending in 2-4, but not 12-14: 2, 3, 4, 22, 23, 24, etc.
		if (rem10 >= 2 && rem10 <= 4 && !(rem100 >= 12 && rem100 <= 14)) {
			return "few";
		}

		// Everything else: 0, 5-20, 25-30, etc.
		return "plural";
	}

	/**
	 * Polish pluralization rules (similar to Russian but with slight differences)
	 */
	private static String getPolishPluralizationForm(int count) {
		int rem10 = count % 10;
		int rem100 = count % 100;

		if (count == 1) {
			return "singular";
		}

		if (rem10 >= 2 && rem10 <= 4 && !(rem100 >= 12 && rem100 <= 14)) {
			return "few";
		}

		return "plural";
	}

	/**
	 * Czech and Slovak pluralization rules
	 */
	private static String getCzechSlovakPluralizationForm(int count) {
		if (count == 1) {
			return "singular";
		}

		if (count >= 2 && count <= 4) {
			return "few";
		}

		return "plural";
	}

	/**
	 * Gets the player's language code from their client settings.
	 * Returns "en_US" as default if unavailable.
	 */
	private static String getPlayerLanguage(EntityPlayerMP player) {
		// Use the accessor mixin to directly access the translator field
		String language = ((btw.community.customplayerdata.mixin.EntityPlayerMPAccessor) player).getTranslator();
		return language != null ? language : "en_US";
	}
}