package domain;

public interface ConvoyPool {
    enum ConvoyStatus {
        DEPOT,
        ON_RUN,
        WAITING
    }

    int getIdConvoy();
    int getIdStation();
    void setIdStation(int idStation);
    ConvoyStatus getConvoyStatus();
    void setConvoyStatus(ConvoyStatus convoyStatus);

    static ConvoyPool of(int idConvoy, int idStation, ConvoyStatus convoyStatus) {
        return new ConvoyPoolImp(idConvoy, idStation, convoyStatus);
    }
}
