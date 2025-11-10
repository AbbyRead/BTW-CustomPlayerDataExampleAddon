package btw.community.customplayerdata;

import net.minecraft.src.EntityPlayerMP;

/**
 * Helper class for determining the correct pluralization form based on count and player language.
 * <p>
 * WHY SERVER-SIDE PLURALIZATION?
 * ================================
 * Minecraft 1.6.4's translation system does NOT support conditional pluralization.
 * Modern systems like ICU MessageFormat can handle expressions like "{count, plural, one{# time} other{# times}}"
 * on the client side, but Minecraft 1.6.4 only performs simple %d/%s substitution.
 * <p>
 * To provide grammatically correct translations for languages with complex plural rules
 * (Russian: 3 forms, Arabic: 6 forms, Irish: 5 forms, etc.), we must select the correct
 * translation key server-side based on the player's language and the count value.
 * <p>
 * This approach respects linguistic diversity and provides native speakers with properly
 * conjugated text rather than awkward constructs like "time(s)" or grammatically incorrect plurals.
 */
public class PluralizationHelper {

	/**
	 * Returns the appropriate translation key suffix for the given count and player's language.
	 * <p>
	 * Supported pluralization systems:
	 * <ul>
	 * <li><b>Simple 2-form (most languages):</b> English, Spanish, German, French, Portuguese, etc.
	 *     Returns "singular" for count == 1, "plural" otherwise</li>
	 * <li><b>Slavic 3-form:</b> Russian, Polish, Czech, Slovak
	 *     Returns "singular", "few", or "plural" based on complex ending rules</li>
	 * <li><b>Celtic 5-form:</b> Irish, Scottish Gaelic
	 *     Returns "singular", "dual", "few", "many", or "plural"</li>
	 * <li><b>Arabic 6-form:</b>
	 *     Returns "zero", "singular", "dual", "few", "many", or "plural"</li>
	 * <li><b>Invariant:</b> Japanese, Chinese, Korean, Vietnamese, Thai, etc.
	 *     Always returns "plural" (translation keys should be identical)</li>
	 * </ul>
	 *
	 * @param player The player whose language preference to check
	 * @param count The number to pluralize
	 * @return The translation key suffix
	 */
	public static String getPluralizationKeySuffix(EntityPlayerMP player, int count) {
		String languageCode = getPlayerLanguage(player);
		String language = languageCode.contains("_") ? languageCode.split("_")[0] : languageCode;

		// Arabic - 6-form pluralization (most complex)
		if ("ar".equals(language)) {
			return getArabicPluralizationForm(count);
		}

		// Celtic languages - 5-form pluralization
		if ("ga".equals(language) || "gd".equals(language)) {
			return getCelticPluralizationForm(count);
		}

		// Slavic 3-form pluralization
		if ("ru".equals(language)) {
			return getRussianPluralizationForm(count);
		}
		if ("pl".equals(language)) {
			return getPolishPluralizationForm(count);
		}
		if ("cs".equals(language) || "sk".equals(language)) {
			return getCzechSlovakPluralizationForm(count);
		}

		// Languages without pluralization
		if ("ja".equals(language) || "zh".equals(language) || "ko".equals(language) ||
				"vi".equals(language) || "th".equals(language)) {
			return "plural";
		}

		// Default: simple 2-form pluralization (English, Spanish, German, French, etc.)
		return count == 1 ? "singular" : "plural";
	}

	/**
	 * Arabic pluralization rules (6 forms):
	 * - zero: exactly 0
	 * - singular: exactly 1
	 * - dual: exactly 2
	 * - few: 3-10 (and 103-110, 203-210, etc.)
	 * - many: 11-99 (and 111-199, 211-299, etc.)
	 * - plural: 100, 200, 300, etc. (hundreds, thousands, etc.)
	 */
	private static String getArabicPluralizationForm(int count) {
		if (count == 0) {
			return "zero";
		}
		if (count == 1) {
			return "singular";
		}
		if (count == 2) {
			return "dual";
		}

		int rem100 = count % 100;
		if (rem100 >= 3 && rem100 <= 10) {
			return "few";
		}
		if (rem100 >= 11 && rem100 <= 99) {
			return "many";
		}

		return "plural";
	}

	/**
	 * Celtic (Irish/Scottish Gaelic) pluralization rules (5 forms):
	 * - singular: exactly 1
	 * - dual: exactly 2
	 * - few: 3-6
	 * - many: 7-10
	 * - plural: 0, 11+
	 */
	private static String getCelticPluralizationForm(int count) {
		if (count == 1) {
			return "singular";
		}
		if (count == 2) {
			return "dual";
		}
		if (count >= 3 && count <= 6) {
			return "few";
		}
		if (count >= 7 && count <= 10) {
			return "many";
		}
		return "plural";
	}

	/**
	 * Russian pluralization rules (3 forms):
	 * - singular: numbers ending in 1, except 11 (1, 21, 31, 41, etc.)
	 * - few: numbers ending in 2-4, except 12-14 (2, 3, 4, 22, 23, 24, etc.)
	 * - plural: everything else (0, 5-20, 25-30, etc.)
	 */
	private static String getRussianPluralizationForm(int count) {
		int rem10 = count % 10;
		int rem100 = count % 100;

		if (rem10 == 1 && rem100 != 11) {
			return "singular";
		}
		if (rem10 >= 2 && rem10 <= 4 && !(rem100 >= 12 && rem100 <= 14)) {
			return "few";
		}
		return "plural";
	}

	/**
	 * Polish pluralization rules (3 forms):
	 * Similar to Russian but slightly different for 1
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
	 * Czech and Slovak pluralization rules (3 forms):
	 * - singular: exactly 1
	 * - few: 2-4
	 * - plural: 0, 5+
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
		String language = ((btw.community.customplayerdata.mixin.EntityPlayerMPAccessor) player).getTranslator();
		return language != null ? language : "en_US";
	}
}