package dev.drygo.XSpawn.Utils;

import dev.drygo.XSpawn.Handlers.XSpawnCommand;
import dev.drygo.XSpawn.Handlers.XSpawnTabCompleter;
import dev.drygo.XSpawn.Listeners.SpawnListener;
import dev.drygo.XSpawn.Managers.ConfigManager;
import dev.drygo.XSpawn.Managers.SpawnManager;
import dev.drygo.XSpawn.XSpawn;
import org.bukkit.Bukkit;

public class LoadUtils {
    private final XSpawn plugin;
    private final ConfigManager configManager;
    private final SpawnManager spawnManager;
    private final ChatUtils chatUtils;

    public LoadUtils(XSpawn plugin, ConfigManager configManager, SpawnManager spawnManager, ChatUtils chatUtils) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.spawnManager = spawnManager;
        this.chatUtils = chatUtils;
    }

    public void loadFeatures() {
        loadFiles();
        loadCommand();
        loadListeners();
        loadXTeams();
    }

    public void loadFiles() {
        configManager.loadConfig();
        configManager.reloadMessages();
        configManager.setPrefix(configManager.getMessageConfig().getString("prefix"));
        spawnManager.loadSpawns();
    }
    private void loadCommand() {
        if (plugin.getCommand("xspawn") != null) {
            plugin.getLogger().info("✅ Plugin command /xspawn successfully registered.");
            plugin.getCommand("xspawn").setExecutor(new XSpawnCommand(chatUtils, this, spawnManager, plugin, configManager));
            plugin.getCommand("xspawn").setTabCompleter(new XSpawnTabCompleter(plugin));
        } else {
            plugin.getLogger().severe("❌ Error: /xspawn command is no registered in plugin.yml");
        }
    }

    private void loadListeners() {
        plugin.getServer().getPluginManager().registerEvents(new SpawnListener(spawnManager), plugin);
    }

    private void loadXTeams() {
        if (Bukkit.getPluginManager().getPlugin("xTeams") != null) {
            plugin.getLogger().info("✅ xTeams detected. xTeams hook successfully loaded.");
            plugin.workingXTeams = true;
        }
    }
}
