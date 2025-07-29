package businessLogic.service;


import dao.LineStationDao;
import dao.ConvoyPoolDao;
import dao.StaffPoolDao;
import dao.RunDao;
import dao.StationDao;
import domain.*;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class CreateRunService {
    private final LineStationDao lineStationDao = LineStationDao.of();
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

    // Restituisce una lista di orari di partenza disponibili (esempio ogni 15 min tra 6:00 e 22:00)
    public List<String> getAvailableDepartureTimes() {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        return java.util.stream.IntStream.range(6*4, 22*4)
                .mapToObj(i -> java.time.LocalTime.of(i/4, (i%4)*15).format(formatter))
                .collect(java.util.stream.Collectors.toList());
    }

    // DatePicker: solo oggi e domani selezionabili
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
            Run run = runDao.createRun(line.getIdLine(), convoy.getId(), operator.getIdStaff(), timeDeparture, timeArrival, idFirstStation, idLastStation);
            return run != null ? null : "Errore durante la creazione della run";
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    // Navigazione: torna alla schermata di gestione corse
    public void goBackToManageRun() {
        businessLogic.controller.SceneManager.getInstance().switchScene("/businessLogic/fxml/ManageRun.fxml");
    }
}
