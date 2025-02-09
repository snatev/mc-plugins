<div align="center">

# 🔒 SecureLogin

**SecureLogin** is a lightweight and secure authentication plugin for Minecraft servers, adding an extra layer of protection to prevent unauthorized access.

</div>

<br><br>

## ✨ Features

- 🔑 **Player Authentication** - Players must log in with a password before playing.
- ❌ **Blacklist Failed Logins** - Repeated failed attempts will blacklist the player's IP.
- 📂 **Persistent Storage** - User credentials and settings are stored securely in a config file.
- 🚫 **Prevent Movement & Chat** - Blocks players from moving or chatting before logging in.
- 🔧 **Lightweight & Efficient** - Designed for smooth operation with minimal performance impact.
- 🛡️ **Auto Login with IP** - If a player logs in successfully, their IP is saved for faster authentication on future joins.

---

<br><br>

## ⚙️ How It Works

1. 🔹 New players must register using `/register <password>`.
2. 🔹 Returning players must log in with `/login <password>`.
3. 🔹 If a player logs in successfully, their IP is stored, allowing automatic login on their next join.
4. 🔹 Players cannot move, chat, or use commands until authenticated.
5. 🔹 If a player fails to log in 3 times, their IP gets blacklisted.

---

<br><br>

## 📜 Commands & Permissions

| Command | Description |
|---------|-------------|
| `/register <password>` | Registers a new player
| `/login <password>` | Logs in an existing player
| `/sec enable` | Enables registration
| `/sec disable` | Disables registration
| `/sec status` | Shows registration status

---

<br><br>

## ⚠️ Notes

- IP-based auto-login only works if the IP remains the same.
- Players who quit without logging in will have to log in again.
- To reset a player's password, remove their entry from the `users` section in `config.yml`.
