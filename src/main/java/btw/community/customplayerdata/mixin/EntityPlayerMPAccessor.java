package btw.community.customplayerdata.mixin;

import net.minecraft.src.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor mixin to expose the private translator field from EntityPlayerMP.
 * This provides a clean, efficient way to access the player's language setting
 * without using reflection.
 */
@Mixin(EntityPlayerMP.class)
public interface EntityPlayerMPAccessor {

	/**
	 * Gets the player's language code (e.g., "en_US", "ru_RU", "es_ES").
	 *
	 * @return The language code string
	 */
	@Accessor("translator")
	String getTranslator();
}