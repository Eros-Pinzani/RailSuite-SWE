package businessLogic.service;


import dao.ConvoyDao;
import dao.ConvoyPoolDao;
import dao.LineStationDao;
import dao.RunDao;
import domain.Convoy;
import domain.DTO.RunDTO;
import domain.DTO.TimeTableDTO;


import java.sql.Timestamp;
import java.util.List;

public class RunDetailsService {
    private final RunDao runDao = RunDao.of();
    private RunDTO run;
    private final LineStationDao lineStationDao = LineStationDao.of();
    private TimeTableDTO timeTable;
    private final ConvoyDao convoyDao = ConvoyDao.of();
    private Convoy convoy;
    private final ConvoyPoolDao convoyPoolDao = ConvoyPoolDao.of();

    public RunDetailsService () {}

    public RunDTO selectRun(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture)  {
        if (run != null) return run;
        try {
            return run = runDao.selectRunDTODetails(idLine, idConvoy, idStaff, timeDeparture);
        } catch (Exception e) {
            throw new RuntimeException("Error selecting run details", e);
        }
    }

    public RunDTO selectRun() {
        if (run != null) return run;
        throw new IllegalStateException("Run details have not been selected yet.");
    }

    public TimeTableDTO selectTimeTable(int idLine, int idFirstStation, String departureTime) {
        try {
            List<TimeTableDTO.StationArrAndDepDTO> stationArrAndDepDTOList = lineStationDao.findTimeTableForRun(idLine, idFirstStation, departureTime);
            return new TimeTableDTO(idLine, stationArrAndDepDTOList);
        } catch (Exception e) {
            throw new RuntimeException("Error selecting timetable", e);
        }
    }

    public Convoy selectConvoy(int idConvoy) {
        try {
            return convoy = convoyDao.selectConvoy(idConvoy);
        } catch (Exception e) {
            throw new RuntimeException("Error selecting convoy", e);
        }
    }

    /**
     * Checks if the operator has other runs scheduled after the selected departure time.
     * @param idStaff operator's id
     * @param timeDeparture departure time of the selected run
     * @return true if there are conflicts, false otherwise
     */
    public boolean hasOperatorConflicts(int idStaff, Timestamp timeDeparture) {
        try {
            return runDao.findRunsByStaffAfterTime(idStaff, timeDeparture);
        } catch (Exception e) {
            throw new RuntimeException("Error checking operator conflicts", e);
        }
    }

    public boolean hasRunConflict() {
        if ( run != null) {
            try {
                return runDao.findRunsByConvoyAfterTime(run.getIdLine(), run.getIdConvoy(), run.getIdStaff(), run.getTimeDeparture());
            } catch (Exception e) {
                throw new RuntimeException("Error checking convoy conflicts", e);
            }
        } else {
            throw new IllegalStateException("Run details have not been selected yet.");
        }
    }

    public boolean deleteRun(){
        if (run != null) {
            try {
                return runDao.deleteRun(run.getIdLine(), run.getIdConvoy(), run.getIdStaff(), run.getTimeDeparture());
            } catch (Exception e) {
                throw new RuntimeException("Error deleting run", e);
            }
        } else {
            throw new IllegalStateException("Run details have not been selected yet.");
        }
    }

    public boolean hasConvoyConflict() {
        if (convoy != null) {
            try {
                return convoyPoolDao.checkAndUpdateConvoyStatus(convoy.getId());
            } catch (Exception e) {
                throw new RuntimeException("Error checking convoy conflict", e);
            }
        } else {
            throw new IllegalStateException("Convoy details have not been selected yet.");
        }
    }
}
