package domain.DTO;

import java.util.List;

public class TimeTableDTO {
    private final int idLine;
    List<StationArrAndDepDTO> stationArrAndDepDTOList;

    public TimeTableDTO(int idLine, List<StationArrAndDepDTO> stationArrAndDepDTOList) {
        this.idLine = idLine;
        this.stationArrAndDepDTOList = stationArrAndDepDTOList;
    }

    public int getIdLine() {
        return idLine;
    }

    public List<StationArrAndDepDTO> getStationArrAndDepDTOList() {
        return stationArrAndDepDTOList;
    }

    public static class StationArrAndDepDTO {
        private final int idStation;
        private final String stationName;
        private final String arriveTime;
        private final String departureTime;

        public StationArrAndDepDTO(int idStation, String stationName, String arriveTime, String departureTime) {
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
