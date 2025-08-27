package dao;

import domain.Station;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the StationDao interface.
 * Contains SQL queries and logic for accessing station data.
 */
class StationDaoImp implements StationDao {
    StationDaoImp() {}

    @Override
    public List<Station> findAllHeadStations() throws SQLException{
        // SQL query to get all head stations
        String sql = "SELECT * FROM station WHERE is_head = TRUE";
        try {
            return getStations(sql);
        } catch (SQLException e) {
            throw new SQLException("Error retrieving all head stations", e);
        }
    }

    private List<Station> getStations(String sql) throws SQLException{
        List<Station> stations = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stations.add(mapper.StationMapper.toDomain(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving stations", e);
        }
        return stations;
    }
}
