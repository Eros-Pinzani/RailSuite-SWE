package businessLogic.service;

import dao.CarriageDepotDao;
import dao.CarriageDao;
import dao.ConvoyPoolDao;
import domain.Carriage;
import domain.CarriageDepotDTO;
import java.util.List;

public class ManageCarriagesService {
    public List<CarriageDepotDTO> getCarriagesWithDepotStatusByConvoy(int idConvoy) {
        try {
            CarriageDepotDao depotDao = CarriageDepotDao.of();
            return depotDao.findCarriagesWithDepotStatusByConvoy(idConvoy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Carriage> getAvailableDepotCarriages(int idStation, String modelType) {
        try {
            CarriageDepotDao depotDao = CarriageDepotDao.of();
            return depotDao.findAvailableCarriagesForConvoy(idStation, modelType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addCarriageToConvoy(int idCarriage, int idConvoy) {
        try {
            CarriageDao.of().updateCarriageConvoy(idCarriage, idConvoy);
            CarriageDepotDao.of().deleteCarriageDepotByCarriageIfAvailable(idCarriage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeCarriageFromConvoy(int idCarriage, int idConvoy) {
        try {
            CarriageDepotDao depotDao = CarriageDepotDao.of();
            CarriageDao carriageDao = CarriageDao.of();
            // Verifica se la carriage è già in depot (cioè esiste una riga carriage_depot con time_exited IS NULL)
            domain.CarriageDepot depot = depotDao.findActiveDepotByCarriage(idCarriage);
            if (depot != null && depot.getIdCarriage() == idCarriage) {
                // La carriage è già in depot: elimina solo la reference al convoglio
                carriageDao.updateCarriageConvoy(idCarriage, null);
            } else {
                // La carriage NON è in depot: aggiorna la reference e inserisci in depot del convoy
                // Recupera id_depot del convoy
                ConvoyPoolDao convoyPoolDao = ConvoyPoolDao.of();
                domain.ConvoyPool pool = convoyPoolDao.getConvoyPoolById(idConvoy);
                if (pool != null) {
                    int idDepot = pool.getIdStation();
                    carriageDao.updateCarriageConvoy(idCarriage, null);
                    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                    domain.CarriageDepot cd = domain.CarriageDepot.of(idDepot, idCarriage, now, null, domain.CarriageDepot.StatusOfCarriage.AVAILABLE);
                    depotDao.insertCarriageDepot(cd);
                } else {
                    // fallback: elimina solo la reference
                    carriageDao.updateCarriageConvoy(idCarriage, null);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAvailableDepotCarriageTypes(int idStation) {
        try {
            CarriageDepotDao depotDao = CarriageDepotDao.of();
            return depotDao.findAvailableCarriageTypesForConvoy(idStation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
