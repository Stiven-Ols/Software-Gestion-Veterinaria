import java.util.Date;

public class Cita {
    private String id, docPropietario, idMascota, docVeterinario, motivo, estado;
    private Date fechaHora;

    public Cita(String id, String docPropietario, String idMascota, String docVeterinario, Date fechaHora, String motivo, String estado) {
        this.id = id;
        this.docPropietario = docPropietario;
        this.idMascota = idMascota;
        this.docVeterinario = docVeterinario;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = estado;
    }

    public String getId() { return id; }
    public String getDocPropietario() { return docPropietario; }
    public String getIdMascota() { return idMascota; }
    public String getDocVeterinario() { return docVeterinario; }
    public Date getFechaHora() { return fechaHora; }
    public String getMotivo() { return motivo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
