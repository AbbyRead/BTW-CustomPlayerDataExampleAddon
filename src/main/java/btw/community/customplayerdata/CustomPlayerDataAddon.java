package btw.community.customplayerdata;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.world.util.data.DataEntry;
import btw.world.util.data.DataProvider;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetServerHandler;

/**
 * CustomPlayerDataAddon demonstrates storing per-player data (JoinCount)
 * and sending a welcome message with appropriate localization.
 */
public class CustomPlayerDataAddon extends BTWAddon {

    private static CustomPlayerDataAddon instance;

    // Player data entry for join count
    public static final DataEntry.PlayerDataEntry<Integer> JOIN_COUNT_ENTRY =
            DataProvider.<Integer>getBuilder(Integer.class)
                    .name("JoinCount")
                    .defaultSupplier(() -> 0)
                    .readNBT(nbt -> nbt.hasKey("JoinCount") ? nbt.getInteger("JoinCount") : 0)
                    .writeNBT((nbt, val) -> nbt.setInteger("JoinCount", val))
                    .player()           // marks as player-specific
                    .syncPlayer()       // ensures it syncs with client
                    .buildPlayer();     // builds the PlayerDataEntry instance

    public CustomPlayerDataAddon() {
        super();
        instance = this;
    }

    public static CustomPlayerDataAddon getInstance() {
        return instance;
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
        // Register the PlayerDataEntry so BTW knows to save it
        JOIN_COUNT_ENTRY.register();
    }

    @Override
    public void serverPlayerConnectionInitialized(NetServerHandler serverHandler, EntityPlayerMP playerMP) {
        // Delegate join handling entirely to PlayerJoinTracker
        PlayerJoinTracker.sendWelcomeMessage(playerMP);
    }
}
