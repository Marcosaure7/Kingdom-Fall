package application;

import java.sql.*;

import com.jcraft.jsch.JSchException;
import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class DatabaseManager {
    private MysqlDataSource dataSource;
    private Connection conn;
    Session session = null;

    public DatabaseManager() {

        try {

            Dotenv dotenv = Dotenv.configure().load();
            boolean sessionSsh = dotenv.get("SSH_REQUIRED").equals("true");

            String dbHost = dotenv.get("DB_HOST");
            int dbPort = Integer.parseInt(dotenv.get("DB_PORT"));
            String dbUser = dotenv.get("DB_USER");
            String dbPassword = dotenv.get("DB_PASSWORD");

            if (sessionSsh) {
                String sshHost = dotenv.get("SSH_HOST");
                String sshUser = dotenv.get("SSH_USER");
                String sshPrivateKey = System.getProperty("user.home") + "/.ssh/id_rsa";
                int sshPort = Integer.parseInt(dotenv.get("SSH_PORT"));

                JSch jsch = new JSch();
                jsch.addIdentity(sshPrivateKey); // Ajouter la clé privée SSH

                // Créer une session SSH
                session = jsch.getSession(sshUser, sshHost, sshPort);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();

                // Configurer le tunnel SSH (port forwarding local)
                int assignedPort = session.setPortForwardingL(dbPort, dbHost, dbPort);
                System.out.println("Tunnel SSH établi sur localhost:" + assignedPort);
            }

            dataSource = new MysqlDataSource();
            dataSource.setURL(String.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dotenv.get("DB_NAME")));
            dataSource.setUser(dbUser);
            dataSource.setPassword(dbPassword);
            conn = dataSource.getConnection();
        }

        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
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