package domain.DTO;

import java.sql.Timestamp;

public class RunDTO {
    private final int idLine;
    private final String lineName;
    private final int idConvoy;
    private final int idStaff;
    private final String staffName;
    private final String staffSurname;
    private final String staffEmail;
    private final Timestamp timeDeparture;
    private final String firstStationName;

    public RunDTO(int idLine, String lineName, int idConvoy, int idStaff, String staffName, String staffSurname,
                  String staffEmail, Timestamp timeDeparture, String firstStationName) {
        this.idLine = idLine;
        this.lineName = lineName;
        this.idConvoy = idConvoy;
        this.idStaff = idStaff;
        this.staffName = staffName;
        this.staffSurname = staffSurname;
        this.staffEmail = staffEmail;
        this.timeDeparture = timeDeparture;
        this.firstStationName = firstStationName;
    }

    public int getIdLine() {
        return idLine;
    }

    public String getLineName() {
        return lineName;
    }

    public int getIdConvoy() {
        return idConvoy;
    }

    public int getIdStaff() {
        return idStaff;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getStaffSurname() {
        return staffSurname;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public Timestamp getTimeDeparture() {
        return timeDeparture;
    }

    public String getFirstStationName() {
        return firstStationName;
    }
}
