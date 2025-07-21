package domain;

import java.util.List;

public interface Line {
    int getIdLine();
    String getName();
    LineStation getStationAt(int order);

    static Line of(int idLine, String name, List<LineStation> stations) {
        return new LineImp(idLine, name, stations);
    }
}
