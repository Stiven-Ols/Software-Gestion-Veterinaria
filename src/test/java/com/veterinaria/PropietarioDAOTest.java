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

public class PropietarioDAOTest {

    @BeforeAll // Se ejecuta una vez antes de todas las pruebas de esta clase
    static void setUpDatabase() {
        // Asegurar que la conexión y las tablas se creen una sola vez
        try (Connection conn = Database.conectar()) {
            assertNotNull(conn, "La conexión a la BD no debe ser nula.");
        } catch (SQLException e) {
            fail("Error al conectar a la BD para configuración inicial: " + e.getMessage());
        }
    }

    @BeforeEach // Se ejecuta antes de CADA prueba
    @AfterEach  // Se ejecuta después de CADA prueba
    void limpiarTablaPropietarios() {
        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement()) {
            assertNotNull(conn, "La conexión no debe ser nula para limpiar.");
            stmt.execute("DELETE FROM Propietarios");
            stmt.execute("DELETE FROM Mascotas"); // Limpiar mascotas también si las pruebas de propietario las afectan
            stmt.execute("DELETE FROM Citas");    // Y citas
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error al limpiar la tabla Propietarios: " + e.getMessage());
        }
    }

    @Test
    void testGuardarYListarPropietario() throws SQLException {
        Propietario p1 = new Propietario("123", "Juan Perez", "3001234567", "Calle Falsa 123", "juan@example.com");
        PropietarioDAO.guardar(p1);

        List<Propietario> propietarios = PropietarioDAO.listarTodos();
        assertNotNull(propietarios, "La lista de propietarios no debería ser nula.");
        assertEquals(1, propietarios.size(), "Debería haber 1 propietario en la lista.");
        assertEquals("Juan Perez", propietarios.get(0).getNombre(), "El nombre del propietario no coincide.");
        assertEquals("123", propietarios.get(0).getDocumento());
    }

    @Test
    void testEliminarPropietario() throws SQLException {
        Propietario p1 = new Propietario("123", "Juan Perez", "3001234567", "Calle Falsa 123", "juan@example.com");
        PropietarioDAO.guardar(p1);
        PropietarioDAO.eliminarPorDocumento("123");

        List<Propietario> propietarios = PropietarioDAO.listarTodos();
        assertTrue(propietarios.isEmpty(), "La lista de propietarios debería estar vacía después de eliminar.");
    }

    @Test
    void testGuardarPropietarioDuplicadoDebeFallarOReemplazar() throws SQLException {
        Propietario p1 = new Propietario("789", "Ana Gomez", "3107890123", "Avenida Siempre Viva 742", "ana@example.com");
        PropietarioDAO.guardar(p1); // Guardar la primera vez

        // Intentar guardar de nuevo con el mismo documento (INSERT OR REPLACE lo reemplazará)
        Propietario p2 = new Propietario("789", "Ana Maria Gomez", "3107890123", "Avenida Siempre Viva 742", "ana.maria@example.com");
        PropietarioDAO.guardar(p2);

        List<Propietario> propietarios = PropietarioDAO.listarTodos();
        assertEquals(1, propietarios.size(), "Solo debe haber un propietario con ese documento si se usa INSERT OR REPLACE.");
        assertEquals("Ana Maria Gomez", propietarios.get(0).getNombre(), "El nombre debería ser el del último guardado.");
    }

    // Prueba para verificar que listarTodos devuelve lista vacía si no hay datos
    @Test
    void testListarTodosConTablaVacia() throws SQLException {
        List<Propietario> propietarios = PropietarioDAO.listarTodos();
        assertNotNull(propietarios);
        assertTrue(propietarios.isEmpty());
    }

    // Prueba para el método mostrarInfo de Propietario (si lo implementaste con RegistrableEntidad)
    @Test
    void testMostrarInfoPropietario() {
        Propietario p = new Propietario("TestDoc", "Test Nombre", "Test Tel", "Test Dir", "Test Correo");
        String info = p.mostrarInfo(); // Asumiendo que Propietario implementa mostrarInfo()
        assertNotNull(info);
        assertTrue(info.contains("Test Nombre"));
        assertTrue(info.contains("TestDoc"));
    }
}
