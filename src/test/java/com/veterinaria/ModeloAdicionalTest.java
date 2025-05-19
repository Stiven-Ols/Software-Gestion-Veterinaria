// ModeloAdicionalTest.java
package com.veterinaria;

import org.junit.jupiter.api.*;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

public class ModeloAdicionalTest {

    // Limpiar la tabla de precios antes de cada test que la modifique
    // O asegurar que el test de precios restaure los valores.
    // Para este test específico, vamos a restaurar el precio después.

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
        Veterinario v = new Veterinario(documento, nombre, especialidad, telefono, disponible);
        assertEquals(documento, v.getDocumento());
        assertEquals(nombre, v.getNombre());
        assertEquals(telefono, v.getTelefono());
        assertEquals(especialidad, v.getEspecialidad());
        assertEquals(disponible, v.isDisponibilidad());
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
        // Como los precios ahora pueden cambiar por la BD, probamos el precioPorDefecto
        assertEquals(60000, TipoServicio.CONSULTA.getPrecioPorDefecto());
        assertEquals("Consulta General", TipoServicio.CONSULTA.getDescripcion());
    }

    @Test
    public void testCambioPrecioTipoServicioEnum() {
        TipoServicio servicio = TipoServicio.CONSULTA;
        // Es importante obtener el precio que tiene ANTES de la modificación del test,
        // ya que podría haber sido afectado por la BD o un test anterior si no hay un @AfterEach
        // que limpie la tabla ConfiguracionPrecios o restaure los valores del enum.
        // Para un test más aislado, podríamos forzar el precio actual al precioPorDefecto antes de empezar.

        int precioAntesDelTest = servicio.getPrecio(); // Precio actual cargado (podría ser de BD)
        int precioPorDefectoOriginal = servicio.getPrecioPorDefecto(); // El hardcodeado

        int nuevoPrecioPrueba = 75000;

        try {
            servicio.setPrecioConfigurado(nuevoPrecioPrueba);
            assertEquals(nuevoPrecioPrueba, servicio.getPrecio(), "El precio del servicio no se actualizó correctamente en memoria después de setPrecioConfigurado.");

        } catch (SQLException e) {
            fail("SQLException durante el test de cambio de precio: " + e.getMessage());
        } finally {
            try {
                // Restauramos al precioPorDefecto original del enum, que es un estado conocido.
                servicio.setPrecioConfigurado(precioPorDefectoOriginal);
                // Y actualizamos la instancia en memoria del enum (setPrecioConfigurado ya lo hace)
                servicio.setPrecioInterno(precioPorDefectoOriginal); // Asegurar consistencia en memoria por si acaso

            } catch (SQLException e) {
                System.err.println("Error restaurando precio en testCambioPrecioTipoServicioEnum: " + e.getMessage());
            }
        }
    }


    @Test
    public void testTipoServicioFromString() {
        assertEquals(TipoServicio.CONSULTA, TipoServicio.fromString("Consulta General"));
        assertEquals(TipoServicio.CONSULTA, TipoServicio.fromString("CONSULTA"));
        assertEquals(TipoServicio.VACUNACION, TipoServicio.fromString("vacunacion"));
        // Comprobar el caso por defecto con un string inválido
        // La advertencia en consola es esperada si el string es inválido.
        assertEquals(TipoServicio.CONSULTA, TipoServicio.fromString("ServicioInexistente"));
    }
}