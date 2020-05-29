package fr.pios.report.client;

import fr.pios.report.metier.commands.OnAdminReport;
import fr.pios.report.metier.commands.OnReport;
import fr.pios.report.physique.database.Database;
import fr.pios.report.physique.database.SQLite;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Molzonas
 */
public final class Main extends JavaPlugin implements Listener {

    public static FileConfiguration config;
    public static JavaPlugin plugin;
    public static Database db;
    public static Logger logger;

    public final static DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.FRENCH);

    @Override
    public void onEnable() {
        super.onEnable();
        saveDefaultConfig();
        config = getConfig();
        plugin = this;
        db = new SQLite();
        logger = this.getLogger();
        SetCommands();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void SetCommands() {
        getCommand("report").setExecutor(new OnReport());
        List<String> aliasesReport = new ArrayList<>();
        aliasesReport.add("rep");
        aliasesReport.add("pre");
        getCommand("report").setAliases(aliasesReport);
        getCommand("report").setTabCompleter(new OnReport());
        getCommand("pios-report").setExecutor(new OnAdminReport());
        List<String> aliasesAdminReport = new ArrayList<>();
        aliasesAdminReport.add("piosr");
        aliasesAdminReport.add("pir");
        aliasesAdminReport.add("adrep");
        getCommand("pios-report").setAliases(aliasesAdminReport);
        getCommand("pios-report").setTabCompleter(new OnAdminReport());
    }
}
