# Custom Player Data Example Addon

An example **Better Than Wolves CE** addon demonstrating how to attach and persist custom per-player data (stored in NBT), using a simple in-game counter that increases each time the player loads into a world.

---

## What this example shows

This addon demonstrates how to:

* Add a new integer field (`customValue`) to every player.
* Save and load that data automatically using NBT.
* Preserve it across deaths, respawns, and world reloads.
* Display the value in chat to confirm it’s working.

When you join a world, you’ll see a message like:

```

[CustomPlayerData] You have joined this world 3 times.

```

This confirms that:

* The data is saved to the player’s NBT.
* It persists across sessions.
* It’s properly transferred when the player respawns.

You can use this same pattern to store any type of per-player data — integers, booleans, or strings — that you want to survive between play sessions.

---

## Project structure

```

src/main/
├── java/btw/community/customplayerdata/
│   ├── CustomPlayerDataAddon.java
│   ├── mixin/data/
│   │   ├── EntityPlayerMixin.java
│   │   └── ServerConfigurationManagerMixin.java
│   └── util/
│       └── PlayerDataExtension.java
└── resources/
├── assets/customplayerdata/icon.png
├── fabric.mod.json
└── mixins.customplayerdata.json

```

The key mixins modify `EntityPlayer` and `ServerConfigurationManager` to attach, save, and restore your custom data.

---

## More info

For general setup and build instructions, see the [BTW Gradle Fabric Example](https://github.com/BTW-Community/BTW-gradle-fabric-example).

For additional Fabric and mixin documentation, see the [Legacy Fabric wiki](https://fabricmc.net/wiki/).

---

## License

This example project is released under the **0BSD** license.  
You’re free to use, copy, and modify it for your own addons — attribution is appreciated but not required.
