package businessLogic.service;

import businessLogic.RailSuiteFacade;
import dao.ConvoyPoolDao;
import dao.ConvoyDao;
import dao.CarriageDao;
import dao.CarriageDepotDao;
import domain.Convoy;
import domain.Carriage;
import domain.ConvoyPool;
import domain.ConvoyTableDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConvoyService {
    private final RailSuiteFacade facade = new RailSuiteFacade();

    public List<Convoy> getAllConvoys() {
        try {
            return facade.selectAllConvoys();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createConvoy(List<Carriage> carriages) {
        try {
            ConvoyDao convoyDao = ConvoyDao.of();
            CarriageDao carriageDao = CarriageDao.of();
            CarriageDepotDao depotDao = CarriageDepotDao.of();
            Convoy newConvoy = convoyDao.createConvoy(carriages);
            int newConvoyId = newConvoy.getId();
            // Trova la stazione associata alle vetture selezionate (tutte dello stesso deposito)
            // Recupera id_depot (stazione/deposito) dalla prima carriage selezionata tramite carriage_depot
            Integer idStation = null;
            if (!carriages.isEmpty()) {
                domain.CarriageDepot depot = depotDao.findActiveDepotByCarriage(carriages.get(0).getId());
                if (depot != null) {
                    idStation = depot.getIdDepot();
                }
            }
            // Inserisci in convoy_pool
            if (idStation != null) {
                ConvoyPoolDao convoyPoolDao = ConvoyPoolDao.of();
                ConvoyPool pool = ConvoyPool.of(newConvoyId, idStation, ConvoyPool.ConvoyStatus.WAITING);
                convoyPoolDao.insertConvoyPool(pool);
            }
            for (Carriage carriage : carriages) {
                carriage.setIdConvoy(newConvoyId);
                carriageDao.updateCarriageConvoy(carriage.getId(), newConvoyId);
                // Rimuovi la relazione dal deposito SOLO se la vettura non è in CLEANING o MAINTENANCE
                // Se la vettura è in depot con stato CLEANING o MAINTENANCE, NON cancellare la riga
                // ma aggiorna solo id_convoy della carriage
                // (così la manutenzione/cleaning resta tracciata)
                // Quindi: elimina solo se la vettura è in depot con stato AVAILABLE
                depotDao.deleteCarriageDepotByCarriageIfAvailable(carriage.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ConvoyTableDTO> getConvoyTableByStation(int stationId) {
        try {
            dao.ConvoyPoolDao convoyPoolDao = dao.ConvoyPoolDao.of();
            return convoyPoolDao.getConvoyTableDataByStation(stationId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Restituisce tutte le vetture disponibili per la creazione convoglio (in depot, AVAILABLE, senza id_convoy)
     * per una stazione e tipo specifici, aggiornando lo stato in una sola query.
     */
    public List<Carriage> getAvailableDepotCarriages(int idStation, String modelType) {
        try {
            CarriageDepotDao depotDao = CarriageDepotDao.of();
            return depotDao.findAvailableCarriagesForConvoy(idStation, modelType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Restituisce tutti i model_type delle vetture disponibili (in depot, AVAILABLE, senza id_convoy)
     * per una stazione, in UNA sola query.
     */
    public List<String> getAvailableDepotCarriageTypes(int idStation) {
        try {
            dao.CarriageDepotDao depotDao = dao.CarriageDepotDao.of();
            return depotDao.findAvailableCarriageTypesForConvoy(idStation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteConvoy(int idConvoy, int idStation) {
        try {
            dao.ConvoyDao convoyDao = dao.ConvoyDao.of();
            dao.CarriageDao carriageDao = dao.CarriageDao.of();
            dao.CarriageDepotDao depotDao = dao.CarriageDepotDao.of();
            // Recupera tutte le vetture associate al convoglio
            List<Carriage> carriages = carriageDao.selectCarriagesByConvoyId(idConvoy);
            java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
            for (Carriage carriage : carriages) {
                // Aggiorna la reference del convoglio
                carriage.setIdConvoy(null);
                carriageDao.updateCarriageConvoy(carriage.getId(), null);
                // Inserisci la vettura nel deposito della stazione con stato AVAILABLE
                domain.CarriageDepot cd = domain.CarriageDepot.of(idStation, carriage.getId(), now, null, domain.CarriageDepot.StatusOfCarriage.AVAILABLE);
                depotDao.insertCarriageDepot(cd);
            }
            convoyDao.removeConvoy(idConvoy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Aggiorna lo stato delle vetture in deposito per la stazione specificata (observer pull)
     */
    // Metodo non più usato nell'interfaccia, lasciato per eventuale uso futuro o test
    public void updateDepotCarriageAvailability(int idStation) {
        try {
            dao.CarriageDepotDao depotDao = dao.CarriageDepotDao.of();
            // Aggiorna tutte le vetture in deposito della stazione (senza filtrare per tipo)
            depotDao.findAvailableCarriagesForConvoy(idStation, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Restituisce tutte le vetture associate a un convoglio, con info sullo stato in deposito (e fine manutenzione se presente)
     */
    public List<domain.CarriageDepotDTO> getCarriagesWithDepotStatusByConvoy(int idConvoy) {
        try {
            dao.CarriageDepotDao depotDao = dao.CarriageDepotDao.of();
            return depotDao.findCarriagesWithDepotStatusByConvoy(idConvoy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
