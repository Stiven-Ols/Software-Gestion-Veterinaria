package com.veterinaria;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

public class ModeloAdicionalTest {

    // --- Pruebas para Mascota ---
    @Test
    public void testCrearMascotaModelo() {
        String nombre = "Firulais";
        String especie = "Perro";
        String raza = "Labrador";
        int edad = 3;
        String propietarioDoc = "12345";

        Mascota m = new Mascota(nombre, especie, raza, edad, propietarioDoc);

        assertNotNull(m.getId(), "El ID de la mascota no debe ser nulo.");
        assertEquals(nombre, m.getNombre());
        assertEquals(especie, m.getEspecie());
        assertEquals(raza, m.getRaza());
        assertEquals(edad, m.getEdad());
        assertEquals(propietarioDoc, m.getPropietarioDocumento());
        // Pruebas de interfaz
        assertEquals(m.getId(), m.getIdentificador());
        assertNotNull(m.mostrarInfo());
    }

    @Test
    public void testConstructorMascotaConId() {
        String id = "MASCOTA_TEST_ID";
        Mascota m = new Mascota(id, "Luna", "Gato", "Siamés", 2, "54321");
        assertEquals(id, m.getId(), "El ID de la mascota no coincide con el proporcionado.");
    }

    // --- Pruebas para Veterinario ---
    @Test
    public void testCrearVeterinarioModelo() {
        String documento = "VET_MODEL_001";
        String nombre = "Ana López";
        String telefono = "3159876543";
        String especialidad = "Cardiología";
        boolean disponible = true;

        Veterinario v = new Veterinario(documento, nombre, especialidad, telefono, disponible); // Ajustado orden

        assertEquals(documento, v.getDocumento());
        assertEquals(nombre, v.getNombre());
        assertEquals(telefono, v.getTelefono());
        assertEquals(especialidad, v.getEspecialidad());
        assertEquals(disponible, v.isDisponibilidad());
        // Pruebas de interfaz y clase abstracta
        assertEquals("Veterinario", v.getTipoPersona());
        assertEquals(documento, v.getIdentificador());
        assertNotNull(v.mostrarInfo());
        assertTrue(v.mostrarInfo().contains(nombre) && v.mostrarInfo().contains(especialidad));
    }

    // --- Pruebas para Cita ---
    @Test
    public void testCrearCitaModelo() {
        String docPropietario = "PROP_CITA_01";
        String idMascota = "MAS_CITA_01";
        String docVeterinario = "VET_CITA_01";
        Date fechaHora = new Date();
        TipoServicio motivo = TipoServicio.CONSULTA;
        String estado = "PENDIENTE";

        Cita c = new Cita(docPropietario, idMascota, docVeterinario, fechaHora, motivo, estado);

        assertNotNull(c.getId(), "El ID de la cita no debe ser nulo.");
        assertEquals(docPropietario, c.getDocPropietario());
        assertEquals(idMascota, c.getIdMascota());
        assertEquals(docVeterinario, c.getDocVeterinario());
        assertEquals(fechaHora, c.getFechaHora());
        assertEquals(motivo, c.getMotivo());
        assertEquals(estado, c.getEstado());
        // Pruebas de interfaz
        assertEquals(c.getId(), c.getIdentificador());
        assertNotNull(c.mostrarInfo());
    }

    @Test
    public void testCambiarEstadoCitaModelo() {
        Cita c = new Cita("P01", "M01", "V01", new Date(), TipoServicio.VACUNACION, "PENDIENTE");
        assertEquals("PENDIENTE", c.getEstado());
        c.setEstado("PAGADA");
        assertEquals("PAGADA", c.getEstado());
    }

    // --- Pruebas para TipoServicio ---
    @Test
    public void testValoresTipoServicioEnum() {
        assertNotNull(TipoServicio.CONSULTA);
        assertNotNull(TipoServicio.VACUNACION);
        assertNotNull(TipoServicio.CIRUGIA);
        assertNotNull(TipoServicio.URGENCIA);

        assertEquals("Consulta General", TipoServicio.CONSULTA.getDescripcion());
        assertEquals(60000, TipoServicio.CONSULTA.getPrecio());
    }

    @Test
    public void testCambioPrecioTipoServicioEnum() {
        TipoServicio servicio = TipoServicio.CONSULTA;
        int precioOriginal = servicio.getPrecio();
        int nuevoPrecio = 75000;

        servicio.setPrecio(nuevoPrecio);
        assertEquals(nuevoPrecio, servicio.getPrecio(), "El precio del servicio no se actualizó correctamente.");

        // Restaurar precio para no afectar otras pruebas si se ejecutan en el mismo contexto
        servicio.setPrecio(precioOriginal);
        assertEquals(precioOriginal, servicio.getPrecio(), "El precio del servicio no se restauró.");
    }

    @Test
    public void testTipoServicioFromString() {
        assertEquals(TipoServicio.CONSULTA, TipoServicio.fromString("Consulta General"));
        assertEquals(TipoServicio.CONSULTA, TipoServicio.fromString("CONSULTA"));
        assertEquals(TipoServicio.VACUNACION, TipoServicio.fromString("vacunacion"));
        // Debería devolver el valor por defecto (o null si así lo defines) para un string inválido
        assertEquals(TipoServicio.CONSULTA, TipoServicio.fromString("ServicioInexistente"));
    }
}
