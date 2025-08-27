package businessLogic;

import dao.*;
import domain.*;

import java.sql.Time;
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
    public List<Line> findAllLines() throws SQLException {
        return lineDao.findAll();
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
    public List<Run> selectRunsByConvoy(int idConvoy) throws SQLException {
        return runDao.selectRunsByConvoy(idConvoy);
    }
}