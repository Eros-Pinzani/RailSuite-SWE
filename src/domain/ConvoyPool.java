package domain;

/**
 * Interface representing a ConvoyPool entity.
 * Provides factory method and accessors for convoy pool properties and status management.
 */
public interface ConvoyPool {
    /**
     * Enum representing the status of a convoy in the pool.
     */
    enum ConvoyStatus {
        DEPOT,
        ON_RUN,
        WAITING
    }

    /** @return the unique identifier of the convoy */
    int getIdConvoy();
    /** @return the unique identifier of the station */
    int getIdStation();
    /** Sets the station id for the convoy */
    void setIdStation(int idStation);
    /** @return the status of the convoy */
    ConvoyStatus getConvoyStatus();
    /** Sets the status of the convoy */
    void setConvoyStatus(ConvoyStatus convoyStatus);

    /**
     * Factory method to create a ConvoyPool instance.
     * @param idConvoy the convoy id
     * @param idStation the station id
     * @param convoyStatus the status of the convoy
     * @return a ConvoyPool instance
     */
    static ConvoyPool of(int idConvoy, int idStation, ConvoyStatus convoyStatus) {
        return new ConvoyPoolImp(idConvoy, idStation, convoyStatus);
    }
}
