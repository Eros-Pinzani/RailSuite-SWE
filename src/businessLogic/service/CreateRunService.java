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
    private final LineDao lineDao = LineDao.of();
    private final ConvoyDao convoyDao = ConvoyDao.of();
    private final LineStationDao lineStationDao = LineStationDao.of();

    private final List<Convoy> convoysPoolAvailable = new ArrayList<>();
    private final List<Convoy> convoysPoolAvailableFilteredByType = new ArrayList<>();

    private Duration travelTime;

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

    public List<Line> getAllLines() {
        try {
            return lineDao.getAllLines();
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving lines: " + e.getMessage(), e);
        }
    }

    public Duration calculateTravelTime(Line selectedLine) {
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
                LocalDate tomorrow = today.plusDays(1);
                setDisable(empty || (item.isBefore(today) || item.isAfter(tomorrow)));
            }
        };
    }

    public List<String> getAvailableDepartureTimes(LocalDate selectedDate) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        java.time.LocalTime start;
        java.time.LocalTime end = java.time.LocalTime.of(22, 0);
        List<String> times = new ArrayList<>();
        LocalDate today = LocalDate.now();
        if (selectedDate != null && selectedDate.isAfter(today)) {
            start = java.time.LocalTime.of(6, 0);
        } else {
            java.time.LocalTime now = java.time.LocalTime.now();
            start = now.withMinute((now.getMinute() / 15) * 15).plusMinutes(15);
            if (start.isBefore(java.time.LocalTime.of(6, 0))) {
                start = java.time.LocalTime.of(6, 0);
            }
        }
        for (java.time.LocalTime time = start; !time.isAfter(end); time = time.plusMinutes(15)) {
            times.add(time.format(formatter));
        }
        return times;
    }

    public List<TimeTable.StationArrAndDep> getTimeTableForRun(int idLine, int idStartStation, String departureTime) {
        try {
            return lineStationDao.findTimeTableForRun(idLine, idStartStation, departureTime);
        } catch (Exception e) {
            throw new RuntimeException("Errore nel calcolo della tabella orari: " + e.getMessage(), e);
        }
    }

    public Run createRun(Line line, LocalDate date, String time, Convoy convoy, StaffDTO operator) {
        try{
            if (travelTime == null) {
                travelTime = waitForTravelTime(line);
            }
            RunDao runDao = RunDao.of();
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.of(date, java.time.LocalTime.parse(time));
            java.sql.Timestamp departureTimestamp = java.sql.Timestamp.valueOf(dateTime);
            java.sql.Timestamp arrivalTimestamp = java.sql.Timestamp.valueOf(dateTime.plus(travelTime));
            runDao.createRun(
                line.getIdLine(),
                convoy.getId(),
                operator.getIdStaff(),
                departureTimestamp,
                arrivalTimestamp,
                line.getIdFirstStation(),
                line.getIdLastStation()
            );
            return runDao.selectRunByLineConvoyAndStaff(
                line.getIdLine(),
                convoy.getId(),
                operator.getIdStaff()
            );
        }catch (Exception e) {
            throw new RuntimeException("Error while creating run: " + e.getMessage(), e);
        }
    }

    public void setTravelTime(Duration travelTime) {
        this.travelTime = travelTime;
    }

    public Duration getTravelTime() {
        return travelTime;
    }

    /**
     * * Waits for the travel time to be calculated for the selected line.
     * This method starts a new thread to calculate the travel time and waits for it to complete.
     * If the travel time is not calculated within a certain number of tries, it throws an
     * IllegalStateException.
     * @param selectedLine the line for which to calculate the travel time
     * @return the calculated travel time
     * @throws IllegalStateException if the travel time is not calculated within the maximum number of tries
     */
    public Duration waitForTravelTime(Line selectedLine) {
        setTravelTime(null);
        new Thread(() -> {
            Duration duration = calculateTravelTime(selectedLine);
            setTravelTime(duration);
        }).start();
        int maxTries = 50;
        int tries = 0;
        while (getTravelTime() == null && tries < maxTries) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while waiting for travelTime", e);
            }
            tries++;
        }
        if (getTravelTime() == null) {
            throw new IllegalStateException("travelTime not calculated: cannot continue without duration.");
        }
        return getTravelTime();
    }
}
