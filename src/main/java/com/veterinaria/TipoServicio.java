package com.veterinaria;

public enum TipoServicio {
    CONSULTA("Consulta General", 60000),
    VACUNACION("Vacunación", 40000),
    CIRUGIA("Cirugía", 200000),
    URGENCIA("Urgencia Médica", 100000);

    private final String descripcion;
    private int precio; // Hacemos el precio mutable para que el admin lo cambie

    TipoServicio(String descripcion, int precioInicial) {
        this.descripcion = descripcion;
        this.precio = precioInicial;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int nuevoPrecio) { // Setter para cambiar el precio
        this.precio = nuevoPrecio;
    }

    public static TipoServicio fromString(String text) {
        for (TipoServicio b : TipoServicio.values()) {
            if (b.descripcion.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        // Podrías retornar CONSULTA por defecto o lanzar una excepción si es crítico no encontrarlo
        return CONSULTA; // Opcional: un valor por defecto
    }

    @Override
    public String toString() {
        return descripcion; // Para mostrar en JComboBox
    }
}
