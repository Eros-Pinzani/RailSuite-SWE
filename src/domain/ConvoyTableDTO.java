package domain;

public class ConvoyTableDTO {
    private int idConvoy;
    private String type;
    private String status;
    private int carriageCount;

    public ConvoyTableDTO(int idConvoy, String type, String status, int carriageCount) {
        this.idConvoy = idConvoy;
        this.type = type;
        this.status = status;
        this.carriageCount = carriageCount;
    }

    public int getIdConvoy() { return idConvoy; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public int getCarriageCount() { return carriageCount; }

    public void setIdConvoy(int idConvoy) { this.idConvoy = idConvoy; }
    public void setType(String type) { this.type = type; }
    public void setStatus(String status) { this.status = status; }
    public void setCarriageCount(int carriageCount) { this.carriageCount = carriageCount; }
}

