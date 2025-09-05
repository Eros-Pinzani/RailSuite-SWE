package test.domain;

import domain.LineStation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class LineStationImpTest {
    private LineStation lineStation;
    private final int stationId = 55;
    private final int order = 3;
    private final Duration timeToNextStation = Duration.ofMinutes(12);

    @BeforeEach
    void setUp() {
        lineStation = LineStation.of(stationId, order, timeToNextStation);
    }

    @AfterEach
    void tearDown() {
        lineStation = null;
    }

    @Test
    void getStationId() {
        assertEquals(stationId, lineStation.getStationId());
    }

    @Test
    void getOrder() {
        assertEquals(order, lineStation.getOrder());
    }

    @Test
    void getTimeToNextStation() {
        assertEquals(timeToNextStation, lineStation.getTimeToNextStation());
    }
}