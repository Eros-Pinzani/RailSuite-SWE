package domain;

import java.sql.Timestamp;


public interface StaffPool {
    enum ShiftStatus {
        AVAILABLE,
        ON_RUN,
        RELAX
    }

    int getIdStaff();

    int getIdStation();
    void setIdStation(int idStation);

    int getIdConvoy();
    void setIdConvoy(int idConvoy);

    Timestamp getShiftStart();
    void setShiftStart(Timestamp shiftStart);

    Timestamp getShiftEnd();
    void setShiftEnd(Timestamp shiftEnd);

    ShiftStatus getShiftStatus();
    void setShiftStatus(ShiftStatus shiftStatus);

    static StaffPool of(int idStaff, int idStation, int idConvoy, Timestamp shiftStart, Timestamp shiftEnd, ShiftStatus shiftStatus) {
        return new StaffPoolImp(idStaff, idStation, idConvoy, shiftStart, shiftEnd, shiftStatus);
    }
}
