package domain;

import java.sql.Timestamp;

/**
 * Implementation of the StaffPool interface.
 * Stores and manages the state of a staff member in the pool and their shift.
 */
class StaffPoolImp implements StaffPool {
    private final int idStaff;
    private int idStation;
    private int idConvoy;
    private Timestamp shiftStart;
    private Timestamp shiftEnd;
    private ShiftStatus shiftStatus;

    /**
     * Constructs a StaffPoolImp with all properties.
     * @param idStaff the staff id
     * @param idStation the station id
     * @param idConvoy the convoy id
     * @param shiftStart the shift start timestamp
     * @param shiftEnd the shift end timestamp
     * @param shiftStatus the shift status
     */
    public StaffPoolImp(int idStaff, int idStation, int idConvoy, Timestamp shiftStart, Timestamp shiftEnd, ShiftStatus shiftStatus) {
        this.idStaff = idStaff;
        this.idStation = idStation;
        this.idConvoy = idConvoy;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.shiftStatus = shiftStatus;
    }

    @Override
    public int getIdStaff() {
        return idStaff;
    }

    @Override
    public int getIdStation() {
        return idStation;
    }

    @Override
    public void setIdStation(int idStation) {
        this.idStation = idStation;
    }

    @Override
    public int getIdConvoy() {
        return idConvoy;
    }

    @Override
    public void setIdConvoy(int idConvoy) {
        this.idConvoy = idConvoy;
    }

    @Override
    public Timestamp getShiftStart() {
        return shiftStart;
    }

    @Override
    public void setShiftStart(Timestamp shiftStart) {
        this.shiftStart = shiftStart;
    }

    @Override
    public Timestamp getShiftEnd() {
        return shiftEnd;
    }

    @Override
    public void setShiftEnd(Timestamp shiftEnd) {
        this.shiftEnd = shiftEnd;
    }

    @Override
    public ShiftStatus getShiftStatus() {
        return shiftStatus;
    }

    @Override
    public void setShiftStatus(ShiftStatus shiftStatus) {
        this.shiftStatus = shiftStatus;
    }
}
