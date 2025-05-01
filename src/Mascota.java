public class Mascota {
    private String id;
    private String nombre, especie, raza, propietarioDocumento;
    private int edad;
    private static int contador = 1;

    public Mascota(String nombre, String especie, String raza, int edad, String propietarioDocumento) {
        this.id = "M" + (contador++);
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.propietarioDocumento = propietarioDocumento;
    }
    public Mascota(String id, String nombre, String especie, String raza, int edad, String propietarioDocumento) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.propietarioDocumento = propietarioDocumento;
    }
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEspecie() { return especie; }
    public String getRaza() { return raza; }
    public int getEdad() { return edad; }
    public String getPropietarioDocumento() { return propietarioDocumento; }
    public String toString() { return nombre; }
}
