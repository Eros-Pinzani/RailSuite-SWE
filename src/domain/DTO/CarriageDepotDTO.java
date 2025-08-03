package domain.DTO;

import java.sql.Timestamp;

/**
 * Data Transfer Object for CarriageDepot.
 * Used to transfer carriage and depot status information.
 */
public class CarriageDepotDTO {
    private final int idCarriage;
    private final String model;
    private final int yearProduced;
    private final int capacity;
    private final String depotStatus;
    private final Timestamp timeExited;

    /**
     * Constructs a CarriageDepotDTO with all properties.
     * @param idCarriage the carriage id
     * @param model the model name
     * @param yearProduced the year produced
     * @param capacity the carriage capacity
     * @param depotStatus the status in the depot
     * @param timeExited the exit time from depot (nullable)
     */
    public CarriageDepotDTO(int idCarriage, String model, int yearProduced, int capacity, String depotStatus, Timestamp timeExited) {
        this.idCarriage = idCarriage;
        this.model = model;
        this.yearProduced = yearProduced;
        this.capacity = capacity;
        this.depotStatus = depotStatus;
        this.timeExited = timeExited;
    }

    /** @return the carriage id */
    public int getIdCarriage() { return idCarriage; }
    /** @return the model name */
    public String getModel() { return model; }
    /** @return the year produced */
    public int getYearProduced() { return yearProduced; }
    /** @return the carriage capacity */
    public int getCapacity() { return capacity; }
    /** @return the depot status */
    public String getDepotStatus() { return depotStatus; }
    /** @return the exit time from depot, or null if not present */
    public Timestamp getTimeExited() { return timeExited; }
}
