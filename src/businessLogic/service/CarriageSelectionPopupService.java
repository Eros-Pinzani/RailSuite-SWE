package businessLogic.service;


import dao.CarriageDepotDao;
import domain.Carriage;
import domain.Convoy;

import java.util.List;


public class CarriageSelectionPopupService {
    List<Carriage> carriages;

    public CarriageSelectionPopupService(Convoy convoy) {
        try {
            this.carriages = CarriageDepotDao.of().getCarriagesByConvoyPosition(convoy.getId());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving carriages from station: " + e.getMessage(), e);
        }
    }

    public List<Carriage> getCarriagesFromStation() {
        if (carriages == null || carriages.isEmpty()) {
            throw new RuntimeException("No carriages available at the station.");
        }
        return carriages;
    }

}

