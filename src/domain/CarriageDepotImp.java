package domain;

import java.sql.Timestamp;

/**
 * Implementation of the CarriageDepot interface.
 * Stores and manages the state of a carriage in a depot.
 */
class CarriageDepotImp implements CarriageDepot {
    private final int idDepot;
    private final int idCarriage;
    private Timestamp timeEntered;
    private Timestamp timeExited;
    private StatusOfCarriage statusOfCarriage;

    /**
     * Constructs a CarriageDepotImp with all properties.
     * @param idDepot the depot id
     * @param idCarriage the carriage id
     * @param timeEntered the entry timestamp
     * @param timeExited the exit timestamp
     * @param statusOfCarriage the status of the carriage
     */
    CarriageDepotImp(int idDepot, int idCarriage, Timestamp timeEntered, Timestamp timeExited, StatusOfCarriage statusOfCarriage) {
        this.idDepot = idDepot;
        this.idCarriage = idCarriage;
        this.timeEntered = timeEntered;
        this.timeExited = timeExited;
        this.statusOfCarriage = statusOfCarriage;
    }

    @Override
    public int getIdDepot() {
        return idDepot;
    }
    @Override
    public int getIdCarriage() {
        return idCarriage;
    }
    @Override
    public Timestamp getTimeEntered() {
        return timeEntered;
    }
    @Override
    public Timestamp getTimeExited() {
        return timeExited;
    }
    @Override
    public StatusOfCarriage getStatusOfCarriage() {
        return statusOfCarriage;
    }
    @Override
    public void setTimeEntered(Timestamp timeEntered) {
        this.timeEntered = timeEntered;
    }
    @Override
    public void setTimeExited(Timestamp timeExited) {
        this.timeExited = timeExited;
    }
    @Override
    public void setStatusOfCarriage(StatusOfCarriage statusOfCarriage) {
        this.statusOfCarriage = statusOfCarriage;
    }
}
