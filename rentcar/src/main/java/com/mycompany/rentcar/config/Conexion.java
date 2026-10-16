package com.mycompany.rentcar.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Acceso a la base de datos (Neon / PostgreSQL en la nube).
 *
 * Abrir una conexión nueva a un servidor remoto es LENTO (TCP + SSL +
 * autenticación = varios viajes de ida y vuelta, ~0.5 a 2 s). Antes se abría
 * una conexión nueva en CADA consulta. Ahora las conexiones se reutilizan:
 * el resto del código sigue igual, porque al hacer close() la conexión no se
 * cierra, solo se devuelve a la reserva.
 */
public class Conexion {

    private static final String URL = "jdbc:postgresql://ep-bold-pine-b4dc2rc5-pooler.c-6.us-east-2.aws.neon.tech/neondb?sslmode=require";
    private static final String USER = "neondb_owner";
    // Coloca la contraseña actualizada de Neon aquí
    private static final String PASSWORD = "npg_cTQ2pJ4KtCrI";

    // Si existen estas variables de entorno, tienen prioridad (así la clave
    // no tiene que quedar escrita en el código):
    //   RENTCAR_DB_URL, RENTCAR_DB_USER, RENTCAR_DB_PASSWORD

    // Conexiones abiertas disponibles para reutilizar
    private static final int MAX_LIBRES = 4;
    // Si una conexión estuvo más de este tiempo sin usarse, se comprueba que siga viva
    private static final long VALIDAR_DESPUES_DE_MS = 10_000;

    private static final Deque<Libre> LIBRES = new ArrayDeque<>();

    private static final class Libre {
        final Connection con;
        long ultimoUso;

        Libre(Connection con) {
            this.con = con;
            this.ultimoUso = System.currentTimeMillis();
        }
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver JDBC de PostgreSQL.");
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(Conexion::cerrarTodas));
    }

    public static Connection getConexion() throws SQLException {

        Libre libre;

        while (true) {

            synchronized (LIBRES) {
                libre = LIBRES.pollFirst();
            }

            if (libre == null) {
                break;
            }

            if (estaViva(libre)) {
                return envolver(libre);
            }

            cerrarSilencioso(libre.con);
        }

        return envolver(new Libre(abrirNueva()));
    }

    /** Abre la conexión en segundo plano para que ya esté lista cuando se necesite. */
    public static void precalentar() {

        Thread hilo = new Thread(() -> {
            try (Connection c = getConexion()) {
                // solo deja una conexión abierta en la reserva
            } catch (SQLException e) {
                System.err.println("No se pudo precalentar la conexión: " + e.getMessage());
            }
        }, "rentcar-db-precalentamiento");

        hilo.setDaemon(true);
        hilo.start();
    }

    // ---------------------------------------------------------

    private static Connection abrirNueva() throws SQLException {

        long inicio = System.currentTimeMillis();

        Properties props = new Properties();
        props.setProperty("user", valor("RENTCAR_DB_USER", USER));
        props.setProperty("password", valor("RENTCAR_DB_PASSWORD", PASSWORD));
        props.setProperty("connectTimeout", "15");   // segundos
        props.setProperty("socketTimeout", "60");    // segundos
        props.setProperty("tcpKeepAlive", "true");
        props.setProperty("ApplicationName", "RentCar");
        // El "pooler" de Neon (pgbouncer) funciona mejor sin sentencias preparadas en el servidor
        props.setProperty("prepareThreshold", "0");

        Connection con = DriverManager.getConnection(valor("RENTCAR_DB_URL", URL), props);

        System.out.println("[RentCar] Conexión nueva a la base de datos: "
                + (System.currentTimeMillis() - inicio) + " ms");

        return con;
    }

    private static String valor(String variable, String porDefecto) {

        String v = System.getenv(variable);

        return (v == null || v.isBlank()) ? porDefecto : v;
    }

    private static boolean estaViva(Libre libre) {

        try {
            if (libre.con.isClosed()) {
                return false;
            }

            if (System.currentTimeMillis() - libre.ultimoUso < VALIDAR_DESPUES_DE_MS) {
                return true;
            }

            return libre.con.isValid(3);

        } catch (SQLException e) {
            return false;
        }
    }

    // Entrega la conexión "disfrazada": close() la devuelve a la reserva
    private static Connection envolver(Libre libre) {

        final Connection real = libre.con;
        final AtomicBoolean devuelta = new AtomicBoolean(false);

        return (Connection) Proxy.newProxyInstance(
                Conexion.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, metodo, args) -> {

                    String nombre = metodo.getName();

                    if (nombre.equals("close")) {
                        if (devuelta.compareAndSet(false, true)) {
                            devolver(libre);
                        }
                        return null;
                    }

                    if (nombre.equals("isClosed")) {
                        return devuelta.get() || real.isClosed();
                    }

                    try {
                        return metodo.invoke(real, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                }
        );
    }

    private static void devolver(Libre libre) {

        try {
            if (libre.con.isClosed()) {
                return;
            }

            // Deja la conexión limpia para el siguiente uso
            if (!libre.con.getAutoCommit()) {
                libre.con.rollback();
                libre.con.setAutoCommit(true);
            }

            libre.ultimoUso = System.currentTimeMillis();

            synchronized (LIBRES) {
                if (LIBRES.size() < MAX_LIBRES) {
                    LIBRES.addFirst(libre);
                    return;
                }
            }

        } catch (SQLException e) {
            // se cierra abajo
        }

        cerrarSilencioso(libre.con);
    }

    private static void cerrarSilencioso(Connection con) {
        try {
            con.close();
        } catch (SQLException ignorada) {
        }
    }

    private static void cerrarTodas() {

        synchronized (LIBRES) {
            for (Libre libre : LIBRES) {
                cerrarSilencioso(libre.con);
            }
            LIBRES.clear();
        }
    }
}