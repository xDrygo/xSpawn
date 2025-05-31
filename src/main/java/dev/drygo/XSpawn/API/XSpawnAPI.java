package dev.drygo.XSpawn.API;

import dev.drygo.XSpawn.Managers.SpawnManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.eldrygo.XTeams.Models.Team;

public class XSpawnAPI {

    private static SpawnManager spawnManager;

    public static void init(SpawnManager manager) {
        spawnManager = manager;
    }

    public static void setFirstSpawn(Location location) {
        spawnManager.setFirstSpawn(location);
    }

    public static Location getFirstSpawn() {
        return spawnManager.getFirstSpawn();
    }

    public static void removeFirstSpawn() {
        spawnManager.removeFirstSpawn();
    }

    public static void setPlayerSpawn(String playerName, Location location) {
        spawnManager.setPlayerSpawn(playerName, location);
    }

    public static Location getPlayerSpawn(String playerName) {
        return spawnManager.getPlayerSpawn(playerName);
    }

    public static void removePlayerSpawn(String playerName) {
        spawnManager.removePlayerSpawn(playerName);
    }

    public static void setTeamSpawn(Team team, Location location) {
        spawnManager.setTeamSpawn(team, location);
    }

    public static Location getTeamSpawn(Team team) {
        return spawnManager.getTeamSpawn(team);
    }

    public static void removeTeamSpawn(Team team) {
        spawnManager.removeTeamSpawn(team);
    }

    public static Location getSpawnFor(Player player) {
        return spawnManager.getSpawnFor(player);
    }
}
