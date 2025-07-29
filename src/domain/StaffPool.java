package domain;

import java.sql.Timestamp;

/**
 * Interface representing a StaffPool entity.
 * Provides factory method and accessors for staff pool properties and shift management.
 */
public interface StaffPool {
    /**
     * Enum representing the shift status of a staff member.
     */
    enum ShiftStatus {
        AVAILABLE,
        ON_RUN,
        RELAX
    }

    /** @return the staff id */
    int getIdStaff();
    /** @return the station id */
    int getIdStation();
    /** Sets the station id for the staff */
    void setIdStation(int idStation);
    /** @return the convoy id */
    int getIdConvoy();
    /** Sets the convoy id for the staff */
    void setIdConvoy(int idConvoy);
    /** @return the shift start timestamp */
    Timestamp getShiftStart();
    /** Sets the shift start timestamp */
    void setShiftStart(Timestamp shiftStart);
    /** @return the shift end timestamp */
    Timestamp getShiftEnd();
    /** Sets the shift end timestamp */
    void setShiftEnd(Timestamp shiftEnd);
    /** @return the shift status */
    ShiftStatus getShiftStatus();
    /** Sets the shift status */
    void setShiftStatus(ShiftStatus shiftStatus);

    /**
     * Factory method to create a StaffPool instance.
     * @param idStaff the staff id
     * @param idStation the station id
     * @param idConvoy the convoy id
     * @param shiftStart the shift start timestamp
     * @param shiftEnd the shift end timestamp
     * @param shiftStatus the shift status
     * @return a StaffPool instance
     */
    static StaffPool of(int idStaff, int idStation, int idConvoy, Timestamp shiftStart, Timestamp shiftEnd, ShiftStatus shiftStatus) {
        return new StaffPoolImp(idStaff, idStation, idConvoy, shiftStart, shiftEnd, shiftStatus);
    }
}
