package businessLogic.service;


import dao.*;
import domain.*;
import domain.DTO.StaffDTO;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class CreateRunService {
    private final LineRawDao lineRawDao = LineRawDao.of();
    private final ConvoyDao convoyDao = ConvoyDao.of();
    private final LineStationDao lineStationDao = LineStationDao.of();

    private final List<Convoy> convoysPoolAvailable = new ArrayList<>();
    private final List<Convoy> convoysPoolAvailableFilteredByType = new ArrayList<>();

    public void setConvoysPoolAvailable(List<Convoy> convoysPoolAvailable) {
        this.convoysPoolAvailable.clear();
        if (convoysPoolAvailable != null) {
            this.convoysPoolAvailable.addAll(convoysPoolAvailable);
        }
    }

    public void setConvoysPoolAvailableFilteredByType(List<Convoy> convoysPoolAvailableFilteredByType) {
        this.convoysPoolAvailableFilteredByType.clear();
        if (convoysPoolAvailableFilteredByType != null) {
            this.convoysPoolAvailableFilteredByType.addAll(convoysPoolAvailableFilteredByType);
        }
    }

    public List<Convoy> getConvoysPoolAvailable() {
        return new ArrayList<>(convoysPoolAvailable);
    }

    public List<Convoy> getConvoysPoolAvailableFilteredByType() {
        return new ArrayList<>(convoysPoolAvailableFilteredByType);
    }

    public List<LineRaw> getAllLines() {
        try {
            return lineRawDao.getAllLines();
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving lines: " + e.getMessage(), e);
        }
    }

    public Duration calculateTravelTime(LineRaw selectedLine) {
        try {
            List<LineStation> stations = lineStationDao.findByLine(selectedLine.getIdLine());
            if (stations.isEmpty()) return Duration.ZERO;

            Duration totalTime = Duration.ZERO;
            for (int i = 0; i < stations.size() - 1; i++) {
                totalTime = totalTime.plus(stations.get(i).getTimeToNextStation());
            }
            /* Add a +1 min for every station stop */
            return totalTime.plusMinutes(stations.size());
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating travel time: " + e.getMessage(), e);
        }
    }

    public void setConvoyPools(int idStation, String timeDeparture, LocalDate dateDeparture, int idLine) {
        try {
            setConvoysPoolAvailable(convoyDao.getConvoysForNewRun(idStation, timeDeparture, dateDeparture, idLine));
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving convoy pools: " + e.getMessage(), e);
        }
    }

    public List<String> getConvoyTypes(List<Convoy> convoys) {
        List<String> convoyTypes = new ArrayList<>();
        for (Convoy convoy : convoys) {
            if (!convoyTypes.contains(convoy.getCarriages().getFirst().getModelType())) {
                convoyTypes.add(convoy.getCarriages().getFirst().getModelType());
            }
        }
        return convoyTypes;
    }

    public void setAvailableConvoysFilteredByType(String selectedType, List<Convoy> convoysPoolAvailable) {
        List<Convoy> filteredConvoys = new ArrayList<>();
        for (Convoy convoy : convoysPoolAvailable) {
            if (convoy.getCarriages().getFirst().getModelType().equals(selectedType)) {
                filteredConvoys.add(convoy);
            }
        }
        setConvoysPoolAvailableFilteredByType(filteredConvoys);
    }

    public List<StaffDTO> getStaffPools(int idStation, LocalDate dateDeparture, String timeDeparture) {
        try {
            return StaffPoolDao.of().findAvailableOperatorsForRun(idStation, dateDeparture, timeDeparture);
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving staff pools: " + e.getMessage(), e);
        }
    }


    public Callback<DatePicker, DateCell> getDateCellFactory() {
        return dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || item.isBefore(today) || item.isAfter(today.plusDays(1)));
            }
        };
    }

    public List<String> getAvailableDepartureTimes() {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        java.time.LocalTime now = java.time.LocalTime.now();
        java.time.LocalTime start = now.withMinute((now.getMinute() / 15) * 15).plusMinutes(15);
        java.time.LocalTime end = java.time.LocalTime.of(22, 0);
        List<String> times = new ArrayList<>();
        for (java.time.LocalTime time = start; !time.isAfter(end); time = time.plusMinutes(15)) {
            times.add(time.format(formatter));
        }
        return times;
    }
}

/*
    private final ConvoyPoolDao convoyPoolDao = ConvoyPoolDao.of();
    private final StaffPoolDao staffPoolDao = StaffPoolDao.of();
    private final RunDao runDao = RunDao.of();
    private final StationDao stationDao = StationDao.of();

    // Restituisce "Andata" o "Ritorno" in base alle stazioni di testa
    public String getHeadStationName(Line line, String direction) {
        try {
            List<LineStation> stations = lineStationDao.findByLine(line.getIdLine());
            if (stations.isEmpty()) return "-";
            int idStation = direction.equals("Andata") ? stations.getFirst().getStationId() : stations.getLast().getStationId();
            Station s = stationDao.findById(idStation);
            return s != null ? s.getLocation() : "-";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    // Query unica per trovare convogli disponibili per linea, direzione, data, ora
    public List<Convoy> getAvailableConvoys(Line line, String direction, LocalDate date, String time) {
        // Implementare una query JOIN tra convoy_pool, convoy, run per filtrare solo i convogli disponibili
        return convoyPoolDao.findAvailableConvoysForRun(line.getIdLine(), direction, date, time);
    }

    // Query unica per trovare operatori disponibili per linea, direzione, data, ora
    public List<Staff> getAvailableOperators(Line line, String direction, LocalDate date, String time) {
        // Implementare una query JOIN tra staff_pool, staff, run per filtrare solo gli operatori disponibili
        return staffPoolDao.findAvailableOperatorsForRun(line.getIdLine(), direction, date, time);
    }

    // Crea la run (inserimento in tabella run)
    public String createRun(Line line, String direction, LocalDate date, String time, Convoy convoy, Staff operator) {
        try {
            List<LineStation> stations = lineStationDao.findByLine(line.getIdLine());
            if (stations.isEmpty()) return "Linea senza stazioni";
            int idFirstStation = direction.equals("Andata") ? stations.getFirst().getStationId() : stations.getLast().getStationId();
            int idLastStation = direction.equals("Andata") ? stations.getLast().getStationId() : stations.getFirst().getStationId();
            // TODO: calcolo orari arrivo/partenza per tutte le stazioni
            java.sql.Time timeDeparture = java.sql.Time.valueOf(time);
            // Per ora, stimiamo timeArrival come +1h (da calcolare correttamente)
            java.sql.Time timeArrival = java.sql.Time.valueOf(java.time.LocalTime.parse(time).plusHours(1));
            boolean success = runDao.createRun(line.getIdLine(), convoy.getId(), operator.getIdStaff(), timeDeparture, timeArrival, idFirstStation, idLastStation);
            return success ? null : "Errore durante la creazione della run";
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }
*/
