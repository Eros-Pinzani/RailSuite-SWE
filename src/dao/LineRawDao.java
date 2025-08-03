package dao;

import domain.LineRaw;

import java.util.List;

public interface LineRawDao {
    /**
     * Factory method to create a LineRawDao instance.
     * @return a LineRawDao instance
     */
    static LineRawDao of() {
        return new LineRawDaoImp();
    }

    /**
     * Retrieves the raw data of all lines.
     *
     * @return an iterable collection of LineRaw objects
     */
    List<LineRaw> getAllLines() throws Exception;

}
