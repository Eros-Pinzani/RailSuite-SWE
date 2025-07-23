package dao;

import domain.ConvoyPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConvoyPoolDaoImp implements ConvoyPoolDao {
    private static final String SELECT_BY_ID =
            "SELECT id_convoy, id_station, status FROM convoy_pool WHERE id_convoy = ?";
    private static final String UPDATE =
            "UPDATE convoy_pool SET id_station = ?, status = ? WHERE id_convoy = ?";
    private static final String SELECT_ALL =
            "SELECT id_convoy, id_station, status FROM convoy_pool";
    private static final String SELECT_BY_STATION =
            "SELECT id_convoy, id_station, status FROM convoy_pool WHERE id_station = ?";
    private static final String SELECT_BY_STATUS =
            "SELECT id_convoy, id_station, status FROM convoy_pool WHERE status = ?";
    private static final String SELECT_BY_STATION_AND_STATUS =
            "SELECT id_convoy, id_station, status FROM convoy_pool WHERE id_station = ? AND status = ?";

    ConvoyPoolDaoImp (){}

    @Override
    public ConvoyPool getConvoyPoolById(int idConvoy) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, idConvoy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper.ConvoyPoolMapper.toDomain(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void updateConvoyPool(ConvoyPool convoyPool) throws SQLException {
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setInt(1, convoyPool.getIdStation());
            ps.setString(2, convoyPool.getConvoyStatus().name());
            ps.setInt(3, convoyPool.getIdConvoy());
            ps.executeUpdate();
        }
    }

    @Override
    public List<ConvoyPool> getAllConvoyPools() throws SQLException {
        List<ConvoyPool> list = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapper.ConvoyPoolMapper.toDomain(rs));
            }
        }
        return list;
    }

    @Override
    public List<ConvoyPool> getConvoysByStation(int idStation) throws SQLException {
        List<ConvoyPool> list = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATION)) {
            ps.setInt(1, idStation);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapper.ConvoyPoolMapper.toDomain(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<ConvoyPool> getConvoysByStatus(ConvoyPool.ConvoyStatus status) throws SQLException {
        List<ConvoyPool> list = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATUS)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapper.ConvoyPoolMapper.toDomain(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<ConvoyPool> getConvoysByStationAndStatus(int idStation, ConvoyPool.ConvoyStatus status) throws SQLException {
        List<ConvoyPool> list = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATION_AND_STATUS)) {
            ps.setInt(1, idStation);
            ps.setString(2, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapper.ConvoyPoolMapper.toDomain(rs));
                }
            }
        }
        return list;
    }
}
