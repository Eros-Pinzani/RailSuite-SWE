package test.dao;

import dao.StationDao;
import dao.PostgresConnection;
import domain.Station;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StationDaoTest {
    private static Connection conn;
    private StationDao stationDao;
    private final List<Integer> testStationIds = new ArrayList<>();

    @BeforeAll
    static void setupClass() throws Exception {
        conn = PostgresConnection.getConnection();
    }

    @BeforeEach
    void setup() {
        stationDao = StationDao.of();
        testStationIds.clear();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (!testStationIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < testStationIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM station WHERE id_station IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < testStationIds.size(); i++) {
                    ps.setInt(i + 1, testStationIds.get(i));
                }
                ps.executeUpdate();
            }
        }
    }

    @Test
    void testFindAllHeadStations() throws Exception {
        int headId1 = 88888;
        int headId2 = 88889;
        int nonHeadId = 88890;
        insertStation(headId1, "HEAD1", 2, "desc1", true);
        insertStation(headId2, "HEAD2", 3, "desc2", true);
        insertStation(nonHeadId, "NONHEAD", 1, "desc3", false);
        testStationIds.add(headId1);
        testStationIds.add(headId2);
        testStationIds.add(nonHeadId);
        List<Station> heads = stationDao.findAllHeadStations();
        assertNotNull(heads);
        assertTrue(heads.stream().anyMatch(s -> s.getIdStation() == headId1));
        assertTrue(heads.stream().anyMatch(s -> s.getIdStation() == headId2));
        assertFalse(heads.stream().anyMatch(s -> s.getIdStation() == nonHeadId));
    }

    // --- Helper methods ---
    private void insertStation(int id, String location, int numBins, String desc, boolean isHead) throws SQLException {
        String sql = "INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, location);
            ps.setInt(3, numBins);
            ps.setString(4, desc);
            ps.setBoolean(5, isHead);
            ps.executeUpdate();
        }
    }
}
