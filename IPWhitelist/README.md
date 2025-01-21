
<div align="center">

# 🌐 IPWhitelist

**IPWhitelist** is a lightweight plugin designed to restrict server access based on configurable allowed IP addresses. It ensures that only players with authorized IPs can join, offering an added layer of security.

</div>

<br><br>

## ✨ Features

- 🌐 **IP Whitelisting** Allow only specific IPs to join your server.
- 📜 **List Allowed IPs** Display the current list of whitelisted IPs.
- 🔄 **Reload Configuration** Update the whitelist without restarting the server.

---

<br><br>

## ⚙️ Configuration

The plugin generates a default `config.yml` file in the `plugins/IPWhitelist` directory upon first load.

<br>

### 🛠️ Default `config.yml`
```yaml
allowed-ips:
  - 127.0.0.1
deny-message: "Not Allowed IP"
```

### 🔧 Configuration Options
- **`allowed-ips`** List of IP addresses that are allowed to connect to the server.
- **`deny-message`** Message displayed to players when their IP is not whitelisted. Default is `"Not Allowed IP"`.

---

<br><br>

## 🛠️ Commands

### `/ipwl <reload|list>`
- 🔄 **`/ipwl reload`** Reload the plugin configuration from `config.yml`.
- 📜 **`/ipwl list`** Display the list of currently whitelisted IPs.

---

<br><br>

## 🛠️ How It Works

1. 🛠️ Add allowed IPs to the `config.yml` file under the `allowed-ips` section.
2. 🌐 The plugin checks the player's IP on login.
3. ❌ If the player's IP is not in the list, they are kicked with the configured `deny-message`.
4. ✅ Admins can reload the configuration or view the whitelist using commands.
