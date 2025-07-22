package dao;

import domain.ConvoyPool;
import java.sql.SQLException;
import java.util.List;

public interface ConvoyPoolDao {
    ConvoyPool getConvoyPoolById(int idConvoy) throws SQLException;
    void updateConvoyPool(ConvoyPool convoyPool) throws SQLException;
    List<ConvoyPool> getAllConvoyPools() throws SQLException;
    List<ConvoyPool> getConvoysByStation(int idStation) throws SQLException;
    List<ConvoyPool> getConvoysByStatus(ConvoyPool.ConvoyStatus status) throws SQLException;
    List<ConvoyPool> getConvoysByStationAndStatus(int idStation, ConvoyPool.ConvoyStatus status) throws SQLException;
}
