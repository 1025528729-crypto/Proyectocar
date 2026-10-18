package com.mycompany.rentcar.view;

import com.mycompany.rentcar.presenter.MarcaItem;
import com.mycompany.rentcar.model.Alquiler;
import com.mycompany.rentcar.model.AlquilerDAO;
import com.mycompany.rentcar.util.Async;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class MainPublicView extends JPanel {

    // =========================================================
    // COMPONENTES DEL PRESENTER
    // =========================================================
    private JComboBox<MarcaItem> comboMarcas;
    private JButton btnVerTodos;

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
    // Los favoritos viven mientras el programa esté abierto (también al cambiar de tema, que reconstruye la ventana)
    private static final Set<String> favoritos = new HashSet<>();

    // Reservas: placa → primer día en que el vehículo vuelve a estar libre
    private final Map<String, java.time.LocalDate> reservadas = new HashMap<>();
    private final Set<String> misReservadas = new HashSet<>();   // reservados por el usuario actual
    private final Set<String> avisosPedidos = new HashSet<>();   // "avísame cuando esté disponible"
    private javax.swing.Timer pulsoDisponibilidad;
    private boolean consultandoDisponibilidad = false;
    private boolean procesandoAviso = false;
    private boolean mostrarSoloFavoritos = false;

    // =========================================================
    // COLORES
    // =========================================================
    private static Color COLOR_FONDO = TemaRentCar.c(242, 244, 248);
    private static Color COLOR_HEADER = TemaRentCar.c(255, 255, 255);
    private static Color COLOR_PANEL = TemaRentCar.c(238, 241, 246);
    private static Color COLOR_CARD = TemaRentCar.c(238, 241, 246);
    private static Color COLOR_CAMPO = TemaRentCar.c(230, 234, 241);
    private static Color COLOR_BORDE = TemaRentCar.c(211, 217, 226);
    private static Color COLOR_TEXTO = TemaRentCar.c(23, 27, 34);
    private static Color COLOR_SECUNDARIO = TemaRentCar.c(98, 105, 118);
    private static Color COLOR_ROJO = TemaRentCar.c(224, 43, 49);
    private static Color COLOR_ROJO_HOVER = TemaRentCar.c(244, 61, 68);
    private static Color COLOR_VERDE = TemaRentCar.c(35, 67, 54);

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public MainPublicView() {
        super(new BorderLayout());
        aplicarTemaMenus();
        crearModelo();
        crearInterfaz();
        escucharModelo();
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
        principal.add(crearContenido(), BorderLayout.CENTER);
        add(principal, BorderLayout.CENTER);
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
            BufferedImage original = TemaRentCar.logoOscuro();

            if (original != null) {
                // Se quita el margen transparente y se conserva la proporción real
                original = recortarTransparente(original);
                double escala = Math.min(200.0 / original.getWidth(), 90.0 / original.getHeight());
                int ancho = Math.max(1, (int) Math.round(original.getWidth() * escala));
                int alto = Math.max(1, (int) Math.round(original.getHeight() * escala));
                // Icono nítido: se redibuja a la resolución real de la pantalla
                logo.setIcon(new LogoIcon(original, ancho, alto));
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
        logo.setForeground(COLOR_TEXTO);
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
    }

    private JButton crearBotonSecundario(String texto, java.awt.event.ActionListener accion) {
        JButton b = new JButton(texto);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(COLOR_TEXTO);
        b.setBackground(TemaRentCar.PANEL);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(accion);
        return b;
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

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);
        acciones.add(crearBotonSecundario("Mis reservas", e -> mostrarMisReservas()));
        acciones.add(crearBotonSecundario("Ayuda", e -> mostrarAyuda()));
        panel.add(acciones, BorderLayout.EAST);
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
        TemaRentCar.estilizarCombo(comboLugar);
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
        label.setForeground(TemaRentCar.c(23, 27, 34));
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
        Color pista = TemaRentCar.c(255, 255, 255);
        Color pulgar = TemaRentCar.c(183, 183, 187);
        Color acento = TemaRentCar.c(224, 43, 49);
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
        Color fondo = TemaRentCar.c(238, 241, 246);
        Color campo = TemaRentCar.c(230, 234, 241);
        Color texto = TemaRentCar.c(23, 27, 34);
        Color acento = TemaRentCar.c(224, 43, 49);
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
        UIManager.put("ScrollBar.thumb", TemaRentCar.c(183, 183, 187));
        UIManager.put("ScrollBar.track", TemaRentCar.c(255, 255, 255));
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
                return COLOR_TEXTO;
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
                    : (hover ? TemaRentCar.c(211, 217, 226) : COLOR_CAMPO);
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

    private class CalendarioDialog extends DialogoInterno {

        private Calendar calendario;
        private JLabel lblMes;
        private JPanel panelDias;
        private final Date seleccionInicial;

        private final SimpleDateFormat formatoMes
                = new SimpleDateFormat("MMMM yyyy", new Locale("es", "CO"));

        CalendarioDialog(Component parent, Date fecha, boolean esRecogida) {
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
            raiz.setBorder(BorderFactory.createLineBorder(TemaRentCar.c(211, 217, 226)));
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

    // true mientras los vehículos se leen de la base de datos
    private boolean cargandoVehiculos = false;

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
            JLabel vacio = new JLabel(cargandoVehiculos ? "Cargando vehículos…" : "No hay vehículos para mostrar.", SwingConstants.CENTER);
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
        panelImagen.setBackground(TemaRentCar.c(230, 234, 241));
        panelImagen.setPreferredSize(new Dimension(300, 205));

        BufferedImage imagenOriginal = cargarImagenOriginal(foto);
        ImagenCoverLabel imagen = new ImagenCoverLabel(imagenOriginal);
        imagen.setAlignmentX(Component.CENTER_ALIGNMENT);
        imagen.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Capa superior: estado y favorito encima de la foto.
        // En un OverlayLayout el PRIMER componente queda arriba, por eso esta capa se agrega antes que la foto.
        JPanel controlesImagen = new JPanel(new BorderLayout());
        controlesImagen.setOpaque(false);
        controlesImagen.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlesImagen.setAlignmentY(Component.CENTER_ALIGNMENT);

        boolean disponible = Boolean.parseBoolean(valorFila(filaModelo, 15, "true"));

        JLabel estado = new JLabel();
        estado.setOpaque(true);
        estado.setFont(new Font("SansSerif", Font.BOLD, 10));
        estado.setBorder(BorderFactory.createEmptyBorder(5, 9, 5, 9));
        aplicarEstado(estado, disponible, placa);

        JPanel estadoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        estadoPanel.setOpaque(false);
        estadoPanel.add(estado);

        BotonFavorito favorito = new BotonFavorito(favoritos.contains(placa));
        favorito.addActionListener(e -> {
            if (favoritos.contains(placa)) {
                favoritos.remove(placa);
            } else {
                favoritos.add(placa);
            }
            favorito.setActivo(favoritos.contains(placa));
            if (chkFavoritos != null && chkFavoritos.isSelected()) {
                actualizarTarjetas();
            }
        });

        JPanel favoritoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        favoritoPanel.setOpaque(false);
        favoritoPanel.add(favorito);

        JPanel filaSuperior = new JPanel(new BorderLayout());
        filaSuperior.setOpaque(false);
        filaSuperior.add(estadoPanel, BorderLayout.WEST);
        filaSuperior.add(favoritoPanel, BorderLayout.EAST);
        controlesImagen.add(filaSuperior, BorderLayout.NORTH);

        panelImagen.add(controlesImagen);
        panelImagen.add(imagen);
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
        datosAdicionales.setForeground(TemaRentCar.c(23, 27, 34));
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

        JButton principal = reservar;
        if (disponible && reservadas.containsKey(placa)) {
            if (misReservadas.contains(placa)) {
                principal = crearBotonEstadoReserva("MIS RESERVAS", false);
                principal.addActionListener(e -> mostrarMisReservas());
            } else {
                boolean pedido = avisosPedidos.contains(placa);
                principal = crearBotonEstadoReserva(pedido ? "✓ AVISO ACTIVO" : "AVISARME", pedido);
                principal.setToolTipText(pedido
                        ? "Te notificaremos cuando se desocupe. Toca para cancelar el aviso."
                        : "Recibe una notificación cuando este vehículo vuelva a estar disponible");
                principal.addActionListener(e -> alternarAviso(placa, marca, modelo));
            }
        }

        acciones.add(detalles);
        acciones.add(Box.createVerticalStrut(7));
        acciones.add(principal);
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
                g2.setColor(TemaRentCar.c(247, 207, 43));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(TemaRentCar.c(238, 241, 246));
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
        pais.setForeground(TemaRentCar.c(45, 45, 45));
        pais.setFont(new Font("SansSerif", Font.BOLD, 7));

        JLabel numero = new JLabel(placa == null ? "---" : placa.toUpperCase(Locale.ROOT));
        numero.setHorizontalAlignment(SwingConstants.CENTER);
        numero.setForeground(TemaRentCar.c(20, 20, 20));
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
        // Los emblemas son blancos: se muestran sobre una ficha oscura para que se vean con luz de día
        JLabel logo = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (getIcon() != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TemaRentCar.c(24, 26, 32));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
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

        BufferedImage emblema = obtenerEmblemaMarca(archivo);
        if (emblema != null) {
            int w = emblema.getWidth(), h = emblema.getHeight();
            double escala = Math.min(108.0 / Math.max(1, w), 60.0 / Math.max(1, h));
            int nw = Math.max(1, (int) Math.round(w * escala));
            int nh = Math.max(1, (int) Math.round(h * escala));
            logo.setIcon(new LogoIcon(emblema, nw, nh));
            logo.setText("");
            return logo;
        }

        logo.setText(clave.isBlank() ? "?" : clave.substring(0, 1).toUpperCase(Locale.ROOT));
        logo.setFont(new Font("SansSerif", Font.BOLD, 30));
        logo.setForeground(COLOR_ROJO);
        return logo;
    }

    // =========================================================
    // LOGOS NÍTIDOS (logo del proyecto y emblemas de marcas)
    // =========================================================

    // Emblemas ya procesados (fondo transparente + recortados): se calculan una sola vez por marca
    private static final java.util.Map<String, BufferedImage> CACHE_EMBLEMAS = new java.util.HashMap<>();

    private BufferedImage obtenerEmblemaMarca(String archivo) {
        if (CACHE_EMBLEMAS.containsKey(archivo)) {
            return CACHE_EMBLEMAS.get(archivo);
        }

        BufferedImage resultado = null;
        try {
            java.net.URL recurso = getClass().getResource("/images/marcas/" + archivo);
            if (recurso != null) {
                BufferedImage original = ImageIO.read(recurso);
                if (original != null) {
                    int w = original.getWidth(), h = original.getHeight();
                    // Los logotipos JPG tienen fondo oscuro; se vuelve transparente.
                    BufferedImage sinFondo = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    for (int y = 0; y < h; y++) {
                        for (int x = 0; x < w; x++) {
                            int rgb = original.getRGB(x, y);
                            int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
                            int brillo = (r + g + b) / 3;
                            int alpha = brillo < 38 ? 0 : (brillo < 65 ? (brillo - 38) * 9 : 255);
                            sinFondo.setRGB(x, y, (alpha << 24) | (rgb & 0x00ffffff));
                        }
                    }
                    resultado = recortarTransparente(sinFondo);
                }
            }
        } catch (Exception ignored) {
        }

        CACHE_EMBLEMAS.put(archivo, resultado);
        return resultado;
    }

    /** Recorta el margen transparente de una imagen. */
    private static BufferedImage recortarTransparente(BufferedImage origen) {
        int w = origen.getWidth(), h = origen.getHeight();
        int minX = w, minY = h, maxX = -1, maxY = -1;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (((origen.getRGB(x, y) >>> 24) & 0xff) > 40) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }
        if (maxX <= minX || maxY <= minY) {
            return origen;
        }
        int cw = maxX - minX + 1, ch = maxY - minY + 1;
        BufferedImage recorte = new BufferedImage(cw, ch, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = recorte.createGraphics();
        g2.drawImage(origen, -minX, -minY, null);
        g2.dispose();
        return recorte;
    }

    /** Reduce con calidad: va a la mitad paso a paso y termina con interpolación bicúbica. */
    private static BufferedImage escalarSuave(BufferedImage origen, int anchoFinal, int altoFinal) {
        BufferedImage actual = origen;
        int w = origen.getWidth(), h = origen.getHeight();
        while (w / 2 >= anchoFinal && h / 2 >= altoFinal) {
            w /= 2;
            h /= 2;
            actual = redimensionar(actual, w, h);
        }
        return redimensionar(actual, anchoFinal, altoFinal);
    }

    private static BufferedImage redimensionar(BufferedImage origen, int ancho, int alto) {
        BufferedImage destino = new BufferedImage(Math.max(1, ancho), Math.max(1, alto), BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g2 = destino.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(origen, 0, 0, destino.getWidth(), destino.getHeight(), null);
        g2.dispose();
        return destino;
    }

    /**
     * Icono que se dibuja a la resolución real de la pantalla (también con
     * escala de Windows 125 %, 150 %...), por eso no se ve pixelado.
     */
    private static class LogoIcon implements Icon {

        private final BufferedImage origen;
        private final int ancho, alto;
        private BufferedImage cache;
        private int cacheW = -1, cacheH = -1;

        LogoIcon(BufferedImage origen, int ancho, int alto) {
            this.origen = origen;
            this.ancho = ancho;
            this.alto = alto;
        }

        @Override
        public int getIconWidth() {
            return ancho;
        }

        @Override
        public int getIconHeight() {
            return alto;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            java.awt.geom.AffineTransform t = g2.getTransform();
            double sx = t.getScaleX() > 0 ? t.getScaleX() : 1;
            double sy = t.getScaleY() > 0 ? t.getScaleY() : 1;

            int pw = Math.max(1, (int) Math.round(ancho * sx));
            int ph = Math.max(1, (int) Math.round(alto * sy));

            if (cache == null || cacheW != pw || cacheH != ph) {
                cache = escalarSuave(origen, pw, ph);
                cacheW = pw;
                cacheH = ph;
            }

            g2.scale(1.0 / sx, 1.0 / sy);
            g2.drawImage(cache, (int) Math.round(x * sx), (int) Math.round(y * sy), null);
            g2.dispose();
        }
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
                original = leerImagenReducida(archivo, 2000);
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

        // Foto ya escalada al tamaño actual: no se vuelve a escalar en cada repintado
        private BufferedImage escalada;
        private int escaladaAncho = -1;
        private int escaladaAlto = -1;

        public ImagenCoverLabel(BufferedImage imagen) {
            this.imagen = imagen;
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            setBackground(TemaRentCar.c(230, 234, 241));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (imagen == null) {
                g.setColor(TemaRentCar.c(98, 105, 118));
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

            // Se escala a la resolución REAL de la pantalla (125 %, 150 %...) y por pasos: nítida, no pixelada
            Graphics2D g2d = (Graphics2D) g;
            java.awt.geom.AffineTransform at = g2d.getTransform();
            double sx = at.getScaleX() > 0 ? at.getScaleX() : 1;
            double sy = at.getScaleY() > 0 ? at.getScaleY() : 1;
            int pw = (int) Math.round(ancho * sx);
            int ph = (int) Math.round(alto * sy);

            if (escalada == null || escaladaAncho != pw || escaladaAlto != ph) {
                double esc = Math.max((double) pw / imagen.getWidth(), (double) ph / imagen.getHeight());
                int nw = (int) Math.ceil(imagen.getWidth() * esc);
                int nh = (int) Math.ceil(imagen.getHeight() * esc);
                BufferedImage base = TemaRentCar.escalar(imagen, nw, nh);
                BufferedImage nueva = new BufferedImage(pw, ph, BufferedImage.TYPE_INT_RGB);
                Graphics2D gi = nueva.createGraphics();
                gi.setColor(getBackground());
                gi.fillRect(0, 0, pw, ph);
                gi.drawImage(base, (pw - nw) / 2, (ph - nh) / 2, null);
                gi.dispose();
                escalada = nueva;
                escaladaAncho = pw;
                escaladaAlto = ph;
            }

            Graphics2D g3 = (Graphics2D) g2d.create();
            g3.scale(1.0 / sx, 1.0 / sy);
            g3.drawImage(escalada, 0, 0, null);
            g3.dispose();
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
        label.setForeground(TemaRentCar.c(23, 27, 34));
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

        DialogoInterno dialog = new DialogoInterno(this, "Detalles del vehículo", true);
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
    private JPanel construirPanelDetalles(DialogoInterno dialog, String marca, String modelo,
            String placa, Object precio, int filaModelo) {

        boolean moto = valorFila(filaModelo, 5, "AUTO").equalsIgnoreCase("MOTO");
        boolean disponible = Boolean.parseBoolean(valorFila(filaModelo, 15, "true"));

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(COLOR_PANEL);
        raiz.setBorder(BorderFactory.createLineBorder(TemaRentCar.c(211, 217, 226)));

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
        panelFoto.setBackground(TemaRentCar.c(230, 234, 241));

        // Estado encima de la foto (se agrega primero para quedar arriba)
        JLabel estado = new JLabel();
        estado.setOpaque(true);
        estado.setFont(new Font("SansSerif", Font.BOLD, 12));
        estado.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        aplicarEstado(estado, disponible, placa);

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
    private JPanel crearBarraDialogo(DialogoInterno dialog, String tituloVentana) {

        Color fondo = TemaRentCar.c(242, 244, 248);

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(fondo);
        barra.setPreferredSize(new Dimension(100, 38));
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ROJO));

        JLabel titulo = new JLabel("  RENTCAR  ·  " + tituloVentana);
        titulo.setForeground(TemaRentCar.c(23, 27, 34));
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
                g2.setColor(TemaRentCar.c(23, 27, 34));
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

        if (chkDisponibles != null && chkDisponibles.isSelected()
                && (!Boolean.parseBoolean(valorFila(fila, 15, "true")) || reservadas.containsKey(placa))) {
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
    // DISPONIBILIDAD, AVISOS Y FAVORITOS
    // =========================================================

    private static final java.time.format.DateTimeFormatter FORMATO_CORTO =
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Colorea y escribe la etiqueta de estado: Disponible, Reservado o No disponible. */
    private void aplicarEstado(JLabel estado, boolean disponible, String placa) {
        java.time.LocalDate libre = reservadas.get(placa);
        estado.setForeground(Color.WHITE);
        if (!disponible) {
            estado.setText("● No disponible");
            estado.setBackground(TemaRentCar.c(120, 45, 45));
        } else if (libre != null) {
            boolean mia = misReservadas.contains(placa);
            estado.setText(mia ? "● Reservado por ti" : "● Reservado · libre el " + libre.format(FORMATO_CORTO));
            estado.setBackground(TemaRentCar.c(146, 88, 12));
            estado.setToolTipText("Disponible a partir del " + libre.format(FORMATO_CORTO));
        } else {
            estado.setText("● Disponible");
            estado.setBackground(TemaRentCar.c(27, 120, 78));
        }
    }

    private String textoReservado(String placa) {
        java.time.LocalDate libre = reservadas.get(placa);
        if (misReservadas.contains(placa)) {
            return "Ya tienes una reserva de este vehículo. Puedes verla o cancelarla en «Mis reservas».";
        }
        return "Este vehículo ya está reservado por otra persona"
                + (libre == null ? "" : " y volverá a estar disponible el " + libre.format(FORMATO_CORTO))
                + ". Pulsa «Avisarme» y te notificaremos cuando se desocupe.";
    }

    /** Botón gris (o verde si el aviso ya está activo) que reemplaza a RESERVAR cuando el vehículo está ocupado. */
    private JButton crearBotonEstadoReserva(String texto, boolean activo) {
        JButton b = new JButton(texto);
        b.setFont(new Font("SansSerif", Font.BOLD, 11));
        b.setForeground(activo ? Color.WHITE : COLOR_TEXTO);
        b.setBackground(activo ? TemaRentCar.c(27, 120, 78) : COLOR_CAMPO);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 6, 10, 6));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.RIGHT_ALIGNMENT);
        b.setPreferredSize(new Dimension(116, 42));
        b.setMaximumSize(new Dimension(116, 42));
        return b;
    }

    /** Pide (o cancela) el aviso "avísame cuando esté disponible". */
    private void alternarAviso(String placa, String marca, String modelo) {
        AppFrame app = AppFrame.getInstancia();
        com.mycompany.rentcar.model.Usuario usuario = app == null ? null : app.getUsuario();
        if (usuario == null) {
            pedirInicioSesion("Inicia sesión para que te avisemos cuando este vehículo vuelva a estar disponible.");
            return;
        }
        if (procesandoAviso) {
            return;
        }
        procesandoAviso = true;
        final int usuarioId = usuario.getId();
        final boolean quitar = avisosPedidos.contains(placa);
        final String nombre = ((marca == null ? "" : marca) + " " + (modelo == null ? "" : modelo)).trim();
        Async.run(
                () -> quitar ? new AlquilerDAO().quitarAviso(usuarioId, placa) : new AlquilerDAO().pedirAviso(usuarioId, placa),
                error -> {
                    procesandoAviso = false;
                    if (error != null) {
                        mostrarMensajePersonalizado(error, "Error");
                        return;
                    }
                    if (quitar) {
                        avisosPedidos.remove(placa);
                        NotificacionesRentCar.info("Aviso cancelado", "Ya no te avisaremos del " + nombre + ".");
                    } else {
                        avisosPedidos.add(placa);
                        NotificacionesRentCar.exito("Aviso activado",
                                "Te notificaremos aquí cuando el " + nombre + " vuelva a estar disponible.");
                    }
                    actualizarTarjetas();
                    refrescarDisponibilidad();
                },
                ex -> {
                    procesandoAviso = false;
                    mostrarMensajePersonalizado("No se pudo conectar con la base de datos. Intenta de nuevo.", "Sin conexión");
                });
    }

    /**
     * Vuelve a leer qué vehículos están reservados y entrega los avisos que ya
     * se cumplieron. Se llama al abrir el catálogo, al iniciar/cerrar sesión,
     * al reservar o cancelar y cada medio minuto.
     */
    public void refrescarDisponibilidad() {
        if (consultandoDisponibilidad) {
            return;
        }
        consultandoDisponibilidad = true;
        AppFrame app = AppFrame.getInstancia();
        com.mycompany.rentcar.model.Usuario usuario = app == null ? null : app.getUsuario();
        final int usuarioId = usuario == null ? 0 : usuario.getId();

        Async.run(
                () -> new AlquilerDAO().leerEstado(usuarioId),
                estado -> {
                    consultandoDisponibilidad = false;
                    if (estado == null) {
                        return;   // sin conexión: se conserva lo que ya se mostraba
                    }
                    AppFrame actual = AppFrame.getInstancia();
                    com.mycompany.rentcar.model.Usuario u = actual == null ? null : actual.getUsuario();
                    boolean mismoUsuario = (u == null ? 0 : u.getId()) == usuarioId;

                    if (mismoUsuario) {
                        for (String[] l : estado.listos) {
                            String nombre = (l[1] + " " + l[2]).trim();
                            NotificacionesRentCar.exito(
                                    "¡Ya está disponible!",
                                    "El " + nombre + " que estabas esperando ya se puede reservar.\n"
                                    + "Toca aquí para ir al catálogo.",
                                    () -> {
                                        AppFrame a = AppFrame.getInstancia();
                                        if (a != null) a.mostrar(AppFrame.CATALOGO);
                                    });
                        }
                    }

                    boolean cambio = !estado.libreDesde.equals(reservadas)
                            || !estado.mias.equals(misReservadas)
                            || (mismoUsuario && !estado.avisos.equals(avisosPedidos))
                            || !estado.listos.isEmpty();
                    reservadas.clear();
                    reservadas.putAll(estado.libreDesde);
                    misReservadas.clear();
                    misReservadas.addAll(estado.mias);
                    avisosPedidos.clear();
                    if (mismoUsuario) {
                        avisosPedidos.addAll(estado.avisos);
                    }
                    if (cambio) {
                        actualizarTarjetas();
                    }
                },
                ex -> consultandoDisponibilidad = false);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        refrescarDisponibilidad();
        if (pulsoDisponibilidad == null) {
            pulsoDisponibilidad = new javax.swing.Timer(30_000, e -> refrescarDisponibilidad());
        }
        pulsoDisponibilidad.start();
    }

    @Override
    public void removeNotify() {
        if (pulsoDisponibilidad != null) {
            pulsoDisponibilidad.stop();
        }
        super.removeNotify();
    }

    /** Corazón sobre la foto: círculo claro con un corazón dibujado (relleno si es favorito). */
    private static final class BotonFavorito extends JComponent {
        private boolean activo;
        private boolean encima;

        BotonFavorito(boolean activo) {
            this.activo = activo;
            setPreferredSize(new Dimension(40, 40));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText(activo ? "Quitar de favoritos" : "Agregar a favoritos");
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { encima = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) {
                    for (java.awt.event.ActionListener l : alAccionar) {
                        l.actionPerformed(new ActionEvent(BotonFavorito.this, ActionEvent.ACTION_PERFORMED, "favorito"));
                    }
                }
            });
        }

        private final List<java.awt.event.ActionListener> alAccionar = new ArrayList<>();

        void addActionListener(java.awt.event.ActionListener l) { alAccionar.add(l); }

        void setActivo(boolean a) {
            activo = a;
            setToolTipText(a ? "Quitar de favoritos" : "Agregar a favoritos");
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            int w = getWidth(), h = getHeight(), d = Math.min(w, h) - 2;

            // Sombra suave para que se vea sobre cualquier foto
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillOval((w - d) / 2, (h - d) / 2 + 1, d, d);
            g2.setColor(COLOR_HEADER);
            g2.fillOval((w - d) / 2, (h - d) / 2, d - 1, d - 1);
            g2.setColor(encima ? COLOR_ROJO : COLOR_BORDE);
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawOval((w - d) / 2, (h - d) / 2, d - 1, d - 1);

            double cx = w / 2.0, cy = h / 2.0 + 0.5, s = 8.2;
            java.awt.geom.Path2D.Double corazon = new java.awt.geom.Path2D.Double();
            corazon.moveTo(cx, cy + s * 1.05);
            corazon.curveTo(cx - s * 1.9, cy - s * 0.25, cx - s * 0.95, cy - s * 1.45, cx, cy - s * 0.5);
            corazon.curveTo(cx + s * 0.95, cy - s * 1.45, cx + s * 1.9, cy - s * 0.25, cx, cy + s * 1.05);
            corazon.closePath();
            if (activo) {
                g2.setColor(COLOR_ROJO);
                g2.fill(corazon);
            } else {
                g2.setColor(encima ? COLOR_ROJO : COLOR_SECUNDARIO);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(corazon);
            }
            g2.dispose();
        }
    }

    // =========================================================
    // RESERVAR
    // =========================================================

    // Evita abrir dos formularios (o dos consultas) si el usuario pulsa varias veces
    private boolean procesandoReserva = false;
    private boolean cargandoReservas = false;

    private void reservarVehiculo(String marca, String modelo, String placa, Object precio, int fila) {
        if (!Boolean.parseBoolean(valorFila(fila, 15, "true"))) {
            mostrarMensajePersonalizado("Este vehículo no está disponible para alquiler.", "No disponible");
            return;
        }
        if (reservadas.containsKey(placa)) {
            mostrarMensajePersonalizado(textoReservado(placa), "Vehículo reservado");
            return;
        }

        // 1) Hay que haber iniciado sesión
        AppFrame app = AppFrame.getInstancia();
        com.mycompany.rentcar.model.Usuario usuario = app == null ? null : app.getUsuario();
        if (usuario == null) {
            pedirInicioSesion("Para reservar un vehículo primero debes iniciar sesión o crear una cuenta.");
            return;
        }
        if (procesandoReserva) {
            return;
        }

        long dias = Math.max(1, (fechaDevolucion.getTime() - fechaRecogida.getTime()) / (24L * 60 * 60 * 1000));
        double precioDia = precio instanceof Number ? ((Number) precio).doubleValue() : 0;

        Alquiler base = new Alquiler();
        base.usuarioId = usuario.getId();
        base.placa = placa;
        base.marca = marca;
        base.modelo = modelo;
        base.tipo = valorFila(fila, 5, "AUTO");
        base.foto = valorFila(fila, 0, "");
        base.lugar = String.valueOf(comboLugar.getSelectedItem());
        base.recogida = new java.sql.Date(fechaRecogida.getTime()).toLocalDate();
        base.devolucion = base.recogida.plusDays(dias);
        base.dias = dias;
        base.total = precioDia * dias;

        // 2) Se revisa en la base de datos que no esté reservado (por ti o por otra persona) en esas fechas
        procesandoReserva = true;
        Async.run(
                () -> new AlquilerDAO().verificarDisponibilidad(base.usuarioId, base.placa, base.recogida, base.devolucion),
                mensaje -> {
                    procesandoReserva = false;
                    if (mensaje != null) {
                        mostrarMensajePersonalizado(mensaje, "No se puede reservar");
                    } else {
                        mostrarFormularioAlquiler(base);   // 3) formulario de datos y pago
                    }
                },
                ex -> {
                    procesandoReserva = false;
                    mostrarMensajePersonalizado("No se pudo conectar con la base de datos. Intenta de nuevo.", "Sin conexión");
                });
    }

    private void mostrarFormularioAlquiler(Alquiler base) {

        final DialogoInterno[] dialog = new DialogoInterno[1];
        final FormularioAlquilerPanel[] formulario = new FormularioAlquilerPanel[1];

        formulario[0] = new FormularioAlquilerPanel(base, new FormularioAlquilerPanel.Escucha() {
            @Override
            public void confirmar(Alquiler datos) {
                Async.run(
                        () -> new AlquilerDAO().crear(datos),
                        error -> {
                            if (error == null) {
                                dialog[0].dispose();
                                refrescarDisponibilidad();   // el vehículo pasa a "Reservado" para los demás
                                // Aviso dentro del programa (ya no se abre una ventana)
                                NotificacionesRentCar.exito(
                                        "Reserva confirmada",
                                        (datos.marca + " " + datos.modelo).trim() + "\n"
                                        + "Del " + formatearFecha(java.sql.Date.valueOf(datos.recogida)) + " al "
                                        + formatearFecha(java.sql.Date.valueOf(datos.devolucion)) + "\n"
                                        + "Total: " + formatearPrecio(datos.total) + "  ·  " + datos.descripcionPago() + "\n"
                                        + "Toca aquí para ver «Mis reservas».",
                                        MainPublicView.this::mostrarMisReservas);
                            } else {
                                formulario[0].mostrarFallo(error);
                            }
                        },
                        ex -> formulario[0].mostrarFallo("No se pudo conectar con la base de datos. Intenta de nuevo."));
            }

            @Override
            public void cancelar() {
                dialog[0].dispose();
            }
        });
        estilizarBarraDesplazamiento(formulario[0].getScroll());

        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setOpaque(false);
        cuerpo.add(formulario[0], BorderLayout.CENTER);

        dialog[0] = crearDialogoRentCar(this, "Rentar vehículo", 700, 740, cuerpo);
        dialog[0].setVisible(true);
    }

    /** Aviso con botón para abrir el panel de inicio de sesión. */
    private void pedirInicioSesion(String motivo) {

        final boolean[] ir = {false};

        JPanel cuerpo = new JPanel(new BorderLayout(0, 16));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(26, 32, 24, 32));

        JLabel lblTitulo = new JLabel("Inicia sesión para continuar");
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        cuerpo.add(lblTitulo, BorderLayout.NORTH);

        JTextArea txt = new JTextArea(motivo);
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setFocusable(false);
        txt.setOpaque(false);
        txt.setForeground(COLOR_SECUNDARIO);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 15));
        cuerpo.add(txt, BorderLayout.CENTER);

        JButton btnNo = crearBotonSecundario("Ahora no");
        JButton btnSi = crearBotonPrimario("Iniciar sesión");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botones.setOpaque(false);
        botones.add(btnNo);
        botones.add(btnSi);
        cuerpo.add(botones, BorderLayout.SOUTH);

        DialogoInterno dialog = crearDialogoRentCar(this, "Inicia sesión", 500, 250, cuerpo);
        btnNo.addActionListener(e -> dialog.dispose());
        btnSi.addActionListener(e -> {
            ir[0] = true;
            dialog.dispose();
        });
        dialog.getRootPane().setDefaultButton(btnSi);
        dialog.setVisible(true);

        if (ir[0]) {
            AppFrame app = AppFrame.getInstancia();
            if (app != null) {
                app.abrirAcceso();
            }
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
    private DialogoInterno crearDialogoRentCar(Object owner, String titulo, int ancho, int alto, JPanel cuerpo) {
        DialogoInterno dialog = new DialogoInterno(owner, titulo, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setResizable(false);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(COLOR_PANEL);
        raiz.setBorder(BorderFactory.createLineBorder(TemaRentCar.c(211, 217, 226)));
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

    // ---------- mensajes cortos ----------

    private void mostrarMensajePersonalizado(String mensaje, String titulo) {
        mostrarMensajePersonalizado(this, mensaje, titulo);
    }

    private void mostrarMensajePersonalizado(Object owner, String mensaje, String titulo) {

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

        DialogoInterno dialog = crearDialogoRentCar(owner, titulo, 480, 240, cuerpo);
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

        DialogoInterno dialog = crearDialogoRentCar(this, "Búsqueda", 580, 420, cuerpo);
        btn.addActionListener(e -> dialog.dispose());
        dialog.getRootPane().setDefaultButton(btn);
        dialog.setVisible(true);
    }

    // ---------- Mis reservas ----------

    private void mostrarMisReservas() {

        AppFrame app = AppFrame.getInstancia();
        com.mycompany.rentcar.model.Usuario usuario = app == null ? null : app.getUsuario();
        if (usuario == null) {
            pedirInicioSesion("Inicia sesión para ver las reservas que has hecho.");
            return;
        }
        if (cargandoReservas) {
            return;
        }
        cargandoReservas = true;
        final int usuarioId = usuario.getId();
        Async.run(
                () -> new AlquilerDAO().listarPorUsuario(usuarioId),
                lista -> {
                    cargandoReservas = false;
                    if (lista == null) {
                        mostrarMensajePersonalizado("No se pudieron cargar tus reservas. Intenta de nuevo.", "Sin conexión");
                    } else {
                        mostrarListaReservas(lista, usuarioId);
                    }
                },
                ex -> {
                    cargandoReservas = false;
                    mostrarMensajePersonalizado("No se pudo conectar con la base de datos. Intenta de nuevo.", "Sin conexión");
                });
    }

    private void mostrarListaReservas(List<Alquiler> reservas, int usuarioId) {

        JPanel cuerpo = new JPanel(new BorderLayout(0, 16));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(26, 32, 24, 32));

        int n = 0;
        for (Alquiler a : reservas) {
            if ("ACTIVA".equals(a.estado)) n++;
        }
        cuerpo.add(crearEncabezadoDialogo("TUS RESERVAS", "Mis reservas",
                reservas.isEmpty() ? "Aquí aparecerán los vehículos que reserves."
                        : n + (n == 1 ? " reserva activa" : " reservas activas")),
                BorderLayout.NORTH);

        JButton btnCerrar = crearBotonPrimario(reservas.isEmpty() ? "Ver vehículos" : "Cerrar");
        final DialogoInterno[] dialog = new DialogoInterno[1];

        if (reservas.isEmpty()) {
            JPanel vacio = new JPanel();
            vacio.setBackground(COLOR_CAMPO);
            vacio.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
            vacio.setLayout(new BoxLayout(vacio, BoxLayout.Y_AXIS));

            JLabel l1 = new JLabel("Todavía no has hecho reservas");
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

            for (Alquiler a : reservas) {
                JPanel tarjeta = crearTarjetaAlquiler(a, () -> confirmarCancelacion(a, usuarioId, dialog[0]));
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

        dialog[0] = crearDialogoRentCar(this, "Mis reservas", 800, 640, cuerpo);
        btnCerrar.addActionListener(e -> dialog[0].dispose());
        dialog[0].getRootPane().setDefaultButton(btnCerrar);
        dialog[0].setVisible(true);
    }

    private void confirmarCancelacion(Alquiler a, int usuarioId, DialogoInterno listaAbierta) {
        final boolean[] si = {false};

        JPanel cuerpo = new JPanel(new BorderLayout(0, 16));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(26, 32, 24, 32));

        JLabel lblTitulo = new JLabel("¿Cancelar esta reserva?");
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        cuerpo.add(lblTitulo, BorderLayout.NORTH);

        JTextArea txt = new JTextArea((a.marca + " " + a.modelo).trim() + " · " + a.placa + "\n"
                + formatearFecha(java.sql.Date.valueOf(a.recogida)) + " → "
                + formatearFecha(java.sql.Date.valueOf(a.devolucion)));
        txt.setEditable(false);
        txt.setFocusable(false);
        txt.setOpaque(false);
        txt.setForeground(COLOR_SECUNDARIO);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 15));
        cuerpo.add(txt, BorderLayout.CENTER);

        JButton btnNo = crearBotonSecundario("Conservarla");
        JButton btnSi = crearBotonPrimario("Sí, cancelar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botones.setOpaque(false);
        botones.add(btnNo);
        botones.add(btnSi);
        cuerpo.add(botones, BorderLayout.SOUTH);

        DialogoInterno dialog = crearDialogoRentCar(this, "Cancelar reserva", 480, 260, cuerpo);
        btnNo.addActionListener(e -> dialog.dispose());
        btnSi.addActionListener(e -> {
            si[0] = true;
            dialog.dispose();
        });
        dialog.setVisible(true);

        if (!si[0]) {
            return;
        }
        Async.run(
                () -> new AlquilerDAO().cancelar(a.id, usuarioId),
                ok -> {
                    if (listaAbierta != null) {
                        listaAbierta.dispose();
                    }
                    if (ok) {
                        refrescarDisponibilidad();   // el vehículo queda libre y se avisa a quien lo esperaba
                        NotificacionesRentCar.info(
                                "Reserva cancelada",
                                "Tu reserva del " + (a.marca + " " + a.modelo).trim() + " fue cancelada.");
                        mostrarMisReservas();      // se vuelve a cargar la lista
                    } else {
                        mostrarMensajePersonalizado("No se pudo cancelar la reserva.", "Error");
                    }
                },
                ex -> mostrarMensajePersonalizado("No se pudo conectar con la base de datos.", "Sin conexión"));
    }

    /** Tarjeta de una reserva: foto a la izquierda, datos a la derecha y botón para cancelar. */
    private JPanel crearTarjetaAlquiler(Alquiler r, Runnable alCancelar) {

        boolean activa = "ACTIVA".equals(r.estado);
        Color colorTexto = activa ? COLOR_TEXTO : COLOR_SECUNDARIO;

        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(COLOR_CARD);
        tarjeta.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        tarjeta.setPreferredSize(new Dimension(0, 172));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 172));

        ImagenCoverLabel foto = new ImagenCoverLabel(cargarImagenOriginal(r.foto));
        foto.setPreferredSize(new Dimension(230, 0));
        tarjeta.add(foto, BorderLayout.WEST);

        JPanel info = new JPanel(new BorderLayout(0, 6));
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel tipo = new JLabel((r.esMoto() ? "MOTOCICLETA" : "AUTOMÓVIL") + "  ·  " + r.marca.toUpperCase(Locale.ROOT)
                + (activa ? "" : "  ·  CANCELADA"));
        tipo.setForeground(activa ? COLOR_ROJO_HOVER : COLOR_SECUNDARIO);
        tipo.setFont(new Font("SansSerif", Font.BOLD, 11));
        tipo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel modelo = new JLabel(r.modelo);
        modelo.setForeground(colorTexto);
        modelo.setFont(new Font("SansSerif", Font.BOLD, 19));
        modelo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lugar = new JLabel(r.lugar);
        lugar.setForeground(COLOR_SECUNDARIO);
        lugar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lugar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel fechas = new JLabel(formatearFecha(java.sql.Date.valueOf(r.recogida)) + " → "
                + formatearFecha(java.sql.Date.valueOf(r.devolucion)) + "  ·  " + r.dias + (r.dias == 1 ? " día" : " días"));
        fechas.setForeground(COLOR_SECUNDARIO);
        fechas.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fechas.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pago = new JLabel(r.nombreCompleto() + "  ·  " + r.descripcionPago());
        pago.setForeground(COLOR_SECUNDARIO);
        pago.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pago.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(tipo);
        textos.add(Box.createVerticalStrut(3));
        textos.add(modelo);
        textos.add(Box.createVerticalStrut(3));
        textos.add(lugar);
        textos.add(Box.createVerticalStrut(2));
        textos.add(fechas);
        textos.add(Box.createVerticalStrut(2));
        textos.add(pago);
        info.add(textos, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.add(crearPlacaColombiana(r.placa), BorderLayout.WEST);

        JPanel derecha = new JPanel();
        derecha.setOpaque(false);
        derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));
        JLabel lblTotal = new JLabel("TOTAL");
        lblTotal.setForeground(COLOR_SECUNDARIO);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTotal.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel valor = new JLabel(formatearPrecio(r.total));
        valor.setForeground(colorTexto);
        valor.setFont(new Font("SansSerif", Font.BOLD, 22));
        valor.setAlignmentX(Component.RIGHT_ALIGNMENT);
        derecha.add(lblTotal);
        derecha.add(valor);
        pie.add(derecha, BorderLayout.EAST);

        info.add(pie, BorderLayout.SOUTH);
        tarjeta.add(info, BorderLayout.CENTER);

        if (activa) {
            JButton cancelar = new JButton("Cancelar");
            cancelar.setForeground(COLOR_SECUNDARIO);
            cancelar.setBackground(COLOR_CARD);
            cancelar.setFont(new Font("SansSerif", Font.BOLD, 11));
            cancelar.setFocusPainted(false);
            cancelar.setContentAreaFilled(false);
            cancelar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            cancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cancelar.addActionListener(e -> alCancelar.run());
            JPanel arriba = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            arriba.setOpaque(false);
            arriba.add(cancelar);
            info.add(arriba, BorderLayout.NORTH);
        }
        return tarjeta;
    }

    // =========================================================
    // AYUDA
    // =========================================================
    private void mostrarAyuda() {

        DialogoInterno dialog = new DialogoInterno(this, "Ayuda", true);
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

    private JPanel construirPanelAyuda(DialogoInterno dialog) {

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(COLOR_PANEL);
        raiz.setBorder(BorderFactory.createLineBorder(TemaRentCar.c(211, 217, 226)));

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
    // El presenter avisa cuando empieza y termina la lectura de la base de datos
    public void setCargando(boolean cargando) {

        if (this.cargandoVehiculos == cargando) {
            return;
        }

        this.cargandoVehiculos = cargando;

        setCursor(cargando
                ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                : Cursor.getDefaultCursor());

        // Si no hay tarjetas, muestra/oculta el mensaje "Cargando vehículos…"
        if (tableModel.getRowCount() == 0) {
            actualizarTarjetas();
        }
    }

    public JComboBox<MarcaItem> getComboMarcas() {
        return comboMarcas;
    }

    public JButton getBtnVerTodos() {
        return btnVerTodos;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    /** Vuelve a calcular la paleta cuando cambia el tema claro/oscuro. */
    static void refrescarPaleta() {
        COLOR_FONDO = TemaRentCar.c(242, 244, 248);
        COLOR_HEADER = TemaRentCar.c(255, 255, 255);
        COLOR_PANEL = TemaRentCar.c(238, 241, 246);
        COLOR_CARD = TemaRentCar.c(238, 241, 246);
        COLOR_CAMPO = TemaRentCar.c(230, 234, 241);
        COLOR_BORDE = TemaRentCar.c(211, 217, 226);
        COLOR_TEXTO = TemaRentCar.c(23, 27, 34);
        COLOR_SECUNDARIO = TemaRentCar.c(98, 105, 118);
        COLOR_ROJO = TemaRentCar.c(224, 43, 49);
        COLOR_ROJO_HOVER = TemaRentCar.c(244, 61, 68);
        COLOR_VERDE = TemaRentCar.c(35, 67, 54);
    }
}