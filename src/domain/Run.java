package domain;

import java.sql.Time;

public interface Run {
    static Run of(int idLine, int idConvoy, int idStaff, java.sql.Time timeDeparture, Time timeArrival, int idFirstsStation, int idLastStation) {
        return new RunImp(idLine, idConvoy, idStaff, timeDeparture, timeArrival, idFirstsStation, idLastStation);
    }

    int getIdLine();
    int getIdConvoy();
    int getIdStaff();
    Time getTimeDeparture();
    Time getTimeArrival();
    int getIdFirstStation();
    int getIdLastStation();
}
