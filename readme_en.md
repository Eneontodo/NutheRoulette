[Русская версия](readme.md)
## "Russian Roulette" mini-game (1.20.6+)

The plugin brings the well-known game of "Russian Roulette" into Minecraft.
A group of players gathers in a lobby and each one takes turns shooting themselves; the last survivor in the group becomes the winner.

---

## Features

- Lobby system.
- Configurable number of chambers and bullets.
- Localization into several languages.
- The lobby leader is the player who joined the lobby first.

---

## Commands

> Command alias — /rr.

| Command                | Action                                            | Permission             |
| ---------------------- | ------------------------------------------------- | ---------------------- |
| */roulette leave*      | Leave the current group                           | russianroulette.use    |
| */roulette join*       | Join an available group                           | russianroulette.use    |
| */roulette start*      | Start the game (leader only, minimum 2 players)   | russianroulette.use    |
| */roulette list*       | List active lobbies                               | russianroulette.use    |
| */roulette lang ru/en* | Switch the language                               | russianroulette.lang   |
| /roulette reload       | Reload the configuration                          | russianroulette.reload |

---

## config.yml file (defaults)

```yml
chamberSize: 6            # Number of chambers in the cylinder (>=1)
bulletCount: 1            # Number of bullets in the cylinder (clamped to the range 1...chamberSize)
maxPlayers: 4             # Maximum players per lobby
roundDelayTicks: 60       # Delay between turns, in ticks
language: ru_RU			  # Language
```

---

## Localization
###### Message texts are stored as separate files in the plugin folder
locale/messages_ru.yml — Russian language
locale/messages_en.yml — English language
> Supported placeholders: '%player%', '%round%', '%winner%', '%id%', '%count%', '%max%', '%slots%', '%status%'.
> Color codes via '&'.
---

## Questions/Answers
##### Is this the full version of the plugin?
At the moment the entire plugin is completely free

##### How do I build the plugin from the source files?
Building requires Java JDK 21 and Maven.

Run the command:
```bash
mvn package
```

Once the build finishes, the ready .jar file will be located in the target/ folder.
##### How do I add my own localization?
Adding a localization is done by creating a file in the src/main/resources/locale/messages_<code>.yml folder, copying the variables, and registering the file in LocalizationManager
