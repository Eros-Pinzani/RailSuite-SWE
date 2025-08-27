package domain;
/**
 * Interface representing a Line entity with raw data.
 * Provides factory method and accessors for line properties.
 */

public interface Line {
    /**
     * Factory method to create a LineRaw instance.
     * @param idLine the unique identifier of the line
     * @param lineName the name of the line
     * @param idFirstStation the unique identifier of the first station
     * @param firstStationLocation the name of the line in the main direction
     * @param idLastStation the unique identifier of the last station
     * @param lastStationLocation the name of the line in the return direction
     * @return a LineRaw instance
     */
    static Line of(int idLine, String lineName, int idFirstStation, String firstStationLocation, int idLastStation, String lastStationLocation) {
        return new LineImp(idLine, lineName,idFirstStation, firstStationLocation, idLastStation, lastStationLocation);
    }

    /** @return the unique identifier of the line */
    int getIdLine();
    /** @return the name of the line */
    String getLineName();
    /** @return the unique identifier of the first station */
    int getIdFirstStation();
    /** @return the name of the line in the main direction */
    String getFirstStationLocation();
    /** @return the unique identifier of the last station */
    int getIdLastStation();
    /** @return the name of the line in the return direction */
    String getLastStationLocation();
}
