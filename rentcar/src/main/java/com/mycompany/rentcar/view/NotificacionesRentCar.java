package com.mycompany.rentcar.view;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;

/**
 * Notificaciones DENTRO del programa (sin abrir ventanas):
 *
 *  - Aviso emergente ("toast") arriba a la derecha que desaparece solo.
 *  - Campana en la barra superior con el contador de no leídas.
 *  - Panel con el historial de notificaciones (se abre al tocar la campana).
 *
 * Uso desde cualquier parte del programa:
 *     NotificacionesRentCar.exito("Reserva confirmada", "Tu reserva quedó registrada.");
 *     NotificacionesRentCar.info("Título", "Mensaje", () -> abrirAlgo());   // con acción al tocarla
 *
 * La lista vive en memoria y se conserva aunque se cambie el tema (la ventana
 * se reconstruye); se borra al cerrar sesión.
 */
public final class NotificacionesRentCar {

    public enum Tipo { EXITO, INFO, AVISO, ERROR }

    public static final class Item {
        public final Tipo tipo;
        public final String titulo;
        public final String mensaje;
        public final LocalDateTime fecha = LocalDateTime.now();
        public final Runnable accion;
        public boolean leida;

        Item(Tipo tipo, String titulo, String mensaje, Runnable accion) {
            this.tipo = tipo;
            this.titulo = titulo == null ? "" : titulo;
            this.mensaje = mensaje == null ? "" : mensaje;
            this.accion = accion;
        }
    }

    private static final int MAX_GUARDADAS = 50;
    private static final int MAX_EN_PANTALLA = 4;
    private static final int DURACION_MS = 6500;

    private static final List<Item> ITEMS = new ArrayList<>();   // la más reciente primero
    private static Anfitrion anfitrion;

    private NotificacionesRentCar() {
    }

    // =========================================================
    // API
    // =========================================================

    public static void exito(String titulo, String mensaje) { publicar(Tipo.EXITO, titulo, mensaje, null); }
    public static void exito(String titulo, String mensaje, Runnable accion) { publicar(Tipo.EXITO, titulo, mensaje, accion); }
    public static void info(String titulo, String mensaje) { publicar(Tipo.INFO, titulo, mensaje, null); }
    public static void info(String titulo, String mensaje, Runnable accion) { publicar(Tipo.INFO, titulo, mensaje, accion); }
    public static void aviso(String titulo, String mensaje) { publicar(Tipo.AVISO, titulo, mensaje, null); }
    public static void error(String titulo, String mensaje) { publicar(Tipo.ERROR, titulo, mensaje, null); }

    public static void publicar(Tipo tipo, String titulo, String mensaje, Runnable accion) {

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> publicar(tipo, titulo, mensaje, accion));
            return;
        }

        Item item = new Item(tipo, titulo, mensaje, accion);

        ITEMS.add(0, item);

        while (ITEMS.size() > MAX_GUARDADAS) {
            ITEMS.remove(ITEMS.size() - 1);
        }

        if (anfitrion != null) {
            anfitrion.alCambiar();
            anfitrion.mostrarToast(item);
        }
    }

    public static int noLeidas() {
        int n = 0;
        for (Item i : ITEMS) {
            if (!i.leida) n++;
        }
        return n;
    }

    public static List<Item> todas() {
        return new ArrayList<>(ITEMS);
    }

    public static void marcarTodasLeidas() {
        for (Item i : ITEMS) {
            i.leida = true;
        }
        if (anfitrion != null) anfitrion.alCambiar();
    }

    public static void borrarTodas() {
        ITEMS.clear();
        if (anfitrion != null) anfitrion.alCambiar();
    }

    /** Al cerrar sesión: se borra el historial y se quitan los avisos en pantalla. */
    public static void limpiar() {
        ITEMS.clear();
        if (anfitrion != null) {
            anfitrion.quitarToasts();
            anfitrion.cerrarCentro();
            anfitrion.alCambiar();
        }
    }

    public static void cerrarCentro() {
        if (anfitrion != null) anfitrion.cerrarCentro();
    }

    // =========================================================
    // INSTALACIÓN EN LA VENTANA
    // =========================================================

    /**
     * @param raiz  el JRootPane de la ventana principal
     * @param barra la barra superior (los avisos aparecen justo debajo)
     */
    public static void instalarEn(JRootPane raiz, JComponent barra) {
        anfitrion = new Anfitrion(raiz, barra);
    }

    /** La campana que se coloca en la barra superior. */
    public static JComponent campana() {
        return anfitrion == null ? new JLabel() : anfitrion.campana;
    }

    // =========================================================
    // COLORES / TEXTOS
    // =========================================================

    private static Color colorDe(Tipo t) {
        switch (t) {
            case EXITO: return new Color(22, 163, 74);
            case AVISO: return new Color(234, 138, 8);
            case ERROR: return TemaRentCar.ROJO;
            default:    return new Color(37, 99, 235);
        }
    }

    // Curva de suavizado (empieza rápido y frena al final)
    private static float suavizar(float t) {
        float u = 1f - Math.max(0f, Math.min(1f, t));
        return 1f - u * u * u;
    }

    private static String hace(LocalDateTime f) {
        long min = Duration.between(f, LocalDateTime.now()).toMinutes();
        if (min < 1) return "Ahora";
        if (min < 60) return "Hace " + min + " min";
        if (min < 60 * 24) return "Hace " + (min / 60) + " h";
        return f.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
    }

    private static String recortar(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1).trim() + "…";
    }

    /** Círculo de color con el símbolo del tipo (✓, i, !, ×). */
    private static void dibujarIcono(Graphics2D g2, Tipo tipo, int x, int y, int d) {
        Color c = colorDe(tipo);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 38));
        g2.fill(new Ellipse2D.Double(x, y, d, d));
        g2.setColor(c);
        g2.setStroke(new BasicStroke(d / 11f + 1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double cx = x + d / 2.0, cy = y + d / 2.0, u = d / 10.0;
        switch (tipo) {
            case EXITO: {
                Path2D p = new Path2D.Double();
                p.moveTo(cx - 3.2 * u, cy + 0.2 * u);
                p.lineTo(cx - 0.8 * u, cy + 2.6 * u);
                p.lineTo(cx + 3.4 * u, cy - 2.6 * u);
                g2.draw(p);
                break;
            }
            case ERROR:
                g2.draw(new java.awt.geom.Line2D.Double(cx - 2.6 * u, cy - 2.6 * u, cx + 2.6 * u, cy + 2.6 * u));
                g2.draw(new java.awt.geom.Line2D.Double(cx + 2.6 * u, cy - 2.6 * u, cx - 2.6 * u, cy + 2.6 * u));
                break;
            case AVISO:
                g2.draw(new java.awt.geom.Line2D.Double(cx, cy - 3.4 * u, cx, cy + 0.8 * u));
                g2.fill(new Ellipse2D.Double(cx - 0.9 * u, cy + 2.2 * u, 1.8 * u, 1.8 * u));
                break;
            default:
                g2.fill(new Ellipse2D.Double(cx - 0.9 * u, cy - 3.6 * u, 1.8 * u, 1.8 * u));
                g2.draw(new java.awt.geom.Line2D.Double(cx, cy - 0.8 * u, cx, cy + 3.4 * u));
        }
    }

    // =========================================================
    // ANFITRIÓN: capas dentro de la ventana
    // =========================================================

    private static final class Anfitrion {

        final JRootPane raiz;
        final JComponent barra;
        final JLayeredPane capas;
        final Campana campana = new Campana();

        final JPanel capaToasts;
        final JPanel capaCentro;
        final List<Toast> toasts = new ArrayList<>();
        PanelCentro centro;

        Anfitrion(JRootPane raiz, JComponent barra) {
            this.raiz = raiz;
            this.barra = barra;
            this.capas = raiz.getLayeredPane();

            // Capa de avisos: deja pasar los clics que no caen sobre un aviso
            capaToasts = new JPanel(null) {
                @Override public boolean contains(int x, int y) {
                    for (Component c : getComponents()) {
                        if (c.isVisible() && c.getBounds().contains(x, y)) return true;
                    }
                    return false;
                }
            };
            capaToasts.setOpaque(false);

            // Capa del panel de notificaciones: un clic fuera lo cierra
            capaCentro = new JPanel(null);
            capaCentro.setOpaque(false);
            capaCentro.setVisible(false);
            capaCentro.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (centro == null || !centro.getBounds().contains(e.getPoint())) cerrarCentro();
                }
            });
            capaCentro.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cerrarNotificaciones");
            capaCentro.getActionMap().put("cerrarNotificaciones", new AbstractAction() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) { cerrarCentro(); }
            });

            capas.add(capaCentro, JLayeredPane.POPUP_LAYER);
            capas.add(capaToasts, JLayeredPane.DRAG_LAYER);

            capas.addComponentListener(new ComponentAdapter() {
                @Override public void componentResized(ComponentEvent e) { ajustarCapas(); }
            });
            ajustarCapas();
        }

        void ajustarCapas() {
            capaToasts.setBounds(0, 0, capas.getWidth(), capas.getHeight());
            capaCentro.setBounds(0, 0, capas.getWidth(), capas.getHeight());
            if (centro != null && capaCentro.isVisible()) ubicarCentro();
        }

        int yBase() {
            Point p = SwingUtilities.convertPoint(barra, 0, barra.getHeight(), capas);
            return p.y + 12;
        }

        void alCambiar() {
            campana.repaint();
            if (centro != null && capaCentro.isVisible()) {
                centro.reconstruir(false);
                ubicarCentro();
            }
        }

        // ---------------- avisos ----------------

        void mostrarToast(Item item) {

            while (toasts.size() >= MAX_EN_PANTALLA) {
                toasts.get(0).quitarYa();
            }

            Toast t = new Toast(item);
            toasts.add(t);
            capaToasts.add(t);
            t.iniciar();
        }

        void quitarToasts() {
            for (Toast t : new ArrayList<>(toasts)) {
                t.quitarYa();
            }
        }

        void quitado(Toast t) {
            toasts.remove(t);
            capaToasts.remove(t);
            capaToasts.repaint();
        }

        // ---------------- panel de notificaciones ----------------

        void alternarCentro() {
            if (capaCentro.isVisible()) cerrarCentro(); else abrirCentro();
        }

        void abrirCentro() {
            centro = new PanelCentro();
            capaCentro.removeAll();
            capaCentro.add(centro);
            capaCentro.setBounds(0, 0, capas.getWidth(), capas.getHeight());
            ubicarCentro();
            capaCentro.setVisible(true);
            capaCentro.repaint();
            // los avisos emergentes ya no hacen falta: se ve el historial completo
            for (Toast t : new ArrayList<>(toasts)) t.cerrar();

            // al abrirlo se consideran vistas (el contador de la campana se limpia)
            marcarTodasLeidas();
        }

        void cerrarCentro() {
            if (capaCentro.isVisible()) {
                capaCentro.setVisible(false);
                capas.repaint();
            }
        }

        void ubicarCentro() {
            Dimension d = centro.getPreferredSize();
            Point pb = SwingUtilities.convertPoint(campana, campana.getWidth(), campana.getHeight(), capas);
            int x = Math.max(8, Math.min(pb.x - d.width, capas.getWidth() - d.width - 8));
            int y = pb.y + 10;
            int alto = Math.min(d.height, Math.max(160, capas.getHeight() - y - 12));
            centro.setBounds(x, y, d.width, alto);
            centro.revalidate();
        }

        // =====================================================
        // CAMPANA
        // =====================================================

        final class Campana extends JComponent {

            private boolean encima;

            Campana() {
                setPreferredSize(new Dimension(42, 42));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setToolTipText("Notificaciones");
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) { alternarCentro(); }
                    @Override public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { encima = false; repaint(); }
                });
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();

                g2.setColor(encima || capaCentro.isVisible() ? TemaRentCar.CAMPO : TemaRentCar.PANEL);
                g2.fillOval(1, 1, w - 2, h - 2);
                g2.setColor(TemaRentCar.BORDE);
                g2.drawOval(1, 1, w - 3, h - 3);

                // campana
                double cx = w / 2.0, cy = h / 2.0 + 0.5;
                g2.setColor(TemaRentCar.TEXTO);
                Path2D b = new Path2D.Double();
                b.moveTo(cx - 8, cy + 5);
                b.curveTo(cx - 5, cy + 3, cx - 5, cy - 1, cx - 5, cy - 4);
                b.curveTo(cx - 5, cy - 9.5, cx + 5, cy - 9.5, cx + 5, cy - 4);
                b.curveTo(cx + 5, cy - 1, cx + 5, cy + 3, cx + 8, cy + 5);
                b.closePath();
                g2.fill(b);
                g2.fill(new Ellipse2D.Double(cx - 2.6, cy + 6, 5.2, 3.6));
                g2.fill(new Ellipse2D.Double(cx - 1.6, cy - 12, 3.2, 3.2));

                // contador de no leídas
                int n = noLeidas();
                if (n > 0) {
                    String t = n > 9 ? "9+" : String.valueOf(n);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    int d = Math.max(16, fm.stringWidth(t) + 8);
                    int x = w - d - 1, y = 1;
                    g2.setColor(TemaRentCar.PANEL);
                    g2.fillOval(x - 2, y - 2, d + 4, 20);
                    g2.setColor(TemaRentCar.ROJO);
                    g2.fillRoundRect(x, y, d, 16, 16, 16);
                    g2.setColor(Color.WHITE);
                    g2.drawString(t, x + (d - fm.stringWidth(t)) / 2, y + 12);
                }
                g2.dispose();
            }
        }

        // =====================================================
        // AVISO EMERGENTE
        // =====================================================

        final class Toast extends JPanel {

            static final int ANCHO = 390;

            final Item item;
            final Color color;
            final Timer reloj;
            final JTextArea mensaje;

            float entrada = 0f;
            float salida = 0f;
            float alfa = 0f;
            double yActual = -1;
            long restante = DURACION_MS;
            long ultimo;
            boolean encima, cerrando;

            Toast(Item item) {
                super(null);
                this.item = item;
                this.color = colorDe(item.tipo);
                setOpaque(false);

                int xTexto = 66, anchoTexto = ANCHO - xTexto - 40;

                JLabel titulo = new JLabel(recortar(item.titulo, 40));
                titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
                titulo.setForeground(TemaRentCar.TEXTO);
                titulo.setBounds(xTexto, 13, anchoTexto, 20);
                add(titulo);

                mensaje = new JTextArea(recortar(item.mensaje, 230));
                mensaje.setEditable(false);
                mensaje.setFocusable(false);
                mensaje.setLineWrap(true);
                mensaje.setWrapStyleWord(true);
                mensaje.setOpaque(false);
                mensaje.setBorder(null);
                mensaje.setFont(new Font("SansSerif", Font.PLAIN, 12));
                mensaje.setForeground(TemaRentCar.GRIS);
                mensaje.setSize(anchoTexto, Short.MAX_VALUE);
                int altoMensaje = item.mensaje.isEmpty() ? 0 : mensaje.getPreferredSize().height;
                mensaje.setBounds(xTexto, 35, anchoTexto, altoMensaje);
                add(mensaje);

                int alto = Math.max(64, 35 + altoMensaje + 18);
                setSize(ANCHO, alto);

                JLabel cerrar = new JLabel("×", SwingConstants.CENTER);
                cerrar.setFont(new Font("SansSerif", Font.PLAIN, 20));
                cerrar.setForeground(TemaRentCar.GRIS);
                cerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                cerrar.setBounds(ANCHO - 34, 6, 26, 26);
                cerrar.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) { cerrar(); }
                    @Override public void mouseEntered(MouseEvent e) { cerrar.setForeground(TemaRentCar.TEXTO); }
                    @Override public void mouseExited(MouseEvent e) { cerrar.setForeground(TemaRentCar.GRIS); }
                });
                add(cerrar);

                if (item.accion != null) setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                MouseAdapter raton = new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { encima = true; }
                    @Override public void mouseExited(MouseEvent e) {
                        if (!contieneCursor()) encima = false;
                    }
                    @Override public void mouseClicked(MouseEvent e) {
                        if (item.accion != null) {
                            cerrar();
                            item.accion.run();
                        }
                    }
                };
                addMouseListener(raton);
                mensaje.addMouseListener(raton);
                titulo.addMouseListener(raton);

                reloj = new Timer(16, e -> tic());
            }

            boolean contieneCursor() {
                Point p = MouseInfo.getPointerInfo() == null ? null : MouseInfo.getPointerInfo().getLocation();
                if (p == null || !isShowing()) return false;
                SwingUtilities.convertPointFromScreen(p, this);
                return contains(p);
            }

            void iniciar() {
                ultimo = System.currentTimeMillis();
                reponer();
                reloj.start();
            }

            void cerrar() {
                if (!cerrando) {
                    cerrando = true;
                    salida = 0f;
                }
            }

            void quitarYa() {
                reloj.stop();
                quitado(this);
                reponerTodos();
            }

            // posición objetivo: apilados arriba a la derecha, bajo la barra
            double yObjetivo() {
                int y = yBase();
                for (Toast t : toasts) {
                    if (t == this) break;
                    y += t.getHeight() + 10;
                }
                return y;
            }

            void reponer() {
                double objetivo = yObjetivo();
                if (yActual < 0) yActual = objetivo;
                int desplazamiento = (int) Math.round((1 - suavizar(entrada)) * 50 + salida * 40);
                setLocation(capaToasts.getWidth() - ANCHO - 18 + desplazamiento, (int) Math.round(yActual));
            }

            void tic() {
                long ahora = System.currentTimeMillis();
                long dt = ahora - ultimo;
                ultimo = ahora;

                if (!cerrando) {
                    if (entrada < 1f) {
                        entrada = Math.min(1f, entrada + dt / 220f);
                    }
                    alfa = entrada;
                    if (!encima) restante -= dt;
                    if (restante <= 0) cerrar();
                } else {
                    salida += dt / 200f;
                    alfa = Math.max(0f, 1f - salida);
                    if (salida >= 1f) {
                        reloj.stop();
                        quitado(this);
                        reponerTodos();
                        return;
                    }
                }

                // sube/baja suavemente cuando otro aviso desaparece
                double objetivo = yObjetivo();
                yActual += (objetivo - yActual) * 0.3;
                if (Math.abs(objetivo - yActual) < 0.5) yActual = objetivo;

                reponer();
                repaint();
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();

                // sombra suave
                for (int i = 4; i >= 1; i--) {
                    g2.setColor(new Color(0, 0, 0, 10 + (4 - i) * 4));
                    g2.fill(new RoundRectangle2D.Double(i - 2, i, w - 2 * i + 4, h - i, 16 + i, 16 + i));
                }

                Shape forma = new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 14, 14);
                g2.setColor(TemaRentCar.PANEL);
                g2.fill(forma);

                // franja de color a la izquierda
                Shape previa = g2.getClip();
                g2.setClip(forma);
                g2.setColor(color);
                g2.fillRect(0, 0, 6, h);

                // tiempo restante
                if (!cerrando) {
                    int barra = (int) ((w - 6) * Math.max(0, restante) / (double) DURACION_MS);
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 120));
                    g2.fillRect(6, h - 3, barra, 3);
                }
                g2.setClip(previa);

                g2.setColor(TemaRentCar.BORDE);
                g2.draw(forma);

                dibujarIcono(g2, item.tipo, 20, 14, 34);
                g2.dispose();
            }

            @Override public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, alfa))));
                super.paint(g2);
                g2.dispose();
            }
        }

        void reponerTodos() {
            for (Toast t : toasts) t.reponer();
        }

        // =====================================================
        // PANEL CON EL HISTORIAL
        // =====================================================

        final class PanelCentro extends JPanel {

            static final int ANCHO = 410;

            private final JPanel lista = new JPanel();
            private final JLabel lblResumen = new JLabel();
            private Set<Item> nuevas = new HashSet<>();

            PanelCentro() {
                super(new BorderLayout());
                setBackground(TemaRentCar.PANEL);
                setBorder(BorderFactory.createLineBorder(TemaRentCar.BORDE));

                // quienes estaban sin leer se marcan con un punto rojo aunque se "lean" al abrir
                for (Item i : ITEMS) if (!i.leida) nuevas.add(i);

                // ---- encabezado ----
                JPanel cab = new JPanel(new BorderLayout());
                cab.setOpaque(false);
                cab.setBorder(BorderFactory.createEmptyBorder(16, 18, 12, 14));

                JLabel titulo = new JLabel("Notificaciones");
                titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
                titulo.setForeground(TemaRentCar.TEXTO);

                lblResumen.setFont(new Font("SansSerif", Font.PLAIN, 11));
                lblResumen.setForeground(TemaRentCar.GRIS);

                JPanel textos = new JPanel();
                textos.setOpaque(false);
                textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
                textos.add(titulo);
                textos.add(Box.createVerticalStrut(2));
                textos.add(lblResumen);
                cab.add(textos, BorderLayout.WEST);

                JButton borrar = botonTexto("Borrar todo");
                borrar.addActionListener(e -> borrarTodas());
                cab.add(borrar, BorderLayout.EAST);

                JPanel norte = new JPanel(new BorderLayout());
                norte.setOpaque(false);
                norte.add(cab, BorderLayout.CENTER);
                JPanel linea = new JPanel();
                linea.setBackground(TemaRentCar.BORDE);
                linea.setPreferredSize(new Dimension(1, 1));
                norte.add(linea, BorderLayout.SOUTH);
                add(norte, BorderLayout.NORTH);

                // ---- lista ----
                lista.setOpaque(false);
                lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));

                JPanel envoltura = new JPanel(new BorderLayout());
                envoltura.setOpaque(false);
                envoltura.add(lista, BorderLayout.NORTH);

                JScrollPane scroll = new JScrollPane(envoltura,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scroll.setBorder(BorderFactory.createEmptyBorder());
                scroll.setOpaque(false);
                scroll.getViewport().setOpaque(false);
                scroll.getVerticalScrollBar().setUnitIncrement(18);
                estilizarBarra(scroll.getVerticalScrollBar());
                add(scroll, BorderLayout.CENTER);

                reconstruir(true);
            }

            void reconstruir(boolean primera) {
                lista.removeAll();

                if (ITEMS.isEmpty()) {
                    JLabel vacio = new JLabel("<html><div style='text-align:center'>No tienes notificaciones.<br>"
                            + "Aquí aparecerán tus reservas y avisos.</div></html>", SwingConstants.CENTER);
                    vacio.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    vacio.setForeground(TemaRentCar.GRIS);
                    vacio.setBorder(BorderFactory.createEmptyBorder(38, 20, 38, 20));
                    vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
                    JPanel p = new JPanel(new BorderLayout());
                    p.setOpaque(false);
                    p.add(vacio, BorderLayout.CENTER);
                    p.setAlignmentX(Component.LEFT_ALIGNMENT);
                    lista.add(p);
                } else {
                    for (Item i : ITEMS) {
                        if (!i.leida && !primera) nuevas.add(i);
                        lista.add(crearFila(i));
                    }
                }

                int n = nuevas.size();
                lblResumen.setText(ITEMS.isEmpty() ? "Todo al día"
                        : (n == 0 ? "Todo leído" : n + (n == 1 ? " nueva" : " nuevas")));

                lista.revalidate();
                lista.repaint();
                revalidate();
            }

            @Override public Dimension getPreferredSize() {
                int cabecera = 66;
                int contenido = lista.getPreferredSize().height;
                return new Dimension(ANCHO, cabecera + Math.min(contenido, 400));
            }

            JPanel crearFila(Item item) {
                final boolean sinLeer = nuevas.contains(item);

                JPanel fila = new JPanel(null) {
                    boolean sobre;
                    {
                        setOpaque(false);
                        setAlignmentX(Component.LEFT_ALIGNMENT);
                        if (item.accion != null) setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        addMouseListener(new MouseAdapter() {
                            @Override public void mouseEntered(MouseEvent e) { sobre = true; repaint(); }
                            @Override public void mouseExited(MouseEvent e) { sobre = false; repaint(); }
                            @Override public void mouseClicked(MouseEvent e) {
                                if (item.accion != null) {
                                    cerrarCentro();
                                    item.accion.run();
                                }
                            }
                        });
                    }
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        if (sobre || sinLeer) {
                            g2.setColor(sobre ? TemaRentCar.CAMPO
                                    : new Color(TemaRentCar.ROJO.getRed(), TemaRentCar.ROJO.getGreen(), TemaRentCar.ROJO.getBlue(), 14));
                            g2.fillRect(0, 0, getWidth(), getHeight());
                        }
                        g2.setColor(TemaRentCar.BORDE);
                        g2.drawLine(18, getHeight() - 1, getWidth() - 18, getHeight() - 1);
                        dibujarIcono(g2, item.tipo, 18, 14, 34);
                        if (sinLeer) {
                            g2.setColor(TemaRentCar.ROJO);
                            g2.fillOval(getWidth() - 24, 20, 8, 8);
                        }
                        g2.dispose();
                    }
                };

                int xTexto = 64, anchoTexto = ANCHO - xTexto - 40;

                JLabel titulo = new JLabel(recortar(item.titulo, 38));
                titulo.setFont(new Font("SansSerif", Font.BOLD, 13));
                titulo.setForeground(TemaRentCar.TEXTO);
                titulo.setBounds(xTexto, 11, anchoTexto, 18);
                fila.add(titulo);

                JTextArea msg = new JTextArea(recortar(item.mensaje, 200));
                msg.setEditable(false);
                msg.setFocusable(false);
                msg.setLineWrap(true);
                msg.setWrapStyleWord(true);
                msg.setOpaque(false);
                msg.setBorder(null);
                msg.setFont(new Font("SansSerif", Font.PLAIN, 12));
                msg.setForeground(TemaRentCar.GRIS);
                msg.setSize(anchoTexto, Short.MAX_VALUE);
                int alto = item.mensaje.isEmpty() ? 0 : msg.getPreferredSize().height;
                msg.setBounds(xTexto, 31, anchoTexto, alto);
                fila.add(msg);

                JLabel hora = new JLabel(hace(item.fecha));
                hora.setFont(new Font("SansSerif", Font.PLAIN, 10));
                hora.setForeground(TemaRentCar.GRIS);
                hora.setBounds(xTexto, 31 + alto + 4, anchoTexto, 14);
                fila.add(hora);

                Dimension d = new Dimension(ANCHO - 2, 31 + alto + 4 + 14 + 12);
                fila.setPreferredSize(d);
                fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, d.height));
                fila.setMinimumSize(d);
                return fila;
            }

            void estilizarBarra(JScrollBar barra) {
                barra.setPreferredSize(new Dimension(8, 0));
                barra.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
                    @Override protected void configureScrollBarColors() {
                        thumbColor = TemaRentCar.BORDE;
                        trackColor = TemaRentCar.PANEL;
                    }
                    @Override protected JButton createDecreaseButton(int o) { return vacio(); }
                    @Override protected JButton createIncreaseButton(int o) { return vacio(); }
                    private JButton vacio() {
                        JButton b = new JButton();
                        b.setPreferredSize(new Dimension(0, 0));
                        return b;
                    }
                });
            }

            JButton botonTexto(String texto) {
                JButton b = new JButton(texto);
                b.setFont(new Font("SansSerif", Font.BOLD, 11));
                b.setForeground(TemaRentCar.ROJO);
                b.setContentAreaFilled(false);
                b.setBorderPainted(false);
                b.setFocusPainted(false);
                b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return b;
            }
        }
    }
}