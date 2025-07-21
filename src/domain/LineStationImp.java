package domain;

import java.time.Duration;

class LineStationImp implements LineStation {
    private final int stationId;
    private final int order;
    private final Duration timeToNextStation;

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

