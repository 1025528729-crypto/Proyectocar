package com.mycompany.rentcar.model;

public class Vehiculo {

    private String placa;
    private String marca;
    private String modelo;
    private double precioPorDia;
    private String foto;

    // Constructor por defecto
    public Vehiculo() {
    }

    // Constructor con parámetros
    public Vehiculo(String placa, String marca, String modelo, double precioPorDia) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.precioPorDia = precioPorDia;
    }

    // Constructor con foto
    public Vehiculo(String placa, String marca, String modelo, double precioPorDia, String foto) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.precioPorDia = precioPorDia;
        this.foto = foto;
    }

    // ==============================
    // GETTERS Y SETTERS
    // ==============================

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getPrecioPorDia() {
        return precioPorDia;
    }

    public void setPrecioPorDia(double precioPorDia) {
        this.precioPorDia = precioPorDia;
    }

    // ==============================
    // FOTO DEL VEHÍCULO
    // ==============================

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    // ==============================
    // TOSTRING
    // ==============================

    @Override
    public String toString() {
        return "Vehiculo{"
                + "placa='" + placa + '\''
                + ", marca='" + marca + '\''
                + ", modelo='" + modelo + '\''
                + ", precioPorDia=" + precioPorDia
                + ", foto='" + foto + '\''
                + '}';
    }
}