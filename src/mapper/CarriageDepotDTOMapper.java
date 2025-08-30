package mapper;

import domain.DTO.CarriageDepotDTO;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarriageDepotDTOMapper {
    /**
     * Mappa una riga del ResultSet in un oggetto CarriageDepotDTO.
     */
    public static CarriageDepotDTO toDTO(ResultSet rs) throws SQLException {
        return new CarriageDepotDTO(
            rs.getInt("id_carriage"),
            rs.getString("model"),
            rs.getInt("year_produced"),
            rs.getInt("capacity"),
            rs.getString("status_of_carriage"),
            rs.getTimestamp("time_exited")
        );
    }
}

