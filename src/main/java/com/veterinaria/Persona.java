package com.veterinaria;

import java.io.Serializable; // Para JavaBeans

public abstract class Persona implements RegistrableEntidad, Serializable {
    protected String documento;
    protected String nombre;
    protected String telefono;

    public Persona(String documento, String nombre, String telefono) {
        this.documento = documento;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    // Implementación de RegistrableEntidad
    @Override
    public String getIdentificador() {
        return documento;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    // Getters y Setters para JavaBeans
    public String getDocumento() { // Necesario para que PropietarioDAO y VeterinarioDAO funcionen
        return documento;
    }
    // No hay setter para documento si es la PK

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setNombre(String nombre) { // Setter para nombre
        this.nombre = nombre;
    }


    // Metodo abstracto que las clases hijas deben implementar
    public abstract String getTipoPersona();

    @Override
    public String mostrarInfo() {
        return "Nombre: " + nombre + "\nDocumento: " + documento + "\nTeléfono: " + telefono;
    }

    @Override
    public String toString() {
        return nombre; // Para JComboBoxes
    }
}
