package btw.community.customplayerdata.mixin.data;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ServerConfigurationManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import btw.community.customplayerdata.util.PlayerDataExtension;

@Mixin(ServerConfigurationManager.class)
public class ServerConfigurationManagerMixin {

    @Inject(method = "respawnPlayer", at = @At("TAIL"))
    private void onRespawnPlayer(EntityPlayerMP oldPlayer, int defaultDimension, boolean leavingEnd, CallbackInfoReturnable<EntityPlayerMP> cir) {
        EntityPlayer newPlayer = cir.getReturnValue();

        if (newPlayer != null && oldPlayer != null) {
            PlayerDataExtension oldData = (PlayerDataExtension) oldPlayer;
            PlayerDataExtension newData = (PlayerDataExtension) newPlayer;
            newData.setCustomIntValue(oldData.getCustomIntValue());
        }

    }
}
