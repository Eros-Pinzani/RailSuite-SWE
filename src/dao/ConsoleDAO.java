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
    ConsoleDAO() {} // costruttore package-private

    public static ConsoleDAO getInstance() {
        return instance;
    }

    public List<List<String>> getAllConsoles() {
        List<List<String>> risultati = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM console")) {
            int colonne = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                List<String> riga = new ArrayList<>();
                for (int i = 1; i <= colonne; i++) {
                    riga.add(rs.getString(i));
                }
                risultati.add(riga);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return risultati;
    }
}
