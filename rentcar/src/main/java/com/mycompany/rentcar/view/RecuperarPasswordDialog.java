package com.mycompany.rentcar.view;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.InputStream;

/**
 * Ventana para recuperar la contraseña en tres pasos:
 * 1) usuario, 2) pregunta de seguridad + nueva contraseña, 3) confirmación.
 * La lógica la maneja LoginPresenter; aquí solo está la interfaz.
 */
public class RecuperarPasswordDialog extends JDialog {

    private static Color FONDO = TemaRentCar.c(242, 244, 248);
    private static Color PANEL = TemaRentCar.c(255, 255, 255);
    private static Color CAMPO = TemaRentCar.c(238, 241, 246);
    private static Color ROJO = TemaRentCar.c(220, 38, 38);
    private static Color ROJO_OSCURO = TemaRentCar.c(150, 25, 25);
    private static Color BLANCO = TemaRentCar.c(23, 27, 34);
    private static Color GRIS = TemaRentCar.c(98, 105, 118);
    private static Color BORDE = TemaRentCar.c(211, 217, 226);
    private static Color VERDE = TemaRentCar.c(52, 190, 105);

    private static final String PASO_USUARIO = "usuario";
    private static final String PASO_RESPUESTA = "respuesta";
    private static final String PASO_LISTO = "listo";

    private Font orbitronRegular;
    private Font orbitronBold;

    private final CardLayout pasos = new CardLayout();
    private final JPanel contenedorPasos = new JPanel(pasos);

    private JTextField txtUsuario;
    private JLabel lblPregunta;
    private JTextField txtRespuesta;
    private JPasswordField txtNueva;
    private JPasswordField txtConfirmar;
    private JLabel lblError1;
    private JLabel lblError2;
    private JLabel lblSubtituloExito;

    private JButton btnContinuar;
    private JButton btnCambiar;
    private JButton btnFinalizar;
    private JButton btnCancelar1;
    private JButton btnCancelar2;

    public RecuperarPasswordDialog(Window owner) {

        super(owner, "Recuperar contraseña", ModalityType.APPLICATION_MODAL);

        cargarFuentes();

        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FONDO);
        raiz.setBorder(BorderFactory.createLineBorder(BORDE, 1));

        raiz.add(crearBarra(), BorderLayout.NORTH);

        contenedorPasos.setOpaque(false);
        contenedorPasos.add(crearPasoUsuario(), PASO_USUARIO);
        contenedorPasos.add(crearPasoRespuesta(), PASO_RESPUESTA);
        contenedorPasos.add(crearPasoListo(), PASO_LISTO);
        raiz.add(contenedorPasos, BorderLayout.CENTER);

        setContentPane(raiz);
        setSize(470, 470);
        setLocationRelativeTo(owner);

        getRootPane().setDefaultButton(btnContinuar);
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        pasos.show(contenedorPasos, PASO_USUARIO);
    }

    // =========================================================
    // CONSTRUCCIÓN
    // =========================================================

    private void cargarFuentes() {
        try {
            InputStream regular = getClass().getResourceAsStream("/fonts/Orbitron-Regular.ttf");
            InputStream bold = getClass().getResourceAsStream("/fonts/Orbitron-Bold.ttf");
            if (regular != null) {
                orbitronRegular = Font.createFont(Font.TRUETYPE_FONT, regular);
            }
            if (bold != null) {
                orbitronBold = Font.createFont(Font.TRUETYPE_FONT, bold);
            }
        } catch (Exception e) {
            orbitronRegular = null;
            orbitronBold = null;
        }
    }

    private Font bold(float t) {
        return orbitronBold != null ? orbitronBold.deriveFont(t) : new Font("Arial", Font.BOLD, (int) t);
    }

    private Font regular(float t) {
        return orbitronRegular != null ? orbitronRegular.deriveFont(t) : new Font("Arial", Font.PLAIN, (int) t);
    }

    private JPanel crearBarra() {

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(TemaRentCar.c(242, 244, 248));
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ROJO));
        barra.setPreferredSize(new Dimension(10, 40));

        JLabel titulo = new JLabel("RENTCAR  ·  Recuperar contraseña");
        titulo.setForeground(BLANCO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        barra.add(titulo, BorderLayout.CENTER);

        JLabel cerrar = new JLabel("×", SwingConstants.CENTER);
        cerrar.setOpaque(true);
        cerrar.setBackground(TemaRentCar.c(242, 244, 248));
        cerrar.setForeground(BLANCO);
        cerrar.setFont(new Font("SansSerif", Font.PLAIN, 22));
        cerrar.setPreferredSize(new Dimension(46, 38));
        cerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cerrar.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { dispose(); }
            @Override public void mouseEntered(MouseEvent e) { cerrar.setBackground(TemaRentCar.c(200, 30, 30)); }
            @Override public void mouseExited(MouseEvent e) { cerrar.setBackground(TemaRentCar.c(242, 244, 248)); }
        });
        barra.add(cerrar, BorderLayout.EAST);

        // Permite arrastrar la ventana desde la barra
        final Point[] inicio = new Point[1];
        MouseAdapter arrastre = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { inicio[0] = e.getPoint(); }
            @Override public void mouseDragged(MouseEvent e) {
                if (inicio[0] != null) {
                    Point p = getLocation();
                    setLocation(p.x + e.getX() - inicio[0].x, p.y + e.getY() - inicio[0].y);
                }
            }
        };
        titulo.addMouseListener(arrastre);
        titulo.addMouseMotionListener(arrastre);

        return barra;
    }

    private JPanel nuevoPaso() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(PANEL);
        p.setBorder(BorderFactory.createEmptyBorder(26, 40, 26, 40));
        return p;
    }

    private JLabel texto(String t, Font f, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setForeground(c);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    private JLabel etiqueta(String t) {
        JLabel l = texto(t, bold(10), BLANCO);
        return l;
    }

    private JLabel crearError() {
        JLabel l = texto(" ", new Font("SansSerif", Font.PLAIN, 12), TemaRentCar.c(255, 110, 110));
        l.setMaximumSize(new Dimension(390, 36));
        return l;
    }

    private JPanel crearPasoUsuario() {

        JPanel p = nuevoPaso();

        p.add(texto("¿OLVIDASTE TU CONTRASEÑA?", bold(15), BLANCO));
        p.add(Box.createVerticalStrut(10));
        p.add(texto("<html><div style='text-align:center;width:300px'>Escribe tu nombre de usuario y te haremos tu pregunta de seguridad.</div></html>",
                new Font("SansSerif", Font.PLAIN, 12), GRIS));
        p.add(Box.createVerticalStrut(30));

        p.add(etiqueta("NOMBRE DE USUARIO"));
        p.add(Box.createVerticalStrut(8));
        txtUsuario = crearCampo(new JTextField());
        p.add(txtUsuario);
        p.add(Box.createVerticalStrut(10));

        lblError1 = crearError();
        p.add(lblError1);
        p.add(Box.createVerticalGlue());

        btnContinuar = crearBotonPrimario("CONTINUAR");
        p.add(btnContinuar);
        p.add(Box.createVerticalStrut(12));
        btnCancelar1 = crearBotonSecundario("CANCELAR");
        btnCancelar1.addActionListener(e -> dispose());
        p.add(btnCancelar1);

        return p;
    }

    private JPanel crearPasoRespuesta() {

        JPanel p = nuevoPaso();
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 22, 40));

        p.add(texto("PREGUNTA DE SEGURIDAD", bold(15), BLANCO));
        p.add(Box.createVerticalStrut(12));

        lblPregunta = new JLabel(" ", SwingConstants.CENTER);
        lblPregunta.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblPregunta.setForeground(TemaRentCar.c(255, 205, 0));
        lblPregunta.setOpaque(true);
        lblPregunta.setBackground(CAMPO);
        lblPregunta.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDE, 1, 12),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        lblPregunta.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPregunta.setMaximumSize(new Dimension(390, 56));
        p.add(lblPregunta);
        p.add(Box.createVerticalStrut(16));

        p.add(etiqueta("TU RESPUESTA"));
        p.add(Box.createVerticalStrut(6));
        txtRespuesta = crearCampo(new JTextField());
        p.add(txtRespuesta);
        p.add(Box.createVerticalStrut(12));

        p.add(etiqueta("NUEVA CONTRASEÑA"));
        p.add(Box.createVerticalStrut(6));
        txtNueva = crearCampo(new JPasswordField());
        p.add(txtNueva);
        p.add(Box.createVerticalStrut(12));

        p.add(etiqueta("CONFIRMAR CONTRASEÑA"));
        p.add(Box.createVerticalStrut(6));
        txtConfirmar = crearCampo(new JPasswordField());
        p.add(txtConfirmar);
        p.add(Box.createVerticalStrut(6));

        lblError2 = crearError();
        p.add(lblError2);
        p.add(Box.createVerticalGlue());

        btnCambiar = crearBotonPrimario("CAMBIAR CONTRASEÑA");
        p.add(btnCambiar);
        p.add(Box.createVerticalStrut(10));
        btnCancelar2 = crearBotonSecundario("CANCELAR");
        btnCancelar2.addActionListener(e -> dispose());
        p.add(btnCancelar2);

        return p;
    }

    private JPanel crearPasoListo() {

        JPanel p = nuevoPaso();

        p.add(Box.createVerticalGlue());

        JLabel check = new JLabel("✓", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TemaRentCar.c(52, 190, 105, 40));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(VERDE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        check.setFont(new Font("SansSerif", Font.BOLD, 40));
        check.setForeground(VERDE);
        check.setOpaque(false);
        check.setPreferredSize(new Dimension(84, 84));
        check.setMaximumSize(new Dimension(84, 84));
        check.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(check);
        p.add(Box.createVerticalStrut(24));

        p.add(texto("¡CONTRASEÑA ACTUALIZADA!", bold(16), BLANCO));
        p.add(Box.createVerticalStrut(12));
        lblSubtituloExito = texto(" ", new Font("SansSerif", Font.PLAIN, 13), GRIS);
        p.add(lblSubtituloExito);

        p.add(Box.createVerticalGlue());

        btnFinalizar = crearBotonPrimario("INICIAR SESIÓN");
        btnFinalizar.addActionListener(e -> dispose());
        p.add(btnFinalizar);

        return p;
    }

    private <T extends JTextField> T crearCampo(T campo) {

        campo.setFont(regular(13));
        campo.setForeground(BLANCO);
        campo.setCaretColor(ROJO);
        campo.setBackground(CAMPO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDE, 1, 12),
                BorderFactory.createEmptyBorder(0, 15, 0, 15)));
        campo.setPreferredSize(new Dimension(390, 44));
        campo.setMaximumSize(new Dimension(390, 44));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(ROJO, 2, 12),
                        BorderFactory.createEmptyBorder(0, 14, 0, 14)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(BORDE, 1, 12),
                        BorderFactory.createEmptyBorder(0, 15, 0, 15)));
            }
        });
        return campo;
    }

    private JButton crearBotonPrimario(String t) {

        JButton b = new JButton(t);
        b.setFont(bold(12));
        b.setForeground(Color.WHITE);
        b.setBackground(ROJO);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(390, 46));
        b.setMaximumSize(new Dimension(390, 46));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(ROJO_OSCURO); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(ROJO); }
        });
        return b;
    }

    private JButton crearBotonSecundario(String t) {

        JButton b = new JButton(t);
        b.setFont(bold(11));
        b.setForeground(ROJO);
        b.setBackground(PANEL);
        b.setFocusPainted(false);
        b.setBorder(new RoundedBorder(ROJO, 1, 12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(390, 42));
        b.setMaximumSize(new Dimension(390, 42));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(TemaRentCar.c(253, 238, 238)); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(PANEL); }
        });
        return b;
    }

    // =========================================================
    // API PARA EL PRESENTER
    // =========================================================

    public JButton getBtnContinuar() { return btnContinuar; }
    public JButton getBtnCambiar() { return btnCambiar; }

    public String getUsuarioIngresado() { return txtUsuario.getText().trim(); }
    public String getRespuesta() { return txtRespuesta.getText().trim(); }
    public String getNuevaPassword() { return new String(txtNueva.getPassword()); }
    public String getConfirmacion() { return new String(txtConfirmar.getPassword()); }

    private boolean enPasoRespuesta = false;

    public void mostrarError(String mensaje) {
        JLabel destino = enPasoRespuesta ? lblError2 : lblError1;
        destino.setText("<html><div style='text-align:center;width:340px'>" + mensaje + "</div></html>");
    }

    public void mostrarPasoRespuesta(String pregunta) {
        lblPregunta.setText("<html><div style='text-align:center;width:330px'>" + pregunta + "</div></html>");
        lblError1.setText(" ");
        lblError2.setText(" ");
        enPasoRespuesta = true;
        pasos.show(contenedorPasos, PASO_RESPUESTA);
        getRootPane().setDefaultButton(btnCambiar);
        txtRespuesta.requestFocusInWindow();
    }

    public void mostrarPasoListo(String usuario) {
        lblSubtituloExito.setText("<html><div style='text-align:center;width:330px'>Ya puedes iniciar sesión con tu nueva contraseña, <b>"
                + usuario + "</b>.</div></html>");
        enPasoRespuesta = false;
        pasos.show(contenedorPasos, PASO_LISTO);
        getRootPane().setDefaultButton(btnFinalizar);
    }

    public void limpiarRespuesta() {
        txtRespuesta.setText("");
        txtRespuesta.requestFocusInWindow();
    }

    private static class RoundedBorder extends AbstractBorder {

        private final Color color;
        private final int grosor;
        private final int radio;

        RoundedBorder(Color color, int grosor, int radio) {
            this.color = color;
            this.grosor = grosor;
            this.radio = radio;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(grosor));
            g2.draw(new RoundRectangle2D.Double(x + grosor / 2.0, y + grosor / 2.0,
                    width - grosor, height - grosor, radio, radio));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }
    }

    /** Vuelve a calcular la paleta cuando cambia el tema claro/oscuro. */
    static void refrescarPaleta() {
        FONDO = TemaRentCar.c(242, 244, 248);
        PANEL = TemaRentCar.c(255, 255, 255);
        CAMPO = TemaRentCar.c(238, 241, 246);
        ROJO = TemaRentCar.c(220, 38, 38);
        ROJO_OSCURO = TemaRentCar.c(150, 25, 25);
        BLANCO = TemaRentCar.c(23, 27, 34);
        GRIS = TemaRentCar.c(98, 105, 118);
        BORDE = TemaRentCar.c(211, 217, 226);
        VERDE = TemaRentCar.c(52, 190, 105);
    }
}