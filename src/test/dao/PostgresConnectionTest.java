package test.dao;

import dao.PostgresConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class PostgresConnectionTest {
    @Test
    void testGetConnectionAndQuery() throws Exception {
        try (Connection conn = PostgresConnection.getConnection()) {
            assertNotNull(conn, "La connessione non deve essere null");
            assertFalse(conn.isClosed(), "La connessione deve essere aperta");
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT 1")) {
                    assertTrue(rs.next(), "La query SELECT 1 deve restituire almeno una riga");
                    assertEquals(1, rs.getInt(1), "Il risultato della query deve essere 1");
                }
            }
        }
    }
}

