package mapper;

import domain.CarriageDepot;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarriageDepotMapper {
    /**
     * Maps a ResultSet row to a CarriageDepot domain object.
     * @param rs the ResultSet containing carriage depot data
     * @return a CarriageDepot domain object
     * @throws SQLException if a database access error occurs
     */
    public static CarriageDepot toDomain(ResultSet rs) throws SQLException {
        return CarriageDepot.of(
            rs.getInt("id_depot"),
            rs.getInt("id_carriage"),
            rs.getTimestamp("time_entered"),
            rs.getTimestamp("time_exited"),
            CarriageDepot.StatusOfCarriage.valueOf(rs.getString("status_of_carriage"))
        );
    }
}
