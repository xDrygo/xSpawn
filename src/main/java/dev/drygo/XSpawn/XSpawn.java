package dev.drygo.XSpawn;

import dev.drygo.XSpawn.API.XSpawnAPI;
import dev.drygo.XSpawn.Managers.ConfigManager;
import dev.drygo.XSpawn.Managers.SpawnManager;
import dev.drygo.XSpawn.Utils.ChatUtils;
import dev.drygo.XSpawn.Utils.LoadUtils;
import dev.drygo.XSpawn.Utils.LogsUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class XSpawn extends JavaPlugin {
    public String prefix;
    public String version;
    public boolean workingXTeams;
    private LogsUtils logsUtils;

    @Override
    public void onEnable() {
        version = getDescription().getVersion();
        workingXTeams = false;
        this.logsUtils = new LogsUtils(this);
        ConfigManager configManager = new ConfigManager(this);
        ChatUtils chatUtils = new ChatUtils(configManager, this);
        SpawnManager spawnManager = new SpawnManager(this);
        LoadUtils loadUtils = new LoadUtils(this, configManager, spawnManager, chatUtils);
        XSpawnAPI.init(spawnManager);

        loadUtils.loadFeatures();
        logsUtils.sendStartupMessage();
    }

    @Override
    public void onDisable() {
        logsUtils.sendShutdownMessage();
    }

    public boolean isWorkingXTeams() {
        return workingXTeams;
    }
}
