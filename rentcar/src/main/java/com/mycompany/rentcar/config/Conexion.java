package com.mycompany.rentcar.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:postgresql://ep-bold-pine-b4dc2rc5-pooler.c-6.us-east-2.aws.neon.tech/neondb?sslmode=require";
    private static final String USER = "neondb_owner";
    // Coloca la contraseña actualizada de Neon aquí
    private static final String PASSWORD = "npg_cTQ2pJ4KtCrI";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver JDBC de PostgreSQL.");
            e.printStackTrace();
        }
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}