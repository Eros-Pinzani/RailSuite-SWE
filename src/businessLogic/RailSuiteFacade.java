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

    // Carriage
    public Carriage selectCarriage(int id) throws SQLException {
        return carriageDao.selectCarriage(id);
    }

    public List<Carriage> selectAllCarriages() throws SQLException {
        return carriageDao.selectAllCarriages();
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
    // aggiungi altri metodi di business semplificati
}
