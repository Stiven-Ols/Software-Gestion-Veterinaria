package com.veterinaria;

import java.sql.SQLException;

public enum TipoServicio {
    CONSULTA("Consulta General", 60000),
    VACUNACION("Vacunación", 40000),
    CIRUGIA("Cirugía", 200000),
    URGENCIA("Urgencia Médica", 100000);

    private final String descripcion;
    private final int precioPorDefecto;
    private int precioActual;

    TipoServicio(String descripcion, int precioInicial) {
        this.descripcion = descripcion;
        this.precioPorDefecto = precioInicial;
        this.precioActual = precioInicial;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPrecio() {
        return precioActual;
    }

    public int getPrecioPorDefecto() {
        return precioPorDefecto;
    }

    void setPrecioInterno(int nuevoPrecio) { // Llamado por Main al cargar desde BD
        this.precioActual = nuevoPrecio;
    }

    // Usado por AdminPanel para cambiar precio y persistirlo
    public void setPrecioConfigurado(int nuevoPrecio) throws SQLException {
        ConfiguracionPreciosDAO.guardarPrecio(this.name(), nuevoPrecio);
        this.precioActual = nuevoPrecio;
    }

    public static TipoServicio fromString(String text) {
        if (text == null) return CONSULTA;
        for (TipoServicio b : TipoServicio.values()) {
            if (b.descripcion.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return CONSULTA;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}