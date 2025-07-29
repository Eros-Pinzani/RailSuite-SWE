package domain;

/**
 * Interface representing a Carriage entity.
 * Provides factory method and accessors for carriage properties.
 */
public interface Carriage {
    /**
     * Factory method to create a Carriage instance.
     * @param id the unique identifier of the carriage
     * @param model the model name
     * @param modelType the type of the model
     * @param yearProduced the year the carriage was produced
     * @param capacity the capacity of the carriage
     * @param idConvoy the convoy id the carriage is assigned to (nullable)
     * @return a Carriage instance
     */
    static Carriage of(int id, String model, String modelType, int yearProduced, int capacity, Integer idConvoy) {
        return new CarriageImp(id, model, modelType, yearProduced, capacity, idConvoy);
    }

    /** @return the unique identifier of the carriage */
    int getId();
    /** @return the model name */
    String getModel();
    /** @return the type of the model */
    String getModelType();
    /** @return the year the carriage was produced */
    int getYearProduced();
    /** @return the capacity of the carriage */
    int getCapacity();
    /** @return the convoy id the carriage is assigned to, or null if not assigned */
    Integer getIdConvoy();
    /** Sets the convoy id for the carriage */
    void setIdConvoy(Integer idConvoy);
}
