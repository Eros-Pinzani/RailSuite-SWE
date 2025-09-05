package test.domain;

import domain.Run;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class RunImpTest {
    private Run run;
    private final Integer idLine = 1;
    private final String lineName = "Linea Blu";
    private final Integer idConvoy = 2;
    private final Integer idStaff = 3;
    private final String staffName = "Gianni";
    private final String staffSurname = "Bianchi";
    private final Integer idFirstStation = 10;
    private final String firstStationName = "Roma";
    private final Integer idLastStation = 20;
    private final String lastStationName = "Napoli";
    private final Timestamp now = new Timestamp(System.currentTimeMillis());
    private final Timestamp before = new Timestamp(now.getTime() + 3600_000); // +1h
    private final Timestamp after = new Timestamp(now.getTime() - 3600_000); // -1h
    private final Timestamp futureArrival = new Timestamp(now.getTime() + 7200_000); // +2h
    private final Timestamp pastArrival = new Timestamp(now.getTime() - 1800_000); // -30min

    @BeforeEach
    void setUp() {
        // default: run not started yet
        run = Run.of(idLine, lineName, idConvoy, idStaff, staffName, staffSurname, idFirstStation, firstStationName, idLastStation, lastStationName, before, futureArrival);
    }

    @AfterEach
    void tearDown() {
        run = null;
    }

    @Test
    void getIdLine() {
        assertEquals(idLine, run.getIdLine());
    }

    @Test
    void getLineName() {
        assertEquals(lineName, run.getLineName());
    }

    @Test
    void getIdConvoy() {
        assertEquals(idConvoy, run.getIdConvoy());
    }

    @Test
    void getIdStaff() {
        assertEquals(idStaff, run.getIdStaff());
    }

    @Test
    void getStaffNameSurname() {
        assertEquals(staffName + " " + staffSurname, run.getStaffNameSurname());
    }

    @Test
    void getIdFirstStation() {
        assertEquals(idFirstStation, run.getIdFirstStation());
    }

    @Test
    void getFirstStationName() {
        assertEquals(firstStationName, run.getFirstStationName());
    }

    @Test
    void getIdLastStation() {
        assertEquals(idLastStation, run.getIdLastStation());
    }

    @Test
    void getLastStationName() {
        assertEquals(lastStationName, run.getLastStationName());
    }

    @Test
    void getTimeDeparture() {
        assertEquals(before, run.getTimeDeparture());
    }

    @Test
    void getTimeArrival() {
        assertEquals(futureArrival, run.getTimeArrival());
    }

    @Test
    void getStatus_beforeRun() {
        // ora < partenza
        assertEquals(Run.RunStatus.BEFORE_RUN, run.getStatus());
    }

    @Test
    void getStatus_run() {
        // ora tra partenza e arrivo
        Run running = Run.of(idLine, lineName, idConvoy, idStaff, staffName, staffSurname, idFirstStation, firstStationName, idLastStation, lastStationName, after, futureArrival);
        assertEquals(Run.RunStatus.RUN, running.getStatus());
    }

    @Test
    void getStatus_afterRun() {
        // ora > arrivo
        Run finished = Run.of(idLine, lineName, idConvoy, idStaff, staffName, staffSurname, idFirstStation, firstStationName, idLastStation, lastStationName, after, pastArrival);
        assertEquals(Run.RunStatus.AFTER_RUN, finished.getStatus());
    }

    @Test
    void getStatus_nullIfTimesNull() {
        Run nullRun = Run.of(idLine, lineName, idConvoy, idStaff, staffName, staffSurname, idFirstStation, firstStationName, idLastStation, lastStationName, null, null);
        assertNull(nullRun.getStatus());
    }
}