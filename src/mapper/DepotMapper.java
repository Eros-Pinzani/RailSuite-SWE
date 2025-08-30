package mapper;

import domain.Depot;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

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

    /**
     * Sets the id_depot parameter in a PreparedStatement.
     */
    public static void setIdDepot(PreparedStatement stmt, int idDepot) throws SQLException {
        stmt.setInt(1, idDepot);
    }
}
