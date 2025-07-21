package domain;

import java.time.Duration;

public interface Line {
    int getIdLine();
    Station getStation();
    Station getNextStation();
    Duration getTimeToNextStation();

    static Line of(int idLine, Station station, Station nextStation, Duration timeToNextStation) {
        return new LineImp(idLine, station, nextStation, timeToNextStation);
    }
}
