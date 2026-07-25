[Русская версия](Changelog.md)
## [6.2] — Bug fixes

### Critical
- **Winner detection.** Previously the win check sat in an unreachable branch, so the last survivor kept playing against themselves until death. The winner is now determined correctly as soon as a single player remains.
- **Game state moved into the lobby.** `GameManager` no longer stores the game globally — several independent games can now run in different lobbies at the same time.
- **The `gameStarted` flag is reset after a game.** Previously a lobby would stay "stuck" forever after a match ended (you couldn't join it or start a new game).
- **Server freeze eliminated.** With `bulletCount > chamberSize` there was an infinite loop, and with `chamberSize = 0` an exception. Values are now validated and clamped.

### Logic
- **A minimum of 2 players is now required to start a game (was `< 1`)**.
- **All game and command messages go through ` LocalizationManager`, and `/roulette lang ru|en`** changes the language for each player individually.
- **Added handling of player disconnect (`PlayerQuitEvent`)**: a disconnected player no longer blocks the game or leaves "dangling" references.
- **Fixed the turn order when a player leaves from the middle of the list**.
- **The round counter now increments and is shown in the turn message**.
- **Fixed an off-by-one**: the first chamber (index 0) is no longer skipped.

### Other / cleanup
- **Added the `/roulette reload` command** (permission `russianroulette.reload`) — reloads the config and locales.
- **The language became server-wide (was per-player)**. Previously `/roulette lang` changed the language only for whoever ran the command — now it changes for everyone, the choice is saved in `config.yml` (`language: ru_RU|en_US`) and survives a restart.
- **Language switching (`/roulette lang`) was moved under a separate permission `russianroulette.lang`** (default `op`) — the language is changed by an administrator.
- **`plugin.yml`: removed non-existent subcommands/permissions (`reloadplugin`)**, fixed the `usage` line.
- **Removed the dead `LangCommand` class**.
- **`CommandHandler` is created once** (it was duplicated for the executor and the tab-completer).
- **Empty extra lobbies are removed automatically** (fixed the growth of the lobby count).
- **Fixed typos in messages** («left»/«loss» with a missing space, etc.).
- **`config.yml` cleaned up**: only gameplay settings remain, texts live in `locale/`.
- **Removed the non-working `LocalizationManager.format()` method**.
