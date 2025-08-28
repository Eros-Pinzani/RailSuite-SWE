package domain;

import java.sql.Timestamp;

class NotificationImp implements Notification {
    private final int idCarriage;
    private final String typeOfCarriage;
    private final int idConvoy;
    private final String typeOfNotification;
    private final Timestamp dateTimeOfNotification;
    private final int idStaff;
    private final String staffName;
    private final String staffSurname;

    public NotificationImp(int idCarriage, String typeOfCarriage, int idConvoy, String typeOfNotification, Timestamp dateTimeOfNotification, int idStaff, String staffName, String staffSurname) {
        this.idCarriage = idCarriage;
        this.typeOfCarriage = typeOfCarriage;
        this.idConvoy = idConvoy;
        this.typeOfNotification = typeOfNotification;
        this.dateTimeOfNotification = dateTimeOfNotification;
        this.idStaff = idStaff;
        this.staffName = staffName;
        this.staffSurname = staffSurname;
    }

    @Override
    public int getIdCarriage() {
        return idCarriage;
    }

    @Override
    public String getTypeOfCarriage() {
        return typeOfCarriage;
    }

    @Override
    public int getIdConvoy() {
        return idConvoy;
    }

    @Override
    public String getTypeOfNotification() {
        return typeOfNotification;
    }

    @Override
    public Timestamp getDateTimeOfNotification() {
        return dateTimeOfNotification;
    }

    @Override
    public int getIdStaff() {
        return idStaff;
    }

    @Override
    public String getStaffName() {
        return staffName;
    }

    @Override
    public String getStaffSurname() {
        return staffSurname;
    }
}

