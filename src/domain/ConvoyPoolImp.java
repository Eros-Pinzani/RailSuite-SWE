package domain;

class ConvoyPoolImp implements ConvoyPool{
    private final int idConvoy;
    private int idStation;
    private ConvoyStatus convoyStatus;

    public ConvoyPoolImp(int idConvoy, int idStation, ConvoyStatus convoyStatus) {
        this.idConvoy = idConvoy;
        this.idStation = idStation;
        this.convoyStatus = convoyStatus;
    }

    @Override
    public int getIdConvoy() {
        return idConvoy;
    }

    @Override
    public int getIdStation() {
        return idStation;
    }

    @Override
    public void setIdStation(int idStation) {
        this.idStation = idStation;
    }

    @Override
    public ConvoyStatus getConvoyStatus() {
        return convoyStatus;
    }

    @Override
    public void setConvoyStatus(ConvoyStatus convoyStatus) {
        this.convoyStatus = convoyStatus;
    }
}
