package com.veterinaria;

/**
 * Interfaz que define comportamientos comunes para entidades registrables en el sistema.
 */
public interface RegistrableEntidad {
    /**
     * Obtiene el identificador único de la entidad.
     * @return String con el identificador
     */
    String getIdentificador();

    /**
     * Obtiene el nombre de la entidad.
     * @return String con el nombre
     */
    String getNombre();

    /**
     * Proporciona la información formateada de la entidad.
     * @return String con información detallada
     */
    String mostrarInfo();
}
