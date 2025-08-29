package businessLogic.service;

import dao.*;
import domain.*;
import java.util.List;
import java.util.logging.Logger;

public class ManageRunService {
    private static final Logger logger = Logger.getLogger(ManageRunService.class.getName());
    /**
     * Gestisce la logica di fine corsa: rimuove le carrozze segnalate dal convoglio, le inserisce nel deposito associato
     * alla stazione di coda con lo status corretto, e imposta il timer per la disponibilità futura.
     * Da chiamare quando una corsa viene completata.
     */
    public void completeRun(Run run) {
        try {
            int convoyId = run.getIdConvoy();
            int tailStationId = run.getIdLastStation();
            List<Notification> approvedNotifications = NotificationDao.of().selectApprovedNotificationsByConvoy(convoyId);
            for (Notification notif : approvedNotifications) {
                int carriageId = notif.getIdCarriage();
                // Rimuovi la carrozza dal convoglio
                CarriageDao.of().updateCarriageConvoy(carriageId, null);
                // Propaga la rimozione alle corse future
                List<Run> futureRuns = RunDao.of().selectRunsForConvoyAfterTime(convoyId, run.getTimeArrival());
                for (Run _ : futureRuns) {
                    CarriageDao.of().updateCarriageConvoy(carriageId, null); // Assicura che la carrozza non sia più assegnata
                }
                // Trova il deposito associato alla stazione di coda
                Depot depot = DepotDao.of().getDepotByStationId(tailStationId);
                if (depot != null) {
                    int depotId = depot.getIdDepot();
                    CarriageDepot.StatusOfCarriage status =
                        notif.getTypeOfNotification().equalsIgnoreCase("CLEANING") ?
                        CarriageDepot.StatusOfCarriage.CLEANING :
                        CarriageDepot.StatusOfCarriage.MAINTENANCE;
                    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                    CarriageDepot carriageDepot = CarriageDepot.of(depotId, carriageId, now, null, status);
                    CarriageDepotDao.of().insertCarriageDepot(carriageDepot);
                    long millisToAdd = status == CarriageDepot.StatusOfCarriage.CLEANING ? 3_600_000L : 21_600_000L;
                    new Thread(() -> {
                        try {
                            Thread.sleep(millisToAdd);
                            java.sql.Timestamp exited = new java.sql.Timestamp(System.currentTimeMillis());
                            CarriageDepotDao.of().updateCarriageDepotStatusAndExitTime(depotId, carriageId, CarriageDepot.StatusOfCarriage.AVAILABLE.name(), exited);
                        } catch (Exception e) {
                            // Gestione errore silenziosa
                        }
                    }).start();
                }
            }
        } catch (Exception e) {
            logger.severe("Errore in completeRun: " + e.getMessage());
        }
    }

    /**
     * Restituisce tutte le corse presenti nel sistema.
     */
    public List<Run> getAllRun() {
        try {
            return RunDao.of().selectAllRun();
        } catch (Exception e) {
            logger.severe("Errore in getAllRun: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Filtra le corse in base ai parametri forniti.
     */
    public List<Run> filterRunRaws(String line, String convoy, String operator, String firstStation, java.sql.Timestamp dayStart) {
        List<Run> allRuns = getAllRun();
        return allRuns.stream().filter(run -> {
            boolean match = true;
            if (line != null) match &= line.equals(run.getLineName());
            if (convoy != null) match &= convoy.equals(String.valueOf(run.getIdConvoy()));
            if (operator != null) match &= operator.equals(run.getStaffNameSurname());
            if (firstStation != null) match &= firstStation.equals(run.getFirstStationName());
            if (dayStart != null) match &= run.getTimeDeparture().after(dayStart);
            return match;
        }).toList();
    }

    /**
     * Restituisce le corse per giorno e filtri.
     */
    public List<Run> searchRunsByDay(String line, String convoy, String operator, String firstStation, java.sql.Timestamp dayStart, java.sql.Timestamp dayEnd) {
        try {
            return RunDao.of().searchRunsByDay(line, convoy, operator, firstStation, dayStart, dayEnd);
        } catch (Exception e) {
            logger.severe("Errore in searchRunsByDay: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }
}
