package domain;

public interface Staff {
    int getIdStaff();
    String getName();
    String getSurname();
    String getAddress();
    String getEmail();
    String getPassword();
    TypeOfStaff getTypeOfStaff();

    enum TypeOfStaff {
        OPERATOR,
        SUPERVISOR
    }

    static Staff of(int idStaff, String name, String surname, String address, String email, String password, TypeOfStaff typeOfStaff) {
        return new StaffImp(idStaff, name, surname, address, email, password, typeOfStaff);
    }
}
