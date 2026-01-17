# 🎯 Russian Roulette — Minecraft Plugin (1.20+)

> **Developer:** Eneotodo 
> **Last version:** 6.1
> **Language:** Java (Maven)  
> **Platform:** Bukkit and forks  
> **Supported Versions:** 1.20 and above

---

## 📖 Description

**Russian Roulette** is a lightweight, immersive mini-game plugin for Minecraft servers that brings the tension of the classic "Russian roulette" directly into the game world.

Players join lobbies, take turns pulling the trigger, and test their luck — one chamber may contain a bullet. Who will survive? Only fate decides.

The plugin supports **full localization** (including Russian and English), configurable settings, and clean, modular code structure for easy customization.

---

## ✨ Features

✅ Realistic Russian roulette gameplay: chambers, bullets, suspense  
✅ Lobby system — up to 4 players per game  
✅ Configurable number of **chambers** and **bullets**  
✅ Turn-based gameplay with automatic player rotation  
✅ Player eliminated on "shot" — last survivor wins  
✅ Full **localization support** (`ru_RU`, `en_US`)  
✅ Language switching: `/roulette lang ru` or `/roulette lang en`  
✅ Color-coded messages with placeholders (`%player%`, `%round%`)  
✅ Intuitive commands with TabCompletion  
✅ Safe operation — no interference with other server systems  
✅ Clean, multi-file code architecture — easy to maintain and extend  

---

## 🛠 Commands

| Command | Description |
|--------|-------------|
| `/roulette join` | Join an available lobby |
| `/roulette leave` | Leave current lobby |
| `/roulette start` | Start the game (leader only) |
| `/roulette list` | View all active lobbies |
| `/roulette lang ru` | Switch language to Russian |
| `/roulette lang en` | Switch language to English |

---

## ⚙ Configuration (`config.yml`)

```yaml
chamberSize: 6            # Total number of chambers in the revolver
bulletCount: 1            # Number of bullets loaded
maxPlayers: 4             # Maximum players per lobby
roundDelayTicks: 60       # Delay between turns (60 ticks = ~3 seconds)
startGameMessage: "&aStarting Russian Roulette!"
spinningDrumMessage: "&e%player% spins the chamber..."
safeMessage: "&aClick... You survived!"
shotMessage: "&cBANG! You lost..."