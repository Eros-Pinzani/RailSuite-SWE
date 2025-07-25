package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Carriage;
import java.util.List;
import businessLogic.service.OperatorHomeService.AssignedConvoyInfo;
import domain.Run;
import domain.Line;


public class ConvoyDetailsService {
    private final RailSuiteFacade facade = new RailSuiteFacade();

    public static class ConvoyDetailsDTO {
        public final String convoyId;
        public final String lineName;
        public final String staffName;
        public final String departureStation;
        public final String departureTime;
        public final String arrivalStation;
        public final String arrivalTime;
        public final List<Carriage> carriages;
        public final List<StationRow> stationRows;
        public ConvoyDetailsDTO(String convoyId, String lineName, String staffName, String departureStation, String departureTime, String arrivalStation, String arrivalTime, List<Carriage> carriages, List<StationRow> stationRows) {
            this.convoyId = convoyId;
            this.lineName = lineName;
            this.staffName = staffName;
            this.departureStation = departureStation;
            this.departureTime = departureTime;
            this.arrivalStation = arrivalStation;
            this.arrivalTime = arrivalTime;
            this.carriages = carriages;
            this.stationRows = stationRows;
        }
    }

    public static class StationRow {
        public final String stationName;
        public final String arrivalTime;
        public final String departureTime;
        public StationRow(String stationName, String arrivalTime, String departureTime) {
            this.stationName = stationName;
            this.arrivalTime = arrivalTime;
            this.departureTime = departureTime;
        }
    }

    public ConvoyDetailsDTO getConvoyDetailsDTO(AssignedConvoyInfo convoyInfo) {
        if (convoyInfo == null) return null;
        try {
            String convoyId = String.valueOf(convoyInfo.convoyId);
            String departureStation = convoyInfo.departureStation;
            String departureTime = convoyInfo.departureTime;
            String arrivalStation = convoyInfo.arrivalStation;
            String arrivalTime = convoyInfo.arrivalTime;
            String staffName = "";
            String lineName = "";
            List<Carriage> carriages = facade.selectCarriagesByConvoyId(convoyInfo.convoyId);
            List<StationRow> stationRows = new java.util.ArrayList<>();
            List<Run> runs = facade.selectRunsByConvoy(convoyInfo.convoyId);
            if (!runs.isEmpty()) {
                Run run = runs.getFirst();
                try {
                    int staffId = run.getIdStaff();
                    domain.Staff staff = facade.findStaffById(staffId);
                    staffName = (staff != null && staff.getName() != null && !staff.getName().isBlank()) ? staff.getName() : "Staff non trovato";
                } catch (Exception e) {
                    staffName = "Staff non trovato";
                }
                Line line = facade.findLineById(run.getIdLine());
                if (line != null) {
                    lineName = line.getName();
                    List<domain.LineStation> lineStations = facade.findLineStationsByLineId(run.getIdLine());
                    for (domain.LineStation ls : lineStations) {
                        String stationName = "";
                        try {
                            domain.Station station = facade.findStationById(ls.getStationId());
                            if (station != null) stationName = station.getLocation();
                        } catch (Exception ignored) {}
                        stationRows.add(new StationRow(stationName, "", ""));
                    }
                }
            }
            return new ConvoyDetailsDTO(convoyId, lineName, staffName, departureStation, departureTime, arrivalStation, arrivalTime, carriages, stationRows);
        } catch (Exception e) {
            return null;
        }
    }
}
