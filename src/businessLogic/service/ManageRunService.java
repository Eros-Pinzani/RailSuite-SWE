package businessLogic.service;

import dao.RunDao;
import domain.Run;

import java.util.List;

public class ManageRunService {
    private final RunDao runDao = RunDao.of();

    /**
     * Returns the runs filtered according to the provided parameters.
     * If a parameter is null, it is not used as a filter.
     * If all are null, returns an empty list.
     */
    public List<Run> getFilteredRuns(Integer idLine, Integer idConvoy, Integer idStaff) {
        try {
            // Single and dynamic SQL query: to be implemented in RunDao/RunDaoImp
            return runDao.selectRunsFiltered(idLine, idConvoy, idStaff);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
