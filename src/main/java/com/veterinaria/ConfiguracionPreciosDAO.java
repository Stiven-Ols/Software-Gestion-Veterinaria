package com.veterinaria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ConfiguracionPreciosDAO {

    public static void guardarPrecio(String nombreServicio, int precio) throws SQLException {
        String sql = "INSERT OR REPLACE INTO ConfiguracionPrecios (servicio_nombre, precio) VALUES (?, ?)";
        Connection conn = Database.conectar();
        if (conn == null) throw new SQLException("No hay conexión a la BD para guardar precio.");
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, nombreServicio);
            pst.setInt(2, precio);
            pst.executeUpdate();
        }
    }

    public static Map<String, Integer> cargarPrecios() throws SQLException {
        Map<String, Integer> precios = new HashMap<>();
        String sql = "SELECT servicio_nombre, precio FROM ConfiguracionPrecios";
        Connection conn = Database.conectar();
        if (conn == null) throw new SQLException("No hay conexión a la BD para cargar precios.");
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                precios.put(rs.getString("servicio_nombre"), rs.getInt("precio"));
            }
        }
        return precios;
    }

    // Metodo para obtener un precio específico, útil si no quieres cargar todo el mapa siempre
    public static Integer getPrecioServicio(String nombreServicio) throws SQLException {
        String sql = "SELECT precio FROM ConfiguracionPrecios WHERE servicio_nombre = ?";
        Connection conn = Database.conectar();
        if (conn == null) throw new SQLException("No hay conexión a la BD para obtener precio.");
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, nombreServicio);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("precio");
                }
            }
        }
        return null; // O lanzar excepción / devolver un default
    }
}