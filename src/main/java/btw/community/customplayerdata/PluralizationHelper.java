package btw.community.customplayerdata;

import net.minecraft.src.LanguageManager;
import net.minecraft.src.Language;
import net.minecraft.src.Minecraft;
import net.minecraft.src.StringTranslate;
import btw.AddonHandler;

/**
 * Helper class for fetching localized strings and handling simple pluralization.
 * <p>
 * NOTE: In Minecraft 1.6.4, regional fallback is not supported.
 * Only exact filename matches (e.g., "es_ES.lang") are loaded.
 */
public class PluralizationHelper {

	private static boolean langFilesLoaded = false;

	/**
	 * Ensures the addon language files are loaded on the client.
	 * Safe to call multiple times; will only load once.
	 */
	private static void ensureLangFilesLoaded() {
		if (langFilesLoaded) return;

		try {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc != null && mc.getLanguageManager() != null) {
				LanguageManager langManager = mc.getLanguageManager();
				langManager.loadAddonLanguageExtension(mc.getResourceManager(), "customplayerdata");
				langFilesLoaded = true;
				AddonHandler.logMessage("CustomPlayerDataAddon language files loaded successfully.");
			}
		} catch (Exception e) {
			AddonHandler.logWarning("Failed to load CustomPlayerDataAddon language files: " + e);
		}
	}

	/**
	 * Returns the localized word for "time" based on count and language.
	 */
	public static String getTimesWord(int count) {
		Minecraft mc = Minecraft.getMinecraft();
		LanguageManager langManager = mc != null ? mc.getLanguageManager() : null;
		Language currentLang = langManager != null ? langManager.getCurrentLanguage() : null;
		String langCode = currentLang != null ? currentLang.getLanguageCode() : "en_US";

		// Strip region codes for pluralization logic simplicity
		String languageCode = langCode.contains("_") ? langCode.split("_")[0] : langCode;

		switch (languageCode) {
			case "en":
			case "de":
			case "fr":
			case "pt":
			case "es":
				return count == 1 ? "time" : "times";

			case "ru":
				int rem10 = count % 10;
				int rem100 = count % 100;

				if (rem10 == 1 && rem100 != 11) return "раз";
				if (rem10 >= 2 && rem10 <= 4 && !(rem100 >= 12 && rem100 <= 14)) return "раза";
				return "раз";

			case "hi": return "बार";
			case "ja": return "回";
			case "zh": return "次";

			default: return count == 1 ? "time" : "times";
		}
	}

	/**
	 * Fetches a localized translation string for a given key.
	 * Returns the key itself if no translation exists.
	 */
	public static String getLocalizedString(String key) {
		ensureLangFilesLoaded(); // load language files if not already

		StringTranslate translator = StringTranslate.getInstance();
		String translated = translator.translateKey(key);
		return (translated == null || translated.equals(key)) ? key : translated;
	}
}
