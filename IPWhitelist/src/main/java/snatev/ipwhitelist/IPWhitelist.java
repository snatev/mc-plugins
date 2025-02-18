package snatev.ipwhitelist;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.configuration.file.FileConfiguration;

public class IPWhitelist extends JavaPlugin implements Listener, TabExecutor {
    private List<String> allowedIPs;
    private String denyMessage;
    private boolean isEnabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("ipw").setTabCompleter(this);
        getCommand("ipw").setExecutor(this);
    }

    private void saveConfigState() {
        getConfig().set("enabled", isEnabled);
        saveConfig();
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        isEnabled = config.getBoolean("enabled", true);
        allowedIPs = config.getStringList("allowed-ips");
        denyMessage = config.getString("deny-message", "Not Allowed IP");
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (!isEnabled) return;

        String playerIP = event.getAddress().getHostAddress();
        if (!allowedIPs.contains(playerIP)) event.disallow(PlayerLoginEvent.Result.KICK_OTHER, denyMessage);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ipw")) {
            if (!sender.isOp()) { sender.sendMessage("§c<IPW> Insufficient Permissions"); return true; }
            if (args.length == 0) { sender.sendMessage("§c<IPW> /ipw <reload|list>"); return true; }

            switch (args[0].toLowerCase()) {
                case "enable":
                    sender.sendMessage("§a<IPW> Enabled");
                    isEnabled = true; saveConfigState(); break;
                case "disable":
                    sender.sendMessage("§e<IPW> Disabled");
                    isEnabled = false; saveConfigState(); break;
                case "status":
                    sender.sendMessage("§b<IPW> Is " + (isEnabled ? "Enabled" : "Disabled"));
                    break;
                case "reload":
                    reloadConfig(); loadConfig();
                    sender.sendMessage("§a<IPW> Configuration Reloaded");
                    break;
                case "list":
                    allowedIPs.forEach(ip -> sender.sendMessage(" - §b" + ip));
                    sender.sendMessage("§a<IPW> Allowed IPs"); break;

                default: sender.sendMessage("§c<IPW> /ipw <reload|list>"); break;
            }

            return true;
        } return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("reload");
            completions.add("list");
        }

        return completions.stream()
            .filter(c -> c.toLowerCase().startsWith(args[0].toLowerCase()))
            .collect(Collectors.toList());
    }
}
