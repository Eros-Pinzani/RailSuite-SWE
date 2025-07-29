package businessLogic.service;

import java.util.logging.Logger;

/**
 * Service for retrieving and processing convoy details.
 * Provides methods to fetch convoy, carriage, and station information for display.
 */
import domain.Carriage;
import java.util.List;
import businessLogic.service.OperatorHomeService.AssignedConvoyInfo;



public class ConvoyDetailsService {
    private static final Logger logger = Logger.getLogger(ConvoyDetailsService.class.getName());

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

    /**
     * Retrieves detailed information about a convoy, including its carriages and stations.
     * This method is used to provide all the details needed for the convoy details view.
     * @param convoyInfo The assigned convoy info to look up.
     * @return A ConvoyDetailsDTO with all details, or null if not found or on error.
     */
    public ConvoyDetailsDTO getConvoyDetailsDTO(AssignedConvoyInfo convoyInfo) {
        if (convoyInfo == null) return null;
        try {
            dao.ConvoyDao convoyDao = dao.ConvoyDao.of();
            ConvoyDetailsRaw raw = convoyDao.selectConvoyDetailsById(convoyInfo.convoyId);
            return new ConvoyDetailsDTO(
                String.valueOf(raw.convoyId),
                raw.lineName,
                raw.staffName,
                raw.departureStation,
                raw.departureTime,
                raw.arrivalStation,
                raw.arrivalTime,
                raw.carriages,
                raw.stationRows
            );
        } catch (Exception e) {
            logger.severe("Error updating convoy details: " + e.getMessage());
            return null;
        }
    }

    public static class ConvoyDetailsRaw {
        public int convoyId;
        public String lineName;
        public String staffName;
        public String departureStation;
        public String departureTime;
        public String arrivalStation;
        public String arrivalTime;
        public List<Carriage> carriages;
        public List<StationRow> stationRows;
    }
}
