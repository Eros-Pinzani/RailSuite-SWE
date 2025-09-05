package test.domain.DTO;

import domain.DTO.ConvoyTableDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConvoyTableDTOTest {
    private ConvoyTableDTO dto;
    private final int idConvoy = 42;
    private final String model = "FrecciaRossa";
    private final String status = "AVAILABLE";
    private final int carriageCount = 8;
    private final int capacity = 480;
    private final String modelType = "AltaVelocità";

    @BeforeEach
    void setUp() {
        dto = new ConvoyTableDTO(idConvoy, model, status, carriageCount, capacity, modelType);
    }

    @AfterEach
    void tearDown() {
        dto = null;
    }

    @Test
    void getIdConvoy() {
        assertEquals(idConvoy, dto.getIdConvoy());
    }

    @Test
    void getModel() {
        assertEquals(model, dto.getModel());
    }

    @Test
    void getStatus() {
        assertEquals(status, dto.getStatus());
    }

    @Test
    void getCarriageCount() {
        assertEquals(carriageCount, dto.getCarriageCount());
    }

    @Test
    void getCapacity() {
        assertEquals(capacity, dto.getCapacity());
    }

    @Test
    void getModelType() {
        assertEquals(modelType, dto.getModelType());
    }
}