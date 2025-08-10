package businessLogic.service;


import dao.RunDao;
import domain.DTO.RunDTO;

public class RunDetailsService {
    private final RunDao runDao = RunDao.of();
    private RunDTO run;
    // other attributes

    public RunDetailsService () {}

    RunDTO selectRun(int idLine, int idConvoy, int idStaff) {
        try {
            return run = runDao.selectRunDTOdetails(idLine, idConvoy, idStaff);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}


