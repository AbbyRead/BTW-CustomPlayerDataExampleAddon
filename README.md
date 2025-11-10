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

    * With `serverPlayerConnectionInitialized` and `PlayerDataEntry`, mixins are **not needed** — except for clean data access such as player language (see below).

---

## 🗣️ Bonus Example Content: Localization

This addon includes a fully localized and pluralization-aware welcome message system, powered by Minecraft’s built-in translation components (`ChatMessageComponent`).

### How it works

* Each language file defines up to three message forms:

  ```text
  message.customplayerdata.welcome.singular=Welcome back! You have joined %d time.
  message.customplayerdata.welcome.few=Welcome back! You have joined %d times.
  message.customplayerdata.welcome.plural=Welcome back! You have joined %d times.
  ```

* When a player joins:

    1. Their join count is incremented and saved.
    2. Their selected client language (e.g. `en_US`, `ru_RU`, `ja_JP`) is detected using a mixin accessor.
    3. `PluralizationHelper` determines the correct plural form based on count and language.
    4. The addon sends a localized message using `ChatMessageComponent`, allowing the client to render it correctly.

Example code:

```java
String pluralSuffix = PluralizationHelper.getPluralizationKeySuffix(player, joinCount);
String translationKey = "message.customplayerdata.welcome." + pluralSuffix;
ChatMessageComponent msg = ChatMessageComponent.createFromTranslationWithSubstitutions(translationKey, joinCount);
player.sendChatToPlayer(msg);
```

### Supported pluralization rules

| Language type                      | Examples                                     | Key suffixes used             |
| ---------------------------------- | -------------------------------------------- | ----------------------------- |
| **Simple 2-form (most languages)** | English, Spanish, German, French, Portuguese | `singular` / `plural`         |
| **3-form (Slavic languages)**      | Russian, Polish, Czech, Slovak               | `singular` / `few` / `plural` |
| **Invariant**                      | Japanese, Chinese, Hindi, Korean, etc.       | `plural` (same text for all)  |

### Technical notes

* Language detection uses an accessor mixin to read the private `translator` field from `EntityPlayerMP`:

  ```java
  ((EntityPlayerMPAccessor) player).getTranslator();
  ```
* All `.lang` files **must be saved as UTF-8** for non-Latin characters to display properly.
* Minecraft 1.6.4 does **not** support regional fallbacks — filenames must exactly match the player’s selected language code (e.g., `es_ES.lang`).

---

## 📁 Project structure

```
src/main/
├── java/btw/community/customplayerdata/
│   ├── CustomPlayerDataAddon.java
│   ├── PlayerJoinTracker.java
│   ├── PluralizationHelper.java
│   └── mixin/
│       └── EntityPlayerMPAccessor.java
└── resources/
    ├── assets/customplayerdata/
    │   ├── icon.png
    │   └── lang/
    │       ├── en_US.lang
    │       ├── de_DE.lang
    │       ├── ...
    │       └── zh_CN.lang
    ├── customplayerdata.mixins.json
    └── fabric.mod.json
```

---

## ⚙️ How it works

1. **Initialization**

    * `CustomPlayerDataAddon.initialize()` registers the `PlayerDataEntry`.

2. **Player joins the world**

    * `serverPlayerConnectionInitialized` triggers `PlayerJoinTracker.sendWelcomeMessage(player)`.
    * The join count is incremented, saved, and displayed with correct pluralization and localization.

3. **Persistence**

    * BTW automatically saves registered `PlayerDataEntry` values to each player’s `.dat` file.

4. **Syncing**

    * The `.syncPlayer()` flag ensures the player’s data stays synchronized between server and client.

5. **Localization**

    * The player’s language is detected via a mixin accessor, ensuring accurate translation key selection.

---

## Extending this pattern

* Add more per-player stats by creating new `PlayerDataEntry`s.
* Store world-specific or global data using `.world()` or `.global()` instead of `.player()`.
* Combine with `.syncPlayerAll()` to share data between multiple players if needed.

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
