public class Propietario {
    private String documento;
    private String nombre;
    private String telefono;
    private String direccion;
    private String correo;

    public Propietario(String documento, String nombre, String telefono, String direccion, String correo) {
        this.documento = documento;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.correo = correo;
    }

    public String getDocumento() { return documento; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public String getCorreo() { return correo; }

    @Override
    public String toString() {
        return nombre; // MOSTRAR SOLO EL NOMBRE EN EL COMBOBOX
    }
}
