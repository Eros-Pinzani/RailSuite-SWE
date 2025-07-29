package domain;

/**
 * Implementation of the Carriage interface.
 * Stores and manages the state of a carriage.
 */
class CarriageImp implements Carriage {
    private final int id;
    private final String model;
    private final String modelType;
    private final int yearProduced;
    private final int capacity;
    private Integer idConvoy;

    /**
     * Constructs a CarriageImp with all properties.
     * @param id the carriage id
     * @param model the model name
     * @param modelType the type of the model
     * @param yearProduced the year produced
     * @param capacity the carriage capacity
     * @param idConvoy the convoy id (nullable)
     */
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
