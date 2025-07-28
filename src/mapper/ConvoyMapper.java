package mapper;

import domain.Convoy;
import domain.Carriage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ConvoyMapper {
    public static Convoy toDomain(ResultSet rs, List<Carriage> carriages) throws SQLException {
        return Convoy.of(
            rs.getInt("id_convoy"),
            carriages
        );
    }
}
