// TipoServicio.java
public enum TipoServicio {
    VACUNACION("Vacunación"),
    CONSULTA("Consulta"),
    CIRUGIA("Cirugía"),
    URGENCIA("Urgencia");

    private final String descripcion;

    TipoServicio(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
