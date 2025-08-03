package domain.DTO;

/**
 * Data Transfer Object for Convoy table views.
 * Used to transfer convoy summary information for UI tables.
 */
public class ConvoyTableDTO {
    private int idConvoy;
    private String type;
    private String status;
    private int carriageCount;

    /**
     * Constructs a ConvoyTableDTO with all properties.
     * @param idConvoy the convoy id
     * @param type the type of convoy
     * @param status the status of the convoy
     * @param carriageCount the number of carriages in the convoy
     */
    public ConvoyTableDTO(int idConvoy, String type, String status, int carriageCount) {
        this.idConvoy = idConvoy;
        this.type = type;
        this.status = status;
        this.carriageCount = carriageCount;
    }

    /** @return the convoy id */
    public int getIdConvoy() { return idConvoy; }
    /** @return the type of convoy */
    public String getType() { return type; }
    /** @return the status of the convoy */
    public String getStatus() { return status; }
    /** @return the number of carriages in the convoy */
    public int getCarriageCount() { return carriageCount; }

    /** Sets the convoy id */
    public void setIdConvoy(int idConvoy) { this.idConvoy = idConvoy; }
    /** Sets the type of convoy */
    public void setType(String type) { this.type = type; }
    /** Sets the status of the convoy */
    public void setStatus(String status) { this.status = status; }
    /** Sets the number of carriages in the convoy */
    public void setCarriageCount(int carriageCount) { this.carriageCount = carriageCount; }
}
