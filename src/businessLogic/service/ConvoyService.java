package businessLogic.service;

/**
 * Service for managing convoys.
 * Provides business logic for creating, updating, deleting, and retrieving convoys and their carriages.
 */
import businessLogic.RailSuiteFacade;
import dao.ConvoyPoolDao;
import dao.ConvoyDao;
import dao.CarriageDao;
import dao.CarriageDepotDao;
import domain.Convoy;
import domain.Carriage;
import domain.ConvoyPool;
import domain.DTO.ConvoyTableDTO;
import domain.DTO.CarriageDepotDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ConvoyService {
    private final RailSuiteFacade facade = new RailSuiteFacade();
    private static final Logger logger = Logger.getLogger(ConvoyService.class.getName());

    /**
     * Returns a list of all convoys in the system.
     * Used to display or manage all convoys from the controller.
     * @return List of all Convoy objects.
     */
    public List<Convoy> getAllConvoys() {
        try {
            return facade.selectAllConvoys();
        } catch (SQLException e) {
            logger.severe("Error getting all convoys: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new convoy with the given carriages, updates their status, and manages depot and pool information.
     * Called when a new convoy is created from the UI.
     * @param carriages List of carriages to include in the new convoy.
     */
    public void createConvoy(List<Carriage> carriages) {
        try {
            ConvoyDao convoyDao = ConvoyDao.of();
            CarriageDao carriageDao = CarriageDao.of();
            CarriageDepotDao depotDao = CarriageDepotDao.of();
            Convoy newConvoy = convoyDao.createConvoy(carriages);
            int newConvoyId = newConvoy.getId();
            Integer idStation = null;
            if (!carriages.isEmpty()) {
                domain.CarriageDepot depot = depotDao.findActiveDepotByCarriage(carriages.getFirst().getId());
                if (depot != null) {
                    idStation = depot.getIdDepot();
                }
            }
            if (idStation != null) {
                ConvoyPoolDao convoyPoolDao = ConvoyPoolDao.of();
                ConvoyPool pool = ConvoyPool.of(newConvoyId, idStation, ConvoyPool.ConvoyStatus.WAITING);
                convoyPoolDao.insertConvoyPool(pool);
            }
            for (Carriage carriage : carriages) {
                carriage.setIdConvoy(newConvoyId);
                carriageDao.updateCarriageConvoy(carriage.getId(), newConvoyId);
                depotDao.deleteCarriageDepotByCarriageIfAvailable(carriage.getId());
            }
        } catch (Exception e) {
            logger.severe("Error creating convoy: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a list of convoys for a specific station, formatted for table display.
     * Used to show convoys at a station in a table view.
     * @param stationId The ID of the station.
     * @return List of ConvoyTableDTO objects for the station.
     */
    public List<ConvoyTableDTO> getConvoyTableByStation(int stationId) {
        try {
            dao.ConvoyPoolDao convoyPoolDao = dao.ConvoyPoolDao.of();
            return convoyPoolDao.getConvoyTableDataByStation(stationId);
        } catch (Exception e) {
            logger.severe("Error getting convoy table by station: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns available carriages in the depot for a given station and model type.
     * Used to select carriages for convoy creation or management.
     * @param idStation The station ID.
     * @param modelType The type of carriage model (can be null for all types).
     * @return List of available Carriage objects.
     */
    public List<Carriage> getAvailableDepotCarriages(int idStation, String modelType) {
        try {
            CarriageDepotDao depotDao = CarriageDepotDao.of();
            return depotDao.findAvailableCarriagesForConvoy(idStation, modelType);
        } catch (Exception e) {
            logger.severe("Error getting available depot carriages: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the available carriage types in the depot for a given station.
     * Used to filter or select carriage types in the UI.
     * @param idStation The station ID.
     * @return List of available carriage type names.
     */
    public List<String> getAvailableDepotCarriageTypes(int idStation) {
        try {
            dao.CarriageDepotDao depotDao = dao.CarriageDepotDao.of();
            return depotDao.findAvailableCarriageTypesForConvoy(idStation);
        } catch (Exception e) {
            logger.severe("Error getting available depot carriage types: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a convoy and returns its carriages to the depot, updating their status.
     * Used when a convoy is removed from the system.
     * @param idConvoy The ID of the convoy to delete.
     * @param idStation The station ID for the depot.
     */
    public void deleteConvoy(int idConvoy, int idStation) {
        try {
            dao.ConvoyDao convoyDao = dao.ConvoyDao.of();
            dao.CarriageDao carriageDao = dao.CarriageDao.of();
            dao.CarriageDepotDao depotDao = dao.CarriageDepotDao.of();
            List<Carriage> carriages = carriageDao.selectCarriagesByConvoyId(idConvoy);
            java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
            for (Carriage carriage : carriages) {
                carriage.setIdConvoy(null);
                carriageDao.updateCarriageConvoy(carriage.getId(), null);
                domain.CarriageDepot cd = domain.CarriageDepot.of(idStation, carriage.getId(), now, null, domain.CarriageDepot.StatusOfCarriage.AVAILABLE);
                depotDao.insertCarriageDepot(cd);
            }
            convoyDao.removeConvoy(idConvoy);
        } catch (Exception e) {
            logger.severe("Error deleting convoy: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the availability of depot carriages for a given station.
     * Used to refresh the list of available carriages in the UI.
     * @param idStation The station ID.
     */
    public void updateDepotCarriageAvailability(int idStation) {
        try {
            dao.CarriageDepotDao depotDao = dao.CarriageDepotDao.of();
            depotDao.findAvailableCarriagesForConvoy(idStation, null);
        } catch (Exception e) {
            logger.severe("Error updating depot carriage availability: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns all carriages in a convoy, including their depot status.
     * @param idConvoy The convoy ID.
     * Used to display the status of each carriage in a convoy.
     * @return List of CarriageDepotDTO objects for the convoy.
     */
    public List<CarriageDepotDTO> getCarriagesWithDepotStatusByConvoy(int idConvoy) {
        try {
            dao.CarriageDepotDao depotDao = dao.CarriageDepotDao.of();
            return depotDao.findCarriagesWithDepotStatusByConvoy(idConvoy);
        } catch (Exception e) {
            logger.severe("Error getting carriages with depot status by convoy: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
