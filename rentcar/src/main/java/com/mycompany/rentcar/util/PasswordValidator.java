
package com.mycompany.rentcar.util;

import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordValidator {

    // Expresiones regulares para los criterios
    private static final Pattern MAYUSCULA = Pattern.compile("[A-Z]");
    private static final Pattern MINUSCULA = Pattern.compile("[a-z]");
    private static final Pattern NUMERO = Pattern.compile("[0-9]");
    private static final Pattern ESPECIAL = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

    public enum NivelSeguridad {
        MUY_DEBIL("Muy Débil", "#FF3B30", 10),
        DEBIL("Débil", "#FF9500", 35),
        MEDIA("Media", "#FFCC00", 65),
        FUERTE("Fuerte", "#34C759", 100);

        private final String etiqueta;
        private final String colorHex;
        private final int porcentaje;

        NivelSeguridad(String etiqueta, String colorHex, int porcentaje) {
            this.etiqueta = etiqueta;
            this.colorHex = colorHex;
            this.porcentaje = porcentaje;
        }

        public String getEtiqueta() { return etiqueta; }
        public String getColorHex() { return colorHex; }
        public int getPorcentaje() { return porcentaje; }
    }

    /**
     * Evalúa la fortaleza de la contraseña ingresada.
     */
    public static NivelSeguridad evaluarSeguridad(String password) {
        if (password == null || password.isEmpty()) {
            return NivelSeguridad.MUY_DEBIL;
        }

        int puntos = 0;

        // Criterio 1: Longitud
        if (password.length() >= 8) puntos += 25;
        if (password.length() >= 12) puntos += 15;

        // Criterio 2: Tipos de caracteres
        if (MAYUSCULA.matcher(password).find()) puntos += 15;
        if (MINUSCULA.matcher(password).find()) puntos += 15;
        if (NUMERO.matcher(password).find()) puntos += 15;
        if (ESPECIAL.matcher(password).find()) puntos += 15;

        // Clasificación
        if (puntos < 40) return NivelSeguridad.MUY_DEBIL;
        if (puntos < 65) return NivelSeguridad.DEBIL;
        if (puntos < 85) return NivelSeguridad.MEDIA;
        return NivelSeguridad.FUERTE;
    }

    /**
     * Verifica si la contraseña cumple con los requisitos mínimos de registro.
     */
    public static boolean esValida(String password) {
        if (password == null || password.length() < 8) return false;
        return MAYUSCULA.matcher(password).find()
                && MINUSCULA.matcher(password).find()
                && NUMERO.matcher(password).find()
                && ESPECIAL.matcher(password).find();
    }

    // ==========================================
    // MÉTODOS DE BCRYPT PARA PERSISTENCIA EN BD
    // ==========================================

    /**
     * Encripta la contraseña usando el algoritmo BCrypt con un costo de sal de 12.
     */
    public static String encriptarPassword(String passwordPlana) {
        return BCrypt.hashpw(passwordPlana, BCrypt.gensalt(12));
    }

    /**
     * Verifica si la contraseña ingresada coincide con el hash almacenado en la BD.
     */
    public static boolean verificarPassword(String passwordIngresada, String hashBD) {
        if (hashBD == null || !hashBD.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(passwordIngresada, hashBD);
    }
}