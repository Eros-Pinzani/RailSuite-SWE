package dao;

import domain.Line;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for Line entities.
 * Defines methods for CRUD operations and queries on lines.
 */
public interface LineDao {
    /**
     * Factory method to create a LineDao instance.
     * @return a LineDao implementation
     */
    static LineDao of() {
        return new LineDaoImp();
    }

    /**
     * Retrieves a line by its unique identifier.
     * @param idLine the id of the line
     * @return the Line object, or null if not found
     * @throws SQLException if a database access error occurs
     */
    Line findById(int idLine) throws SQLException;

    /**
     * Retrieves all lines from the database.
     * @return a list of all Line objects
     * @throws SQLException if a database access error occurs
     */
    List<Line> findAll() throws SQLException;

    /**
     * Retrieves all lines that pass through a specific station.
     * @param idStation the id of the station
     * @return a list of Line objects
     * @throws SQLException if a database access error occurs
     */
    List<Line> findByStation(int idStation) throws SQLException;
}
