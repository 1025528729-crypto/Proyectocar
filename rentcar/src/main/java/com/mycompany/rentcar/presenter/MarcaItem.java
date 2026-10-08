package com.mycompany.rentcar.presenter;

public class MarcaItem {
private int id;
private String nombre;
private String nombreArchivoImagen; // Ejemplo: "audi.jpg", "bmw.jpg"

public MarcaItem(int id, String nombre, String nombreArchivoImagen) {
    this.id = id;
    this.nombre = nombre;
    this.nombreArchivoImagen = nombreArchivoImagen;
}

public int getId() {
    return id;
}

public String getNombre() {
    return nombre;
}

public String getNombreArchivoImagen() {
    return nombreArchivoImagen;
}

@Override
public String toString() {
    return nombre; // Esto es lo que se mostrará en el JComboBox
}
}