package businessLogic.service;

import dao.CarriageDepotDao;
import domain.Carriage;
import java.util.List;

public class CreateConvoyService {
    public List<Carriage> getAvailableDepotCarriages(int idStation, String modelType) {
        try {
            CarriageDepotDao depotDao = CarriageDepotDao.of();
            return depotDao.findAvailableCarriagesForConvoy(idStation, modelType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createConvoy(List<Carriage> carriages) {
        new ConvoyService().createConvoy(carriages);
    }

    public List<String> getAvailableDepotCarriageTypes(int idStation) {
        return new ConvoyService().getAvailableDepotCarriageTypes(idStation);
    }

    public List<String> getAvailableDepotCarriageModels(int idStation, String modelType) {
        try {
            CarriageDepotDao depotDao = CarriageDepotDao.of();
            return depotDao.findAvailableCarriageModelsForConvoy(idStation, modelType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
