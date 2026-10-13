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
    private final List<ReservaSesion> reservasSesion = new ArrayList<>();
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
                    mostrarMisReservas();
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
        private final Date seleccionInicial;

        private final SimpleDateFormat formatoMes
                = new SimpleDateFormat("MMMM yyyy", new Locale("es", "CO"));

        CalendarioDialog(Frame parent, Date fecha, boolean esRecogida) {
            super(parent, "Seleccionar fecha", true);
            setUndecorated(true);
            seleccionInicial = fecha;
            calendario = Calendar.getInstance();
            calendario.setTime(fecha);
            setSize(420, 470);
            setResizable(false);
            setLocationRelativeTo(parent);
            crearCalendario(esRecogida);
        }

        private void crearCalendario(boolean esRecogida) {
            JPanel principal = new JPanel(new BorderLayout(0, 10));
            principal.setBackground(COLOR_PANEL);
            principal.setBorder(new EmptyBorder(20, 24, 20, 24));

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

            panelDias = new JPanel(new GridLayout(0, 7, 4, 4));
            panelDias.setOpaque(false);
            principal.add(panelDias, BorderLayout.CENTER);

            JButton cancelar = crearBotonSecundario("Cancelar");
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

            JPanel raiz = new JPanel(new BorderLayout());
            raiz.setBackground(COLOR_PANEL);
            raiz.setBorder(BorderFactory.createLineBorder(new Color(48, 51, 58)));
            raiz.add(crearBarraDialogo(this, "Seleccionar fecha"), BorderLayout.NORTH);
            raiz.add(principal, BorderLayout.CENTER);
            setContentPane(raiz);
            getRootPane().registerKeyboardAction(
                    e -> dispose(),
                    KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW);
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
                boton.setFont(new Font("SansSerif", Font.PLAIN, 13));
                boton.setForeground(COLOR_TEXTO);
                boton.setBackground(COLOR_CAMPO);
                boton.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
                boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                Calendar actual = Calendar.getInstance();
                actual.setTime(seleccionInicial);
                if (actual.get(Calendar.YEAR) == calendario.get(Calendar.YEAR)
                        && actual.get(Calendar.MONTH) == calendario.get(Calendar.MONTH)
                        && actual.get(Calendar.DAY_OF_MONTH) == dia) {
                    boton.setBackground(COLOR_ROJO);
                    boton.setForeground(Color.WHITE);
                    boton.setFont(new Font("SansSerif", Font.BOLD, 13));
                }

                boton.addActionListener(e -> {
                    Calendar seleccion = (Calendar) calendario.clone();
                    seleccion.set(Calendar.DAY_OF_MONTH, diaSeleccionado);
                    Date fecha = seleccion.getTime();

                    if (esRecogida && fechaDevolucion != null && fecha.after(fechaDevolucion)) {
                        fechaDevolucion = sumarDias(fecha, 1);
                        btnFechaDevolucion.setText(formatearFecha(fechaDevolucion));
                    }

                    if (!esRecogida && fechaRecogida != null && fecha.before(fechaRecogida)) {
                        mostrarMensajePersonalizado(CalendarioDialog.this,
                                "La devolución no puede ser anterior a la recogida.",
                                "Fecha no válida");
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
                // Si llegan varios cambios seguidos (una fila por vehículo) se reconstruye una sola vez
                if (!reconstruccionPendiente) {
                    reconstruccionPendiente = true;
                    SwingUtilities.invokeLater(() -> {
                        reconstruccionPendiente = false;
                        actualizarTarjetas();
                    });
                }
            }
        });
    }

    private boolean reconstruccionPendiente = false;

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
    // Fotos ya leídas y reducidas: evita decodificar la misma foto grande en cada filtro o búsqueda
    private final java.util.Map<String, BufferedImage> cacheImagenes = new java.util.HashMap<>();

    private BufferedImage cargarImagenOriginal(String ruta) {
        if (ruta == null || ruta.trim().isEmpty()) {
            return null;
        }

        try {
            File archivo = FotoVehiculoUtil.resolver(ruta);
            String clave = archivo != null && archivo.isFile()
                    ? archivo.getAbsolutePath() + "|" + archivo.lastModified() + "|" + archivo.length()
                    : "recurso:" + ruta;

            if (cacheImagenes.containsKey(clave)) {
                return cacheImagenes.get(clave);
            }

            BufferedImage original = null;
            if (archivo != null && archivo.isFile()) {
                original = leerImagenReducida(archivo, 1000);
            }

            // También permite imágenes guardadas dentro de resources.
            if (original == null) {
                java.net.URL recurso = getClass().getResource(ruta.startsWith("/") ? ruta : "/" + ruta);
                if (recurso != null) {
                    original = ImageIO.read(recurso);
                }
            }

            cacheImagenes.put(clave, original);
            return original;

        } catch (Exception e) {
            return null;
        }
    }

    /** Lee la foto saltando píxeles y la deja con máximo "lado" píxeles (rápido incluso con fotos de 12 MP). */
    private BufferedImage leerImagenReducida(File archivo, int lado) {
        try (javax.imageio.stream.ImageInputStream in = ImageIO.createImageInputStream(archivo)) {
            java.util.Iterator<javax.imageio.ImageReader> lectores = ImageIO.getImageReaders(in);
            if (!lectores.hasNext()) {
                return null;
            }
            javax.imageio.ImageReader lector = lectores.next();
            try {
                lector.setInput(in, true, true);
                int w = lector.getWidth(0);
                int h = lector.getHeight(0);

                javax.imageio.ImageReadParam parametros = lector.getDefaultReadParam();
                int paso = Math.max(1, Math.max(w, h) / lado);
                parametros.setSourceSubsampling(paso, paso, 0, 0);
                BufferedImage leida = lector.read(0, parametros);
                if (leida == null) {
                    return null;
                }

                int mayor = Math.max(leida.getWidth(), leida.getHeight());
                if (mayor <= lado) {
                    return leida;
                }

                double escala = (double) lado / mayor;
                int nw = Math.max(1, (int) Math.round(leida.getWidth() * escala));
                int nh = Math.max(1, (int) Math.round(leida.getHeight() * escala));
                BufferedImage reducida = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = reducida.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.drawImage(leida, 0, 0, nw, nh, null);
                g2.dispose();
                return reducida;
            } finally {
                lector.dispose();
            }
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
            mostrarMensajePersonalizado(
                    "La fecha de devolución debe ser posterior a la fecha de recogida.",
                    "Fechas no válidas");
            return;
        }

        actualizarTarjetas();

        String lugar = comboLugar.getSelectedItem().toString();
        String marca = comboMarcas.getSelectedItem() == null
                ? "Todas" : comboMarcas.getSelectedItem().toString();

        int minimo = sliderPrecio.getMinimumValue();
        int maximo = sliderPrecio.getMaximumValue();

        mostrarResumenBusqueda(lugar, marca, minimo, maximo);
        actualizarTarjetas();
    }

// =========================================================
    // DETALLES
    // =========================================================
    private void mostrarDetalles(String marca, String modelo, String placa, Object precio, int filaModelo) {

        JDialog dialog = new JDialog(this, "Detalles del vehículo", true);
        dialog.setUndecorated(true);
        dialog.setResizable(false);

        dialog.setContentPane(construirPanelDetalles(dialog, marca, modelo, placa, precio, filaModelo));
        dialog.setSize(940, 620);
        dialog.setLocationRelativeTo(this);

        // ESC cierra la ventana
        dialog.getRootPane().registerKeyboardAction(
                e -> dialog.dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialog.setVisible(true);
    }

    /**
     * Contenido completo de la ventana de detalles: barra superior con el
     * mismo diseño de las demás ventanas, foto del vehículo y su ficha.
     */
    private JPanel construirPanelDetalles(JDialog dialog, String marca, String modelo,
            String placa, Object precio, int filaModelo) {

        boolean moto = valorFila(filaModelo, 5, "AUTO").equalsIgnoreCase("MOTO");
        boolean disponible = Boolean.parseBoolean(valorFila(filaModelo, 15, "true"));

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(COLOR_PANEL);
        raiz.setBorder(BorderFactory.createLineBorder(new Color(48, 51, 58)));

        raiz.add(crearBarraDialogo(dialog, "Detalles del vehículo"), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setBackground(COLOR_PANEL);

        // =====================================================
        // IZQUIERDA: FOTO + PLACA + PRECIO
        // =====================================================
        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.setBackground(COLOR_CARD);
        izquierda.setPreferredSize(new Dimension(420, 0));
        izquierda.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_BORDE));

        JPanel panelFoto = new JPanel();
        panelFoto.setLayout(new OverlayLayout(panelFoto));
        panelFoto.setBackground(new Color(31, 33, 38));

        // Estado encima de la foto (se agrega primero para quedar arriba)
        JLabel estado = new JLabel(disponible ? "● Disponible" : "● No disponible");
        estado.setOpaque(true);
        estado.setBackground(disponible ? COLOR_VERDE : new Color(85, 38, 38));
        estado.setForeground(new Color(185, 235, 205));
        estado.setFont(new Font("SansSerif", Font.BOLD, 12));
        estado.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        JPanel capaEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 14));
        capaEstado.setOpaque(false);
        capaEstado.add(estado);

        JPanel capaSuperior = new JPanel(new BorderLayout());
        capaSuperior.setOpaque(false);
        capaSuperior.setAlignmentX(Component.CENTER_ALIGNMENT);
        capaSuperior.setAlignmentY(Component.CENTER_ALIGNMENT);
        capaSuperior.add(capaEstado, BorderLayout.NORTH);
        panelFoto.add(capaSuperior);

        ImagenCoverLabel imagen = new ImagenCoverLabel(cargarImagenOriginal(valorFila(filaModelo, 0, "")));
        imagen.setAlignmentX(Component.CENTER_ALIGNMENT);
        imagen.setAlignmentY(Component.CENTER_ALIGNMENT);
        panelFoto.add(imagen);

        izquierda.add(panelFoto, BorderLayout.CENTER);

        JPanel pie = new JPanel();
        pie.setOpaque(false);
        pie.setLayout(new BoxLayout(pie, BoxLayout.Y_AXIS));
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE));
        pie.setPreferredSize(new Dimension(0, 150));

        JPanel placaPanel = crearPlacaColombiana(placa);
        placaPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel precioLabel = new JLabel(formatearPrecio(precio) + " / día");
        precioLabel.setForeground(COLOR_TEXTO);
        precioLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        precioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel moneda = new JLabel("COP · precio por día");
        moneda.setForeground(COLOR_SECUNDARIO);
        moneda.setFont(new Font("SansSerif", Font.PLAIN, 12));
        moneda.setAlignmentX(Component.CENTER_ALIGNMENT);

        pie.add(Box.createVerticalGlue());
        pie.add(placaPanel);
        pie.add(Box.createVerticalStrut(12));
        pie.add(precioLabel);
        pie.add(Box.createVerticalStrut(2));
        pie.add(moneda);
        pie.add(Box.createVerticalGlue());

        izquierda.add(pie, BorderLayout.SOUTH);
        cuerpo.add(izquierda, BorderLayout.WEST);

        // =====================================================
        // DERECHA: FICHA TÉCNICA
        // =====================================================
        JPanel derecha = new JPanel(new BorderLayout(0, 14));
        derecha.setOpaque(false);
        derecha.setBorder(BorderFactory.createEmptyBorder(24, 28, 22, 28));

        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));

        JLabel lblTipo = new JLabel(moto ? "MOTOCICLETA" : "AUTOMÓVIL");
        lblTipo.setForeground(COLOR_ROJO_HOVER);
        lblTipo.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTipo.setAlignmentX(Component.LEFT_ALIGNMENT);
        superior.add(lblTipo);
        superior.add(Box.createVerticalStrut(6));

        JLabel lblMarca = new JLabel(marca.toUpperCase(Locale.ROOT));
        lblMarca.setForeground(COLOR_SECUNDARIO);
        lblMarca.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblMarca.setAlignmentX(Component.LEFT_ALIGNMENT);
        superior.add(lblMarca);

        JLabel lblModelo = new JLabel(modelo);
        lblModelo.setForeground(COLOR_TEXTO);
        lblModelo.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblModelo.setAlignmentX(Component.LEFT_ALIGNMENT);
        superior.add(lblModelo);
        superior.add(Box.createVerticalStrut(4));

        JLabel lblCiudad = new JLabel(valorFila(filaModelo, 14, "Ubicación no especificada"));
        lblCiudad.setForeground(COLOR_SECUNDARIO);
        lblCiudad.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblCiudad.setAlignmentX(Component.LEFT_ALIGNMENT);
        superior.add(lblCiudad);
        superior.add(Box.createVerticalStrut(16));

        String km = valorFila(filaModelo, 11, "");
        try {
            km = NumberFormat.getNumberInstance(new Locale("es", "CO")).format(Long.parseLong(km.trim())) + " km";
        } catch (Exception e) {
            km = km.isEmpty() ? "No especificado" : km + " km";
        }

        JPanel ficha = new JPanel(new GridLayout(0, 2, 10, 10));
        ficha.setOpaque(false);
        ficha.setAlignmentX(Component.LEFT_ALIGNMENT);
        ficha.add(crearDatoFicha("AÑO", valorFila(filaModelo, 6, "No especificado")));
        ficha.add(crearDatoFicha("COLOR", valorFila(filaModelo, 7, "No especificado")));
        ficha.add(crearDatoFicha("TRANSMISIÓN", valorFila(filaModelo, 8, "No especificada")));
        ficha.add(crearDatoFicha("COMBUSTIBLE", valorFila(filaModelo, 9, "No especificado")));
        ficha.add(crearDatoFicha("CAPACIDAD", valorFila(filaModelo, 10, moto ? "2" : "5") + " personas"));
        ficha.add(crearDatoFicha(moto ? "TIPO" : "PUERTAS", moto ? "Motocicleta" : valorFila(filaModelo, 16, "4")));
        ficha.add(crearDatoFicha("KILOMETRAJE", km));
        ficha.add(crearDatoFicha("CATEGORÍA", valorFila(filaModelo, 12, "No especificada")));
        superior.add(ficha);

        derecha.add(superior, BorderLayout.NORTH);

        // Descripción
        JPanel bloqueDescripcion = new JPanel(new BorderLayout(0, 6));
        bloqueDescripcion.setOpaque(false);

        JLabel lblDescripcion = new JLabel("DESCRIPCIÓN");
        lblDescripcion.setForeground(COLOR_SECUNDARIO);
        lblDescripcion.setFont(new Font("SansSerif", Font.BOLD, 11));
        bloqueDescripcion.add(lblDescripcion, BorderLayout.NORTH);

        JTextArea txtDescripcion = new JTextArea(valorFila(filaModelo, 13, "Sin descripción"));
        txtDescripcion.setEditable(false);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setFocusable(false);
        txtDescripcion.setBackground(COLOR_CAMPO);
        txtDescripcion.setForeground(COLOR_TEXTO);
        txtDescripcion.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtDescripcion.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        scrollDescripcion.getViewport().setBackground(COLOR_CAMPO);
        estilizarBarraDesplazamiento(scrollDescripcion);
        bloqueDescripcion.add(scrollDescripcion, BorderLayout.CENTER);

        derecha.add(bloqueDescripcion, BorderLayout.CENTER);

        // Botón
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBackground(COLOR_ROJO);
        btnCerrar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.setBorder(BorderFactory.createEmptyBorder(11, 36, 11, 36));
        agregarHover(btnCerrar);
        btnCerrar.addActionListener(e -> {
            if (dialog != null) dialog.dispose();
        });

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBoton.setOpaque(false);
        panelBoton.add(btnCerrar);
        derecha.add(panelBoton, BorderLayout.SOUTH);

        cuerpo.add(derecha, BorderLayout.CENTER);
        raiz.add(cuerpo, BorderLayout.CENTER);

        if (dialog != null) {
            dialog.getRootPane().setDefaultButton(btnCerrar);
        }

        return raiz;
    }

    /** Casilla de la ficha técnica: título pequeño arriba y valor debajo. */
    private JPanel crearDatoFicha(String titulo, String valor) {

        JPanel celda = new JPanel();
        celda.setLayout(new BoxLayout(celda, BoxLayout.Y_AXIS));
        celda.setBackground(COLOR_CAMPO);
        celda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(8, 12, 9, 12)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(COLOR_SECUNDARIO);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(valor);
        lblValor.setForeground(COLOR_TEXTO);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        celda.add(lblTitulo);
        celda.add(Box.createVerticalStrut(3));
        celda.add(lblValor);
        return celda;
    }

    /** Barra superior idéntica a la de las demás ventanas (RENTCAR · título + botón cerrar). */
    private JPanel crearBarraDialogo(JDialog dialog, String tituloVentana) {

        Color fondo = new Color(12, 14, 18);

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(fondo);
        barra.setPreferredSize(new Dimension(100, 38));
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ROJO));

        JLabel titulo = new JLabel("  RENTCAR  ·  " + tituloVentana);
        titulo.setForeground(new Color(242, 243, 245));
        titulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        titulo.setBorder(new EmptyBorder(0, 7, 0, 0));
        barra.add(titulo, BorderLayout.CENTER);

        JButton cerrar = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_ROJO);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(242, 243, 245));
                g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                g2.drawLine(cx - 5, cy - 5, cx + 5, cy + 5);
                g2.drawLine(cx + 5, cy - 5, cx - 5, cy + 5);
                g2.dispose();
            }
        };
        cerrar.setToolTipText("Cerrar");
        cerrar.setOpaque(false);
        cerrar.setContentAreaFilled(false);
        cerrar.setBorderPainted(false);
        cerrar.setFocusPainted(false);
        cerrar.setFocusable(false);
        cerrar.setPreferredSize(new Dimension(46, 36));
        cerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cerrar.addActionListener(e -> {
            if (dialog != null) dialog.dispose();
        });
        barra.add(cerrar, BorderLayout.EAST);

        // Arrastrar la ventana desde la barra
        MouseAdapter arrastre = new MouseAdapter() {
            private Point origenPantalla;
            private Point origenVentana;

            @Override
            public void mousePressed(MouseEvent e) {
                if (dialog == null) return;
                origenPantalla = e.getLocationOnScreen();
                origenVentana = dialog.getLocation();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dialog == null || origenPantalla == null || origenVentana == null) return;
                Point actual = e.getLocationOnScreen();
                dialog.setLocation(origenVentana.x + actual.x - origenPantalla.x,
                        origenVentana.y + actual.y - origenPantalla.y);
            }
        };
        barra.addMouseListener(arrastre);
        barra.addMouseMotionListener(arrastre);
        titulo.addMouseListener(arrastre);
        titulo.addMouseMotionListener(arrastre);

        return barra;
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

    /** Reserva solicitada durante la sesión (guarda la foto para mostrarla en "Mis reservas"). */
    private static class ReservaSesion {

        final String foto, tipo, marca, modelo, placa, lugar, recogida, devolucion, total;
        final long dias;

        ReservaSesion(String foto, String tipo, String marca, String modelo, String placa,
                String lugar, String recogida, String devolucion, long dias, String total) {
            this.foto = foto;
            this.tipo = tipo;
            this.marca = marca;
            this.modelo = modelo;
            this.placa = placa;
            this.lugar = lugar;
            this.recogida = recogida;
            this.devolucion = devolucion;
            this.dias = dias;
            this.total = total;
        }

        boolean esMoto() {
            return "MOTO".equalsIgnoreCase(tipo);
        }

        String duracion() {
            return dias + (dias == 1 ? " día" : " días");
        }
    }

    private void reservarVehiculo(String marca, String modelo, String placa, Object precio, int fila) {
        if (!Boolean.parseBoolean(valorFila(fila, 15, "true"))) {
            mostrarMensajePersonalizado("Este vehículo no está disponible para alquiler.", "No disponible");
            return;
        }

        long dias = Math.max(1, (fechaDevolucion.getTime() - fechaRecogida.getTime()) / (24L * 60 * 60 * 1000));
        double precioDia = precio instanceof Number ? ((Number) precio).doubleValue() : 0;

        ReservaSesion reserva = new ReservaSesion(
                valorFila(fila, 0, ""),
                valorFila(fila, 5, "AUTO"),
                marca, modelo, placa,
                String.valueOf(comboLugar.getSelectedItem()),
                formatearFecha(fechaRecogida),
                formatearFecha(fechaDevolucion),
                dias,
                formatearPrecio(precioDia * dias));

        if (mostrarConfirmacionReserva(reserva)) {
            reservasSesion.add(reserva);
            mostrarMensajePersonalizado("Solicitud agregada a Mis reservas durante esta sesión.", "Reserva exitosa");
        }
    }

    // ---------- piezas reutilizables de las ventanas ----------

    private JButton crearBotonPrimario(String texto) {
        JButton b = new JButton(texto);
        b.setForeground(Color.WHITE);
        b.setBackground(COLOR_ROJO);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(11, 30, 11, 30));
        agregarHover(b);
        return b;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton b = new JButton(texto);
        b.setForeground(COLOR_TEXTO);
        b.setBackground(COLOR_CAMPO);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(10, 28, 10, 28)));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(COLOR_BORDE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(COLOR_CAMPO);
            }
        });
        return b;
    }

    /** Ventana modal sin marco nativo: barra RENTCAR + cuerpo. */
    private JDialog crearDialogoRentCar(Window owner, String titulo, int ancho, int alto, JPanel cuerpo) {
        JDialog dialog = new JDialog(owner, titulo, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setResizable(false);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(COLOR_PANEL);
        raiz.setBorder(BorderFactory.createLineBorder(new Color(48, 51, 58)));
        raiz.add(crearBarraDialogo(dialog, titulo), BorderLayout.NORTH);
        raiz.add(cuerpo, BorderLayout.CENTER);

        dialog.setContentPane(raiz);
        dialog.setSize(ancho, alto);
        dialog.setLocationRelativeTo(owner);
        dialog.getRootPane().registerKeyboardAction(
                e -> dialog.dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        return dialog;
    }

    private JPanel crearEncabezadoDialogo(String pequeno, String titulo, String subtitulo) {
        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));

        JLabel l1 = new JLabel(pequeno);
        l1.setForeground(COLOR_ROJO_HOVER);
        l1.setFont(new Font("SansSerif", Font.BOLD, 12));
        l1.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.add(l1);
        encabezado.add(Box.createVerticalStrut(6));

        JLabel l2 = new JLabel(titulo);
        l2.setForeground(COLOR_TEXTO);
        l2.setFont(new Font("SansSerif", Font.BOLD, 26));
        l2.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.add(l2);

        if (subtitulo != null) {
            encabezado.add(Box.createVerticalStrut(4));
            JLabel l3 = new JLabel(subtitulo);
            l3.setForeground(COLOR_SECUNDARIO);
            l3.setFont(new Font("SansSerif", Font.PLAIN, 14));
            l3.setAlignmentX(Component.LEFT_ALIGNMENT);
            encabezado.add(l3);
        }
        return encabezado;
    }

    // ---------- confirmación de reserva ----------

    private boolean mostrarConfirmacionReserva(ReservaSesion r) {

        final boolean[] confirmado = {false};

        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setOpaque(false);

        ImagenCoverLabel foto = new ImagenCoverLabel(cargarImagenOriginal(r.foto));
        foto.setPreferredSize(new Dimension(0, 200));
        cuerpo.add(foto, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 14));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(20, 28, 22, 28));

        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel tipo = new JLabel((r.esMoto() ? "MOTOCICLETA" : "AUTOMÓVIL") + "  ·  " + r.marca.toUpperCase(Locale.ROOT));
        tipo.setForeground(COLOR_ROJO_HOVER);
        tipo.setFont(new Font("SansSerif", Font.BOLD, 12));
        tipo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel modelo = new JLabel(r.modelo);
        modelo.setForeground(COLOR_TEXTO);
        modelo.setFont(new Font("SansSerif", Font.BOLD, 22));
        modelo.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(tipo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(modelo);
        fila.add(textos, BorderLayout.CENTER);
        fila.add(crearPlacaColombiana(r.placa), BorderLayout.EAST);
        centro.add(fila, BorderLayout.NORTH);

        JPanel ficha = new JPanel(new GridLayout(0, 2, 10, 10));
        ficha.setOpaque(false);
        ficha.add(crearDatoFicha("LUGAR DE RECOGIDA", r.lugar));
        ficha.add(crearDatoFicha("FECHAS", r.recogida + " → " + r.devolucion));
        ficha.add(crearDatoFicha("DURACIÓN", r.duracion()));
        ficha.add(crearDatoFicha("TOTAL ESTIMADO", r.total));
        JPanel envolturaFicha = new JPanel(new BorderLayout());
        envolturaFicha.setOpaque(false);
        envolturaFicha.add(ficha, BorderLayout.NORTH);
        centro.add(envolturaFicha, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(0, 14));
        sur.setOpaque(false);
        JLabel pregunta = new JLabel("¿Agregar a Mis reservas de esta sesión?");
        pregunta.setForeground(COLOR_SECUNDARIO);
        pregunta.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sur.add(pregunta, BorderLayout.NORTH);

        JButton btnSi = crearBotonPrimario("Sí, agregar");
        JButton btnNo = crearBotonSecundario("Cancelar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botones.setOpaque(false);
        botones.add(btnNo);
        botones.add(btnSi);
        sur.add(botones, BorderLayout.SOUTH);
        centro.add(sur, BorderLayout.SOUTH);

        cuerpo.add(centro, BorderLayout.CENTER);

        JDialog dialog = crearDialogoRentCar(this, "Solicitar reserva", 620, 585, cuerpo);
        btnSi.addActionListener(e -> {
            confirmado[0] = true;
            dialog.dispose();
        });
        btnNo.addActionListener(e -> dialog.dispose());
        dialog.getRootPane().setDefaultButton(btnSi);
        dialog.setVisible(true);

        return confirmado[0];
    }

    // ---------- mensajes cortos ----------

    private void mostrarMensajePersonalizado(String mensaje, String titulo) {
        mostrarMensajePersonalizado(this, mensaje, titulo);
    }

    private void mostrarMensajePersonalizado(Window owner, String mensaje, String titulo) {

        JPanel cuerpo = new JPanel(new BorderLayout(0, 16));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(26, 32, 24, 32));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        cuerpo.add(lblTitulo, BorderLayout.NORTH);

        JTextArea txt = new JTextArea(mensaje);
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setFocusable(false);
        txt.setOpaque(false);
        txt.setForeground(COLOR_SECUNDARIO);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 15));
        cuerpo.add(txt, BorderLayout.CENTER);

        JButton btn = crearBotonPrimario("Aceptar");
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBoton.setOpaque(false);
        panelBoton.add(btn);
        cuerpo.add(panelBoton, BorderLayout.SOUTH);

        JDialog dialog = crearDialogoRentCar(owner, titulo, 480, 240, cuerpo);
        btn.addActionListener(e -> dialog.dispose());
        dialog.getRootPane().setDefaultButton(btn);
        dialog.setVisible(true);
    }

    // ---------- resumen de búsqueda ----------

    private void mostrarResumenBusqueda(String lugar, String marca, int minimo, int maximo) {

        JPanel cuerpo = new JPanel(new BorderLayout(0, 18));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(26, 32, 24, 32));

        cuerpo.add(crearEncabezadoDialogo("RESULTADOS", "Búsqueda realizada",
                "Estos son los criterios que aplicaste."), BorderLayout.NORTH);

        JPanel ficha = new JPanel(new GridLayout(0, 2, 10, 10));
        ficha.setOpaque(false);
        ficha.add(crearDatoFicha("LUGAR", lugar));
        ficha.add(crearDatoFicha("MARCA", marca));
        ficha.add(crearDatoFicha("RECOGIDA", formatearFecha(fechaRecogida)));
        ficha.add(crearDatoFicha("DEVOLUCIÓN", formatearFecha(fechaDevolucion)));
        ficha.add(crearDatoFicha("PRECIO MÍNIMO", formatearPrecio(minimo)));
        ficha.add(crearDatoFicha("PRECIO MÁXIMO", formatearPrecio(maximo)));
        JPanel envoltura = new JPanel(new BorderLayout());
        envoltura.setOpaque(false);
        envoltura.add(ficha, BorderLayout.NORTH);
        cuerpo.add(envoltura, BorderLayout.CENTER);

        JButton btn = crearBotonPrimario("Aceptar");
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBoton.setOpaque(false);
        panelBoton.add(btn);
        cuerpo.add(panelBoton, BorderLayout.SOUTH);

        JDialog dialog = crearDialogoRentCar(this, "Búsqueda", 580, 420, cuerpo);
        btn.addActionListener(e -> dialog.dispose());
        dialog.getRootPane().setDefaultButton(btn);
        dialog.setVisible(true);
    }

    // ---------- Mis reservas ----------

    private void mostrarMisReservas() {

        JPanel cuerpo = new JPanel(new BorderLayout(0, 16));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(26, 32, 24, 32));

        int n = reservasSesion.size();
        cuerpo.add(crearEncabezadoDialogo("TUS SOLICITUDES", "Mis reservas",
                n == 0 ? "Aquí aparecerán los vehículos que reserves."
                        : n + (n == 1 ? " solicitud" : " solicitudes") + " en esta sesión."),
                BorderLayout.NORTH);

        JButton btnCerrar = crearBotonPrimario(n == 0 ? "Ver vehículos" : "Cerrar");

        if (n == 0) {
            JPanel vacio = new JPanel();
            vacio.setBackground(COLOR_CAMPO);
            vacio.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
            vacio.setLayout(new BoxLayout(vacio, BoxLayout.Y_AXIS));

            JLabel l1 = new JLabel("Todavía no has solicitado reservas");
            l1.setForeground(COLOR_TEXTO);
            l1.setFont(new Font("SansSerif", Font.BOLD, 18));
            l1.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel l2 = new JLabel("Pulsa «Reservar» en cualquier vehículo del catálogo.");
            l2.setForeground(COLOR_SECUNDARIO);
            l2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            l2.setAlignmentX(Component.CENTER_ALIGNMENT);

            vacio.add(Box.createVerticalGlue());
            vacio.add(l1);
            vacio.add(Box.createVerticalStrut(8));
            vacio.add(l2);
            vacio.add(Box.createVerticalGlue());
            cuerpo.add(vacio, BorderLayout.CENTER);

        } else {
            JPanel lista = new JPanel();
            lista.setOpaque(false);
            lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));

            for (ReservaSesion r : reservasSesion) {
                JPanel tarjeta = crearTarjetaReserva(r);
                tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
                lista.add(tarjeta);
                lista.add(Box.createVerticalStrut(12));
            }

            JPanel envoltura = new JPanel(new BorderLayout());
            envoltura.setOpaque(false);
            envoltura.add(lista, BorderLayout.NORTH);

            JScrollPane scroll = new JScrollPane(envoltura,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.getVerticalScrollBar().setUnitIncrement(24);
            estilizarBarraDesplazamiento(scroll);
            cuerpo.add(scroll, BorderLayout.CENTER);
        }

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBoton.setOpaque(false);
        panelBoton.add(btnCerrar);
        cuerpo.add(panelBoton, BorderLayout.SOUTH);

        JDialog dialog = crearDialogoRentCar(this, "Mis reservas", 800, 640, cuerpo);
        btnCerrar.addActionListener(e -> dialog.dispose());
        dialog.getRootPane().setDefaultButton(btnCerrar);
        dialog.setVisible(true);
    }

    /** Tarjeta de una reserva: foto del vehículo a la izquierda y los datos a la derecha. */
    private JPanel crearTarjetaReserva(ReservaSesion r) {

        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(COLOR_CARD);
        tarjeta.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        tarjeta.setPreferredSize(new Dimension(0, 160));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        ImagenCoverLabel foto = new ImagenCoverLabel(cargarImagenOriginal(r.foto));
        foto.setPreferredSize(new Dimension(230, 0));
        tarjeta.add(foto, BorderLayout.WEST);

        JPanel info = new JPanel(new BorderLayout(0, 6));
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel tipo = new JLabel((r.esMoto() ? "MOTOCICLETA" : "AUTOMÓVIL") + "  ·  " + r.marca.toUpperCase(Locale.ROOT));
        tipo.setForeground(COLOR_ROJO_HOVER);
        tipo.setFont(new Font("SansSerif", Font.BOLD, 11));
        tipo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel modelo = new JLabel(r.modelo);
        modelo.setForeground(COLOR_TEXTO);
        modelo.setFont(new Font("SansSerif", Font.BOLD, 19));
        modelo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lugar = new JLabel(r.lugar);
        lugar.setForeground(COLOR_SECUNDARIO);
        lugar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lugar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel fechas = new JLabel(r.recogida + " → " + r.devolucion + "  ·  " + r.duracion());
        fechas.setForeground(COLOR_SECUNDARIO);
        fechas.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fechas.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(tipo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(modelo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lugar);
        textos.add(Box.createVerticalStrut(2));
        textos.add(fechas);
        info.add(textos, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.add(crearPlacaColombiana(r.placa), BorderLayout.WEST);

        JPanel total = new JPanel();
        total.setOpaque(false);
        total.setLayout(new BoxLayout(total, BoxLayout.Y_AXIS));
        JLabel lblTotal = new JLabel("TOTAL ESTIMADO");
        lblTotal.setForeground(COLOR_SECUNDARIO);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTotal.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel valor = new JLabel(r.total);
        valor.setForeground(COLOR_TEXTO);
        valor.setFont(new Font("SansSerif", Font.BOLD, 22));
        valor.setAlignmentX(Component.RIGHT_ALIGNMENT);
        total.add(lblTotal);
        total.add(valor);
        pie.add(total, BorderLayout.EAST);

        info.add(pie, BorderLayout.SOUTH);
        tarjeta.add(info, BorderLayout.CENTER);

        return tarjeta;
    }

    // =========================================================
    // AYUDA
    // =========================================================
    private void mostrarAyuda() {

        JDialog dialog = new JDialog(this, "Ayuda", true);
        dialog.setUndecorated(true);
        dialog.setResizable(false);

        dialog.setContentPane(construirPanelAyuda(dialog));
        dialog.setSize(640, 600);
        dialog.setLocationRelativeTo(this);

        // ESC cierra la ventana
        dialog.getRootPane().registerKeyboardAction(
                e -> dialog.dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialog.setVisible(true);
    }

    private JPanel construirPanelAyuda(JDialog dialog) {

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(COLOR_PANEL);
        raiz.setBorder(BorderFactory.createLineBorder(new Color(48, 51, 58)));

        raiz.add(crearBarraDialogo(dialog, "Ayuda"), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 18));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(26, 32, 24, 32));

        // ---------------- ENCABEZADO ----------------
        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));

        JLabel pequeno = new JLabel("GUÍA RÁPIDA");
        pequeno.setForeground(COLOR_ROJO_HOVER);
        pequeno.setFont(new Font("SansSerif", Font.BOLD, 12));
        pequeno.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.add(pequeno);
        encabezado.add(Box.createVerticalStrut(6));

        JLabel titulo = new JLabel("Ayuda RentCar");
        titulo.setForeground(COLOR_TEXTO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(4));

        JLabel subtitulo = new JLabel("Reserva tu auto o moto en cinco pasos.");
        subtitulo.setForeground(COLOR_SECUNDARIO);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.add(subtitulo);

        cuerpo.add(encabezado, BorderLayout.NORTH);

        // ---------------- PASOS ----------------
        JPanel pasos = new JPanel(new GridLayout(0, 1, 0, 10));
        pasos.setOpaque(false);

        pasos.add(crearPasoAyuda(1, "Elige el lugar", "Selecciona dónde recogerás el vehículo."));
        pasos.add(crearPasoAyuda(2, "Define las fechas", "Selecciona la fecha de recogida y de devolución."));
        pasos.add(crearPasoAyuda(3, "Ajusta el precio", "Mueve el control para fijar el precio mínimo y máximo por día."));
        pasos.add(crearPasoAyuda(4, "Usa los filtros", "Filtra por tipo, categoría, marca o disponibilidad para encontrar tu vehículo."));
        pasos.add(crearPasoAyuda(5, "Busca y reserva", "Pulsa «Buscar vehículos» y luego «Reservar» en el que más te guste."));

        cuerpo.add(pasos, BorderLayout.CENTER);

        // ---------------- BOTÓN ----------------
        JButton btnCerrar = new JButton("Entendido");
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBackground(COLOR_ROJO);
        btnCerrar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.setBorder(BorderFactory.createEmptyBorder(11, 36, 11, 36));
        agregarHover(btnCerrar);
        btnCerrar.addActionListener(e -> {
            if (dialog != null) dialog.dispose();
        });

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBoton.setOpaque(false);
        panelBoton.add(btnCerrar);
        cuerpo.add(panelBoton, BorderLayout.SOUTH);

        raiz.add(cuerpo, BorderLayout.CENTER);

        if (dialog != null) {
            dialog.getRootPane().setDefaultButton(btnCerrar);
        }

        return raiz;
    }

    /** Fila de la guía: círculo rojo con el número y, al lado, título y descripción. */
    private JPanel crearPasoAyuda(int numero, String titulo, String descripcion) {

        JPanel fila = new JPanel(new BorderLayout(16, 0));
        fila.setBackground(COLOR_CAMPO);
        fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));

        JLabel insignia = new JLabel(String.valueOf(numero), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_ROJO);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        insignia.setOpaque(false);
        insignia.setForeground(Color.WHITE);
        insignia.setFont(new Font("SansSerif", Font.BOLD, 15));
        insignia.setPreferredSize(new Dimension(36, 36));

        JPanel envolturaInsignia = new JPanel(new GridBagLayout());
        envolturaInsignia.setOpaque(false);
        envolturaInsignia.add(insignia);
        fila.add(envolturaInsignia, BorderLayout.WEST);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 15));

        JLabel lblDescripcion = new JLabel(descripcion);
        lblDescripcion.setForeground(COLOR_SECUNDARIO);
        lblDescripcion.setFont(new Font("SansSerif", Font.PLAIN, 13));

        textos.add(lblTitulo);
        textos.add(lblDescripcion);
        fila.add(textos, BorderLayout.CENTER);

        return fila;
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