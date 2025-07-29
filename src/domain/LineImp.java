package domain;

import java.util.List;

/**
 * Implementation of the Line interface.
 * Stores and manages a list of stations for a line.
 */
class LineImp implements Line {
    private final int idLine;
    private final String name;
    private final List<LineStation> stations;

    /**
     * Constructs a LineImp with the given id, name, and stations.
     * @param idLine the line id
     * @param name the name of the line
     * @param stations the list of stations for the line
     */
    public LineImp(int idLine, String name, List<LineStation> stations) {
        this.idLine = idLine;
        this.name = name;
        this.stations = stations;
    }

    @Override
    public int getIdLine() {
        return idLine;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LineStation getStationAt(int order) {
        return stations.get(order);
    }
}
