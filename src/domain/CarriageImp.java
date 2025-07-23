package domain;

class CarriageImp implements Carriage {
    private final int id;
    private final String model;
    private final String modelType;
    private final int yearProduced;
    private final int capacity;
    private Integer idConvoy;

    CarriageImp(int id, String model, String modelType, int yearProduced, int capacity, Integer idConvoy) {
        this.id = id;
        this.model = model;
        this.modelType = modelType;
        this.yearProduced = yearProduced;
        this.capacity = capacity;
        this.idConvoy = idConvoy;
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

    @Override
    public Integer getIdConvoy() {
        return idConvoy;
    }

    @Override
    public void setIdConvoy(Integer idConvoy) {
        this.idConvoy = idConvoy;
    }
}
