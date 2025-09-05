package test.dao;

import dao.LineStationDao;
import dao.PostgresConnection;
import domain.LineStation;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LineStationDaoTest {
    private static Connection conn;
    private LineStationDao lineStationDao;
    private final List<Integer> testLineIds = new ArrayList<>();
    private final List<Integer> testStationIds = new ArrayList<>();
    private final List<int[]> testLineStationPairs = new ArrayList<>();

    @BeforeAll
    static void setupClass() throws Exception {
        conn = PostgresConnection.getConnection();
    }

    @BeforeEach
    void setup() {
        lineStationDao = LineStationDao.of();
        testLineIds.clear();
        testStationIds.clear();
        testLineStationPairs.clear();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Pulisci line_station
        if (!testLineStationPairs.isEmpty()) {
            String sql = "DELETE FROM line_station WHERE (id_line = ? AND id_station = ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int[] pair : testLineStationPairs) {
                    ps.setInt(1, pair[0]);
                    ps.setInt(2, pair[1]);
                    ps.executeUpdate();
                }
            }
        }
        // Pulisci line
        if (!testLineIds.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < testLineIds.size(); i++) {
                inClause.append(i == 0 ? "?" : ",?");
            }
            String sql = "DELETE FROM line WHERE id_line IN (" + inClause + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < testLineIds.size(); i++) {
                    ps.setInt(i + 1, testLineIds.get(i));
                }
                ps.executeUpdate();
            }
        }
        // Pulisci station
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
    void testFindByLine() throws Exception {
        int lineId = 88888;
        String name = "LINEA_TEST";
        int stationId1 = 90001;
        int stationId2 = 90002;
        insertLine(lineId, name);
        insertStation(stationId1);
        insertStation(stationId2);
        insertLineStation(lineId, stationId1, 1);
        insertLineStation(lineId, stationId2, 2);
        testLineIds.add(lineId);
        testStationIds.add(stationId1);
        testStationIds.add(stationId2);
        testLineStationPairs.add(new int[]{lineId, stationId1});
        testLineStationPairs.add(new int[]{lineId, stationId2});
        List<LineStation> result = lineStationDao.findByLine(lineId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(stationId1, result.get(0).getStationId());
        assertEquals(1, result.get(0).getOrder());
        assertEquals(stationId2, result.get(1).getStationId());
        assertEquals(2, result.get(1).getOrder());
    }

    // --- Helper methods ---
    private void insertLine(int id, String name) throws SQLException {
        String sql = "INSERT INTO line (id_line, name) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.executeUpdate();
        }
    }
    private void insertStation(int id) throws SQLException {
        String sql = "INSERT INTO station (id_station, location, num_bins, service_description, is_head) VALUES (?, 'TEST', 1, 'desc', ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setBoolean(2, true);
            ps.executeUpdate();
        }
    }
    private void insertLineStation(int idLine, int idStation, int order) throws SQLException {
        String sql = "INSERT INTO line_station (id_line, id_station, station_order) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLine);
            ps.setInt(2, idStation);
            ps.setInt(3, order);
            ps.executeUpdate();
        }
    }
}
