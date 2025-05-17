package com.veterinaria;

import java.sql.*;

public class Database {
    private static Connection conn = null;
    private static final String URL = "jdbc:sqlite:vetdb.sqlite"; // Nombre del archivo de la BD

    public static synchronized Connection conectar() {
        try {
            if (conn == null || conn.isClosed()) {
                try {
                    Class.forName("org.sqlite.JDBC"); // Carga explícita del driver
                    conn = DriverManager.getConnection(URL);
                    // Habilitar claves foráneas para SQLite, MUY IMPORTANTE
                    try (Statement pragmaStmt = conn.createStatement()) {
                        pragmaStmt.execute("PRAGMA foreign_keys = ON;");
                    }
                    crearTablasSiNoExisten(conn);
                    System.out.println("Conexión SQLite establecida y tablas verificadas.");
                } catch (ClassNotFoundException e) {
                    System.err.println("Error: Driver JDBC SQLite no encontrado. Asegúrate de tener la dependencia en build.gradle.");
                    e.printStackTrace();
                    conn = null; // No se pudo conectar
                    return null;
                } catch (SQLException e) {
                    System.err.println("Error al conectar/crear tablas SQLite: " + e.getMessage());
                    e.printStackTrace();
                    conn = null; // No se pudo conectar
                    return null;
                }
            }
        } catch (SQLException e) { // Error al verificar conn.isClosed()
            System.err.println("Error al verificar el estado de la conexión SQLite: " + e.getMessage());
            conn = null; // Asumir inválida
        }
        return conn;
    }

    private static void crearTablasSiNoExisten(Connection connection) throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.err.println("No se pueden crear tablas, la conexión no está disponible.");
            return;
        }
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Propietarios (" +
                    "documento TEXT PRIMARY KEY, nombre TEXT NOT NULL, telefono TEXT, direccion TEXT, correo TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Mascotas (" +
                    "id TEXT PRIMARY KEY, nombre TEXT NOT NULL, especie TEXT, raza TEXT, edad INTEGER, " +
                    "propietarioDocumento TEXT, FOREIGN KEY(propietarioDocumento) REFERENCES Propietarios(documento) ON DELETE CASCADE)"); // ON DELETE CASCADE es importante
            stmt.execute("CREATE TABLE IF NOT EXISTS Veterinarios (" +
                    "documento TEXT PRIMARY KEY, nombre TEXT NOT NULL, especialidad TEXT, telefono TEXT, disponibilidad INTEGER DEFAULT 1)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Citas (" +
                    "id TEXT PRIMARY KEY, docPropietario TEXT, idMascota TEXT, docVeterinario TEXT, " +
                    "fechaHora TEXT NOT NULL, motivo TEXT, estado TEXT DEFAULT 'Pendiente', " + // motivo como TEXT (nombre del enum)
                    "FOREIGN KEY(docPropietario) REFERENCES Propietarios(documento) ON DELETE CASCADE, " +
                    "FOREIGN KEY(idMascota) REFERENCES Mascotas(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(docVeterinario) REFERENCES Veterinarios(documento) ON DELETE SET NULL)"); // Si se borra un vet, la cita queda sin vet o se borra (SET NULL o CASCADE)
        }
    }

    // Opcional: Método para cerrar la conexión al final de la aplicación
    public static void cerrarConexion() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
                System.out.println("Conexión SQLite cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión SQLite: " + e.getMessage());
        }
    }
}
