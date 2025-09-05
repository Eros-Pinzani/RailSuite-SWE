package test.domain.DTO;

import domain.DTO.CarriageDepotDTO;
import java.sql.Timestamp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarriageDepotDTOTest {
    private CarriageDepotDTO dto;
    private final int idCarriage = 123;
    private final String model = "IC4";
    private final int yearProduced = 2018;
    private final int capacity = 80;
    private final String depotStatus = "MAINTENANCE";
    private final Timestamp timeExited = Timestamp.valueOf("2025-09-05 12:00:00");

    @BeforeEach
    void setUp() {
        dto = new CarriageDepotDTO(idCarriage, model, yearProduced, capacity, depotStatus, timeExited);
    }

    @AfterEach
    void tearDown() {
        dto = null;
    }

    @Test
    void getIdCarriage() {
        assertEquals(idCarriage, dto.getIdCarriage());
    }

    @Test
    void getModel() {
        assertEquals(model, dto.getModel());
    }

    @Test
    void getYearProduced() {
        assertEquals(yearProduced, dto.getYearProduced());
    }

    @Test
    void getCapacity() {
        assertEquals(capacity, dto.getCapacity());
    }

    @Test
    void getDepotStatus() {
        assertEquals(depotStatus, dto.getDepotStatus());
    }

    @Test
    void getTimeExited() {
        assertEquals(timeExited, dto.getTimeExited());
    }

    @Test
    void getTimeExited_null() {
        CarriageDepotDTO dtoNull = new CarriageDepotDTO(idCarriage, model, yearProduced, capacity, depotStatus, null);
        assertNull(dtoNull.getTimeExited());
    }
}