package com.mycompany.rentcar.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    // Configuración para MySQL en XAMPP (por defecto el usuario es "root" sin contraseña)
    private static final String URL = "jdbc:mysql://localhost:3306/rentcar_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}