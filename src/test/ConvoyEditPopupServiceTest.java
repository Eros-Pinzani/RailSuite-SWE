package test;

import businessLogic.service.ConvoyEditPopupService;
import domain.Carriage;
import domain.Convoy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConvoyEditPopupServiceTest {
    private Convoy convoy;
    private Carriage carriage1;
    private Carriage carriage2;
    private ConvoyEditPopupService service;

    @BeforeEach
    void setUp() {
        carriage1 = Carriage.of(1001, "TestModel1", "TypeA", 2020, 50, null);
        carriage2 = Carriage.of(1002, "TestModel2", "TypeB", 2021, 60, null);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(carriage1);
        convoy = Convoy.of(2001, carriages);
        service = new ConvoyEditPopupService();
    }

    @AfterEach
    void tearDown() {
        convoy = null;
        carriage1 = null;
        carriage2 = null;
        service = null;
    }

    @Test
    void removeCarriageFromConvoy_success() throws Exception {
        assertTrue(convoy.getCarriages().contains(carriage1));
        service.removeCarriageFromConvoy(convoy, carriage1);
        assertFalse(convoy.getCarriages().contains(carriage1));
    }

    @Test
    void removeCarriageFromConvoy_nullArguments() {
        assertThrows(IllegalArgumentException.class, () -> service.removeCarriageFromConvoy(null, carriage1));
        assertThrows(IllegalArgumentException.class, () -> service.removeCarriageFromConvoy(convoy, null));
    }

    @Test
    void removeCarriageFromConvoy_notInConvoy() {
        assertFalse(convoy.getCarriages().contains(carriage2));
        assertThrows(IllegalArgumentException.class, () -> service.removeCarriageFromConvoy(convoy, carriage2));
    }

    @Test
    void addCarriagesToConvoy_success() {
        List<Carriage> toAdd = List.of(carriage2);
        assertFalse(convoy.getCarriages().contains(carriage2));
        service.addCarriagesToConvoy(convoy, toAdd);
        assertTrue(convoy.getCarriages().contains(carriage2));
    }

    @Test
    void addCarriagesToConvoy_nullOrEmpty() {
        assertThrows(IllegalArgumentException.class, () -> service.addCarriagesToConvoy(null, List.of(carriage2)));
        assertThrows(IllegalArgumentException.class, () -> service.addCarriagesToConvoy(convoy, null));
        assertThrows(IllegalArgumentException.class, () -> service.addCarriagesToConvoy(convoy, new ArrayList<>()));
    }

    @Test
    void addCarriagesToConvoy_alreadyPresent() {
        List<Carriage> toAdd = List.of(carriage1);
        assertTrue(convoy.getCarriages().contains(carriage1));
        assertThrows(IllegalArgumentException.class, () -> service.addCarriagesToConvoy(convoy, toAdd));
    }
}