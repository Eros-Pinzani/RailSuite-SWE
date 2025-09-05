package test.domain;

import domain.Convoy;
import domain.Carriage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConvoyImpTest {
    private Convoy convoy;
    private List<Carriage> carriages;
    private final int idConvoy = 42;
    private Carriage carriage1;
    private Carriage carriage2;
    private Carriage carriage3;

    @BeforeEach
    void setUp() {
        carriage1 = Carriage.of(1, "ModelA", "TypeA", 2010, 50, null);
        carriage2 = Carriage.of(2, "ModelB", "TypeB", 2015, 60, null);
        carriage3 = Carriage.of(3, "ModelC", "TypeC", 2020, 70, null);
        carriages = new ArrayList<>();
        carriages.add(carriage1);
        carriages.add(carriage2);
        convoy = Convoy.of(idConvoy, carriages);
    }

    @AfterEach
    void tearDown() {
        convoy = null;
        carriages = null;
    }

    @Test
    void getId() {
        assertEquals(idConvoy, convoy.getId());
    }

    @Test
    void getCarriages() {
        List<Carriage> result = convoy.getCarriages();
        assertEquals(2, result.size());
        assertTrue(result.contains(carriage1));
        assertTrue(result.contains(carriage2));
    }

    @Test
    void addCarriage() {
        assertTrue(convoy.addCarriage(carriage3));
        assertTrue(convoy.getCarriages().contains(carriage3));
        assertEquals(3, convoy.convoySize());
        // Non aggiunge doppioni
        assertFalse(convoy.addCarriage(carriage3));
        assertEquals(3, convoy.convoySize());
    }

    @Test
    void removeCarriage() {
        assertTrue(convoy.removeCarriage(carriage1));
        assertFalse(convoy.getCarriages().contains(carriage1));
        assertEquals(1, convoy.convoySize());
        // Non rimuove se non presente
        assertFalse(convoy.removeCarriage(carriage3));
        assertEquals(1, convoy.convoySize());
    }

    @Test
    void convoySize() {
        assertEquals(2, convoy.convoySize());
        convoy.addCarriage(carriage3);
        assertEquals(3, convoy.convoySize());
        convoy.removeCarriage(carriage2);
        assertEquals(2, convoy.convoySize());
    }
}