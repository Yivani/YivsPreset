<!-- Modrinth README -->

# Yiv’s Presets

<p align="center">
  <img src="https://img.shields.io/badge/Available%20for%20Minecraft-1.21.x-ecebe6?labelColor=76feff&style=for-the-badge" alt="Minecraft 1.21.x"/>
  <img src="https://img.shields.io/badge/Loader-Fabric-ecebe6?labelColor=76feff&style=for-the-badge" alt="Fabric Loader"/>
  <img src="https://img.shields.io/badge/Java-21+-ecebe6?labelColor=76feff&style=for-the-badge" alt="Java 21+"/>
  <img src="https://img.shields.io/badge/License-GPL--3.0-ecebe6?labelColor=76feff&style=for-the-badge" alt="License GPL-3.0"/>
</p>

---

**Created by Yivani** - Client-only performance presets you can cycle with one key. Tune render/FPS options for **Build**, **Explore**, and **Combat**, then swap instantly in-game with a small top-center HUD toast.  

- **Default keybind**: `F9` (Cycle Performance Preset)  
- **Presets out of the box**: Build → Explore → Combat  
- **Environment**: Client only (works on servers without installing the mod)
- **License**: GNU General Public License v3.0  

---

## ✨ Features
- One-key preset cycling with a minimal toast (top-center, 4.5s)  
- Per-world/server persistence via JSON config  
- Advanced Preset Editor (CRUD, set default)  
- Options per preset include:  
  - Render Distance (chunks)  
  - Simulation Distance (chunks) [clamped to vanilla-safe min 5]  
  - Graphics: Fast / Fancy / Fabulous  
  - Particles: Minimal / Decreased / All  
  - Clouds: Off / Fast / Fancy  
  - Smooth Lighting (Ambient Occlusion) On/Off  
  - Entity Shadows On/Off  
  - Entity Distance Scaling  
  - Mipmap Levels  
  - Biome Blend Radius  
  - View Bobbing On/Off  
  - Distortion Effects Scale  
  - Max FPS (Unlimited at 260)  
  - VSync On/Off (applied on title screen for stability)  

<p align="center"><img src="https://i.imgur.com/eSeUBjO.png" alt="section divider"/></p>

## 🧩 Requirements

### ✅ Required
- Fabric Loader: `>= 0.17.2`  
- Minecraft: `1.21.x` (declared as `~1.21`)  
- Java: `>= 21`  
- Fabric API: any 1.21-compatible version  

### ⚙️ Optional
- [Mod Menu](https://modrinth.com/mod/modmenu): Adds a “Settings” button that opens the mod’s settings screen (your mod runs fine without it).  

📌 Notes:  
- Cloth Config isn’t required; the **Advanced Editor is built-in**.  
- The mod is **client-only**; no server installation needed.  

<p align="center"><img src="https://i.imgur.com/eSeUBjO.png" alt="section divider"/></p>

## 📸 Screenshots

**1) Settings screen (entry)**  
- Access the Advanced Editor  
- Reset to default presets (with confirmation dialog)  

![Settings](https://i.imgur.com/LdL3rWn.png)  

**2) Advanced Editor hub**  
- Add, Delete, Edit, and Make Default for your presets.  
- The current default preset is labeled.  

![Editor Hub](https://i.imgur.com/hd1HSnL.png)  

**3) Preset Editor (edit a profile)**  
- Fields for every supported option (Render/Simulation Distance, Graphics, Particles, Clouds, Smooth Lighting, Entity Shadows, Entity Distance Scaling, Mipmap Levels, Biome Blend, View Bobbing, Distortion, Max FPS, and VSync).  
- Save/Cancel buttons at the bottom.  

![Preset Editor](https://i.imgur.com/OcB6eQ2.png)  

<p align="center"><img src="https://i.imgur.com/eSeUBjO.png" alt="section divider"/></p>

## 🎮 Usage
- Press `F9` in-game to cycle: Build → Explore → Combat.  
- A toast shows which preset was applied.  
- Open the settings (via Mod Menu) to tweak or add new presets.  

<p align="center"><img src="https://i.imgur.com/eSeUBjO.png" alt="section divider"/></p>

## ⚖️ Stability
- Simulation Distance is clamped to a safe minimum (5) to avoid vanilla errors.  
- VSync and Fabulous graphics can cause GPU pipeline resets; to stay stable, VSync changes and Fabulous mode are applied on the title screen.  
- In-world, **Fabulous** is mapped to **Fancy** for safety.  
- Mipmap changes don’t force a mid-frame resource reload (avoids crashes). If needed, manually reload via Minecraft’s resource pack reload.  

<p align="center"><img src="https://i.imgur.com/eSeUBjO.png" alt="section divider"/></p>

## 📜 License
This project is licensed under the **GNU General Public License v3.0**.

### What this means:
- ✅ **Free to use** - Anyone can download and use this mod
- ✅ **Free to modify** - You can modify the source code for your own use
- ✅ **Free to distribute** - You can share the mod with others
- ❌ **Cannot be sold** - The mod cannot be sold commercially
- ❌ **Cannot be redistributed without source** - If you distribute it, you must include the source code
- ❌ **Must maintain attribution** - You must keep the original author credits

For the full license text, see the [LICENSE](LICENSE) file.  
