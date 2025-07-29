package domain;

import java.sql.Timestamp;

/**
 * Interface representing a CarriageDepot entity.
 * Provides factory method and accessors for carriage depot properties.
 */
public interface CarriageDepot {
    /** @return the unique identifier of the depot */
    int getIdDepot();
    /** @return the unique identifier of the carriage */
    int getIdCarriage();
    /** @return the timestamp when the carriage entered the depot */
    Timestamp getTimeEntered();
    /** @return the timestamp when the carriage exited the depot */
    Timestamp getTimeExited();
    /** @return the status of the carriage in the depot */
    StatusOfCarriage getStatusOfCarriage();

    /** Sets the entry time for the carriage in the depot */
    void setTimeEntered(Timestamp timeEntered);
    /** Sets the exit time for the carriage from the depot */
    void setTimeExited(Timestamp timeExited);
    /** Sets the status of the carriage in the depot */
    void setStatusOfCarriage(StatusOfCarriage statusOfCarriage);

    /**
     * Enum representing the status of a carriage in the depot.
     */
    enum StatusOfCarriage {
        CLEANING, MAINTENANCE, AVAILABLE
    }

    /**
     * Factory method to create a CarriageDepot instance.
     * @param idDepot the depot id
     * @param idCarriage the carriage id
     * @param timeEntered the entry timestamp
     * @param timeExited the exit timestamp
     * @param statusOfCarriage the status of the carriage
     * @return a CarriageDepot instance
     */
    static CarriageDepot of(int idDepot, int idCarriage, Timestamp timeEntered, Timestamp timeExited, StatusOfCarriage statusOfCarriage) {
        return new CarriageDepotImp(idDepot, idCarriage, timeEntered, timeExited, statusOfCarriage);
    }
}
