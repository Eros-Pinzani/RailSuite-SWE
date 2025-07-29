package mapper;

import domain.Depot;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepotMapper {
    /**
     * Maps a ResultSet row to a Depot domain object.
     * @param rs the ResultSet containing depot data
     * @return a Depot domain object
     * @throws SQLException if a database access error occurs
     */
    public static Depot toDomain(ResultSet rs) throws SQLException {
        return Depot.of(rs.getInt("id_depot"));
    }
}
