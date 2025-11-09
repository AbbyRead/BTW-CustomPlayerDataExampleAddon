package btw.community.customplayerdata;

import net.minecraft.src.LanguageManager;
import net.minecraft.src.Language;
import net.minecraft.src.Minecraft;
import net.minecraft.src.StringTranslate;

/**
 * Helper class for fetching localized strings and handling simple pluralization.
 *
 * <p>
 * NOTE: In Minecraft 1.6.4, regional fallback is not supported. For example,
 * selecting "es_mx" will not fall back to "es_es"; the game only loads the exact
 * language file matching the selected option (or defaults to en_US). Region codes
 * are stripped here purely for pluralization logic simplicity, but translation
 * files themselves still require exact filename matches (e.g., "es_mx.lang").
 */
public class LocalizationHelper {

    /**
     * Returns the localized word for "time" based on count and language.
     *
     * <p>Handles complex pluralization rules:
     * <ul>
     *   <li>English, German, French, Portuguese, Spanish: "time" (1) vs "times" (2+)</li>
     *   <li>Russian: Three forms based on complex rules:
     *     <ul>
     *       <li>"раз" for 1, 21, 31, etc. (ending in 1, but not 11)</li>
     *       <li>"раза" for 2-4, 22-24, etc. (ending in 2-4, but not 12-14)</li>
     *       <li>"раз" for 5+, 11-14, and all other cases</li>
     *     </ul>
     *   </li>
     *   <li>Hindi, Japanese, Chinese: No pluralization (same word regardless of count)</li>
     * </ul>
     *
     * @param count The number of times the player has joined.
     * @return The correctly pluralized string.
     */
    public static String getTimesWord(int count) {
        LanguageManager langManager = Minecraft.getMinecraft().getLanguageManager();
        Language currentLang = langManager.getCurrentLanguage();
        String langCode = currentLang != null ? currentLang.getLanguageCode() : "en_us";

        // Strip region codes for pluralization logic simplicity (e.g., "es_mx" -> "es")
        // Note: This does NOT affect which translation file is loaded by Minecraft
        String languageCode = langCode.contains("_") ? langCode.split("_")[0] : langCode;

        switch (languageCode) {
            case "en": case "de": case "fr": case "pt": case "es":
                return count == 1 ? "time" : "times";

            case "ru":
                // Russian has three plural forms based on the last digit and last two digits
                int rem10 = count % 10;
                int rem100 = count % 100;

                // Form 1: ends in 1, but not 11 (1, 21, 31, ..., but not 11, 111, etc.)
                if (rem10 == 1 && rem100 != 11) return "раз";

                // Form 2: ends in 2-4, but not 12-14 (2-4, 22-24, ..., but not 12-14, 112-114, etc.)
                if (rem10 >= 2 && rem10 <= 4 && !(rem100 >= 12 && rem100 <= 14)) return "раза";

                // Form 3: all other cases (0, 5-20, 25-30, etc.)
                return "раз";

            case "hi": return "बार";
            case "ja": return "回";
            case "zh": return "次";

            default:
                return count == 1 ? "time" : "times";
        }
    }

    /**
     * Fetches a localized translation string for a given key.
     *
     * <p>
     * Returns the key itself if no translation exists. Regional fallbacks
     * (e.g., "es_mx" -> "es_es") do not occur in MC 1.6.4 — only exact
     * language file matches are loaded.
     *
     * @param key Translation key
     * @return Localized string, or the key if missing
     */
    public static String getLocalizedString(String key) {
        StringTranslate translator = StringTranslate.getInstance();
        String translated = translator.translateKey(key);

        return (translated == null || translated.equals(key)) ? key : translated;
    }
}