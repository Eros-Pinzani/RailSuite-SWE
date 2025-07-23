package domain;

public interface Carriage {
    static Carriage of(int id, String model, String modelType, int yearProduced, int capacity, Integer idConvoy) {
        return new CarriageImp(id, model, modelType, yearProduced, capacity, idConvoy);
    }

    int getId();
    String getModel();
    String getModelType();
    int getYearProduced();
    int getCapacity();
    Integer getIdConvoy();
    void setIdConvoy(Integer idConvoy);
}
