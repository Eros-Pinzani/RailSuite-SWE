package domain;

/**
 * Interface representing a Station entity.
 * Provides factory method and accessors for station properties.
 */
public interface Station {
    /** @return the unique identifier of the station */
    int getIdStation();
    /** @return the location of the station */
    String getLocation();
    /** @return the number of bins at the station */
    int getNumBins();
    /** @return the service description of the station */
    String getServiceDescription();
    /** @return true if the station is a head station, false otherwise */
    boolean isHead();

    /**
     * Factory method to create a Station instance.
     * @param idStation the station id
     * @param location the location of the station
     * @param numBins the number of bins
     * @param serviceDescription the service description
     * @param isHead true if head station
     * @return a Station instance
     */
    static Station of(int idStation, String location, int numBins, String serviceDescription, boolean isHead) {
        return new StationImp(idStation, location, numBins, serviceDescription, isHead);
    }
}