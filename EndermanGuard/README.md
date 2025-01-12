
<div align="center">

# 🛡️ EndermanGuard

**EndermanGuard** is a plugin designed to address an Enderman bug where they teleport onto blocks they shouldn't. This allows server administrators to restrict Enderman teleportation to specific blocks or within specific worlds, ensuring gameplay balance and stability.

</div>

<br><br>

## ✨ Features

- 🌍 **World-Based Restrictions** Allow Enderman teleportation control on a per-world basis.
- 🚫 **Block-Specific Protection** Remove Endermen when they teleport onto restricted blocks.
- ✅ **Enable/Disable Functionality** Flexible commands to enable or disable the plugin as needed.
- 🔄 **Dynamic Configuration Reload** Reload settings from the configuration without restarting the server.

---

<br><br>

## ⚙️ Configuration

The plugin generates a default `config.yml` file in the `plugins/EndermanGuard` directory upon first load.

<br>

### 🛠️ Default `config.yml`
```yaml
allowed-worlds:
  - world
  - world_nether

kill-blocks:
  - diamond_block
  - emerald_block
```

### 🔧 Configuration Options
- **`allowed-worlds`** A list of worlds where EndermanGuard is active.
- **`kill-blocks`** A list of block types where Endermen are not allowed to teleport.

---

<br><br>

## 🛠️ Commands

### `/eg <enable|disable|status|reload>`
- ✅ **`/eg enable`** Enable the EndermanGuard functionality.
- 🚫 **`/eg disable`** Disable the EndermanGuard functionality.
- 🟢 **`/eg status`** Check whether EndermanGuard is currently enabled or disabled.
- 🔄 **`/eg reload`** Reload the configuration settings.

---

<br><br>

## 🛠️ How It Works

1. 🕵️ **Enderman Teleportation:** Monitors Enderman teleportation events in real-time.
2. ⚙️ **Customizable Settings:** Configure restricted blocks and worlds via the plugin's configuration file.
3. 🚨 **Preventative Action:** Removes Endermen that teleport to restricted blocks or into disallowed worlds.
4. 📜 **Configuration Rules:** Checks if the destination block or world is restricted based on the configuration.
