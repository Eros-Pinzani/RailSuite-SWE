package mapper;

import domain.Convoy;
import domain.Carriage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ConvoyMapper {
    /**
     * Maps a ResultSet row and a list of carriages to a Convoy domain object.
     * @param rs the ResultSet containing convoy data
     * @param carriages the list of carriages belonging to the convoy
     * @return a Convoy domain object
     * @throws SQLException if a database access error occurs
     */
    public static Convoy toDomain(ResultSet rs, List<Carriage> carriages) throws SQLException {
        return Convoy.of(
            rs.getInt("id_convoy"),
            carriages
        );
    }
}
