package com.mycompany.rentcar.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Pantalla de acceso con tres pestañas: Iniciar sesión, Crear cuenta y
 * Recuperar contraseña. Reemplaza a las tres ventanas anteriores.
 */
public class AccesoView extends JPanel {

    public static final String[] PREGUNTAS = {
        "¿Cuál es el nombre de tu primera mascota?",
        "¿En qué ciudad naciste?",
        "¿Cuál es tu comida favorita?",
        "¿Cómo se llama tu mejor amigo de la infancia?",
        "¿Cuál fue tu primer colegio?"
    };

    private static Color VERDE = TemaRentCar.c(22, 163, 74);
    private static final String[] NOMBRES = {"Iniciar sesión", "Crear cuenta", "Recuperar"};

    private final CardLayout cartas = new CardLayout();
    private final JPanel formularios = new JPanel(cartas) {
        // La tarjeta se ajusta al formulario que se está viendo (no al más alto)
        @Override public Dimension getPreferredSize() {
            for (Component c : getComponents()) {
                if (c.isVisible()) return c.getPreferredSize();
            }
            return super.getPreferredSize();
        }
    };
    private final Segmento[] segmentos = new Segmento[3];
    private final JLabel lblMensaje = new JLabel(" ");
    private int tabActual = 0;

    // Iniciar sesión
    private final JTextField txtUsuario = campoTexto();
    private final JPasswordField txtPassword = campoClave();
    private final JButton btnIngresar = botonPrincipal("Ingresar");

    // Crear cuenta
    private final JTextField txtRegUsuario = campoTexto();
    private final JPasswordField txtRegPassword = campoClave();
    private final JPasswordField txtRegConfirmacion = campoClave();
    private final JComboBox<String> comboPregunta = new JComboBox<>(PREGUNTAS);
    private final JTextField txtRegRespuesta = campoTexto();
    private final JButton btnRegistrar = botonPrincipal("Crear mi cuenta");

    // Recuperar contraseña
    private final CardLayout pasos = new CardLayout();
    private final JPanel panelPasos = new JPanel(pasos) {
        @Override public Dimension getPreferredSize() {
            for (Component c : getComponents()) {
                if (c.isVisible()) return c.getPreferredSize();
            }
            return super.getPreferredSize();
        }
    };
    private final JTextField txtRecUsuario = campoTexto();
    private final JButton btnContinuar = botonPrincipal("Continuar");
    private final JLabel lblPregunta = new JLabel(" ");
    private final JTextField txtRecRespuesta = campoTexto();
    private final JPasswordField txtRecNueva = campoClave();
    private final JPasswordField txtRecConfirmacion = campoClave();
    private final JButton btnCambiar = botonPrincipal("Cambiar contraseña");

    public AccesoView() {
        super(new GridBagLayout());
        setBackground(TemaRentCar.FONDO);

        JPanel tarjeta = new JPanel() {
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(440, d.height + 16);
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i = 8; i >= 1; i--) {            // sombra suave
                    g2.setColor(TemaRentCar.c(20, 30, 50, 5));
                    g2.fillRoundRect(8 - i, 10 - i, getWidth() - 16 + 2 * i, getHeight() - 16 + 2 * i, 28, 28);
                }
                g2.setColor(TemaRentCar.PANEL);
                g2.fillRoundRect(8, 4, getWidth() - 16, getHeight() - 16, 22, 22);
                g2.setColor(TemaRentCar.BORDE);
                g2.drawRoundRect(8, 4, getWidth() - 17, getHeight() - 17, 22, 22);
                g2.dispose();
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(26, 34, 32, 34));

        JLabel titulo = new JLabel("Bienvenido a RentCar");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(TemaRentCar.TEXTO);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitulo = new JLabel("Entra a tu cuenta o crea una nueva.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setForeground(TemaRentCar.GRIS);
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);

        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(4));
        tarjeta.add(subtitulo);
        tarjeta.add(Box.createVerticalStrut(20));
        tarjeta.add(crearSegmentos());
        tarjeta.add(Box.createVerticalStrut(20));

        formularios.setOpaque(false);
        formularios.setAlignmentX(LEFT_ALIGNMENT);
        formularios.add(crearFormularioLogin(), "0");
        formularios.add(crearFormularioRegistro(), "1");
        formularios.add(crearFormularioRecuperar(), "2");
        tarjeta.add(formularios);

        tarjeta.add(Box.createVerticalStrut(8));
        lblMensaje.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblMensaje.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.add(lblMensaje);

        add(tarjeta);

        mostrarTab(0);
    }

    // =========================================================
    // CONSTRUCCIÓN
    // =========================================================

    private JPanel crearSegmentos() {
        JPanel fila = new JPanel(new GridLayout(1, 3, 6, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TemaRentCar.CAMPO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        for (int i = 0; i < 3; i++) {
            final int indice = i;
            segmentos[i] = new Segmento(NOMBRES[i]);
            segmentos[i].addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { mostrarTab(indice); limpiarMensaje(); }
            });
            fila.add(segmentos[i]);
        }
        return fila;
    }

    private JPanel crearFormularioLogin() {
        JPanel p = formulario();
        p.add(fila("Usuario", txtUsuario));
        p.add(fila("Contraseña", txtPassword));
        p.add(Box.createVerticalStrut(6));
        p.add(btnIngresar);
        p.add(Box.createVerticalStrut(14));

        JPanel enlaces = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        enlaces.setOpaque(false);
        enlaces.setAlignmentX(LEFT_ALIGNMENT);
        enlaces.add(enlace("¿Olvidaste tu contraseña?", 2));
        enlaces.add(new JLabel("   ·   "));
        enlaces.add(enlace("Crear una cuenta", 1));
        p.add(enlaces);

        txtUsuario.addActionListener(e -> txtPassword.requestFocusInWindow());
        txtPassword.addActionListener(e -> btnIngresar.doClick());
        return p;
    }

    private JPanel crearFormularioRegistro() {
        JPanel p = formulario();
        p.add(fila("Usuario", txtRegUsuario));
        p.add(fila("Contraseña", txtRegPassword));
        p.add(fila("Confirmar contraseña", txtRegConfirmacion));
        estilizarCombo(comboPregunta);
        p.add(fila("Pregunta de seguridad (para recuperar tu contraseña)", comboPregunta));
        p.add(fila("Tu respuesta", txtRegRespuesta));
        p.add(Box.createVerticalStrut(6));
        p.add(btnRegistrar);
        txtRegRespuesta.addActionListener(e -> btnRegistrar.doClick());
        return p;
    }

    private JPanel crearFormularioRecuperar() {
        panelPasos.setOpaque(false);
        panelPasos.setAlignmentX(LEFT_ALIGNMENT);

        JPanel paso1 = formulario();
        JLabel ayuda = new JLabel("<html>Escribe tu usuario y responde tu pregunta de seguridad para elegir una contraseña nueva.</html>");
        ayuda.setFont(new Font("SansSerif", Font.PLAIN, 12));
        ayuda.setForeground(TemaRentCar.GRIS);
        ayuda.setAlignmentX(LEFT_ALIGNMENT);
        paso1.add(ayuda);
        paso1.add(Box.createVerticalStrut(12));
        paso1.add(fila("Usuario", txtRecUsuario));
        paso1.add(Box.createVerticalStrut(6));
        paso1.add(btnContinuar);
        txtRecUsuario.addActionListener(e -> btnContinuar.doClick());

        JPanel paso2 = formulario();
        lblPregunta.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblPregunta.setForeground(TemaRentCar.TEXTO);
        lblPregunta.setAlignmentX(LEFT_ALIGNMENT);
        paso2.add(lblPregunta);
        paso2.add(Box.createVerticalStrut(10));
        paso2.add(fila("Tu respuesta", txtRecRespuesta));
        paso2.add(fila("Contraseña nueva", txtRecNueva));
        paso2.add(fila("Confirmar contraseña nueva", txtRecConfirmacion));
        paso2.add(Box.createVerticalStrut(6));
        paso2.add(btnCambiar);
        txtRecConfirmacion.addActionListener(e -> btnCambiar.doClick());

        panelPasos.add(paso1, "1");
        panelPasos.add(paso2, "2");
        return panelPasos;
    }

    private static JPanel formulario() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(LEFT_ALIGNMENT);
        return p;
    }

    private static JPanel fila(String etiqueta, JComponent campo) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(LEFT_ALIGNMENT);
        JLabel l = new JLabel(etiqueta);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(TemaRentCar.GRIS);
        l.setAlignmentX(LEFT_ALIGNMENT);
        campo.setAlignmentX(LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        campo.setPreferredSize(new Dimension(300, 40));
        p.add(l);
        p.add(Box.createVerticalStrut(5));
        p.add(campo);
        p.add(Box.createVerticalStrut(12));
        return p;
    }

    private JLabel enlace(String texto, int destino) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(TemaRentCar.ROJO);
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { mostrarTab(destino); limpiarMensaje(); }
            @Override public void mouseEntered(MouseEvent e) { l.setForeground(TemaRentCar.ROJO_OSCURO); }
            @Override public void mouseExited(MouseEvent e) { l.setForeground(TemaRentCar.ROJO); }
        });
        return l;
    }

    // =========================================================
    // ESTILO DE CAMPOS Y BOTONES
    // =========================================================

    private static Border borde(Color c) {
        return BorderFactory.createCompoundBorder(new BordeRedondo(c, 1, 12),
                BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    private static void estilizar(JTextComponent campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setForeground(TemaRentCar.TEXTO);
        campo.setCaretColor(TemaRentCar.TEXTO);
        campo.setBackground(TemaRentCar.CAMPO);
        campo.setBorder(borde(TemaRentCar.BORDE));
        campo.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { campo.setBorder(borde(TemaRentCar.ROJO)); }
            @Override public void focusLost(FocusEvent e) { campo.setBorder(borde(TemaRentCar.BORDE)); }
        });
    }

    private static JTextField campoTexto() {
        JTextField c = new JTextField();
        estilizar(c);
        return c;
    }

    private static JPasswordField campoClave() {
        JPasswordField c = new JPasswordField();
        estilizar(c);
        return c;
    }

    private static void estilizarCombo(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setBackground(TemaRentCar.CAMPO);
        combo.setForeground(TemaRentCar.TEXTO);
    }

    private static JButton botonPrincipal(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(TemaRentCar.ROJO);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (b.isEnabled()) b.setBackground(TemaRentCar.c(240, 60, 60)); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(TemaRentCar.ROJO); }
        });
        return b;
    }

    /** Pestaña tipo "píldora" de la parte superior de la tarjeta. */
    private static final class Segmento extends JComponent {
        private final String texto;
        private boolean activo;

        Segmento(String texto) {
            this.texto = texto;
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void setActivo(boolean a) { activo = a; repaint(); }

        @Override public Dimension getPreferredSize() { return new Dimension(120, 36); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            if (activo) {
                g2.setColor(TemaRentCar.CAMPO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(TemaRentCar.BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
            g2.setFont(getFont());
            g2.setColor(activo ? TemaRentCar.ROJO : TemaRentCar.GRIS);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(texto, (getWidth() - fm.stringWidth(texto)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    // =========================================================
    // API PARA EL PRESENTADOR
    // =========================================================

    public void mostrarTab(int indice) {
        javax.swing.SwingUtilities.invokeLater(() -> { revalidate(); repaint(); });
        tabActual = indice;
        cartas.show(formularios, String.valueOf(indice));
        for (int i = 0; i < segmentos.length; i++) {
            segmentos[i].setActivo(i == indice);
        }
        SwingUtilities.invokeLater(() -> {
            if (indice == 0) txtUsuario.requestFocusInWindow();
            else if (indice == 1) txtRegUsuario.requestFocusInWindow();
            else txtRecUsuario.requestFocusInWindow();
        });
    }

    /** Se llama cada vez que se entra a esta pantalla. */
    public void alMostrar() {
        limpiarMensaje();
        txtPassword.setText("");
        mostrarTab(tabActual == 2 ? 2 : 0);
    }

    public void mostrarError(String texto) {
        lblMensaje.setForeground(TemaRentCar.ROJO);
        lblMensaje.setText("<html>" + texto + "</html>");
    }

    public void mostrarExito(String texto) {
        lblMensaje.setForeground(VERDE);
        lblMensaje.setText("<html>" + texto + "</html>");
    }

    public void limpiarMensaje() {
        lblMensaje.setText(" ");
    }

    /** Bloquea los botones mientras se consulta la base de datos. */
    public void setOcupado(boolean ocupado) {
        btnIngresar.setEnabled(!ocupado);
        btnRegistrar.setEnabled(!ocupado);
        btnContinuar.setEnabled(!ocupado);
        btnCambiar.setEnabled(!ocupado);
        setCursor(ocupado ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    // Iniciar sesión
    public String getUsuario() { return txtUsuario.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public void setUsuario(String u) { txtUsuario.setText(u); }
    public void limpiarPassword() { txtPassword.setText(""); }
    public JButton getBtnIngresar() { return btnIngresar; }

    // Crear cuenta
    public String getRegUsuario() { return txtRegUsuario.getText().trim(); }
    public String getRegPassword() { return new String(txtRegPassword.getPassword()); }
    public String getRegConfirmacion() { return new String(txtRegConfirmacion.getPassword()); }
    public String getRegPregunta() { return (String) comboPregunta.getSelectedItem(); }
    public String getRegRespuesta() { return txtRegRespuesta.getText().trim(); }
    public JButton getBtnRegistrar() { return btnRegistrar; }
    public void limpiarRegistro() {
        txtRegUsuario.setText("");
        txtRegPassword.setText("");
        txtRegConfirmacion.setText("");
        txtRegRespuesta.setText("");
        comboPregunta.setSelectedIndex(0);
    }

    // Recuperar contraseña
    public String getRecUsuario() { return txtRecUsuario.getText().trim(); }
    public JButton getBtnContinuar() { return btnContinuar; }
    public String getRecRespuesta() { return txtRecRespuesta.getText().trim(); }
    public String getRecNueva() { return new String(txtRecNueva.getPassword()); }
    public String getRecConfirmacion() { return new String(txtRecConfirmacion.getPassword()); }
    public JButton getBtnCambiar() { return btnCambiar; }
    public void limpiarRespuesta() { txtRecRespuesta.setText(""); txtRecRespuesta.requestFocusInWindow(); }

    public void mostrarPasoRespuesta(String pregunta) {
        lblPregunta.setText("<html>" + pregunta + "</html>");
        pasos.show(panelPasos, "2");
        txtRecRespuesta.requestFocusInWindow();
    }

    public void reiniciarRecuperacion() {
        txtRecUsuario.setText("");
        txtRecRespuesta.setText("");
        txtRecNueva.setText("");
        txtRecConfirmacion.setText("");
        pasos.show(panelPasos, "1");
    }

    /** Vuelve a calcular la paleta cuando cambia el tema claro/oscuro. */
    static void refrescarPaleta() {
        VERDE = TemaRentCar.c(22, 163, 74);
    }
}