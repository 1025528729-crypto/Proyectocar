package com.mycompany.rentcar.view;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.InputStream;

public class RegistroView extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegistrar;
    private JButton btnVolver;

    private Font orbitronRegular;
    private Font orbitronBold;

    private final Color FONDO = new Color(10, 10, 12);
    private final Color TARJETA = new Color(22, 22, 26);
    private final Color TARJETA2 = new Color(28, 28, 33);
    private final Color ROJO = new Color(220, 38, 38);
    private final Color ROJO_OSCURO = new Color(150, 25, 25);
    private final Color BLANCO = new Color(245, 245, 245);
    private final Color GRIS = new Color(165, 165, 170);
    private final Color BORDE = new Color(55, 55, 62);

    public RegistroView() {

        cargarFuentes();

        setTitle("RentCar - Crear Nueva Cuenta");
        setSize(560, 780);
        setMinimumSize(new Dimension(500, 720));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(FONDO);

        construirInterfaz();

        getRootPane().setDefaultButton(btnRegistrar);
    }

    private void cargarFuentes() {

        try {

            InputStream regular = getClass()
                    .getResourceAsStream("/fonts/Orbitron-Regular.ttf");

            InputStream bold = getClass()
                    .getResourceAsStream("/fonts/Orbitron-Bold.ttf");

            if (regular != null) {
                orbitronRegular = Font.createFont(
                        Font.TRUETYPE_FONT,
                        regular
                );
            }

            if (bold != null) {
                orbitronBold = Font.createFont(
                        Font.TRUETYPE_FONT,
                        bold
                );
            }

        } catch (Exception e) {

            orbitronRegular = new Font(
                    "Arial",
                    Font.PLAIN,
                    12
            );

            orbitronBold = new Font(
                    "Arial",
                    Font.BOLD,
                    12
            );
        }
    }

    private Font fuenteRegular(float tamaño) {

        if (orbitronRegular != null) {
            return orbitronRegular.deriveFont(tamaño);
        }

        return new Font(
                "Arial",
                Font.PLAIN,
                (int) tamaño
        );
    }

    private Font fuenteBold(float tamaño) {

        if (orbitronBold != null) {
            return orbitronBold.deriveFont(tamaño);
        }

        return new Font(
                "Arial",
                Font.BOLD,
                (int) tamaño
        );
    }

    private void construirInterfaz() {

        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(FONDO);

        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(
                new BoxLayout(tarjeta, BoxLayout.Y_AXIS)
        );

        tarjeta.setBackground(TARJETA);

        tarjeta.setBorder(
                new RoundedBorder(BORDE, 1, 28)
        );

        Dimension tamañoTarjeta =
                new Dimension(440, 650);

        tarjeta.setPreferredSize(tamañoTarjeta);
        tarjeta.setMinimumSize(tamañoTarjeta);
        tarjeta.setMaximumSize(tamañoTarjeta);

        // LOGO
        JLabel logo = crearLogo();

        // TÍTULO
        JLabel titulo =
                new JLabel("CREAR NUEVA CUENTA");

        titulo.setFont(fuenteBold(21));
        titulo.setForeground(BLANCO);
        titulo.setHorizontalAlignment(
                SwingConstants.CENTER
        );
        titulo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel subtitulo =
                new JLabel(
                        "<html><div style='text-align:center;'>"
                        + "Registra tus datos para acceder"
                        + "<br>"
                        + "al sistema RentCar"
                        + "</div></html>"
                );

        subtitulo.setFont(fuenteRegular(11));
        subtitulo.setForeground(GRIS);
        subtitulo.setHorizontalAlignment(
                SwingConstants.CENTER
        );
        subtitulo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        tarjeta.add(Box.createVerticalStrut(25));
        tarjeta.add(logo);
        tarjeta.add(Box.createVerticalStrut(22));
        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(subtitulo);
        tarjeta.add(Box.createVerticalStrut(28));

        // USUARIO
        JLabel lblUsuario =
                crearEtiqueta("NOMBRE DE USUARIO");

        txtUsuario =
                crearCampo("Ingresa tu nombre de usuario");

        tarjeta.add(lblUsuario);
        tarjeta.add(Box.createVerticalStrut(7));
        tarjeta.add(txtUsuario);
        tarjeta.add(Box.createVerticalStrut(20));

        // CONTRASEÑA
        JLabel lblPassword =
                crearEtiqueta("CONTRASEÑA");

        txtPassword =
                crearCampoPassword(
                        "Ingresa tu contraseña"
                );

        tarjeta.add(lblPassword);
        tarjeta.add(Box.createVerticalStrut(7));
        tarjeta.add(txtPassword);
        tarjeta.add(Box.createVerticalStrut(20));

        // CONFIRMAR CONTRASEÑA
        JLabel lblConfirmar =
                crearEtiqueta(
                        "CONFIRMAR CONTRASEÑA"
                );

        txtConfirmPassword =
                crearCampoPassword(
                        "Repite tu contraseña"
                );

        tarjeta.add(lblConfirmar);
        tarjeta.add(Box.createVerticalStrut(7));
        tarjeta.add(txtConfirmPassword);
        tarjeta.add(Box.createVerticalStrut(27));

        // REGISTRAR
        btnRegistrar =
                crearBotonPrincipal("CREAR CUENTA");

        tarjeta.add(btnRegistrar);

        tarjeta.add(Box.createVerticalStrut(13));

        // VOLVER
        btnVolver =
                crearBotonSecundario(
                        "VOLVER AL ACCESO"
                );

        tarjeta.add(btnVolver);

        tarjeta.add(Box.createVerticalGlue());

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        fondo.add(tarjeta, gbc);

        setContentPane(fondo);
    }

    private JLabel crearLogo() {

        JLabel logo = new JLabel();

        try {

            ImageIcon icono =
                    new ImageIcon(
                            getClass().getResource(
                                    "/images/rentcar_logo.png"
                            )
                    );

            Image imagen =
                    icono.getImage();

            int ancho = 185;

            int alto =
                    imagen.getHeight(null) > 0
                    ? (int) (
                            (double)
                            imagen.getHeight(null)
                            /
                            imagen.getWidth(null)
                            *
                            ancho
                    )
                    : 80;

            imagen =
                    imagen.getScaledInstance(
                            ancho,
                            alto,
                            Image.SCALE_SMOOTH
                    );

            logo.setIcon(
                    new ImageIcon(imagen)
            );

        } catch (Exception e) {

            logo.setText("RENTCAR");
            logo.setFont(
                    fuenteBold(28)
            );
            logo.setForeground(ROJO);
        }

        logo.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        logo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        return logo;
    }

    private JLabel crearEtiqueta(String texto) {

        JLabel label =
                new JLabel(texto);

        label.setFont(
                fuenteBold(10)
        );

        label.setForeground(BLANCO);

        label.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        label.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        return label;
    }

    private JTextField crearCampo(
            String placeholder) {

        JTextField campo =
                new JTextField();

        configurarCampo(
                campo,
                placeholder
        );

        return campo;
    }

    private JPasswordField crearCampoPassword(
            String placeholder) {

        JPasswordField campo =
                new JPasswordField();

        configurarCampo(
                campo,
                placeholder
        );

        return campo;
    }

    private void configurarCampo(
            JTextField campo,
            String placeholder) {

        campo.setFont(
                fuenteRegular(12)
        );

        campo.setForeground(BLANCO);
        campo.setCaretColor(ROJO);
        campo.setBackground(TARJETA2);

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(
                                BORDE,
                                1,
                                12
                        ),
                        BorderFactory.createEmptyBorder(
                                0,
                                15,
                                0,
                                15
                        )
                )
        );

        campo.setPreferredSize(
                new Dimension(350, 46)
        );

        campo.setMaximumSize(
                new Dimension(350, 46)
        );

        campo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        campo.setToolTipText(
                placeholder
        );

        campo.addFocusListener(
                new FocusAdapter() {

                    @Override
                    public void focusGained(
                            FocusEvent e) {

                        campo.setBorder(
                                BorderFactory.createCompoundBorder(
                                        new RoundedBorder(
                                                ROJO,
                                                2,
                                                12
                                        ),
                                        BorderFactory.createEmptyBorder(
                                                0,
                                                14,
                                                0,
                                                14
                                        )
                                )
                        );
                    }

                    @Override
                    public void focusLost(
                            FocusEvent e) {

                        campo.setBorder(
                                BorderFactory.createCompoundBorder(
                                        new RoundedBorder(
                                                BORDE,
                                                1,
                                                12
                                        ),
                                        BorderFactory.createEmptyBorder(
                                                0,
                                                15,
                                                0,
                                                15
                                        )
                                )
                        );
                    }
                }
        );
    }

    private JButton crearBotonPrincipal(
            String texto) {

        JButton boton =
                new JButton(texto);

        boton.setFont(
                fuenteBold(11)
        );

        boton.setForeground(Color.WHITE);
        boton.setBackground(ROJO);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.setPreferredSize(
                new Dimension(350, 47)
        );

        boton.setMaximumSize(
                new Dimension(350, 47)
        );

        boton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        boton.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e) {

                        boton.setBackground(
                                ROJO_OSCURO
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e) {

                        boton.setBackground(
                                ROJO
                        );
                    }
                }
        );

        return boton;
    }

    private JButton crearBotonSecundario(
            String texto) {

        JButton boton =
                new JButton(texto);

        boton.setFont(
                fuenteBold(10)
        );

        boton.setForeground(ROJO);
        boton.setBackground(TARJETA);
        boton.setFocusPainted(false);
        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.setBorder(
                new RoundedBorder(
                        ROJO,
                        1,
                        12
                )
        );

        boton.setPreferredSize(
                new Dimension(350, 44)
        );

        boton.setMaximumSize(
                new Dimension(350, 44)
        );

        boton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        boton.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e) {

                        boton.setBackground(
                                new Color(45, 25, 25)
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e) {

                        boton.setBackground(
                                TARJETA
                        );
                    }
                }
        );

        return boton;
    }

    public String getUsuario() {
        return txtUsuario.getText().trim();
    }

    public String getPassword() {
        return new String(
                txtPassword.getPassword()
        );
    }

    public String getConfirmPassword() {
        return new String(
                txtConfirmPassword.getPassword()
        );
    }

    public JButton getBtnRegistrar() {
        return btnRegistrar;
    }

    public JButton getBtnVolver() {
        return btnVolver;
    }

    public void mostrarMensaje(
            String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "RentCar",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static class RoundedBorder
            extends AbstractBorder {

        private final Color color;
        private final int grosor;
        private final int radio;

        public RoundedBorder(
                Color color,
                int grosor,
                int radio) {

            this.color = color;
            this.grosor = grosor;
            this.radio = radio;
        }

        @Override
        public void paintBorder(
                Component c,
                Graphics g,
                int x,
                int y,
                int width,
                int height) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);

            g2.setStroke(
                    new BasicStroke(grosor)
            );

            g2.draw(
                    new RoundRectangle2D.Double(
                            x + grosor / 2.0,
                            y + grosor / 2.0,
                            width - grosor,
                            height - grosor,
                            radio,
                            radio
                    )
            );

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(
                Component c) {

            return new Insets(
                    2,
                    2,
                    2,
                    2
            );
        }
    }
}