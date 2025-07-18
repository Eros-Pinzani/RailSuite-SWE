package domain;

import java.util.List;

public interface Convoy {
    static Convoy of(int idConvoy, List<Carriage> carriages) {
        return new ConvoyImp(idConvoy, carriages);
    }
    int getId();
    List<Carriage> getCarriages();
    boolean removeCarriage(Carriage carriage);
    boolean addCarriage(Carriage carriage);
    int convoySize();
}
