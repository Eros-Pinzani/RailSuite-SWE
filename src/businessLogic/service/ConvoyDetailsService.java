package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Convoy;
import domain.Carriage;
import java.util.List;
import java.util.logging.Logger;
import businessLogic.service.OperatorHomeService.AssignedConvoyInfo;
import domain.Run;
import domain.Line;


public class ConvoyDetailsService {
    private static final Logger logger = Logger.getLogger(ConvoyDetailsService.class.getName());
    private final RailSuiteFacade facade = new RailSuiteFacade();

    public Convoy getConvoyDetails(int convoyId) {
        try {
            return facade.selectConvoy(convoyId);
        } catch (Exception e) {
            logger.warning("Error in getConvoyDetails: " + e.getMessage());
            return null;
        }
    }

    public List<Carriage> getCarriagesForConvoy(int convoyId) {
        try {
            return facade.selectAllCarriages().stream()
                .filter(c -> {
                    Integer idConvoy = null;
                    try {
                        idConvoy = facade.findConvoyIdByCarriageId(c.getId());
                    } catch (Exception e) {
                        logger.warning("Error in getCarriagesForConvoy (filter): " + e.getMessage());
                    }
                    return idConvoy != null && idConvoy == convoyId;
                })
                .toList();
        } catch (Exception e) {
            logger.warning("Error in getCarriagesForConvoy: " + e.getMessage());
            return List.of();
        }
    }

    public boolean addCarriageToConvoy(int convoyId, int carriageId) {
        Carriage carriage = null;
        try {
            carriage = facade.selectCarriage(carriageId);
        } catch (Exception e) {
            logger.warning("Error in addCarriageToConvoy (selectCarriage): " + e.getMessage());
        }
        if (carriage != null) {
            try {
                Integer existingConvoyId = facade.findConvoyIdByCarriageId(carriageId);
                if (existingConvoyId != null) {
                    logger.warning("Carriage already assigned to a convoy.");
                    throw new IllegalStateException("La carrozza è già assegnata a un convoglio.");
                }
                boolean result = facade.addCarriageToConvoy(convoyId, carriage);
                if (result) {
                    carriage.setIdConvoy(convoyId);
                    facade.updateCarriageConvoy(carriageId, convoyId);
                }
                return result;
            } catch (IllegalStateException e) {
                throw e;
            } catch (Exception e) {
                logger.warning("Error in addCarriageToConvoy (addCarriage): " + e.getMessage());
            }
        }
        return false;
    }

    public boolean removeCarriageFromConvoy(int convoyId, int carriageId) {
        Carriage carriage = null;
        try {
            carriage = facade.selectCarriage(carriageId);
        } catch (Exception e) {
            logger.warning("Error in removeCarriageFromConvoy (selectCarriage): " + e.getMessage());
        }
        if (carriage != null) {
            try {
                boolean result = facade.removeCarriageFromConvoy(convoyId, carriage);
                if (result) {
                    carriage.setIdConvoy(null);
                    facade.updateCarriageConvoy(carriageId, null);
                }
                return result;
            } catch (Exception e) {
                logger.warning("Error in removeCarriageFromConvoy (removeCarriage): " + e.getMessage());
            }
        }
        return false;
    }

    public boolean updateCarriageConvoy(int carriageId, Integer idConvoy) {
        try {
            return facade.updateCarriageConvoy(carriageId, idConvoy);
        } catch (Exception e) {
            logger.warning("Error in updateCarriageConvoy: " + e.getMessage());
            return false;
        }
    }

    public Carriage selectCarriage(int id) {
        try {
            return facade.selectCarriage(id);
        } catch (Exception e) {
            logger.warning("Error in selectCarriage: " + e.getMessage());
            return null;
        }
    }

    public Convoy createConvoy(List<Carriage> carriages) {
        // Controllo che nessuna carrozza sia già assegnata a un convoglio
        for (Carriage carriage : carriages) {
            Integer idConvoy;
            try {
                idConvoy = facade.findConvoyIdByCarriageId(carriage.getId());
            } catch (Exception e) {
                logger.warning("Error checking associated convoy: " + e.getMessage());
                throw new RuntimeException("Error checking associated convoy for carriage " + carriage.getId());
            }
            if (idConvoy != null) {
                logger.warning("Carriage with ID " + carriage.getId() + " is already assigned to convoy " + idConvoy);
                throw new IllegalArgumentException("La carrozza con ID " + carriage.getId() + " è già assegnata al convoglio " + idConvoy);
            }
        }
        try {
            return facade.createConvoy(carriages);
        } catch (Exception e) {
            logger.warning("Error in createConvoy: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parsing e validazione degli ID carrozze da stringa separata da virgole
     */
    public List<Integer> parseCarriageIds(String idsText) throws IllegalArgumentException {
        if (idsText == null || idsText.isBlank()) {
            throw new IllegalArgumentException("Inserisci almeno un ID di carrozza.");
        }
        String[] idsArr = idsText.split(",");
        List<Integer> ids = new java.util.ArrayList<>();
        for (String idStr : idsArr) {
            try {
                ids.add(Integer.parseInt(idStr.trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("ID non valido: " + idStr);
            }
        }
        return ids;
    }

    public boolean convoyHasCarriages(int convoyId) {
        List<Carriage> carriages = getCarriagesForConvoy(convoyId);
        return carriages != null && !carriages.isEmpty();
    }

    public boolean convoyExists(int convoyId) {
        Convoy convoy = getConvoyDetails(convoyId);
        return convoy != null;
    }

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
            RailSuiteFacade facade = new RailSuiteFacade();
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
                    if (staff != null) staffName = staff.getName();
                } catch (Exception ignored) {}
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
