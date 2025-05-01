import java.sql.*;

public class Database {
    private static Connection conn = null;
    private static final String URL = "jdbc:sqlite:vetdb.sqlite";

    public static synchronized Connection conectar() {
        try {
            if (conn == null || conn.isClosed()) {
                // En SQLite es importante forzar pragma para claves foráneas
                conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement();
                stmt.execute("PRAGMA foreign_keys = ON;");
                crearTablasSiNoExisten(conn);
                System.out.println("Conexión SQLite establecida correctamente");
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión SQLite: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    private static void crearTablasSiNoExisten(Connection connection) {
        try {
            Statement stmt = connection.createStatement();

            // Propietarios
            stmt.execute("CREATE TABLE IF NOT EXISTS Propietarios (" +
                    "documento TEXT PRIMARY KEY, " +
                    "nombre TEXT NOT NULL, " +
                    "telefono TEXT, " +
                    "direccion TEXT, " +
                    "correo TEXT)");

            // Mascotas
            stmt.execute("CREATE TABLE IF NOT EXISTS Mascotas (" +
                    "id TEXT PRIMARY KEY, " +
                    "nombre TEXT NOT NULL, " +
                    "especie TEXT, " +
                    "raza TEXT, " +
                    "edad INTEGER, " +
                    "propietarioDocumento TEXT, " +
                    "FOREIGN KEY(propietarioDocumento) REFERENCES Propietarios(documento))");

            // Veterinarios
            stmt.execute("CREATE TABLE IF NOT EXISTS Veterinarios (" +
                    "documento TEXT PRIMARY KEY, " +
                    "nombre TEXT NOT NULL, " +
                    "especialidad TEXT, " +
                    "telefono TEXT, " +
                    "disponibilidad INTEGER DEFAULT 1)");

            // Citas
            stmt.execute("CREATE TABLE IF NOT EXISTS Citas (" +
                    "id TEXT PRIMARY KEY, " +
                    "docPropietario TEXT, " +
                    "idMascota TEXT, " +
                    "docVeterinario TEXT, " +
                    "fechaHora TEXT, " +
                    "motivo TEXT, " +
                    "estado TEXT DEFAULT 'Pendiente')");
        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
