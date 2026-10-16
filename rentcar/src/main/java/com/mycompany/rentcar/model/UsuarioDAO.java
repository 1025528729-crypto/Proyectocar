package com.mycompany.rentcar.model;

import com.mycompany.rentcar.config.Conexion;
import com.mycompany.rentcar.util.PasswordValidator;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    // Se agregan (una sola vez) las columnas de recuperación de contraseña
    private static boolean columnasListas = false;

    public UsuarioDAO() {
        asegurarColumnasRecuperacion();
    }

    private static synchronized void asegurarColumnasRecuperacion() {
        if (columnasListas) {
            return;
        }
        String[][] columnas = {
            {"pregunta_seguridad", "VARCHAR(150) NULL"},
            {"respuesta_seguridad", "VARCHAR(100) NULL"}
        };
        try (Connection c = Conexion.getConexion()) {
            DatabaseMetaData md = c.getMetaData();
            Set<String> existentes = new HashSet<>();
            try (ResultSet rs = md.getColumns(c.getCatalog(), null, "usuarios", null)) {
                while (rs.next()) {
                    existentes.add(rs.getString("COLUMN_NAME").toLowerCase(Locale.ROOT));
                }
            }
            for (String[] col : columnas) {
                if (!existentes.contains(col[0])) {
                    try (Statement st = c.createStatement()) {
                        st.executeUpdate("ALTER TABLE usuarios ADD COLUMN " + col[0] + " " + col[1]);
                    } catch (SQLException ex) {
                        System.err.println("No se pudo agregar la columna " + col[0] + ": " + ex.getMessage());
                        return;
                    }
                }
            }
            columnasListas = true;
        } catch (SQLException ex) {
            System.err.println("No se pudieron verificar las columnas de usuarios: " + ex.getMessage());
        }
    }

    /** Normaliza la respuesta (sin tildes, minúsculas, sin espacios extra) y la convierte en hash. */
    private static String hashRespuesta(String respuesta) {
        String normal = Normalizer.normalize(respuesta == null ? "" : respuesta.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
        try {
            byte[] h = MessageDigest.getInstance("SHA-256").digest(normal.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : h) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return normal;
        }
    }

    /** Registra un cliente con su pregunta de seguridad para poder recuperar la contraseña (encriptada con BCrypt). */
    public boolean registrar(String username, String password, String pregunta, String respuesta) {
        String sql = "INSERT INTO usuarios (username, password, rol, pregunta_seguridad, respuesta_seguridad) VALUES (?, ?, 'CLIENTE', ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Se encripta la contraseña usando BCrypt antes de guardar
            String passwordEncriptada = PasswordValidator.encriptarPassword(password);
            
            ps.setString(1, username);
            ps.setString(2, passwordEncriptada);
            ps.setString(3, pregunta);
            ps.setString(4, hashRespuesta(respuesta));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    /** Devuelve la pregunta de seguridad del usuario, o null si no existe o no la configuró. */
    public String obtenerPreguntaSeguridad(String username) {
        String sql = "SELECT pregunta_seguridad FROM usuarios WHERE username = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String pregunta = rs.getString("pregunta_seguridad");
                    return (pregunta == null || pregunta.isBlank()) ? null : pregunta;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar la pregunta de seguridad: " + e.getMessage());
        }
        return null;
    }

    public boolean verificarRespuestaSeguridad(String username, String respuesta) {
        String sql = "SELECT respuesta_seguridad FROM usuarios WHERE username = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String guardada = rs.getString("respuesta_seguridad");
                    return guardada != null && !guardada.isBlank()
                            && guardada.equals(hashRespuesta(respuesta));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar la respuesta: " + e.getMessage());
        }
        return false;
    }

    public boolean cambiarPassword(String username, String nuevaPassword) {
        String sql = "UPDATE usuarios SET password = ? WHERE username = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Se encripta la nueva contraseña con BCrypt
            String passwordEncriptada = PasswordValidator.encriptarPassword(nuevaPassword);
            
            ps.setString(1, passwordEncriptada);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar la contraseña: " + e.getMessage());
            return false;
        }
    }

    public Usuario autenticar(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String passwordGuardada = rs.getString("password");
                    
                    // Soporte híbrido: verifica si la contraseña es un Hash BCrypt o texto plano
                    boolean passwordValida = false;
                    if (passwordGuardada != null && passwordGuardada.startsWith("$2a$")) {
                        passwordValida = PasswordValidator.verificarPassword(password, passwordGuardada);
                    } else {
                        passwordValida = password.equals(passwordGuardada);
                    }

                    if (passwordValida) {
                        Usuario u = new Usuario();
                        u.setId(rs.getInt("id"));
                        u.setUsername(rs.getString("username"));
                        u.setPassword(passwordGuardada);
                        u.setRol(rs.getString("rol"));
                        return u;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        return null;
    }

    // Registra un cliente nuevo en la base de datos
    public boolean registrar(String username, String password) {
        String sql = "INSERT INTO usuarios (username, password, rol) VALUES (?, ?, 'CLIENTE')";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se encripta la contraseña con BCrypt
            String passwordEncriptada = PasswordValidator.encriptarPassword(password);

            ps.setString(1, username);
            ps.setString(2, passwordEncriptada);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    // Verifica si el nombre de usuario ya está tomado
    public boolean existeUsuario(String username) {
        String sql = "SELECT id FROM usuarios WHERE username = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
            return false;
        }
    }
}