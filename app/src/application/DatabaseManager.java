package application;

import java.sql.*;
import com.mysql.cj.jdbc.MysqlDataSource;

public class DatabaseManager {
    private MysqlDataSource dataSource;

    public DatabaseManager() {
        dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/rpg_game");
        dataSource.setUser("magou277");
        dataSource.setPassword("degeneration10");
    }

    public MysqlDataSource getDataSource() {
        return dataSource;
    }
}