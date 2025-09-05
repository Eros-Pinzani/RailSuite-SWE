package test.domain;

import domain.Station;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationImpTest {
    private Station station;
    private final int idStation = 77;
    private final String location = "Firenze";
    private final int numBins = 4;
    private final String serviceDescription = "Servizi igienici, bar, biglietteria";
    private final boolean isHead = true;

    @BeforeEach
    void setUp() {
        station = Station.of(idStation, location, numBins, serviceDescription, isHead);
    }

    @AfterEach
    void tearDown() {
        station = null;
    }

    @Test
    void getIdStation() {
        assertEquals(idStation, station.getIdStation());
    }

    @Test
    void getLocation() {
        assertEquals(location, station.getLocation());
    }

    @Test
    void getNumBins() {
        assertEquals(numBins, station.getNumBins());
    }

    @Test
    void getServiceDescription() {
        assertEquals(serviceDescription, station.getServiceDescription());
    }

    @Test
    void isHead() {
        assertEquals(isHead, station.isHead());
    }
}