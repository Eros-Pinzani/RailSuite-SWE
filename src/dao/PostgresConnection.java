package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresConnection {
    private static final String PROPERTIES_PATH = "src/dao/db.properties";
    private static final String url;
    private static final String user;
    private static final String password;

    static {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(PROPERTIES_PATH)) {
            props.load(fis);
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
        } catch (IOException e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            throw new RuntimeException("Unable to load database configuration", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
