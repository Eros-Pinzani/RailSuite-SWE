package domain;

import java.util.List;

/**
 * Implementation of the Convoy interface.
 * Stores and manages a list of carriages belonging to a convoy.
 */
class ConvoyImp implements Convoy {
    private final int idConvoy;
    private final List<Carriage> carriages;

    /**
     * Constructs a ConvoyImp with the given id and carriages.
     * @param idConvoy the convoy id
     * @param carriages the list of carriages in the convoy
     */
    public ConvoyImp(int idConvoy, List<Carriage> carriages) {
        this.idConvoy = idConvoy;
        this.carriages = carriages;
    }

    @Override
    public int getId() {
        return idConvoy;
    }

    @Override
    public List<Carriage> getCarriages() {
        return carriages;
    }

    @Override
    public boolean removeCarriage(Carriage carriage) {
        if (carriages.contains(carriage)) {
            carriages.remove(carriage);
            return true;
        }
        return false;
    }

    @Override
    public boolean addCarriage(Carriage carriage) {
        if (!carriages.contains(carriage)) {
            carriages.add(carriage);
            return true;
        }
        return false;
    }

    @Override
    public int convoySize() {
        return carriages.size();
    }
}
