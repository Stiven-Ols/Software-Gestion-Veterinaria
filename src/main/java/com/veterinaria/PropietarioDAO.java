package com.veterinaria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropietarioDAO {
    public static void guardar(Propietario p) throws SQLException {
        String sql = "INSERT OR REPLACE INTO Propietarios(documento, nombre, telefono, direccion, correo) VALUES (?, ?, ?, ?, ?)";
        Connection conn = Database.conectar();
        if (conn == null) throw new SQLException("No hay conexión a la BD.");
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, p.getDocumento());
            pst.setString(2, p.getNombre());
            pst.setString(3, p.getTelefono());
            pst.setString(4, p.getDireccion());
            pst.setString(5, p.getCorreo());
            pst.executeUpdate();
        }
    }

    public static List<Propietario> listarTodos() throws SQLException {
        List<Propietario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Propietarios";
        Connection conn = Database.conectar();
        if (conn == null) throw new SQLException("No hay conexión a la BD.");
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Propietario(
                        rs.getString("documento"), rs.getString("nombre"), rs.getString("telefono"),
                        rs.getString("direccion"), rs.getString("correo")
                ));
            }
        }
        return lista;
    }

    public static void eliminarPorDocumento(String documento) throws SQLException {
        String sql = "DELETE FROM Propietarios WHERE documento=?";
        Connection conn = Database.conectar();
        if (conn == null) throw new SQLException("No hay conexión a la BD.");
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, documento);
            pst.executeUpdate();
        }
    }
}
