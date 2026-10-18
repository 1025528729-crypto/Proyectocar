package com.mycompany.rentcar.presenter;

import com.mycompany.rentcar.model.Usuario;
import com.mycompany.rentcar.model.UsuarioDAO;
import com.mycompany.rentcar.util.Async;
import com.mycompany.rentcar.view.AccesoView;
import com.mycompany.rentcar.view.AppFrame;

/**
 * Lógica de la pantalla de acceso (iniciar sesión, crear cuenta y recuperar
 * contraseña). Las consultas a la base de datos se hacen en segundo plano
 * para que la ventana no se congele.
 */
public class LoginPresenter {

    private final AccesoView view;
    private final AppFrame app;

    private UsuarioDAO usuarioDAO;           // se crea la primera vez que se necesita (en segundo plano)

    private String usuarioVerificado;        // paso 2 de la recuperación
    private int intentosFallidos = 0;

    public LoginPresenter(AccesoView view, AppFrame app) {
        this.view = view;
        this.app = app;

        view.getBtnIngresar().addActionListener(e -> autenticar());
        view.getBtnRegistrar().addActionListener(e -> registrar());
        view.getBtnContinuar().addActionListener(e -> buscarUsuarioParaRecuperar());
        view.getBtnCambiar().addActionListener(e -> cambiarPassword());
    }

    private synchronized UsuarioDAO dao() {
        if (usuarioDAO == null) {
            usuarioDAO = new UsuarioDAO();
        }
        return usuarioDAO;
    }

    private void fallo(Throwable ex) {
        view.setOcupado(false);
        view.mostrarError("No se pudo conectar con la base de datos. Revisa tu internet e inténtalo de nuevo.");
        ex.printStackTrace();
    }

    // =========================================================
    // INICIAR SESIÓN
    // =========================================================

    private void autenticar() {
        String user = view.getUsuario();
        String pass = view.getPassword();

        if (user.isEmpty() || pass.isEmpty()) {
            view.mostrarError("Ingresa tu usuario y tu contraseña.");
            return;
        }

        view.limpiarMensaje();
        view.setOcupado(true);

        Async.run(() -> dao().autenticar(user, pass), usuario -> {
            view.setOcupado(false);
            if (usuario != null) {
                view.limpiarPassword();
                app.iniciarSesion(usuario);
            } else {
                view.mostrarError("Usuario o contraseña incorrectos.");
            }
        }, this::fallo);
    }

    // =========================================================
    // CREAR CUENTA
    // =========================================================

    private void registrar() {
        String user = view.getRegUsuario();
        String pass = view.getRegPassword();
        String respuesta = view.getRegRespuesta();

        if (user.isEmpty() || pass.isEmpty()) {
            view.mostrarError("Completa el usuario y la contraseña.");
            return;
        }
        if (!pass.equals(view.getRegConfirmacion())) {
            view.mostrarError("Las contraseñas no coinciden.");
            return;
        }
        if (respuesta.length() < 2) {
            view.mostrarError("Escribe la respuesta a tu pregunta de seguridad (te servirá para recuperar tu contraseña).");
            return;
        }

        String pregunta = view.getRegPregunta();

        view.limpiarMensaje();
        view.setOcupado(true);

        // Una sola tarea en segundo plano: ver si existe y, si no, crearla
        Async.run(() -> {
            if (dao().existeUsuario(user)) {
                return "EXISTE";
            }
            return dao().registrar(user, pass, pregunta, respuesta) ? "OK" : "ERROR";
        }, resultado -> {
            view.setOcupado(false);
            switch (resultado) {
                case "EXISTE" -> view.mostrarError("Ese nombre de usuario ya está registrado.");
                case "OK" -> {
                    view.limpiarRegistro();
                    view.mostrarTab(0);
                    view.setUsuario(user);
                    view.mostrarExito("¡Cuenta creada! Ya puedes iniciar sesión.");
                }
                default -> view.mostrarError("No se pudo crear la cuenta. Inténtalo de nuevo.");
            }
        }, this::fallo);
    }

    // =========================================================
    // RECUPERAR CONTRASEÑA
    // =========================================================

    private void buscarUsuarioParaRecuperar() {
        String usuario = view.getRecUsuario();

        if (usuario.isEmpty()) {
            view.mostrarError("Escribe tu nombre de usuario.");
            return;
        }

        view.limpiarMensaje();
        view.setOcupado(true);

        Async.run(() -> {
            if (!dao().existeUsuario(usuario)) {
                return "NO_EXISTE";
            }
            String pregunta = dao().obtenerPreguntaSeguridad(usuario);
            return pregunta == null ? "SIN_PREGUNTA" : "P:" + pregunta;
        }, resultado -> {
            view.setOcupado(false);
            if (resultado.equals("NO_EXISTE")) {
                view.mostrarError("No encontramos ese usuario.");
            } else if (resultado.equals("SIN_PREGUNTA")) {
                view.mostrarError("Esta cuenta no tiene pregunta de seguridad (se creó antes de esta función). "
                        + "Pide a un administrador que restablezca tu contraseña.");
            } else {
                usuarioVerificado = usuario;
                intentosFallidos = 0;
                view.mostrarPasoRespuesta(resultado.substring(2));
            }
        }, this::fallo);
    }

    private void cambiarPassword() {
        String respuesta = view.getRecRespuesta();
        String nueva = view.getRecNueva();
        String confirmacion = view.getRecConfirmacion();

        if (respuesta.isEmpty() || nueva.isEmpty() || confirmacion.isEmpty()) {
            view.mostrarError("Completa todos los campos.");
            return;
        }
        if (nueva.length() < 4) {
            view.mostrarError("La contraseña nueva debe tener al menos 4 caracteres.");
            return;
        }
        if (!nueva.equals(confirmacion)) {
            view.mostrarError("Las contraseñas no coinciden.");
            return;
        }

        final String usuario = usuarioVerificado;

        view.limpiarMensaje();
        view.setOcupado(true);

        Async.run(() -> {
            if (!dao().verificarRespuestaSeguridad(usuario, respuesta)) {
                return "RESPUESTA_MALA";
            }
            return dao().cambiarPassword(usuario, nueva) ? "OK" : "ERROR";
        }, resultado -> {
            view.setOcupado(false);
            switch (resultado) {
                case "RESPUESTA_MALA" -> {
                    intentosFallidos++;
                    if (intentosFallidos >= 5) {
                        view.reiniciarRecuperacion();
                        view.mostrarTab(0);
                        view.mostrarError("Demasiados intentos fallidos. Inténtalo de nuevo más tarde.");
                    } else {
                        view.mostrarError("La respuesta no es correcta. Intentos restantes: " + (5 - intentosFallidos) + ".");
                        view.limpiarRespuesta();
                    }
                }
                case "OK" -> {
                    view.reiniciarRecuperacion();
                    view.mostrarTab(0);
                    view.setUsuario(usuario);
                    view.mostrarExito("Contraseña actualizada. Ya puedes iniciar sesión con la nueva.");
                }
                default -> view.mostrarError("No se pudo actualizar la contraseña. Inténtalo de nuevo.");
            }
        }, this::fallo);
    }
}