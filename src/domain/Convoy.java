package domain;

import java.util.List;

/**
 * Interface representing a Convoy entity.
 * Provides factory method and accessors for convoy properties and carriages management.
 */
public interface Convoy {
    /**
     * Factory method to create a Convoy instance.
     * @param idConvoy the unique identifier of the convoy
     * @param carriages the list of carriages in the convoy
     * @return a Convoy instance
     */
    static Convoy of(int idConvoy, List<Carriage> carriages) {
        return new ConvoyImp(idConvoy, carriages);
    }
    /** @return the unique identifier of the convoy */
    int getId();
    /** @return the list of carriages in the convoy */
    List<Carriage> getCarriages();
    /**
     * Removes a carriage from the convoy.
     * @param carriage the carriage to remove
     * @return true if removed, false otherwise
     */
    boolean removeCarriage(Carriage carriage);
    /**
     * Adds a carriage to the convoy.
     * @param carriage the carriage to add
     * @return true if added, false otherwise
     */
    boolean addCarriage(Carriage carriage);
    /** @return the number of carriages in the convoy */
    int convoySize();
}
