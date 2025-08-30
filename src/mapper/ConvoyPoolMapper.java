package mapper;

import domain.ConvoyPool;
import domain.DTO.ConvoyTableDTO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConvoyPoolMapper {
    /**
     * Maps a ResultSet row to a ConvoyPool domain object.
     * @param rs the ResultSet containing convoy pool data
     * @return a ConvoyPool domain object
     * @throws SQLException if a database access error occurs
     */
    public static ConvoyPool toDomain(ResultSet rs) throws SQLException {
        return ConvoyPool.of(
            rs.getInt("id_convoy"),
            rs.getInt("id_station"),
            ConvoyPool.ConvoyStatus.valueOf(rs.getString("status").trim().toUpperCase())
        );
    }

    public static ConvoyTableDTO toConvoyTableDTO(ResultSet rs) throws SQLException {
        return new ConvoyTableDTO(
            rs.getInt("id_convoy"),
            rs.getString("model"),
            rs.getString("status"),
            rs.getInt("carriage_count"),
            rs.getInt("sum"),
            rs.getString("model_types")
        );
    }

    public static void setIdConvoy(PreparedStatement ps, int idConvoy) throws SQLException {
        ps.setInt(1, idConvoy);
    }

    public static void setInsertConvoyPool(PreparedStatement ps, ConvoyPool pool) throws SQLException {
        ps.setInt(1, pool.getIdConvoy());
        ps.setInt(2, pool.getIdStation());
        ps.setString(3, pool.getConvoyStatus().name());
    }

    public static void setIdStation(PreparedStatement ps, int idStation) throws SQLException {
        ps.setInt(1, idStation);
    }

    public static void setCheckConvoyStatus(PreparedStatement ps, int idConvoy) throws SQLException {
        ps.setInt(1, idConvoy);
        ps.setInt(2, idConvoy);
    }
}
