package com.mycompany.rentcar.model;

import com.mycompany.rentcar.config.Conexion;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Alquileres de vehículos (PostgreSQL). Evita reservas repetidas o con fechas cruzadas. */
public class AlquilerDAO {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static boolean tablaLista = false;

    /**
     * Estado de las reservas de todo el catálogo, visto por un usuario:
     * qué vehículos están reservados (y desde cuándo vuelven a estar libres),
     * cuáles reservó él mismo, de cuáles pidió que le avisen y de cuáles ya puede enterarse.
     */
    public static final class Estado {
        /** placa → primer día en que el vehículo queda libre (el día que termina la última reserva). */
        public final Map<String, LocalDate> libreDesde = new HashMap<>();
        /** Vehículos reservados por el propio usuario. */
        public final Set<String> mias = new HashSet<>();
        /** Vehículos de los que el usuario pidió que le avisen cuando se desocupen. */
        public final Set<String> avisos = new HashSet<>();
        /** Avisos que acaban de cumplirse: {placa, marca, modelo}. Ya se borraron de la base de datos. */
        public final List<String[]> listos = new ArrayList<>();
    }

    public AlquilerDAO() {
        asegurarTabla();
    }

    private static synchronized void asegurarTabla() {
        if (tablaLista) return;
        String sql = "CREATE TABLE IF NOT EXISTS alquileres ("
                + "id SERIAL PRIMARY KEY,"
                + "usuario_id INT NOT NULL,"
                + "placa VARCHAR(20) NOT NULL,"
                + "lugar VARCHAR(120),"
                + "fecha_recogida DATE NOT NULL,"
                + "fecha_devolucion DATE NOT NULL,"
                + "dias INT NOT NULL,"
                + "total NUMERIC(14,2) NOT NULL,"
                + "nombres VARCHAR(80) NOT NULL,"
                + "apellidos VARCHAR(80) NOT NULL,"
                + "documento VARCHAR(30) NOT NULL,"
                + "email VARCHAR(120) NOT NULL,"
                + "telefono VARCHAR(30) NOT NULL,"
                + "metodo_pago VARCHAR(20) NOT NULL,"
                + "titular VARCHAR(100),"
                + "tarjeta_ultimos4 VARCHAR(4),"
                + "estado VARCHAR(12) NOT NULL DEFAULT 'ACTIVA',"
                + "creado TIMESTAMP DEFAULT now())";
        String avisos = "CREATE TABLE IF NOT EXISTS avisos_disponibilidad ("
                + "id SERIAL PRIMARY KEY,"
                + "usuario_id INT NOT NULL,"
                + "placa VARCHAR(20) NOT NULL,"
                + "creado TIMESTAMP DEFAULT now(),"
                + "UNIQUE (usuario_id, placa))";
        try (Connection c = Conexion.getConexion(); Statement st = c.createStatement()) {
            st.executeUpdate(sql);
            st.executeUpdate(avisos);
            tablaLista = true;
        } catch (SQLException e) {
            System.err.println("No se pudo crear la tabla alquileres: " + e.getMessage());
        }
    }

    /** Devuelve null si el vehículo está libre en esas fechas, o el mensaje que se le muestra al usuario. */
    public String verificarDisponibilidad(int usuarioId, String placa, LocalDate desde, LocalDate hasta) {
        try (Connection c = Conexion.getConexion()) {
            return validar(c, usuarioId, placa, desde, hasta);
        } catch (SQLException e) {
            return "No se pudo consultar la base de datos: " + e.getMessage();
        }
    }

    private String validar(Connection c, int usuarioId, String placa, LocalDate desde, LocalDate hasta) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("SELECT disponible FROM vehiculos WHERE placa=?")) {
            p.setString(1, placa);
            try (ResultSet rs = p.executeQuery()) {
                if (!rs.next()) return "Este vehículo ya no existe en el catálogo.";
                if (!rs.getBoolean(1)) return "Este vehículo no está disponible para alquiler.";
            }
        }

        LocalDate hoy = LocalDate.now();

        // Reserva del propio usuario que se cruza con las fechas elegidas
        String propio = null;
        String sql = "SELECT fecha_recogida, fecha_devolucion FROM alquileres "
                + "WHERE placa=? AND usuario_id=? AND estado='ACTIVA' AND fecha_recogida < ? AND fecha_devolucion > ? "
                + "ORDER BY fecha_recogida";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, placa);
            p.setInt(2, usuarioId);
            p.setDate(3, Date.valueOf(hasta));
            p.setDate(4, Date.valueOf(desde));
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    propio = rs.getDate(1).toLocalDate().format(FORMATO) + " al "
                            + rs.getDate(2).toLocalDate().format(FORMATO);
                }
            }
        }
        if (propio != null) {
            return "Ya reservaste este vehículo del " + propio
                    + ". Si quieres cambiar las fechas, cancela esa reserva en «Mis reservas».";
        }

        // Mientras otra persona lo tenga reservado, el vehículo no está disponible para nadie más
        try (PreparedStatement p = c.prepareStatement(
                "SELECT MAX(fecha_devolucion) FROM alquileres "
                + "WHERE placa=? AND usuario_id<>? AND estado='ACTIVA' AND fecha_devolucion > ?")) {
            p.setString(1, placa);
            p.setInt(2, usuarioId);
            p.setDate(3, Date.valueOf(hoy));
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next() && rs.getDate(1) != null) {
                    return "Este vehículo ya está reservado por otra persona y volverá a estar disponible el "
                            + rs.getDate(1).toLocalDate().format(FORMATO)
                            + ". Pulsa «Avisarme» y te notificaremos cuando se desocupe.";
                }
            }
        }
        return null;
    }

    /** Guarda el alquiler. Devuelve null si todo salió bien, o el mensaje de error. */
    public String crear(Alquiler a) {
        try (Connection c = Conexion.getConexion()) {
            c.setAutoCommit(false);
            try {
                // Bloquea la fila del vehículo: dos personas no pueden reservarlo a la vez
                try (PreparedStatement p = c.prepareStatement("SELECT 1 FROM vehiculos WHERE placa=? FOR UPDATE")) {
                    p.setString(1, a.placa);
                    p.executeQuery().close();
                }
                String error = validar(c, a.usuarioId, a.placa, a.recogida, a.devolucion);
                if (error != null) {
                    c.rollback();
                    return error;
                }
                String sql = "INSERT INTO alquileres (usuario_id,placa,lugar,fecha_recogida,fecha_devolucion,dias,total,"
                        + "nombres,apellidos,documento,email,telefono,metodo_pago,titular,tarjeta_ultimos4) "
                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement p = c.prepareStatement(sql)) {
                    p.setInt(1, a.usuarioId);
                    p.setString(2, a.placa);
                    p.setString(3, a.lugar);
                    p.setDate(4, Date.valueOf(a.recogida));
                    p.setDate(5, Date.valueOf(a.devolucion));
                    p.setInt(6, (int) a.dias);
                    p.setBigDecimal(7, java.math.BigDecimal.valueOf(a.total));
                    p.setString(8, a.nombres);
                    p.setString(9, a.apellidos);
                    p.setString(10, a.documento);
                    p.setString(11, a.email);
                    p.setString(12, a.telefono);
                    p.setString(13, a.metodoPago);
                    p.setString(14, a.titular);
                    p.setString(15, a.ultimos4);
                    p.executeUpdate();
                }
                c.commit();
                return null;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return "No se pudo guardar la reserva: " + e.getMessage();
        }
    }

    public List<Alquiler> listarPorUsuario(int usuarioId) {
        List<Alquiler> lista = new ArrayList<>();
        String sql = "SELECT a.*, v.marca, v.modelo, v.tipo, v.foto FROM alquileres a "
                + "LEFT JOIN vehiculos v ON v.placa = a.placa WHERE a.usuario_id=? "
                + "ORDER BY (a.estado='ACTIVA') DESC, a.fecha_recogida DESC, a.id DESC";
        try (Connection c = Conexion.getConexion(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, usuarioId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    Alquiler a = new Alquiler();
                    a.id = rs.getInt("id");
                    a.usuarioId = usuarioId;
                    a.placa = rs.getString("placa");
                    a.marca = rs.getString("marca") == null ? "" : rs.getString("marca");
                    a.modelo = rs.getString("modelo") == null ? a.placa : rs.getString("modelo");
                    a.tipo = rs.getString("tipo") == null ? "AUTO" : rs.getString("tipo");
                    a.foto = rs.getString("foto");
                    a.lugar = rs.getString("lugar");
                    a.recogida = rs.getDate("fecha_recogida").toLocalDate();
                    a.devolucion = rs.getDate("fecha_devolucion").toLocalDate();
                    a.dias = rs.getInt("dias");
                    a.total = rs.getDouble("total");
                    a.nombres = rs.getString("nombres");
                    a.apellidos = rs.getString("apellidos");
                    a.documento = rs.getString("documento");
                    a.email = rs.getString("email");
                    a.telefono = rs.getString("telefono");
                    a.metodoPago = rs.getString("metodo_pago");
                    a.titular = rs.getString("titular");
                    a.ultimos4 = rs.getString("tarjeta_ultimos4");
                    a.estado = rs.getString("estado");
                    lista.add(a);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar alquileres: " + e.getMessage());
            return null;
        }
        return lista;
    }

    /** Solo el dueño de la reserva puede cancelarla. */
    public boolean cancelar(int alquilerId, int usuarioId) {
        String sql = "UPDATE alquileres SET estado='CANCELADA' WHERE id=? AND usuario_id=? AND estado='ACTIVA'";
        try (Connection c = Conexion.getConexion(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, alquilerId);
            p.setInt(2, usuarioId);
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cancelar alquiler: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // DISPONIBILIDAD Y AVISOS
    // =========================================================

    /**
     * Lee en una sola conexión qué vehículos están reservados, y si el usuario
     * (usuarioId > 0) tiene avisos pendientes. Los avisos que ya se cumplieron
     * (el vehículo quedó libre) se devuelven en {@code listos} y se borran, para
     * que se notifiquen una sola vez. Devuelve null si no hay conexión.
     */
    public Estado leerEstado(int usuarioId) {
        Estado e = new Estado();
        Date hoy = Date.valueOf(LocalDate.now());
        try (Connection c = Conexion.getConexion()) {

            String sql = "SELECT placa, MAX(fecha_devolucion), BOOL_OR(usuario_id=?) FROM alquileres "
                    + "WHERE estado='ACTIVA' AND fecha_devolucion > ? GROUP BY placa";
            try (PreparedStatement p = c.prepareStatement(sql)) {
                p.setInt(1, usuarioId);
                p.setDate(2, hoy);
                try (ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        e.libreDesde.put(rs.getString(1), rs.getDate(2).toLocalDate());
                        if (rs.getBoolean(3)) e.mias.add(rs.getString(1));
                    }
                }
            }

            if (usuarioId > 0) {
                try (PreparedStatement p = c.prepareStatement("SELECT placa FROM avisos_disponibilidad WHERE usuario_id=?")) {
                    p.setInt(1, usuarioId);
                    try (ResultSet rs = p.executeQuery()) {
                        while (rs.next()) e.avisos.add(rs.getString(1));
                    }
                }

                // Avisos cuyo vehículo ya no está reservado (y existe y está disponible)
                String listos = "SELECT a.placa, v.marca, v.modelo FROM avisos_disponibilidad a "
                        + "JOIN vehiculos v ON v.placa = a.placa "
                        + "WHERE a.usuario_id=? AND v.disponible = TRUE AND NOT EXISTS ("
                        + "SELECT 1 FROM alquileres r WHERE r.placa = a.placa AND r.estado='ACTIVA' AND r.fecha_devolucion > ?)";
                try (PreparedStatement p = c.prepareStatement(listos)) {
                    p.setInt(1, usuarioId);
                    p.setDate(2, hoy);
                    try (ResultSet rs = p.executeQuery()) {
                        while (rs.next()) {
                            e.listos.add(new String[]{rs.getString(1),
                                rs.getString(2) == null ? "" : rs.getString(2),
                                rs.getString(3) == null ? "" : rs.getString(3)});
                        }
                    }
                }
                for (String[] l : e.listos) {
                    quitarAviso(c, usuarioId, l[0]);
                    e.avisos.remove(l[0]);
                }
                // Avisos de vehículos que ya no existen
                try (PreparedStatement p = c.prepareStatement(
                        "DELETE FROM avisos_disponibilidad WHERE usuario_id=? "
                        + "AND NOT EXISTS (SELECT 1 FROM vehiculos v WHERE v.placa = avisos_disponibilidad.placa)")) {
                    p.setInt(1, usuarioId);
                    p.executeUpdate();
                }
            }
            return e;
        } catch (SQLException ex) {
            System.err.println("Error al leer la disponibilidad: " + ex.getMessage());
            return null;
        }
    }

    private static void quitarAviso(Connection c, int usuarioId, String placa) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("DELETE FROM avisos_disponibilidad WHERE usuario_id=? AND placa=?")) {
            p.setInt(1, usuarioId);
            p.setString(2, placa);
            p.executeUpdate();
        }
    }

    /** Pide que le avisen cuando el vehículo se desocupe. Devuelve null si salió bien, o el motivo del error. */
    public String pedirAviso(int usuarioId, String placa) {
        String sql = "INSERT INTO avisos_disponibilidad (usuario_id, placa) VALUES (?, ?) "
                + "ON CONFLICT (usuario_id, placa) DO NOTHING";
        try (Connection c = Conexion.getConexion(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, usuarioId);
            p.setString(2, placa);
            p.executeUpdate();
            return null;
        } catch (SQLException e) {
            return "No se pudo guardar el aviso: " + e.getMessage();
        }
    }

    /** Cancela el aviso. Devuelve null si salió bien, o el motivo del error. */
    public String quitarAviso(int usuarioId, String placa) {
        try (Connection c = Conexion.getConexion()) {
            quitarAviso(c, usuarioId, placa);
            return null;
        } catch (SQLException e) {
            return "No se pudo cancelar el aviso: " + e.getMessage();
        }
    }
}