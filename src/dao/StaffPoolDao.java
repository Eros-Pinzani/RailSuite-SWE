package dao;

import java.sql.SQLException;
import java.util.List;

import domain.StaffPool;

public interface StaffPoolDao {
    enum ShiftStatus {
        AVAILABLE,
        ON_RUN,
        RELAX
    }

    StaffPool findById(int idStaff) throws SQLException;
    List<StaffPool> findByStation(int idStation) throws SQLException;
    void update(StaffPool staffPool) throws SQLException;
    List<StaffPool> findByStatus(ShiftStatus status) throws SQLException;
    List<StaffPool> findByStatusAndStation(ShiftStatus status, int idStation) throws SQLException;

}
