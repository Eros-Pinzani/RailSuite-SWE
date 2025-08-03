package domain.DTO;

public class StaffDTO {
    private final int idStaff;
    private final String staffNameSurname;

    public StaffDTO(int idStaff, String name, String surname) {
        this.idStaff = idStaff;
        this.staffNameSurname = name + " " + surname;
    }

    public int getIdStaff() {
        return idStaff;
    }

    public String getStaffNameSurname() {
        return staffNameSurname;
    }

}
