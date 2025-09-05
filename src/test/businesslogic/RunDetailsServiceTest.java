package test.businesslogic;

import businessLogic.service.RunDetailsService;
import domain.Staff;
import org.junit.jupiter.api.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RunDetailsServiceTest {
    private RunDetailsService service;

    @BeforeEach
    void setUp() {
        service = new RunDetailsService();
    }

    @Test
    void testSelectRun_existing() {
        // Sostituire con valori reali presenti nel DB
        int idLine = 1, idConvoy = 1, idStaff = 1;
        Timestamp timeDeparture = Timestamp.valueOf(LocalDateTime.now().plusDays(1).withHour(8).withMinute(0));
        try {
            assertDoesNotThrow(() -> service.selectRun(idLine, idConvoy, idStaff, timeDeparture));
        } catch (Exception e) {
            // Se non esistono dati, il test fallirà
            fail("Dati di test non presenti nel DB: " + e.getMessage());
        }
    }

    @Test
    void testSelectRun_notExisting() {
        // Deve restituire null se la run non esiste
        assertNull(service.selectRun(-1, -1, -1, Timestamp.valueOf(LocalDateTime.now())));
    }

    @Test
    void testSelectTimeTable() {
        // Sostituire con valori reali presenti nel DB
        int idLine = 1, idFirstStation = 1;
        String departureTime = "08:00:00";
        assertDoesNotThrow(() -> service.selectTimeTable(idLine, idFirstStation, departureTime));
    }

    @Test
    void testSelectConvoy_existing() {
        // Sostituire con idConvoy reale
        int idConvoy = 1;
        assertDoesNotThrow(() -> service.selectConvoy(idConvoy));
    }

    @Test
    void testSelectConvoy_notExisting() {
        // Deve restituire null se il convoglio non esiste
        assertNull(service.selectConvoy(-1));
    }

    @Test
    void testHasOperatorConflicts() {
        // Sostituire con valori reali
        int idStaff = 1;
        Timestamp timeDeparture = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
        assertDoesNotThrow(() -> service.hasOperatorConflicts(idStaff, timeDeparture));
    }

    @Test
    void testCheckAvailabilityOfOperator() {
        List<Staff> result = service.checkAvailabilityOfOperator();
        assertNotNull(result);
    }

}
