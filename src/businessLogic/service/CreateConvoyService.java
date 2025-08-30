package businessLogic.service;

import businessLogic.RailSuiteFacade;
import domain.Carriage;
import java.util.List;

/**
 * Service for creating new convoys.
 * Provides business logic for selecting available carriages and creating convoys.
 */
public class CreateConvoyService {
    private final RailSuiteFacade facade = new RailSuiteFacade();

    /**
     * Returns available carriages in the depot for a given station and model type.
     * Used to select carriages for convoy creation.
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
     * Delegates the creation of a convoy to ConvoyService.
     * Used when a new convoy is created from the UI.
     * @param carriages List of carriages to include in the new convoy.
     */
    public void createConvoy(List<Carriage> carriages) {
        new ConvoyService().createConvoy(carriages);
    }

    /**
     * Delegates the retrieval of available carriage types to ConvoyService.
     * Used to filter or select carriage types in the UI.
     * @param idStation The station ID.
     * @return List of available carriage type names.
     */
    public List<String> getAvailableDepotCarriageTypes(int idStation) {
        return new ConvoyService().getAvailableDepotCarriageTypes(idStation);
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
}
