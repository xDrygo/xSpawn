package dev.drygo.XSpawn.Handlers;

import dev.drygo.XSpawn.Managers.ConfigManager;
import dev.drygo.XSpawn.Managers.SpawnManager;
import dev.drygo.XSpawn.Utils.ChatUtils;
import dev.drygo.XSpawn.Utils.LoadUtils;
import dev.drygo.XSpawn.XSpawn;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.drygo.XTeams.API.XTeamsAPI;
import dev.drygo.XTeams.Models.Team;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class  XSpawnCommand implements CommandExecutor {
    private final ChatUtils chatUtils;
    private final LoadUtils loadUtils;
    private final SpawnManager spawnManager;
    private final XSpawn plugin;
    private final ConfigManager configManager;

    public XSpawnCommand(ChatUtils chatUtils, LoadUtils loadUtils, SpawnManager spawnManager, XSpawn plugin, ConfigManager configManager) {
        this.chatUtils = chatUtils;
        this.loadUtils = loadUtils;
        this.spawnManager = spawnManager;
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length < 1) {
            sender.sendMessage(chatUtils.getMessage("error.unknown_command", null));
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "set" -> handleSet(sender, args, label);
            case "tp" -> handleTP(sender, args, label);
            case "del" -> handleDel(sender, args, label);
            case "reload" -> handleReload(sender, label, args);
            case "help" -> {
                if (!sender.hasPermission("xspawn.command.help") && !sender.hasPermission("xspawn.admin") && !sender.isOp()) {
                    sender.sendMessage(chatUtils.getMessage("error.no_permission", null)
                            .replace("%command%", label + " " + String.join(" ", args)));
                    return true;
                }
                List<String> helpMessage = configManager.getMessageConfig().getStringList("command.help");
                for (String line : helpMessage) {
                    sender.sendMessage(ChatUtils.formatColor(line));
                }
            }
            default -> sender.sendMessage(chatUtils.getMessage("error.unknown_command", null));
        }
        return false;
    }

    private void handleSet(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(chatUtils.getMessage("error.only_player", null));
            return;
        }
        if (!sender.hasPermission("xspawn.command.set") && !sender.hasPermission("xspawn.admin") && !sender.isOp()) {
            sender.sendMessage(chatUtils.getMessage("error.no_permission", null)
                    .replace("%command%", label + (args.length > 0 ? " " + String.join(" ", args) : "")));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(chatUtils.getMessage("error.invalid_type", null));
            return;
        }

        String type = args[1];
        Location l = player.getLocation();

        switch (type) {
            case "first" -> {
                spawnManager.setFirstSpawn(l);
                sender.sendMessage(chatUtils.getMessage("command.set.first.success", null)
                        .replace("%location%", SpawnManager.locToString(l, true)));
            }
            case "team" -> {
                if (!plugin.isWorkingXTeams()) {
                    sender.sendMessage(chatUtils.getMessage("error.xteams_not_loaded", null));
                    return;
                }

                if (args.length < 3) {
                    sender.sendMessage(chatUtils.getMessage("error.team_not_specified", null));
                    return;
                }
                Team team = XTeamsAPI.getTeam(args[2]);
                if (team == null) {
                    sender.sendMessage(chatUtils.getMessage("error.invalid_team", null)
                            .replace("%team%", args[2]));
                    return;
                }
                spawnManager.setTeamSpawn(team, l);
                sender.sendMessage(chatUtils.getMessage("command.set.team.success", null)
                        .replace("%team%", team.getName())
                        .replace("%location%", SpawnManager.locToString(l, true)));
            }
            case "player" -> {
                if (args.length < 3) {
                    sender.sendMessage(chatUtils.getMessage("error.player_not_specified", null));
                    return;
                }

                String target = args[2];

                if (target.equals("*")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        spawnManager.setPlayerSpawn(p.getName(), l);
                    }
                    sender.sendMessage(chatUtils.getMessage("command.set.player.all", null)
                            .replace("%location%", SpawnManager.locToString(l, true)));
                } else {
                    spawnManager.setPlayerSpawn(target, l);
                    sender.sendMessage(chatUtils.getMessage("command.set.player.success", null)
                            .replace("%location%", SpawnManager.locToString(l, true))
                            .replace("%target%", target));
                }
            }
            default -> sender.sendMessage(chatUtils.getMessage("error.invalid_type", null));
        }
    }

    private void handleTP(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(chatUtils.getMessage("error.only_player", null));
            return;
        }
        if (!sender.hasPermission("xspawn.command.tp") && !sender.hasPermission("xspawn.admin") && !sender.isOp()) {
            sender.sendMessage(chatUtils.getMessage("error.no_permission", null)
                    .replace("%command%", label + (args.length > 0 ? " " + String.join(" ", args) : "")));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(chatUtils.getMessage("error.invalid_type", null));
            return;
        }
        String type = args[1];

        switch (type) {
            case "first" -> {
                Location l = spawnManager.getFirstSpawn();
                if (l == null) {
                    l = player.getWorld().getSpawnLocation();
                    sender.sendMessage(chatUtils.getMessage("command.tp.first.using_world_spawn", null));
                    teleport(player, l);
                } else {
                    sender.sendMessage(chatUtils.getMessage("command.tp.first.success", null));
                    teleport(player, l);
                }
            }
            case "team" -> {
                if (!plugin.isWorkingXTeams()) {
                    sender.sendMessage(chatUtils.getMessage("error.xteams_not_loaded", null));
                    return;
                }

                if (args.length < 3) {
                    sender.sendMessage(chatUtils.getMessage("error.team_not_specified", null));
                    return;
                }
                Team team = XTeamsAPI.getTeam(args[2]);
                if (team == null) {
                    sender.sendMessage(chatUtils.getMessage("error.invalid_team", null)
                            .replace("%team%", args[2]));
                    return;
                }
                Location l = spawnManager.getTeamSpawn(XTeamsAPI.getTeam(args[2]));
                if (l != null) {
                    sender.sendMessage(chatUtils.getMessage("command.tp.team.success", null)
                            .replace("%team%", team.getName()));
                    teleport(player, l);
                } else {
                    sender.sendMessage(chatUtils.getMessage("error.team_not_defined", null)
                            .replace("%team%", team.getName()));
                }
            }
            case "player" -> {
                if (args.length < 3) {
                    sender.sendMessage(chatUtils.getMessage("error.player_not_specified", null));
                    return;
                }
                String target = args[2];
                Location l = spawnManager.getPlayerSpawn(target);
                if (l != null) {
                    sender.sendMessage(chatUtils.getMessage("command.tp.player.success", null)
                            .replace("%target%", target));
                    teleport(player, l);
                } else {
                    sender.sendMessage(chatUtils.getMessage("error.player_not_defined", null)
                            .replace("%target%", target));
                }
            }
            default -> sender.sendMessage(chatUtils.getMessage("error.invalid_type", null));
        }
    }

    private void handleDel(CommandSender sender, String[] args, String label) {
        if (!sender.hasPermission("xspawn.command.del") && !sender.hasPermission("xspawn.admin") && !sender.isOp()) {
            sender.sendMessage(chatUtils.getMessage("error.no_permission", null)
                    .replace("%command%", label + (args.length > 0 ? " " + String.join(" ", args) : "")));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(chatUtils.getMessage("error.invalid_type", null));
            return;
        }
        String type = args[1];

        switch (type) {
            case "first" -> {
                spawnManager.removeFirstSpawn();
                sender.sendMessage(chatUtils.getMessage("command.del.first.success", null));
            }
            case "team" -> {
                if (!plugin.isWorkingXTeams()) {
                    sender.sendMessage(chatUtils.getMessage("error.xteams_not_loaded", null));
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage(chatUtils.getMessage("error.team_not_specified", null));
                    return;
                }
                Team team = XTeamsAPI.getTeam(args[2]);
                if (team == null) {
                    sender.sendMessage(chatUtils.getMessage("error.invalid_team", null)
                            .replace("%team%", args[2]));
                    return;
                }

                Location l = spawnManager.getTeamSpawn(team);
                if (l == null) {
                    sender.sendMessage(chatUtils.getMessage("error.team_not_defined", null)
                            .replace("%team%", args[2]));
                    return;
                }

                spawnManager.removeTeamSpawn(team);
                sender.sendMessage(chatUtils.getMessage("command.del.team.success", null)
                        .replace("%team%", team.getName()));
            }
            case "player" -> {
                if (args.length < 3) {
                    sender.sendMessage(chatUtils.getMessage("error.player_not_specified", null));
                    return;
                }

                String target = args[2];

                if (target.equals("*")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        spawnManager.removePlayerSpawn(p.getName());
                    }
                    sender.sendMessage(chatUtils.getMessage("command.del.player.all", null));
                } else {

                    Location l = spawnManager.getPlayerSpawn(target);
                    if (l == null) {
                        sender.sendMessage(chatUtils.getMessage("error.player_not_defined", null)
                                .replace("%target%", target));
                        return;
                    }

                    spawnManager.removePlayerSpawn(target);
                    sender.sendMessage(chatUtils.getMessage("command.del.player.success", null)
                            .replace("%target%", target));
                }
            }
            default -> sender.sendMessage(chatUtils.getMessage("error.invalid_type", null));
        }
    }
    private void handleReload(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("xspawn.command.reload") && !sender.hasPermission("xspawn.admin") && !sender.isOp()) {
            sender.sendMessage(chatUtils.getMessage("error.no_permission", null)
                    .replace("%command%", label + (args.length > 0 ? " " + String.join(" ", args) : "")));
            return;
        }

        Player target = (sender instanceof Player) ? (Player) sender : null;
        try {
            loadUtils.loadFiles();
        } catch (Exception e) {
            sender.sendMessage(chatUtils.getMessage("command.reload.error", target));
            return;
        }
        sender.sendMessage(chatUtils.getMessage("command.reload.success", target));
    }

    private void teleport(Player p, Location l) {
        if (plugin.getConfig().getBoolean("settings.tp_to_spectator", false)) {
            p.setGameMode(GameMode.SPECTATOR);
        }
        p.teleport(l);
    }
}
