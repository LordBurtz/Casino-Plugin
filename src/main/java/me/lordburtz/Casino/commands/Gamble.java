package me.lordburtz.Casino.commands;

import me.lordburtz.Casino.Main;
import me.lordburtz.Casino.data.Data;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Gamble implements CommandExecutor, TabCompleter {

    public Main plugin;
    public Random random;
    public FileConfiguration config;
    public Data data;
    public Economy econ;

    public static final List<String> COMMANDS = new ArrayList<>();
    public static final List<String> COMMANDS2 = new ArrayList<>();
    public static final List<String> COLORS = new ArrayList<String>();

    private final String file = "casinos.yml";
    private String prefix;

    public Gamble(Main plugin, Data data) {
        this.plugin = plugin;
        this.data = CreateCasino.data;
        this.random = new Random();
        this.econ = plugin.getEconomy();

        plugin.getCommand("roulette").setTabCompleter(this);
        config = plugin.getConfig();
        prefix = plugin.getConfig().getString("prefix") + " ";

        setupCommands();
        setupColors();
    }

    public final void setupCommands() {
        COMMANDS.add("amount");
        COMMANDS2.add("red"); COMMANDS2.add("black");
        COMMANDS2.add("green");
    }

    public final void setupColors() {
        COLORS.add("red"); COLORS.add("black");
        COLORS.add("green");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
           return true;
        }
        if (strings.length < 2) {
            commandSender.sendMessage("not enough Arguments");
            return true;
        }

        if (!COLORS.contains(strings[1].toLowerCase())) {
            commandSender.sendMessage(ChatColor.RED + prefix + "Color not correct");
            return true;
        }

        int amount;
        try {
             amount = Integer.parseInt(strings[0]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(ChatColor.RED + prefix + String.format(" %s is not a number", strings[0]));
            return true;
        }

        if ((amount < config.getInt("minimum")) || (amount > config.getInt("maximum"))) {
            commandSender.sendMessage(ChatColor.RED + prefix + "this does not comply with the bet standards");
            return true;
        }

        String color = strings[1].toLowerCase();
        double chance = config.getDouble("probability." + color);

        int multiplier = config.getInt("reward." + color);

        Player player = ((Player) commandSender);
        econ.withdrawPlayer(player, amount);

        if (plugin.getConfig().getBoolean("casino")) {
            if (isNotCasino(commandSender)) {
                commandSender.sendMessage(ChatColor.RED + "you are not in a casino");
                return true;
            }
        }

        int rand = random.nextInt(1000);
        String dice;
        String dice_color;

        if (rand < config.getInt("probability.red")) {
            dice = "red";
            dice_color = ChatColor.RED + "Red";
        } else if (rand < (config.getInt("probability.red") + config.getInt("probability.black"))){
            dice = "black";
            dice_color = ChatColor.BLACK + "Black";
        } else {
            dice = "green";
            dice_color = ChatColor.GREEN + "Green";
        }
        commandSender.sendMessage(prefix + String.format("Landed on %s.", dice_color));
        if (!strings[1].equals(dice)) {
            commandSender.sendMessage(ChatColor.RED + prefix +plugin.getConfig().getString("loose-msg"));
            if (config.getBoolean("global")) {
                plugin.getServer().broadcastMessage(ChatColor.RED + prefix + commandSender.getName() + " has lost $" + amount);
            }
            return true;
        } else {
            if (config.getBoolean("sound")) {
                player.playEffect(player.getLocation(), Effect.ENDERDRAGON_GROWL, 7);
            }
            if (config.getBoolean("particles")) {
                for (int i = 0; i < 50; i++) {
                    player.spawnParticle(Particle.TOTEM, ((Player) commandSender).getLocation(), 7);
                }
            }
            int won = amount * multiplier;
            econ.depositPlayer(player, won);
            if (config.getBoolean("global")) {
                plugin.getServer().broadcastMessage(ChatColor.GOLD + prefix + commandSender.getName() + " has won $" + won);
            }
            commandSender.sendMessage(ChatColor.GOLD + prefix + config.getString("win-msg").replace("%amount%", String.valueOf(won))); return true;
        }
    }

    public boolean isNotCasino(CommandSender sender) {
        Player player = (Player) sender;
        for (int i = 1; i<=data.getConfig(file).getInt("casinos.count"); i++) {
            Location loc = new Location(player.getWorld(),
                    data.getConfig(file).getDouble(String.format("casinos.casino%d.x", i)),
                    data.getConfig(file).getDouble(String.format("casinos.casino%d.y", i)),
                    data.getConfig(file).getDouble(String.format("casinos.casino%d.z", i)));
            if (player.getLocation().distance(loc) < data.getConfig(file).getInt(String.format("casinos.casino%d.radius", i))) {
                return false;
            }
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

        if (strings.length == 2) {
            for (String a : COMMANDS2) {
                if (a.toLowerCase().startsWith(strings[1].toLowerCase())) completions.add(a);
            }
            return completions;
        }
        return COMMANDS;
    }
}
