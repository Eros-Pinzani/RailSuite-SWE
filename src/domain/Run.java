package domain;

import java.sql.Timestamp;

/**
 * Interface representing a Run entity.
 * Provides factory method and accessors for run properties.
 */
public interface Run {
    /**
     * Factory method to create a Run instance.
     * @param idLine the line id
     * @param idConvoy the convoy id
     * @param idStaff the staff id
     * @param timeDeparture the departure time
     * @param timeArrival the arrival time
     * @param idFirstsStation the first station id
     * @param idLastStation the last station id
     * @return a Run instance
     */
    static Run of(int idLine, String lineName,  int idConvoy, int idStaff, String staffName, String staffSurname,
                  Timestamp timeDeparture, Timestamp timeArrival, int idFirstsStation, String firstStationName,
                  int idLastStation, String lastStationName) {
        return new RunImp(idLine, lineName, idConvoy, idStaff, staffName, staffSurname, timeDeparture, timeArrival,
                idFirstsStation, firstStationName, idLastStation, lastStationName);
    }
    enum RunStatus {
        RUN, BEFORE_RUN, AFTER_RUN
    }

    /** @return the line id */
    int getIdLine();
    /** @return the line name */
    String getLineName();
    /** @return the convoy id */
    int getIdConvoy();
    /** @return the staff id */
    int getIdStaff();
    /** @return the staff name and surname */
    String getStaffNameSurname();
    /** @return the departure time */
    Timestamp getTimeDeparture();
    /** @return the arrival time */
    Timestamp getTimeArrival();
    /** @return the first station id */
    int getIdFirstStation();
    /** @return the first station name */
    String getFirstStationName();
    /** @return the last station id */
    int getIdLastStation();
    /** @return the last station name */
    String getLastStationName();
    /** @return the status of the run */
    RunStatus getStatus();
}
