package businessLogic.service;


import dao.*;
import domain.Convoy;
import domain.DTO.ConvoyTableDTO;
import domain.DTO.RunDTO;
import domain.DTO.TimeTableDTO;
import domain.Run;
import domain.Staff;


import java.sql.Timestamp;
import java.time.LocalDateTime;

import java.time.Duration;
import java.util.List;

public class RunDetailsService {
    private final RunDao runDao = RunDao.of();
    private RunDTO run;
    private final LineStationDao lineStationDao = LineStationDao.of();
    private TimeTableDTO timeTable;
    private final ConvoyDao convoyDao = ConvoyDao.of();
    private Convoy convoy;
    private final ConvoyPoolDao convoyPoolDao = ConvoyPoolDao.of();
    private final StaffDao staffDao = StaffDao.of();

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
            this.timeTable = new TimeTableDTO(idLine, stationArrAndDepDTOList);
            return this.timeTable;
        } catch (Exception e) {
            throw new RuntimeException("Error selecting timetable", e);
        }
    }

    public Convoy selectConvoy(int idConvoy) {
        if (convoy != null && convoy.getId() == idConvoy) return convoy;
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

    public List<ConvoyTableDTO> checkAvailabilityOfConvoy() {
        int firstStation = timeTable.getStationArrAndDepDTOList().getFirst().getIdStation();
        if (convoy != null) {
            try {
                return convoyPoolDao.checkConvoyAvailability(firstStation);
            } catch (Exception e) {
                throw new RuntimeException("Error checking convoy availability", e);
            }
        } else {
            throw new IllegalStateException("Convoy details have not been selected yet.");
        }
    }

    public List<ConvoyTableDTO> getFutureRunsOfCurrentConvoy(int idConvoy, Timestamp timeDeparture) {
        try {
            return runDao.selectRunsByConvoyAndTimeForTakeFutureRuns(idConvoy, timeDeparture);
        } catch (Exception e) {
            throw new RuntimeException("Error selecting run details", e);
        }
    }

    public void replaceFutureRunsConvoy(int idConvoy, int newIdConvoy) {
        try {
            if(!runDao.replaceFutureRunsConvoy(idConvoy, newIdConvoy, run)) {
                throw new RuntimeException("Failed to replace future runs convoy");
            }
            run = selectRun(run.getIdLine(), newIdConvoy, run.getIdStaff(), run.getTimeDeparture());
            convoy = selectConvoy(newIdConvoy);

        } catch (Exception e) {
            throw new RuntimeException("Error replacing future runs convoy", e);
        }
    }

    public List<Staff> checkAvailabilityOfOperator() {
        try {
            return staffDao.checkOperatorAvailability(run.getIdStaff(), run.getIdLine(), run.getTimeDeparture());
        } catch (Exception e) {
            throw new RuntimeException("Error checking operator availability", e);
        }
    }

    public void changeOperator(Staff selectedStaff) {
        try {
            runDao.updateRunStaff(run.getIdLine(), run.getIdConvoy(), run.getIdStaff(), run.getTimeDeparture(), selectedStaff.getIdStaff());
        } catch (Exception e) {
            throw new RuntimeException("Error changing operator", e);
        }
    }

    /**
     * Verifica se la nuova partenza rispetta tutti i vincoli di staff e convoglio.
     * - Minimo 15 minuti dal momento attuale
     * - Massimo domani alle 23:59
     * - Staff: non superare 10h di turno, almeno 15 minuti di pausa tra le run
     * - Convoglio: nessun conflitto con altre run
     */
    public boolean isDepartureTimeValid(int staffId, int convoyId, Timestamp newDeparture) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minAllowed = now.plusMinutes(15);
        LocalDateTime maxAllowed = now.plusDays(1).withHour(23).withMinute(59);
        LocalDateTime newDep = newDeparture.toLocalDateTime();
        if (newDep.isBefore(minAllowed) || newDep.isAfter(maxAllowed)) {
            return false;
        }
        try {
            List<Run> staffRuns = runDao.selectRunsByStaff(staffId);
            for (Run r : staffRuns) {
                LocalDateTime rDep = r.getTimeDeparture().toLocalDateTime();
                LocalDateTime rArr = r.getTimeArrival().toLocalDateTime();
                if (Math.abs(Duration.between(rArr, newDep).toMinutes()) < 15) {
                    return false;
                }
                if (Duration.between(rDep, newDep).toHours() > 10) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        try {
            List<Run> convoyRuns = runDao.selectRunsByConvoy(convoyId);
            for (Run r : convoyRuns) {
                LocalDateTime rDep = r.getTimeDeparture().toLocalDateTime();
                LocalDateTime rArr = r.getTimeArrival().toLocalDateTime();
                if (!newDep.isBefore(rDep) && !newDep.isAfter(rArr)) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public boolean updateRunDepartureTime(int lineId, int convoyId, int staffId, Timestamp oldDeparture, Timestamp newDeparture) {
        try {
            return runDao.updateRunDepartureTime(lineId, convoyId, staffId, oldDeparture, newDeparture);
        } catch (Exception e) {
            throw new RuntimeException("Error updating departure time", e);
        }
    }
}
