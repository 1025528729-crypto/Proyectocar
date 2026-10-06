package com.mycompany.rentcar.model;

public class Reserva {
    private String idReserva;
    private Vehiculo vehiculo;
    private int diasRenta;
    private double costoTotal;
    private double montoPagado;
    private boolean confirmada;

    public Reserva(String idReserva, Vehiculo vehiculo, int diasRenta) {
        this.idReserva = idReserva;
        this.vehiculo = vehiculo;
        this.diasRenta = diasRenta;
        this.costoTotal = vehiculo.getPrecioPorDia() * diasRenta;
        this.montoPagado = 0.0;
        this.confirmada = false;
    }

    public boolean procesarPago(double pago) {
        if (pago >= costoTotal) {
            this.montoPagado = pago;
            this.confirmada = true;
            return true;
        }
        return false;
    }

    // Getters
    public String getIdReserva() { return idReserva; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public int getDiasRenta() { return diasRenta; }
    public double getCostoTotal() { return costoTotal; }
    public double getMontoPagado() { return montoPagado; }
    public boolean isConfirmada() { return confirmada; }
}