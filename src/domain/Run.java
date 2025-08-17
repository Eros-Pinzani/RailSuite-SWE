package domain;

import java.sql.Timestamp;

public interface Run {
    static Run of(Integer idLine, String lineName, Integer idConvoy, Integer idStaff, String staffName, String staffSurname, Integer idFirstStation, String firstStationName, Timestamp timeDeparture) {
        return new RunImp(idLine, lineName, idConvoy, idStaff, staffName, staffSurname, idFirstStation, firstStationName, timeDeparture);
    }

    /**
     * @return the line id
     */
    Integer getIdLine();

    /**
     * @return the line name
     */
    String getLineName();

    /**
     * @return the convoy id
     */
    Integer getIdConvoy();

    /**
     * @return the staff id
     */
    Integer getIdStaff();

    /**
     * @return the staff name and surname
     */
    String getStaffNameSurname();

    /**
     * @return the first station id
     */
    Integer getIdFirstStation();

    /**
     * @return the first station name
     */
    String getFirstStationName();

    /**
     * @return the departure time
     */
    Timestamp getTimeDeparture();

}

