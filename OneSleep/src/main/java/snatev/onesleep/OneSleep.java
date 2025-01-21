package snatev.onesleep;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class OneSleep extends JavaPlugin implements Listener, TabExecutor {
    private boolean isEnabled = true;
    private boolean resetPhantomForAll = true;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("osl").setTabCompleter(this);
        getCommand("osl").setExecutor(this);
    }

    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        if (!isEnabled || event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        Player player = event.getPlayer();
        World world = player.getWorld();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (world.getTime() >= 12541 && world.getTime() <= 23458) {
                world.setThundering(false);
                world.setStorm(false);
                world.setTime(0);

                if (resetPhantomForAll) {
                    for (Player p : world.getPlayers()) {
                        p.setStatistic(org.bukkit.Statistic.TIME_SINCE_REST, 0); }
                } else player.setStatistic(org.bukkit.Statistic.TIME_SINCE_REST, 0);
            }
        }, 5L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("osl")) {
            if (!sender.isOp()) { sender.sendMessage("§c<OSL> Insufficient Permissions"); return true; }
            if (args.length == 0) { sender.sendMessage("§c<OSL> /osl <enable|disable|status|phantom>"); return true; }

            switch (args[0].toLowerCase()) {
                case "enable": sender.sendMessage("§a<OSL> Enabled"); isEnabled = true; break;
                case "disable": sender.sendMessage("§e<OSL> Disabled"); isEnabled = false; break;
                case "status": sender.sendMessage("§b<OSL> Is " + (isEnabled ? "Enabled" : "Disabled")); break;

                case "phantom":
                    if (args.length < 2) { sender.sendMessage("§c<OSL> /osl phantom <enable|disable|status>"); return true; }

                    switch (args[1].toLowerCase()) {
                        case "enable": sender.sendMessage("§a<OSL> Phantom Reset Enabled"); resetPhantomForAll = true; break;
                        case "disable": sender.sendMessage("§e<OSL> Phantom Reset Disabled"); resetPhantomForAll = false; break;
                        case "status": sender.sendMessage("§b<OSL> Phantom Reset Is " + (resetPhantomForAll ? "Enabled" : "Disabled")); break;
                        default: sender.sendMessage("§c<OSL> /osl phantom <enable|disable|status>"); break;
                    }

                    break;
                default: sender.sendMessage("§c<OSL> /osl <enable|disable|status|phantom>"); break;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("status");
            completions.add("enable");
            completions.add("disable");
            completions.add("phantom");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("phantom")) {
            completions.add("status");
            completions.add("enable");
            completions.add("disable");
        }

        return completions;
    }
}
