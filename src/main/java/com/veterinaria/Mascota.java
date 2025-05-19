package com.veterinaria;

import java.io.Serializable;

public class Mascota implements RegistrableEntidad, Serializable {
    private String id;
    private String nombre;
    private String especie;
    private String raza;
    private int edad;
    private String propietarioDocumento;
    private static int contadorIdTemporal = 1; // Para generar IDs si no se provee uno (solo para nuevas mascotas)

    public Mascota() {} // Constructor sin argumentos

    // Constructor para cuando se crea una nueva mascota desde la UI
    public Mascota(String nombre, String especie, String raza, int edad, String propietarioDocumento) {
        // Para el ID, es mejor que el DAO lo genere o que se use UUID, pero para simplificar:
        this.id = "Mascota-" + System.currentTimeMillis() + "-" + contadorIdTemporal++; // ID más unico
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.propietarioDocumento = propietarioDocumento;
    }

    // Constructor para cuando se carga desde la BD (ya tiene ID)
    public Mascota(String id, String nombre, String especie, String raza, int edad, String propietarioDocumento) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.propietarioDocumento = propietarioDocumento;
    }

    // Implementación de RegistrableEntidad
    @Override
    public String getIdentificador() { return id; }
    @Override
    public String getNombre() { return nombre; }
    @Override
    public String mostrarInfo() {
        return "ID: " + id + "\nNombre: " + nombre + "\nEspecie: " + especie + "\nRaza: " + raza + "\nEdad: " + edad + " años\nDoc. Propietario: " + propietarioDocumento;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getPropietarioDocumento() { return propietarioDocumento; }
    public void setPropietarioDocumento(String propietarioDocumento) { this.propietarioDocumento = propietarioDocumento; }

    @Override
    public String toString() {
        return nombre; // Para los JComboBox
    }
}
