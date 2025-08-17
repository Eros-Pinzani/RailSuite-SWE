package domain.DTO;

/**
 * Data Transfer Object for Convoy table views.
 * Used to transfer convoy summary information for UI tables.
 */
public class ConvoyTableDTO {
    private final int idConvoy;
    private final String model;
    private final String status;
    private final int carriageCount;
    private final int capacity;
    private final String modelType;

    /**
     * Constructs a ConvoyTableDTO with all properties.
     * @param idConvoy the convoy id
     * @param model the type of convoy
     * @param status the status of the convoy
     * @param carriageCount the number of carriages in the convoy
     */
    public ConvoyTableDTO(int idConvoy, String model, String status, int carriageCount, int capacity, String modelType) {
        this.idConvoy = idConvoy;
        this.model = model;
        this.status = status;
        this.carriageCount = carriageCount;
        this.capacity = capacity;
        this.modelType = modelType;
    }

    /** @return the convoy id */
    public int getIdConvoy() { return idConvoy; }
    /** @return the Model of convoy */
    public String getModel() { return model; }
    /** @return the status of the convoy */
    public String getStatus() { return status; }
    /** @return the number of carriages in the convoy */
    public int getCarriageCount() { return carriageCount; }
    /** @return the capacity of the convoy */
    public int getCapacity() { return capacity;}
    /** @return the model type of the convoy */
    public String getModelType() { return modelType;}
}
