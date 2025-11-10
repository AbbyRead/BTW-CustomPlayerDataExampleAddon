# Custom Player Data Example Addon

This Better Than Wolves CE 3.0.0 addon demonstrates how to attach and maintain custom per-player data. It uses the existing PlayerDataEntry API to create an NBT (Named Binary Tag) that is automatically saved to the player's in-game data and save file. To illustrate, the addon tracks a simple join count that increments each time the player joins a world.

---

## What this example shows

This addon demonstrates how to:

* Add a new integer field (`JoinCount`) to every player.
* Persist the data automatically using BTW's **`PlayerDataEntry`** system.
* Preserve the data across deaths, respawns, and world reloads.
* Sync the data between server and client immediately.
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

1. **Create `PlayerDataEntry`s with `DataProvider.getBuilder(...)`**

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

6. **Avoid unnecessary mixins (unless you're crazy like me)**
    * For most per-player data with `serverPlayerConnectionInitialized` and `PlayerDataEntry`, mixins are generally unnecessary.
    * This example uses a single mixin to access the player's language so the server can select the correct pluralization key.
    * The actual translation and formatting of the message is performed entirely by the client, which uses the .lang file corresponding to the player's currently selected language in-game.

---

## 🗣️ Bonus Example Content: Localization & Pluralization

This addon includes a **linguistically robust** localization system that properly handles pluralization rules across multiple language families. Unlike simple "time(s)" workarounds, this system provides grammatically correct text for native speakers.

### Why server-side pluralization?

Minecraft 1.6.4's translation system does **not** support conditional pluralization. Modern systems like ICU MessageFormat can handle expressions like `{count, plural, one{# time} other{# times}}` on the client side, but Minecraft 1.6.4 only performs simple `%d`/`%s` substitution.

To respect linguistic diversity and provide proper grammar, we:
1. Detect the player's language setting (via mixin accessor)
2. Apply language-specific pluralization rules on the server
3. Send the appropriate translation key to the client
4. Let the client render the translated text with variable substitution

### How it works

When a player joins:
1. Their join count is incremented and saved
2. Their client language (e.g., `en_US`, `ru_RU`, `ar_SA`) is detected
3. `PluralizationHelper` determines the correct plural form based on linguistic rules
4. The server sends a `ChatMessageComponent` with the appropriate translation key
5. The client renders the message in their language with the count substituted

Example code:

```java
String pluralSuffix = PluralizationHelper.getPluralizationKeySuffix(player, joinCount);
String translationKey = "message.customplayerdata.welcome." + pluralSuffix;
ChatMessageComponent msg = ChatMessageComponent.createFromTranslationWithSubstitutions(translationKey, joinCount);
player.sendChatToPlayer(msg);
```

### Supported pluralization systems

| Language family | Examples | Forms | Keys used |
|----------------|----------|-------|-----------|
| **Simple 2-form** | English, Spanish, German, French, Portuguese | 2 | `singular`, `plural` |
| **Slavic 3-form** | Russian, Polish, Czech, Slovak | 3 | `singular`, `few`, `plural` |
| **Celtic 5-form** | Irish (Gaeilge), Scottish Gaelic | 5 | `singular`, `dual`, `few`, `many`, `plural` |
| **Arabic 6-form** | Arabic | 6 | `zero`, `singular`, `dual`, `few`, `many`, `plural` |
| **Invariant** | Japanese, Chinese, Korean, Hindi, Vietnamese, Thai | 1 | `plural` only (no change) |

### Example: Russian pluralization

Russian uses different word endings based on the number:
- **1, 21, 31, 41...** → "раз" (singular)
- **2, 3, 4, 22, 23, 24...** → "раза" (few)
- **0, 5-20, 25-30...** → "раз" (plural)

The system automatically selects the correct form:
```
С возвращением! Вы зашли 1 раз.
С возвращением! Вы зашли 2 раза.
С возвращением! Вы зашли 5 раз.
```

### Technical notes

* `.lang` filenames are **case-sensitive** (e.g., `en_US.lang`, not `en_us.lang`)
* Files must be saved with **UTF-8 encoding** for non-ASCII characters
* Minecraft 1.6.4 does **not** support regional fallbacks; filenames must exactly match player language
* The mixin accessor (`EntityPlayerMPAccessor`) provides efficient access to player language without reflection

---

## Project structure

```
src/main/
├── java/btw/community/customplayerdata/
│   ├── CustomPlayerDataAddon.java
│   ├── PlayerJoinTracker.java
│   ├── PluralizationHelper.java
│   └── mixin/EntityPlayerMPAccessor.java
└── resources/
    ├── assets/customplayerdata/lang/
    ├── customplayerdata.mixins.json
    └── fabric.mod.json
```

---

## Extending this pattern

* Add more per-player stats via `PlayerDataEntry`s.
* Use `.world()` or `.global()` for non-player-specific data.
* Combine with `.syncPlayerAll()` to share data between multiple players.

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