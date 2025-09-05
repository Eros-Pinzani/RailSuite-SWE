package test.mapper;

import mapper.StaffMapper;
import domain.Staff;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class StaffMapperTest {
    private Connection conn;
    private final int TEST_STAFF_ID = 3002;
    private final String TEST_NAME = "TestName";
    private final String TEST_SURNAME = "TestSurname";
    private final String TEST_ADDRESS = "TestAddress";
    private final String TEST_EMAIL = "test.staff@rail.com";
    private final String TEST_PASSWORD = "testpwd";
    private final String TEST_TYPE = "OPERATOR";

    @BeforeEach
    void setUp() throws Exception {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/dao/db.properties")) {
            props.load(fis);
        }
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        conn = DriverManager.getConnection(url, user, password);
        Statement st = conn.createStatement();
        st.executeUpdate("INSERT INTO staff (id_staff, name, surname, address, email, password, type_of_staff) VALUES (" + TEST_STAFF_ID + ", '" + TEST_NAME + "', '" + TEST_SURNAME + "', '" + TEST_ADDRESS + "', '" + TEST_EMAIL + "', '" + TEST_PASSWORD + "', '" + TEST_TYPE + "') ON CONFLICT DO NOTHING");
        st.close();
    }

    @AfterEach
    void tearDown() throws Exception {
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM staff WHERE id_staff=" + TEST_STAFF_ID);
        st.close();
        conn.close();
    }

    @Test
    void toDomain() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM staff WHERE id_staff=" + TEST_STAFF_ID);
        assertTrue(rs.next());
        Staff staff = StaffMapper.toDomain(rs);
        assertEquals(TEST_STAFF_ID, staff.getIdStaff());
        assertEquals(TEST_NAME, staff.getName());
        assertEquals(TEST_SURNAME, staff.getSurname());
        assertEquals(TEST_ADDRESS, staff.getAddress());
        assertEquals(TEST_EMAIL, staff.getEmail());
        assertEquals(TEST_PASSWORD, staff.getPassword());
        assertEquals(Staff.TypeOfStaff.OPERATOR, staff.getTypeOfStaff());
        rs.close();
        st.close();
    }

    @Test
    void setEmail() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM staff WHERE email=?");
        StaffMapper.setEmail(ps, TEST_EMAIL);
        ResultSet rs = ps.executeQuery();
        assertTrue(rs.next());
        assertEquals(TEST_EMAIL, rs.getString("email"));
        rs.close();
        ps.close();
    }

    @Test
    void setTypeOfStaff() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM staff WHERE type_of_staff LIKE ?");
        StaffMapper.setTypeOfStaff(ps, Staff.TypeOfStaff.OPERATOR);
        ResultSet rs = ps.executeQuery();
        boolean found = false;
        while (rs.next()) {
            if (rs.getInt("id_staff") == TEST_STAFF_ID) {
                found = true;
                assertEquals(TEST_TYPE, rs.getString("type_of_staff"));
            }
        }
        assertTrue(found);
        rs.close();
        ps.close();
    }
}