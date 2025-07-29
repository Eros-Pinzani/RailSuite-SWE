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
    public Station findById(int id) throws SQLException {
        // SQL query to get a station by id
        String sql = "SELECT * FROM station WHERE id_station = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapper.StationMapper.toDomain(rs);
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding station by ID: " + id, e);
        }
        return null;
    }

    @Override
    public Station findByLocation(String location) throws SQLException{
        // SQL query to get a station by location
        String sql = "SELECT * FROM station WHERE location = ? LIMIT 1";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, location);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapper.StationMapper.toDomain(rs);
            }
        } catch (SQLException e) {
            throw new SQLException("Error finding station by location: " + location, e);
        }
        return null;
    }

    @Override
    public List<Station> findAll() throws SQLException{
        // SQL query to get all stations
        String sql = "SELECT * FROM station";
        try {
            return getStations(sql);
        } catch (SQLException e) {
            throw new SQLException("Error retrieving all stations", e);
        }
    }

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
