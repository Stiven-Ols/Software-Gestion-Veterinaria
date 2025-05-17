package com.veterinaria;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DatabaseMetaData;

public class DatabaseTest {

    @AfterEach
    void tearDown() {
        // Cierra la conexión después de cada prueba para asegurar un estado limpio
        // y permitir que las pruebas de reconexión funcionen correctamente.
        Database.cerrarConexion();
    }

    @Test
    void testConectarDevuelveConexionValida() {
        Connection conn = Database.conectar();
        assertNotNull(conn, "La conexión no debería ser nula.");
        try {
            assertFalse(conn.isClosed(), "La conexión no debería estar cerrada.");
        } catch (SQLException e) {
            fail("Error al verificar si la conexión está cerrada: " + e.getMessage());
        }
    }

    @Test
    void testConectarEsSingleton() {
        Connection conn1 = Database.conectar();
        assertNotNull(conn1, "La primera conexión no debería ser nula.");

        Connection conn2 = Database.conectar();
        assertNotNull(conn2, "La segunda conexión no debería ser nula.");

        assertSame(conn1, conn2, "Database.conectar() debería devolver la misma instancia de conexión si no se ha cerrado previamente.");
    }

    @Test
    void testCerrarConexionYReconectar() throws SQLException {
        Connection conn1 = Database.conectar();
        assertNotNull(conn1, "La conexión inicial no debería ser nula.");
        assertFalse(conn1.isClosed(), "La conexión inicial no debería estar cerrada.");

        Database.cerrarConexion(); // Esto cierra la conexión interna de la clase Database y la establece a null.
        // conn1 (la variable local) ahora referencia un objeto de conexión que ha sido cerrado.

        assertTrue(conn1.isClosed(), "La conexión original (conn1) referenciada localmente debería estar cerrada después de llamar a Database.cerrarConexion().");

        Connection conn2 = Database.conectar(); // Esto debería crear una nueva instancia de conexión.
        assertNotNull(conn2, "La conexión después de cerrar y reconectar no debería ser nula.");
        assertFalse(conn2.isClosed(), "La nueva conexión no debería estar cerrada.");

        assertNotSame(conn1, conn2, "Después de cerrar y reconectar, se debería obtener una nueva instancia de conexión.");
    }

    @Test
    void testCreacionDeTablas() {
        Connection conn = Database.conectar();
        assertNotNull(conn, "La conexión no debe ser nula para probar la creación de tablas.");

        String[] expectedTables = {"Propietarios", "Mascotas", "Veterinarios", "Citas"};
        DatabaseMetaData metaData;

        try {
            metaData = conn.getMetaData();
            for (String tableName : expectedTables) {
                // El tercer argumento (tableNamePattern) es sensible a mayúsculas/minúsculas en algunas BDs,
                // pero para SQLite suele funcionar con el nombre exacto como fue creado.
                // El cuarto argumento es un array de tipos de tabla a buscar.
                ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
                assertTrue(tables.next(), "La tabla '" + tableName + "' debería existir.");
                assertEquals(tableName, tables.getString("TABLE_NAME"),
                        "El nombre de la tabla encontrado (" + tables.getString("TABLE_NAME") +
                                ") no coincide con el esperado (" + tableName + ").");
                tables.close();
            }
        } catch (SQLException e) {
            fail("Error al verificar la existencia de las tablas: " + e.getMessage());
        }
    }

    @Test
    void testForeignKeysHabilitadas() {
        Connection conn = Database.conectar(); // El método conectar ya ejecuta "PRAGMA foreign_keys = ON;"
        assertNotNull(conn, "La conexión no debe ser nula para probar las claves foráneas.");

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA foreign_keys;")) {
            assertTrue(rs.next(), "El pragma foreign_keys debería devolver un resultado.");
            assertEquals(1, rs.getInt(1), "Las claves foráneas deberían estar habilitadas (valor 1).");
        } catch (SQLException e) {
            fail("Error al verificar el estado de las claves foráneas: " + e.getMessage());
        }
    }
}
