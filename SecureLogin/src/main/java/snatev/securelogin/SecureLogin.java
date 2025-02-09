package snatev.securelogin;

import java.util.*;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;
import java.security.NoSuchAlgorithmException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class SecureLogin extends JavaPlugin implements Listener, CommandExecutor {
    private final Set<UUID> loggedInUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<UUID, String> userPasswords = new ConcurrentHashMap<>();
    private final Map<String, Integer> failedAttempts = new HashMap<>();
    private final Map<UUID, String> lastLoginIPs = new HashMap<>();

    private final Set<String> blacklistedIPs = new HashSet<>();
    private boolean registerEnabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("register").setExecutor(this);
        getCommand("login").setExecutor(this);

        getCommand("sec").setTabCompleter(this);
        getCommand("sec").setExecutor(this);
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("§c<SEC> Server Reloaded. Please Rejoin.");
        }
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        blacklistedIPs.addAll(config.getStringList("blacklisted-ips"));
        registerEnabled = config.getBoolean("register-enabled", false);

        if (config.contains("users")) {
            for (String uuid : config.getConfigurationSection("users").getKeys(false)) {
                userPasswords.put(UUID.fromString(uuid), config.getString("users." + uuid));
            }
        }

        if (config.contains("last-ips")) {
            for (String uuid : config.getConfigurationSection("last-ips").getKeys(false)) {
                String ip = config.getString("last-ips." + uuid);
                if (ip != null) lastLoginIPs.put(UUID.fromString(uuid), ip);
            }
        }
    }


    private void saveConfigState() {
        getConfig().set("blacklisted-ips", new ArrayList<>(blacklistedIPs));
        getConfig().set("register-enabled", registerEnabled);

        for (Map.Entry<UUID, String> entry : lastLoginIPs.entrySet()) {
            getConfig().set("last-ips." + entry.getKey().toString(), entry.getValue());
        }

        saveConfig();
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        String ip = event.getAddress().getHostAddress();
        UUID uuid = event.getUniqueId();
        String name = event.getName();

        if (blacklistedIPs.contains(ip)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§c<SEC> You Are Blacklisted");
            return;
        }

        if (!registerEnabled && !userPasswords.containsKey(uuid)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§c<SEC> Registration Is Disabled");
            return;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String currentIP = player.getAddress().getHostString();

        player.setInvulnerable(true);

        if (userPasswords.containsKey(uuid)) {
            if (lastLoginIPs.containsKey(uuid) && lastLoginIPs.get(uuid).equals(currentIP)) {
                loggedInUsers.add(uuid);
                player.setInvulnerable(false);
                player.sendMessage("§a<SEC> Auto-Logged In (IP Matched)");
            } else player.sendMessage("§e<SEC> Login With: /login <password>");
        } else if (registerEnabled) player.sendMessage("§e<SEC> Register With: /register <password>");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!loggedInUsers.contains(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!loggedInUsers.contains(player.getUniqueId())) {
            event.setCancelled(true); player.sendMessage("§c<SEC> Use /login Or /register First");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("sec")) {
            if (!sender.isOp()) { sender.sendMessage("§c<SEC> Insufficient Permissions"); return true; }
            if (args.length == 0) { sender.sendMessage("§c<SEC> /sec <enable|disable|status>"); return true; }

            switch (args[0].toLowerCase()) {
                case "enable":
                    sender.sendMessage("§a<SEC> Registration Enabled");
                    registerEnabled = true; saveConfigState(); break;
                case "disable":
                    sender.sendMessage("§e<SEC> Registration Disabled");
                    registerEnabled = false; saveConfigState(); break;
                case "status":  sender.sendMessage("§b<SEC> Registration Is " + (registerEnabled ? "Enabled" : "Disabled")); break;
                default:  sender.sendMessage("§c<SEC> /sec <enable|disable|status>"); break;
            } return true;
        }

        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (command.getName().equalsIgnoreCase("register")) {
            if (!registerEnabled) { player.sendMessage("§c<SEC> Registration Is Disabled"); return true; }
            if (userPasswords.containsKey(uuid)) { player.sendMessage("§c<SEC> Already Registered"); return true; }
            if (args.length != 1) { player.sendMessage("§c<SEC> Register With: /register <password>"); return true; }

            String hashedPassword = hashPassword(args[0]);
            userPasswords.put(uuid, hashedPassword);
            loggedInUsers.add(uuid);

            getConfig().set("users." + uuid.toString(), hashedPassword);
            saveConfig();

            player.sendMessage("§a<SEC> Successful Registration");
            player.setInvulnerable(false);
            return true;
        }

        if (command.getName().equalsIgnoreCase("login")) {
            if (!userPasswords.containsKey(uuid)) { player.sendMessage("§c<SEC> Not Registered Yet"); return true; }
            if (args.length != 1) { player.sendMessage("§c<SEC> Login With: /login <password>"); return true; }

            String storedPassword = userPasswords.get(uuid);
            String playerIP = player.getAddress().getHostString();

            if (storedPassword.equals(hashPassword(args[0]))) {
                player.sendMessage("§a<SEC> Successful Login");
                failedAttempts.remove(playerIP);
                player.setInvulnerable(false);
                loggedInUsers.add(uuid);

                lastLoginIPs.put(uuid, playerIP);
                saveConfigState();
            } else {
                int attempts = failedAttempts.getOrDefault(playerIP, 0) + 1;
                failedAttempts.put(playerIP, attempts);

                if (attempts >= 3) {
                    blacklistedIPs.add(playerIP);
                    saveConfigState();

                    player.kickPlayer("§c<SEC> Too Many Failed Attempts");
                } else player.sendMessage("§c<SEC> Incorrect Password - Attempt " + attempts + "/3");
            } return true;
        }

        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) { loggedInUsers.remove(event.getPlayer().getUniqueId()); }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) { throw new RuntimeException("Error Hashing Password", e); }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("sec")) {
            if (args.length == 1) {
                completions.add("enable");
                completions.add("disable");
                completions.add("status");
            }
        }

        return completions;
    }
}
