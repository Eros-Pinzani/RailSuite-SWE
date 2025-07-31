package dao;

import domain.RunRaw;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for RunRaw entities.
 * Defines methods for retrieving RunRaw records from the database.
 */
public interface RunRawDao {
    /**
     * Factory method to create a RunRawDao instance.
     * @return a new RunRawDao instance
     */
    static RunRawDao of() {
        return new RunRawDaoImp();
    }

    /**
     * Retrieves all RunRaw records from the database.
     * @return a list of RunRaw objects
     * @throws SQLException if a database access error occurs
     */
    List<RunRaw> selectAllRunRaws() throws SQLException;
}
