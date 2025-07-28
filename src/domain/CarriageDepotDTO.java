package domain;

import java.sql.Timestamp;

public class CarriageDepotDTO {
    private final int idCarriage;
    private final String model;
    private final int yearProduced;
    private final int capacity;
    private final String depotStatus; // AVAILABLE, CLEANING, MAINTENANCE
    private final Timestamp timeExited; // Fine manutenzione, se presente

    public CarriageDepotDTO(int idCarriage, String model, int yearProduced, int capacity, String depotStatus, Timestamp timeExited) {
        this.idCarriage = idCarriage;
        this.model = model;
        this.yearProduced = yearProduced;
        this.capacity = capacity;
        this.depotStatus = depotStatus;
        this.timeExited = timeExited;
    }

    public int getIdCarriage() { return idCarriage; }
    public String getModel() { return model; }
    public int getYearProduced() { return yearProduced; }
    public int getCapacity() { return capacity; }
    public String getDepotStatus() { return depotStatus; }
    public Timestamp getTimeExited() { return timeExited; }
}

