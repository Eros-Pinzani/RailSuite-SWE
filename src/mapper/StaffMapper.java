package mapper;

import domain.Staff;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class StaffMapper {
    /**
     * Maps a ResultSet row to a Staff domain object.
     * @param rs the ResultSet containing staff data
     * @return a Staff domain object
     * @throws SQLException if a database access error occurs
     */
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

    /**
     * Sets the email parameter in the PreparedStatement.
     * @param stmt the PreparedStatement
     * @param email the email to set
     * @throws SQLException if a database access error occurs
     */
    public static void setEmail(PreparedStatement stmt, String email) throws SQLException {
        stmt.setString(1, email);
    }

    /**
     * Sets the type of staff parameter in the PreparedStatement.
     * @param stmt the PreparedStatement
     * @param type the type of staff to set
     * @throws SQLException if a database access error occurs
     */
    public static void setTypeOfStaff(PreparedStatement stmt, Staff.TypeOfStaff type) throws SQLException {
        stmt.setString(1, "%" + type.name() + "%");
    }
}
