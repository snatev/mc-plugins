package snatev.entityprotector;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EntityProtector extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("<EP> Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("<EP> Disabled");
    }

    @EventHandler
    public void onEndermanBlockPickup(EntityChangeBlockEvent event) {
        if (event.getEntity().getType().toString().equalsIgnoreCase("ENDERMAN")) event.setCancelled(true);
    }

    @EventHandler
    public void onCreeperExplosion(EntityExplodeEvent event) {
        if (event.getEntity().getType().toString().equalsIgnoreCase("CREEPER")) event.setCancelled(true);
    }
}
