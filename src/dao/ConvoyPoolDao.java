package dao;

import domain.ConvoyPool;
import java.sql.SQLException;
import java.util.List;

public interface ConvoyPoolDao {
    static ConvoyPoolDao of() {
        return new ConvoyPoolDaoImp();
    }

    ConvoyPool getConvoyPoolById(int idConvoy) throws SQLException;
    void updateConvoyPool(ConvoyPool convoyPool) throws SQLException;
    void insertConvoyPool(domain.ConvoyPool pool) throws SQLException;
    List<ConvoyPool> getAllConvoyPools() throws SQLException;
    List<ConvoyPool> getConvoysByStation(int idStation) throws SQLException;
    List<ConvoyPool> getConvoysByStatus(ConvoyPool.ConvoyStatus status) throws SQLException;
    List<ConvoyPool> getConvoysByStationAndStatus(int idStation, ConvoyPool.ConvoyStatus status) throws SQLException;
    /**
     * Restituisce per ogni convoglio associato a una stazione:
     * id convoglio, status, numero vetture, tipi vetture (separati da virgola)
     */
    List<domain.ConvoyTableDTO> getConvoyTableDataByStation(int idStation) throws SQLException;
}
