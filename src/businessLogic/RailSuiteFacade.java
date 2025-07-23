package businessLogic;

import dao.*;
import domain.*;
import mapper.*;

import java.util.List;
import java.sql.SQLException;

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
    public Carriage selectCarriage(int id) throws SQLException {
        return carriageDao.selectCarriage(id);
    }

    public List<Carriage> selectAllCarriages() throws SQLException {
        return carriageDao.selectAllCarriages();
    }

    public List<Carriage> selectCarriagesByConvoyId(int convoyId) throws SQLException {
        return carriageDao.selectCarriagesByConvoyId(convoyId);
    }

    public boolean updateCarriageConvoy(int carriageId, Integer idConvoy) throws SQLException {
        return carriageDao.updateCarriageConvoy(carriageId, idConvoy);
    }


    // Staff
    public Staff findStaffById(int id) throws SQLException {
        return staffDao.findById(id);
    }

    public Staff findStaffByEmail(String email) throws SQLException {
        return staffDao.findByEmail(email);
    }

    public List<Staff> findAllStaff() throws SQLException {
        return staffDao.findAll();
    }

    public List<Staff> findStaffByType(Staff.TypeOfStaff type) throws SQLException {
        return staffDao.findByType(type);
    }

    // Station
    public Station findStationById(int id) throws SQLException {
        return stationDao.findById(id);
    }

    public Station findStationByLocation(String location) throws SQLException {
        return stationDao.findByLocation(location);
    }

    public List<Station> findAllStations() throws SQLException {
        return stationDao.findAll();
    }

    public List<Station> findAllHeadStations() throws SQLException {
        return stationDao.findAllHeadStations();
    }

    // Line
    public Line findLineById(int idLine) throws SQLException {
        return lineDao.findById(idLine);
    }

    public List<Line> findAllLines() throws SQLException {
        return lineDao.findAll();
    }

    public List<Line> findLinesByStation(int idStation) throws SQLException {
        return lineDao.findByStation(idStation);
    }

    // Convoy
    public Convoy selectConvoy(int id) throws SQLException {
        return convoyDao.selectConvoy(id);
    }

    public List<Convoy> selectAllConvoys() throws SQLException {
        return convoyDao.selectAllConvoys();
    }

    public boolean removeConvoy(int id) throws SQLException {
        return convoyDao.removeConvoy(id);
    }

    public boolean addCarriageToConvoy(int convoyId, Carriage carriage) throws SQLException {
        return convoyDao.addCarriageToConvoy(convoyId, carriage);
    }

    public boolean removeCarriageFromConvoy(int convoyId, Carriage carriage) throws SQLException {
        return convoyDao.removeCarriageFromConvoy(convoyId, carriage);
    }

    public Integer findConvoyIdByCarriageId(int carriageId) throws SQLException {
        return convoyDao.findConvoyIdByCarriageId(carriageId);
    }

    public Convoy createConvoy(List<Carriage> carriages) throws SQLException {
        return convoyDao.createConvoy(carriages);
    }

    // ConvoyPool
    public ConvoyPool getConvoyPoolById(int idConvoy) throws SQLException {
        return convoyPoolDao.getConvoyPoolById(idConvoy);
    }

    public void updateConvoyPool(ConvoyPool convoyPool) throws SQLException {
        convoyPoolDao.updateConvoyPool(convoyPool);
    }

    public List<ConvoyPool> getAllConvoyPools() throws SQLException {
        return convoyPoolDao.getAllConvoyPools();
    }

    public List<ConvoyPool> getConvoysByStation(int idStation) throws SQLException {
        return convoyPoolDao.getConvoysByStation(idStation);
    }

    public List<ConvoyPool> getConvoysByStatus(ConvoyPool.ConvoyStatus status) throws SQLException {
        return convoyPoolDao.getConvoysByStatus(status);
    }

    public List<ConvoyPool> getConvoysByStationAndStatus(int idStation, ConvoyPool.ConvoyStatus status) throws SQLException {
        return convoyPoolDao.getConvoysByStationAndStatus(idStation, status);
    }

    // StaffPool
    public StaffPool findStaffPoolById(int idStaff) throws SQLException {
        return staffPoolDao.findById(idStaff);
    }

    public List<StaffPool> findStaffPoolByStation(int idStation) throws SQLException {
        return staffPoolDao.findByStation(idStation);
    }

    public void updateStaffPool(StaffPool staffPool) throws SQLException {
        staffPoolDao.update(staffPool);
    }

    public List<StaffPool> findStaffPoolByStatus(StaffPoolDao.ShiftStatus status) throws SQLException {
        return staffPoolDao.findByStatus(status);
    }

    public List<StaffPool> findStaffPoolByStatusAndStation(StaffPoolDao.ShiftStatus status, int idStation) throws SQLException {
        return staffPoolDao.findByStatusAndStation(status, idStation);
    }

    // LineStation
    public LineStation findLineStationById(int idLine, int idStation) throws SQLException {
        return lineStationDao.findById(idLine, idStation);
    }

    public List<LineStation> findLineStationsByLine(int idLine) throws SQLException {
        return lineStationDao.findByLine(idLine);
    }

    // CarriageDepot
    public CarriageDepot getCarriageDepot(int idDepot, int idCarriage) throws SQLException {
        return carriageDepotDao.getCarriageDepot(idDepot, idCarriage);
    }
    public List<CarriageDepot> getCarriagesByDepot(int idDepot) throws SQLException {
        return carriageDepotDao.getCarriagesByDepot(idDepot);
    }

    // Depot
    public Depot getDepot(int idDepot) throws SQLException {
        return depotDao.getDepot(idDepot);
    }
    public List<Depot> getAllDepots() throws SQLException {
        return depotDao.getAllDepots();
    }
    public void insertDepot(int idDepot) throws SQLException {
        depotDao.insertDepot(idDepot);
    }
    public void deleteDepot(int idDepot) throws SQLException {
        depotDao.deleteDepot(idDepot);
    }

    // Run
    public Run selectRunByLineAndConvoy(int idLine, int idConvoy) throws SQLException {
        return runDao.selectRunByLineAndConvoy(idLine, idConvoy);
    }
    public Run selectRun(int idLine, int idConvoy, int idStaff) throws SQLException {
        return runDao.selectRun(idLine, idConvoy, idStaff);
    }
    public Run selectRunByStaffAndConvoy(int idStaff, int idConvoy) throws SQLException {
        return runDao.selectRunByStaffAndConvoy(idStaff, idConvoy);
    }
    public Run selectRunByStaffAndLine(int idStaff, int idLine) throws SQLException {
        return runDao.selectRunByStaffAndLine(idStaff, idLine);
    }
    public List<Run> selectAllRuns() throws SQLException {
        return runDao.selectAllRuns();
    }
    public boolean removeRun(int idLine, int idConvoy) throws SQLException {
        return runDao.removeRun(idLine, idConvoy);
    }
    public Run createRun(int idLine, int idConvoy, int idStaff, java.sql.Time timeDeparture, java.sql.Time timeArrival, int idFirstStation, int idLastStation) throws SQLException {
        return runDao.createRun(idLine, idConvoy, idStaff, timeDeparture, timeArrival, idFirstStation, idLastStation);
    }
    public boolean updateRun(int idLine, int idConvoy, int idStaff, java.sql.Time timeDeparture, java.sql.Time timeArrival, int idFirstStation, int idLastStation) throws SQLException {
        return runDao.updateRun(idLine, idConvoy, idStaff, timeDeparture, timeArrival, idFirstStation, idLastStation);
    }
    public List<Run> selectRunsByStaff(int idStaff) throws SQLException {
        return runDao.selectRunsByStaff(idStaff);
    }
    public List<Run> selectRunsByLine(int idLine) throws SQLException {
        return runDao.selectRunsByLine(idLine);
    }
    public List<Run> selectRunsByConvoy(int idConvoy) throws SQLException {
        return runDao.selectRunsByConvoy(idConvoy);
    }
    public List<Run> selectRunsByFirstStation(int idFirstStation) throws SQLException {
        return runDao.selectRunsByFirstStation(idFirstStation);
    }
    public List<Run> selectRunsByLastStation(int idLastStation) throws SQLException {
        return runDao.selectRunsByLastStation(idLastStation);
    }
    public List<Run> selectRunsByFirstStationAndDeparture(int idFirstStation, java.sql.Time timeDeparture) throws SQLException {
        return runDao.selectRunsByFirstStationAndDeparture(idFirstStation, timeDeparture);
    }

}
