package test;

import businessLogic.service.ManageRunService;
import domain.*;
import org.junit.jupiter.api.*;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ManageRunServiceTest {
    private ManageRunService service;

    @BeforeEach
    void setUp() {
        service = new ManageRunService();
    }

    @Test
    void testGetAllRun() {
        List<Run> runs = service.getAllRun();
        assertNotNull(runs);
    }

    @Test
    void testFilterRunRaws() {
        List<Run> allRuns = service.getAllRun();
        if (!allRuns.isEmpty()) {
            Run sample = allRuns.getFirst();
            List<Run> filtered = service.filterRunRaws(sample.getLineName(), String.valueOf(sample.getIdConvoy()), sample.getStaffNameSurname(), sample.getFirstStationName(), null);
            assertTrue(filtered.stream().anyMatch(r -> runsSuperKeyEquals(r, sample)));
        } else {
            List<Run> filtered = service.filterRunRaws("fake", "fake", "fake", "fake", null);
            assertTrue(filtered.isEmpty());
        }
    }

    /**
     * Confronta due Run usando la superchiave del DB: id_staff, id_convoy, id_line, id_first_station, time_departure
     */
    private boolean runsSuperKeyEquals(Run a, Run b) {
        return Objects.equals(a.getIdStaff(), b.getIdStaff()) &&
               Objects.equals(a.getIdConvoy(), b.getIdConvoy()) &&
               Objects.equals(a.getIdLine(), b.getIdLine()) &&
               Objects.equals(a.getIdFirstStation(), b.getIdFirstStation()) &&
               Objects.equals(a.getTimeDeparture(), b.getTimeDeparture());
    }

    @Test
    void testSearchRunsByDay() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp future = new Timestamp(System.currentTimeMillis() + 100000);
        List<Run> runs = service.searchRunsByDay(null, null, null, null, now, future);
        assertNotNull(runs);
    }

    @Test
    void testUpdateCarriageDepotStatuses() {
        // Il metodo non lancia eccezioni e aggiorna lo stato delle carrozze in cleaning/maintenance
        assertDoesNotThrow(() -> service.updateCarriageDepotStatuses());
    }

    @Test
    void testCompleteRun() {
        // Questo test richiede un Run valido con notifiche approvate associate
        List<Run> runs = service.getAllRun();
        if (!runs.isEmpty()) {
            Run run = runs.getFirst();
            assertDoesNotThrow(() -> service.completeRun(run));
        }
    }
}
