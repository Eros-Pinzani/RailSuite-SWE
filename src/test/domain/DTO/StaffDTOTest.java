package test.domain.DTO;

import domain.DTO.StaffDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StaffDTOTest {
    private StaffDTO dto;
    private final int idStaff = 77;
    private final String name = "Gianni";
    private final String surname = "Bianchi";

    @BeforeEach
    void setUp() {
        dto = new StaffDTO(idStaff, name, surname);
    }

    @AfterEach
    void tearDown() {
        dto = null;
    }

    @Test
    void getIdStaff() {
        assertEquals(idStaff, dto.getIdStaff());
    }

    @Test
    void getStaffNameSurname() {
        assertEquals(name + " " + surname, dto.getStaffNameSurname());
    }
}