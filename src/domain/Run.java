package domain;

import java.sql.Time;

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
    static Run of(int idLine, int idConvoy, int idStaff, java.sql.Time timeDeparture, Time timeArrival, int idFirstsStation, int idLastStation) {
        return new RunImp(idLine, idConvoy, idStaff, timeDeparture, timeArrival, idFirstsStation, idLastStation);
    }

    /** @return the line id */
    int getIdLine();
    /** @return the convoy id */
    int getIdConvoy();
    /** @return the staff id */
    int getIdStaff();
    /** @return the departure time */
    Time getTimeDeparture();
    /** @return the arrival time */
    Time getTimeArrival();
    /** @return the first station id */
    int getIdFirstStation();
    /** @return the last station id */
    int getIdLastStation();
}
