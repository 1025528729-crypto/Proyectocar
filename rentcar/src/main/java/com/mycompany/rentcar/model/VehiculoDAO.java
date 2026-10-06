package com.mycompany.rentcar.model;

import com.mycompany.rentcar.config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO {

    public boolean insertar(Vehiculo vehiculo) {
        String sql = "INSERT INTO vehiculos (placa, marca, modelo, precio_por_dia) VALUES (?, ?, ?, ?)";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, vehiculo.getPlaca());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setDouble(4, vehiculo.getPrecioPorDia());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar vehículo en MySQL: " + e.getMessage());
            return false;
        }
    }
    public boolean eliminar(String placa) {
    String sql = "DELETE FROM vehiculos WHERE placa = ?";

    try (Connection con = Conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, placa);
        int filasAfectadas = ps.executeUpdate();
        return filasAfectadas > 0;

    } catch (SQLException e) {
        System.err.println("Error al eliminar vehículo: " + e.getMessage());
        return false;
    }
}

    // Nuevo método para consultar todos los vehículos
    public List<Vehiculo> listar() {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculos";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Vehiculo v = new Vehiculo();
                v.setPlaca(rs.getString("placa"));
                v.setMarca(rs.getString("marca"));
                v.setModelo(rs.getString("modelo"));
                v.setPrecioPorDia(rs.getDouble("precio_por_dia"));
                lista.add(v);
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar vehículos: " + e.getMessage());
        }

        return lista;
    }
}