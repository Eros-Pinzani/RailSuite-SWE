package domain;

import java.util.List;

class ConvoyImp implements Convoy {
    private final int idConvoy;
    private final List<Carriage> carriages;

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
