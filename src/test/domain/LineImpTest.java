package test.domain;

import domain.Line;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LineImpTest {
    private Line line;
    private final int idLine = 7;
    private final String lineName = "Linea Nord";
    private final int idFirstStation = 101;
    private final String firstStationLocation = "Milano";
    private final int idLastStation = 202;
    private final String lastStationLocation = "Torino";

    @BeforeEach
    void setUp() {
        line = Line.of(idLine, lineName, idFirstStation, firstStationLocation, idLastStation, lastStationLocation);
    }

    @AfterEach
    void tearDown() {
        line = null;
    }

    @Test
    void getIdLine() {
        assertEquals(idLine, line.getIdLine());
    }

    @Test
    void getLineName() {
        assertEquals(lineName, line.getLineName());
    }

    @Test
    void getIdFirstStation() {
        assertEquals(idFirstStation, line.getIdFirstStation());
    }

    @Test
    void getFirstStationLocation() {
        assertEquals(firstStationLocation, line.getFirstStationLocation());
    }

    @Test
    void getIdLastStation() {
        assertEquals(idLastStation, line.getIdLastStation());
    }

    @Test
    void getLastStationLocation() {
        assertEquals(lastStationLocation, line.getLastStationLocation());
    }
}