package btw.community.customplayerdata.mixin.data;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import btw.community.customplayerdata.util.PlayerDataExtension;

@Mixin(EntityPlayer.class)
@Implements(@Interface(iface = PlayerDataExtension.class, prefix = "custom$"))
public class EntityPlayerMixin {

    @Unique private static final String NBT_TAG = "CustomPlayerData";
    @Unique private int customValue = 0;
    @Unique private int timesJoinedWorld = 0;
    @Unique private boolean hasDisplayedJoinMessage = false;

    // Interface getter/setter
    public int custom$getCustomIntValue() { return customValue; }
    public void custom$setCustomIntValue(int value) { this.customValue = value; }

    // Save to NBT
    @Inject(method = "writeModDataToNBT", at = @At("TAIL"))
    private void onWriteModDataToNBT(NBTTagCompound tag, CallbackInfo ci) {
        NBTTagCompound myData = new NBTTagCompound();
        myData.setInteger("customValue", customValue);
        myData.setInteger("timesJoinedWorld", timesJoinedWorld);
        tag.setTag(NBT_TAG, myData);
    }

    // Load from NBT
    @Inject(method = "readModDataFromNBT", at = @At("TAIL"))
    private void onReadModDataFromNBT(NBTTagCompound tag, CallbackInfo ci) {
        if (tag.hasKey(NBT_TAG)) {
            NBTTagCompound myData = tag.getCompoundTag(NBT_TAG);
            customValue = myData.hasKey("customValue") ? myData.getInteger("customValue") : 0;
            timesJoinedWorld = myData.hasKey("timesJoinedWorld") ? myData.getInteger("timesJoinedWorld") : 0;
        }
        // Increment join counter
        timesJoinedWorld++;
    }

    // Show message on world join (once)
    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void onLivingUpdate(CallbackInfo ci) {
        EntityPlayer self = (EntityPlayer)(Object)this;
        World world = self.worldObj;

        if (!world.isRemote && !hasDisplayedJoinMessage && self.addedToChunk) {
            self.addChatMessage("You've joined this world " + timesJoinedWorld + " times!");
            hasDisplayedJoinMessage = true;
        }
    }
}
