package dao;

import domain.Line;

import java.util.List;

public interface LineDao {
    /**
     * Factory method to create a LineRawDao instance.
     * @return a LineRawDao instance
     */
    static LineDao of() {
        return new LineDaoImp();
    }

    /**
     * Retrieves the raw data of all lines.
     *
     * @return an iterable collection of LineRaw objects
     */
    List<Line> getAllLines() throws Exception;

}
