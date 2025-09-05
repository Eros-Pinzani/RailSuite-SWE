package test.domain.DTO;

import domain.DTO.RunDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class RunDTOTest {
    private RunDTO dto;
    private final int idLine = 7;
    private final String lineName = "Linea Nord";
    private final int idConvoy = 42;
    private final int idStaff = 99;
    private final String staffName = "Gianni";
    private final String staffSurname = "Bianchi";
    private final String staffEmail = "gianni.bianchi@email.it";
    private final Timestamp timeDeparture = Timestamp.valueOf("2025-09-05 08:00:00");
    private final String firstStationName = "Milano";

    @BeforeEach
    void setUp() {
        dto = new RunDTO(idLine, lineName, idConvoy, idStaff, staffName, staffSurname, staffEmail, timeDeparture, firstStationName);
    }

    @AfterEach
    void tearDown() {
        dto = null;
    }

    @Test
    void getIdLine() {
        assertEquals(idLine, dto.getIdLine());
    }

    @Test
    void getLineName() {
        assertEquals(lineName, dto.getLineName());
    }

    @Test
    void getIdConvoy() {
        assertEquals(idConvoy, dto.getIdConvoy());
    }

    @Test
    void getIdStaff() {
        assertEquals(idStaff, dto.getIdStaff());
    }

    @Test
    void getStaffName() {
        assertEquals(staffName, dto.getStaffName());
    }

    @Test
    void getStaffSurname() {
        assertEquals(staffSurname, dto.getStaffSurname());
    }

    @Test
    void getStaffEmail() {
        assertEquals(staffEmail, dto.getStaffEmail());
    }

    @Test
    void getTimeDeparture() {
        assertEquals(timeDeparture, dto.getTimeDeparture());
    }

    @Test
    void getFirstStationName() {
        assertEquals(firstStationName, dto.getFirstStationName());
    }
}