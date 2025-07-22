package domain;

import java.sql.Timestamp;

class StaffPoolImp implements StaffPool {
    private final int idStaff;
    private int idStation;
    private int idConvoy;
    private Timestamp shiftStart;
    private Timestamp shiftEnd;
    private ShiftStatus shiftStatus;

    public StaffPoolImp(int idStaff, int idStation, int idConvoy, Timestamp shiftStart, Timestamp shiftEnd, ShiftStatus shiftStatus) {
        this.idStaff = idStaff;
        this.idStation = idStation;
        this.idConvoy = idConvoy;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.shiftStatus = shiftStatus;
    }

    public int getIdStaff() {
        return idStaff;
    }

    public int getIdStation() {
        return idStation;
    }

    public void setIdStation(int idStation) {
        this.idStation = idStation;
    }

    public int getIdConvoy() {
        return idConvoy;
    }

    public void setIdConvoy(int idConvoy) {
        this.idConvoy = idConvoy;
    }

    public Timestamp getShiftStart() {
        return shiftStart;
    }

    public void setShiftStart(Timestamp shiftStart) {
        this.shiftStart = shiftStart;
    }

    public Timestamp getShiftEnd() {
        return shiftEnd;
    }

    public void setShiftEnd(Timestamp shiftEnd) {
        this.shiftEnd = shiftEnd;
    }

    public ShiftStatus getShiftStatus() {
        return shiftStatus;
    }

    public void setShiftStatus(ShiftStatus shiftStatus) {
        this.shiftStatus = shiftStatus;
    }
}
