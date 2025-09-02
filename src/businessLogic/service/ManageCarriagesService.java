package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Carriage;
import domain.DTO.CarriageDepotDTO;
import java.util.List;

/**
 * Service for managing carriages in a convoy.
 * Provides business logic for adding, removing, and retrieving carriages with depot status.
 */
public class ManageCarriagesService {
    private final RailSuiteFacade facade = new RailSuiteFacade();

    /**
     * Returns all carriages in a convoy, including their depot status.
     * Used to display the status of each carriage in a convoy.
     * @param idConvoy The convoy ID.
     * @return List of CarriageDepotDTO objects for the convoy.
     */
    public List<CarriageDepotDTO> getCarriagesWithDepotStatusByConvoy(int idConvoy) {
        try {
            return facade.findCarriagesWithDepotStatusByConvoy(idConvoy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns available carriages in the depot for a given station and model type.
     * Used to select carriages for convoy management.
     * @param idStation The station ID.
     * @param modelType The type of carriage model (can be null for all types).
     * @return List of available Carriage objects.
     */
    public List<Carriage> getAvailableDepotCarriages(int idStation, String modelType) {
        try {
            return facade.findAvailableCarriagesForConvoy(idStation, modelType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a carriage to a convoy and updates its depot status.
     * Used to assign a carriage to a convoy from the UI.
     * @param idCarriage The ID of the carriage.
     * @param idConvoy The ID of the convoy.
     */
    public void addCarriageToConvoy(int idCarriage, int idConvoy) {
        try {
            facade.updateCarriageConvoy(idCarriage, idConvoy);
            facade.deleteCarriageDepotByCarriageIfAvailable(idCarriage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a carriage from a convoy and updates its depot status.
     * Used to remove a carriage from a convoy and return it to the depot.
     * @param idCarriage The ID of the carriage.
     * @param idConvoy The ID of the convoy.
     */
    public void removeCarriageFromConvoy(int idCarriage, int idConvoy) {
        try {
            domain.CarriageDepot depot = facade.findActiveDepotByCarriage(idCarriage);
            if (depot != null && depot.getIdCarriage() == idCarriage) {
                facade.updateCarriageConvoy(idCarriage, null);
            } else {
                domain.ConvoyPool pool = facade.getConvoyPoolById(idConvoy);
                if (pool != null) {
                    int idDepot = pool.getIdStation();
                    facade.updateCarriageConvoy(idCarriage, null);
                    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                    domain.CarriageDepot cd = domain.CarriageDepot.of(idDepot, idCarriage, now, null, domain.CarriageDepot.StatusOfCarriage.AVAILABLE);
                    facade.insertCarriageDepot(cd);
                } else {
                    facade.updateCarriageConvoy(idCarriage, null);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns available carriage types in the depot for a given station.
     * Used to filter or select carriage types in the UI.
     * @param idStation The station ID.
     * @return List of available carriage type names.
     */
    public List<String> getAvailableDepotCarriageTypes(int idStation) {
        try {
            return facade.findAvailableCarriageTypesForConvoy(idStation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns available carriage models in the depot for a given station and type.
     * Used to filter or select carriage models in the UI.
     * @param idStation The station ID.
     * @param modelType The type of carriage model (can be null for all types).
     * @return List of available carriage model names.
     */
    public List<String> getAvailableDepotCarriageModels(int idStation, String modelType) {
        try {
            return facade.findAvailableCarriageModelsForConvoy(idStation, modelType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Restituisce una carrozza dato il suo id.
     */
    public Carriage getCarriageById(int idCarriage) {
        try {
            return facade.selectCarriage(idCarriage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
