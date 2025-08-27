package domain;

import java.util.List;

public class TimeTable {
    private final int idLine;
    List<StationArrAndDep> stationArrAndDepList;

    public TimeTable(int idLine, List<StationArrAndDep> stationArrAndDepList) {
        this.idLine = idLine;
        this.stationArrAndDepList = stationArrAndDepList;
    }

    public int getIdLine() {
        return idLine;
    }

    public List<StationArrAndDep> getStationArrAndDepList() {
        return stationArrAndDepList;
    }

    public static class StationArrAndDep {
        private final int idStation;
        private final String stationName;
        private final String arriveTime;
        private final String departureTime;

        public StationArrAndDep(int idStation, String stationName, String arriveTime, String departureTime) {
            this.idStation = idStation;
            this.stationName = stationName;
            this.arriveTime = arriveTime;
            this.departureTime = departureTime;
        }

        public int getIdStation() {
            return idStation;
        }

        public String getStationName() {
            return stationName;
        }

        public String getArriveTime() {
            return arriveTime;
        }

        public String getDepartureTime() {
            return departureTime;
        }
    }

}
