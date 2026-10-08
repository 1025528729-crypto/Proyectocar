package com.mycompany.rentcar.view;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Locale;
import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.event.ItemEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

public class VehiculoView extends JFrame {

    // ============================================================
    // CAMPOS
    // ============================================================

    private JTextField txtPlaca;
    private JComboBox<String> comboMarca;
    private JTextField txtModelo;
    private JTextField txtPrecio;

    private JButton btnGuardar;
    private JButton btnEliminar;

    private JTable tablaVehiculos;
    private DefaultTableModel modeloTabla;

    // ============================================================
    // COLORES
    // ============================================================

    private final Color COLOR_FONDO = new Color(11, 13, 17);
    private final Color COLOR_ENCABEZADO = new Color(8, 9, 12);
    private final Color COLOR_PANEL = new Color(20, 23, 28);
    private final Color COLOR_BORDE = new Color(38, 43, 51);
    private final Color COLOR_CAMPO = new Color(27, 31, 38);

    private final Color COLOR_TEXTO = new Color(240, 242, 245);
    private final Color COLOR_GRIS = new Color(156, 163, 175);

    private final Color COLOR_ACENTO = new Color(225, 6, 0);
    private final Color COLOR_ACENTO_HOVER = new Color(255, 45, 38);

    private final Color COLOR_ELIMINAR = new Color(55, 60, 70);
    private final Color COLOR_ELIMINAR_HOVER = new Color(150, 22, 22);

    private final Color COLOR_FILA = new Color(20, 23, 28);
    private final Color COLOR_FILA_ALT = new Color(26, 30, 36);
    private final Color COLOR_SELECCION = new Color(70, 20, 22);

    // PLACA
    private final Color COLOR_PLACA = new Color(255, 205, 0);
    private final Color COLOR_PLACA_TEXTO = new Color(15, 15, 15);

    // PRECIO DIGITAL
    private final Color COLOR_DISPLAY_FONDO = new Color(6, 7, 9);
    private final Color COLOR_DISPLAY_BORDE = new Color(80, 22, 22);
    private final Color COLOR_DISPLAY_ROJO = new Color(255, 35, 35);

    // ============================================================
    // FUENTES
    // ============================================================

    private Font orbitronRegular;
    private Font orbitronBold;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public VehiculoView() {

        orbitronRegular = cargarFuenteBase(
                "/fonts/Orbitron-Regular.ttf",
                Font.PLAIN
        );

        orbitronBold = cargarFuenteBase(
                "/fonts/Orbitron-Bold.ttf",
                Font.BOLD
        );

        initComponents();
    }

    // ============================================================
    // INTERFAZ
    // ============================================================

    private void initComponents() {

        setTitle("RentCar - Gestión de Vehículos");

        // Ventana maximizada para aprovechar toda la pantalla
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setMinimumSize(
                new Dimension(1100, 700)
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLayout(
                new BorderLayout()
        );

        getContentPane().setBackground(
                COLOR_FONDO
        );

        // ========================================================
        // ENCABEZADO
        // ========================================================

        JPanel panelEncabezado =
                new JPanel(new BorderLayout());

        panelEncabezado.setBackground(
                COLOR_ENCABEZADO
        );

        panelEncabezado.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0,
                                0,
                                3,
                                0,
                                COLOR_ACENTO
                        ),
                        BorderFactory.createEmptyBorder(
                                18,
                                25,
                                18,
                                25
                        )
                )
        );

        JLabel lblLogo = new JLabel(
                cargarLogo(
                        "/images/rentcar_logo.png",
                        90
                )
        );

        JLabel lblSubtitulo =
                new JLabel(
                        "Gestión de vehículos y alquileres"
                );

        lblSubtitulo.setForeground(
                COLOR_GRIS
        );

        lblSubtitulo.setFont(
                orbitron(false, 12f)
        );

        JPanel panelTitulos =
                new JPanel(
                        new BorderLayout(15, 0)
                );

        panelTitulos.setOpaque(false);

        panelTitulos.add(
                lblLogo,
                BorderLayout.WEST
        );

        panelTitulos.add(
                lblSubtitulo,
                BorderLayout.CENTER
        );

        panelEncabezado.add(
                panelTitulos,
                BorderLayout.WEST
        );

        JLabel lblModulo =
                new JLabel(
                        "MÓDULO DE VEHÍCULOS"
                );

        lblModulo.setForeground(
                COLOR_ACENTO
        );

        lblModulo.setFont(
                orbitron(true, 12f)
        );

        panelEncabezado.add(
                lblModulo,
                BorderLayout.EAST
        );

        add(
                panelEncabezado,
                BorderLayout.NORTH
        );

        // ========================================================
        // PANEL PRINCIPAL
        // ========================================================

        JPanel panelPrincipal =
                new JPanel(
                        new BorderLayout(20, 20)
                );

        panelPrincipal.setBackground(
                COLOR_FONDO
        );

        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        // ========================================================
        // FORMULARIO
        // ========================================================

        JPanel panelFormulario =
                new JPanel(
                        new GridBagLayout()
                );

        panelFormulario.setBackground(
                COLOR_PANEL
        );

        panelFormulario.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                20,
                                20,
                                20,
                                20
                        )
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        8,
                        5,
                        8,
                        5
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1;

        // ========================================================
        // TÍTULO
        // ========================================================

        JLabel lblFormulario =
                new JLabel(
                        "Registrar vehículo"
                );

        lblFormulario.setFont(
                orbitron(true, 17f)
        );

        lblFormulario.setForeground(
                COLOR_TEXTO
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        gbc.insets =
                new Insets(
                        0,
                        5,
                        20,
                        5
                );

        panelFormulario.add(
                lblFormulario,
                gbc
        );

        // ========================================================
        // DESCRIPCIÓN
        // ========================================================

        JLabel lblDescripcion =
                new JLabel(
                        "<html>Ingresa los datos del vehículo<br>"
                        + "que deseas registrar.</html>"
                );

        lblDescripcion.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        lblDescripcion.setForeground(
                COLOR_GRIS
        );

        gbc.gridy = 1;

        gbc.insets =
                new Insets(
                        0,
                        5,
                        15,
                        5
                );

        panelFormulario.add(
                lblDescripcion,
                gbc
        );

        // ========================================================
        // PLACA
        // ========================================================

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;

        gbc.insets =
                new Insets(
                        7,
                        5,
                        5,
                        5
                );

        panelFormulario.add(
                crearEtiqueta("Placa"),
                gbc
        );

        txtPlaca =
                crearCampoPlaca();

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        panelFormulario.add(
                new PlacaPanel(txtPlaca),
                gbc
        );

        // ========================================================
        // MARCA
        // ========================================================

        gbc.gridy = 4;
        gbc.gridwidth = 1;

        panelFormulario.add(
                crearEtiqueta("Marca"),
                gbc
        );

        comboMarca =
                crearComboMarca();

        logoMarca =
                new LogoMarcaPanel();

        // Cada vez que cambia la marca, se actualiza el logo
        comboMarca.addItemListener(
                e -> {

                    if (
                            e.getStateChange()
                            == ItemEvent.SELECTED
                    ) {

                        logoMarca.setMarca(
                                getMarca()
                        );
                    }
                }
        );

        gbc.gridy = 5;
        gbc.gridwidth = 2;

        panelFormulario.add(
                new MarcaPanel(
                        logoMarca,
                        comboMarca
                ),
                gbc
        );

        // ========================================================
        // MODELO
        // ========================================================

        gbc.gridy = 6;
        gbc.gridwidth = 1;

        panelFormulario.add(
                crearEtiqueta("Modelo"),
                gbc
        );

        txtModelo =
                crearCampoTexto();

        gbc.gridy = 7;
        gbc.gridwidth = 2;

        panelFormulario.add(
                txtModelo,
                gbc
        );

        // ========================================================
        // PRECIO
        // ========================================================

        gbc.gridy = 8;
        gbc.gridwidth = 1;

        panelFormulario.add(
                crearEtiqueta("Precio por día"),
                gbc
        );

        txtPrecio =
                crearCampoPrecio();

        gbc.gridy = 9;
        gbc.gridwidth = 2;

        panelFormulario.add(
                new PrecioPanel(txtPrecio),
                gbc
        );

        // ========================================================
        // BOTÓN GUARDAR
        // ========================================================

        btnGuardar =
                new BotonRacing(
                        "Registrar vehículo",
                        COLOR_ACENTO,
                        COLOR_ACENTO_HOVER
                );

        configurarBoton(
                btnGuardar
        );

        gbc.gridy = 10;

        gbc.insets =
                new Insets(
                        20,
                        5,
                        8,
                        5
                );

        panelFormulario.add(
                btnGuardar,
                gbc
        );

        // ========================================================
        // BOTÓN ELIMINAR
        // ========================================================

        btnEliminar =
                new BotonRacing(
                        "Eliminar seleccionado",
                        COLOR_ELIMINAR,
                        COLOR_ELIMINAR_HOVER
                );

        configurarBoton(
                btnEliminar
        );

        gbc.gridy = 11;

        gbc.insets =
                new Insets(
                        5,
                        5,
                        5,
                        5
                );

        panelFormulario.add(
                btnEliminar,
                gbc
        );

        // ESPACIO

        gbc.gridy = 12;
        gbc.weighty = 1;
        gbc.fill =
                GridBagConstraints.BOTH;

        JPanel espacio =
                new JPanel();

        espacio.setOpaque(false);

        panelFormulario.add(
                espacio,
                gbc
        );

        // ========================================================
        // PANEL DERECHO
        // ========================================================

        JPanel panelDerecho =
                new JPanel(
                        new BorderLayout(
                                0,
                                15
                        )
                );

        panelDerecho.setOpaque(false);

        // ========================================================
        // TARJETAS
        // ========================================================

        JPanel panelTarjetas =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                15,
                                0
                        )
                );

        panelTarjetas.setOpaque(false);

        JPanel tarjetaTotal =
                crearTarjeta(
                        "VEHÍCULOS REGISTRADOS",
                        "Consulta la lista de vehículos disponibles"
                );

        JPanel tarjetaInfo =
                crearTarjeta(
                        "GESTIÓN",
                        "Selecciona un vehículo para eliminarlo"
                );

        panelTarjetas.add(
                tarjetaTotal
        );

        panelTarjetas.add(
                tarjetaInfo
        );

        panelDerecho.add(
                panelTarjetas,
                BorderLayout.NORTH
        );

        // ========================================================
        // TABLA
        // ========================================================

        JPanel panelTabla =
                new JPanel(
                        new BorderLayout()
                );

        panelTabla.setBackground(
                COLOR_PANEL
        );

        panelTabla.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                15,
                                15,
                                15,
                                15
                        )
                )
        );

        JLabel lblLista =
                new JLabel(
                        "Vehículos registrados"
                );

        lblLista.setFont(
                orbitron(true, 15f)
        );

        lblLista.setForeground(
                COLOR_TEXTO
        );

        lblLista.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        12,
                        0
                )
        );

        panelTabla.add(
                lblLista,
                BorderLayout.NORTH
        );

        // ========================================================
        // MODELO TABLA
        // ========================================================

        modeloTabla =
                new DefaultTableModel(
                        new String[]{
                                "Placa",
                                "Marca",
                                "Modelo",
                                "Precio / Día"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        // ========================================================
        // TABLA
        // ========================================================

        tablaVehiculos =
                new JTable(
                        modeloTabla
                );

        tablaVehiculos.setRowHeight(
                38
        );

        tablaVehiculos.setFont(
                orbitron(false, 11f)
        );

        tablaVehiculos.setForeground(
                COLOR_TEXTO
        );

        tablaVehiculos.setBackground(
                COLOR_FILA
        );

        tablaVehiculos.setSelectionBackground(
                COLOR_SELECCION
        );

        tablaVehiculos.setSelectionForeground(
                Color.WHITE
        );

        tablaVehiculos.setShowVerticalLines(
                false
        );

        tablaVehiculos.setShowHorizontalLines(
                true
        );

        tablaVehiculos.setGridColor(
                COLOR_BORDE
        );

        tablaVehiculos.setFillsViewportHeight(
                true
        );

        tablaVehiculos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        // ========================================================
        // CABECERA
        // ========================================================

        tablaVehiculos
                .getTableHeader()
                .setDefaultRenderer(
                        new CabeceraRenderer()
                );

        tablaVehiculos
                .getTableHeader()
                .setBackground(
                        COLOR_ENCABEZADO
                );

        tablaVehiculos
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                40
                        )
                );

        // ========================================================
        // RENDERIZADORES
        // ========================================================

        tablaVehiculos
                .getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        new PlacaCeldaRenderer()
                );

        tablaVehiculos
                .getColumnModel()
                .getColumn(1)
                .setCellRenderer(
                        new CeldaRenderer()
                );

        tablaVehiculos
                .getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        new CeldaRenderer()
                );

        tablaVehiculos
                .getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        new PrecioCeldaRenderer()
                );

        // ========================================================
        // ANCHOS
        // ========================================================

        tablaVehiculos
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(130);

        tablaVehiculos
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(140);

        tablaVehiculos
                .getColumnModel()
                .getColumn(2)
                .setPreferredWidth(140);

        tablaVehiculos
                .getColumnModel()
                .getColumn(3)
                .setPreferredWidth(160);

        // ========================================================
        // SCROLLPANE SIN BARRAS
        // ========================================================

        JScrollPane scrollTabla =
                new JScrollPane(
                        tablaVehiculos,
                        JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                );

        scrollTabla.setBorder(
                BorderFactory.createLineBorder(
                        COLOR_BORDE
                )
        );

        scrollTabla
                .getViewport()
                .setBackground(
                        COLOR_FILA
                );

        panelTabla.add(
                scrollTabla,
                BorderLayout.CENTER
        );

        panelDerecho.add(
                panelTabla,
                BorderLayout.CENTER
        );

        // ========================================================
        // AGREGAR TODO
        // ========================================================

        panelPrincipal.add(
                panelFormulario,
                BorderLayout.WEST
        );

        panelPrincipal.add(
                panelDerecho,
                BorderLayout.CENTER
        );

        add(
                panelPrincipal,
                BorderLayout.CENTER
        );
    }

    // ============================================================
    // FUENTES
    // ============================================================

    private Font cargarFuenteBase(
            String ruta,
            int estilo
    ) {

        try {

            InputStream is =
                    getClass()
                            .getResourceAsStream(ruta);

            if (is != null) {

                Font fuente =
                        Font.createFont(
                                Font.TRUETYPE_FONT,
                                is
                        );

                return fuente.deriveFont(
                        estilo,
                        12f
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "No se pudo cargar la fuente: "
                    + ruta
            );
        }

        return new Font(
                "Arial",
                estilo,
                12
        );
    }

    private Font orbitron(
            boolean bold,
            float size
    ) {

        Font base =
                bold
                        ? orbitronBold
                        : orbitronRegular;

        return base.deriveFont(size);
    }

    // ============================================================
    // LOGO
    // ============================================================

    private javax.swing.Icon cargarLogo(
            String ruta,
            int altura
    ) {

        try {

            InputStream is =
                    getClass()
                            .getResourceAsStream(ruta);

            if (is != null) {

                BufferedImage imagen =
                        ImageIO.read(is);

                return new LogoIcon(
                        imagen,
                        altura
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "No se pudo cargar el logo."
            );
        }

        return null;
    }

    private static class LogoIcon
            implements javax.swing.Icon {

        private final BufferedImage imagen;
        private final int altura;
        private final int ancho;

        public LogoIcon(
                BufferedImage imagen,
                int altura
        ) {

            this.imagen = imagen;
            this.altura = altura;

            this.ancho =
                    (int) (
                            imagen.getWidth()
                            * (
                                    altura
                                    / (double) imagen.getHeight()
                            )
                    );
        }

        @Override
        public int getIconWidth() {
            return ancho;
        }

        @Override
        public int getIconHeight() {
            return altura;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y
        ) {

            g.drawImage(
                    imagen,
                    x,
                    y,
                    ancho,
                    altura,
                    null
            );
        }
    }

    // ============================================================
    // PLACA
    // ============================================================

    private JTextField crearCampoPlaca() {

        JTextField campo =
                new JTextField();

        campo.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        campo.setFont(
                orbitron(true, 19f)
        );

        campo.setForeground(
                COLOR_PLACA_TEXTO
        );

        campo.setBackground(
                COLOR_PLACA
        );

        campo.setCaretColor(
                COLOR_PLACA_TEXTO
        );

        campo.setBorder(
                BorderFactory.createEmptyBorder(
                        3,
                        15,
                        3,
                        15
                )
        );

        campo.addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyTyped(
                            KeyEvent e
                    ) {

                        char c =
                                Character.toUpperCase(
                                        e.getKeyChar()
                                );

                        if (!Character.isLetterOrDigit(c)) {

                            e.consume();

                            return;
                        }

                        if (
                                campo.getText()
                                        .length()
                                >= 6
                        ) {

                            e.consume();

                            return;
                        }

                        e.setKeyChar(c);
                    }
                }
        );

        return campo;
    }

    private class PlacaPanel
            extends JPanel {

        private final JTextField campo;

        public PlacaPanel(
                JTextField campo
        ) {

            this.campo = campo;

            setLayout(
                    new BorderLayout()
            );

            setBackground(
                    COLOR_PLACA
            );

            setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    new Color(
                                            190,
                                            150,
                                            0
                                    ),
                                    2
                            ),
                            BorderFactory.createEmptyBorder(
                                    4,
                                    8,
                                    4,
                                    8
                            )
                    )
            );

            JLabel colombia =
                    new JLabel(
                            "COLOMBIA"
                    );

            colombia.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            colombia.setForeground(
                    COLOR_PLACA_TEXTO
            );

            colombia.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            9
                    )
            );

            add(
                    colombia,
                    BorderLayout.NORTH
            );

            add(
                    campo,
                    BorderLayout.CENTER
            );
        }
    }

    // ============================================================
    // RENDER PLACA (CORREGIDO)
    // ============================================================

    private class PlacaCeldaRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            JLabel label =
                    (JLabel) super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column
                    );

            label.setText(
                    value == null
                            ? ""
                            : value.toString()
            );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setFont(
                    orbitron(true, 10f)
            );

            label.setForeground(
                    COLOR_PLACA_TEXTO
            );

            label.setBackground(
                    COLOR_PLACA
            );

            label.setOpaque(true);

            label.setBorder(
                    BorderFactory.createEmptyBorder(
                            4,
                            8,
                            4,
                            8
                    )
            );

            if (isSelected) {

                label.setBackground(
                        new Color(
                                255,
                                220,
                                50
                        )
                );
            }

            return label;
        }
    }

    // ============================================================
    // LOGOS DE MARCAS
    // ============================================================

    // Recuadro donde se muestra el logo de la marca elegida
    private LogoMarcaPanel logoMarca;

    // Logos ya cargados (para no leer el archivo cada vez)
    private final Map<String, BufferedImage> logosMarca =
            new HashMap<>();

    // Esquina redondeada del recuadro (proporcion del ancho)
    private static final float RADIO_RECUADRO = 0.136f;

    /**
     * Busca el logo en /images/marcas/ usando el nombre de la marca
     * en minusculas (Toyota -> toyota.jpg, Mercedes-Benz -> mercedes-benz.jpg).
     * Devuelve null si no existe (por ejemplo "Otra").
     */
    private BufferedImage obtenerLogoMarca(
            String marca
    ) {

        if (
                marca == null
                || marca.trim().isEmpty()
        ) {

            return null;
        }

        String clave =
                marca
                        .trim()
                        .toLowerCase()
                        .replace(" ", "-");

        if (logosMarca.containsKey(clave)) {

            return logosMarca.get(clave);
        }

        BufferedImage logo = null;

        String[] extensiones = {
                "png",
                "jpg",
                "jpeg"
        };

        for (String ext : extensiones) {

            logo =
                    leerLogoMarca(
                            "/images/marcas/"
                            + clave
                            + "."
                            + ext
                    );

            if (logo != null) {
                break;
            }
        }

        // Se guarda aunque sea null, para no buscarlo otra vez
        logosMarca.put(clave, logo);

        return logo;
    }

    private BufferedImage leerLogoMarca(
            String ruta
    ) {

        try (
                InputStream is =
                        getClass()
                                .getResourceAsStream(ruta)
        ) {

            if (is == null) {

                return null;
            }

            BufferedImage original =
                    ImageIO.read(is);

            if (original == null) {

                return null;
            }

            return recortarRecuadro(original);

        } catch (Exception e) {

            System.out.println(
                    "No se pudo cargar el logo: "
                    + ruta
            );

            return null;
        }
    }

    /**
     * Las imagenes de las marcas traen el logo en un recuadro negro
     * rodeado de mucho fondo gris. Aqui se detecta ese recuadro y se
     * recorta con las esquinas redondeadas, para que el logo se vea grande.
     */
    private BufferedImage recortarRecuadro(
            BufferedImage origen
    ) {

        int w = origen.getWidth();
        int h = origen.getHeight();

        int minX = w;
        int minY = h;
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < h; y += 2) {

            for (int x = 0; x < w; x += 2) {

                int rgb = origen.getRGB(x, y);

                if ((rgb >>> 24) < 200) {
                    continue;
                }

                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // El recuadro es casi negro puro
                if (Math.max(r, Math.max(g, b)) < 10) {

                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }

        int cw = maxX - minX + 2;
        int ch = maxY - minY + 2;

        // Si no se encontro un recuadro razonable, se usa la imagen completa
        if (
                maxX < 0
                || cw < w / 10
                || ch < h / 10
        ) {

            return origen;
        }

        if (minX + cw > w) cw = w - minX;
        if (minY + ch > h) ch = h - minY;

        BufferedImage recorte =
                new BufferedImage(
                        cw,
                        ch,
                        BufferedImage.TYPE_INT_ARGB
                );

        Graphics2D g2 =
                recorte.createGraphics();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        float arco = cw * RADIO_RECUADRO;

        // Mascara redondeada y luego se pega la imagen encima
        g2.fill(
                new RoundRectangle2D.Float(
                        0,
                        0,
                        cw,
                        ch,
                        arco,
                        arco
                )
        );

        g2.setComposite(AlphaComposite.SrcIn);

        g2.drawImage(
                origen,
                -minX,
                -minY,
                null
        );

        g2.dispose();

        return recorte;
    }

    // Reduce a la mitad varias veces y luego al tamano exacto (mejor calidad)
    private BufferedImage escalarImagen(
            BufferedImage origen,
            int anchoFinal,
            int altoFinal
    ) {

        BufferedImage actual = origen;

        int w = origen.getWidth();
        int h = origen.getHeight();

        while (
                w / 2 >= anchoFinal
                && h / 2 >= altoFinal
        ) {

            w /= 2;
            h /= 2;

            actual = redimensionarImagen(actual, w, h);
        }

        return redimensionarImagen(
                actual,
                anchoFinal,
                altoFinal
        );
    }

    private BufferedImage redimensionarImagen(
            BufferedImage origen,
            int ancho,
            int alto
    ) {

        BufferedImage destino =
                new BufferedImage(
                        ancho,
                        alto,
                        BufferedImage.TYPE_INT_ARGB
                );

        Graphics2D g2 =
                destino.createGraphics();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );

        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.drawImage(
                origen,
                0,
                0,
                ancho,
                alto,
                null
        );

        g2.dispose();

        return destino;
    }

    // ------------------------------------------------------------
    // Recuadro con el logo de la marca seleccionada
    // ------------------------------------------------------------

    private class LogoMarcaPanel
            extends JPanel {

        private String marca = "";
        private BufferedImage logo;

        private BufferedImage cache;
        private int cacheW = -1;
        private int cacheH = -1;

        public LogoMarcaPanel() {

            setOpaque(false);

            setPreferredSize(
                    new Dimension(
                            56,
                            56
                    )
            );
        }

        public void setMarca(
                String marca
        ) {

            this.marca =
                    marca == null
                            ? ""
                            : marca;

            this.logo =
                    obtenerLogoMarca(
                            this.marca
                    );

            this.cache = null;

            repaint();
        }

        @Override
        protected void paintComponent(
                Graphics g
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            boolean hayMarca =
                    !marca.isEmpty();

            int lado =
                    Math.min(
                            getWidth(),
                            getHeight()
                    );

            int anchoDibujo = lado;
            int altoDibujo = lado;

            if (logo != null) {

                altoDibujo =
                        Math.round(
                                lado
                                * (
                                        logo.getHeight()
                                        / (float) logo.getWidth()
                                )
                        );
            }

            int x = (getWidth() - anchoDibujo) / 2;
            int y = (getHeight() - altoDibujo) / 2;

            float arco =
                    anchoDibujo * RADIO_RECUADRO;

            if (logo != null) {

                // Se dibuja a la resolucion real de la pantalla
                Graphics2D gi =
                        (Graphics2D) g2.create();

                double sx =
                        gi.getTransform().getScaleX();

                double sy =
                        gi.getTransform().getScaleY();

                int pw =
                        Math.max(
                                1,
                                (int) Math.round(
                                        anchoDibujo * sx
                                )
                        );

                int ph =
                        Math.max(
                                1,
                                (int) Math.round(
                                        altoDibujo * sy
                                )
                        );

                if (
                        cache == null
                        || cacheW != pw
                        || cacheH != ph
                ) {

                    cache =
                            escalarImagen(
                                    logo,
                                    pw,
                                    ph
                            );

                    cacheW = pw;
                    cacheH = ph;
                }

                gi.translate(x, y);

                gi.scale(
                        1.0 / sx,
                        1.0 / sy
                );

                gi.drawImage(
                        cache,
                        0,
                        0,
                        null
                );

                gi.dispose();

                // Aro rojo alrededor del logo
                g2.setColor(COLOR_ACENTO);

                g2.setStroke(
                        new BasicStroke(2f)
                );

                g2.draw(
                        new RoundRectangle2D.Float(
                                x + 1,
                                y + 1,
                                anchoDibujo - 2,
                                altoDibujo - 2,
                                arco,
                                arco
                        )
                );

            } else {

                // Sin logo: recuadro vacio con texto
                RoundRectangle2D forma =
                        new RoundRectangle2D.Float(
                                x + 1,
                                y + 1,
                                anchoDibujo - 2,
                                altoDibujo - 2,
                                arco,
                                arco
                        );

                g2.setColor(COLOR_CAMPO);

                g2.fill(forma);

                if (hayMarca) {

                    g2.setColor(COLOR_ACENTO);

                    g2.setStroke(
                            new BasicStroke(2f)
                    );

                } else {

                    g2.setColor(COLOR_BORDE);

                    g2.setStroke(
                            new BasicStroke(
                                    1.5f,
                                    BasicStroke.CAP_BUTT,
                                    BasicStroke.JOIN_MITER,
                                    10f,
                                    new float[]{4f, 4f},
                                    0f
                            )
                    );
                }

                g2.draw(forma);

                String texto;

                if (!hayMarca) {

                    texto = "?";

                } else if (marca.length() > 4) {

                    texto =
                            marca
                                    .substring(0, 3)
                                    .toUpperCase();

                } else {

                    texto =
                            marca.toUpperCase();
                }

                g2.setFont(
                        orbitron(
                                true,
                                hayMarca
                                        ? 11f
                                        : 22f
                        )
                );

                g2.setColor(COLOR_GRIS);

                FontMetrics fm =
                        g2.getFontMetrics();

                int tx =
                        x
                        + (
                                anchoDibujo
                                - fm.stringWidth(texto)
                        ) / 2;

                int ty =
                        y
                        + (
                                altoDibujo
                                - fm.getHeight()
                        ) / 2
                        + fm.getAscent();

                g2.drawString(
                        texto,
                        tx,
                        ty
                );
            }

            g2.dispose();
        }
    }

    // ------------------------------------------------------------
    // Fila "Marca": recuadro con el logo + lista desplegable
    // ------------------------------------------------------------

    private class MarcaPanel
            extends JPanel {

        public MarcaPanel(
                LogoMarcaPanel logo,
                JComboBox<String> combo
        ) {

            setLayout(
                    new BorderLayout(
                            8,
                            0
                    )
            );

            setOpaque(false);

            JPanel centro =
                    new JPanel(
                            new GridBagLayout()
                    );

            centro.setOpaque(false);

            GridBagConstraints c =
                    new GridBagConstraints();

            c.fill =
                    GridBagConstraints.HORIZONTAL;

            c.weightx = 1;

            centro.add(
                    combo,
                    c
            );

            add(
                    logo,
                    BorderLayout.WEST
            );

            add(
                    centro,
                    BorderLayout.CENTER
            );
        }
    }

    // ============================================================
    // MARCAS
    // ============================================================

    private JComboBox<String> crearComboMarca() {

        String[] marcas = {

                "Selecciona una marca...",

                "Chevrolet",
                "Renault",
                "Mazda",
                "Kia",
                "Toyota",
                "Nissan",
                "Ford",
                "Hyundai",
                "Volkswagen",
                "Suzuki",
                "Honda",
                "BMW",
                "Mercedes-Benz",
                "Audi",
                "Peugeot",
                "Jeep",
                "Fiat",
                "Chery",
                "BYD",
                "Otra"
        };

        JComboBox<String> combo =
                new JComboBox<>(marcas);

        combo.setFont(
                orbitron(false, 11f)
        );

        combo.setBackground(
                COLOR_CAMPO
        );

        combo.setForeground(
                COLOR_TEXTO
        );

        combo.setBorder(
                crearBordeCampo(
                        COLOR_BORDE
                )
        );

        combo.setPreferredSize(
                new Dimension(
                        200,
                        38
                )
        );

        combo.setMaximumRowCount(
                marcas.length
        );

        combo.setFocusable(true);

        combo.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {

                        JLabel label =
                                (JLabel) super
                                        .getListCellRendererComponent(
                                                list,
                                                value,
                                                index,
                                                isSelected,
                                                cellHasFocus
                                        );

                        label.setFont(
                                orbitron(
                                        false,
                                        11f
                                )
                        );

                        label.setBorder(
                                BorderFactory.createEmptyBorder(
                                        7,
                                        10,
                                        7,
                                        10
                                )
                        );

                        if (isSelected) {

                            label.setBackground(
                                    COLOR_SELECCION
                            );

                            label.setForeground(
                                    Color.WHITE
                            );

                        } else {

                            label.setBackground(
                                    COLOR_CAMPO
                            );

                            if (
                                    value != null
                                    && value.toString()
                                            .equals(
                                                    "Selecciona una marca..."
                                            )
                            ) {

                                label.setForeground(
                                        COLOR_GRIS
                                );

                            } else {

                                label.setForeground(
                                        COLOR_TEXTO
                                );
                            }
                        }

                        return label;
                    }
                }
        );

        combo.addFocusListener(
                new FocusAdapter() {

                    @Override
                    public void focusGained(
                            FocusEvent e
                    ) {

                        combo.setBorder(
                                BorderFactory.createLineBorder(
                                        COLOR_ACENTO,
                                        1
                                )
                        );
                    }

                    @Override
                    public void focusLost(
                            FocusEvent e
                    ) {

                        combo.setBorder(
                                crearBordeCampo(
                                        COLOR_BORDE
                                )
                        );
                    }
                }
        );

        return combo;
    }

    // ============================================================
    // CAMPO TEXTO
    // ============================================================

    private JTextField crearCampoTexto() {

        JTextField campo =
                new JTextField();

        campo.setFont(
                orbitron(false, 11f)
        );

        campo.setForeground(
                COLOR_TEXTO
        );

        campo.setBackground(
                COLOR_CAMPO
        );

        campo.setCaretColor(
                COLOR_ACENTO
        );

        campo.setBorder(
                crearBordeCampo(
                        COLOR_BORDE
                )
        );

        campo.setPreferredSize(
                new Dimension(
                        200,
                        38
                )
        );

        campo.addFocusListener(
                new FocusAdapter() {

                    @Override
                    public void focusGained(
                            FocusEvent e
                    ) {

                        campo.setBorder(
                                crearBordeCampo(
                                        COLOR_ACENTO
                                )
                        );
                    }

                    @Override
                    public void focusLost(
                            FocusEvent e
                    ) {

                        campo.setBorder(
                                crearBordeCampo(
                                        COLOR_BORDE
                                )
                        );
                    }
                }
        );

        return campo;
    }

    // ============================================================
    // PRECIO
    // ============================================================

    private JTextField crearCampoPrecio() {

        JTextField campo =
                new JTextField();

        campo.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        campo.setFont(
                orbitron(true, 18f)
        );

        campo.setForeground(
                COLOR_DISPLAY_ROJO
        );

        campo.setBackground(
                COLOR_DISPLAY_FONDO
        );

        campo.setCaretColor(
                COLOR_DISPLAY_ROJO
        );

        campo.setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        45,
                        5,
                        55
                )
        );

        campo.addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyReleased(
                            KeyEvent e
                    ) {

                        String texto =
                                campo.getText()
                                        .replace(
                                                ".",
                                                ""
                                        )
                                        .replace(
                                                "$",
                                                ""
                                        )
                                        .replace(
                                                " ",
                                                ""
                                        );

                        if (
                                texto.isEmpty()
                                || !texto.matches(
                                        "\\d+"
                                )
                        ) {

                            return;
                        }

                        try {

                            long numero =
                                    Long.parseLong(
                                            texto
                                    );

                            String formateado =
                                    formatearMiles(
                                            numero
                                    );

                            if (
                                    !campo.getText()
                                            .equals(
                                                    formateado
                                            )
                            ) {

                                int posicion =
                                        campo.getCaretPosition();

                                int digitosAntes =
                                        contarDigitos(
                                                campo.getText()
                                                        .substring(
                                                                0,
                                                                Math.min(
                                                                        posicion,
                                                                        campo.getText()
                                                                                .length()
                                                                )
                                                        )
                                        );

                                campo.setText(
                                        formateado
                                );

                                campo.setCaretPosition(
                                        posicionTrasDigitos(
                                                formateado,
                                                digitosAntes
                                        )
                                );
                            }

                        } catch (Exception ex) {
                            // Ignorar valores inválidos
                        }
                    }
                }
        );

        return campo;
    }

    private int contarDigitos(
            String texto
    ) {

        int contador = 0;

        for (
                int i = 0;
                i < texto.length();
                i++
        ) {

            if (
                    Character.isDigit(
                            texto.charAt(i)
                    )
            ) {

                contador++;
            }
        }

        return contador;
    }

    private int posicionTrasDigitos(
            String texto,
            int cantidad
    ) {

        int contador = 0;

        for (
                int i = 0;
                i < texto.length();
                i++
        ) {

            if (
                    Character.isDigit(
                            texto.charAt(i)
                    )
            ) {

                contador++;

                if (
                        contador >= cantidad
                ) {

                    return i + 1;
                }
            }
        }

        return texto.length();
    }

    // ============================================================
    // PANEL PRECIO
    // ============================================================

    private class PrecioPanel
            extends JPanel {

        private final JTextField campo;

        public PrecioPanel(
                JTextField campo
        ) {

            this.campo = campo;

            setLayout(
                    new BorderLayout()
            );

            setBackground(
                    COLOR_DISPLAY_FONDO
            );

            setBorder(
                    BorderFactory.createLineBorder(
                            COLOR_DISPLAY_BORDE,
                            2
                    )
            );

            JLabel simbolo =
                    new JLabel("$");

            simbolo.setForeground(
                    COLOR_DISPLAY_ROJO
            );

            simbolo.setFont(
                    orbitron(true, 18f)
            );

            simbolo.setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            10,
                            0,
                            0
                    )
            );

            JLabel dia =
                    new JLabel("/ DÍA");

            dia.setForeground(
                    COLOR_GRIS
            );

            dia.setFont(
                    orbitron(true, 10f)
            );

            dia.setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            0,
                            0,
                            10
                    )
            );

            add(
                    simbolo,
                    BorderLayout.WEST
            );

            add(
                    campo,
                    BorderLayout.CENTER
            );

            add(
                    dia,
                    BorderLayout.EAST
            );
        }
    }

    // ============================================================
    // PRECIO EN TABLA (CORREGIDO)
    // ============================================================

    private class PrecioCeldaRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            JLabel label =
                    (JLabel) super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column
                    );

            String precio =
                    value == null
                            ? "0"
                            : value.toString();

            label.setText(
                    "$ "
                    + formatearPrecio(precio)
                    + " / DÍA"
            );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setFont(
                    orbitron(true, 10f)
            );

            label.setForeground(
                    COLOR_DISPLAY_ROJO
            );

            label.setBackground(
                    COLOR_DISPLAY_FONDO
            );

            label.setOpaque(true);

            label.setBorder(
                    BorderFactory.createLineBorder(
                            COLOR_DISPLAY_BORDE
                    )
            );

            if (isSelected) {

                label.setBackground(
                        new Color(
                                25,
                                10,
                                12
                        )
                );
            }

            return label;
        }
    }

    private String formatearPrecio(
            String precio
    ) {

        try {

            String limpio =
                    precio
                            .replace(
                                    ".",
                                    ""
                            )
                            .replace(
                                    "$",
                                    ""
                            )
                            .trim();

            long numero =
                    Long.parseLong(
                            limpio
                    );

            return formatearMiles(
                    numero
            );

        } catch (Exception e) {

            return precio;
        }
    }

    private String formatearMiles(
            long numero
    ) {

        NumberFormat formato =
                NumberFormat.getNumberInstance(
                        Locale.US
                );

        String resultado =
                formato.format(
                        numero
                );

        return resultado.replace(
                ",",
                "."
        );
    }

    // ============================================================
    // BOTÓN
    // ============================================================

    private class BotonRacing
            extends JButton {

        private final Color colorNormal;
        private final Color colorHover;

        private boolean hover = false;

        public BotonRacing(
                String texto,
                Color colorNormal,
                Color colorHover
        ) {

            super(texto);

            this.colorNormal =
                    colorNormal;

            this.colorHover =
                    colorHover;

            setFont(
                    orbitron(true, 11f)
            );

            setForeground(
                    Color.WHITE
            );

            setBackground(
                    colorNormal
            );

            setFocusPainted(
                    false
            );

            setBorderPainted(
                    false
            );

            setContentAreaFilled(
                    false
            );

            setOpaque(false);

            setCursor(
                    new java.awt.Cursor(
                            java.awt.Cursor.HAND_CURSOR
                    )
            );

            addMouseListener(
                    new java.awt.event.MouseAdapter() {

                        @Override
                        public void mouseEntered(
                                java.awt.event.MouseEvent e
                        ) {

                            hover = true;

                            repaint();
                        }

                        @Override
                        public void mouseExited(
                                java.awt.event.MouseEvent e
                        ) {

                            hover = false;

                            repaint();
                        }
                    }
            );
        }

        @Override
        protected void paintComponent(
                Graphics g
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color color =
                    hover
                            ? colorHover
                            : colorNormal;

            g2.setColor(color);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    10,
                    10
            );

            g2.setColor(
                    new Color(
                            255,
                            255,
                            255,
                            30
                    )
            );

            g2.drawRoundRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1,
                    10,
                    10
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // ============================================================
    // CELDA NORMAL
    // ============================================================

    private class CeldaRenderer
            extends DefaultTableCellRenderer {

        public CeldaRenderer() {

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            JLabel label =
                    (JLabel) super
                            .getTableCellRendererComponent(
                                    table,
                                    value,
                                    isSelected,
                                    hasFocus,
                                    row,
                                    column
                            );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setFont(
                    orbitron(false, 10f)
            );

            if (isSelected) {

                label.setBackground(
                        COLOR_SELECCION
                );

                label.setForeground(
                        Color.WHITE
                );

            } else {

                label.setBackground(
                        row % 2 == 0
                                ? COLOR_FILA
                                : COLOR_FILA_ALT
                );

                label.setForeground(
                        COLOR_TEXTO
                );
            }

            label.setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            5,
                            0,
                            5
                    )
            );

            return label;
        }
    }

    // ============================================================
    // CABECERA
    // ============================================================

    private class CabeceraRenderer
            extends DefaultTableCellRenderer {

        public CabeceraRenderer() {

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            JLabel label =
                    (JLabel) super
                            .getTableCellRendererComponent(
                                    table,
                                    value,
                                    isSelected,
                                    hasFocus,
                                    row,
                                    column
                            );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setFont(
                    orbitron(true, 10f)
            );

            label.setForeground(
                    COLOR_GRIS
            );

            label.setBackground(
                    COLOR_ENCABEZADO
            );

            label.setBorder(
                    BorderFactory.createMatteBorder(
                            0,
                            0,
                            1,
                            0,
                            COLOR_BORDE
                    )
            );

            return label;
        }
    }

    // ============================================================
    // BORDES
    // ============================================================

    private javax.swing.border.Border crearBordeCampo(
            Color color
    ) {

        return BorderFactory.createCompoundBorder(

                BorderFactory.createLineBorder(
                        color,
                        1
                ),

                BorderFactory.createEmptyBorder(
                        5,
                        10,
                        5,
                        10
                )
        );
    }

    // ============================================================
    // ETIQUETAS
    // ============================================================

    private JLabel crearEtiqueta(
            String texto
    ) {

        JLabel label =
                new JLabel(
                        texto
                );

        label.setFont(
                orbitron(true, 10f)
        );

        label.setForeground(
                COLOR_GRIS
        );

        return label;
    }

    // ============================================================
    // BOTONES
    // ============================================================

    private void configurarBoton(
            JButton boton
    ) {

        boton.setPreferredSize(
                new Dimension(
                        200,
                        42
                )
        );
    }

    // ============================================================
    // TARJETAS
    // ============================================================

    private JPanel crearTarjeta(
            String titulo,
            String descripcion
    ) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                0,
                                5
                        )
                );

        tarjeta.setBackground(
                COLOR_PANEL
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),

                        BorderFactory.createEmptyBorder(
                                12,
                                15,
                                12,
                                15
                        )
                )
        );

        JLabel lblTitulo =
                new JLabel(
                        titulo
                );

        lblTitulo.setFont(
                orbitron(true, 10f)
        );

        lblTitulo.setForeground(
                COLOR_ACENTO
        );

        JLabel lblDescripcion =
                new JLabel(
                        descripcion
                );

        lblDescripcion.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        11
                )
        );

        lblDescripcion.setForeground(
                COLOR_GRIS
        );

        tarjeta.add(
                lblTitulo,
                BorderLayout.NORTH
        );

        tarjeta.add(
                lblDescripcion,
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    // ============================================================
    // MÉTODOS PARA VEHICULOPRESENTER
    // ============================================================

    public String getPlaca() {

        return txtPlaca
                .getText()
                .trim()
                .toUpperCase();
    }

    public String getMarca() {

        Object seleccion =
                comboMarca.getSelectedItem();

        if (
                seleccion == null
                || seleccion.toString()
                        .equals(
                                "Selecciona una marca..."
                        )
        ) {

            return "";
        }

        return seleccion
                .toString()
                .trim();
    }

    public String getModelo() {

        return txtModelo
                .getText()
                .trim();
    }

    public double getPrecio() {

        String texto =
                txtPrecio
                        .getText()
                        .replace(
                                ".",
                                ""
                        )
                        .replace(
                                "$",
                                ""
                        )
                        .trim();

        if (texto.isEmpty()) {

            return 0.0;
        }

        try {

            return Double.parseDouble(
                    texto
            );

        } catch (
                NumberFormatException e
        ) {

            return 0.0;
        }
    }

    public JButton getBtnGuardar() {

        return btnGuardar;
    }

    public JButton getBtnEliminar() {

        return btnEliminar;
    }

    public DefaultTableModel getModeloTabla() {

        return modeloTabla;
    }

    public String getPlacaSeleccionada() {

        int fila =
                tablaVehiculos
                        .getSelectedRow();

        if (fila == -1) {

            return null;
        }

        Object valor =
                modeloTabla.getValueAt(
                        fila,
                        0
                );

        return valor == null
                ? null
                : valor.toString();
    }

    public void limpiarCampos() {

        txtPlaca.setText("");

        comboMarca.setSelectedIndex(0);

        txtModelo.setText("");

        txtPrecio.setText("");

        txtPlaca.requestFocus();
    }
}