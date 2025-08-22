package dev.drygo.XSpawn.Handlers;

import dev.drygo.XSpawn.XSpawn;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import dev.drygo.XTeams.API.XTeamsAPI;
import dev.drygo.XTeams.Models.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class XSpawnTabCompleter implements TabCompleter {

    private final XSpawn plugin;

    public XSpawnTabCompleter(XSpawn plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        if (args.length == 1) {
            return Arrays.asList("set", "tp", "del", "reload", "help", "info").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "set":
                case "tp":
                case "del":
                    return Arrays.asList("first", "team", "player").stream()
                            .filter(s -> s.startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
            }
        }

        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "set":
                case "tp":
                case "del":
                    switch (args[1].toLowerCase()) {
                        case "team":
                            if (plugin.isWorkingXTeams()) {
                                return XTeamsAPI.getAllTeams().stream()
                                        .map(Team::getName)
                                        .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                                        .collect(Collectors.toList());
                            }
                            break;
                        case "player":
                            List<String> players = Bukkit.getOnlinePlayers().stream()
                                    .map(p -> p.getName())
                                    .collect(Collectors.toList());
                            players.add("*");
                            return players.stream()
                                    .filter(name -> name.startsWith(args[2].toLowerCase()))
                                    .collect(Collectors.toList());
                    }
            }
        }

        return new ArrayList<>();
    }
}
