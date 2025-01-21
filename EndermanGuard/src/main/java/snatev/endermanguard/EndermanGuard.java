package snatev.endermanguard;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.configuration.file.FileConfiguration;

public class EndermanGuard extends JavaPlugin implements Listener {
    private Set<String> allowedWorlds = new HashSet<>();
    private Set<Material> killBlocks = new HashSet<>();
    private boolean isEnabled = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("eg").setTabCompleter(this);
        getCommand("eg").setExecutor(this);
    }

    @EventHandler
    public void onEntityTeleport(EntityTeleportEvent event) {
        if (!isEnabled) return;

        if (event.getEntity() instanceof Enderman enderman) {
            World world = enderman.getWorld();
            if (!allowedWorlds.contains(world.getName())) return;

            new BukkitRunnable() {
                @Override
                public void run() {
                    Block block = event.getTo().getBlock();
                    Material blockType = block.getType();
                    if (killBlocks.contains(blockType)) enderman.remove();
                }
            }.runTaskLater(this, 1L);
        }
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();

        allowedWorlds.clear();
        List<String> worldList = config.getStringList("allowed-worlds");
        if (!worldList.isEmpty()) allowedWorlds.addAll(worldList);

        killBlocks.clear();
        List<String> blockList = config.getStringList("kill-blocks");

        if (!blockList.isEmpty()) {
            for (String blockName : blockList) {
                try {
                    Material material = Material.valueOf(blockName.toUpperCase());
                    killBlocks.add(material);
                } catch (IllegalArgumentException e) { getLogger().warning("<EMG> Invalid Block " + blockName); }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("eg")) {
            if (!sender.isOp()) { sender.sendMessage("§c<EMG> Insufficient Permissions"); return true; }
            if (args.length == 0) { sender.sendMessage("§c/emg <enable|disable|status|reload>"); return true; }

            switch (args[0].toLowerCase()) {
                case "enable": sender.sendMessage("§a<EMG> Enabled"); isEnabled = true; break;
                case "disable": sender.sendMessage("§e<EMG> Disabled"); isEnabled = false; break;
                case "status": sender.sendMessage("§b<EMG> Is " + (isEnabled ? "Enabled" : "Disabled")); break;
                case "reload":
                    sender.sendMessage("§a<EMG> Configuration Reloaded");
                    reloadConfig(); loadConfig(); break;
                default: sender.sendMessage("§c<EMG> /emg <enable|disable|status|reload>"); break;
            }

            return true;
        } return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("enable");
            completions.add("disable");
            completions.add("status");
            completions.add("reload");
        }

        return completions;
    }
}
