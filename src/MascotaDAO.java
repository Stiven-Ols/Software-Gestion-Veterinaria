import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MascotaDAO {
    public static void guardar(Mascota m) throws SQLException {
        String sql = "INSERT OR REPLACE INTO Mascotas(id, nombre, especie, raza, edad, propietarioDocumento) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = Database.conectar();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, m.getId());
            pst.setString(2, m.getNombre());
            pst.setString(3, m.getEspecie());
            pst.setString(4, m.getRaza());
            pst.setInt(5, m.getEdad());
            pst.setString(6, m.getPropietarioDocumento());
            pst.executeUpdate();
        }
    }

    public static List<Mascota> listarTodos() throws SQLException {
        List<Mascota> lista = new ArrayList<>();
        String sql = "SELECT * FROM Mascotas";
        Connection conn = Database.conectar();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Mascota(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("especie"),
                        rs.getString("raza"),
                        rs.getInt("edad"),
                        rs.getString("propietarioDocumento")
                ));
            }
        }
        return lista;
    }

    public static void eliminarPorId(String id) throws SQLException {
        String sql = "DELETE FROM Mascotas WHERE id=?";
        Connection conn = Database.conectar();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            pst.executeUpdate();
        }
    }

    public static List<Mascota> listarPorPropietario(String docPropietario) throws SQLException {
        List<Mascota> lista = new ArrayList<>();
        String sql = "SELECT * FROM Mascotas WHERE propietarioDocumento=?";
        Connection conn = Database.conectar();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, docPropietario);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Mascota(
                            rs.getString("id"),
                            rs.getString("nombre"),
                            rs.getString("especie"),
                            rs.getString("raza"),
                            rs.getInt("edad"),
                            rs.getString("propietarioDocumento")
                    ));
                }
            }
        }
        return lista;
    }
}
