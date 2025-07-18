package domain;

public interface Carriage {
    static Carriage of(int id, String model, String modelType, int yearProduced, int capacity) {
        return new CarriageImp(id, model, modelType, yearProduced, capacity);
    }

    int getId();
    String getModel();
    String getModelType();
    int getYearProduced();
    int getCapacity();
}
