package me.lordburtz.Casino.commands;

import me.lordburtz.Casino.Main;
import me.lordburtz.Casino.data.Data;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//author: Fingolfin, LordBurtz
public class CreateCasino implements CommandExecutor, TabCompleter {

    private final Main plugin;
    public static Data data;
    public static final List<String> COMMANDS = new ArrayList<>();

    private final String file = "casinos.yml";

    public CreateCasino(Main main, Data data) {
        this.plugin = main;
        this.data = data;
        plugin.getCommand("casino").setTabCompleter(this);

        data.saveDefaultConfig(file);
        setupCommands();
    }

    public final void setupCommands() {
        COMMANDS.add("circle");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;
        List<String> allowed = Arrays.asList(plugin.getConfig().getString("allowed-casino").split(", "));
        if (!allowed.contains(commandSender.getName())) {
            commandSender.sendMessage("you do not have the privilege to execute this command");
            return true;
        }
        if (strings.length == 0) {
            commandSender.sendMessage("use /casino to create a casino");
            commandSender.sendMessage("use /casino circle [radius] to create a circle");
            commandSender.sendMessage("use /casino square and then the coords of two opposite corners");
            return true;
        }//TODO: add check for second argument
        if (strings[0].equals("circle")) {
            int count = data.getConfig(file).getInt("casinos.count");
            count++;
            data.getConfig(file).set("casinos.count", count);
            Player player = ((Player) commandSender);

            data.getConfig(file).set("casinos.casino" + count + ".owner", commandSender.getName());
            data.getConfig(file).set("casinos.casino" + count + ".radius", Integer.parseInt(strings[1]));

            data.getConfig(file).set("casinos.casino" + count + ".x", player.getLocation().getX());
            data.getConfig(file).set("casinos.casino" + count + ".y", player.getLocation().getY());
            data.getConfig(file).set("casinos.casino" + count + ".z", player.getLocation().getZ());
            data.saveConfig(file);
            player.sendMessage("casino set");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> completions = new ArrayList<>();

        if (strings.length == 1) {
            for (String a : COMMANDS) {
                if (a.toLowerCase().startsWith(strings[0].toLowerCase())) completions.add(a);
            }
            return completions;
        }
        return COMMANDS;
    }
}
