package businessLogic;

import dao.*;
import domain.*;
import java.sql.Timestamp;
import java.util.List;
import java.sql.SQLException;

/**
 * Facade class that provides a simplified interface to the business logic layer of the RailSuite application.
 * It aggregates and exposes high-level methods for managing carriages, staff, stations, lines, convoys, depots, and runs,
 * delegating the actual data access and operations to the underlying DAO classes.
 * This class is used by services and controllers to interact with the core domain logic in a consistent and centralized way.
 */
public class RailSuiteFacade {
    private final CarriageDao carriageDao = CarriageDao.of();
    private final StaffDao staffDao = StaffDao.of();
    private final StationDao stationDao = StationDao.of();
    private final LineDao lineDao = LineDao.of();
    private final ConvoyDao convoyDao = ConvoyDao.of();
    private final ConvoyPoolDao convoyPoolDao = ConvoyPoolDao.of();
    private final StaffPoolDao staffPoolDao = StaffPoolDao.of();
    private final LineStationDao lineStationDao = LineStationDao.of();
    private final CarriageDepotDao carriageDepotDao = CarriageDepotDao.of();
    private final DepotDao depotDao = DepotDao.of();
    private final RunDao runDao = RunDao.of();
    private final NotificationDao notificationDao = NotificationDao.of();

    // Carriage

    /**
     * Retrieves a carriage by its ID.
     * Used to get detailed information about a specific carriage.
     */
    public Carriage selectCarriage(int id) throws SQLException {
        return carriageDao.selectCarriage(id);
    }

    /**
     * Retrieves all carriages assigned to a specific convoy.
     * Used to display or manage carriages of a convoy.
     */
    public List<Carriage> selectCarriagesByConvoyId(int convoyId) throws SQLException {
        return carriageDao.selectCarriagesByConvoyId(convoyId);
    }

    /**
     * Updates the convoy assignment for a carriage.
     * Used to assign or remove a carriage from a convoy.
     */
    public boolean updateCarriageConvoy(int carriageId, Integer idConvoy) throws SQLException {
        return carriageDao.updateCarriageConvoy(carriageId, idConvoy);
    }

    /**
     * Restituisce le carrozze di un convoglio ordinate per posizione.
     */
    public List<Carriage> getCarriagesByConvoyPosition(int convoyId) throws SQLException {
        return carriageDepotDao.getCarriagesByConvoyPosition(convoyId);
    }
    // Staff

    /**
     * Retrieves a staff member by their email.
     * Used for authentication and staff lookup.
     */
    public Staff findStaffByEmail(String email) throws SQLException {
        return staffDao.findByEmail(email);
    }

    /**
     * Retrieves all staff members of a specific type.
     * Used to filter staff by their role or type.
     */
    public List<Staff> findStaffByType(Staff.TypeOfStaff type) throws SQLException {
        return staffDao.findByType(type);
    }

    /**
     * Retrieves all operators (staff with OPERATOR role).
     * Used to list or manage all operators.
     */
    public List<Staff> findAllOperators() throws SQLException {
        return staffDao.findByType(Staff.TypeOfStaff.OPERATOR);
    }

    // Station

    /**
     * Retrieves all head stations (main stations) in the system.
     * Used to list or manage head stations.
     */
    public List<Station> findAllHeadStations() throws SQLException {
        return stationDao.findAllHeadStations();
    }

    // Line

    /**
     * Retrieves all lines in the system.
     * Used to list or manage all lines.
     */
    public List<Line> findAllLines() throws Exception {
        return lineDao.getAllLines();
    }

    // Convoy

    /**
     * Retrieves a convoy by its ID.
     * Used to get detailed information about a specific convoy.
     */
    public Convoy selectConvoy(int id) throws SQLException {
        return convoyDao.selectConvoy(id);
    }

    /**
     * Retrieves all convoys in the system.
     * Used to list or manage all convoys.
     */
    public List<Convoy> selectAllConvoys() throws SQLException {
        return convoyDao.selectAllConvoys();
    }

    /**
     * Removes a carriage from a convoy.
     * Used to unassign a carriage from a convoy.
     */
    public boolean removeCarriageFromConvoy(int convoyId, Carriage carriage) throws SQLException {
        return convoyDao.removeCarriageFromConvoy(convoyId, carriage);
    }

    /**
     * Creates a new convoy with the given carriages.
     * Used to add a new convoy to the system.
     */
    public Convoy createConvoy(List<Carriage> carriages) throws SQLException {
        return convoyDao.createConvoy(carriages);
    }

    /**
     * Removes a convoy from the system.
     * Used to delete a convoy and its associations.
     */
    public boolean removeConvoy(int id) throws SQLException {
        return convoyDao.removeConvoy(id);
    }
    // ConvoyPool

    /**
     * Retrieves a ConvoyPool by convoy ID.
     * Used to get pool information for a specific convoy.
     */
    public ConvoyPool getConvoyPoolById(int idConvoy) throws SQLException {
        return convoyPoolDao.getConvoyPoolById(idConvoy);
    }

    // LineStation

    /**
     * Retrieves all LineStation entries for a specific line.
     * Used to list all stations on a line.
     */
    public List<LineStation> findLineStationsByLineId(int lineId) throws SQLException {
        return lineStationDao.findByLine(lineId);
    }

    // CarriageDepot

    /**
     * Retrieves all carriages in a specific depot.
     * Used to list or manage carriages in a depot.
     */
    public List<CarriageDepot> getCarriagesByDepot(int idDepot) throws SQLException {
        return carriageDepotDao.getCarriagesByDepot(idDepot);
    }

    // Depot

    /**
     * Retrieves a depot by its ID.
     * Used to get detailed information about a specific depot.
     */
    public Depot getDepot(int idDepot) throws SQLException {
        return depotDao.getDepot(idDepot);
    }

    /**
     * Retrieves all depots in the system.
     * Used to list or manage all depots.
     */
    public List<Depot> getAllDepots() throws SQLException {
        return depotDao.getAllDepots();
    }

    /**
     * Inserts a new depot into the system.
     * Used to add a new depot.
     */
    public void insertDepot(int idDepot) throws SQLException {
        depotDao.insertDepot(idDepot);
    }

    /**
     * Deletes a depot from the system.
     * Used to remove a depot.
     */
    public void deleteDepot(int idDepot) throws SQLException {
        depotDao.deleteDepot(idDepot);
    }

    // Run

    /**
     * Retrieves a run by line, convoy, and staff ID.
     * Used to get run information for a specific line, convoy, and staff.
     */
    public Run selectRun(int idLine, int idConvoy, int idStaff) throws SQLException {
        return runDao.selectRunByLineConvoyAndStaff(idLine, idConvoy, idStaff);
    }

    /**
     * Removes a run from the system by line, convoy and staff ID.
     * Used to delete a run.
     */
    public boolean removeRun(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture) throws SQLException {
        return runDao.deleteRun(idLine, idConvoy, idStaff, timeDeparture);
    }

    /**
     * Creates a new run with the given parameters.
     * Used to add a new run to the system.
     */
    public boolean createRun(int idLine, int idConvoy, int idStaff, java.sql.Timestamp timeDeparture, java.sql.Timestamp timeArrival, int idFirstStation, int idLastStation) throws SQLException {
        return runDao.createRun(idLine, idConvoy, idStaff, timeDeparture, timeArrival, idFirstStation, idLastStation);
    }

    /**
     * Retrieves all runs assigned to a specific staff member.
     * Used to list or manage runs by staff.
     */
    public List<Run> selectRunsByStaff(int idStaff) throws SQLException {
        return runDao.selectRunsByStaff(idStaff);
    }

    /**
     * Retrieves all runs for a specific convoy.
     * Used to list or manage runs by convoy.
     */
    public List<Run> selectRunsByConvoy(int convoyId) throws SQLException {
        return runDao.selectRunsByConvoy(convoyId);
    }

    /**
     * Restituisce tutte le notifiche approvate per un convoglio.
     */
    public List<Notification> selectApprovedNotificationsByConvoy(int convoyId) throws SQLException {
        return notificationDao.selectApprovedNotificationsByConvoy(convoyId);
    }

    /**
     * Restituisce tutte le corse future per un convoglio dopo un certo orario.
     */
    public List<Run> selectRunsForConvoyAfterTime(int convoyId, java.sql.Timestamp afterTime) throws SQLException {
        return runDao.selectRunsForConvoyAfterTime(convoyId, afterTime);
    }

    /**
     * Restituisce il deposito associato a una stazione.
     */
    public Depot getDepotByStationId(int stationId) throws SQLException {
        return depotDao.getDepotByStationId(stationId);
    }

    /**
     * Restituisce tutte le corse presenti nel sistema.
     */
    public List<Run> selectAllRun() throws SQLException {
        return runDao.selectAllRun();
    }

    /**
     * Restituisce le corse per giorno e filtri.
     */
    public List<Run> searchRunsByDay(String line, String convoy, String operator, String firstStation, java.sql.Timestamp dayStart, java.sql.Timestamp dayEnd) throws SQLException {
        return runDao.searchRunsByDay(line, convoy, operator, firstStation, dayStart, dayEnd);
    }

    /**
     * Restituisce le carrozze in cleaning o maintenance.
     */
    public List<CarriageDepot> getCarriagesInCleaningOrMaintenance() throws SQLException {
        return carriageDepotDao.getCarriagesInCleaningOrMaintenance();
    }

    /**
     * Aggiorna lo stato e il tempo di uscita di una carrozza in deposito.
     */
    public void updateCarriageDepotStatusAndExitTime(int idDepot, int idCarriage, String status, java.sql.Timestamp exitTime) throws SQLException {
        carriageDepotDao.updateCarriageDepotStatusAndExitTime(idDepot, idCarriage, status, exitTime);
    }

    /**
     * Recupera tutte le notifiche associate a un convoglio.
     */
    public java.util.List<Notification> getNotificationsByConvoyId(int convoyId) throws java.sql.SQLException {
        return notificationDao.getNotificationsByConvoyId(convoyId);
    }

    /**
     * Recupera tutte le notifiche associate a un convoglio e a uno staff specifico.
     */
    public java.util.List<Notification> getAllNotificationsForConvoyAndStaff(int convoyId, int idStaff) throws java.sql.SQLException {
        return notificationDao.getAllNotificationsForConvoyAndStaff(convoyId, idStaff);
    }

    /**
     * Sposta una notifica nello storico con stato specificato.
     */
    public void moveNotificationToHistory(int idCarriage, int idConvoy, java.sql.Timestamp dateTimeOfNotification, String typeOfNotification, int idStaff, String staffName, String staffSurname, String status) throws SQLException {
        notificationDao.moveNotificationToHistory(idCarriage, idConvoy, dateTimeOfNotification, typeOfNotification, idStaff, staffName, staffSurname, status);
    }

    /**
     * Elimina una notifica dalla tabella principale.
     */
    public void deleteNotification(int idCarriage, int idConvoy, java.sql.Timestamp dateTimeOfNotification) throws SQLException {
        notificationDao.deleteNotification(idCarriage, idConvoy, dateTimeOfNotification);
    }

    /**
     * Restituisce i dettagli di un convoglio per ConvoyDetailsService.
     */
    public businessLogic.service.ConvoyDetailsService.ConvoyDetailsRaw selectConvoyDetailsById(int convoyId) throws SQLException {
        return convoyDao.selectConvoyDetailsById(convoyId);
    }

    /**
     * Restituisce la tabella oraria reale per una corsa.
     */
    public java.util.List<domain.TimeTable.StationArrAndDep> findTimeTableForRun(int idLine, int idFirstStation, String depTimeStr) throws SQLException {
        return lineStationDao.findTimeTableForRun(idLine, idFirstStation, depTimeStr);
    }

    /**
     * Aggiunge una notifica tramite NotificationDao.
     */
    public void addNotification(int idCarriage, int idConvoy, java.sql.Timestamp timestamp, String workType, int idStaff) throws SQLException {
        notificationDao.addNotification(idCarriage, idConvoy, timestamp, workType, idStaff);
    }

    /**
     * Aggiunge più carrozze a un convoglio.
     */
    public void addCarriagesToConvoy(int convoyId, List<Carriage> carriages) throws SQLException {
        convoyDao.addCarriagesToConvoy(convoyId, carriages);
    }

    /**
     * Trova il depot attivo per una carrozza.
     */
    public CarriageDepot findActiveDepotByCarriage(int idCarriage) throws SQLException {
        return carriageDepotDao.findActiveDepotByCarriage(idCarriage);
    }

    /**
     * Inserisce un ConvoyPool.
     */
    public void insertConvoyPool(ConvoyPool pool) throws SQLException {
        convoyPoolDao.insertConvoyPool(pool);
    }

    /**
     * Elimina la relazione depot-carrozza se disponibile.
     */
    public void deleteCarriageDepotByCarriageIfAvailable(int idCarriage) throws SQLException {
        carriageDepotDao.deleteCarriageDepotByCarriageIfAvailable(idCarriage);
    }

    /**
     * Restituisce i dati tabellari dei convogli per una stazione.
     */
    public List<domain.DTO.ConvoyTableDTO> getConvoyTableDataByStation(int stationId) throws SQLException {
        return convoyPoolDao.getConvoyTableDataByStation(stationId);
    }

    /**
     * Trova le carrozze disponibili per un convoglio.
     */
    public List<Carriage> findAvailableCarriagesForConvoy(int idStation, String modelType) throws SQLException {
        return carriageDepotDao.findAvailableCarriagesForConvoy(idStation, modelType);
    }

    /**
     * Trova i tipi di carrozza disponibili per un convoglio.
     */
    public List<String> findAvailableCarriageTypesForConvoy(int idStation) throws SQLException {
        return carriageDepotDao.findAvailableCarriageTypesForConvoy(idStation);
    }

    /**
     * Inserisce una relazione depot-carrozza.
     */
    public void insertCarriageDepot(CarriageDepot cd) throws SQLException {
        carriageDepotDao.insertCarriageDepot(cd);
    }

    /**
     * Trova le carrozze con stato depot per un convoglio.
     */
    public List<domain.DTO.CarriageDepotDTO> findCarriagesWithDepotStatusByConvoy(int idConvoy) throws SQLException {
        return carriageDepotDao.findCarriagesWithDepotStatusByConvoy(idConvoy);
    }

    /**
     * Trova i modelli di carrozza disponibili per un convoglio.
     */
    public List<String> findAvailableCarriageModelsForConvoy(int idStation, String modelType) throws SQLException {
        return carriageDepotDao.findAvailableCarriageModelsForConvoy(idStation, modelType);
    }

    /**
     * Restituisce i convogli disponibili per una nuova corsa.
     */
    public List<Convoy> getConvoysForNewRun(int idStation, String timeDeparture, java.time.LocalDate dateDeparture, int idLine) throws SQLException {
        return convoyDao.getConvoysForNewRun(idStation, timeDeparture, dateDeparture, idLine);
    }

    /**
     * Restituisce gli operatori disponibili per una corsa.
     */
    public java.util.List<domain.DTO.StaffDTO> findAvailableOperatorsForRun(int idStation, java.time.LocalDate dateDeparture, String timeDeparture) throws SQLException {
        return StaffPoolDao.of().findAvailableOperatorsForRun(idStation, dateDeparture, timeDeparture);
    }

    /**
     * Restituisce tutte le notifiche presenti nel sistema.
     */
    public java.util.List<Notification> getAllNotifications() throws java.sql.SQLException {
        return notificationDao.getAllNotifications();
    }

    /**
     * Aggiunge una notifica con tutti i parametri richiesti da NotificationService.
     */
    public void addNotification(int idCarriage, String typeOfCarriage, int idConvoy, String typeOfNotification, java.sql.Timestamp notifyTime, int idStaff, String staffName, String staffSurname, String status) throws java.sql.SQLException {
        // Si assume che notificationDao.addNotification accetti solo i parametri principali, gli altri sono usati solo per la notifica agli observer
        notificationDao.addNotification(idCarriage, idConvoy, notifyTime, typeOfNotification, idStaff);
    }

    /**
     * Restituisce le righe dei convogli assegnati a uno staff.
     */
    public java.util.List<dao.ConvoyDao.ConvoyAssignedRow> selectAssignedConvoysRowsByStaff(int staffId) throws java.sql.SQLException {
        return convoyDao.selectAssignedConvoysRowsByStaff(staffId);
    }

    /**
     * Restituisce le info dei convogli assegnati a uno staff come DTO, senza esporre classi DAO.
     */
    public java.util.List<businessLogic.service.OperatorHomeService.AssignedConvoyInfo> getAssignedConvoysInfoByStaff(int staffId) throws java.sql.SQLException {
        java.util.List<dao.ConvoyDao.ConvoyAssignedRow> rows = convoyDao.selectAssignedConvoysRowsByStaff(staffId);
        return rows.stream()
                .map(r -> new businessLogic.service.OperatorHomeService.AssignedConvoyInfo(
                        r.convoyId, r.idLine, r.idStaff, r.idFirstStation, r.timeDeparture, r.departureStation, r.arrivalStation, r.arrivalTime
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    // --- Metodi aggiunti/corretti per RunDetailsService ---
    public domain.DTO.RunDTO selectRun(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture) throws SQLException {
        return runDao.selectRunDTODetails(idLine, idConvoy, idStaff, timeDeparture);
    }

    public boolean hasOperatorConflicts(int idStaff, Timestamp timeDeparture) throws SQLException {
        return runDao.findRunsByStaffAfterTime(idStaff, timeDeparture);
    }

    public boolean hasRunConflict(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture) throws SQLException {
        return runDao.findRunsByConvoyAfterTime(idLine, idConvoy, idStaff, timeDeparture);
    }

    public boolean hasConvoyConflict(int idConvoy) throws SQLException {
        // Consideriamo conflitto se ci sono future run per il convoglio
        List<Run> futureRuns = runDao.selectRunsForConvoyAfterTime(idConvoy, new Timestamp(System.currentTimeMillis()));
        return !futureRuns.isEmpty();
    }

    public List<domain.DTO.ConvoyTableDTO> checkConvoyAvailability(int firstStation) throws SQLException {
        return convoyPoolDao.getConvoyTableDataByStation(firstStation);
    }

    public List<domain.DTO.ConvoyTableDTO> getFutureRunsOfCurrentConvoy(int idConvoy, Timestamp timeDeparture) throws SQLException {
        return runDao.selectRunsByConvoyAndTimeForTakeFutureRuns(idConvoy, timeDeparture);
    }

    public boolean replaceFutureRunsConvoy(int idConvoy, int newIdConvoy, domain.DTO.RunDTO run) throws SQLException {
        return runDao.replaceFutureRunsConvoy(idConvoy, newIdConvoy, run);
    }

    public List<domain.Staff> checkOperatorAvailability() throws SQLException {
        // Dummy: restituisce tutti gli operatori
        return staffDao.findByType(domain.Staff.TypeOfStaff.OPERATOR);
    }

    public void updateRunStaff(int idLine, int idConvoy, int idStaff, Timestamp timeDeparture, int newIdStaff) throws SQLException {
        runDao.updateRunStaff(idLine, idConvoy, idStaff, timeDeparture, newIdStaff);
    }

    public boolean updateRunDepartureTime(int lineId, int convoyId, int staffId, Timestamp oldDeparture, Timestamp newDeparture) throws SQLException {
        return runDao.updateRunDepartureTime(lineId, convoyId, staffId, oldDeparture, newDeparture);
    }
    // --- Fine metodi aggiunti/corretti ---
}
