package com.mycompany.rentcar.model;

import com.mycompany.rentcar.config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReservaDAO {

    public boolean guardarReserva(Reserva reserva) {
        String sql = "INSERT INTO reservas (id_reserva, placa_vehiculo, dias_renta, costo_total, monto_pagado, confirmada) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, reserva.getIdReserva());
            ps.setString(2, reserva.getVehiculo().getPlaca());
            ps.setInt(3, reserva.getDiasRenta());
            ps.setDouble(4, reserva.getCostoTotal());
            ps.setDouble(5, reserva.getMontoPagado());
            ps.setBoolean(6, reserva.isConfirmada());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar reserva: " + e.getMessage());
            return false;
        }
    }
}
