package domain;

import java.sql.Timestamp;

/**
 * Implementation of the Run interface.
 * Stores and manages the state of a run (train journey).
 */
public class RunImp implements Run {
    private final int idLine;
    private final String lineName;
    private final int idConvoy;
    private final int idStaff;
    private final String staffNameSurname;
    private final Timestamp timeDeparture;
    private final Timestamp timeArrival;
    private final int idFirstStation;
    private final String firstStationName;
    private final int idLastStation;
    private final String lastStationName;
    private final RunStatus status;

    /**
     * Constructs a RunImp with all properties.
     *
     * @param idLine         the line id
     * @param idConvoy       the convoy id
     * @param idStaff        the staff id
     * @param timeDeparture  the departure time
     * @param timeArrival    the arrival time
     * @param idFirstStation the first station id
     * @param idLastStation  the last station id
     */
    RunImp(int idLine, String lineName, int idConvoy, int idStaff, String staffName, String staffSurname, Timestamp timeDeparture,
           Timestamp timeArrival, int idFirstStation, String firstStationName, int idLastStation, String lastStationName) {
        this.idLine = idLine;
        this.lineName = lineName;
        this.idConvoy = idConvoy;
        this.idStaff = idStaff;
        this.staffNameSurname = staffName + " " + staffSurname;
        this.timeDeparture = timeDeparture;
        this.timeArrival = timeArrival;
        this.idFirstStation = idFirstStation;
        this.firstStationName = firstStationName;
        this.idLastStation = idLastStation;
        this.lastStationName = lastStationName;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (now.before(timeDeparture)) {
            this.status = RunStatus.BEFORE_RUN;
        } else if (now.after(timeDeparture) && now.before(timeArrival)) {
            this.status = RunStatus.RUN;
        } else {
            this.status = RunStatus.AFTER_RUN;
        }
    }

    @Override
    public int getIdLine() {
        return idLine;
    }

    @Override
    public String getLineName() {
        return lineName;
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
    public String getStaffNameSurname() {
        return staffNameSurname;
    }

    @Override
    public Timestamp getTimeDeparture() {
        return timeDeparture;
    }

    @Override
    public Timestamp getTimeArrival() {
        return timeArrival;
    }

    @Override
    public int getIdFirstStation() {
        return idFirstStation;
    }

    @Override
    public String getFirstStationName() {
        return firstStationName;
    }

    @Override
    public int getIdLastStation() {
        return idLastStation;
    }

    @Override
    public String getLastStationName() {
        return lastStationName;
    }

    @Override
    public RunStatus getStatus() {
        return status;
    }
}
