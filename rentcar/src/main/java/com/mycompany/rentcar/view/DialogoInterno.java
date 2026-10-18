package com.mycompany.rentcar.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * Reemplazo de JDialog que NO abre una ventana del sistema: el cuadro se dibuja
 * encima de la ventana principal (con el fondo atenuado) y bloquea la pantalla
 * de atrás mientras está abierto, igual que un cuadro modal.
 *
 * Tiene los mismos métodos que se usaban de JDialog, así que en el resto del
 * código solo cambia el nombre de la clase.
 */
public class DialogoInterno {

    // Capa transparente que cubre toda la ventana principal
    private static Capa capa;

    private final JRootPane raiz = new JRootPane();
    private final boolean modal;
    private String titulo;
    private int ancho = 480, alto = 320;
    private Point ubicacionManual;          // en coordenadas de la capa (null = centrado)
    private SecondaryLoop bucle;
    private boolean visible;
    private Component focoAnterior;

    public DialogoInterno(Object dueño, String titulo, boolean modal) {
        this.titulo = titulo;
        this.modal = modal;
        raiz.setOpaque(true);
        raiz.setFocusCycleRoot(true);
    }

    public DialogoInterno(Object dueño, String titulo, Dialog.ModalityType tipo) {
        this(dueño, titulo, tipo != Dialog.ModalityType.MODELESS);
    }

    public DialogoInterno(Object dueño) {
        this(dueño, "", true);
    }

    /** Lo llama la ventana principal una sola vez para activar la capa. */
    public static void instalarEn(JFrame ventana) {
        capa = new Capa();
        ventana.setGlassPane(capa);
        capa.setVisible(false);
    }

    // ------------------------------------------------------------------
    // API parecida a JDialog
    // ------------------------------------------------------------------
    public void setUndecorated(boolean b) { }
    public void setResizable(boolean b) { }
    public void setModal(boolean b) { }
    public void setDefaultCloseOperation(int op) { }
    public void setAlwaysOnTop(boolean b) { }
    public void setTitle(String t) { titulo = t; }
    public String getTitle() { return titulo; }
    public void setBackground(Color c) { raiz.setBackground(c); }
    public void toFront() { }
    public boolean isDisplayable() { return visible; }
    public boolean isVisible() { return visible; }
    public JRootPane getRootPane() { return raiz; }
    public Container getContentPane() { return raiz.getContentPane(); }
    public void setContentPane(Container c) { raiz.setContentPane(c); }
    public void setLayout(java.awt.LayoutManager m) { raiz.getContentPane().setLayout(m); }
    public void add(Component c, Object restriccion) { raiz.getContentPane().add(c, restriccion); }
    public void add(Component c) { raiz.getContentPane().add(c); }
    public void setMinimumSize(Dimension d) { }

    public void setSize(int w, int h) { ancho = w; alto = h; relayout(); }
    public void setSize(Dimension d) { setSize(d.width, d.height); }
    public Dimension getSize() { return new Dimension(ancho, alto); }
    public int getWidth() { return ancho; }
    public int getHeight() { return alto; }
    public Dimension getPreferredSize() { return raiz.getPreferredSize(); }

    public void pack() {
        Dimension d = raiz.getContentPane().getPreferredSize();
        setSize(d.width, d.height);
    }

    public void setLocationRelativeTo(Object c) { ubicacionManual = null; relayout(); }

    /** Coordenadas de pantalla (para poder arrastrar el cuadro por su barra de título). */
    public void setLocation(int x, int y) {
        if (capa == null || !capa.isShowing()) return;
        Point o = capa.getLocationOnScreen();
        ubicacionManual = new Point(x - o.x, y - o.y);
        relayout();
    }

    public Point getLocation() {
        if (raiz.isShowing()) return raiz.getLocationOnScreen();
        return new Point(0, 0);
    }

    public Point getLocationOnScreen() { return getLocation(); }

    public void dispose() { setVisible(false); }

    public void setVisible(boolean mostrar) {
        if (!EventQueue.isDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(() -> setVisible(mostrar));
            } catch (Exception ignorada) { }
            return;
        }
        if (mostrar == visible) return;
        if (capa == null) {
            throw new IllegalStateException("Falta llamar a DialogoInterno.instalarEn(ventana).");
        }
        if (mostrar) {
            visible = true;
            focoAnterior = java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            capa.abrir(this);
            if (modal) {
                bucle = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
                bucle.enter();           // espera aquí hasta que se cierre (como un modal)
            }
        } else {
            visible = false;
            capa.cerrar(this);
            if (bucle != null) {
                bucle.exit();
                bucle = null;
            }
            if (focoAnterior != null && focoAnterior.isShowing()) {
                focoAnterior.requestFocusInWindow();
            }
        }
    }

    private void relayout() {
        if (visible && capa != null) {
            capa.revalidate();
            capa.repaint();
        }
    }

    // ------------------------------------------------------------------
    // Capa que se dibuja encima de la ventana principal
    // ------------------------------------------------------------------
    static final class Capa extends JPanel {

        private final List<DialogoInterno> abiertos = new ArrayList<>();

        Capa() {
            super(null);
            setOpaque(false);
            // Se "comen" los eventos del mouse para que la pantalla de atrás no responda
            addMouseListener(new MouseAdapter() { });
            addMouseMotionListener(new MouseMotionAdapter() { });
            addMouseWheelListener((MouseWheelListener) e -> e.consume());
        }

        void abrir(DialogoInterno d) {
            abiertos.add(d);
            add(d.raiz);
            setVisible(true);
            revalidate();
            repaint();
            SwingUtilities.invokeLater(() -> {
                Component primero = new java.awt.ContainerOrderFocusTraversalPolicy().getFirstComponent(d.raiz);
                if (primero != null) primero.requestFocusInWindow();
                else d.raiz.requestFocusInWindow();
            });
        }

        void cerrar(DialogoInterno d) {
            abiertos.remove(d);
            remove(d.raiz);
            if (abiertos.isEmpty()) setVisible(false);
            revalidate();
            repaint();
        }

        @Override
        public void doLayout() {
            for (DialogoInterno d : abiertos) {
                int w = Math.min(d.ancho, Math.max(200, getWidth() - 24));
                int h = Math.min(d.alto, Math.max(120, getHeight() - 24));
                int x = d.ubicacionManual != null ? d.ubicacionManual.x : (getWidth() - w) / 2;
                int y = d.ubicacionManual != null ? d.ubicacionManual.y : (getHeight() - h) / 2;
                d.raiz.setBounds(x, y, w, h);
                d.raiz.validate();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (abiertos.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(TemaRentCar.c(15, 18, 25, 140));       // fondo atenuado
            g2.fillRect(0, 0, getWidth(), getHeight());
            for (DialogoInterno d : abiertos) {                // sombra suave
                java.awt.Rectangle r = d.raiz.getBounds();
                for (int i = 14; i >= 1; i--) {
                    g2.setComposite(AlphaComposite.SrcOver.derive(0.018f));
                    g2.setColor(Color.BLACK);
                    g2.fillRoundRect(r.x - i, r.y - i + 6, r.width + 2 * i, r.height + 2 * i, 18, 18);
                }
            }
            g2.dispose();
        }
    }
}