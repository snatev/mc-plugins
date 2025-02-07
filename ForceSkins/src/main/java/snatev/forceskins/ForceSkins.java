package snatev.forceskins;

import java.net.URL;
import java.util.UUID;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerJoinEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

public class ForceSkins extends JavaPlugin implements Listener {
    @Override
    public void onEnable() { Bukkit.getPluginManager().registerEvents(this, this); }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        UUID uuid = getMojangUUID(player.getName());
        if (uuid != null) applyMojangSkin(player, uuid);
    }

    private UUID getMojangUUID(String playerName) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());

                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                return UUID.fromString(json.get("id").getAsString().replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void applyMojangSkin(Player player, UUID mojangUUID) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + mojangUUID + "?unsigned=false");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());

                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject properties = json.getAsJsonArray("properties").get(0).getAsJsonObject();

                String value = properties.get("value").getAsString();
                String signature = properties.get("signature").getAsString();

                PlayerProfile profile = player.getPlayerProfile();
                profile.setProperty(new ProfileProperty("textures", value, signature));

                player.setPlayerProfile(profile);
            }
        } catch (Exception ignored) {}
    }
}
