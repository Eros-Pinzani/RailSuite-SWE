package businessLogic;

import dao.*;
import domain.*;

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

    // Carriage
    /**
     * Retrieves a carriage by its ID.
     * Used to get detailed information about a specific carriage.
     */
    public Carriage selectCarriage(int id) throws SQLException {
        return carriageDao.selectCarriage(id);
    }

    /**
     * Retrieves all carriages in the system.
     * Used to list or manage all carriages.
     */
    public List<Carriage> selectAllCarriages() throws SQLException {
        return carriageDao.selectAllCarriages();
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

    // Staff
    /**
     * Retrieves a staff member by their ID.
     * Used to get detailed information about a specific staff member.
     */
    public Staff findStaffById(int id) throws SQLException {
        return staffDao.findById(id);
    }

    /**
     * Retrieves a staff member by their email.
     * Used for authentication and staff lookup.
     */
    public Staff findStaffByEmail(String email) throws SQLException {
        return staffDao.findByEmail(email);
    }

    /**
     * Retrieves all staff members in the system.
     * Used to list or manage all staff.
     */
    public List<Staff> findAllStaff() throws SQLException {
        return staffDao.findAll();
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
     * Retrieves a station by its ID.
     * Used to get detailed information about a specific station.
     */
    public Station findStationById(int id) throws SQLException {
        return stationDao.findById(id);
    }

    /**
     * Retrieves a station by its location.
     * Used to find a station based on its location string.
     */
    public Station findStationByLocation(String location) throws SQLException {
        return stationDao.findByLocation(location);
    }

    /**
     * Retrieves all stations in the system.
     * Used to list or manage all stations.
     */
    public List<Station> findAllStations() throws SQLException {
        return stationDao.findAll();
    }

    /**
     * Retrieves all head stations (main stations) in the system.
     * Used to list or manage head stations.
     */
    public List<Station> findAllHeadStations() throws SQLException {
        return stationDao.findAllHeadStations();
    }

    // Line
    /**
     * Retrieves a line by its ID.
     * Used to get detailed information about a specific line.
     */
    public Line findLineById(int idLine) throws SQLException {
        return lineDao.findById(idLine);
    }

    /**
     * Retrieves all lines in the system.
     * Used to list or manage all lines.
     */
    public List<Line> findAllLines() throws SQLException {
        return lineDao.findAll();
    }

    /**
     * Retrieves all lines that pass through a specific station.
     * Used to find lines by station.
     */
    public List<Line> findLinesByStation(int idStation) throws SQLException {
        return lineDao.findByStation(idStation);
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
     * Adds a carriage to a convoy.
     * Used to assign a carriage to a convoy.
     */
    public boolean addCarriageToConvoy(int convoyId, Carriage carriage) throws SQLException {
        return convoyDao.addCarriageToConvoy(convoyId, carriage);
    }

    /**
     * Removes a carriage from a convoy.
     * Used to unassign a carriage from a convoy.
     */
    public boolean removeCarriageFromConvoy(int convoyId, Carriage carriage) throws SQLException {
        return convoyDao.removeCarriageFromConvoy(convoyId, carriage);
    }

    /**
     * Finds the convoy ID associated with a specific carriage.
     * Used to determine which convoy a carriage belongs to.
     */
    public Integer findConvoyIdByCarriageId(int carriageId) throws SQLException {
        return convoyDao.findConvoyIdByCarriageId(carriageId);
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

    /**
     * Updates a ConvoyPool entry.
     * Used to update the status or information of a convoy pool.
     */
    public void updateConvoyPool(ConvoyPool convoyPool) throws SQLException {
        convoyPoolDao.updateConvoyPool(convoyPool);
    }

    /**
     * Retrieves all ConvoyPool entries in the system.
     * Used to list or manage all convoy pools.
     */
    public List<ConvoyPool> getAllConvoyPools() throws SQLException {
        return convoyPoolDao.getAllConvoyPools();
    }

    /**
     * Retrieves all convoys at a specific station.
     * Used to find convoys by station.
     */
    public List<ConvoyPool> getConvoysByStation(int idStation) throws SQLException {
        return convoyPoolDao.getConvoysByStation(idStation);
    }

    /**
     * Retrieves all convoys with a specific status.
     * Used to filter convoys by their status.
     */
    public List<ConvoyPool> getConvoysByStatus(ConvoyPool.ConvoyStatus status) throws SQLException {
        return convoyPoolDao.getConvoysByStatus(status);
    }

    /**
     * Retrieves all convoys at a station with a specific status.
     * Used to filter convoys by station and status.
     */
    public List<ConvoyPool> getConvoysByStationAndStatus(int idStation, ConvoyPool.ConvoyStatus status) throws SQLException {
        return convoyPoolDao.getConvoysByStationAndStatus(idStation, status);
    }

    // StaffPool
    /**
     * Retrieves a StaffPool entry by staff ID.
     * Used to get pool information for a specific staff member.
     */
    public StaffPool findStaffPoolById(int idStaff) throws SQLException {
        return staffPoolDao.findById(idStaff);
    }

    /**
     * Retrieves all StaffPool entries for a specific station.
     * Used to find staff pools by station.
     */
    public List<StaffPool> findStaffPoolByStation(int idStation) throws SQLException {
        return staffPoolDao.findByStation(idStation);
    }

    /**
     * Updates a StaffPool entry.
     * Used to update the status or information of a staff pool.
     */
    public void updateStaffPool(StaffPool staffPool) throws SQLException {
        staffPoolDao.update(staffPool);
    }

    /**
     * Retrieves all StaffPool entries with a specific shift status.
     * Used to filter staff pools by their shift status.
     */
    public List<StaffPool> findStaffPoolByStatus(StaffPoolDao.ShiftStatus status) throws SQLException {
        return staffPoolDao.findByStatus(status);
    }

    /**
     * Retrieves all StaffPool entries with a specific shift status at a station.
     * Used to filter staff pools by status and station.
     */
    public List<StaffPool> findStaffPoolByStatusAndStation(StaffPoolDao.ShiftStatus status, int idStation) throws SQLException {
        return staffPoolDao.findByStatusAndStation(status, idStation);
    }

    // LineStation
    /**
     * Retrieves a LineStation entry by line and station ID.
     * Used to get information about a station on a specific line.
     */
    public LineStation findLineStationById(int idLine, int idStation) throws SQLException {
        return lineStationDao.findById(idLine, idStation);
    }

    /**
     * Retrieves all LineStation entries for a specific line.
     * Used to list all stations on a line.
     */
    public List<LineStation> findLineStationsByLineId(int lineId) throws SQLException {
        return lineStationDao.findByLine(lineId);
    }

    // CarriageDepot
    /**
     * Retrieves a CarriageDepot entry by depot and carriage ID.
     * Used to get depot information for a specific carriage.
     */
    public CarriageDepot getCarriageDepot(int idDepot, int idCarriage) throws SQLException {
        return carriageDepotDao.getCarriageDepot(idDepot, idCarriage);
    }
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

    // Run2
    /**
     * Retrieves runs by line and convoy ID.
     * Used to get run information for a specific line and convoy.
     */
    public List<Run2> selectRunsByLineAndConvoy(int idLine, int idConvoy) throws SQLException {
        return runDao.selectRunsByLineAndConvoy(idLine, idConvoy);
    }
    /**
     * Retrieves a run by line, convoy, and staff ID.
     * Used to get run information for a specific line, convoy, and staff.
     */
    public Run2 selectRun(int idLine, int idConvoy, int idStaff) throws SQLException {
        return runDao.selectRunByLineConvoyAndStaff(idLine, idConvoy, idStaff);
    }
    /**
     * Retrieves runs by staff and convoy ID.
     * Used to get run information for a specific staff and convoy.
     */
    public List<Run2> selectRunsByStaffAndConvoy(int idStaff, int idConvoy) throws SQLException {
        return runDao.selectRunsByStaffAndConvoy(idStaff, idConvoy);
    }
    /**
     * Retrieves runs by staff and line ID.
     * Used to get run information for a specific staff and line.
     */
    public List<Run2> selectRunsByStaffAndLine(int idStaff, int idLine) throws SQLException {
        return runDao.selectRunsByStaffAndLine(idStaff, idLine);
    }
    /**
     * Removes a run from the system by line, convoy and staff ID.
     * Used to delete a run.
     */
    public boolean removeRun(int idLine, int idConvoy, int idStaff) throws SQLException {
        return runDao.removeRun(idLine, idConvoy, idStaff);
    }
    /**
     * Creates a new run with the given parameters.
     * Used to add a new run to the system.
     */
    public boolean createRun(int idLine, int idConvoy, int idStaff, java.sql.Timestamp timeDeparture, java.sql.Timestamp timeArrival, int idFirstStation, int idLastStation) throws SQLException {
        return runDao.createRun(idLine, idConvoy, idStaff, timeDeparture, timeArrival, idFirstStation, idLastStation);
    }
    /**
     * Updates an existing run with new parameters.
     * Used to modify run details.
     */
    public boolean updateRun(int idLine, int idConvoy, int idStaff, java.sql.Timestamp timeDeparture, java.sql.Timestamp timeArrival, int idFirstStation, int idLastStation) throws SQLException {
        return runDao.updateRun(idLine, idConvoy, idStaff, timeDeparture, timeArrival, idFirstStation, idLastStation);
    }
    /**
     * Retrieves all runs assigned to a specific staff member.
     * Used to list or manage runs by staff.
     */
    public List<Run2> selectRunsByStaff(int idStaff) throws SQLException {
        return runDao.selectRunsByStaff(idStaff);
    }
    /**
     * Retrieves all runs for a specific line.
     * Used to list or manage runs by line.
     */
    public List<Run2> selectRunsByLine(int idLine) throws SQLException {
        return runDao.selectRunsByLine(idLine);
    }
    /**
     * Retrieves all runs for a specific convoy.
     * Used to list or manage runs by convoy.
     */
    public List<Run2> selectRunsByConvoy(int idConvoy) throws SQLException {
        return runDao.selectRunsByConvoy(idConvoy);
    }
    /**
     * Retrieves all runs that start at a specific station.
     * Used to list or manage runs by first station.
     */
    public List<Run2> selectRunsByFirstStation(int idFirstStation) throws SQLException {
        return runDao.selectRunsByFirstStation(idFirstStation);
    }
    /**
     * Retrieves all runs that end at a specific station.
     * Used to list or manage runs by last station.
     */
    public List<Run2> selectRunsByLastStation(int idLastStation) throws SQLException {
        return runDao.selectRunsByLastStation(idLastStation);
    }
    /**
     * Retrieves all runs that start at a specific station and have a specific departure time.
     * Used to filter runs by station and departure time.
     */
    public List<Run2> selectRunsByFirstStationAndDeparture(int idFirstStation, java.sql.Timestamp timeDeparture) throws SQLException {
        return runDao.selectRunsByFirstStationAndDeparture(idFirstStation, timeDeparture);
    }
}