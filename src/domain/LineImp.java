package domain;

import java.time.Duration;

class LineImp implements Line {
    private final int idLine;
    private final Station station;
    private final Station nextStation;
    private final Duration timeToNextStation;

    LineImp(int idLine, Station station, Station nextStation, Duration timeToNextStation) {
        this.idLine = idLine;
        this.station = station;
        this.nextStation = nextStation;
        this.timeToNextStation = timeToNextStation;
    }

    @Override
    public int getIdLine() { return idLine; }
    @Override
    public Station getStation() { return station; }
    @Override
    public Station getNextStation() { return nextStation; }
    @Override
    public Duration getTimeToNextStation() { return timeToNextStation; }
}
