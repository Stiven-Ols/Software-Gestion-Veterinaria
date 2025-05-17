package com.veterinaria;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Cita implements RegistrableEntidad, Serializable {
    private String id;
    private String docPropietario;
    private String idMascota;
    private String docVeterinario;
    private Date fechaHora;
    private TipoServicio motivo; // Usar el Enum TipoServicio
    private String estado;

    public Cita() {} // Constructor sin argumentos

    // Constructor para crear una nueva cita
    public Cita(String docPropietario, String idMascota, String docVeterinario, Date fechaHora, TipoServicio motivo, String estado) {
        this.id = UUID.randomUUID().toString();
        this.docPropietario = docPropietario;
        this.idMascota = idMascota;
        this.docVeterinario = docVeterinario;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = estado;
    }

    // Constructor para cargar desde la BD
    public Cita(String id, String docPropietario, String idMascota, String docVeterinario, Date fechaHora, TipoServicio motivo, String estado) {
        this.id = id;
        this.docPropietario = docPropietario;
        this.idMascota = idMascota;
        this.docVeterinario = docVeterinario;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = estado;
    }

    @Override
    public String getIdentificador() { return id; }
    @Override
    public String getNombre() {
        return "Cita para Mascota ID: " + idMascota + " el " + (fechaHora != null ? new SimpleDateFormat("dd/MM/yyyy").format(fechaHora) : "N/A");
    }
    @Override
    public String mostrarInfo() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return "ID Cita: " + id + "\nDoc. Propietario: " + docPropietario + "\nID Mascota: " + idMascota +
                "\nDoc. Veterinario: " + docVeterinario + "\nFecha y Hora: " + (fechaHora != null ? sdf.format(fechaHora) : "N/A") +
                "\nMotivo: " + (motivo != null ? motivo.getDescripcion() : "N/A") + "\nEstado: " + estado;
    }

    // Getters y Setters
    public String getId() { return id; }
    public String getDocPropietario() { return docPropietario; }
    public void setDocPropietario(String docPropietario) { this.docPropietario = docPropietario; }
    public String getIdMascota() { return idMascota; }
    public void setIdMascota(String idMascota) { this.idMascota = idMascota; }
    public String getDocVeterinario() { return docVeterinario; }
    public void setDocVeterinario(String docVeterinario) { this.docVeterinario = docVeterinario; }
    public Date getFechaHora() { return fechaHora; }
    public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }
    public TipoServicio getMotivo() { return motivo; } // Devuelve el Enum
    public void setMotivo(TipoServicio motivo) { this.motivo = motivo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
