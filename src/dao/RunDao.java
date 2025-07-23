package dao;

import domain.Run;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

public interface RunDao {
    static RunDao of() {
        return new RunDaoImp();
    }
    Run selectRunByLineAndConvoy(int idLine, int idConvoy) throws SQLException;
    Run selectRun(int idLine, int idConvoy, int idStaff) throws SQLException;
    Run selectRunByStaffAndConvoy(int idStaff, int idConvoy) throws SQLException;
    Run selectRunByStaffAndLine(int idStaff, int idLine) throws SQLException;
    List<Run> selectAllRuns() throws SQLException;
    boolean removeRun(int idLine, int idConvoy) throws SQLException;
    Run createRun(int idLine, int idConvoy, int idStaff, Time timeDeparture, Time timeArrival, int idFirstStation, int idLastStation) throws SQLException;
    boolean updateRun(int idLine, int idConvoy, int idStaff, Time timeDeparture, Time timeArrival, int idFirstStation, int idLastStation) throws SQLException;
    List<Run> selectRunsByStaff(int idStaff) throws SQLException;
    List<Run> selectRunsByLine(int idLine) throws SQLException;
    List<Run> selectRunsByConvoy(int idConvoy) throws SQLException;
    List<Run> selectRunsByFirstStation(int idFirstStation) throws SQLException;
    List<Run> selectRunsByLastStation(int idLastStation) throws SQLException;
    List<Run> selectRunsByFirstStationAndDeparture(int idFirstStation, Time timeDeparture) throws SQLException;

}
