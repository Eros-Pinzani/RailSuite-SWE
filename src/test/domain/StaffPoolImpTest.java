package test.domain;

import domain.StaffPool;
import domain.StaffPool.ShiftStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class StaffPoolImpTest {
    private StaffPool staffPool;
    private final int idStaff = 1;
    private final int idStation = 2;
    private final int idConvoy = 3;
    private final Timestamp shiftStart = Timestamp.valueOf("2025-09-05 08:00:00");
    private final Timestamp shiftEnd = Timestamp.valueOf("2025-09-05 16:00:00");
    private final ShiftStatus shiftStatus = ShiftStatus.AVAILABLE;

    @BeforeEach
    void setUp() {
        staffPool = StaffPool.of(idStaff, idStation, idConvoy, shiftStart, shiftEnd, shiftStatus);
    }

    @AfterEach
    void tearDown() {
        staffPool = null;
    }

    @Test
    void getIdStaff() {
        assertEquals(idStaff, staffPool.getIdStaff());
    }

    @Test
    void getIdStation() {
        assertEquals(idStation, staffPool.getIdStation());
    }

    @Test
    void setIdStation() {
        int newIdStation = 99;
        staffPool.setIdStation(newIdStation);
        assertEquals(newIdStation, staffPool.getIdStation());
    }

    @Test
    void getIdConvoy() {
        assertEquals(idConvoy, staffPool.getIdConvoy());
    }

    @Test
    void setIdConvoy() {
        int newIdConvoy = 88;
        staffPool.setIdConvoy(newIdConvoy);
        assertEquals(newIdConvoy, staffPool.getIdConvoy());
    }

    @Test
    void getShiftStart() {
        assertEquals(shiftStart, staffPool.getShiftStart());
    }

    @Test
    void setShiftStart() {
        Timestamp newStart = Timestamp.valueOf("2025-09-05 09:00:00");
        staffPool.setShiftStart(newStart);
        assertEquals(newStart, staffPool.getShiftStart());
    }

    @Test
    void getShiftEnd() {
        assertEquals(shiftEnd, staffPool.getShiftEnd());
    }

    @Test
    void setShiftEnd() {
        Timestamp newEnd = Timestamp.valueOf("2025-09-05 17:00:00");
        staffPool.setShiftEnd(newEnd);
        assertEquals(newEnd, staffPool.getShiftEnd());
    }

    @Test
    void getShiftStatus() {
        assertEquals(shiftStatus, staffPool.getShiftStatus());
    }

    @Test
    void setShiftStatus() {
        staffPool.setShiftStatus(ShiftStatus.ON_RUN);
        assertEquals(ShiftStatus.ON_RUN, staffPool.getShiftStatus());
        staffPool.setShiftStatus(ShiftStatus.RELAX);
        assertEquals(ShiftStatus.RELAX, staffPool.getShiftStatus());
    }
}