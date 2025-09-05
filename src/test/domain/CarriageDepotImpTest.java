package test.domain;

import domain.CarriageDepot;
import domain.CarriageDepot.StatusOfCarriage;
import java.sql.Timestamp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarriageDepotImpTest {
    private CarriageDepot depot;
    private final int idDepot = 1;
    private final int idCarriage = 100;
    private final Timestamp timeEntered = Timestamp.valueOf("2025-09-05 10:00:00");
    private final Timestamp timeExited = Timestamp.valueOf("2025-09-05 12:00:00");
    private final StatusOfCarriage status = StatusOfCarriage.CLEANING;

    @BeforeEach
    void setUp() {
        depot = CarriageDepot.of(idDepot, idCarriage, timeEntered, timeExited, status);
    }

    @AfterEach
    void tearDown() {
        depot = null;
    }

    @Test
    void getIdDepot() {
        assertEquals(idDepot, depot.getIdDepot());
    }

    @Test
    void getIdCarriage() {
        assertEquals(idCarriage, depot.getIdCarriage());
    }

    @Test
    void getTimeEntered() {
        assertEquals(timeEntered, depot.getTimeEntered());
    }

    @Test
    void getTimeExited() {
        assertEquals(timeExited, depot.getTimeExited());
    }

    @Test
    void getStatusOfCarriage() {
        assertEquals(status, depot.getStatusOfCarriage());
    }

    @Test
    void setTimeEntered() {
        Timestamp newTime = Timestamp.valueOf("2025-09-05 11:00:00");
        depot.setTimeEntered(newTime);
        assertEquals(newTime, depot.getTimeEntered());
    }

    @Test
    void setTimeExited() {
        Timestamp newTime = Timestamp.valueOf("2025-09-05 13:00:00");
        depot.setTimeExited(newTime);
        assertEquals(newTime, depot.getTimeExited());
    }

    @Test
    void setStatusOfCarriage() {
        depot.setStatusOfCarriage(StatusOfCarriage.AVAILABLE);
        assertEquals(StatusOfCarriage.AVAILABLE, depot.getStatusOfCarriage());
    }
}