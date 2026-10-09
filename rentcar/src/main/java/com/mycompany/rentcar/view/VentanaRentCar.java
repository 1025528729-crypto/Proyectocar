package com.mycompany.rentcar.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Barra superior personalizada para las ventanas principales de RentCar. */
public final class VentanaRentCar {
    private static final Color FONDO = new Color(12, 14, 18);
    private static final Color TEXTO = new Color(242, 243, 245);
    private static final Color ROJO = new Color(224, 43, 49);
    private static final Color HOVER = new Color(48, 51, 58);

    private VentanaRentCar() { }

    public static void instalar(JFrame frame) {
        if (frame == null || frame.isUndecorated()) return;

        Container contenidoAnterior = frame.getContentPane();
        frame.setUndecorated(true);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FONDO);
        raiz.setBorder(BorderFactory.createLineBorder(new Color(48, 51, 58)));

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(FONDO);
        barra.setPreferredSize(new Dimension(100, 38));
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ROJO));

        JLabel titulo = new JLabel("  RENTCAR  ·  " + frame.getTitle());
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        titulo.setBorder(new EmptyBorder(0, 7, 0, 0));
        barra.add(titulo, BorderLayout.CENTER);

        JPanel controles = new JPanel(new GridLayout(1, 3, 0, 0));
        controles.setOpaque(false);
        JButton minimizar = crearBoton(TipoControl.MINIMIZAR, HOVER);
        JButton maximizar = crearBoton(TipoControl.MAXIMIZAR, HOVER);
        JButton cerrar = crearBoton(TipoControl.CERRAR, ROJO);
        minimizar.setToolTipText("Minimizar");
        maximizar.setToolTipText("Maximizar / restaurar");
        cerrar.setToolTipText("Cerrar");
        controles.add(minimizar);
        controles.add(maximizar);
        controles.add(cerrar);
        barra.add(controles, BorderLayout.EAST);

        minimizar.addActionListener(e -> frame.setState(Frame.ICONIFIED));
        maximizar.addActionListener(e -> {
            boolean maximizada = (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
            frame.setExtendedState(maximizada ? Frame.NORMAL : Frame.MAXIMIZED_BOTH);
            maximizar.putClientProperty("rentcar.maximizada", !maximizada);
            maximizar.repaint();
        });
        cerrar.addActionListener(e -> frame.dispatchEvent(
                new java.awt.event.WindowEvent(frame, java.awt.event.WindowEvent.WINDOW_CLOSING)));

        MouseAdapter arrastre = new MouseAdapter() {
            private Point origenPantalla;
            private Point origenVentana;
            @Override public void mousePressed(MouseEvent e) {
                if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) return;
                origenPantalla = e.getLocationOnScreen();
                origenVentana = frame.getLocation();
            }
            @Override public void mouseDragged(MouseEvent e) {
                if (origenPantalla == null || origenVentana == null) return;
                Point actual = e.getLocationOnScreen();
                frame.setLocation(origenVentana.x + actual.x - origenPantalla.x,
                        origenVentana.y + actual.y - origenPantalla.y);
            }
        };
        barra.addMouseListener(arrastre);
        barra.addMouseMotionListener(arrastre);
        titulo.addMouseListener(arrastre);
        titulo.addMouseMotionListener(arrastre);

        raiz.add(barra, BorderLayout.NORTH);
        raiz.add(contenidoAnterior, BorderLayout.CENTER);
        frame.setContentPane(raiz);
        frame.revalidate();
        frame.repaint();
    }

    private enum TipoControl { MINIMIZAR, MAXIMIZAR, CERRAR }

    private static JButton crearBoton(TipoControl tipo, Color fondo) {
        JButton boton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() && tipo != TipoControl.CERRAR
                        ? ROJO : fondo);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(TEXTO);
                g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                if (tipo == TipoControl.MINIMIZAR) {
                    g2.drawLine(cx - 6, cy + 4, cx + 6, cy + 4);
                } else if (tipo == TipoControl.MAXIMIZAR) {
                    boolean maximizada = Boolean.TRUE.equals(
                            getClientProperty("rentcar.maximizada"));
                    if (maximizada) {
                        g2.drawRect(cx - 5, cy - 5, 9, 8);
                        g2.drawLine(cx - 3, cy - 7, cx + 7, cy - 7);
                        g2.drawLine(cx + 7, cy - 7, cx + 7, cy + 3);
                    } else {
                        g2.drawRect(cx - 6, cy - 6, 12, 12);
                    }
                } else {
                    g2.drawLine(cx - 5, cy - 5, cx + 5, cy + 5);
                    g2.drawLine(cx + 5, cy - 5, cx - 5, cy + 5);
                }
                g2.dispose();
            }
        };
        boton.setToolTipText(tipo == TipoControl.MINIMIZAR ? "Minimizar"
                : tipo == TipoControl.MAXIMIZAR ? "Maximizar / restaurar" : "Cerrar");
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setFocusable(false);
        boton.setPreferredSize(new Dimension(46, 36));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.getModel().addChangeListener(e -> boton.repaint());
        return boton;
    }
}
