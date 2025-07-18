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

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public String getModelType() {
        return modelType;
    }

    @Override
    public int getYearProduced() {
        return yearProduced;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
