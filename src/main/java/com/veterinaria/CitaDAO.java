package com.veterinaria;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CitaDAO {
    // Formato para guardar/leer fechas en SQLite como TEXT
    private static final SimpleDateFormat SDF_DATABASE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void guardar(Cita c) throws SQLException {
        String sql = "INSERT OR REPLACE INTO Citas(id, docPropietario, idMascota, docVeterinario, fechaHora, motivo, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = Database.conectar();
        if (conn == null) throw new SQLException("No hay conexión a la BD.");
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, c.getId());
            pst.setString(2, c.getDocPropietario());
            pst.setString(3, c.getIdMascota());
            pst.setString(4, c.getDocVeterinario());
            pst.setString(5, (c.getFechaHora() != null) ? SDF_DATABASE.format(c.getFechaHora()) : null); // Fecha a String
            pst.setString(6, (c.getMotivo() != null) ? c.getMotivo().name() : null); // Guardar el NOMBRE del enum
            pst.setString(7, c.getEstado());
            pst.executeUpdate();
        }
    }

    public static List<Cita> listarTodos() throws SQLException {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT * FROM Citas";
        Connection conn = Database.conectar();
        if (conn == null) throw new SQLException("No hay conexión a la BD.");
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Date fechaHora = null;
                String fechaHoraStr = rs.getString("fechaHora");
                if (fechaHoraStr != null) {
                    try {
                        fechaHora = SDF_DATABASE.parse(fechaHoraStr); // String a Fecha
                    } catch (ParseException e) {
                        System.err.println("Error al parsear fecha de cita desde BD: " + fechaHoraStr + " para Cita ID: " + rs.getString("id") + " - " + e.getMessage());
                    }
                }
                TipoServicio motivoEnum = TipoServicio.fromString(rs.getString("motivo"));

                lista.add(new Cita( // Usa el constructor que recibe ID y TipoServicio Enum
                        rs.getString("id"),
                        rs.getString("docPropietario"),
                        rs.getString("idMascota"),
                        rs.getString("docVeterinario"),
                        fechaHora,
                        motivoEnum,
                        rs.getString("estado")
                ));
            }
        }
        return lista;
    }

    public static void eliminarPorId(String id) throws SQLException {
        String sql = "DELETE FROM Citas WHERE id=?";
        Connection conn = Database.conectar();
        if (conn == null) throw new SQLException("No hay conexión a la BD.");
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            pst.executeUpdate();
        }
    }

    // Puedes agregar listarPorEstado si lo necesitas para filtrar en PagosPanel
    public static List<Cita> listarPorEstado(String estado) throws SQLException {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT * FROM Citas WHERE estado = ?";
        Connection conn = Database.conectar();
        if (conn == null) throw new SQLException("No hay conexión a la BD.");
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, estado);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Date fechaHora = null;
                    String fechaHoraStr = rs.getString("fechaHora");
                    if (fechaHoraStr != null) {
                        try {
                            fechaHora = SDF_DATABASE.parse(fechaHoraStr);
                        } catch (ParseException e) {
                            System.err.println("Error al parsear fecha (listarPorEstado): " + fechaHoraStr);
                        }
                    }
                    TipoServicio motivoEnum = TipoServicio.fromString(rs.getString("motivo"));
                    lista.add(new Cita(
                            rs.getString("id"), rs.getString("docPropietario"), rs.getString("idMascota"),
                            rs.getString("docVeterinario"), fechaHora, motivoEnum, rs.getString("estado")
                    ));
                }
            }
        }
        return lista;
    }
}
