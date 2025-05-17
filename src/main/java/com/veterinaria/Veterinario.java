package com.veterinaria;

import java.io.Serializable;

public class Veterinario extends Persona implements Serializable {
    private String especialidad;
    private boolean disponibilidad;

    public Veterinario() {
        super("", "", "");
        this.disponibilidad = true;
    }

    public Veterinario(String documento, String nombre, String especialidad, String telefono, boolean disponibilidad) {
        super(documento, nombre, telefono);
        this.especialidad = especialidad;
        this.disponibilidad = disponibilidad;
    }

    // Getters y Setters
    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    @Override
    public String getTipoPersona() { // Cambiado de getTipo a getTipoPersona para consistencia
        return "Veterinario";
    }

    @Override
    public String mostrarInfo() {
        return super.mostrarInfo() + "\nEspecialidad: " + especialidad + "\nDisponibilidad: " + (disponibilidad ? "Sí" : "No") + "\nTipo: " + getTipoPersona();
    }
}