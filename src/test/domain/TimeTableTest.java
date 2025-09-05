package test.domain;

import domain.TimeTable;
import domain.TimeTable.StationArrAndDep;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeTableTest {
    private TimeTable timeTable;
    private final int idLine = 5;
    private List<StationArrAndDep> stationList;

    @BeforeEach
    void setUp() {
        stationList = Arrays.asList(
            new StationArrAndDep(101, "Milano", "08:00", "08:10"),
            new StationArrAndDep(102, "Bologna", "09:00", "09:10"),
            new StationArrAndDep(103, "Firenze", "10:00", "10:10")
        );
        timeTable = new TimeTable(idLine, stationList);
    }

    @AfterEach
    void tearDown() {
        timeTable = null;
        stationList = null;
    }

    @Test
    void getIdLine() {
        assertEquals(idLine, timeTable.getIdLine());
    }

    @Test
    void getStationArrAndDepList() {
        List<StationArrAndDep> result = timeTable.getStationArrAndDepList();
        assertEquals(3, result.size());
        assertEquals(101, result.getFirst().getIdStation());
        assertEquals("Milano", result.getFirst().getStationName());
        assertEquals("08:00", result.get(0).getArriveTime());
        assertEquals("08:10", result.get(0).getDepartureTime());
        assertEquals(102, result.get(1).getIdStation());
        assertEquals("Bologna", result.get(1).getStationName());
        assertEquals("09:00", result.get(1).getArriveTime());
        assertEquals("09:10", result.get(1).getDepartureTime());
        assertEquals(103, result.get(2).getIdStation());
        assertEquals("Firenze", result.get(2).getStationName());
        assertEquals("10:00", result.get(2).getArriveTime());
        assertEquals("10:10", result.get(2).getDepartureTime());
    }
}