package test.businesslogic;

import businessLogic.service.OperatorHomeService;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OperatorHomeServiceTest {
    private OperatorHomeService service;

    @BeforeEach
    void setUp() {
        service = new OperatorHomeService();
    }

    @Test
    void testGetAssignedConvoysForOperator_NoException() {
        // Test base: verifica che la chiamata non lanci eccezioni (serve un id staff valido nel DB)
        try {
            List<OperatorHomeService.AssignedConvoyInfo> result = service.getAssignedConvoysForOperator(1); // Sostituisci 1 con un id staff valido
            assertNotNull(result);
        } catch (SQLException e) {
            fail("SQLException: " + e.getMessage());
        }
    }

    @Test
    void testGetAssignedConvoysForOperator_EmptyListIfNoConvoys() throws SQLException {
        // Usa uno staffId che sicuramente non ha convogli assegnati
        List<OperatorHomeService.AssignedConvoyInfo> result = service.getAssignedConvoysForOperator(-9999);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
