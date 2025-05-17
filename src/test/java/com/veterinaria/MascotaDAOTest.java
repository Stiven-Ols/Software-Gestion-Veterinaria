package com.veterinaria;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MascotaDAOTest {

    @BeforeAll
    static void setUpDatabase() {
        try (Connection conn = Database.conectar()) {
            assertNotNull(conn);
        } catch (SQLException e) {
            fail("Error al conectar a la BD para MascotaDAOTest: " + e.getMessage());
        }
    }

    @BeforeEach
    @AfterEach
    void limpiarTablas() { // Limpiar tablas relevantes antes/después de cada test
        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM Mascotas");
            stmt.execute("DELETE FROM Propietarios"); // Si las mascotas dependen de propietarios
            stmt.execute("DELETE FROM Citas");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error al limpiar tablas para MascotaDAOTest: " + e.getMessage());
        }
    }

    // Método auxiliar para crear un propietario de prueba
    private Propietario crearPropietarioDePrueba(String doc) throws SQLException {
        Propietario p = new Propietario(doc, "Propietario de Prueba " + doc, "12345", "Dir Prueba", "prop@test.com");
        PropietarioDAO.guardar(p);
        return p;
    }

    @Test
    void testGuardarYListarMascota() throws SQLException {
        Propietario prop = crearPropietarioDePrueba("PROP001");
        Mascota m1 = new Mascota("Firulais", "Perro", "Labrador", 5, prop.getDocumento());
        MascotaDAO.guardar(m1);

        List<Mascota> mascotas = MascotaDAO.listarTodos();
        assertNotNull(mascotas);
        assertEquals(1, mascotas.size());
        assertEquals("Firulais", mascotas.get(0).getNombre());
        assertEquals(prop.getDocumento(), mascotas.get(0).getPropietarioDocumento());
        assertNotNull(mascotas.get(0).getId(), "El ID de la mascota no debe ser nulo después de guardarla.");
    }

    @Test
    void testEliminarMascota() throws SQLException {
        Propietario prop = crearPropietarioDePrueba("PROP002");
        Mascota m1 = new Mascota("Luna", "Gato", "Siamés", 2, prop.getDocumento());
        MascotaDAO.guardar(m1);
        String idMascotaGuardada = m1.getId(); // Guardar el ID antes de que se pierda la referencia

        MascotaDAO.eliminarPorId(idMascotaGuardada);

        List<Mascota> mascotas = MascotaDAO.listarTodos();
        assertTrue(mascotas.isEmpty());
    }

    @Test
    void testListarMascotasPorPropietario() throws SQLException {
        Propietario prop1 = crearPropietarioDePrueba("PROP003A");
        Propietario prop2 = crearPropietarioDePrueba("PROP003B");

        Mascota m1 = new Mascota("Max", "Perro", "Golden", 3, prop1.getDocumento());
        Mascota m2 = new Mascota("Bella", "Perro", "Poodle", 1, prop1.getDocumento());
        Mascota m3 = new Mascota("Milo", "Gato", "Angora", 4, prop2.getDocumento());

        MascotaDAO.guardar(m1);
        MascotaDAO.guardar(m2);
        MascotaDAO.guardar(m3);

        List<Mascota> mascotasProp1 = MascotaDAO.listarPorPropietario(prop1.getDocumento());
        assertEquals(2, mascotasProp1.size(), "El propietario 1 debería tener 2 mascotas.");
        assertTrue(mascotasProp1.stream().anyMatch(m -> m.getNombre().equals("Max")));
        assertTrue(mascotasProp1.stream().anyMatch(m -> m.getNombre().equals("Bella")));

        List<Mascota> mascotasProp2 = MascotaDAO.listarPorPropietario(prop2.getDocumento());
        assertEquals(1, mascotasProp2.size(), "El propietario 2 debería tener 1 mascota.");
        assertEquals("Milo", mascotasProp2.get(0).getNombre());
    }

    @Test
    void testListarMascotasPorPropietarioInexistente() throws SQLException {
        List<Mascota> mascotas = MascotaDAO.listarPorPropietario("DOC_INEXISTENTE");
        assertNotNull(mascotas);
        assertTrue(mascotas.isEmpty(), "No deberían listarse mascotas para un propietario inexistente.");
    }

    @Test
    void testMostrarInfoMascota() throws SQLException {
        Propietario prop = crearPropietarioDePrueba("PROPINFO");
        Mascota m = new Mascota("Buddy", "Perro", "Beagle", 4, prop.getDocumento());
        MascotaDAO.guardar(m); // Guardar para que tenga ID generado por la lógica (si es el caso) o el UUID

        String info = m.mostrarInfo();
        assertNotNull(info);
        assertTrue(info.contains("Buddy"));
        assertTrue(info.contains(m.getId())); // Verificar que el ID esté en la info
        assertTrue(info.contains(prop.getDocumento()));
    }
}
