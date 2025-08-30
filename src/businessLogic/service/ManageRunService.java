package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.*;
import java.util.List;
import java.util.logging.Logger;

public class ManageRunService {
    private static final Logger logger = Logger.getLogger(ManageRunService.class.getName());
    private final RailSuiteFacade facade = new RailSuiteFacade();
    /**
     * Gestisce la logica di fine corsa: rimuove le carrozze segnalate dal convoglio, le inserisce nel deposito associato
     * alla stazione di coda con lo status corretto, e imposta il timer per la disponibilità futura.
     * Da chiamare quando una corsa viene completata.
     */
    public void completeRun(Run run) {
        try {
            int convoyId = run.getIdConvoy();
            int tailStationId = run.getIdLastStation();
            List<Notification> approvedNotifications = facade.selectApprovedNotificationsByConvoy(convoyId);
            for (Notification notif : approvedNotifications) {
                int carriageId = notif.getIdCarriage();
                // Rimuovi la carrozza dal convoglio
                facade.updateCarriageConvoy(carriageId, null);
                // Propaga la rimozione alle corse future
                List<Run> futureRuns = facade.selectRunsForConvoyAfterTime(convoyId, run.getTimeArrival());
                for (Run _ : futureRuns) {
                    facade.updateCarriageConvoy(carriageId, null); // Assicura che la carrozza non sia più assegnata
                }
                // Trova il deposito associato alla stazione di coda
                Depot depot = facade.getDepotByStationId(tailStationId);
                if (depot != null) {
                    int depotId = depot.getIdDepot();
                    CarriageDepot.StatusOfCarriage status =
                        notif.getTypeOfNotification().equalsIgnoreCase("CLEANING") ?
                        CarriageDepot.StatusOfCarriage.CLEANING :
                        CarriageDepot.StatusOfCarriage.MAINTENANCE;
                    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                    CarriageDepot carriageDepot = CarriageDepot.of(depotId, carriageId, now, null, status);
                    facade.insertCarriageDepot(carriageDepot);
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
            return facade.selectAllRun();
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
            return facade.searchRunsByDay(line, convoy, operator, firstStation, dayStart, dayEnd);
        } catch (Exception e) {
            logger.severe("Errore in searchRunsByDay: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }
    /**
     * Aggiorna lo stato delle carrozze in deposito che hanno terminato il tempo di CLEANING o MAINTENANCE.
     * Può essere chiamato periodicamente o all'avvio dell'applicazione.
     */
    public void updateCarriageDepotStatuses() {
        try {
            List<CarriageDepot> depots = facade.getCarriagesInCleaningOrMaintenance();
            long now = System.currentTimeMillis();
            for (CarriageDepot depot : depots) {
                long entered = depot.getTimeEntered().getTime();
                long millisToWait = depot.getStatusOfCarriage() == CarriageDepot.StatusOfCarriage.CLEANING ? 3_600_000L : 21_600_000L;
                if (now - entered >= millisToWait) {
                    facade.updateCarriageDepotStatusAndExitTime(
                        depot.getIdDepot(),
                        depot.getIdCarriage(),
                        CarriageDepot.StatusOfCarriage.AVAILABLE.name(),
                        new java.sql.Timestamp(now)
                    );
                }
            }
        } catch (Exception e) {
            logger.severe("Errore in updateCarriageDepotStatuses: " + e.getMessage());
        }
    }
}
