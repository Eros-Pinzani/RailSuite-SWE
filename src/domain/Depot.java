package domain;

import java.util.List;

/**
 * Interface representing a Depot entity.
 * Provides factory method and accessors for depot properties and carriage management.
 */
public interface Depot {
    /** @return the unique identifier of the depot */
    int getIdDepot();
    /** @return the list of carriages in the depot */
    List<CarriageDepot> getCarriages();

    /**
     * Adds a carriage to the depot.
     * @param carriageDepot the carriage to add
     */
    void addCarriage(CarriageDepot carriageDepot);
    /**
     * Removes a carriage from the depot by id.
     * @param idCarriage the id of the carriage to remove
     * @return true if removed, false otherwise
     */
    boolean removeCarriage(int idCarriage);

    /**
     * Factory method to create a Depot instance.
     * @param idDepot the depot id
     * @return a Depot instance
     */
    static Depot of(int idDepot) {
        return new DepotImp(idDepot);
    }
}
