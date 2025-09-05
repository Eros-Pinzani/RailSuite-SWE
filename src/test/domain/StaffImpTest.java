package test.domain;

import domain.Staff;
import domain.Staff.TypeOfStaff;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StaffImpTest {
    private Staff staff;
    private final int idStaff = 123;
    private final String name = "Giovanni";
    private final String surname = "Verdi";
    private final String address = "Via Roma 1";
    private final String email = "giovanni.verdi@email.it";
    private final String password = "password123";
    private final TypeOfStaff typeOfStaff = TypeOfStaff.OPERATOR;

    @BeforeEach
    void setUp() {
        staff = Staff.of(idStaff, name, surname, address, email, password, typeOfStaff);
    }

    @AfterEach
    void tearDown() {
        staff = null;
    }

    @Test
    void getIdStaff() {
        assertEquals(idStaff, staff.getIdStaff());
    }

    @Test
    void getName() {
        assertEquals(name, staff.getName());
    }

    @Test
    void getSurname() {
        assertEquals(surname, staff.getSurname());
    }

    @Test
    void getAddress() {
        assertEquals(address, staff.getAddress());
    }

    @Test
    void getEmail() {
        assertEquals(email, staff.getEmail());
    }

    @Test
    void getPassword() {
        assertEquals(password, staff.getPassword());
    }

    @Test
    void getTypeOfStaff() {
        assertEquals(typeOfStaff, staff.getTypeOfStaff());
    }
}