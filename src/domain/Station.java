package domain;

public interface Station {
    int getIdStation();
    String getLocation();
    int getNumBins();
    String getServiceDescription();
    boolean isHead();

    static Station of(int idStation, String location, int numBins, String serviceDescription, boolean isHead) {
        return new StationImp(idStation, location, numBins, serviceDescription, isHead);
    }
}