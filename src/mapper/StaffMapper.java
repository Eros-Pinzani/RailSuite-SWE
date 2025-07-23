package mapper;

import domain.Staff;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffMapper {
    public static Staff toDomain(ResultSet rs) throws SQLException {
        return Staff.of(
            rs.getInt("id_staff"),
            rs.getString("name"),
            rs.getString("surname"),
            rs.getString("address"),
            rs.getString("email"),
            rs.getString("password"),
            Staff.TypeOfStaff.valueOf(rs.getString("type_of_staff").trim().toUpperCase())
        );
    }
}

