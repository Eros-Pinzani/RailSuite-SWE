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
