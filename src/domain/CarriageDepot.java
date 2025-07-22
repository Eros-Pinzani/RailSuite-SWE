package domain;

import java.sql.Timestamp;

public interface CarriageDepot {
    int getIdDepot();
    int getIdCarriage();
    Timestamp getTimeEntered();
    Timestamp getTimeExited();
    StatusOfCarriage getStatusOfCarriage();

    void setTimeEntered(Timestamp timeEntered);
    void setTimeExited(Timestamp timeExited);
    void setStatusOfCarriage(StatusOfCarriage statusOfCarriage);

    enum StatusOfCarriage {
        CLEANING, MAINTENANCE, AVAILABLE
    }

    static CarriageDepot of(int idDepot, int idCarriage, Timestamp timeEntered, Timestamp timeExited, StatusOfCarriage statusOfCarriage) {
        return new CarriageDepotImp(idDepot, idCarriage, timeEntered, timeExited, statusOfCarriage);
    }
}
