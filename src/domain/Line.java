package domain;

import java.util.List;

/**
 * Interface representing a Line entity.
 * Provides factory method and accessors for line properties and stations management.
 */
public interface Line {
    /** @return the unique identifier of the line */
    int getIdLine();
    /** @return the name of the line */
    String getName();
    /**
     * Returns the station at the given order in the line.
     * @param order the order of the station
     * @return the LineStation at the specified order
     */
    LineStation getStationAt(int order);

    /**
     * Factory method to create a Line instance.
     * @param idLine the line id
     * @param name the name of the line
     * @param stations the list of stations for the line
     * @return a Line instance
     */
    static Line of(int idLine, String name, List<LineStation> stations) {
        return new LineImp(idLine, name, stations);
    }
}
