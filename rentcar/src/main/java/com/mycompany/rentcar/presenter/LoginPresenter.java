package com.mycompany.rentcar.presenter;

import com.mycompany.rentcar.model.Usuario;
import com.mycompany.rentcar.model.UsuarioDAO;
import com.mycompany.rentcar.view.LoginView;
import com.mycompany.rentcar.view.RecuperarPasswordDialog;
import com.mycompany.rentcar.view.RegistroView;
import com.mycompany.rentcar.view.VehiculoView;

public class LoginPresenter {
    private LoginView view;
    private UsuarioDAO usuarioDAO;
    private Runnable onLoginExitoso;

    public LoginPresenter(LoginView view) {
        this(view, null);
    }

    public LoginPresenter(LoginView view, Runnable onLoginExitoso) {
        this.view = view;
        this.onLoginExitoso = onLoginExitoso;
        this.usuarioDAO = new UsuarioDAO();
        
        this.view.getBtnIngresar().addActionListener(e -> autenticar());
        this.view.getBtnRegistrarse().addActionListener(e -> abrirRegistro());
        this.view.getBtnOlvide().addActionListener(e -> abrirRecuperacion());
    }

    private void autenticar() {
        String user = view.getUsuario();
        String pass = view.getPassword();

        if (user.isEmpty() || pass.isEmpty()) {
            view.mostrarMensaje("Por favor, ingrese usuario y contraseña.");
            return;
        }

        Usuario usuario = usuarioDAO.autenticar(user, pass);

        if (usuario != null) {
            view.dispose(); // Cierra ventana de login sin mostrar mensaje emergente
            if (onLoginExitoso != null) {
                onLoginExitoso.run(); // cierra el catálogo solo ahora que el login fue exitoso
            }
            
            VehiculoView vehiculoView = new VehiculoView();
            new VehiculoPresenter(vehiculoView, usuario);
            vehiculoView.setVisible(true);
        } else {
            view.mostrarMensaje("Usuario o contraseña incorrectos.");
        }
    }

    // =========================================================
    // RECUPERAR CONTRASEÑA (pregunta de seguridad)
    // =========================================================

    private void abrirRecuperacion() {

        RecuperarPasswordDialog dialogo = new RecuperarPasswordDialog(view);
        final String[] usuarioVerificado = {null};
        final int[] intentos = {0};
        final boolean[] cambiada = {false};

        // Paso 1: buscar el usuario y mostrar su pregunta
        dialogo.getBtnContinuar().addActionListener(e -> {
            String usuario = dialogo.getUsuarioIngresado();

            if (usuario.isEmpty()) {
                dialogo.mostrarError("Escribe tu nombre de usuario.");
                return;
            }
            if (!usuarioDAO.existeUsuario(usuario)) {
                dialogo.mostrarError("No encontramos ese usuario.");
                return;
            }

            String pregunta = usuarioDAO.obtenerPreguntaSeguridad(usuario);
            if (pregunta == null) {
                dialogo.mostrarError("Esta cuenta no tiene pregunta de seguridad (se creó antes de esta función). "
                        + "Pide a un administrador que restablezca tu contraseña.");
                return;
            }

            usuarioVerificado[0] = usuario;
            dialogo.mostrarPasoRespuesta(pregunta);
        });

        // Paso 2: validar respuesta y guardar la nueva contraseña
        dialogo.getBtnCambiar().addActionListener(e -> {
            String respuesta = dialogo.getRespuesta();
            String nueva = dialogo.getNuevaPassword();
            String confirmacion = dialogo.getConfirmacion();

            if (respuesta.isEmpty() || nueva.isEmpty() || confirmacion.isEmpty()) {
                dialogo.mostrarError("Completa todos los campos.");
                return;
            }
            if (nueva.length() < 4) {
                dialogo.mostrarError("La nueva contraseña debe tener al menos 4 caracteres.");
                return;
            }
            if (!nueva.equals(confirmacion)) {
                dialogo.mostrarError("Las contraseñas no coinciden.");
                return;
            }

            if (!usuarioDAO.verificarRespuestaSeguridad(usuarioVerificado[0], respuesta)) {
                intentos[0]++;
                if (intentos[0] >= 5) {
                    dialogo.dispose();
                    view.mostrarMensaje("Demasiados intentos fallidos. Inténtalo de nuevo más tarde.");
                    return;
                }
                dialogo.mostrarError("La respuesta no es correcta. Intentos restantes: " + (5 - intentos[0]) + ".");
                dialogo.limpiarRespuesta();
                return;
            }

            if (usuarioDAO.cambiarPassword(usuarioVerificado[0], nueva)) {
                cambiada[0] = true;
                dialogo.mostrarPasoListo(usuarioVerificado[0]);
            } else {
                dialogo.mostrarError("No se pudo actualizar la contraseña. Intenta de nuevo.");
            }
        });

        dialogo.setVisible(true); // modal: continúa cuando se cierra

        // Si se cambió la contraseña, el usuario queda escrito en el login
        if (cambiada[0]) {
            view.setUsuario(usuarioVerificado[0]);
        }
    }

    private void abrirRegistro() {
        RegistroView registroView = new RegistroView();
        
        registroView.getBtnRegistrar().addActionListener(e -> {
            String user = registroView.getUsuario();
            String pass = registroView.getPassword();
            String confirmPass = registroView.getConfirmPassword();

            if (user.isEmpty() || pass.isEmpty()) {
                registroView.mostrarMensaje("Por favor complete todos los campos.");
                return;
            }

            if (!pass.equals(confirmPass)) {
                registroView.mostrarMensaje("Las contraseñas no coinciden.");
                return;
            }

            if (usuarioDAO.existeUsuario(user)) {
                registroView.mostrarMensaje("El nombre de usuario ya está registrado.");
                return;
            }

            String respuesta = registroView.getRespuesta();
            if (respuesta.length() < 2) {
                registroView.mostrarMensaje("Escribe la respuesta a tu pregunta de seguridad (te servirá para recuperar tu contraseña).");
                return;
            }

            if (usuarioDAO.registrar(user, pass, registroView.getPregunta(), respuesta)) {
                registroView.mostrarMensaje("¡Registro exitoso! Ya puedes iniciar sesión.");
                registroView.dispose();
            } else {
                registroView.mostrarMensaje("Error al crear la cuenta. Intente nuevamente.");
            }
        });

        registroView.getBtnVolver().addActionListener(e -> registroView.dispose());
        registroView.setVisible(true);
    }
}