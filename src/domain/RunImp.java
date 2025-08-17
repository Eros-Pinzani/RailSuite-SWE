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
    private final Timestamp timeDeparture;

    RunImp(Integer idLine, String lineName, Integer idConvoy, Integer idStaff, String staffName, String staffSurname, Integer idFirstStation, String firstStationName, Timestamp timeDeparture) {
        this.idLine = idLine;
        this.lineName = lineName;
        this.idConvoy = idConvoy;
        this.idStaff = idStaff;
        this.staffNameSurname = staffName + " " + staffSurname;
        this.idFirstStation = idFirstStation;
        this.firstStationName = firstStationName;
        this.timeDeparture = timeDeparture;
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
    public Timestamp getTimeDeparture() {
        return timeDeparture;
    }
}

