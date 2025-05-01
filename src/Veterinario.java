public class Veterinario {
    private String documento;
    private String nombre;
    private String especialidad;
    private String telefono;
    private boolean disponibilidad;

    public Veterinario(String documento, String nombre, String especialidad, String telefono, boolean disponibilidad) {
        this.documento = documento;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.telefono = telefono;
        this.disponibilidad = disponibilidad;
    }

    // Asegúrate de que Veterinario tenga este método
    @Override
    public String toString() {
        return this.nombre; // Así el combo mostrará el nombre del veterinario, no 'Veterinario@68a18b33'
    }

    // getters y setters...
    public String getDocumento() { return documento; }
    public String getNombre() { return nombre; }
    public String getEspecialidad() { return especialidad; }
    public String getTelefono() { return telefono; }
    public boolean isDisponibilidad() { return disponibilidad; }
}
