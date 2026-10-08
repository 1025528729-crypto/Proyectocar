package com.mycompany.rentcar.view;

import javax.swing.*;
import java.awt.*;

public class RegistroView extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegistrar;
    private JButton btnVolver;

    public RegistroView() {
        setTitle("Crear Nueva Cuenta - RentCar");
        setSize(380, 260);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel(" Nombre de Usuario:"));
        txtUsuario = new JTextField();
        add(txtUsuario);

        add(new JLabel(" Contraseña:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        add(new JLabel(" Confirmar Contraseña:"));
        txtConfirmPassword = new JPasswordField();
        add(txtConfirmPassword);

        btnVolver = new JButton("Volver");
        btnRegistrar = new JButton("Registrarse");
        
        btnRegistrar.setBackground(new Color(76, 175, 80));
        btnRegistrar.setForeground(Color.WHITE);

        add(btnVolver);
        add(btnRegistrar);
    }

    public String getUsuario() { return txtUsuario.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public String getConfirmPassword() { return new String(txtConfirmPassword.getPassword()); }
    public JButton getBtnRegistrar() { return btnRegistrar; }
    public JButton getBtnVolver() { return btnVolver; }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}