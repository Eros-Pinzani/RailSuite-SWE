package dao;

import domain.Staff;
import java.sql.SQLException;
import java.util.List;

public interface StaffDao {
    static StaffDao of() {
        return new StaffDaoImp();
    }

    Staff findById(int id) throws SQLException;
    Staff findByEmail(String email) throws SQLException;
    List<Staff> findAll() throws SQLException;
    List<Staff> findByType(Staff.TypeOfStaff type) throws SQLException;
}
