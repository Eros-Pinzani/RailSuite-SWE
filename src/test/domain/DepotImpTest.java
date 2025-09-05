package test.domain;

import domain.Depot;
import domain.CarriageDepot;
import domain.CarriageDepot.StatusOfCarriage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DepotImpTest {
    private Depot depot;
    private final int idDepot = 1;
    private CarriageDepot carriage1;
    private CarriageDepot carriage2;

    @BeforeEach
    void setUp() {
        depot = Depot.of(idDepot);
        carriage1 = CarriageDepot.of(idDepot, 100, Timestamp.valueOf("2025-09-05 10:00:00"), null, StatusOfCarriage.AVAILABLE);
        carriage2 = CarriageDepot.of(idDepot, 101, Timestamp.valueOf("2025-09-05 11:00:00"), null, StatusOfCarriage.CLEANING);
    }

    @AfterEach
    void tearDown() {
        depot = null;
        carriage1 = null;
        carriage2 = null;
    }

    @Test
    void getIdDepot() {
        assertEquals(idDepot, depot.getIdDepot());
    }

    @Test
    void getCarriages_initialEmpty() {
        assertTrue(depot.getCarriages().isEmpty());
    }

    @Test
    void addCarriage() {
        depot.addCarriage(carriage1);
        List<CarriageDepot> carriages = depot.getCarriages();
        assertEquals(1, carriages.size());
        assertTrue(carriages.contains(carriage1));
        depot.addCarriage(carriage2);
        carriages = depot.getCarriages();
        assertEquals(2, carriages.size());
        assertTrue(carriages.contains(carriage2));
    }

    @Test
    void removeCarriage() {
        depot.addCarriage(carriage1);
        depot.addCarriage(carriage2);
        assertTrue(depot.removeCarriage(100));
        List<CarriageDepot> carriages = depot.getCarriages();
        assertEquals(1, carriages.size());
        assertFalse(carriages.contains(carriage1));
        // Rimozione di id non presente
        assertFalse(depot.removeCarriage(999));
        assertEquals(1, depot.getCarriages().size());
    }

    @Test
    void getCarriages_unmodifiable() {
        depot.addCarriage(carriage1);
        List<CarriageDepot> carriages = depot.getCarriages();
        assertThrows(UnsupportedOperationException.class, () -> carriages.add(carriage2));
    }
}