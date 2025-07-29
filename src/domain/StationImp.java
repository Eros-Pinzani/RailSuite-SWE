package domain;

/**
 * Implementation of the Station interface.
 * Stores and manages the state of a station.
 */
class StationImp implements Station {
    private final int idStation;
    private final String location;
    private final int numBins;
    private final String serviceDescription;
    private final boolean isHead;

    /**
     * Constructs a StationImp with all properties.
     * @param idStation the station id
     * @param location the location of the station
     * @param numBins the number of bins
     * @param serviceDescription the service description
     * @param isHead true if head station
     */
    public StationImp(int idStation, String location, int numBins, String serviceDescription, boolean isHead) {
        this.idStation = idStation;
        this.location = location;
        this.numBins = numBins;
        this.serviceDescription = serviceDescription;
        this.isHead = isHead;
    }

    @Override
    public int getIdStation() {
        return idStation;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public int getNumBins() {
        return numBins;
    }

    @Override
    public String getServiceDescription() {
        return serviceDescription;
    }

    @Override
    public boolean isHead() {
        return isHead;
    }
}