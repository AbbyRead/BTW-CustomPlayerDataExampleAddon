# Custom Player Data Example Addon

An example **Better Than Wolves CE** addon demonstrating how to attach and persist custom per-player data using the **official `PlayerDataEntry` API**, with a simple join count that increments each time the player joins a world.

---

## What this example shows

This addon demonstrates how to:

* Add a new integer field (`JoinCount`) to every player.
* Persist the data automatically using BTW's **`PlayerDataEntry`** system.
* Preserve the data across deaths, respawns, and world reloads.
* Sync the data between server and client (so players can see it immediately).
* Display the value in chat to confirm it's working.

When you join a world, you'll see a message like:

```
Welcome back! You have joined 3 times.
```

This confirms that:

* The data is saved in the player's persistent `.dat` file.
* It survives across sessions and server restarts.
* It can be safely read and updated using `getData()` and `setData()`.

You can use this pattern to store any type of per-player data — integers, booleans, strings, or more complex objects — that should persist between play sessions.

---

## Best Practices

1. **Always use `DataProvider.getBuilder(...)` to create `PlayerDataEntry`s**

   ```java
   public static final DataEntry.PlayerDataEntry<Integer> JOIN_COUNT_ENTRY =
       DataProvider.<Integer>getBuilder(Integer.class)
           .name("JoinCount")
           .defaultSupplier(() -> 0)
           .readNBT(nbt -> nbt.hasKey("JoinCount") ? nbt.getInteger("JoinCount") : 0)
           .writeNBT((nbt, val) -> nbt.setInteger("JoinCount", val))
           .player()       // marks as player-specific
           .syncPlayer()   // syncs automatically with the client
           .buildPlayer();
   ```

2. **Register entries in `initialize()`**

   ```java
   JOIN_COUNT_ENTRY.register();
   ```

   This ensures BTW saves the data automatically and manages synchronization.

3. **Read and write data via the API**

   ```java
   int joinCount = player.getData(JOIN_COUNT_ENTRY); // read
   player.setData(JOIN_COUNT_ENTRY, joinCount + 1);  // write
   ```

   Avoid manipulating raw NBT directly — BTW handles the storage location, caching, and syncing for you.

4. **Use addon-specific handlers for logic**

   Keep your data logic separate from core player events. Example:

   ```java
   PlayerJoinTracker.sendWelcomeMessage(player);
   ```

5. **Data storage location**

   BTW stores player-specific data in the player's `.dat` file:

   ```
   players/<playername>.dat -> JoinCount
   ```

   There is **no nested compound**; BTW handles storage and persistence automatically.

6. **Avoid unnecessary mixins**

   * With `serverPlayerConnectionInitialized` and `PlayerDataEntry`, mixins are **not needed**.

---

## Bonus Example Component: Localization & Pluralization

This addon includes an example of implementing localized and properly pluralized messages for the welcome text.

* The message template uses placeholders:

  ```text
  message.customplayerdata.welcome=Welcome back! You have joined %d %s.
  ```

* `LocalizationHelper` selects the correct word form based on count and fetches the translation:

  ```java
  String timesWord = LocalizationHelper.getTimesWord(joinCount);
  String messageTemplate = LocalizationHelper.getLocalizedString("message.customplayerdata.welcome");
  player.addChatMessage(String.format(messageTemplate, joinCount, timesWord));
  ```

* All `.lang` files **must be saved as UTF-8 encoding** to properly display non-ASCII characters (Russian, Chinese, Japanese, Hindi, etc.). Most text editors default to UTF-8, but verify this for files containing non-Latin characters.

* In Minecraft 1.6.4, regional fallback is not supported. Selecting "Español (México)" will not fall back to using the es_ES.lang file — the game only loads the exact language file matching the selected language code (or defaults to `en_US`).

* The "addon prefix" setting in fabric.mod.json (e.g., "EX", or in this addon's case: "CPDA") does **not** affect loading of `.lang` files.
  Minecraft reads language files directly from `assets/<modid>/lang/<locale>.lang` based on the exact filename
  (case-sensitive). Correctly naming your files (e.g., `en_US.lang`) is what ensures they load properly.

* **Pluralization logic** handles complex rules:
   - **English, German, French, Portuguese, Spanish**: "time" (1) vs "times" (2+)
   - **Russian**: Uses three forms based on the number:
      - **"раз"** → numbers ending in 1, except those ending in 11  
        *Examples:* 1 раз, 21 раз, 31 раз
      - **"раза"** → numbers ending in 2–4, except those ending in 12–14  
        *Examples:* 2 раза, 3 раза, 4 раза, 22 раза, 23 раза
      - **"раз"** → numbers ending in 5–9, 0, or 11–14  
        *Examples:* 5 раз, 6 раз, 10 раз, 11 раз, 12 раз, 14 раз, 25 раз
  - **Hindi, Japanese, Chinese**: No pluralization (same word for all counts)

* Adding a new language requires creating a `.lang` file with the same key and ensuring proper UTF-8 encoding.

---

## Project structure

```
src/main/
├── java/btw/community/customplayerdata/
│   ├── CustomPlayerDataAddon.java
│   ├── LocalizationHelper.java
│   ├── PlayerJoinTracker.java
│   └── mixin/                     // optional mixins (none currently)
└── resources/
    ├── assets/customplayerdata/
    │   ├── icon.png
    │   └── lang/                  // localization strings
    │       ├── de_DE.lang
    │       ├── ...
    │       └── zh_CN.lang
    ├── customplayerdata.mixins.json
    └── fabric.mod.json
```

---

## How it works

1. **Initialization**

   * `CustomPlayerDataAddon.initialize()` registers the `PlayerDataEntry`.

2. **Player joins the world**

   * `serverPlayerConnectionInitialized` is called.
   * `PlayerJoinTracker.sendWelcomeMessage(player)` reads the stored join count, increments it, writes it back, and sends a **localized and pluralized** message.

3. **Persistence**

   * BTW automatically saves all registered `PlayerDataEntry` values to the player's `.dat` file.

4. **Syncing**

   * The `syncPlayer()` component ensures clients see updated values immediately.

---

## Extending this pattern

* Add more per-player stats by creating new `PlayerDataEntry`s.
* Store world-specific or global data using `.world()` or `.global()` instead of `.player()`.
* Combine with `syncPlayerAll()` to share data between multiple players if needed.

---

## More info

* [BTW Gradle Fabric Example](https://github.com/BTW-Community/BTW-gradle-fabric-example)
* [Legacy Fabric wiki](https://fabricmc.net/wiki/)
* [BTW CE Wiki](https://wiki.btwce.com/)
* [BTW CE Discord](https://discord.btwce.com/)

---

## License

This example project is released under the **0BSD** license.
You are free to use, copy, and modify it for your own addons — attribution is appreciated but not required.