package com.mycompany.rentcar.view;

import com.mycompany.rentcar.presenter.MarcaItem;
import com.mycompany.rentcar.util.FotoVehiculoUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.NumberFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class MainPublicView extends JFrame {

    // =========================================================
    // COMPONENTES DEL PRESENTER
    // =========================================================
    private JComboBox<MarcaItem> comboMarcas;
    private JButton btnVerTodos;
    private JButton btnLogin;

    private DefaultTableModel tableModel;
    private JTable tablaDatos;

    // =========================================================
    // COMPONENTES DE LA INTERFAZ
    // =========================================================
    private JPanel panelVehiculos;

    private JComboBox<String> comboLugar;

    private JButton btnFechaRecogida;
    private JButton btnFechaDevolucion;

    private Date fechaRecogida;
    private Date fechaDevolucion;

    private RangeSlider sliderPrecio;

    private JTextField lblPrecioMinimo;
    private JTextField lblPrecioMaximo;

    private JCheckBox chkTodos;
    private JCheckBox chkAuto;
    private JCheckBox chkFavoritos;
    private JLabel lblResultados;
    private JCheckBox chkCompacto;
    private JCheckBox chkSedan;
    private JCheckBox chkSUV;
    private JCheckBox chkPickup;
    private JCheckBox chkDisponibles;
    private JCheckBox chkMoto;
    private JCheckBox chkDeportiva, chkScooter, chkNaked, chkTouring, chkEnduro, chkTrabajo, chkOtra;
    private JCheckBox chkManual, chkAutomatica, chkSemiautomatica;
    private final Set<String> favoritos = new HashSet<>();
    private final List<String> reservasSesion = new ArrayList<>();
    private boolean mostrarSoloFavoritos = false;

    // =========================================================
    // COLORES
    // =========================================================
    private static final Color COLOR_FONDO = new Color(13, 14, 17);
    private static final Color COLOR_HEADER = new Color(15, 16, 20);
    private static final Color COLOR_PANEL = new Color(27, 29, 34);
    private static final Color COLOR_CARD = new Color(28, 29, 34);
    private static final Color COLOR_CAMPO = new Color(35, 37, 43);
    private static final Color COLOR_BORDE = new Color(49, 52, 59);
    private static final Color COLOR_TEXTO = new Color(239, 241, 244);
    private static final Color COLOR_SECUNDARIO = new Color(151, 156, 166);
    private static final Color COLOR_ROJO = new Color(224, 43, 49);
    private static final Color COLOR_ROJO_HOVER = new Color(244, 61, 68);
    private static final Color COLOR_VERDE = new Color(35, 67, 54);

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public MainPublicView() {
        aplicarTemaMenus();
        setTitle("RentCar - Catálogo de Vehículos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        crearModelo();
        crearInterfaz();
        escucharModelo();
        VentanaRentCar.instalar(this);
        setLocationRelativeTo(null);
    }

    // =========================================================
    // MODELO
    // =========================================================
    private void crearModelo() {
        tableModel = new DefaultTableModel(
                new Object[]{"Foto", "Placa", "Marca", "Modelo", "Precio/Día", "Tipo", "Año", "Color",
                    "Transmisión", "Combustible", "Capacidad", "Kilometraje", "Categoría",
                    "Descripción", "Ciudad", "Disponible", "Puertas"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDatos = new JTable(tableModel);
    }

    // =========================================================
    // INTERFAZ
    // =========================================================
    private void crearInterfaz() {
        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(COLOR_FONDO);
        principal.add(crearHeader(), BorderLayout.NORTH);
        principal.add(crearContenido(), BorderLayout.CENTER);
        setContentPane(principal);
    }

    // =========================================================
    // HEADER
    // =========================================================
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_HEADER);
        header.setPreferredSize(new Dimension(0, 125));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 17));
        izquierda.setOpaque(false);
        izquierda.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        izquierda.add(crearLogo());
        izquierda.add(crearNav("Vehículos", true));
        izquierda.add(crearNav("Mis reservas", false));
        izquierda.add(crearNav("Ayuda", false));
        header.add(izquierda, BorderLayout.WEST);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 38));
        derecha.setOpaque(false);

        JLabel ubicacion = new JLabel("⌖ Colombia");
        ubicacion.setForeground(COLOR_SECUNDARIO);
        ubicacion.setFont(new Font("SansSerif", Font.PLAIN, 11));
        derecha.add(ubicacion);

        btnLogin = new JButton("INICIAR SESIÓN");
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(COLOR_ROJO);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setBorder(BorderFactory.createEmptyBorder(13, 22, 13, 22));
        agregarHover(btnLogin);
        derecha.add(btnLogin);

        JLabel usuario = new JLabel("JD", SwingConstants.CENTER);
        usuario.setOpaque(true);
        usuario.setBackground(COLOR_CAMPO);
        usuario.setForeground(COLOR_TEXTO);
        usuario.setFont(new Font("SansSerif", Font.BOLD, 12));
        usuario.setPreferredSize(new Dimension(40, 40));
        usuario.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        derecha.add(usuario);

        header.add(derecha, BorderLayout.EAST);
        return header;
    }

    // =========================================================
    // LOGO
    // =========================================================
    private JLabel crearLogo() {
        JLabel logo = new JLabel();
        logo.setPreferredSize(new Dimension(200, 90));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setVerticalAlignment(SwingConstants.CENTER);

        try {
            java.net.URL url = getClass().getResource("/images/rentcar_logo.png");
            if (url != null) {
                ImageIcon original = new ImageIcon(url);
                int anchoOriginal = original.getIconWidth();
                int altoOriginal = original.getIconHeight();
                double escala = Math.min(200.0 / anchoOriginal, 90.0 / altoOriginal);
                int ancho = Math.max(1, (int) Math.round(anchoOriginal * escala));
                int alto = Math.max(1, (int) Math.round(altoOriginal * escala));
                BufferedImage suavizada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = suavizada.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(original.getImage(), 0, 0, ancho, alto, null);
                g2.dispose();
                logo.setIcon(new ImageIcon(suavizada));
            } else {
                ponerLogoTexto(logo);
            }
        } catch (Exception e) {
            ponerLogoTexto(logo);
        }
        return logo;
    }

    private void ponerLogoTexto(JLabel logo) {
        logo.setText("<html><div style='text-align:center;'>"
                + "<font size='6'><b>RentCar.</b></font><br>"
                + "<font size='2'>ALQUILER DE VEHÍCULOS</font></div></html>");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
    }

    // =========================================================
    // NAVEGACIÓN
    // =========================================================
    private JLabel crearNav(String texto, boolean activo) {
        JLabel label = new JLabel(texto);
        label.setForeground(activo ? COLOR_TEXTO : COLOR_SECUNDARIO);
        label.setFont(new Font("SansSerif", activo ? Font.BOLD : Font.PLAIN, 13));

        if (activo) {
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 3, 0, COLOR_ROJO),
                    BorderFactory.createEmptyBorder(18, 6, 15, 6)));
        } else {
            label.setBorder(BorderFactory.createEmptyBorder(18, 6, 18, 6));
        }

        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (texto.equals("Ayuda")) {
                    mostrarAyuda();
                } else if (texto.equals("Mis reservas")) {
                    String contenido = reservasSesion.isEmpty()
                            ? "Todavía no has solicitado reservas en esta sesión."
                            : String.join("\n\n", reservasSesion);
                    JOptionPane.showMessageDialog(MainPublicView.this, contenido, "Mis reservas", JOptionPane.INFORMATION_MESSAGE);
                } else if (texto.equals("Vehículos")) {
                    mostrarSoloFavoritos = false;
                    actualizarTarjetas();
                }
            }
        });
        return label;
    }

    // =========================================================
    // CONTENIDO
    // =========================================================
    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBackground(COLOR_FONDO);
        // Márgenes más compactos para dar prioridad visual al catálogo.
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 28, 16, 28));
        contenido.add(crearTitulo(), BorderLayout.NORTH);
        contenido.add(crearCatalogo(), BorderLayout.CENTER);
        return contenido;
    }

    // =========================================================
    // TITULO
    // =========================================================
    private JPanel crearTitulo() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel pequeno = new JLabel("RENTA TU PRÓXIMO VIAJE");
        pequeno.setForeground(COLOR_ROJO);
        pequeno.setFont(new Font("SansSerif", Font.BOLD, 9));
        textos.add(pequeno);
        textos.add(Box.createVerticalStrut(2));

        JLabel titulo = new JLabel("Encuentra tu próximo vehículo");
        titulo.setForeground(COLOR_TEXTO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(2));

        JLabel descripcion = new JLabel("Elige tus fechas y descubre opciones para tu viaje.");
        descripcion.setForeground(COLOR_SECUNDARIO);
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textos.add(descripcion);

        panel.add(textos, BorderLayout.WEST);
        return panel;
    }

    // =========================================================
    // CATALOGO
    // =========================================================
    private JPanel crearCatalogo() {
        JPanel principal = new JPanel(new BorderLayout(0, 8));
        principal.setOpaque(false);

        JPanel contenedorBusqueda = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contenedorBusqueda.setOpaque(false);
        contenedorBusqueda.add(crearBarraBusqueda());
        principal.add(contenedorBusqueda, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(20, 0));
        cuerpo.setOpaque(false);

        JScrollPane scrollFiltros = new JScrollPane(crearFiltros());
        scrollFiltros.setPreferredSize(new Dimension(262, 470));
        scrollFiltros.setMinimumSize(new Dimension(262, 120));
        scrollFiltros.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollFiltros.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollFiltros.setBorder(BorderFactory.createEmptyBorder());
        scrollFiltros.setOpaque(false);
        scrollFiltros.getViewport().setOpaque(false);
        scrollFiltros.getVerticalScrollBar().setUnitIncrement(16);
        estilizarBarraDesplazamiento(scrollFiltros);
        cuerpo.add(scrollFiltros, BorderLayout.WEST);

        panelVehiculos = new PanelVehiculosResponsive();
        panelVehiculos.setOpaque(false);

        JScrollPane scroll = new JScrollPane(panelVehiculos);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        estilizarBarraDesplazamiento(scroll);

        cuerpo.add(scroll, BorderLayout.CENTER);
        principal.add(cuerpo, BorderLayout.CENTER);
        return principal;
    }

    // =========================================================
    // BARRA DE BUSQUEDA
    // =========================================================
    private JPanel crearBarraBusqueda() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 14, 0));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        panel.setPreferredSize(new Dimension(900, 64));
        panel.setMaximumSize(new Dimension(960, 64));

        comboLugar = new JComboBox<>(obtenerLugaresColombia());
        comboLugar.addActionListener(e -> actualizarTarjetas());
        comboLugar.setBackground(COLOR_CAMPO);
        comboLugar.setForeground(COLOR_TEXTO);
        comboLugar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(crearCampoConComponente("Lugar de recogida", comboLugar));

        fechaRecogida = sumarDias(new Date(), 1);
        btnFechaRecogida = new JButton(formatearFecha(fechaRecogida));
        configurarBotonFecha(btnFechaRecogida);
        btnFechaRecogida.addActionListener(e -> mostrarCalendario(true));
        panel.add(crearCampoConComponente("Recogida", btnFechaRecogida));

        fechaDevolucion = sumarDias(new Date(), 2);
        btnFechaDevolucion = new JButton(formatearFecha(fechaDevolucion));
        configurarBotonFecha(btnFechaDevolucion);
        btnFechaDevolucion.addActionListener(e -> mostrarCalendario(false));
        panel.add(crearCampoConComponente("Devolución", btnFechaDevolucion));

        JButton buscar = crearBoton("Buscar vehículos   🔍", COLOR_ROJO);
        buscar.addActionListener(this::realizarBusqueda);
        agregarHover(buscar);
        panel.add(buscar);

        return panel;
    }

    private JPanel crearCampoConComponente(String titulo, JComponent componente) {
        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.setBackground(COLOR_CAMPO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        JLabel label = new JLabel(titulo);
        label.setForeground(new Color(205, 211, 221));
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));

        panel.add(label, BorderLayout.NORTH);
        panel.add(componente, BorderLayout.CENTER);
        return panel;
    }

    private void configurarBotonFecha(JButton boton) {
        boton.setForeground(COLOR_TEXTO);
        boton.setBackground(COLOR_CAMPO);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // =========================================================
    // UTILIDADES DE ESTILO Y TEXTO
    // =========================================================
    private void estilizarBarraDesplazamiento(JScrollPane scroll) {
        Color pista = new Color(17, 19, 24);
        Color pulgar = new Color(65, 70, 80);
        Color acento = new Color(224, 43, 49);
        for (JScrollBar barra : new JScrollBar[]{scroll.getVerticalScrollBar(), scroll.getHorizontalScrollBar()}) {
            barra.setBackground(pista);
            barra.setForeground(pulgar);
            barra.setPreferredSize(new Dimension(12, 12));
            barra.setUI(new BasicScrollBarUI() {
                @Override
                protected void configureScrollBarColors() {
                    thumbColor = pulgar;
                    trackColor = pista;
                }

                @Override
                protected JButton createDecreaseButton(int orientation) {
                    return botonVacioBarra();
                }

                @Override
                protected JButton createIncreaseButton(int orientation) {
                    return botonVacioBarra();
                }

                @Override
                protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                    if (r.isEmpty() || !scrollbar.isEnabled()) {
                        return;
                    }
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(isDragging ? acento : pulgar);
                    g2.fillRoundRect(r.x + 2, r.y + 2, Math.max(3, r.width - 4), Math.max(3, r.height - 4), 8, 8);
                    g2.dispose();
                }
            });
        }
    }

    private JButton botonVacioBarra() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        b.setVisible(false);
        return b;
    }

    private String normalizarTexto(String texto) {
        return Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
    }

    private String ciudadBase(String ubicacion) {
        int separador = ubicacion.indexOf(" · ");
        return separador >= 0 ? ubicacion.substring(0, separador).trim() : ubicacion.trim();
    }

    private void aplicarTemaMenus() {
        Color fondo = new Color(27, 29, 34);
        Color campo = new Color(35, 37, 43);
        Color texto = new Color(239, 241, 244);
        Color acento = new Color(224, 43, 49);
        UIManager.put("MenuBar.background", fondo);
        UIManager.put("MenuBar.foreground", texto);
        UIManager.put("Menu.background", fondo);
        UIManager.put("Menu.foreground", texto);
        UIManager.put("Menu.selectionBackground", acento);
        UIManager.put("Menu.selectionForeground", Color.WHITE);
        UIManager.put("MenuItem.background", fondo);
        UIManager.put("MenuItem.foreground", texto);
        UIManager.put("MenuItem.selectionBackground", acento);
        UIManager.put("MenuItem.selectionForeground", Color.WHITE);
        UIManager.put("PopupMenu.background", fondo);
        UIManager.put("ComboBox.background", campo);
        UIManager.put("ComboBox.foreground", texto);
        UIManager.put("List.background", campo);
        UIManager.put("List.foreground", texto);
        UIManager.put("ScrollBar.thumb", new Color(65, 70, 80));
        UIManager.put("ScrollBar.track", new Color(17, 19, 24));
    }

    private String[] obtenerLugaresColombia() {
        return new String[]{
            "Todas las ciudades",
            "Medellín · El Poblado",
            "Medellín · Aeropuerto JMC",
            "Bogotá · Aeropuerto El Dorado",
            "Bogotá · Chapinero",
            "Bogotá · Zona T",
            "Cartagena · Centro Histórico",
            "Cartagena · Aeropuerto Rafael Núñez",
            "Cali · Granada",
            "Cali · Aeropuerto Alfonso Bonilla Aragón",
            "Barranquilla · Riomar",
            "Barranquilla · Aeropuerto Ernesto Cortissoz",
            "Santa Marta · Rodadero",
            "Santa Marta · Centro",
            "San Andrés · Centro",
            "Pereira · Centro",
            "Pereira · Aeropuerto Matecaña",
            "Bucaramanga · Cabecera",
            "Manizales · Centro",
            "Armenia · Centro",
            "Villavicencio · Centro"
        };
    }

    // =========================================================
    // FILTROS
    // =========================================================
    private JPanel crearFiltros() {

        FiltrosPanel panel = new FiltrosPanel();
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // ---------------- CABECERA ----------------
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);

        JLabel titulo = new JLabel("Filtros");
        titulo.setForeground(COLOR_TEXTO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        encabezado.add(titulo, BorderLayout.WEST);

        btnVerTodos = new JButton("Limpiar");
        btnVerTodos.setForeground(COLOR_ROJO_HOVER);
        btnVerTodos.setBackground(COLOR_PANEL);
        btnVerTodos.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnVerTodos.setFocusPainted(false);
        btnVerTodos.setBorderPainted(false);
        btnVerTodos.setContentAreaFilled(false);
        btnVerTodos.setBorder(BorderFactory.createEmptyBorder());
        btnVerTodos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerTodos.addActionListener(e -> limpiarFiltros());
        encabezado.add(btnVerTodos, BorderLayout.EAST);
        agregarFiltro(panel, encabezado);

        lblResultados = new JLabel("Todos los vehículos");
        lblResultados.setForeground(COLOR_SECUNDARIO);
        lblResultados.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblResultados.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        agregarFiltro(panel, lblResultados);

        // ---------------- TIPO ----------------
        agregarSeccion(panel, "TIPO DE VEHÍCULO");
        chkTodos = crearChip("Todos", true);
        chkAuto = crearChip("Autos", false);
        chkMoto = crearChip("Motos", false);
        agregarFiltro(panel, crearGrilla(3, chkTodos, chkAuto, chkMoto));

        // ---------------- PRECIO ----------------
        agregarSeccion(panel, "PRECIO POR DÍA");
        lblPrecioMinimo = crearCampoPrecioFiltro(true);
        lblPrecioMaximo = crearCampoPrecioFiltro(false);

        JPanel camposPrecio = new JPanel(new GridLayout(1, 2, 10, 0));
        camposPrecio.setOpaque(false);
        camposPrecio.add(crearCampoEtiquetado("Mínimo", lblPrecioMinimo));
        camposPrecio.add(crearCampoEtiquetado("Máximo", lblPrecioMaximo));
        fijarAltura(camposPrecio);
        agregarFiltro(panel, camposPrecio);
        panel.add(Box.createVerticalStrut(8));

        sliderPrecio = new RangeSlider(0, 2000000);
        sliderPrecio.setMinimumValue(0);
        sliderPrecio.setMaximumValue(2000000);
        sliderPrecio.setPreferredSize(new Dimension(150, 38));
        sliderPrecio.setMinimumSize(new Dimension(60, 38));
        sliderPrecio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        lblPrecioMinimo.setText(formatearPrecio(0));
        lblPrecioMaximo.setText(formatearPrecio(2000000));
        sliderPrecio.addChangeListener(e -> actualizarPrecio());
        agregarFiltro(panel, sliderPrecio);

        // ---------------- MARCA ----------------
        agregarSeccion(panel, "MARCA");
        comboMarcas = new JComboBox<>();
        comboMarcas.setBackground(COLOR_CAMPO);
        comboMarcas.setForeground(COLOR_TEXTO);
        comboMarcas.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comboMarcas.setPreferredSize(new Dimension(150, 36));
        comboMarcas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        comboMarcas.addActionListener(e -> actualizarTarjetas());
        agregarFiltro(panel, comboMarcas);

        // ---------------- CATEGORÍA ----------------
        agregarSeccion(panel, "CATEGORÍA");
        chkCompacto = crearChip("Compacto", false);
        chkSedan = crearChip("Sedán", false);
        chkSUV = crearChip("SUV", false);
        chkPickup = crearChip("Pickup", false);
        chkDeportiva = crearChip("Deportiva", false);
        chkScooter = crearChip("Scooter", false);
        chkNaked = crearChip("Naked", false);
        chkTouring = crearChip("Touring", false);
        chkEnduro = crearChip("Enduro", false);
        chkTrabajo = crearChip("Trabajo", false);
        chkOtra = crearChip("Otra", false);
        agregarFiltro(panel, crearGrilla(2, chkCompacto, chkSedan, chkSUV, chkPickup, chkDeportiva,
                chkScooter, chkNaked, chkTouring, chkEnduro, chkTrabajo, chkOtra));

        // ---------------- TRANSMISIÓN ----------------
        agregarSeccion(panel, "TRANSMISIÓN");
        chkManual = crearChip("Manual", false);
        chkAutomatica = crearChip("Automática", false);
        chkSemiautomatica = crearChip("Semiautomática", false);
        agregarFiltro(panel, crearGrilla(1, chkManual, chkAutomatica, chkSemiautomatica));

        // ---------------- DISPONIBILIDAD ----------------
        agregarSeccion(panel, "DISPONIBILIDAD");
        chkDisponibles = crearChip("Solo disponibles", false);
        chkFavoritos = crearChip("♥  Solo favoritos", false);
        agregarFiltro(panel, crearGrilla(1, chkDisponibles, chkFavoritos));

        panel.add(Box.createVerticalGlue());

        // ---------------- COMPORTAMIENTO ----------------
        // "Todos" y "Autos/Motos" se excluyen entre sí; nunca queda el tipo vacío.
        // La lógica y el refresco van en el mismo listener para que no haya desfase.
        chkTodos.addActionListener(e -> {
            chkTodos.setSelected(true);
            chkAuto.setSelected(false);
            chkMoto.setSelected(false);
            actualizarTarjetas();
        });
        java.awt.event.ActionListener alCambiarTipo = e -> {
            chkTodos.setSelected(!chkAuto.isSelected() && !chkMoto.isSelected());
            actualizarTarjetas();
        };
        chkAuto.addActionListener(alCambiarTipo);
        chkMoto.addActionListener(alCambiarTipo);

        chkFavoritos.addActionListener(e -> {
            mostrarSoloFavoritos = chkFavoritos.isSelected();
            actualizarTarjetas();
        });

        for (JCheckBox filtro : new JCheckBox[]{chkCompacto, chkSedan, chkSUV, chkPickup, chkDeportiva,
            chkScooter, chkNaked, chkTouring, chkEnduro, chkTrabajo, chkOtra,
            chkManual, chkAutomatica, chkSemiautomatica, chkDisponibles}) {
            filtro.addActionListener(e -> actualizarTarjetas());
        }

        return panel;
    }

    // ---------- Ayudas de construcción del panel de filtros ----------
    private void agregarFiltro(JPanel panel, JComponent componente) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(componente);
    }

    private void agregarSeccion(JPanel panel, String titulo) {
        panel.add(Box.createVerticalStrut(16));
        JSeparator separador = crearSeparador();
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        agregarFiltro(panel, separador);
        panel.add(Box.createVerticalStrut(14));
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setForeground(COLOR_TEXTO);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 12));
        agregarFiltro(panel, etiqueta);
        panel.add(Box.createVerticalStrut(10));
    }

    private JCheckBox crearChip(String texto, boolean seleccionado) {
        return new FiltroChip(texto, seleccionado);
    }

    private JPanel crearGrilla(int columnas, JComponent... elementos) {
        JPanel grilla = new JPanel(new GridLayout(0, columnas, 8, 8));
        grilla.setOpaque(false);
        for (JComponent elemento : elementos) {
            grilla.add(elemento);
        }
        fijarAltura(grilla);
        return grilla;
    }

    private void fijarAltura(JComponent componente) {
        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, componente.getPreferredSize().height));
    }

    private JPanel crearCampoEtiquetado(String texto, JComponent campo) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(COLOR_SECUNDARIO);
        etiqueta.setFont(new Font("SansSerif", Font.PLAIN, 11));
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);
        return panel;
    }

    private JTextField crearCampoPrecioFiltro(boolean esMinimo) {
        JTextField campo = new JTextField();
        campo.setBackground(COLOR_CAMPO);
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_ROJO);
        campo.setFont(new Font("SansSerif", Font.BOLD, 12));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(7, 8, 7, 8)));
        campo.setToolTipText("Escribe un valor y presiona Enter");
        campo.addActionListener(e -> aplicarPrecioEscrito(campo, esMinimo));
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                aplicarPrecioEscrito(campo, esMinimo);
            }
        });
        return campo;
    }

    /**
     * Aplica al slider el precio escrito a mano en el campo mínimo o máximo.
     */
    private void aplicarPrecioEscrito(JTextField campo, boolean esMinimo) {
        if (sliderPrecio == null) {
            return;
        }
        String digitos = campo.getText().replaceAll("[^0-9]", "");
        if (digitos.isEmpty()) {
            actualizarPrecio();
            return;
        }
        long valor;
        try {
            valor = Long.parseLong(digitos.length() > 9 ? digitos.substring(0, 9) : digitos);
        } catch (NumberFormatException ex) {
            actualizarPrecio();
            return;
        }
        int v = (int) Math.max(0, Math.min(2000000L, valor));
        v = Math.round(v / 10000f) * 10000;
        if (esMinimo) {
            sliderPrecio.setMinimumValue(v);
        } else {
            sliderPrecio.setMaximumValue(v);
        }
    }

    private void actualizarResultados(int mostrados, int total) {
        if (lblResultados == null) {
            return;
        }
        if (mostrados == total) {
            lblResultados.setText(total + (total == 1 ? " vehículo" : " vehículos"));
        } else {
            lblResultados.setText(mostrados + " de " + total + " vehículos");
        }
    }

    /**
     * Botón tipo "chip": se pinta de rojo cuando está activo.
     */
    private static class FiltroChip extends JCheckBox {

        private static final Icon VACIO = new Icon() {
            @Override
            public int getIconWidth() {
                return 0;
            }

            @Override
            public int getIconHeight() {
                return 0;
            }

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
            }
        };

        FiltroChip(String texto, boolean seleccionado) {
            super(texto, seleccionado);
            setIcon(VACIO);
            setSelectedIcon(VACIO);
            setRolloverIcon(VACIO);
            setRolloverSelectedIcon(VACIO);
            setPressedIcon(VACIO);
            setOpaque(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setRolloverEnabled(true);
            setHorizontalAlignment(SwingConstants.CENTER);
            setHorizontalTextPosition(SwingConstants.CENTER);
            setIconTextGap(0);
            setMargin(new Insets(0, 4, 0, 4));
            setFont(new Font("SansSerif", Font.BOLD, 11));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(80, 32));
        }

        @Override
        public Color getForeground() {
            if (getModel() == null) {
                return super.getForeground();
            }
            if (isSelected()) {
                return Color.WHITE;
            }
            return getModel().isRollover() ? COLOR_TEXTO : COLOR_SECUNDARIO;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean activo = isSelected();
            boolean hover = getModel().isRollover();
            Color fondo = activo ? (hover ? COLOR_ROJO_HOVER : COLOR_ROJO)
                    : (hover ? new Color(46, 49, 57) : COLOR_CAMPO);
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.setColor(activo ? COLOR_ROJO : (hover ? COLOR_SECUNDARIO : COLOR_BORDE));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Panel de filtros: se ajusta al ancho visible y se estira si sobra alto.
     */
    private static class FiltrosPanel extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return Math.max(24, visibleRect.height - 24);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return getParent() instanceof JViewport && getParent().getHeight() > getPreferredSize().height;
        }
    }

    private void actualizarPrecio() {
        if (sliderPrecio == null) {
            return;
        }
        lblPrecioMinimo.setText(formatearPrecio(sliderPrecio.getMinimumValue()));
        lblPrecioMaximo.setText(formatearPrecio(sliderPrecio.getMaximumValue()));
        actualizarTarjetas();
    }

    private void limpiarFiltros() {

        chkTodos.setSelected(true);
        for (JCheckBox filtro : new JCheckBox[]{chkAuto, chkMoto, chkCompacto, chkSedan, chkSUV, chkPickup,
            chkDeportiva, chkScooter, chkNaked, chkTouring, chkEnduro, chkTrabajo, chkOtra,
            chkManual, chkAutomatica, chkSemiautomatica, chkDisponibles, chkFavoritos}) {
            filtro.setSelected(false);
        }
        mostrarSoloFavoritos = false;

        sliderPrecio.setMinimumValue(0);
        sliderPrecio.setMaximumValue(2000000);

        if (comboMarcas.getItemCount() > 0) {
            comboMarcas.setSelectedIndex(0);
        }
        if (comboLugar.getItemCount() > 0) {
            comboLugar.setSelectedIndex(0);
        }

        fechaRecogida = sumarDias(new Date(), 1);
        fechaDevolucion = sumarDias(new Date(), 2);
        btnFechaRecogida.setText(formatearFecha(fechaRecogida));
        btnFechaDevolucion.setText(formatearFecha(fechaDevolucion));

        actualizarPrecio();
    }

    // =========================================================
    // CALENDARIO
    // =========================================================
    private void mostrarCalendario(boolean esRecogida) {
        Date fechaInicial = esRecogida ? fechaRecogida : fechaDevolucion;
        CalendarioDialog dialog = new CalendarioDialog(this, fechaInicial, esRecogida);
        dialog.setVisible(true);
    }

    private class CalendarioDialog extends JDialog {

        private Calendar calendario;
        private JLabel lblMes;
        private JPanel panelDias;

        private final SimpleDateFormat formatoMes
                = new SimpleDateFormat("MMMM yyyy", new Locale("es", "CO"));

        CalendarioDialog(Frame parent, Date fecha, boolean esRecogida) {
            super(parent, "Seleccionar fecha", true);
            calendario = Calendar.getInstance();
            calendario.setTime(fecha);
            setSize(390, 390);
            setResizable(false);
            setLocationRelativeTo(parent);
            crearCalendario(esRecogida);
        }

        private void crearCalendario(boolean esRecogida) {
            JPanel principal = new JPanel(new BorderLayout(0, 10));
            principal.setBackground(COLOR_PANEL);
            principal.setBorder(new EmptyBorder(15, 15, 15, 15));

            JPanel cabecera = new JPanel(new BorderLayout());
            cabecera.setOpaque(false);

            JButton anterior = new JButton("‹");
            JButton siguiente = new JButton("›");
            configurarBotonCalendario(anterior);
            configurarBotonCalendario(siguiente);

            lblMes = new JLabel("", SwingConstants.CENTER);
            lblMes.setForeground(COLOR_TEXTO);
            lblMes.setFont(new Font("SansSerif", Font.BOLD, 16));

            cabecera.add(anterior, BorderLayout.WEST);
            cabecera.add(lblMes, BorderLayout.CENTER);
            cabecera.add(siguiente, BorderLayout.EAST);
            principal.add(cabecera, BorderLayout.NORTH);

            panelDias = new JPanel(new GridLayout(7, 7, 4, 4));
            panelDias.setOpaque(false);
            principal.add(panelDias, BorderLayout.CENTER);

            JButton cancelar = new JButton("Cancelar");
            cancelar.setForeground(COLOR_SECUNDARIO);
            cancelar.setBackground(COLOR_CAMPO);
            cancelar.setFocusPainted(false);
            cancelar.addActionListener(e -> dispose());
            principal.add(cancelar, BorderLayout.SOUTH);

            anterior.addActionListener(e -> {
                calendario.add(Calendar.MONTH, -1);
                actualizarCalendario(esRecogida);
            });

            siguiente.addActionListener(e -> {
                calendario.add(Calendar.MONTH, 1);
                actualizarCalendario(esRecogida);
            });

            setContentPane(principal);
            actualizarCalendario(esRecogida);
        }

        private void configurarBotonCalendario(JButton boton) {
            boton.setForeground(COLOR_TEXTO);
            boton.setBackground(COLOR_CAMPO);
            boton.setFont(new Font("SansSerif", Font.BOLD, 20));
            boton.setFocusPainted(false);
            boton.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        }

        private void actualizarCalendario(boolean esRecogida) {
            panelDias.removeAll();
            lblMes.setText(capitalizar(formatoMes.format(calendario.getTime())));

            String[] nombresDias = {"L", "M", "X", "J", "V", "S", "D"};
            for (String dia : nombresDias) {
                JLabel label = new JLabel(dia, SwingConstants.CENTER);
                label.setForeground(COLOR_SECUNDARIO);
                label.setFont(new Font("SansSerif", Font.BOLD, 10));
                panelDias.add(label);
            }

            Calendar primero = (Calendar) calendario.clone();
            primero.set(Calendar.DAY_OF_MONTH, 1);

            int diaSemana = primero.get(Calendar.DAY_OF_WEEK);
            int espacios = diaSemana == Calendar.SUNDAY ? 6 : diaSemana - 2;

            for (int i = 0; i < espacios; i++) {
                panelDias.add(new JLabel());
            }

            int cantidadDias = primero.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (int dia = 1; dia <= cantidadDias; dia++) {
                final int diaSeleccionado = dia;

                JButton boton = new JButton(String.valueOf(dia));
                boton.setFocusPainted(false);
                boton.setFont(new Font("SansSerif", Font.PLAIN, 11));
                boton.setForeground(COLOR_TEXTO);
                boton.setBackground(COLOR_CAMPO);
                boton.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));

                boton.addActionListener(e -> {
                    Calendar seleccion = (Calendar) calendario.clone();
                    seleccion.set(Calendar.DAY_OF_MONTH, diaSeleccionado);
                    Date fecha = seleccion.getTime();

                    if (esRecogida && fechaDevolucion != null && fecha.after(fechaDevolucion)) {
                        fechaDevolucion = sumarDias(fecha, 1);
                        btnFechaDevolucion.setText(formatearFecha(fechaDevolucion));
                    }

                    if (!esRecogida && fechaRecogida != null && fecha.before(fechaRecogida)) {
                        JOptionPane.showMessageDialog(CalendarioDialog.this,
                                "La devolución no puede ser anterior a la recogida.",
                                "Fecha no válida", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (esRecogida) {
                        fechaRecogida = fecha;
                        btnFechaRecogida.setText(formatearFecha(fecha));
                    } else {
                        fechaDevolucion = fecha;
                        btnFechaDevolucion.setText(formatearFecha(fecha));
                    }
                    dispose();
                });

                panelDias.add(boton);
            }

            panelDias.revalidate();
            panelDias.repaint();
        }
    }

    // =========================================================
    // RANGE SLIDER (precio mínimo y máximo)
    // =========================================================
    private class RangeSlider extends JComponent {

        private final int minimoPermitido;
        private final int maximoPermitido;

        private int valorMinimo;
        private int valorMaximo;

        private boolean arrastrandoMinimo;
        private boolean arrastrandoMaximo;

        private final EventListenerList listeners = new EventListenerList();

        private RangeSlider(int minimo, int maximo) {
            minimoPermitido = minimo;
            maximoPermitido = maximo;
            valorMinimo = minimo;
            valorMaximo = maximo;

            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            MouseAdapter mouse = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int x = e.getX();
                    int xMin = convertirAPosicion(valorMinimo);
                    int xMax = convertirAPosicion(valorMaximo);

                    if (Math.abs(x - xMin) < Math.abs(x - xMax)) {
                        arrastrandoMinimo = true;
                    } else {
                        arrastrandoMaximo = true;
                    }
                    actualizarDesdeMouse(x);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    actualizarDesdeMouse(e.getX());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    arrastrandoMinimo = false;
                    arrastrandoMaximo = false;
                }
            };

            addMouseListener(mouse);
            addMouseMotionListener(mouse);
        }

        private void actualizarDesdeMouse(int x) {
            int nuevoValor = convertirDesdePosicion(x);

            if (arrastrandoMinimo) {
                if (nuevoValor <= valorMaximo - 50000) {
                    valorMinimo = Math.max(minimoPermitido, nuevoValor);
                    fireChange();
                }
            } else if (arrastrandoMaximo) {
                if (nuevoValor >= valorMinimo + 50000) {
                    valorMaximo = Math.min(maximoPermitido, nuevoValor);
                    fireChange();
                }
            }
            repaint();
        }

        private int convertirAPosicion(int valor) {
            int ancho = Math.max(1, getWidth() - 20);
            double porcentaje = (double) (valor - minimoPermitido) / (maximoPermitido - minimoPermitido);
            return 10 + (int) (porcentaje * ancho);
        }

        private int convertirDesdePosicion(int x) {
            int ancho = Math.max(1, getWidth() - 20);
            x = Math.max(10, Math.min(getWidth() - 10, x));
            double porcentaje = (double) (x - 10) / ancho;
            int valor = minimoPermitido + (int) (porcentaje * (maximoPermitido - minimoPermitido));
            // Saltos de $10.000
            valor = Math.round(valor / 10000.0f) * 10000;
            return valor;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int izquierda = 10;
            int derecha = getWidth() - 10;
            int y = getHeight() / 2;

            g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(COLOR_BORDE);
            g.drawLine(izquierda, y, derecha, y);

            int xMin = convertirAPosicion(valorMinimo);
            int xMax = convertirAPosicion(valorMaximo);

            g.setColor(COLOR_ROJO);
            g.drawLine(xMin, y, xMax, y);

            g.setColor(Color.WHITE);
            g.fillOval(xMin - 7, y - 7, 14, 14);
            g.setColor(COLOR_ROJO);
            g.fillOval(xMin - 4, y - 4, 8, 8);

            g.setColor(Color.WHITE);
            g.fillOval(xMax - 7, y - 7, 14, 14);
            g.setColor(COLOR_ROJO);
            g.fillOval(xMax - 4, y - 4, 8, 8);

            g.dispose();
        }

        public int getMinimumValue() {
            return valorMinimo;
        }

        public int getMaximumValue() {
            return valorMaximo;
        }

        public void setMinimumValue(int valor) {
            valorMinimo = Math.max(minimoPermitido, Math.min(valor, valorMaximo - 50000));
            repaint();
            fireChange();
        }

        public void setMaximumValue(int valor) {
            valorMaximo = Math.min(maximoPermitido, Math.max(valor, valorMinimo + 50000));
            repaint();
            fireChange();
        }

        public void addChangeListener(javax.swing.event.ChangeListener listener) {
            listeners.add(javax.swing.event.ChangeListener.class, listener);
        }

        private void fireChange() {
            javax.swing.event.ChangeEvent evento = new javax.swing.event.ChangeEvent(this);
            for (javax.swing.event.ChangeListener listener
                    : listeners.getListeners(javax.swing.event.ChangeListener.class)) {
                listener.stateChanged(evento);
            }
        }
    }

    // =========================================================
    // SEPARADOR, ETIQUETA Y CHECKBOX
    // =========================================================
    private JSeparator crearSeparador() {
        JSeparator separador = new JSeparator();
        separador.setForeground(COLOR_BORDE);
        return separador;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(COLOR_TEXTO);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        return label;
    }

    private JCheckBox crearCheck(String texto, boolean seleccionado) {
        JCheckBox check = new JCheckBox(texto, seleccionado);
        check.setOpaque(false);
        check.setForeground(COLOR_SECUNDARIO);
        check.setFont(new Font("SansSerif", Font.PLAIN, 9));
        check.setFocusPainted(false);
        return check;
    }

    // =========================================================
    // ESCUCHAR MODELO
    // =========================================================
    private void escucharModelo() {
        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.INSERT
                    || e.getType() == TableModelEvent.DELETE
                    || e.getType() == TableModelEvent.UPDATE) {
                SwingUtilities.invokeLater(this::actualizarTarjetas);
            }
        });
    }

    // =========================================================
    // ACTUALIZAR TARJETAS
    // =========================================================
    private void actualizarTarjetas() {
        if (panelVehiculos == null) {
            return;
        }

        panelVehiculos.removeAll();

        int cantidad = tableModel.getRowCount();

        if (cantidad == 0) {
            JLabel vacio = new JLabel("No hay vehículos para mostrar.", SwingConstants.CENTER);
            vacio.setForeground(COLOR_SECUNDARIO);
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 13));
            panelVehiculos.setLayout(new BorderLayout());
            panelVehiculos.add(vacio, BorderLayout.CENTER);
            actualizarResultados(0, 0);
        } else {
            panelVehiculos.setLayout(new GridLayout(0, 3, 12, 16));

            for (int fila = 0; fila < cantidad; fila++) {
                String foto = obtenerValor(fila, 0);
                String placa = obtenerValor(fila, 1);
                String marca = obtenerValor(fila, 2);
                String modelo = obtenerValor(fila, 3);
                Object precio = tableModel.getValueAt(fila, 4);

                if (!cumpleFiltros(fila, precio)) {
                    continue;
                }
                panelVehiculos.add(crearTarjeta(foto, placa, marca, modelo, precio, fila));
            }

            actualizarResultados(panelVehiculos.getComponentCount(), cantidad);
            if (panelVehiculos.getComponentCount() == 0) {
                panelVehiculos.setLayout(new BorderLayout());
                JLabel sinCoincidencias = new JLabel(
                        "No hay vehículos que coincidan con esos filtros.", SwingConstants.CENTER);
                sinCoincidencias.setForeground(COLOR_SECUNDARIO);
                sinCoincidencias.setFont(new Font("SansSerif", Font.PLAIN, 14));
                panelVehiculos.add(sinCoincidencias, BorderLayout.CENTER);
            }
        }

        panelVehiculos.revalidate();
        panelVehiculos.repaint();
    }

    private String obtenerValor(int fila, int columna) {
        Object valor = tableModel.getValueAt(fila, columna);
        if (valor == null) {
            return "";
        }
        return valor.toString();
    }

    // =========================================================
    // TARJETA
    // =========================================================
    private JPanel crearTarjeta(String foto, String placa, String marca, String modelo,
            Object precio, int filaModelo) {

        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(COLOR_CARD);
        tarjeta.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));

        // ---------------- IMAGEN ----------------
        JPanel panelImagen = new JPanel();
        panelImagen.setLayout(new OverlayLayout(panelImagen));
        panelImagen.setBackground(new Color(31, 33, 38));
        panelImagen.setPreferredSize(new Dimension(300, 205));

        BufferedImage imagenOriginal = cargarImagenOriginal(foto);
        ImagenCoverLabel imagen = new ImagenCoverLabel(imagenOriginal);
        imagen.setAlignmentX(Component.CENTER_ALIGNMENT);
        imagen.setAlignmentY(Component.CENTER_ALIGNMENT);
        panelImagen.add(imagen);

        // Capa superior: estado y favorito encima de la foto
        JPanel controlesImagen = new JPanel(new BorderLayout());
        controlesImagen.setOpaque(false);
        controlesImagen.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlesImagen.setAlignmentY(Component.CENTER_ALIGNMENT);

        boolean disponible = Boolean.parseBoolean(valorFila(filaModelo, 15, "true"));

        JLabel estado = new JLabel(disponible ? "● Disponible" : "● No disponible");
        estado.setOpaque(true);
        estado.setBackground(disponible ? COLOR_VERDE : new Color(85, 38, 38));
        estado.setForeground(new Color(185, 235, 205));
        estado.setFont(new Font("SansSerif", Font.BOLD, 9));
        estado.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        JPanel estadoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 7));
        estadoPanel.setOpaque(false);
        estadoPanel.add(estado);
        controlesImagen.add(estadoPanel, BorderLayout.NORTH);

        JButton favorito = new JButton("♡");
        favorito.setForeground(Color.WHITE);
        favorito.setBackground(new Color(24, 25, 29));
        favorito.setFont(new Font("SansSerif", Font.BOLD, 20));
        favorito.setFocusPainted(false);
        favorito.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        favorito.setPreferredSize(new Dimension(38, 38));
        favorito.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (favoritos.contains(placa)) {
            favorito.setText("♥");
            favorito.setForeground(COLOR_ROJO);
        }

        favorito.addActionListener(e -> {
            if (favoritos.contains(placa)) {
                favoritos.remove(placa);
                favorito.setText("♡");
                favorito.setForeground(Color.WHITE);
            } else {
                favoritos.add(placa);
                favorito.setText("♥");
                favorito.setForeground(COLOR_ROJO);
            }
            if (chkFavoritos != null && chkFavoritos.isSelected()) {
                actualizarTarjetas();
            }
        });

        JPanel favoritoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 7));
        favoritoPanel.setOpaque(false);
        favoritoPanel.add(favorito);
        controlesImagen.add(favoritoPanel, BorderLayout.SOUTH);

        panelImagen.add(controlesImagen);
        tarjeta.add(panelImagen, BorderLayout.NORTH);

        // ---------------- INFORMACIÓN ----------------
        JPanel informacion = new JPanel();
        informacion.setOpaque(false);
        informacion.setBorder(BorderFactory.createEmptyBorder(8, 7, 12, 7));
        informacion.setLayout(new BoxLayout(informacion, BoxLayout.Y_AXIS));

        // *** CORREGIDO: logo a la izquierda y MARCA + MODELO escritos al lado ***
        JPanel cabeceraVehiculo = new JPanel(new BorderLayout(12, 0));
        cabeceraVehiculo.setOpaque(false);
        cabeceraVehiculo.setAlignmentX(Component.CENTER_ALIGNMENT);
        cabeceraVehiculo.setPreferredSize(new Dimension(300, 84));
        cabeceraVehiculo.setMinimumSize(new Dimension(0, 84));
        cabeceraVehiculo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));

        JLabel logoMarca = crearLogoMarca(marca);
        logoMarca.setHorizontalAlignment(SwingConstants.CENTER);
        cabeceraVehiculo.add(logoMarca, BorderLayout.WEST);

        String marcaVisible = (marca == null || marca.isBlank())
                ? "MARCA NO DISPONIBLE" : marca.toUpperCase(Locale.ROOT);
        String modeloVisible = (modelo == null || modelo.isBlank())
                ? "Modelo no disponible" : modelo;

        JLabel marcaLabel = new JLabel(marcaVisible);
        marcaLabel.setForeground(COLOR_TEXTO);
        marcaLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        marcaLabel.setToolTipText(marcaVisible);

        JLabel modeloLabel = new JLabel(modeloVisible);
        modeloLabel.setForeground(COLOR_SECUNDARIO);
        modeloLabel.setFont(new Font("SansSerif", Font.PLAIN, 17));
        modeloLabel.setToolTipText(modeloVisible);

        JPanel textosMarca = new JPanel();
        textosMarca.setOpaque(false);
        textosMarca.setLayout(new BoxLayout(textosMarca, BoxLayout.Y_AXIS));
        textosMarca.add(Box.createVerticalGlue());
        textosMarca.add(marcaLabel);
        textosMarca.add(Box.createVerticalStrut(2));
        textosMarca.add(modeloLabel);
        textosMarca.add(Box.createVerticalGlue());

        cabeceraVehiculo.add(textosMarca, BorderLayout.CENTER);

        informacion.add(cabeceraVehiculo);
        informacion.add(Box.createVerticalStrut(8));

        // Características
        boolean esMoto = valorFila(filaModelo, 5, "AUTO").equalsIgnoreCase("MOTO");
        JPanel caracteristicas = new JPanel(new GridLayout(2, 2, 8, 10));
        caracteristicas.setOpaque(false);
        caracteristicas.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        caracteristicas.setAlignmentX(Component.CENTER_ALIGNMENT);
        caracteristicas.setPreferredSize(new Dimension(380, 76));
        caracteristicas.setMinimumSize(new Dimension(0, 76));
        caracteristicas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        caracteristicas.add(crearCaracteristica("⚙  " + valorFila(filaModelo, 8, "No especificada")));
        caracteristicas.add(crearCaracteristica("⛽  " + valorFila(filaModelo, 9, "No especificado")));
        caracteristicas.add(crearCaracteristica("♙  " + valorFila(filaModelo, 10, esMoto ? "2" : "5") + " personas"));
        caracteristicas.add(crearCaracteristica("🚪  " + (esMoto
                ? "Puertas: N/A" : "Puertas: " + valorFila(filaModelo, 16, "4"))));
        informacion.add(caracteristicas);

        JLabel datosAdicionales = new JLabel(
                "Año  " + valorFila(filaModelo, 6, "—")
                + "     ·     " + valorFila(filaModelo, 7, "Color no indicado")
                + "     ·     " + valorFila(filaModelo, 14, "Ubicación no indicada"));
        datosAdicionales.setForeground(new Color(205, 211, 221));
        datosAdicionales.setFont(new Font("SansSerif", Font.PLAIN, 15));
        datosAdicionales.setHorizontalAlignment(SwingConstants.CENTER);
        datosAdicionales.setAlignmentX(Component.CENTER_ALIGNMENT);
        informacion.add(Box.createVerticalStrut(14));
        informacion.add(datosAdicionales);
        informacion.add(Box.createVerticalStrut(12));

        // Pie: placa/precio y acciones
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        pie.setOpaque(false);
        pie.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel bloquePrecio = new JPanel();
        bloquePrecio.setOpaque(false);
        bloquePrecio.setLayout(new BoxLayout(bloquePrecio, BoxLayout.Y_AXIS));
        bloquePrecio.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel placaSobrePrecio = crearPlacaColombiana(placa);
        placaSobrePrecio.setAlignmentX(Component.CENTER_ALIGNMENT);
        bloquePrecio.add(placaSobrePrecio);
        bloquePrecio.add(Box.createVerticalStrut(15));
        JLabel precioLabel = new JLabel(formatearPrecio(precio) + " / día");
        precioLabel.setForeground(COLOR_TEXTO);
        precioLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        precioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel moneda = new JLabel("COP · precio por día");
        moneda.setAlignmentX(Component.CENTER_ALIGNMENT);
        moneda.setForeground(COLOR_SECUNDARIO);
        moneda.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bloquePrecio.add(precioLabel);
        bloquePrecio.add(Box.createVerticalStrut(4));
        bloquePrecio.add(moneda);
        pie.add(bloquePrecio);

        JPanel acciones = new JPanel();
        acciones.setOpaque(false);
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));
        acciones.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton detalles = new JButton("Detalles ↗");
        detalles.setForeground(COLOR_TEXTO);
        detalles.setBackground(COLOR_CAMPO);
        detalles.setFont(new Font("SansSerif", Font.BOLD, 11));
        detalles.setFocusPainted(false);
        detalles.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        detalles.setPreferredSize(new Dimension(116, 36));
        detalles.setMaximumSize(new Dimension(116, 36));
        detalles.setAlignmentX(Component.RIGHT_ALIGNMENT);
        detalles.addActionListener(e -> mostrarDetalles(marca, modelo, placa, precio, filaModelo));

        JButton reservar = new JButton("RESERVAR");
        reservar.setForeground(Color.WHITE);
        reservar.setBackground(COLOR_ROJO);
        reservar.setFont(new Font("SansSerif", Font.BOLD, 12));
        reservar.setFocusPainted(false);
        reservar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        reservar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        reservar.setAlignmentX(Component.RIGHT_ALIGNMENT);
        reservar.setMaximumSize(new Dimension(150, 42));
        reservar.addActionListener(e -> reservarVehiculo(marca, modelo, placa, precio, filaModelo));

        acciones.add(detalles);
        acciones.add(Box.createVerticalStrut(7));
        acciones.add(reservar);
        pie.add(acciones);
        informacion.add(pie);

        tarjeta.add(informacion, BorderLayout.CENTER);
        return tarjeta;
    }

    /**
     * Placa inspirada en el formato colombiano, sin alterar el número
     * almacenado.
     */
    private JPanel crearPlacaColombiana(String placa) {
        JPanel placaPanel = new JPanel(new BorderLayout(0, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(247, 207, 43));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(28, 29, 31));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 7, 7);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        placaPanel.setOpaque(false);
        placaPanel.setPreferredSize(new Dimension(142, 39));
        placaPanel.setMaximumSize(new Dimension(142, 39));
        placaPanel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        JLabel pais = new JLabel("COLOMBIA");
        pais.setHorizontalAlignment(SwingConstants.CENTER);
        pais.setForeground(new Color(45, 45, 45));
        pais.setFont(new Font("SansSerif", Font.BOLD, 7));

        JLabel numero = new JLabel(placa == null ? "---" : placa.toUpperCase(Locale.ROOT));
        numero.setHorizontalAlignment(SwingConstants.CENTER);
        numero.setForeground(new Color(20, 20, 20));
        numero.setFont(new Font("Monospaced", Font.BOLD, 19));

        placaPanel.add(pais, BorderLayout.NORTH);
        placaPanel.add(numero, BorderLayout.CENTER);
        return placaPanel;
    }

    /**
     * Carga el emblema de la marca; si no hay imagen, muestra la inicial de la
     * marca.
     */
    private JLabel crearLogoMarca(String marca) {
        JLabel logo = new JLabel();
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setVerticalAlignment(SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(118, 76));
        logo.setMinimumSize(new Dimension(118, 76));
        logo.setMaximumSize(new Dimension(118, 76));
        logo.setOpaque(false);
        logo.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        String clave = marca == null ? "" : marca.trim().toLowerCase(Locale.ROOT);
        java.util.Map<String, String> nombres = new java.util.HashMap<>();
        nombres.put("royal enfield", "royalenfield.jpg");
        nombres.put("harley-davidson", "harley-davidson.jpg");
        nombres.put("bmw motorrad", "bmw motorroad.jpg");
        nombres.put("cfmoto", "cfmoto.jpg");
        nombres.put("tvs", "tvs.jpg");
        nombres.put("kymco", "kymco.jpg");
        nombres.put("ktm", "ktm.jpg");
        nombres.put("kawasaki", "kawasaki.jpg");
        nombres.put("honda", "hondamoto.jpg");
        nombres.put("hero", "hero.jpg");
        nombres.put("ducati", "ducati.jpg");
        nombres.put("benelli", "benelli.jpg");
        nombres.put("bajaj", "bajaj.jpg");
        nombres.put("auteco", "auteco.jpg");
        nombres.put("akt", "akt.jpg");
        nombres.put("yamaha", "yamaha.jpg");
        String archivo = nombres.getOrDefault(clave, clave.replaceAll("[^a-z0-9]+", "") + ".jpg");

        try {
            java.net.URL recurso = getClass().getResource("/images/marcas/" + archivo);
            if (recurso != null) {
                BufferedImage original = ImageIO.read(recurso);
                if (original != null) {
                    // Los logotipos JPG tienen fondo oscuro; se vuelve transparente.
                    BufferedImage sinFondo = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    for (int y = 0; y < original.getHeight(); y++) {
                        for (int x = 0; x < original.getWidth(); x++) {
                            int rgb = original.getRGB(x, y);
                            int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
                            int brillo = (r + g + b) / 3;
                            int alpha = brillo < 38 ? 0 : (brillo < 65 ? (brillo - 38) * 9 : 255);
                            sinFondo.setRGB(x, y, (alpha << 24) | (rgb & 0x00ffffff));
                        }
                    }
                    int w = sinFondo.getWidth(), h = sinFondo.getHeight();
                    // Recorta el margen transparente para que el logo ocupe toda su caja.
                    int minX = w, minY = h, maxX = -1, maxY = -1;
                    for (int yy = 0; yy < h; yy++) {
                        for (int xx = 0; xx < w; xx++) {
                            if (((sinFondo.getRGB(xx, yy) >>> 24) & 0xff) > 40) {
                                if (xx < minX) {
                                    minX = xx;
                                }
                                if (xx > maxX) {
                                    maxX = xx;
                                }
                                if (yy < minY) {
                                    minY = yy;
                                }
                                if (yy > maxY) {
                                    maxY = yy;
                                }
                            }
                        }
                    }
                    if (maxX > minX && maxY > minY) {
                        sinFondo = sinFondo.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
                        w = sinFondo.getWidth();
                        h = sinFondo.getHeight();
                    }
                    double escala = Math.min(108.0 / Math.max(1, w), 60.0 / Math.max(1, h));
                    int nw = Math.max(1, (int) Math.round(w * escala));
                    int nh = Math.max(1, (int) Math.round(h * escala));
                    Image mini = sinFondo.getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
                    logo.setIcon(new ImageIcon(mini));
                    logo.setText("");
                    return logo;
                }
            }
        } catch (Exception ignored) {
        }

        logo.setText(clave.isBlank() ? "?" : clave.substring(0, 1).toUpperCase(Locale.ROOT));
        logo.setFont(new Font("SansSerif", Font.BOLD, 30));
        logo.setForeground(COLOR_ROJO);
        return logo;
    }

    // =========================================================
    // IMAGEN DEL VEHÍCULO
    // =========================================================
    private BufferedImage cargarImagenOriginal(String ruta) {
        if (ruta == null || ruta.trim().isEmpty()) {
            return null;
        }

        try {
            File archivo = FotoVehiculoUtil.resolver(ruta);
            BufferedImage original = null;
            if (archivo != null && archivo.isFile()) {
                original = ImageIO.read(archivo);
            }

            // También permite imágenes guardadas dentro de resources.
            if (original == null) {
                java.net.URL recurso = getClass().getResource(ruta.startsWith("/") ? ruta : "/" + ruta);
                if (recurso != null) {
                    original = ImageIO.read(recurso);
                }
            }
            return original;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * La imagen llena todo el recuadro, mantiene proporción y recorta el
     * sobrante.
     */
    private static class ImagenCoverLabel extends JLabel {

        private final BufferedImage imagen;

        public ImagenCoverLabel(BufferedImage imagen) {
            this.imagen = imagen;
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            setBackground(new Color(31, 33, 38));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (imagen == null) {
                g.setColor(new Color(151, 156, 166));
                g.setFont(new Font("SansSerif", Font.BOLD, 12));
                String texto = "SIN FOTO";
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(texto)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g.drawString(texto, x, y);
                return;
            }

            int ancho = getWidth();
            int alto = getHeight();
            if (ancho <= 0 || alto <= 0) {
                return;
            }

            double escala = Math.max((double) ancho / imagen.getWidth(), (double) alto / imagen.getHeight());
            int nuevoAncho = (int) Math.ceil(imagen.getWidth() * escala);
            int nuevoAlto = (int) Math.ceil(imagen.getHeight() * escala);
            int x = (ancho - nuevoAncho) / 2;
            int y = (alto - nuevoAlto) / 2;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(imagen, x, y, nuevoAncho, nuevoAlto, null);
            g2.dispose();
        }
    }

    /**
     * Panel que adapta las tres columnas al ancho visible del JScrollPane.
     */
    private static class PanelVehiculosResponsive extends JPanel implements javax.swing.Scrollable {

        PanelVehiculosResponsive() {
            super(new GridLayout(0, 3, 12, 16));
            setMinimumSize(new Dimension(0, 0));
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return new Dimension(900, 600);
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return Math.max(24, visibleRect.height - 24);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    // =========================================================
    // CARACTERISTICA, BOTÓN, HOVER
    // =========================================================
    private JLabel crearCaracteristica(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(new Color(240, 243, 248));
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setToolTipText(texto);
        label.setMinimumSize(new Dimension(0, 24));
        return label;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("SansSerif", Font.BOLD, 11));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return boton;
    }

    private void agregarHover(JButton boton) {
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(COLOR_ROJO_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(COLOR_ROJO);
            }
        });
    }

    // =========================================================
    // BUSCAR
    // =========================================================
    private void realizarBusqueda(ActionEvent e) {
        if (fechaDevolucion.before(fechaRecogida)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de devolución debe ser posterior a la fecha de recogida.",
                    "Fechas no válidas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        actualizarTarjetas();

        String lugar = comboLugar.getSelectedItem().toString();
        String marca = comboMarcas.getSelectedItem() == null
                ? "Todas" : comboMarcas.getSelectedItem().toString();

        int minimo = sliderPrecio.getMinimumValue();
        int maximo = sliderPrecio.getMaximumValue();

        String mensaje = "<html>"
                + "<b>Búsqueda realizada</b><br><br>"
                + "📍 Lugar: " + lugar + "<br>"
                + "📅 Recogida: " + formatearFecha(fechaRecogida) + "<br>"
                + "📅 Devolución: " + formatearFecha(fechaDevolucion) + "<br>"
                + "🚗 Marca: " + marca + "<br>"
                + "💰 Precio: " + formatearPrecio(minimo) + " - " + formatearPrecio(maximo)
                + "</html>";

        JOptionPane.showMessageDialog(this, mensaje, "RentCar", JOptionPane.INFORMATION_MESSAGE);
        actualizarTarjetas();
    }

// =========================================================
    // DETALLES
    // =========================================================
    private void mostrarDetalles(String marca, String modelo, String placa, Object precio, int filaModelo) {
        String tipo = valorFila(filaModelo, 5, "AUTO");
        String tituloTipo = tipo.equalsIgnoreCase("MOTO") ? "Motocicleta" : "Automóvil";

        // Construimos el texto plano con saltos de línea limpios
        String contenido = tituloTipo.toUpperCase() + ": " + marca.toUpperCase() + " " + modelo.toUpperCase() + "\n\n"
                + "Placa: " + placa + "\n"
                + "Precio: " + formatearPrecio(precio) + " / día\n"
                + "Año: " + valorFila(filaModelo, 6, "No especificado") + "\n"
                + "Color: " + valorFila(filaModelo, 7, "No especificado") + "\n"
                + "Transmisión: " + valorFila(filaModelo, 8, "No especificada") + "\n"
                + "Combustible: " + valorFila(filaModelo, 9, "No especificado") + "\n"
                + "Capacidad: " + valorFila(filaModelo, 10, tipo.equalsIgnoreCase("MOTO") ? "2" : "5") + " personas\n"
                + "Puertas: " + (tipo.equalsIgnoreCase("MOTO") ? "No aplica" : valorFila(filaModelo, 16, "4")) + "\n"
                + "Kilometraje: " + valorFila(filaModelo, 11, "No especificado") + " km\n"
                + "Categoría: " + valorFila(filaModelo, 12, "No especificada") + "\n"
                + "Ciudad: " + valorFila(filaModelo, 14, "No especificada") + "\n"
                + "Disponibilidad: " + (Boolean.parseBoolean(valorFila(filaModelo, 15, "true")) ? "Disponible" : "No disponible") + "\n\n"
                + "Descripción: " + valorFila(filaModelo, 13, "Sin descripción");

        // Diálogo personalizado con el diseño oscuro de la app
        JDialog dialog = new JDialog(this, "Detalles del vehículo", true);
        dialog.setSize(480, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 15));
        panelPrincipal.setBackground(COLOR_PANEL);
        panelPrincipal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JLabel lblTitulo = new JLabel("Información del Vehículo");
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Usamos JTextPane con los colores exactos de los campos del catálogo (COLOR_CAMPO y COLOR_TEXTO)
        JTextPane txtInfo = new JTextPane();
        txtInfo.setText(contenido);
        txtInfo.setEditable(false);
        txtInfo.setBackground(COLOR_CAMPO);
        txtInfo.setForeground(COLOR_TEXTO); // Forzamos el color blanco/claro del texto del catálogo
        txtInfo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtInfo.setFocusable(false);
        txtInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        scrollInfo.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        scrollInfo.getViewport().setBackground(COLOR_CAMPO);

        // Aplicamos exactamente la misma barra de desplazamiento estilizada de tu catálogo
        estilizarBarraDesplazamiento(scrollInfo);
        panelPrincipal.add(scrollInfo, BorderLayout.CENTER);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelBoton.setOpaque(false);

        JButton btnCerrar = new JButton("Aceptar");
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBackground(COLOR_ROJO);
        btnCerrar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        agregarHover(btnCerrar);
        btnCerrar.addActionListener(e -> dialog.dispose());

        panelBoton.add(btnCerrar);
        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);

        dialog.setContentPane(panelPrincipal);
        dialog.setVisible(true);
    }

    private String valorFila(int fila, int columna, String defecto) {
        if (fila < 0 || fila >= tableModel.getRowCount() || columna >= tableModel.getColumnCount()) {
            return defecto;
        }
        Object v = tableModel.getValueAt(fila, columna);
        if (v == null || v.toString().isBlank() || v.toString().equals("0")) {
            return defecto;
        }
        return v.toString();
    }

    // =========================================================
    // FILTROS
    // =========================================================
    private boolean cumpleFiltros(int fila, Object precioObj) {
        String placa = obtenerValor(fila, 1);
        if (chkFavoritos != null && chkFavoritos.isSelected() && !favoritos.contains(placa)) {
            return false;
        }

        double precio = 0;
        try {
            precio = Double.parseDouble(String.valueOf(precioObj));
        } catch (Exception ignored) {
        }
        if (sliderPrecio != null && (precio < sliderPrecio.getMinimumValue() || precio > sliderPrecio.getMaximumValue())) {
            return false;
        }

        if (chkDisponibles != null && chkDisponibles.isSelected() && !Boolean.parseBoolean(valorFila(fila, 15, "true"))) {
            return false;
        }

        String tipo = valorFila(fila, 5, "AUTO").toUpperCase(Locale.ROOT);
        String categoria = valorFila(fila, 12, "").toLowerCase(Locale.ROOT);
        boolean pideMoto = chkMoto != null && chkMoto.isSelected();
        boolean pideAuto = chkAuto != null && chkAuto.isSelected();
        if (pideMoto && !pideAuto && !tipo.equals("MOTO")) {
            return false;
        }
        if (pideAuto && !pideMoto && tipo.equals("MOTO")) {
            return false;
        }

        boolean hayCategoriaSeleccionada = chkCompacto.isSelected() || chkSedan.isSelected() || chkSUV.isSelected()
                || chkPickup.isSelected() || chkDeportiva.isSelected() || chkScooter.isSelected()
                || chkNaked.isSelected() || chkTouring.isSelected() || chkEnduro.isSelected()
                || chkTrabajo.isSelected() || chkOtra.isSelected();
        if (hayCategoriaSeleccionada) {
            boolean coincide = (chkCompacto.isSelected() && categoria.contains("compacto"))
                    || (chkSedan.isSelected() && (categoria.contains("sed") || categoria.contains("sedán")))
                    || (chkSUV.isSelected() && categoria.contains("suv"))
                    || (chkPickup.isSelected() && categoria.contains("pickup"))
                    || (chkDeportiva.isSelected() && categoria.contains("deportiva"))
                    || (chkScooter.isSelected() && categoria.contains("scooter"))
                    || (chkNaked.isSelected() && categoria.contains("naked"))
                    || (chkTouring.isSelected() && categoria.contains("touring"))
                    || (chkEnduro.isSelected() && categoria.contains("enduro"))
                    || (chkTrabajo.isSelected() && categoria.contains("trabajo"))
                    || (chkOtra.isSelected() && (categoria.equals("otra") || categoria.equals("otro")));
            if (!coincide) {
                return false;
            }
        }

        String transmision = valorFila(fila, 8, "").trim().toLowerCase(Locale.ROOT);
        if (chkManual.isSelected() || chkAutomatica.isSelected() || chkSemiautomatica.isSelected()) {
            boolean coincideTransmision = (chkManual.isSelected() && transmision.equals("manual"))
                    || (chkAutomatica.isSelected() && (transmision.equals("automática") || transmision.equals("automatica")))
                    || (chkSemiautomatica.isSelected() && (transmision.equals("semiautomática") || transmision.equals("semiautomatica")));
            if (!coincideTransmision) {
                return false;
            }
        }

        String ciudadVehiculo = valorFila(fila, 14, "").trim();
        String lugarElegido = comboLugar == null || comboLugar.getSelectedItem() == null
                ? "Todas las ciudades" : comboLugar.getSelectedItem().toString().trim();
        if (!lugarElegido.equalsIgnoreCase("Todas las ciudades")) {
            if (ciudadVehiculo.isBlank()) {
                return false;
            }
            String ciudadVehiculoNormalizada = normalizarTexto(ciudadVehiculo);
            String lugarNormalizado = normalizarTexto(lugarElegido);
            String ciudadVehiculoBase = ciudadBase(ciudadVehiculoNormalizada);
            String lugarBase = ciudadBase(lugarNormalizado);
            boolean mismaUbicacion = ciudadVehiculoNormalizada.equals(lugarNormalizado);
            // Si una de las dos entradas solo contiene la ciudad, se compara por ciudad.
            if (!mismaUbicacion && !ciudadVehiculoNormalizada.contains(" · ") && !lugarNormalizado.contains(" · ")) {
                mismaUbicacion = ciudadVehiculoNormalizada.equals(lugarNormalizado);
            } else if (!mismaUbicacion && (!ciudadVehiculoNormalizada.contains(" · ") || !lugarNormalizado.contains(" · "))) {
                mismaUbicacion = ciudadVehiculoBase.equals(lugarBase);
            }
            if (!mismaUbicacion) {
                return false;
            }
        }

        Object marcaSel = comboMarcas == null ? null : comboMarcas.getSelectedItem();
        if (marcaSel != null && !"Todas las marcas".equalsIgnoreCase(marcaSel.toString())
                && !"Todas".equalsIgnoreCase(marcaSel.toString())
                && !obtenerValor(fila, 2).equalsIgnoreCase(marcaSel.toString())) {
            return false;
        }
        return true;
    }

    // =========================================================
    // RESERVAR
    // =========================================================
    private void reservarVehiculo(String marca, String modelo, String placa, Object precio, int fila) {
        if (!Boolean.parseBoolean(valorFila(fila, 15, "true"))) {
            mostrarMensajePersonalizado("Este vehículo no está disponible para alquiler.", "No disponible");
            return;
        }

        long dias = Math.max(1, (fechaDevolucion.getTime() - fechaRecogida.getTime()) / (24L * 60 * 60 * 1000));
        String resumen = (valorFila(fila, 5, "AUTO").equalsIgnoreCase("MOTO") ? "Moto" : "Auto") + " " + marca + " " + modelo
                + " (" + placa + ")\nLugar: " + comboLugar.getSelectedItem()
                + "\nFechas: " + formatearFecha(fechaRecogida) + " - " + formatearFecha(fechaDevolucion)
                + "\nTotal estimado: " + formatearPrecio(((Number) precio).doubleValue() * dias);

        boolean confirmado = mostrarConfirmacionReserva(
                resumen + "\n\n¿Agregar a Mis reservas de esta sesión?",
                "Solicitar reserva"
        );

        if (confirmado) {
            reservasSesion.add(resumen);
            mostrarMensajePersonalizado("Solicitud agregada a Mis reservas durante esta sesión.", "Reserva exitosa");
        }
    }

    private boolean mostrarConfirmacionReserva(String mensaje, String titulo) {
        JDialog dialog = new JDialog(this, titulo, true);
        dialog.setSize(440, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 15));
        panelPrincipal.setBackground(COLOR_PANEL);
        panelPrincipal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        JTextPane txtInfo = new JTextPane();
        txtInfo.setText(mensaje);
        txtInfo.setEditable(false);
        txtInfo.setBackground(COLOR_CAMPO);
        txtInfo.setForeground(COLOR_TEXTO);
        txtInfo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtInfo.setFocusable(false);
        txtInfo.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        scrollInfo.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        scrollInfo.getViewport().setBackground(COLOR_CAMPO);
        panelPrincipal.add(scrollInfo, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setOpaque(false);

        final boolean[] confirmado = {false};

        JButton btnSi = new JButton("Sí, agregar");
        btnSi.setForeground(Color.WHITE);
        btnSi.setBackground(COLOR_ROJO);
        btnSi.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSi.setFocusPainted(false);
        btnSi.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSi.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        agregarHover(btnSi);
        btnSi.addActionListener(e -> {
            confirmado[0] = true;
            dialog.dispose();
        });

        JButton btnNo = new JButton("Cancelar");
        btnNo.setForeground(COLOR_TEXTO);
        btnNo.setBackground(COLOR_CAMPO);
        btnNo.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnNo.setFocusPainted(false);
        btnNo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btnNo.addActionListener(e -> {
            confirmado[0] = false;
            dialog.dispose();
        });

        panelBotones.add(btnSi);
        panelBotones.add(btnNo);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        dialog.setContentPane(panelPrincipal);
        dialog.setVisible(true);

        return confirmado[0];
    }

    private void mostrarMensajePersonalizado(String mensaje, String titulo) {
        JDialog dialog = new JDialog(this, titulo, true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 15));
        panelPrincipal.setBackground(COLOR_PANEL);
        panelPrincipal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        JTextPane txtInfo = new JTextPane();
        txtInfo.setText(mensaje);
        txtInfo.setEditable(false);
        txtInfo.setBackground(COLOR_CAMPO);
        txtInfo.setForeground(COLOR_TEXTO);
        txtInfo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtInfo.setFocusable(false);
        txtInfo.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        scrollInfo.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        scrollInfo.getViewport().setBackground(COLOR_CAMPO);
        panelPrincipal.add(scrollInfo, BorderLayout.CENTER);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelBoton.setOpaque(false);

        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setBackground(COLOR_ROJO);
        btnAceptar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnAceptar.setFocusPainted(false);
        btnAceptar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAceptar.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        agregarHover(btnAceptar);
        btnAceptar.addActionListener(e -> dialog.dispose());

        panelBoton.add(btnAceptar);
        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);

        dialog.setContentPane(panelPrincipal);
        dialog.setVisible(true);
    }

    // =========================================================
    // AYUDA
    // =========================================================
    private void mostrarAyuda() {
        String texto = "<html><div style='width:330px;'>"
                + "<h2>Ayuda RentCar</h2>"
                + "1. Selecciona el lugar donde recogerás el vehículo.<br><br>"
                + "2. Selecciona la fecha de recogida y devolución.<br><br>"
                + "3. Ajusta el precio mínimo y máximo.<br><br>"
                + "4. Utiliza los filtros para encontrar un vehículo.<br><br>"
                + "5. Pulsa <b>Buscar vehículos</b>."
                + "</div></html>";
        JOptionPane.showMessageDialog(this, texto, "Ayuda", JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================
    // FECHAS, TEXTO Y PRECIO
    // =========================================================
    private String formatearFecha(Date fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "CO"));
        return formato.format(fecha);
    }

    private Date sumarDias(Date fecha, int dias) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(fecha);
        calendario.add(Calendar.DAY_OF_MONTH, dias);
        return calendario.getTime();
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        return texto.substring(0, 1).toUpperCase(new Locale("es", "CO")) + texto.substring(1);
    }

    private String formatearPrecio(Object valor) {
        try {
            double numero;
            if (valor instanceof Number) {
                numero = ((Number) valor).doubleValue();
            } else {
                String texto = valor.toString().replace("$", "").replace(".", "").replace(",", ".");
                numero = Double.parseDouble(texto);
            }
            NumberFormat formato = NumberFormat.getNumberInstance(new Locale("es", "CO"));
            formato.setMaximumFractionDigits(0);
            return "$" + formato.format(numero);
        } catch (Exception e) {
            return "$0";
        }
    }

    // =========================================================
    // GETTERS
    // =========================================================
    public JComboBox<MarcaItem> getComboMarcas() {
        return comboMarcas;
    }

    public JButton getBtnVerTodos() {
        return btnVerTodos;
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
