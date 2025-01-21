package snatev.xpoptimizer;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.entity.EntitySpawnEvent;

public class XPOptimizer extends JavaPlugin implements Listener {
    private boolean isEnabled = true;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("xpo").setTabCompleter(this);
        getCommand("xpo").setExecutor(this);
    }

    @EventHandler
    public void onXPOrbSpawn(EntitySpawnEvent event) {
        if (!isEnabled) return;

        if (event.getEntity() instanceof ExperienceOrb) {
            ExperienceOrb newOrb = (ExperienceOrb) event.getEntity();

            new BukkitRunnable() {
                @Override
                public void run() { mergeNearbyXPOrbs(newOrb); }
            }.runTaskLater(this, 1L);
        }
    }

    private void mergeNearbyXPOrbs(ExperienceOrb baseOrb) {
        if (!baseOrb.isValid()) return;

        Location location = baseOrb.getLocation();
        int totalXP = baseOrb.getExperience();

        List<ExperienceOrb> nearbyOrbs = baseOrb.getWorld().getNearbyEntities(location, 3, 3, 3).stream()
            .filter(entity -> entity instanceof ExperienceOrb).map(entity -> (ExperienceOrb) entity)
            .filter(orb -> !orb.equals(baseOrb) && orb.isValid()).toList();

        for (ExperienceOrb orb : nearbyOrbs) {
            totalXP += orb.getExperience();
            orb.remove();
        }

        baseOrb.setExperience(totalXP);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("xpo")) {
            if (!sender.isOp()) { sender.sendMessage("§c<XPO> Insufficient Permissions"); return true; }
            if (args.length == 0) { sender.sendMessage("§c<XPO> /xpo <enable|disable|status>"); return true; }

            switch (args[0].toLowerCase()) {
                case "enable": sender.sendMessage("§a<XPO> Enabled"); isEnabled = true; break;
                case "disable": sender.sendMessage("§e<XPO> Disabled"); isEnabled = false; break;
                case "status": sender.sendMessage("§b<XPO> Is " + (isEnabled ? "Enabled" : "Disabled")); break;
                default: sender.sendMessage("§c<XPO> /xpo <enable|disable|status>"); break;
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
        }

        return completions;
    }
}
