package me.lordburtz.Casino;

import me.lordburtz.Casino.commands.CreateCasino;
import me.lordburtz.Casino.commands.Gamble;
import me.lordburtz.Casino.data.Data;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private Data data;
    private static Economy econ = null;


    @Override
    public void onEnable() {
        data = new Data(this, "casinos.yml");

        if (!setupEconomy() ) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        initCommands();
        initListeners();
        saveDefaultConfig();
    }

    public static Economy getEconomy() {
        return econ;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public void initCommands() {
        this.getCommand("casino").setExecutor(new CreateCasino(this, data));
        this.getCommand("roulette").setExecutor(new Gamble(this, data));
    }

    public void initListeners() {
    }
}
