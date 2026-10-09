package com.mycompany.rentcar.model;

/** Modelo común para automóviles y motocicletas. */
public class Vehiculo {
    private String placa, marca, modelo, foto;
    private String tipo = "AUTO";
    private int anio;
    private String color = "", transmision = "", combustible = "";
    private int capacidad = 5;
    private int puertas = 4;
    private int kilometraje;
    private String categoria = "", descripcion = "", ciudad = "";
    private boolean disponible = true;
    private double precioPorDia;

    public Vehiculo() {}
    public Vehiculo(String placa, String marca, String modelo, double precioPorDia) {
        this.placa=placa; this.marca=marca; this.modelo=modelo; this.precioPorDia=precioPorDia;
    }
    public Vehiculo(String placa, String marca, String modelo, double precioPorDia, String foto) {
        this(placa, marca, modelo, precioPorDia); this.foto=foto;
    }
    public String getPlaca(){return placa;} public void setPlaca(String v){placa=v;}
    public String getMarca(){return marca;} public void setMarca(String v){marca=v;}
    public String getModelo(){return modelo;} public void setModelo(String v){modelo=v;}
    public double getPrecioPorDia(){return precioPorDia;} public void setPrecioPorDia(double v){precioPorDia=v;}
    public String getFoto(){return foto;} public void setFoto(String v){foto=v;}
    public String getTipo(){return tipo==null||tipo.isBlank()?"AUTO":tipo;} public void setTipo(String v){tipo=v;}
    public int getAnio(){return anio;} public void setAnio(int v){anio=v;}
    public String getColor(){return color==null?"":color;} public void setColor(String v){color=v;}
    public String getTransmision(){return transmision==null?"":transmision;} public void setTransmision(String v){transmision=v;}
    public String getCombustible(){return combustible==null?"":combustible;} public void setCombustible(String v){combustible=v;}
    public int getCapacidad(){return capacidad;} public void setCapacidad(int v){capacidad=v;}
    public int getPuertas(){return puertas;} public void setPuertas(int v){puertas=v;}
    public int getKilometraje(){return kilometraje;} public void setKilometraje(int v){kilometraje=v;}
    public String getCategoria(){return categoria==null?"":categoria;} public void setCategoria(String v){categoria=v;}
    public String getDescripcion(){return descripcion==null?"":descripcion;} public void setDescripcion(String v){descripcion=v;}
    public String getCiudad(){return ciudad==null?"":ciudad;} public void setCiudad(String v){ciudad=v;}
    public boolean isDisponible(){return disponible;} public void setDisponible(boolean v){disponible=v;}
    @Override public String toString(){return "Vehiculo{"+"placa='"+placa+'\''+", tipo='"+getTipo()+'\''+", marca='"+marca+'\''+", modelo='"+modelo+'\''+", precioPorDia="+precioPorDia+'}';}
}
