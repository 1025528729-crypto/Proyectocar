package com.mycompany.rentcar.view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnRegistrarse;

    public LoginView() {
        setTitle("Acceso al Sistema - Rentcar");
        setSize(380, 230);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel(" Usuario:"));
        txtUsuario = new JTextField();
        add(txtUsuario);

        add(new JLabel(" Contraseña:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        btnRegistrarse = new JButton("Registrarse");
        btnIngresar = new JButton("Iniciar Sesión");
        btnIngresar.setBackground(new Color(33, 150, 243));
        btnIngresar.setForeground(Color.WHITE);

        add(btnRegistrarse);
        add(btnIngresar);
    }

    public String getUsuario() { return txtUsuario.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public JButton getBtnIngresar() { return btnIngresar; }
    public JButton getBtnRegistrarse() { return btnRegistrarse; }
    
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}