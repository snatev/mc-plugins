
<div align="center">

# 🌟 EntityCleaner

**EntityCleaner** is a Minecraft server plugin designed to keep your worlds clean by periodically removing dropped items (entities) based on configurable criteria. The plugin provides flexible control over cleanup intervals, broadcasting cleanup warnings, and pausing or resuming the task as needed.

</div>

<br>

## ✨ Features

- ⏸️ **Pause and Resume** Temporarily pause or resume the cleanup task.
- 🔄 **Reload Configuration** Reload settings without restarting the server.
- 🗑️ **Automatic Cleanup** Periodically removes dropped items after a specified age.
- 📢 **Broadcast Messages** Warn players about cleanup events and notify them when the cleanup is complete.

<br>

## ⚙️ Configuration

The plugin generates a default `config.yml` file in the `plugins/EntityCleaner` directory upon first load.

### 🛠️ Default `config.yml`
```yaml
cleanup-interval: 6000
minimum-item-age: 3000
broadcast-enabled: false
```

### 🔧 Configuration Options
- **`cleanup-interval`** Interval (in ticks) between each cleanup task. Default is `6000` ticks (5 minutes).
- **`minimum-item-age`** Minimum age (in ticks) an item must have before being eligible for removal. Default is `3000` ticks (2.5 minutes).
- **`broadcast-enabled`** Whether to display cleanup warnings and completion messages to players. Default is `false`.

<br>

## 🛠️ Commands

### `/ec <pause|resume|status|reload|broadcast>`
- ⏸️ **`/ec pause`** Pause the cleanup task.
- ▶️ **`/ec resume`** Resume the cleanup task.
- 🟢 **`/ec status`** Display whether the cleanup task is paused or running.
- 🔄 **`/ec reload`** Reload the plugin configuration.
- 📢 **`/ec broadcast`** Enable, disable, or check the status of broadcast messages.

### `/ec broadcast <enable|disable|status>`
- 📢 **`/ec broadcast enable`** Enable broadcasting of cleanup messages
- 📢 **`/ec broadcast disable`** Disable broadcasting of cleanup messages
- 📢 **`/ec broadcast status`** Check the current status of broadcast messages

<br>

## 🛠️ How It Works

1. 🕒 The plugin schedules a periodic task to clean up dropped items.
2. 📢 If broadcasting is enabled, it warns players 10 seconds before cleanup starts.
3. 🗑️ The plugin removes all dropped items older than the configured `minimum-item-age`.
4. ✅ Completion messages are displayed to players if broadcasting is enabled.
5. 🛠️ Commands allow for real-time control of the cleanup process.
