package test.businesslogic;

import businessLogic.service.LogInService;
import domain.Staff;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class LogInServiceTest {
    private Connection conn;
    private final String testEmail = "junit.staff@example.com";
    private final String testPassword = "JUnitPassword123";
    private final int testStaffId = 99999;

    @BeforeEach
    void setUp() throws Exception {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("dao/db.properties")) {
            if (is == null) throw new RuntimeException("db.properties non trovato nel classpath!");
            props.load(is);
        }
        String dbUrl = props.getProperty("db.url");
        String dbUser = props.getProperty("db.user");
        String dbPassword = props.getProperty("db.password");
        conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        // Inserisci staff di test
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO staff (id_staff, name, surname, email, password, type_of_staff) VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (id_staff) DO UPDATE SET email = EXCLUDED.email, password = EXCLUDED.password;")) {
            ps.setInt(1, testStaffId);
            ps.setString(2, "JUnit");
            ps.setString(3, "Tester");
            ps.setString(4, testEmail);
            ps.setString(5, testPassword);
            ps.setString(6, "OPERATOR");
            ps.executeUpdate();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn == null || conn.isClosed()) return;
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM staff WHERE id_staff = ?;")) {
            ps.setInt(1, testStaffId);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    @Test
    void authenticate_success() {
        LogInService service = new LogInService();
        Staff staff = service.authenticate(testEmail, testPassword);
        assertNotNull(staff);
        assertEquals(testEmail, staff.getEmail());
        assertEquals(testStaffId, staff.getIdStaff());
    }

    @Test
    void authenticate_wrongPassword() {
        LogInService service = new LogInService();
        Staff staff = service.authenticate(testEmail, "WrongPassword");
        assertNull(staff);
    }

    @Test
    void authenticate_nonExistingEmail() {
        LogInService service = new LogInService();
        Staff staff = service.authenticate("not.exists@example.com", testPassword);
        assertNull(staff);
    }
}