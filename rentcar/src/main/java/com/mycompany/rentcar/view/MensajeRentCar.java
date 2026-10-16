package com.mycompany.rentcar.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;

/**
 * Ventana de mensaje con el diseño de RentCar. Reemplaza a JOptionPane.showMessageDialog
 * (mismos parámetros: ventana padre, mensaje, título y tipo de mensaje).
 */
public final class MensajeRentCar {

    private static final Color VERDE = new Color(22, 163, 74);
    private static final Color AMBAR = new Color(234, 138, 8);
    private static final Color AZUL = new Color(37, 99, 235);

    private enum Clase { EXITO, INFO, AVISO, ERROR }

    private MensajeRentCar() {
    }

    public static void mostrar(Component padre, Object mensaje, String titulo, int tipo) {

        if (SwingUtilities.isEventDispatchThread()) {
            construir(padre, mensaje, titulo, tipo);
        } else {
            try {
                SwingUtilities.invokeAndWait(() -> construir(padre, mensaje, titulo, tipo));
            } catch (Exception ignorada) {
            }
        }
    }

    private static Clase clasificar(String texto, int tipo) {
        if (tipo == JOptionPane.ERROR_MESSAGE) return Clase.ERROR;
        if (tipo == JOptionPane.WARNING_MESSAGE) return Clase.AVISO;
        String t = texto.toLowerCase(Locale.ROOT);
        String[] avisos = {"incorrect", "no coinciden", "ya está", "por favor", "complete", "escribe",
            "demasiados", "debe tener", "no encontr", "ingrese", "selecciona", "no se pudo"};
        for (String a : avisos) {
            if (t.contains(a)) return Clase.AVISO;
        }
        if (t.contains("error")) return Clase.ERROR;
        if (t.contains("exitos") || t.contains("éxito") || t.contains("correctamente")) return Clase.EXITO;
        return Clase.INFO;
    }

    private static void construir(Component padre, Object mensaje, String titulo, int tipo) {

        String texto = mensaje == null ? "" : mensaje.toString();
        Clase clase = clasificar(texto, tipo);
        Color color = clase == Clase.EXITO ? VERDE : clase == Clase.AVISO ? AMBAR
                : clase == Clase.ERROR ? TemaRentCar.ROJO : AZUL;

        Window dueño = padre == null ? null : (padre instanceof Window ? (Window) padre : SwingUtilities.getWindowAncestor(padre));
        JDialog dialogo = new JDialog(dueño, titulo == null ? "RentCar" : titulo, Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setUndecorated(true);
        dialogo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(TemaRentCar.PANEL);
        raiz.setBorder(BorderFactory.createLineBorder(TemaRentCar.BORDE));

        // Barra superior
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(TemaRentCar.FONDO);
        barra.setPreferredSize(new Dimension(100, 38));
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, TemaRentCar.ROJO));
        JLabel lblBarra = new JLabel("   RENTCAR  ·  " + (titulo == null ? "RentCar" : titulo));
        lblBarra.setForeground(TemaRentCar.TEXTO);
        lblBarra.setFont(new Font("SansSerif", Font.BOLD, 12));
        barra.add(lblBarra, BorderLayout.CENTER);
        barra.add(crearCerrar(dialogo), BorderLayout.EAST);

        MouseAdapter arrastre = new MouseAdapter() {
            private Point origen;
            @Override public void mousePressed(MouseEvent e) { origen = e.getPoint(); }
            @Override public void mouseDragged(MouseEvent e) {
                if (origen == null) return;
                Point p = dialogo.getLocation();
                dialogo.setLocation(p.x + e.getX() - origen.x, p.y + e.getY() - origen.y);
            }
        };
        barra.addMouseListener(arrastre);
        barra.addMouseMotionListener(arrastre);

        // Cuerpo: insignia + mensaje
        JPanel insignia = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                int d = Math.min(getWidth(), getHeight());
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 32));
                g2.fillOval(0, 0, d, d);
                g2.setColor(color);
                g2.fillOval(7, 7, d - 14, d - 14);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int c = d / 2;
                switch (clase) {
                    case EXITO:
                        g2.drawPolyline(new int[]{c - 9, c - 2, c + 10}, new int[]{c + 1, c + 8, c - 7}, 3);
                        break;
                    case ERROR:
                        g2.drawLine(c - 8, c - 8, c + 8, c + 8);
                        g2.drawLine(c + 8, c - 8, c - 8, c + 8);
                        break;
                    case AVISO:
                        g2.drawLine(c, c - 11, c, c + 3);
                        g2.fillOval(c - 2, c + 8, 5, 5);
                        break;
                    default:
                        g2.fillOval(c - 2, c - 12, 5, 5);
                        g2.drawLine(c, c - 3, c, c + 11);
                        break;
                }
                g2.dispose();
            }
        };
        insignia.setOpaque(false);
        insignia.setPreferredSize(new Dimension(58, 58));

        JPanel colIcono = new JPanel(new BorderLayout());
        colIcono.setOpaque(false);
        colIcono.add(insignia, BorderLayout.NORTH);

        String html = texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
        JLabel lblTitulo = new JLabel(clase == Clase.EXITO ? "¡Listo!" : clase == Clase.ERROR ? "Ocurrió un problema"
                : clase == Clase.AVISO ? "Atención" : "Información");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblTitulo.setForeground(TemaRentCar.TEXTO);
        JLabel lblTexto = new JLabel("<html><div style='width:290px'>" + html + "</div></html>");
        lblTexto.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTexto.setForeground(TemaRentCar.GRIS);

        JPanel textos = new JPanel(new BorderLayout(0, 8));
        textos.setOpaque(false);
        textos.add(lblTitulo, BorderLayout.NORTH);
        textos.add(lblTexto, BorderLayout.CENTER);

        JPanel cuerpo = new JPanel(new BorderLayout(18, 0));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(26, 28, 12, 28));
        cuerpo.add(colIcono, BorderLayout.WEST);
        cuerpo.add(textos, BorderLayout.CENTER);

        JButton aceptar = crearBoton("ENTENDIDO");
        aceptar.addActionListener(e -> dialogo.dispose());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 28, 22, 28));
        pie.add(aceptar);

        raiz.add(barra, BorderLayout.NORTH);
        raiz.add(cuerpo, BorderLayout.CENTER);
        raiz.add(pie, BorderLayout.SOUTH);
        dialogo.setContentPane(raiz);
        dialogo.getRootPane().setDefaultButton(aceptar);
        dialogo.getRootPane().registerKeyboardAction(e -> dialogo.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialogo.pack();
        dialogo.setSize(Math.max(470, dialogo.getWidth()), dialogo.getHeight());
        dialogo.setLocationRelativeTo(dueño);
        dialogo.setVisible(true);
    }

    private static JButton crearCerrar(JDialog dialogo) {
        JButton b = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean sobre = getModel().isRollover();
                g2.setColor(sobre ? TemaRentCar.ROJO : TemaRentCar.FONDO);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(sobre ? Color.WHITE : TemaRentCar.TEXTO);
                g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.drawLine(cx - 5, cy - 5, cx + 5, cy + 5);
                g2.drawLine(cx + 5, cy - 5, cx - 5, cy + 5);
                g2.dispose();
            }
        };
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setFocusable(false);
        b.setPreferredSize(new Dimension(46, 36));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.getModel().addChangeListener(e -> b.repaint());
        b.addActionListener(e -> dialogo.dispose());
        return b;
    }

    private static JButton crearBoton(String texto) {
        JButton b = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? TemaRentCar.ROJO_OSCURO : TemaRentCar.ROJO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setPreferredSize(new Dimension(150, 42));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.getModel().addChangeListener(e -> b.repaint());
        return b;
    }
}