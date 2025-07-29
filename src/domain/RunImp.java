package domain;

import java.sql.Time;

/**
 * Implementation of the Run interface.
 * Stores and manages the state of a run (train journey).
 */
public class RunImp implements Run{
    private final int idLine;
    private final int idConvoy;
    private final int idStaff;
    private final Time timeDeparture;
    private final Time timeArrival;
    private final int idFirstStation;
    private final int idLastStation;

    /**
     * Constructs a RunImp with all properties.
     * @param idLine the line id
     * @param idConvoy the convoy id
     * @param idStaff the staff id
     * @param timeDeparture the departure time
     * @param timeArrival the arrival time
     * @param idFirstStation the first station id
     * @param idLastStation the last station id
     */
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
