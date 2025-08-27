package domain;

class LineImp implements Line {
    private final int idLine;
    private final String lineName;
    private final int idFirstStation;
    private final String firstStationLocation;
    private final int idLastStation;
    private final String lastStationLocation;

    public LineImp(int idLine, String lineName, int idFirstStation, String firstStationLocation, int idLastStation, String lastStationLocation) {
        this.idLine = idLine;
        this.lineName = lineName;
        this.idFirstStation = idFirstStation;
        this.firstStationLocation = firstStationLocation;
        this.idLastStation = idLastStation;
        this.lastStationLocation = lastStationLocation;
    }

    @Override
    public int getIdLine() {
        return idLine;
    }

    @Override
    public String getLineName() {
        return lineName;
    }

    @Override
    public int getIdFirstStation() {
        return idFirstStation;
    }

    @Override
    public String getFirstStationLocation() {
        return firstStationLocation;
    }

    @Override
    public int getIdLastStation() {
        return idLastStation;
    }

    @Override
    public String getLastStationLocation() {
        return lastStationLocation;
    }
}
