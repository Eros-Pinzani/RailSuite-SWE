package test.domain;

import domain.Carriage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarriageImpTest {
    private Carriage carriage;
    private final int id = 10;
    private final String model = "FrecciaRossa";
    private final String modelType = "AltaVelocità";
    private final int yearProduced = 2020;
    private final int capacity = 80;
    private final Integer idConvoy = 5;

    @BeforeEach
    void setUp() {
        carriage = Carriage.of(id, model, modelType, yearProduced, capacity, idConvoy);
    }

    @AfterEach
    void tearDown() {
        carriage = null;
    }

    @Test
    void getId() {
        assertEquals(id, carriage.getId());
    }

    @Test
    void getModel() {
        assertEquals(model, carriage.getModel());
    }

    @Test
    void getModelType() {
        assertEquals(modelType, carriage.getModelType());
    }

    @Test
    void getYearProduced() {
        assertEquals(yearProduced, carriage.getYearProduced());
    }

    @Test
    void getCapacity() {
        assertEquals(capacity, carriage.getCapacity());
    }

    @Test
    void getIdConvoy() {
        assertEquals(idConvoy, carriage.getIdConvoy());
    }

    @Test
    void setIdConvoy() {
        Integer newIdConvoy = 99;
        carriage.setIdConvoy(newIdConvoy);
        assertEquals(newIdConvoy, carriage.getIdConvoy());
        carriage.setIdConvoy(null);
        assertNull(carriage.getIdConvoy());
    }
}