package domain;

class StaffImp implements Staff {
    private final int idStaff;
    private final String name;
    private final String surname;
    private final String address;
    private final String email;
    private final String password;
    private final TypeOfStaff typeOfStaff;

    StaffImp(int idStaff, String name, String surname, String address, String email, String password, TypeOfStaff typeOfStaff) {
        this.idStaff = idStaff;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.email = email;
        this.password = password;
        this.typeOfStaff = typeOfStaff;
    }

    @Override
    public int getIdStaff() { return idStaff; }
    @Override
    public String getName() { return name; }
    @Override
    public String getSurname() { return surname; }
    @Override
    public String getAddress() { return address; }
    @Override
    public String getEmail() { return email; }
    @Override
    public String getPassword() { return password; }
    @Override
    public TypeOfStaff getTypeOfStaff() { return typeOfStaff; }
}
