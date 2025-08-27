package dao;


import java.util.List;



/**
 * Data Access Object interface for StaffPool entities.
 * Defines methods for queries and updates on staff pool records.
 */
public interface StaffPoolDao {
    enum ShiftStatus {
        AVAILABLE,
        ON_RUN,
        RELAX
    }

    static StaffPoolDao of() {
        return new StaffPoolDaoImp();
    }

    /**
     * Finds available operators for a run at a specific station on a given date and time.
     * @param idStation the station id
     * @param date the date of the run
     * @param time the time of the run
     * @return list of available StaffDTO objects
     */
    List<domain.DTO.StaffDTO> findAvailableOperatorsForRun(int idStation, java.time.LocalDate date, String time);
}
