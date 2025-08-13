package businessLogic.service;

import dao.ConvoyDao;
import domain.Carriage;
import domain.Convoy;

import java.util.List;

public class ConvoyEditPopupService {
    ConvoyDao convoyDao = ConvoyDao.of();

    public void removeCarriageFromConvoy(Convoy convoy, Carriage selectedCarriage) throws Exception {
        if (convoy == null || selectedCarriage == null) {
            throw new IllegalArgumentException("Convoy and selected carriage must not be null.");
        }
        if (!convoy.getCarriages().contains(selectedCarriage)) {
            throw new IllegalArgumentException("Selected carriage is not part of the convoy.");
        }
        convoy.getCarriages().remove(selectedCarriage);
        try {
            convoyDao.removeCarriageFromConvoy(convoy.getId(), selectedCarriage);
        }
        catch (Exception e) {
            throw new Exception("Error removing carriage from convoy: " + e.getMessage(), e);
        }
    }

    public void addCarriagesToConvoy(Convoy convoy, List<Carriage> carriages) {
        if (convoy == null || carriages == null || carriages.isEmpty()) {
            throw new IllegalArgumentException("Convoy and carriages must not be null or empty.");
        }
        for (Carriage carriage : carriages) {
            if (convoy.getCarriages().contains(carriage)) {
                throw new IllegalArgumentException("Carriage " + carriage.getId() + " is already part of the convoy.");
            }
            convoy.getCarriages().add(carriage);
        }
        try {
            convoyDao.addCarriagesToConvoy(convoy.getId(), carriages);
        } catch (Exception e) {
            throw new RuntimeException("Error adding carriages to convoy: " + e.getMessage(), e);
        }
    }
}
