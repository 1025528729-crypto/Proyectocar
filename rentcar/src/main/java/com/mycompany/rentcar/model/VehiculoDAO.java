package com.mycompany.rentcar.model;

import com.mycompany.rentcar.config.Conexion;
import com.mycompany.rentcar.presenter.MarcaItem;
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

    // ==========================================================
    // NUEVO MÉTODO: Obtiene las marcas únicas desde la tabla vehículos
    // y les asigna automáticamente el nombre de archivo (ej: "audi.jpg")
    // ==========================================================
    public List<MarcaItem> obtenerMarcas() {
        List<MarcaItem> listaMarcas = new ArrayList<>();
        // Consultamos las marcas distintas que ya tienes guardadas en tus vehículos
        String sql = "SELECT DISTINCT marca FROM vehiculos WHERE marca IS NOT NULL"; 
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            int id = 1;
            while (rs.next()) {
                String nombreMarca = rs.getString("marca");
                // Convertimos el nombre a minúsculas para que coincida con tu imagen (ej: "Audi" -> "audi.jpg")
                String archivoImagen = nombreMarca.toLowerCase().trim() + ".jpg";
                
                listaMarcas.add(new MarcaItem(id, nombreMarca, archivoImagen));
                id++;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener las marcas: " + e.getMessage());
        }
        return listaMarcas;
    }

    // ==========================================================
    // MÉTODOS ADICIONALES PARA MODO USUARIO
    // ==========================================================
    public List<Vehiculo> listarPorMarca(String marca) {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculos WHERE LOWER(marca) = LOWER(?)";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, marca);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehiculo v = new Vehiculo();
                    v.setPlaca(rs.getString("placa"));
                    v.setMarca(rs.getString("marca"));
                    v.setModelo(rs.getString("modelo"));
                    v.setPrecioPorDia(rs.getDouble("precio_por_dia"));
                    lista.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al filtrar por marca: " + e.getMessage());
        }
        return lista;
    }

    public Vehiculo buscarPorPlaca(String placa) {
        String sql = "SELECT * FROM vehiculos WHERE placa = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Vehiculo v = new Vehiculo();
                    v.setPlaca(rs.getString("placa"));
                    v.setMarca(rs.getString("marca"));
                    v.setModelo(rs.getString("modelo"));
                    v.setPrecioPorDia(rs.getDouble("precio_por_dia"));
                    return v;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar por placa: " + e.getMessage());
        }
        return null;
    }
}