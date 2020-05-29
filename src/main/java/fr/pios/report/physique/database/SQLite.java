package fr.pios.report.physique.database;

import fr.pios.report.client.Main;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 *
 * @author Molzonas
 */
public class SQLite extends Database{
    String dbname;
    public SQLite(){
        dbname = "report";
        load();
    }

    public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS report (" + 
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
            "`sender` varchar(32) NOT NULL," + 
            "`reported` varchar(32) NOT NULL," +
            "`reason` varchar(512) NOT NULL," +
            "`date` int(11) NOT NULL," +
            "`resolved` int(1) NOT NULL," +
            "`removed` int(1) NOT NULL" +
            ");";

    @Override
    public Connection getSQLConnection() {
        File dataFolder = new File(Main.plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                Main.logger.log(Level.SEVERE, "File write error: {0}.db", dbname);
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            Main.logger.log(Level.SEVERE, "JBDC library error.");
        }
        return null;
    }

    @Override
    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}
