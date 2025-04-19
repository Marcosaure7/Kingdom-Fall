package application;

import java.sql.*;
import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseManager {
    private MysqlDataSource dataSource;
    private Connection conn;

    public DatabaseManager() {

        Dotenv dotenv = Dotenv.configure().load();

        try {
            dataSource = new MysqlDataSource();
            dataSource.setURL(String.format("jdbc:mysql://%s:%s/%s", dotenv.get("DB_HOST"), dotenv.get("DB_PORT"), dotenv.get("DB_NAME")));
            dataSource.setUser(dotenv.get("DB_USER"));
            dataSource.setPassword(dotenv.get("DB_PASSWORD"));
            conn = dataSource.getConnection();
        }

        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    public MysqlDataSource getDataSource() {
        return dataSource;
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


}