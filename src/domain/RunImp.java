package domain;

import java.sql.Time;

public class RunImp implements Run{
    private final int idLine;
    private final int idConvoy;
    private final int idStaff;
    private final Time timeDeparture;
    private final Time timeArrival;
    private final int idFirstStation;
    private final int idLastStation;

    RunImp(int idLine, int idConvoy, int idStaff, Time timeDeparture, Time timeArrival, int idFirstStation, int idLastStation) {
        this.idLine = idLine;
        this.idConvoy = idConvoy;
        this.idStaff = idStaff;
        this.timeDeparture = timeDeparture;
        this.timeArrival = timeArrival;
        this.idFirstStation = idFirstStation;
        this.idLastStation = idLastStation;
    }

    @Override
    public int getIdLine() {
        return idLine;
    }

    @Override
    public int getIdConvoy() {
        return idConvoy;
    }

    @Override
    public int getIdStaff() {
        return idStaff;
    }

    @Override
    public Time getTimeDeparture() {
        return timeDeparture;
    }

    @Override
    public Time getTimeArrival() {
        return timeArrival;
    }

    @Override
    public int getIdFirstStation() {
        return idFirstStation;
    }

    @Override
    public int getIdLastStation() {
        return idLastStation;
    }

}
