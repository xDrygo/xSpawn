package dev.drygo.XSpawn.Managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.drygo.XSpawn.XSpawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.eldrygo.XTeams.API.XTeamsAPI;
import org.eldrygo.XTeams.Models.Team;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SpawnManager {

    private final XSpawn plugin;
    private final File spawnFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private JsonObject spawnsData;

    public SpawnManager(XSpawn plugin) {
        this.plugin = plugin;
        this.spawnFile = new File(plugin.getDataFolder(), "data/spawns.json");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
    }

    public void loadSpawns() {
        if (!spawnFile.exists()) {
            spawnsData = new JsonObject();
            spawnsData.add("first", null);
            spawnsData.add("team", new JsonObject());
            spawnsData.add("player", new JsonObject());
            saveSpawns();
        } else {
            try (FileReader reader = new FileReader(spawnFile)) {
                spawnsData = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed on loading spawns.json");
                e.printStackTrace();
            }
        }
    }

    public void saveSpawns() {
        try (FileWriter writer = new FileWriter(spawnFile)) {
            gson.toJson(spawnsData, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed on saving spawns.json");
            e.printStackTrace();
        }
    }

    public void setFirstSpawn(Location loc) {
        spawnsData.add("first", locToJson(loc));
        saveSpawns();
    }

    public void setPlayerSpawn(String playerName, Location loc) {
        JsonObject playerSpawns = spawnsData.getAsJsonObject("player");
        playerSpawns.add(playerName, locToJson(loc));
        saveSpawns();
    }

    public void setTeamSpawn(Team team, Location loc) {
        JsonObject teamSpawns = spawnsData.getAsJsonObject("team");
        teamSpawns.add(team.getName(), locToJson(loc));
        saveSpawns();
    }

    public Location getSpawnFor(Player player) {
        JsonObject playerSpawns = spawnsData.getAsJsonObject("player");
        if (playerSpawns.has(player.getName())) {
            return jsonToLoc(playerSpawns.getAsJsonObject(player.getName()));
        }

        JsonObject teamSpawns = spawnsData.getAsJsonObject("team");
        List<Team> teams = XTeamsAPI.getPlayerTeams(player.getName());
        if (teams != null && !teams.isEmpty()) {
            for (Team team : teams) {
                String teamName = team.getName();
                if (teamSpawns.has(teamName)) {
                    return jsonToLoc(teamSpawns.getAsJsonObject(teamName));
                }
            }
        }

        if (spawnsData.has("first") && !spawnsData.get("first").isJsonNull()) {
            return jsonToLoc(spawnsData.getAsJsonObject("first"));
        }

        return null;
    }
    public Location getFirstSpawn() {
        if (spawnsData.has("first") && !spawnsData.get("first").isJsonNull()) {
            return jsonToLoc(spawnsData.getAsJsonObject("first"));
        }
        return null;
    }

    public Location getTeamSpawn(Team team) {
        JsonObject teams = spawnsData.getAsJsonObject("team");
        if (teams.has(team.getName())) {
            return jsonToLoc(teams.getAsJsonObject(team.getName()));
        }
        return null;
    }

    public Location getPlayerSpawn(String playerName) {
        JsonObject players = spawnsData.getAsJsonObject("player");
        if (players.has(playerName)) {
            return jsonToLoc(players.getAsJsonObject(playerName));
        }
        return null;
    }

    public void removeFirstSpawn() {
        spawnsData.add("first", null);
        saveSpawns();
    }

    public void removeTeamSpawn(Team team) {
        spawnsData.getAsJsonObject("team").remove(team.getName());
        saveSpawns();
    }

    public void removePlayerSpawn(String playerName) {
        spawnsData.getAsJsonObject("player").remove(playerName);
        saveSpawns();
    }

    private JsonObject locToJson(Location loc) {
        JsonObject obj = new JsonObject();
        obj.addProperty("world", loc.getWorld().getName());
        obj.addProperty("x", loc.getX());
        obj.addProperty("y", loc.getY());
        obj.addProperty("z", loc.getZ());
        obj.addProperty("yaw", loc.getYaw());
        obj.addProperty("pitch", loc.getPitch());
        return obj;
    }

    private Location jsonToLoc(JsonObject obj) {
        World world = Bukkit.getWorld(obj.get("world").getAsString());
        double x = obj.get("x").getAsDouble();
        double y = obj.get("y").getAsDouble();
        double z = obj.get("z").getAsDouble();
        float yaw = obj.get("yaw").getAsFloat();
        float pitch = obj.get("pitch").getAsFloat();
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static String locToString(Location loc, boolean includeWorld) {
        if (loc == null) return "null";

        String base = String.format(
                "%.2f, %.2f, %.2f, [%.2f, %.2f]",
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getYaw(),
                loc.getPitch()
        );

        return includeWorld ? loc.getWorld().getName() + ": " + base : base;
    }
}
