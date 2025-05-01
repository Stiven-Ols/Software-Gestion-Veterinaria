import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeterinarioDAO {
    public static void guardar(Veterinario v) throws SQLException {
        String sql = "INSERT OR REPLACE INTO Veterinarios(documento, nombre, especialidad, telefono, disponibilidad) VALUES (?, ?, ?, ?, ?)";
        Connection conn = Database.conectar();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, v.getDocumento());
            pst.setString(2, v.getNombre());
            pst.setString(3, v.getEspecialidad());
            pst.setString(4, v.getTelefono());
            pst.setInt(5, v.isDisponibilidad() ? 1 : 0);
            pst.executeUpdate();
        }
    }

    public static List<Veterinario> listarTodos() throws SQLException {
        List<Veterinario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Veterinarios";
        Connection conn = Database.conectar();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Veterinario(
                        rs.getString("documento"),
                        rs.getString("nombre"),
                        rs.getString("especialidad"),
                        rs.getString("telefono"),
                        rs.getInt("disponibilidad") == 1
                ));
            }
        }
        return lista;
    }

    public static void eliminarPorDocumento(String documento) throws SQLException {
        String sql = "DELETE FROM Veterinarios WHERE documento=?";
        Connection conn = Database.conectar();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, documento);
            pst.executeUpdate();
        }
    }
}
