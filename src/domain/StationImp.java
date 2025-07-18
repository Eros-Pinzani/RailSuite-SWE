package domain;

class StationImp implements Station {
    private final int idStation;
    private final String location;
    private final int numBins;
    private final String serviceDescription;
    private final boolean isHead;

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