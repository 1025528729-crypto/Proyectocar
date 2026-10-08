package com.mycompany.rentcar.presenter;

import com.mycompany.rentcar.model.Usuario;
import com.mycompany.rentcar.model.UsuarioDAO;
import com.mycompany.rentcar.view.LoginView;
import com.mycompany.rentcar.view.RegistroView;
import com.mycompany.rentcar.view.VehiculoView;

public class LoginPresenter {
    private LoginView view;
    private UsuarioDAO usuarioDAO;

    public LoginPresenter(LoginView view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
        
        this.view.getBtnIngresar().addActionListener(e -> autenticar());
        this.view.getBtnRegistrarse().addActionListener(e -> abrirRegistro());
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
            view.mostrarMensaje("¡Bienvenido " + usuario.getUsername() + "! Rol: " + usuario.getRol());
            view.dispose(); // Cierra ventana de login
            
            VehiculoView vehiculoView = new VehiculoView();
            new VehiculoPresenter(vehiculoView, usuario);
            vehiculoView.setVisible(true);
        } else {
            view.mostrarMensaje("Usuario o contraseña incorrectos.");
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

            if (usuarioDAO.registrar(user, pass)) {
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