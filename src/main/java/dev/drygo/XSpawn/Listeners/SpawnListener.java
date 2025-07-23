package dev.drygo.XSpawn.Listeners;

import dev.drygo.XSpawn.Managers.SpawnManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnListener implements Listener {

    private final SpawnManager spawnManager;

    public SpawnListener(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location spawn = spawnManager.getFirstSpawn();
        if (player.hasPlayedBefore()) return;
        if (spawn != null) {
            player.teleport(spawn);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location spawn = spawnManager.getSpawnFor(player);
        if (spawn != null) {
            event.setRespawnLocation(spawn);
        }
    }
}
