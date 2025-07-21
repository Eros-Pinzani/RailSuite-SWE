package domain;

import java.time.Duration;
import java.util.List;

public interface LineStation {
    int getStationId();
    int getOrder();
    Duration getTimeToNextStation();

    static LineStation of(int stationId, int order, Duration timeToNextStation) {
        return new LineStationImp(stationId, order, timeToNextStation);
    }
}
