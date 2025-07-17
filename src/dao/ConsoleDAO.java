package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConsoleDAO {
    // Singleton
    private static final ConsoleDAO instance = new ConsoleDAO();
    ConsoleDAO() {} // constructor package-private

    public static ConsoleDAO getInstance() {
        return instance;
    }

    public List<List<String>> getAllConsoles() {
        List<List<String>> results = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM console")) {
            int columns = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                List<String> riga = new ArrayList<>();
                for (int i = 1; i <= columns; i++) {
                    riga.add(rs.getString(i));
                }
                results.add(riga);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
