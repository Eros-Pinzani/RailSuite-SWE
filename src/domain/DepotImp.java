package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the Depot interface.
 * Stores and manages a list of carriages in a depot.
 */
class DepotImp implements Depot {
    private final int idDepot;
    private final List<CarriageDepot> carriages;

    /**
     * Constructs a DepotImp with the given id.
     * @param idDepot the depot id
     */
    DepotImp(int idDepot) {
        this.idDepot = idDepot;
        this.carriages = new ArrayList<>();
    }

    @Override
    public int getIdDepot() {
        return idDepot;
    }

    @Override
    public List<CarriageDepot> getCarriages() {
        return Collections.unmodifiableList(carriages);
    }

    @Override
    public void addCarriage(CarriageDepot carriageDepot) {
        carriages.add(carriageDepot);
    }

    @Override
    public boolean removeCarriage(int idCarriage) {
        return carriages.removeIf(c -> c.getIdCarriage() == idCarriage);
    }
}
