package com.mycompany.rentcar.model;

import java.time.LocalDate;

/** Un alquiler guardado en la base de datos (o uno que se está por guardar). */
public class Alquiler {
    public int id;
    public int usuarioId;

    // Vehículo
    public String placa, marca, modelo, tipo, foto;
    public String lugar;
    public LocalDate recogida, devolucion;
    public long dias;
    public double total;

    // Quien alquila
    public String nombres, apellidos, documento, email, telefono;

    // Pago: NUNCA se guarda el número completo ni el CVV, solo los últimos 4 dígitos
    public String metodoPago;      // TARJETA_CREDITO, TARJETA_DEBITO, PSE, NEQUI, DAVIPLATA, EFECTIVO
    public String titular;
    public String ultimos4;

    public String estado = "ACTIVA";   // ACTIVA o CANCELADA

    public boolean esMoto() {
        return tipo != null && tipo.equalsIgnoreCase("MOTO");
    }

    public String nombreCompleto() {
        return ((nombres == null ? "" : nombres) + " " + (apellidos == null ? "" : apellidos)).trim();
    }

    public String descripcionPago() {
        if (metodoPago == null) return "";
        switch (metodoPago) {
            case "TARJETA_CREDITO": return "Tarjeta de crédito •••• " + (ultimos4 == null ? "" : ultimos4);
            case "TARJETA_DEBITO":  return "Tarjeta débito •••• " + (ultimos4 == null ? "" : ultimos4);
            case "PSE":             return "PSE";
            case "NEQUI":           return "Nequi";
            case "DAVIPLATA":       return "Daviplata";
            case "EFECTIVO":        return "Efectivo al recoger";
            default:                return metodoPago;
        }
    }
}