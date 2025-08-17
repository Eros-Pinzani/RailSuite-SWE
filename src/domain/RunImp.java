package domain;

import java.sql.Timestamp;

class RunImp implements Run {
    private final Integer idLine;
    private final String lineName;
    private final Integer idConvoy;
    private final Integer idStaff;
    private final String staffNameSurname;
    private final Integer idFirstStation;
    private final String firstStationName;
    private final Integer idLastStation;
    private final String lastStationName;
    private final Timestamp timeDeparture;
    private final Timestamp timeArrival;
    private final RunStatus status;

    RunImp(Integer idLine, String lineName, Integer idConvoy, Integer idStaff, String staffName, String staffSurname, Integer idFirstStation, String firstStationName, Integer idLastStation, String lastStationName, Timestamp timeDeparture, Timestamp timeArrival) {
        this.idLine = idLine;
        this.lineName = lineName;
        this.idConvoy = idConvoy;
        this.idStaff = idStaff;
        this.staffNameSurname = staffName + " " + staffSurname;
        this.idFirstStation = idFirstStation;
        this.firstStationName = firstStationName;
        this.idLastStation = idLastStation;
        this.lastStationName = lastStationName;
        this.timeDeparture = timeDeparture;
        this.timeArrival = timeArrival;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (timeDeparture != null && timeArrival != null) {
            if (now.before(timeDeparture)) {
                this.status = RunStatus.BEFORE_RUN;
            } else if (now.after(timeDeparture) && now.before(timeArrival)) {
                this.status = RunStatus.RUN;
            } else {
                this.status = RunStatus.AFTER_RUN;
            }
        } else {
            this.status = null;
        }
    }

    @Override
    public Integer getIdLine() {
        return idLine;
    }

    @Override
    public String getLineName() {
        return lineName;
    }

    @Override
    public Integer getIdConvoy() {
        return idConvoy;
    }

    @Override
    public Integer getIdStaff() {
        return idStaff;
    }

    @Override
    public String getStaffNameSurname() {
        return staffNameSurname;
    }

    @Override
    public Integer getIdFirstStation() {
        return idFirstStation;
    }

    @Override
    public String getFirstStationName() {
        return firstStationName;
    }

    @Override
    public Integer getIdLastStation() {
        return idLastStation;
    }

    @Override
    public String getLastStationName() {
        return lastStationName;
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
    public RunStatus getStatus() {
        return status;
    }
}
