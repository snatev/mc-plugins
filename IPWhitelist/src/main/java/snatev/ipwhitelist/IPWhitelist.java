package snatev.ipwhitelist;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class IPWhitelist extends JavaPlugin implements Listener, TabExecutor {
    private String denyMessage;
    private List<String> allowedIPs;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("ipwl").setTabCompleter(this);
        getCommand("ipwl").setExecutor(this);
        getLogger().info("<IPWL> Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("<IPWL> Disabled");
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        allowedIPs = config.getStringList("allowed-ips");
        denyMessage = config.getString("deny-message", "Not Allowed IP");
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String playerIP = event.getAddress().getHostAddress();

        if (!allowedIPs.contains(playerIP)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, denyMessage);
            getLogger().warning("Blocked Connection From " + playerIP);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("ipwl")) return false;

        if (!sender.isOp()) {
            sender.sendMessage("§c<IPWL> Insufficient Permissions");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§c<IPWL> /ipwl <reload|list>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                reloadConfig();
                loadConfig();
                sender.sendMessage("§a<IPWL> Configuration Reloaded");
                break;
            case "list":
                sender.sendMessage("§a<IPWL> Allowed IPs");
                allowedIPs.forEach(ip -> sender.sendMessage(" - §b" + ip));
                break;
            default:
                sender.sendMessage("§c<IPWL> /ipwl <reload|list>");
        }

        return true;
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
