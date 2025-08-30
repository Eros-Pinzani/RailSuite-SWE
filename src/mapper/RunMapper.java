package mapper;

import domain.Run;
import domain.DTO.RunDTO;
import domain.DTO.ConvoyTableDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class RunMapper {
    public static Run toDomain(ResultSet rs) throws SQLException {
        return Run.of(
            rs.getInt("id_line"),
            rs.getString("line_name"),
            rs.getInt("id_convoy"),
            rs.getInt("id_staff"),
            rs.getString("name"),
            rs.getString("surname"),
            rs.getInt("id_first_station"),
            rs.getString("first_station_name"),
            rs.getInt("id_last_station"),
            rs.getString("last_station_name"),
            rs.getTimestamp("time_departure"),
            rs.getTimestamp("time_arrival")
        );
    }

    public static RunDTO toRunDTO(ResultSet rs) throws SQLException {
        return new RunDTO(
            rs.getInt("id_line"),
            rs.getString("name"),
            rs.getInt("id_convoy"),
            rs.getInt("id_staff"),
            rs.getString("staff_name"),
            rs.getString("surname"),
            rs.getString("email"),
            rs.getTimestamp("time_departure"),
            rs.getString("location")
        );
    }

    public static ConvoyTableDTO toConvoyTableDTO(ResultSet rs) throws SQLException {
        return new ConvoyTableDTO(
            rs.getInt("id_convoy"),
            rs.getString("model"),
            rs.getString("status"),
            rs.getInt("carriage_count"),
            rs.getInt("capacity"),
            rs.getString("model_type")
        );
    }

    public static void setRunKeyParams(PreparedStatement ps, int idLine, int idConvoy, int idStaff, Timestamp timeDeparture) throws SQLException {
        ps.setInt(1, idLine);
        ps.setInt(2, idConvoy);
        ps.setInt(3, idStaff);
        ps.setTimestamp(4, timeDeparture);
    }

    public static void setInsertRunParams(PreparedStatement ps, int idLine, int idConvoy, int idStaff, Timestamp timeDeparture, Timestamp timeArrival, int idFirstStation, int idLastStation) throws SQLException {
        ps.setInt(1, idLine);
        ps.setInt(2, idConvoy);
        ps.setInt(3, idStaff);
        ps.setTimestamp(4, timeDeparture);
        ps.setTimestamp(5, timeArrival);
        ps.setInt(6, idFirstStation);
        ps.setInt(7, idLastStation);
    }

    public static void setIdParam(PreparedStatement ps, int id) throws SQLException {
        ps.setInt(1, id);
    }

    public static void setIdAndTimestampParams(PreparedStatement ps, int id, Timestamp ts) throws SQLException {
        ps.setInt(1, id);
        ps.setTimestamp(2, ts);
    }

    public static void setReplaceFutureRunsParams(PreparedStatement ps, int newIdConvoy, int idConvoy) throws SQLException {
        ps.setInt(1, newIdConvoy);
        ps.setInt(2, idConvoy);
    }

    public static void setReplaceFutureRunsWithTimeParams(PreparedStatement ps, int newIdConvoy, int idConvoy, Timestamp timeDeparture, int idLine, int idStaff) throws SQLException {
        ps.setInt(1, newIdConvoy);
        ps.setInt(2, idConvoy);
        ps.setTimestamp(3, timeDeparture);
        ps.setInt(4, idLine);
        ps.setInt(5, idStaff);
    }

    public static void setUpdateRunStaffParams(PreparedStatement ps, int idStaff1, int idLine, int idConvoy, int idStaff, Timestamp timeDeparture) throws SQLException {
        ps.setInt(1, idStaff1);
        ps.setInt(2, idLine);
        ps.setInt(3, idConvoy);
        ps.setInt(4, idStaff);
        ps.setTimestamp(5, timeDeparture);
    }

    public static void setUpdateRunDepartureTimeParams(PreparedStatement ps, Timestamp newDeparture, int idLine, int idConvoy, int idStaff, Timestamp oldDeparture) throws SQLException {
        ps.setTimestamp(1, newDeparture);
        ps.setInt(2, idLine);
        ps.setInt(3, idConvoy);
        ps.setInt(4, idStaff);
        ps.setTimestamp(5, oldDeparture);
    }
}

