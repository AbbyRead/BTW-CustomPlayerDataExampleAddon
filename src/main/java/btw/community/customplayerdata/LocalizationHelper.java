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
 * selecting "es_mx" will not fall back to "es"; the game only loads the exact
 * language file in the options (or defaults to en_US). Therefore, stripping
 * region codes is purely cosmetic and does not affect which file is loaded.
 */
public class LocalizationHelper {

    /**
     * Returns the localized word for "time" based on count and language.
     *
     * @param count The number of times the player has joined.
     * @return The correctly pluralized string.
     */
    public static String getTimesWord(int count) {
        LanguageManager langManager = Minecraft.getMinecraft().getLanguageManager();
        Language currentLang = langManager.getCurrentLanguage();
        String langCode = currentLang != null ? currentLang.getLanguageCode() : "en_us";

        // Cosmetic: strip region codes (e.g., "es_mx" -> "es")
        String languageCode = langCode.contains("_") ? langCode.split("_")[0] : langCode;

        switch (languageCode) {
            case "en": case "de": case "fr": case "pt": case "es":
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

            default:
                return count == 1 ? "time" : "times";
        }
    }

    /**
     * Fetches a localized translation string for a given key.
     *
     * <p>
     * Returns the key itself if no translation exists. Regional fallbacks
     * (e.g., "es_mx" -> "es") do not occur in MC 1.6.4.
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
