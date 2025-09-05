package test.domain;

import domain.ConvoyPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConvoyPoolImpTest {
    private ConvoyPool convoyPool;
    private final int idConvoy = 101;
    private final int idStation = 202;
    private final ConvoyPool.ConvoyStatus status = ConvoyPool.ConvoyStatus.DEPOT;

    @BeforeEach
    void setUp() {
        convoyPool = ConvoyPool.of(idConvoy, idStation, status);
    }

    @AfterEach
    void tearDown() {
        convoyPool = null;
    }

    @Test
    void getIdConvoy() {
        assertEquals(idConvoy, convoyPool.getIdConvoy());
    }

    @Test
    void getIdStation() {
        assertEquals(idStation, convoyPool.getIdStation());
    }

    @Test
    void setIdStation() {
        int newIdStation = 303;
        convoyPool.setIdStation(newIdStation);
        assertEquals(newIdStation, convoyPool.getIdStation());
    }

    @Test
    void getConvoyStatus() {
        assertEquals(status, convoyPool.getConvoyStatus());
    }

    @Test
    void setConvoyStatus() {
        convoyPool.setConvoyStatus(ConvoyPool.ConvoyStatus.ON_RUN);
        assertEquals(ConvoyPool.ConvoyStatus.ON_RUN, convoyPool.getConvoyStatus());
        convoyPool.setConvoyStatus(ConvoyPool.ConvoyStatus.WAITING);
        assertEquals(ConvoyPool.ConvoyStatus.WAITING, convoyPool.getConvoyStatus());
    }
}