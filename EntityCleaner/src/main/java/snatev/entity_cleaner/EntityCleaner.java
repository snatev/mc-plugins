package snatev.entity_cleaner;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Entity;
import org.bukkit.command.Command;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityCleaner extends JavaPlugin {
    private BukkitTask cleanupTask;

    private long minItemAge;
    private long cleanupInterval;
    private boolean isPaused = false;
    private boolean isBroadcastEnabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        minItemAge = getConfig().getLong("minimum-item-age", 3000);
        cleanupInterval = getConfig().getLong("cleanup-interval", 6000);
        isBroadcastEnabled = getConfig().getBoolean("broadcast-enabled", false);

        startCleanupTask();
        getLogger().info("<EC> Enabled");
    }

    @Override
    public void onDisable() {
        if (cleanupTask != null) cleanupTask.cancel();
        getLogger().info("<EC> Disabled");
    }

    private void startCleanupTask() {
        cleanupTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (!isPaused) {
                if (isBroadcastEnabled) Bukkit.broadcastMessage("§e<EC> Cleanup In 10 Seconds");

                Bukkit.getScheduler().runTaskLater(this, () -> {
                    if (!isPaused) {
                        for (World world : Bukkit.getWorlds()) {
                            for (Entity entity : world.getEntities()) {
                                if (entity instanceof Item) {
                                    Item item = (Item) entity;
                                    if (item.getTicksLived() > minItemAge) item.remove();
                                }
                            }
                        }

                        if (isBroadcastEnabled) Bukkit.broadcastMessage("§a<EC> Cleanup Completed");
                    }
                }, 200L);
            }
        }, 0L, cleanupInterval);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ec")) {
            if (!sender.isOp()) {
                sender.sendMessage("§c<EC> Insufficient Permissions");
                return true;
            }

            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "pause": sender.sendMessage("§e<EC> Paused"); isPaused = true; break;
                    case "resume": sender.sendMessage("§a<EC> Resumed"); isPaused = false; break;
                    case "status":  sender.sendMessage("§b<EC> Is " + (isPaused ? "Paused" : "Running")); break;
                    case "broadcast":
                        if (args.length > 1) {
                            switch (args[1].toLowerCase()) {
                                case "enable": sender.sendMessage("§a<EC> Broadcast Enabled"); isBroadcastEnabled = true; break;
                                case "disable": sender.sendMessage("§e<EC> Broadcast Disabled"); isBroadcastEnabled = false; break;
                                case "status": sender.sendMessage("§b<EC> Broadcast Is " + (isBroadcastEnabled ? "Enabled" : "Disabled")); break;
                                default: sender.sendMessage("§c/ec broadcast <enable|disable|status>");
                            }
                        } else sender.sendMessage("§c/ec broadcast <enable|disable|status>");
                        break;
                    case "reload":
                        reloadConfig();
                        sender.sendMessage("§a<EC> Configuration Reloaded");
                        minItemAge = getConfig().getLong("minimum-item-age", 3000);
                        cleanupInterval = getConfig().getLong("cleanup-interval", 6000);
                        isBroadcastEnabled = getConfig().getBoolean("broadcast-enabled", false); break;
                    default: sender.sendMessage("§c/ec <pause|resume|status|broadcast|reload>");
                }
                return true;
            }

            sender.sendMessage("§c/ec <pause|resume|status|broadcast|reload>");
            return true;
        }

        return false;
    }
}
