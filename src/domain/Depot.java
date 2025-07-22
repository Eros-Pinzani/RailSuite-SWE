package domain;

import java.util.List;

public interface Depot {
    int getIdDepot();
    List<CarriageDepot> getCarriages();

    void addCarriage(CarriageDepot carriageDepot);
    boolean removeCarriage(int idCarriage);

    static Depot of(int idDepot) {
        return new DepotImp(idDepot);
    }
}
