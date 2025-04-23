package application;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import com.jcraft.jsch.*;
import java.sql.DriverManager;

public class DatabaseManager {
    private Connection conn;
    Session session = null;

    public DatabaseManager() {
        try {
//            // Copy the database from resources to a temporary file
//            String dbPath = "rpg_game.db";
//            try (InputStream dbStream = DatabaseManager.class.getResourceAsStream("/database/rpg_game.db")) {
//                if (dbStream == null) {
//                    throw new IllegalStateException("Database file not found in resources: /database/rpg_game.db");
//                }
//                Files.copy(dbStream, Paths.get(dbPath), StandardCopyOption.REPLACE_EXISTING);
//            }

            String jarPath = new File(App.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI())
                    .getParent();

            String url = "jdbc:sqlite:" + jarPath + "/classes/database/rpg_game.db";
            System.out.println("Connecting to database " + url);
            DriverManager.registerDriver(new org.sqlite.JDBC());
            conn = DriverManager.getConnection(url);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ResultSetHandler<T> {
        T handle(ResultSet rs) throws SQLException;
    }

    public <T> void executerLecture(String sql, Object value, ResultSetHandler<T> handler) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Détermination du type de la value
            if (value instanceof String) {
                stmt.setString(1, (String) value);
            }
            else if (value instanceof Integer) {
                stmt.setInt(1, (Integer) value);
            }
            else if (value instanceof Double) {
                stmt.setDouble(1, (Double) value);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                handler.handle(rs);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int executerMaJ(String sql) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeUpdate(sql);
    }

    public Connection getConnection() {
        return conn;
    }

}