package domain;

import java.util.List;

class LineImp implements Line {
    private final int idLine;
    private final String name;
    private final List<LineStation> stations;

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
