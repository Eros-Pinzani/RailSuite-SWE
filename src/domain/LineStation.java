package domain;

import java.time.Duration;
import java.util.List;

/**
 * Interface representing a LineStation entity.
 * Provides factory method and accessors for line station properties.
 */
public interface LineStation {
    /** @return the unique identifier of the station */
    int getStationId();
    /** @return the order of the station in the line */
    int getOrder();
    /** @return the time to the next station */
    Duration getTimeToNextStation();

    /**
     * Factory method to create a LineStation instance.
     * @param stationId the station id
     * @param order the order of the station
     * @param timeToNextStation the time to the next station
     * @return a LineStation instance
     */
    static LineStation of(int stationId, int order, Duration timeToNextStation) {
        return new LineStationImp(stationId, order, timeToNextStation);
    }
}
