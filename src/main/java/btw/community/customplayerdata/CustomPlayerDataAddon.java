package btw.community.customplayerdata;

import btw.AddonHandler;
import btw.BTWAddon;

public class CustomPlayerDataAddon extends BTWAddon {
    private static CustomPlayerDataAddon instance;

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
    }
}
