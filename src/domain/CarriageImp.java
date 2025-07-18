package domain;

class CarriageImp implements Carriage {
    private final int id;
    private final String model;
    private final String modelType;
    private final int yearProduced;
    private final int capacity;

    CarriageImp(int id, String model, String modelType, int yearProduced, int capacity) {
        this.id = id;
        this.model = model;
        this.modelType = modelType;
        this.yearProduced = yearProduced;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getModelType() {
        return modelType;
    }

    public int getYearProduced() {
        return yearProduced;
    }

    public int getCapacity() {
        return capacity;
    }
}
