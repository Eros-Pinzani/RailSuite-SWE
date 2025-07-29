package domain;

import java.time.Duration;

/**
 * Implementation of the LineStation interface.
 * Stores and manages the state of a station in a line.
 */
class LineStationImp implements LineStation {
    private final int stationId;
    private final int order;
    private final Duration timeToNextStation;

    /**
     * Constructs a LineStationImp with the given properties.
     * @param stationId the station id
     * @param order the order of the station
     * @param timeToNextStation the time to the next station
     */
    public LineStationImp(int stationId, int order, Duration timeToNextStation) {
        this.stationId = stationId;
        this.order = order;
        this.timeToNextStation = timeToNextStation;
    }

    @Override
    public int getStationId() { return stationId; }
    @Override
    public int getOrder() { return order; }
    @Override
    public Duration getTimeToNextStation() { return timeToNextStation; }
}
