package com.veterinaria;

import java.io.Serializable;

public class Propietario extends Persona implements Serializable {
    private String direccion;
    private String correo;

    // Constructor para JavaBean (puede ser necesario para algunas librerías o frameworks)
    public Propietario() {
        super("", "", ""); // Llama al constructor de Persona con valores vacíos
    }

    public Propietario(String documento, String nombre, String telefono, String direccion, String correo) {
        super(documento, nombre, telefono);
        this.direccion = direccion;
        this.correo = correo;
    }

    // Getters y Setters
    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String getTipoPersona() {
        return "Propietario";
    }

    @Override
    public String mostrarInfo() {
        return super.mostrarInfo() + "\nDirección: " + direccion + "\nCorreo: " + correo + "\nTipo: " + getTipoPersona();
    }
}



//    public String getDocumento() {
//        return documento;
//    }


