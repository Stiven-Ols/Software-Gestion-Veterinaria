import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CitaDAO {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void guardar(Cita c) throws SQLException {
        String sql = "INSERT OR REPLACE INTO Citas(id, docPropietario, idMascota, docVeterinario, fechaHora, motivo, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = Database.conectar();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, c.getId());
            pst.setString(2, c.getDocPropietario());
            pst.setString(3, c.getIdMascota());
            pst.setString(4, c.getDocVeterinario());
            pst.setString(5, SDF.format(c.getFechaHora())); // Formato fecha para SQLite
            pst.setString(6, c.getMotivo());
            pst.setString(7, c.getEstado());
            pst.executeUpdate();
        }
    }

    public static List<Cita> listarTodos() throws SQLException {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT * FROM Citas";
        Connection conn = Database.conectar();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Date fechaHora;
                try {
                    fechaHora = SDF.parse(rs.getString("fechaHora"));
                } catch (ParseException e) {
                    System.err.println("Error al parsear fecha: " + e.getMessage());
                    fechaHora = new Date(); // Fecha actual por defecto
                }

                lista.add(new Cita(
                        rs.getString("id"),
                        rs.getString("docPropietario"),
                        rs.getString("idMascota"),
                        rs.getString("docVeterinario"),
                        fechaHora,
                        rs.getString("motivo"),
                        rs.getString("estado")
                ));
            }
        }
        return lista;
    }

    public static void eliminarPorId(String id) throws SQLException {
        String sql = "DELETE FROM Citas WHERE id=?";
        Connection conn = Database.conectar();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            pst.executeUpdate();
        }
    }

    public static List<Cita> listarPorEstado(String estado) throws SQLException {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT * FROM Citas WHERE estado=?";
        Connection conn = Database.conectar();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, estado);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Date fechaHora;
                    try {
                        fechaHora = SDF.parse(rs.getString("fechaHora"));
                    } catch (ParseException e) {
                        System.err.println("Error al parsear fecha: " + e.getMessage());
                        fechaHora = new Date(); // Fecha actual por defecto
                    }

                    lista.add(new Cita(
                            rs.getString("id"),
                            rs.getString("docPropietario"),
                            rs.getString("idMascota"),
                            rs.getString("docVeterinario"),
                            fechaHora,
                            rs.getString("motivo"),
                            rs.getString("estado")
                    ));
                }
            }
        }
        return lista;
    }
}
