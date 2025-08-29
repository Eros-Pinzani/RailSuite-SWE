package domain;

public interface Notification {
    static Notification of(int idCarriage, String typeOfCarriage, int idConvoy, String typeOfNotification, java.sql.Timestamp dateTimeOfNotification, int idStaff, String staffName, String staffSurname, String status) {
        return new NotificationImp(idCarriage, typeOfCarriage, idConvoy,  typeOfNotification, dateTimeOfNotification, idStaff, staffName, staffSurname, status);
    }

    int getIdCarriage();
    String getTypeOfCarriage();
    int getIdConvoy();
    String getTypeOfNotification();
    java.sql.Timestamp getDateTimeOfNotification();
    int getIdStaff();
    String getStaffName();
    String getStaffSurname();
    String getStatus();
}
