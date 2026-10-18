package com.mycompany.rentcar.view;

import com.mycompany.rentcar.model.Usuario;
import com.mycompany.rentcar.presenter.LoginPresenter;
import com.mycompany.rentcar.presenter.MainPublicPresenter;
import com.mycompany.rentcar.presenter.VehiculoPresenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Única ventana del programa. Arriba tiene una barra con el logo, las
 * pestañas y el usuario; abajo cambia la pantalla (catálogo, acceso, gestión).
 */
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

public class AppFrame extends JFrame {

    public static final String CATALOGO = "catalogo";
    public static final String ACCESO = "acceso";
    public static final String GESTION = "gestion";

    private static AppFrame instancia;

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenido = new JPanel(cartas);

    private final Pestana tabCatalogo = new Pestana("Catálogo", CATALOGO);
    private final Pestana tabGestion = new Pestana("Gestión de vehículos", GESTION);
    private final JPanel zonaUsuario = new JPanel(new GridBagLayout());
    private JPanel barra;
    private JPanel capaAcceso;

    private MainPublicView catalogo;
    private AccesoView acceso;
    private VehiculoView gestion;
    private Usuario usuario;
    private String actual = CATALOGO;

    public AppFrame() {
        instancia = this;
        setTitle("Alquiler de vehículos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Cuadros emergentes dentro de esta misma ventana
        DialogoInterno.instalarEn(this);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(TemaRentCar.FONDO);
        raiz.add(crearBarra(), BorderLayout.NORTH);

        contenido.setBackground(TemaRentCar.FONDO);
        raiz.add(contenido, BorderLayout.CENTER);
        setContentPane(raiz);

        catalogo = new MainPublicView();
        new MainPublicPresenter(catalogo);
        contenido.add(catalogo, CATALOGO);

        // El acceso ya no es una página: es un panel que se despliega bajo el botón "Iniciar sesión"
        acceso = new AccesoView();
        new LoginPresenter(acceso, this);
        crearCapaAcceso();

        // Notificaciones dentro del programa: avisos emergentes + campana con el historial
        NotificacionesRentCar.instalarEn(getRootPane(), barra);

        refrescarBarra();
        mostrar(CATALOGO);

        VentanaRentCar.instalar(this);
        setLocationRelativeTo(null);
    }

    public static AppFrame getInstancia() {
        return instancia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    // =========================================================
    // NAVEGACIÓN
    // =========================================================

    public void mostrar(String pantalla) {
        if (ACCESO.equals(pantalla)) {
            abrirAcceso();
            return;
        }
        actual = pantalla;
        cartas.show(contenido, pantalla);
        tabCatalogo.setActiva(CATALOGO.equals(pantalla));
        tabGestion.setActiva(GESTION.equals(pantalla));
        if (ACCESO.equals(pantalla)) {
            acceso.alMostrar();
        }
    }

    public void iniciarSesion(Usuario u) {
        cerrarAcceso();
        this.usuario = u;

        if (gestion != null) {
            contenido.remove(gestion);
        }
        gestion = new VehiculoView();
        new VehiculoPresenter(gestion, u);
        contenido.add(gestion, GESTION);

        refrescarBarra();
        mostrar(GESTION);
        catalogo.refrescarDisponibilidad();   // avisos pendientes y reservas "mías"
    }

    public void cerrarSesion() {
        this.usuario = null;
        NotificacionesRentCar.limpiar();   // las notificaciones son de la sesión
        if (gestion != null) {
            contenido.remove(gestion);
            gestion = null;
        }
        refrescarBarra();
        mostrar(CATALOGO);
        catalogo.refrescarDisponibilidad();
    }

    // =========================================================
    // BARRA SUPERIOR
    // =========================================================

    private JPanel crearBarra() {
        barra = new JPanel(new BorderLayout(24, 0));
        barra.setBackground(TemaRentCar.PANEL);
        barra.setPreferredSize(new Dimension(0, 96));
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, TemaRentCar.BORDE),
                BorderFactory.createEmptyBorder(0, 24, 0, 24)));

        JLabel logo = new JLabel();
        Icon icono = TemaRentCar.logoIcono(320, 76);
        if (icono != null) {
            logo.setIcon(icono);
        } else {
            logo.setText("RentCar.");
            logo.setFont(new Font("SansSerif", Font.BOLD, 22));
            logo.setForeground(TemaRentCar.TEXTO);
        }
        logo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logo.setToolTipText("Ir al catálogo");
        logo.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { mostrar(CATALOGO); }
        });

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 28, 0));
        izquierda.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        izquierda.setOpaque(false);
        izquierda.add(logo);
        izquierda.add(tabCatalogo);
        izquierda.add(tabGestion);

        zonaUsuario.setOpaque(false);

        barra.add(izquierda, BorderLayout.WEST);
        barra.add(zonaUsuario, BorderLayout.EAST);
        return barra;
    }

    /** Muestra u oculta la pestaña de gestión y cambia entre "Iniciar sesión" y el usuario. */
    private void refrescarBarra() {
        tabGestion.setVisible(usuario != null);
        zonaUsuario.removeAll();
        anadirZona(NotificacionesRentCar.campana());
        anadirZona(crearBotonTema());

        if (usuario == null) {
            JButton entrar = new JButton("Iniciar sesión");
            entrar.setFont(new Font("SansSerif", Font.BOLD, 12));
            entrar.setForeground(Color.WHITE);
            entrar.setBackground(TemaRentCar.ROJO);
            entrar.setFocusPainted(false);
            entrar.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
            entrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            entrar.addActionListener(e -> mostrar(ACCESO));
            anadirZona(entrar);
        } else {
            String nombre = usuario.getUsername() == null ? "Usuario" : usuario.getUsername();

            JLabel avatar = new JLabel(nombre.substring(0, Math.min(2, nombre.length())).toUpperCase(), SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TemaRentCar.ROJO);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            avatar.setForeground(Color.WHITE);
            avatar.setFont(new Font("SansSerif", Font.BOLD, 12));
            avatar.setPreferredSize(new Dimension(38, 38));

            JLabel etiqueta = new JLabel("<html><b>" + nombre + "</b><br><span style='color:" + String.format("#%06x", TemaRentCar.GRIS.getRGB() & 0xffffff) + ";font-size:9px'>"
                    + (usuario.esAdmin() ? "Administrador" : "Cliente") + "</span></html>");
            etiqueta.setFont(new Font("SansSerif", Font.PLAIN, 12));
            etiqueta.setForeground(TemaRentCar.TEXTO);

            JButton salir = new JButton("Cerrar sesión");
            salir.setFont(new Font("SansSerif", Font.BOLD, 12));
            salir.setForeground(TemaRentCar.TEXTO);
            salir.setBackground(TemaRentCar.PANEL);
            salir.setFocusPainted(false);
            salir.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(TemaRentCar.BORDE),
                    BorderFactory.createEmptyBorder(8, 18, 8, 18)));
            salir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            salir.addActionListener(e -> cerrarSesion());

            anadirZona(avatar);
            anadirZona(etiqueta);
            anadirZona(salir);
        }

        zonaUsuario.getParent().revalidate();
        zonaUsuario.getParent().repaint();
    }

    private void anadirZona(JComponent c) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 6, 0, 6);
        zonaUsuario.add(c, gbc);
    }

    // =========================================================
    // PANEL DESPLEGABLE DE ACCESO (sin ventanas ni páginas nuevas)
    // =========================================================

    private void crearCapaAcceso() {
        capaAcceso = new JPanel(null);
        capaAcceso.setOpaque(false);
        capaAcceso.setVisible(false);
        acceso.setOpaque(false);
        capaAcceso.add(acceso);

        // El panel queda pegado a la esquina superior derecha, justo bajo la barra
        capaAcceso.setLayout(new LayoutManager() {
            @Override public void addLayoutComponent(String n, Component c) { }
            @Override public void removeLayoutComponent(Component c) { }
            @Override public Dimension preferredLayoutSize(Container p) { return p.getSize(); }
            @Override public Dimension minimumLayoutSize(Container p) { return new Dimension(0, 0); }
            @Override public void layoutContainer(Container p) {
                Dimension d = acceso.getPreferredSize();
                int y = SwingUtilities.convertPoint(barra, 0, barra.getHeight(), p).y;
                int alto = Math.min(d.height, Math.max(200, p.getHeight() - y - 8));
                acceso.setBounds(Math.max(8, p.getWidth() - d.width - 18), y, d.width, alto);
            }
        });

        // Clic fuera del panel = cerrarlo
        capaAcceso.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (!acceso.getBounds().contains(e.getPoint())) cerrarAcceso();
            }
        });

        final JLayeredPane capas = getLayeredPane();
        capas.add(capaAcceso, JLayeredPane.POPUP_LAYER);
        capas.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                capaAcceso.setBounds(0, 0, capas.getWidth(), capas.getHeight());
                capaAcceso.doLayout();
            }
        });
        getRootPane().registerKeyboardAction(e -> cerrarAcceso(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void abrirAcceso() {
        if (usuario != null) return;
        NotificacionesRentCar.cerrarCentro();
        if (capaAcceso.isVisible()) {
            cerrarAcceso();
            return;
        }
        JLayeredPane capas = getLayeredPane();
        capaAcceso.setBounds(0, 0, capas.getWidth(), capas.getHeight());
        acceso.mostrarTab(0);
        acceso.limpiarMensaje();
        capaAcceso.setVisible(true);
        capaAcceso.doLayout();
        capaAcceso.revalidate();
        capaAcceso.repaint();
        acceso.alMostrar();
    }

    public void cerrarAcceso() {
        if (capaAcceso != null && capaAcceso.isVisible()) {
            capaAcceso.setVisible(false);
            getLayeredPane().repaint();
        }
    }

    // =========================================================
    // MODO CLARO / OSCURO
    // =========================================================

    private JComponent crearBotonTema() {
        final boolean oscuro = TemaRentCar.isOscuro();
        return new JComponent() {
            private boolean encima;
            {
                setPreferredSize(new Dimension(42, 42));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setToolTipText(oscuro ? "Cambiar a modo claro" : "Cambiar a modo oscuro");
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) { cambiarTema(); }
                    @Override public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { encima = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(encima ? TemaRentCar.CAMPO : TemaRentCar.PANEL);
                g2.fillOval(1, 1, w - 2, h - 2);
                g2.setColor(TemaRentCar.BORDE);
                g2.drawOval(1, 1, w - 3, h - 3);
                double cx = w / 2.0, cy = h / 2.0;
                g2.setColor(TemaRentCar.TEXTO);
                if (oscuro) {                       // sol -> volver al modo claro
                    g2.fill(new java.awt.geom.Ellipse2D.Double(cx - 5, cy - 5, 10, 10));
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    for (int i = 0; i < 8; i++) {
                        double a = Math.toRadians(i * 45);
                        g2.draw(new java.awt.geom.Line2D.Double(cx + Math.cos(a) * 8, cy + Math.sin(a) * 8,
                                cx + Math.cos(a) * 11, cy + Math.sin(a) * 11));
                    }
                } else {                            // luna -> pasar al modo oscuro
                    java.awt.geom.Area luna = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(cx - 8, cy - 8, 16, 16));
                    luna.subtract(new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(cx - 1, cy - 11, 15, 15)));
                    g2.fill(luna);
                }
                g2.dispose();
            }
        };
    }

    /** Cambia el tema y reconstruye la ventana con los colores nuevos (conserva sesión y pantalla). */
    private void cambiarTema() {
        TemaRentCar.aplicar(!TemaRentCar.isOscuro());

        final AppFrame anterior = this;
        final Usuario u = usuario;
        final String pantalla = actual;

        AppFrame nueva = new AppFrame();
        if ((anterior.getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0) {
            nueva.setBounds(anterior.getBounds());
        } else {
            nueva.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        if (u != null) {
            nueva.iniciarSesion(u);
            if (CATALOGO.equals(pantalla)) nueva.mostrar(CATALOGO);
        }
        nueva.setVisible(true);
        SwingUtilities.invokeLater(anterior::dispose);
    }

    // =========================================================
    // PESTAÑA
    // =========================================================

    private final class Pestana extends JComponent {
        private final String texto;
        private boolean activa;
        private boolean encima;

        Pestana(String texto, String destino) {
            this.texto = texto;
            setFont(new Font("SansSerif", Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { mostrar(destino); }
                @Override public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { encima = false; repaint(); }
            });
        }

        void setActiva(boolean a) { activa = a; repaint(); }

        @Override public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth(texto) + 24, 96);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(activa ? TemaRentCar.TEXTO : encima ? TemaRentCar.TEXTO : TemaRentCar.GRIS);
            g2.drawString(texto, (getWidth() - fm.stringWidth(texto)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            if (activa) {
                g2.setColor(TemaRentCar.ROJO);
                g2.fillRoundRect(10, getHeight() - 4, getWidth() - 20, 4, 4, 4);
            }
            g2.dispose();
        }
    }
}