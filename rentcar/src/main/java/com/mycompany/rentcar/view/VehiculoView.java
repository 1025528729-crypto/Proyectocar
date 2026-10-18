package com.mycompany.rentcar.view;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.mycompany.rentcar.model.Vehiculo;
import com.mycompany.rentcar.util.FotoVehiculoUtil;
import com.mycompany.rentcar.presenter.MainPublicPresenter;
import com.mycompany.rentcar.view.MainPublicView;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.FontMetrics;

import java.awt.geom.RoundRectangle2D;


public class VehiculoView extends JPanel {

    // ============================================================
    // CAMPOS
    // ============================================================

    private JTextField txtPlaca;
    private JComboBox<String> comboMarca;
    private JTextField txtModelo;
    private JTextField txtPrecio;
    private JComboBox<String> comboTipo, comboTransmision, comboCombustible, comboCategoria;
    private JTextField txtAnio, txtColor, txtCapacidad, txtKilometraje, txtPuertas;
    private JComboBox<String> comboCiudad;
    private JTextArea txtDescripcion;
    private javax.swing.JCheckBox chkDisponible;

    // FOTO DEL VEHÍCULO
    private JButton btnSubirFoto;
    private JLabel lblFotoPreview;
    private String rutaFotoSeleccionada = "";

    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnModificar;

    private JTable tablaVehiculos;
    private DefaultTableModel modeloTabla;

    // PÁGINAS (formulario / tabla a pantalla completa)
    private static final String PAG_FORM = "form";
    private static final String PAG_TABLA = "tabla";
    private CardLayout paginas;
    private JPanel contenedorPaginas;
    private JLabel lblTarjetaTabla;
    private JLabel lblContadorTabla;
    private JButton btnVolverFormulario;

    private static final String[] COLUMNAS_TABLA = {
        "Foto", "Placa", "Marca", "Modelo", "Precio / Día",
        "Tipo", "Año", "Color", "Transmisión", "Combustible",
        "Capacidad", "Puertas", "Kilometraje", "Categoría", "Ciudad", "Estado"
    };


    // ============================================================
    // COLORES
    // ============================================================

    private final Color COLOR_FONDO =
            TemaRentCar.c(242, 244, 248);

    private final Color COLOR_ENCABEZADO =
            TemaRentCar.c(242, 244, 248);

    private final Color COLOR_PANEL =
            TemaRentCar.c(255, 255, 255);

    private final Color COLOR_BORDE =
            TemaRentCar.c(230, 234, 241);

    private final Color COLOR_CAMPO =
            TemaRentCar.c(238, 241, 246);

    private final Color COLOR_TEXTO =
            TemaRentCar.c(23, 27, 34);

    private final Color COLOR_GRIS =
            TemaRentCar.c(98, 105, 118);

    private final Color COLOR_ACENTO =
            TemaRentCar.c(225, 6, 0);

    private final Color COLOR_ACENTO_HOVER =
            TemaRentCar.c(255, 45, 38);

    private final Color COLOR_ELIMINAR =
            TemaRentCar.c(55, 60, 70);

    private final Color COLOR_ELIMINAR_HOVER =
            TemaRentCar.c(150, 22, 22);

    private final Color COLOR_FILA =
            TemaRentCar.c(255, 255, 255);

    private final Color COLOR_FILA_ALT =
            TemaRentCar.c(238, 241, 246);

    private final Color COLOR_SELECCION =
            TemaRentCar.c(254, 228, 228);

    // PLACA
    private final Color COLOR_PLACA =
            TemaRentCar.c(255, 205, 0);

    private final Color COLOR_PLACA_TEXTO =
            TemaRentCar.c(15, 15, 15);

    // PRECIO DIGITAL
    private final Color COLOR_DISPLAY_FONDO =
            TemaRentCar.c(255, 244, 244);

    private final Color COLOR_DISPLAY_BORDE =
            TemaRentCar.c(232, 168, 168);

    private final Color COLOR_DISPLAY_ROJO =
            TemaRentCar.c(205, 18, 18);


    // ============================================================
    // FOTO DEL VEHÍCULO
    // ============================================================

    private JPanel crearPanelFoto() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(12, 0)
                );

        panel.setOpaque(false);

        lblFotoPreview =
                new JLabel(
                        "SIN FOTO",
                        SwingConstants.CENTER
                );

        lblFotoPreview.setFont(
                orbitron(true, 10f)
        );

        lblFotoPreview.setForeground(
                COLOR_GRIS
        );

        lblFotoPreview.setBackground(
                COLOR_CAMPO
        );

        lblFotoPreview.setOpaque(true);

        lblFotoPreview.setPreferredSize(
                new Dimension(125, 78)
        );

        lblFotoPreview.setBorder(
                BorderFactory.createLineBorder(
                        COLOR_BORDE
                )
        );

        btnSubirFoto =
                new BotonRacing(
                        "Subir foto",
                        COLOR_ELIMINAR,
                        COLOR_ELIMINAR_HOVER
                );

        configurarBoton(btnSubirFoto);

        btnSubirFoto.setPreferredSize(
                new Dimension(150, 42)
        );

        btnSubirFoto.addActionListener(
                e -> seleccionarFoto()
        );

        JPanel info =
                new JPanel(
                        new GridBagLayout()
                );

        info.setOpaque(false);

        GridBagConstraints c =
                new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 6, 0);

        info.add(
                btnSubirFoto,
                c
        );

        JLabel ayuda =
                new JLabel(
                        "JPG, JPEG o PNG · Máx. recomendado: 5 MB"
                );

        ayuda.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        10
                )
        );

        ayuda.setForeground(
                COLOR_GRIS
        );

        c.gridy = 1;

        info.add(
                ayuda,
                c
        );

        panel.add(
                lblFotoPreview,
                BorderLayout.WEST
        );

        panel.add(
                info,
                BorderLayout.CENTER
        );

        return panel;
    }


    private void seleccionarFoto() {

        JFileChooser selector =
                new JFileChooser();

        selector.setDialogTitle(
                "Seleccionar foto del vehículo"
        );

        selector.setFileFilter(
                new FileNameExtensionFilter(
                        "Imágenes JPG, JPEG y PNG",
                        "jpg",
                        "jpeg",
                        "png"
                )
        );

        selector.setAcceptAllFileFilterUsed(false);

        int resultado =
                selector.showOpenDialog(this);

        if (
                resultado
                != JFileChooser.APPROVE_OPTION
        ) {
            return;
        }

        File archivo =
                selector.getSelectedFile();

        String placa =
                getPlaca();

        if (placa.isEmpty()) {

            MensajeRentCar.mostrar(
                    this,
                    "Primero ingresa la placa del vehículo.",
                    "Placa requerida",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String nombre =
                archivo.getName();

        int punto =
                nombre.lastIndexOf('.');

        String extension =
                punto >= 0
                        ? nombre
                                .substring(punto)
                                .toLowerCase(Locale.ROOT)
                        : ".jpg";

        try {
            // Cada selección recibe un nombre único; nunca se sobrescribe la foto de otro vehículo.
            rutaFotoSeleccionada = FotoVehiculoUtil.guardarCopiaUnica(archivo, placa);
            mostrarVistaPrevia(archivo);
        } catch (IOException ex) {

            MensajeRentCar.mostrar(
                    this,
                    "No se pudo guardar la foto: "
                    + ex.getMessage(),
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // Lee la foto saltando píxeles (máx. ~400 px): una foto de cámara de varios MB
    // tardaba cientos de ms en decodificarse solo para mostrar una miniatura.
    private BufferedImage leerFotoReducida(File archivo) throws IOException {

        try (javax.imageio.stream.ImageInputStream in = ImageIO.createImageInputStream(archivo)) {

            if (in == null) {
                return null;
            }

            java.util.Iterator<javax.imageio.ImageReader> lectores = ImageIO.getImageReaders(in);

            if (!lectores.hasNext()) {
                return null;
            }

            javax.imageio.ImageReader lector = lectores.next();

            try {
                lector.setInput(in, true, true);

                int paso = Math.max(1, Math.max(lector.getWidth(0), lector.getHeight(0)) / 400);

                javax.imageio.ImageReadParam parametros = lector.getDefaultReadParam();
                parametros.setSourceSubsampling(paso, paso, 0, 0);

                return lector.read(0, parametros);

            } finally {
                lector.dispose();
            }
        }
    }

    private void mostrarVistaPrevia(
            File archivo
    ) {

        try {

            BufferedImage imagen =
                    leerFotoReducida(
                            archivo
                    );

            if (imagen == null) {

                throw new IOException(
                        "El archivo no es una imagen válida."
                );
            }

            int ancho = 125;
            int alto = 78;

            double escala =
                    Math.min(
                            (double) ancho
                            / imagen.getWidth(),
                            (double) alto
                            / imagen.getHeight()
                    );

            int nuevoAncho =
                    Math.max(
                            1,
                            (int) (
                                    imagen.getWidth()
                                    * escala
                            )
                    );

            int nuevoAlto =
                    Math.max(
                            1,
                            (int) (
                                    imagen.getHeight()
                                    * escala
                            )
                    );

            ImageIcon icono =
                    new ImageIcon(
                            TemaRentCar.escalarImagen(imagen, nuevoAncho, nuevoAlto)
                    );

            lblFotoPreview.setText("");

            lblFotoPreview.setIcon(
                    icono
            );

        } catch (IOException ex) {

            lblFotoPreview.setIcon(null);

            lblFotoPreview.setText(
                    "SIN FOTO"
            );
        }
    }


    // ============================================================
    // FUENTES
    // ============================================================

    private Font orbitronRegular;
    private Font orbitronBold;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public VehiculoView() {

        orbitronRegular =
                cargarFuenteBase(
                        "/fonts/Orbitron-Regular.ttf",
                        Font.PLAIN
                );

        orbitronBold =
                cargarFuenteBase(
                        "/fonts/Orbitron-Bold.ttf",
                        Font.BOLD
                );

        aplicarTemaMenus();
        initComponents();
    }


    // ============================================================
    // INTERFAZ
    // ============================================================

    private void initComponents() {

        setLayout(
                new BorderLayout()
        );

        setBackground(
                COLOR_FONDO
        );


        // ========================================================
        // ENCABEZADO
        // ========================================================

        JPanel panelEncabezado =
                new JPanel(
                        new BorderLayout()
                );

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

        JLabel lblSubtitulo =
                new JLabel(
                        "Gestión de vehículos y alquileres"
                );

        lblSubtitulo.setForeground(
                COLOR_GRIS
        );

        lblSubtitulo.setFont(
                orbitron(
                        false,
                        12f
                )
        );

        JPanel panelTitulos =
                new JPanel(
                        new BorderLayout(
                                15,
                                0
                        )
                );

        panelTitulos.setOpaque(false);

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
                orbitron(
                        true,
                        25f
                )
        );
        lblModulo.setHorizontalAlignment(SwingConstants.RIGHT);
        lblModulo.setToolTipText("Gestión de vehículos RentCar");

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
                        new BorderLayout(
                                20,
                                20
                        )
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
        // FORMULARIO REDISEÑADO: SECCIONES Y CAMPOS EN DOS COLUMNAS
        // ========================================================

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new javax.swing.BoxLayout(panelFormulario, javax.swing.BoxLayout.Y_AXIS));
        panelFormulario.setBackground(COLOR_FONDO);
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(8, 8, 18, 8));
        panelFormulario.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel cabeceraFormulario = new JPanel(new BorderLayout(0, 7));
        cabeceraFormulario.setBackground(COLOR_FONDO);
        cabeceraFormulario.setBorder(BorderFactory.createEmptyBorder(8, 8, 14, 8));
        JLabel lblFormulario = new JLabel("Registrar vehículo");
        lblFormulario.setFont(orbitron(true, 19f));
        lblFormulario.setForeground(COLOR_TEXTO);
        JLabel lblAyudaFormulario = new JLabel("Completa los datos del automóvil o motocicleta");
        lblAyudaFormulario.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblAyudaFormulario.setForeground(COLOR_GRIS);
        cabeceraFormulario.add(lblFormulario, BorderLayout.NORTH);
        cabeceraFormulario.add(lblAyudaFormulario, BorderLayout.CENTER);
        cabeceraFormulario.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelFormulario.add(cabeceraFormulario);

        // Se crean primero los controles para poder distribuirlos por secciones.
        txtPlaca = crearCampoPlaca();
        comboMarca = crearComboMarca();
        logoMarca = new LogoMarcaPanel();
        comboMarca.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && logoMarca != null) {
                logoMarca.setMarca(getMarca());
            }
        });
        txtModelo = crearCampoTexto();
        txtPrecio = crearCampoPrecio();
        comboTipo = new JComboBox<>(new String[]{"AUTO", "MOTO"});
        comboTransmision = new JComboBox<>(new String[]{"No especificada", "Manual", "Automática", "Semiautomática"});
        comboCombustible = new JComboBox<>(new String[]{"No especificado", "Gasolina", "Diésel", "Eléctrico", "Híbrido"});
        comboCategoria = new JComboBox<>(new String[]{"No especificada", "Compacto", "Sedán", "SUV", "Pickup", "Deportiva", "Scooter", "Naked", "Touring", "Enduro", "Trabajo", "Otra"});
        comboCiudad = new JComboBox<>(obtenerLugaresColombia());
        txtAnio = crearCampoTexto();
        txtColor = crearCampoTexto();
        txtCapacidad = crearCampoTexto();
        txtKilometraje = crearCampoTexto();
        txtPuertas = crearCampoTexto();
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBackground(COLOR_CAMPO);
        txtDescripcion.setForeground(COLOR_TEXTO);
        txtDescripcion.setCaretColor(COLOR_TEXTO);
        txtDescripcion.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDescripcion.setBorder(BorderFactory.createEmptyBorder(8, 9, 8, 9));
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        scrollDescripcion.setPreferredSize(new Dimension(360, 78));
        estilizarBarraDesplazamiento(scrollDescripcion);
        chkDisponible = new javax.swing.JCheckBox("Disponible para alquiler", true);
        chkDisponible.setOpaque(false);
        chkDisponible.setForeground(COLOR_TEXTO);
        chkDisponible.setFont(new Font("SansSerif", Font.BOLD, 12));

        for (JComboBox<String> combo : new JComboBox[]{comboTipo, comboTransmision, comboCombustible, comboCategoria, comboCiudad}) {
            estilizarComboDetalle(combo);
            combo.setSelectedIndex(-1);
        }
        comboCiudad.setPreferredSize(new Dimension(190, 36));
        comboTipo.addActionListener(e -> {
            boolean moto = "MOTO".equalsIgnoreCase(getTipo());
            txtCapacidad.setText("");
            if (moto) {
                txtPuertas.setText("0");
                txtPuertas.setEnabled(false);
            } else {
                txtPuertas.setText("");
                txtPuertas.setEnabled(true);
            }
            actualizarMarcasPorTipo();
        });

        JPanel seccionIdentificacion = crearSeccionFormulario("01  ·  IDENTIFICACIÓN");
        agregarCampoFormulario(seccionIdentificacion, "Placa colombiana", new PlacaPanel(txtPlaca));
        agregarCampoFormulario(seccionIdentificacion, "Marca", new MarcaPanel(logoMarca, comboMarca));
        agregarCampoFormulario(seccionIdentificacion, "Modelo", txtModelo);
        agregarCampoFormulario(seccionIdentificacion, "Precio por día (COP)", new PrecioPanel(txtPrecio));
        JPanel colIzquierda = new JPanel();
        colIzquierda.setLayout(new javax.swing.BoxLayout(colIzquierda, javax.swing.BoxLayout.Y_AXIS));
        colIzquierda.setOpaque(false);
        JPanel colDerecha = new JPanel();
        colDerecha.setLayout(new javax.swing.BoxLayout(colDerecha, javax.swing.BoxLayout.Y_AXIS));
        colDerecha.setOpaque(false);
        agregarSeccionAlFormulario(colIzquierda, seccionIdentificacion);

        JPanel seccionEspecificaciones = crearSeccionFormulario("03  ·  ESPECIFICACIONES");
        agregarCampoFormulario(seccionEspecificaciones, "Tipo de vehículo", comboTipo);
        agregarCampoFormulario(seccionEspecificaciones, "Año", txtAnio);
        agregarCampoFormulario(seccionEspecificaciones, "Color", txtColor);
        agregarCampoFormulario(seccionEspecificaciones, "Transmisión", comboTransmision);
        agregarCampoFormulario(seccionEspecificaciones, "Combustible", comboCombustible);
        agregarCampoFormulario(seccionEspecificaciones, "Capacidad (personas)", txtCapacidad);
        agregarCampoFormulario(seccionEspecificaciones, "Número de puertas", txtPuertas);
        agregarCampoFormulario(seccionEspecificaciones, "Kilometraje (km)", txtKilometraje);
        agregarCampoFormulario(seccionEspecificaciones, "Categoría", comboCategoria);
        agregarCampoFormulario(seccionEspecificaciones, "Ciudad / ubicación", comboCiudad);
        agregarSeccionAlFormulario(colDerecha, seccionEspecificaciones);

        JPanel seccionPublicacion = crearSeccionFormulario("02  ·  PUBLICACIÓN", 1);
        agregarCampoFormulario(seccionPublicacion, "Descripción breve", scrollDescripcion);
        JPanel estadoCampo = new JPanel(new BorderLayout());
        estadoCampo.setOpaque(false);
        estadoCampo.add(chkDisponible, BorderLayout.CENTER);
        agregarCampoFormulario(seccionPublicacion, "Estado", estadoCampo);
        agregarCampoFormulario(seccionPublicacion, "Fotografía del vehículo", crearPanelFoto());
        agregarSeccionAlFormulario(colIzquierda, seccionPublicacion);

        JPanel columnasFormulario = new JPanel(new GridLayout(1, 2, 20, 0));
        columnasFormulario.setOpaque(false);
        JPanel envIzq = new JPanel(new BorderLayout());
        envIzq.setOpaque(false);
        envIzq.add(colIzquierda, BorderLayout.NORTH);
        JPanel envDer = new JPanel(new BorderLayout());
        envDer.setOpaque(false);
        envDer.add(colDerecha, BorderLayout.NORTH);
        columnasFormulario.add(envIzq);
        columnasFormulario.add(envDer);
        columnasFormulario.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelFormulario.add(columnasFormulario);

        btnGuardar = new BotonRacing("Registrar vehículo", COLOR_ACENTO, COLOR_ACENTO_HOVER);
        configurarBoton(btnGuardar);
        btnGuardar.setPreferredSize(new Dimension(260, 46));
        btnModificar = new BotonRacing("Modificar seleccionado", TemaRentCar.c(45, 63, 82), TemaRentCar.c(61, 87, 112));
        configurarBoton(btnModificar);
        btnModificar.setPreferredSize(new Dimension(250, 42));
        btnEliminar = new BotonRacing("Eliminar seleccionado", COLOR_ELIMINAR, COLOR_ELIMINAR_HOVER);
        configurarBoton(btnEliminar);
        btnEliminar.setPreferredSize(new Dimension(240, 42));

        // ========================================================
        // PÁGINA DE LA TABLA (pantalla completa)
        // ========================================================

        JPanel panelDerecho = new JPanel(new BorderLayout(0, 15));
        panelDerecho.setOpaque(false);

        JPanel barraTabla = new JPanel(new BorderLayout(15, 0));
        barraTabla.setBackground(COLOR_PANEL);
        barraTabla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        btnVolverFormulario = new BotonRacing("\u2190  Volver al formulario", TemaRentCar.c(45, 63, 82), TemaRentCar.c(61, 87, 112));
        configurarBoton(btnVolverFormulario);
        btnVolverFormulario.setPreferredSize(new Dimension(250, 42));
        btnVolverFormulario.addActionListener(e -> mostrarFormulario());

        JPanel tituloTabla = new JPanel(new GridLayout(2, 1, 0, 2));
        tituloTabla.setOpaque(false);
        JLabel lblTituloTabla = new JLabel("Vehículos registrados");
        lblTituloTabla.setFont(orbitron(true, 17f));
        lblTituloTabla.setForeground(COLOR_TEXTO);
        lblContadorTabla = new JLabel("0 vehículos");
        lblContadorTabla.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblContadorTabla.setForeground(COLOR_GRIS);
        tituloTabla.add(lblTituloTabla);
        tituloTabla.add(lblContadorTabla);

        JPanel accionesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        accionesTabla.setOpaque(false);
        accionesTabla.add(btnModificar);
        accionesTabla.add(btnEliminar);

        JPanel izquierdaBarra = new JPanel(new BorderLayout(18, 0));
        izquierdaBarra.setOpaque(false);
        izquierdaBarra.add(btnVolverFormulario, BorderLayout.WEST);
        izquierdaBarra.add(tituloTabla, BorderLayout.CENTER);
        barraTabla.add(izquierdaBarra, BorderLayout.WEST);
        barraTabla.add(accionesTabla, BorderLayout.EAST);
        panelDerecho.add(barraTabla, BorderLayout.NORTH);


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
                orbitron(
                        true,
                        15f
                )
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



        // ========================================================
        // MODELO TABLA
        // ========================================================

        modeloTabla =
                new DefaultTableModel(
                        COLUMNAS_TABLA,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {

                        return false;
                    }

                    // Acepta filas con solo las 5 primeras columnas (versión anterior del presenter)
                    @Override
                    public void addRow(Object[] fila) {

                        if (fila != null && fila.length < getColumnCount()) {
                            fila = java.util.Arrays.copyOf(fila, getColumnCount());
                        }
                        super.addRow(fila);
                    }
                };

        modeloTabla.addTableModelListener(e -> actualizarContadorVehiculos());


        // ========================================================
        // TABLA
        // ========================================================

        tablaVehiculos =
                new JTable(
                        modeloTabla
                ) {
                    // Si la ventana es más ancha que la tabla, las columnas se estiran;
                    // si es más angosta, aparece la barra horizontal.
                    @Override
                    public boolean getScrollableTracksViewportWidth() {
                        return getParent() == null
                                || getPreferredSize().width <= getParent().getWidth();
                    }
                };
        tablaVehiculos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // MÁS ALTA PARA MOSTRAR FOTOS
        tablaVehiculos.setRowHeight(
                120
        );

        tablaVehiculos.setFont(
                orbitron(
                        false,
                        13f
                )
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
                COLOR_TEXTO
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

        tablaVehiculos.getTableHeader().setBackground(COLOR_ENCABEZADO);
        tablaVehiculos.getTableHeader().setForeground(COLOR_TEXTO);
        tablaVehiculos.getTableHeader().setOpaque(true);
        tablaVehiculos.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));

        tablaVehiculos
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                48
                        )
                );


        // ========================================================
        // RENDERIZADORES
        // ========================================================

        javax.swing.table.TableColumnModel columnas = tablaVehiculos.getColumnModel();
        columnas.getColumn(0).setCellRenderer(new FotoCeldaRenderer());
        columnas.getColumn(1).setCellRenderer(new PlacaCeldaRenderer());
        columnas.getColumn(2).setCellRenderer(new CeldaRenderer());
        columnas.getColumn(3).setCellRenderer(new CeldaRenderer());
        columnas.getColumn(4).setCellRenderer(new PrecioCeldaRenderer());
        for (int i = 5; i < COLUMNAS_TABLA.length; i++) {
            columnas.getColumn(i).setCellRenderer(new DatoCeldaRenderer());
        }

        int[] anchos = {130, 160, 130, 200, 170, 75, 65, 100, 125, 110, 100, 85, 115, 115, 195, 140};
        for (int i = 0; i < anchos.length; i++) {
            columnas.getColumn(i).setPreferredWidth(anchos[i]);
            columnas.getColumn(i).setMinWidth(Math.min(anchos[i], 60));
        }
        columnas.getColumn(0).setMinWidth(120);
        columnas.getColumn(1).setMinWidth(150);


        // ========================================================
        // SCROLLPANE
        // ========================================================

        JScrollPane scrollTabla =
                new JScrollPane(
                        tablaVehiculos,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                );
        scrollTabla.getVerticalScrollBar().setUnitIncrement(24);

        scrollTabla.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        scrollTabla.setBackground(COLOR_PANEL);
        scrollTabla.setOpaque(true);
        scrollTabla.getViewport().setOpaque(true);
        scrollTabla.getViewport().setBackground(COLOR_FILA);

        // Asegura que el encabezado de la tabla esté instalado antes de estilizarlo.
        // JScrollPane puede devolver null en getColumnHeader() si no se ha creado.
        scrollTabla.setColumnHeaderView(tablaVehiculos.getTableHeader());
        if (scrollTabla.getColumnHeader() != null) {
            scrollTabla.getColumnHeader().setOpaque(true);
            scrollTabla.getColumnHeader().setBackground(COLOR_ENCABEZADO);
        }
        tablaVehiculos.getTableHeader().setOpaque(true);
        tablaVehiculos.getTableHeader().setBackground(COLOR_ENCABEZADO);
        tablaVehiculos.getTableHeader().setForeground(COLOR_TEXTO);
        estilizarBarraDesplazamiento(scrollTabla);

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

        JScrollPane scrollFormulario = new JScrollPane(panelFormulario,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollFormulario.setBorder(null);
        scrollFormulario.getViewport().setBackground(COLOR_FONDO);
        scrollFormulario.getVerticalScrollBar().setUnitIncrement(18);
        estilizarBarraDesplazamiento(scrollFormulario);

        // Franja superior del formulario: la tarjeta de vehículos abre la tabla completa
        JPanel franjaTarjetas = new JPanel(new GridLayout(1, 2, 15, 0));
        franjaTarjetas.setOpaque(false);
        franjaTarjetas.add(crearTarjetaTabla());
        franjaTarjetas.add(crearTarjeta(
                "GESTIÓN",
                "Para modificar o eliminar, abre la tabla y selecciona un vehículo"));

        JPanel barraAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        barraAcciones.setBackground(COLOR_PANEL);
        barraAcciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        barraAcciones.add(btnGuardar);

        JPanel paginaFormulario = new JPanel(new BorderLayout(0, 15));
        paginaFormulario.setOpaque(false);
        paginaFormulario.add(franjaTarjetas, BorderLayout.NORTH);
        paginaFormulario.add(scrollFormulario, BorderLayout.CENTER);
        paginaFormulario.add(barraAcciones, BorderLayout.SOUTH);

        paginas = new CardLayout();
        contenedorPaginas = new JPanel(paginas);
        contenedorPaginas.setOpaque(false);
        contenedorPaginas.add(paginaFormulario, PAG_FORM);
        contenedorPaginas.add(panelDerecho, PAG_TABLA);
        paginas.show(contenedorPaginas, PAG_FORM);

        panelPrincipal.add(
                contenedorPaginas,
                BorderLayout.CENTER
        );

        add(
                panelPrincipal,
                BorderLayout.CENTER
        );

        actualizarContadorVehiculos();
    }


    // ============================================================
    // NAVEGACIÓN ENTRE PÁGINAS
    // ============================================================

    public void mostrarFormulario() {
        paginas.show(contenedorPaginas, PAG_FORM);
    }

    public void mostrarTabla() {
        paginas.show(contenedorPaginas, PAG_TABLA);
        tablaVehiculos.requestFocusInWindow();
    }

    private void actualizarContadorVehiculos() {
        int n = modeloTabla == null ? 0 : modeloTabla.getRowCount();
        String texto = n == 1 ? "1 vehículo registrado" : n + " vehículos registrados";
        if (lblContadorTabla != null) {
            lblContadorTabla.setText(texto);
        }
        if (lblTarjetaTabla != null) {
            lblTarjetaTabla.setText("<html>" + texto + "  ·  <font color='#E10600'>Clic para ver la tabla completa \u2192</font></html>");
        }
    }

    /** Tarjeta "Vehículos registrados": al hacer clic abre la tabla a pantalla completa. */
    private JPanel crearTarjetaTabla() {

        final JPanel tarjeta = new JPanel(new BorderLayout(0, 5));
        tarjeta.setBackground(COLOR_PANEL);
        final javax.swing.border.Border normal = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(12, 15, 12, 15));
        final javax.swing.border.Border resaltado = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO),
                BorderFactory.createEmptyBorder(12, 15, 12, 15));
        tarjeta.setBorder(normal);

        JLabel lblTitulo = new JLabel("VEHÍCULOS REGISTRADOS");
        lblTitulo.setFont(orbitron(true, 10f));
        lblTitulo.setForeground(COLOR_ACENTO);

        lblTarjetaTabla = new JLabel("0 vehículos registrados");
        lblTarjetaTabla.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTarjetaTabla.setForeground(COLOR_GRIS);

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblTarjetaTabla, BorderLayout.CENTER);

        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tarjeta.setToolTipText("Ver la tabla de vehículos a pantalla completa");
        MouseAdapter clic = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarTabla();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                tarjeta.setBorder(resaltado);
                tarjeta.setBackground(TemaRentCar.c(238, 241, 246));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                tarjeta.setBorder(normal);
                tarjeta.setBackground(COLOR_PANEL);
            }
        };
        tarjeta.addMouseListener(clic);
        lblTarjetaTabla.addMouseListener(clic);
        lblTitulo.addMouseListener(clic);
        return tarjeta;
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

                if (ruta.contains("rentcar_logo")) {
                    BufferedImage oscuro = TemaRentCar.logoOscuro();
                    if (oscuro != null) {
                        imagen = oscuro;
                    }
                }

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


    /**
     * Icono que se dibuja a la resolución real de la pantalla (también con
     * escala de Windows 125 %, 150 %...), por eso el logo no se ve pixelado.
     */
    private static class LogoIcon
            implements javax.swing.Icon {

        private final BufferedImage imagen;
        private final int altura;
        private final int ancho;

        private BufferedImage cache;
        private int cacheW = -1;
        private int cacheH = -1;

        public LogoIcon(
                BufferedImage imagen,
                int altura
        ) {

            this.imagen = imagen;
            this.altura = altura;

            this.ancho =
                    (int) Math.round(
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

            Graphics2D g2 = (Graphics2D) g.create();

            java.awt.geom.AffineTransform t = g2.getTransform();
            double sx = t.getScaleX() > 0 ? t.getScaleX() : 1;
            double sy = t.getScaleY() > 0 ? t.getScaleY() : 1;

            int pw = Math.max(1, (int) Math.round(ancho * sx));
            int ph = Math.max(1, (int) Math.round(altura * sy));

            if (cache == null || cacheW != pw || cacheH != ph) {
                cache = escalarSuave(imagen, pw, ph);
                cacheW = pw;
                cacheH = ph;
            }

            g2.scale(1.0 / sx, 1.0 / sy);
            g2.drawImage(cache, (int) Math.round(x * sx), (int) Math.round(y * sy), null);
            g2.dispose();
        }

        /** Reduce con calidad: mitad por mitad y bicúbica al final. */
        private static BufferedImage escalarSuave(BufferedImage origen, int anchoFinal, int altoFinal) {
            BufferedImage actual = origen;
            int w = origen.getWidth();
            int h = origen.getHeight();
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
                orbitron(
                        true,
                        23f
                )
        );

        campo.setForeground(
                COLOR_PLACA_TEXTO
        );

        campo.setBackground(
                COLOR_PLACA
        );
        // Deja ver el amarillo del panel de la placa; evita el rectángulo gris interno.
        campo.setOpaque(false);

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

                        if (
                                !Character.isLetterOrDigit(c)
                        ) {

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
            setOpaque(true);

            setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    TemaRentCar.c(190, 150, 0),
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
                            10
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
    // RENDER PLACA
    // ============================================================

    // @fijo-inicio
    private class PlacaCeldaRenderer
            extends JPanel
            implements javax.swing.table.TableCellRenderer {

        private String placa = "";
        private boolean moto = false;
        private boolean seleccionada = false;
        private int fila = 0;

        public PlacaCeldaRenderer() {
            setOpaque(false);
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
            placa = value == null ? "" : value.toString().trim().toUpperCase(Locale.ROOT);
            seleccionada = isSelected;
            fila = row;
            Object tipo = table.getModel().getValueAt(table.convertRowIndexToModel(row), 5);
            moto = tipo != null && "MOTO".equalsIgnoreCase(tipo.toString());
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Fondo de la fila
            g2.setColor(seleccionada ? COLOR_SELECCION : (fila % 2 == 0 ? COLOR_FILA : COLOR_FILA_ALT));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Tamaño de la placa: carro (rectangular) o moto (más cuadrada)
            int pw = moto ? 104 : 142;
            int ph = moto ? 74 : 70;
            pw = Math.min(pw, getWidth() - 16);
            int x = (getWidth() - pw) / 2;
            int y = (getHeight() - ph) / 2;
            int arco = 14;

            // Sombra
            g2.setColor(TemaRentCar.c(0, 0, 0, 90));
            g2.fillRoundRect(x + 2, y + 4, pw, ph, arco, arco);

            // Borde negro exterior
            g2.setColor(TemaRentCar.c(18, 18, 18));
            g2.fillRoundRect(x, y, pw, ph, arco, arco);

            // Lámina amarilla con degradado
            g2.setPaint(new java.awt.GradientPaint(
                    0, y, TemaRentCar.c(255, 221, 20),
                    0, y + ph, TemaRentCar.c(244, 186, 0)));
            g2.fillRoundRect(x + 3, y + 3, pw - 6, ph - 6, arco - 4, arco - 4);

            // Marco interior fino
            g2.setColor(TemaRentCar.c(18, 18, 18, 190));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(x + 7, y + 7, pw - 15, ph - 15, 8, 8);

            // Tornillos
            g2.setColor(TemaRentCar.c(60, 50, 10, 170));
            g2.fillOval(x + 11, y + ph / 2 - 3, 6, 6);
            g2.fillOval(x + pw - 17, y + ph / 2 - 3, 6, 6);

            // "COLOMBIA"
            g2.setColor(TemaRentCar.c(18, 18, 18));
            Font fPais = new Font("Arial", Font.BOLD, 9);
            g2.setFont(fPais);
            String pais = "C O L O M B I A";
            FontMetrics fmP = g2.getFontMetrics();
            g2.drawString(pais, x + (pw - fmP.stringWidth(pais)) / 2, y + 20);

            // Número de la placa, ajustado al ancho disponible
            int disponible = pw - 38;
            float tam = moto ? 20f : 25f;
            Font fPlaca = orbitron(true, tam);
            g2.setFont(fPlaca);
            FontMetrics fm = g2.getFontMetrics();
            while (fm.stringWidth(placa) > disponible && tam > 11f) {
                tam -= 1f;
                fPlaca = orbitron(true, tam);
                g2.setFont(fPlaca);
                fm = g2.getFontMetrics();
            }
            int tx = x + (pw - fm.stringWidth(placa)) / 2;
            int ty = y + 22 + (ph - 22 - 8 + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(placa, tx, ty);

            // Resaltado cuando la fila está seleccionada
            if (seleccionada) {
                g2.setColor(COLOR_ACENTO_HOVER);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(x - 3, y - 3, pw + 5, ph + 5, arco + 4, arco + 4);
            }

            g2.dispose();
        }
    }

    // @fijo-fin
    // ============================================================
    // DATOS GENERALES EN TABLA (tipo, año, color, etc.)
    // ============================================================

    private class DatoCeldaRenderer
            extends CeldaRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            String nombre = table.getColumnName(column);
            String texto;

            if (value == null || value.toString().trim().isEmpty()) {
                texto = "—";
            } else if (value instanceof Boolean) {
                texto = ((Boolean) value) ? "● Disponible" : "● No disponible";
            } else if (value instanceof Number) {
                long n = ((Number) value).longValue();
                if (n <= 0 && !nombre.equals("Kilometraje")) {
                    texto = "—";
                } else if (nombre.equals("Kilometraje")) {
                    texto = formatearMiles(n) + " km";
                } else {
                    texto = String.valueOf(n);
                }
            } else {
                texto = value.toString();
            }

            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, texto, isSelected, hasFocus, row, column);

            if (value instanceof Boolean && !isSelected) {
                label.setForeground(((Boolean) value) ? TemaRentCar.c(22, 150, 70) : COLOR_GRIS);
            } else if (nombre.equals("Tipo") && !isSelected) {
                label.setForeground("MOTO".equalsIgnoreCase(texto) ? TemaRentCar.c(200, 110, 0) : COLOR_TEXTO);
            }
            label.setToolTipText(texto);
            return label;
        }
    }


    // ============================================================
    // FOTO EN TABLA
    // ============================================================

    // ============================================================
    // MINIATURAS EN CACHÉ (tabla de vehículos)
    // ============================================================

    private final Map<String, ImageIcon> cacheMiniaturas = new HashMap<>();

    private ImageIcon obtenerMiniatura(File archivo, int maxAncho, int maxAlto) {

        String clave = archivo.getAbsolutePath()
                + "|" + archivo.lastModified()
                + "|" + archivo.length();

        if (cacheMiniaturas.containsKey(clave)) {
            return cacheMiniaturas.get(clave);
        }

        ImageIcon icono = null;

        try (javax.imageio.stream.ImageInputStream in = ImageIO.createImageInputStream(archivo)) {

            java.util.Iterator<javax.imageio.ImageReader> lectores = ImageIO.getImageReaders(in);

            if (lectores.hasNext()) {

                javax.imageio.ImageReader lector = lectores.next();

                try {
                    lector.setInput(in, true, true);

                    int w = lector.getWidth(0);
                    int h = lector.getHeight(0);

                    // Se lee solo una fracción de los píxeles: mucho más rápido con fotos grandes
                    javax.imageio.ImageReadParam parametros = lector.getDefaultReadParam();
                    int paso = Math.max(1, Math.min(w / (maxAncho * 2), h / (maxAlto * 2)));
                    parametros.setSourceSubsampling(paso, paso, 0, 0);

                    BufferedImage imagen = lector.read(0, parametros);

                    if (imagen != null) {

                        double escala = Math.min(
                                (double) maxAncho / imagen.getWidth(),
                                (double) maxAlto / imagen.getHeight());

                        int nuevoAncho = Math.max(1, (int) (imagen.getWidth() * escala));
                        int nuevoAlto = Math.max(1, (int) (imagen.getHeight() * escala));

                        icono = new ImageIcon(escalarImagen(imagen, nuevoAncho, nuevoAlto));
                    }

                } finally {
                    lector.dispose();
                }
            }

        } catch (Exception e) {
            icono = null;
        }

        cacheMiniaturas.put(clave, icono);
        return icono;
    }

    private class FotoCeldaRenderer
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

            label.setVerticalAlignment(
                    SwingConstants.CENTER
            );

            label.setFont(
                    orbitron(
                            true,
                            11f
                    )
            );

            label.setIcon(null);

            boolean fotoCargada = false;

            if (
                    value != null
                    && !value.toString()
                            .trim()
                            .isEmpty()
            ) {

                String ruta =
                        value.toString()
                                .trim();

                File archivo = FotoVehiculoUtil.resolver(ruta);

                if (archivo != null && archivo.exists()) {

                    try {

                        // Miniatura en caché: no se vuelve a leer ni a escalar la foto
                        // en cada repintado de la tabla.
                        ImageIcon icono = obtenerMiniatura(archivo, 112, 88);

                        if (icono != null) {

                            label.setIcon(icono);
                            label.setText("");
                            fotoCargada = true;
                        }

                    } catch (Exception e) {

                        fotoCargada = false;
                    }
                }
            }

            if (!fotoCargada) {

                label.setText(
                        "SIN FOTO"
                );

                label.setForeground(
                        COLOR_GRIS
                );
            }

            if (isSelected) {

                label.setBackground(
                        COLOR_SELECCION
                );

                if (!fotoCargada) {

                    label.setForeground(
                            COLOR_TEXTO
                    );
                }

            } else {

                label.setBackground(
                        row % 2 == 0
                                ? COLOR_FILA
                                : COLOR_FILA_ALT
                );
            }

            label.setOpaque(true);

            label.setBorder(
                    BorderFactory.createEmptyBorder(
                            4,
                            4,
                            4,
                            4
                    )
            );

            return label;
        }
    }


    // ============================================================
    // LOGOS DE MARCAS
    // ============================================================

    private LogoMarcaPanel logoMarca;

    private final Map<String, BufferedImage> logosMarca =
            new HashMap<>();

    private static final float RADIO_RECUADRO =
            0.136f;


    private BufferedImage obtenerLogoMarca(
            String marca
    ) {

        if (
                marca == null
                || marca.trim().isEmpty()
        ) {

            return null;
        }

        boolean moto = comboTipo != null && "MOTO".equalsIgnoreCase(getTipo());
        String base = marca.trim().toLowerCase(Locale.ROOT);
        String clave = (moto ? "moto:" : "auto:") + base;

        if (logosMarca.containsKey(clave)) {

            return logosMarca.get(clave);
        }

        // Algunos logos de motos tienen otro nombre de archivo
        java.util.List<String> nombres = new java.util.ArrayList<>();
        if (moto) {
            if (base.equals("honda")) nombres.add("hondamoto");
            if (base.equals("bmw motorrad")) nombres.add("bmw motorroad");
        }
        nombres.add(base.replace(" ", "-"));
        nombres.add(base.replace(" ", ""));
        nombres.add(base);

        BufferedImage logo = null;

        String[] extensiones = {
                "png",
                "jpg",
                "jpeg"
        };

        buscar:
        for (String nombre : nombres) {
            for (String ext : extensiones) {

                logo = leerLogoMarca("/images/marcas/" + nombre + "." + ext);

                if (logo != null) {

                    break buscar;
                }
            }
        }

        logosMarca.put(
                clave,
                logo
        );

        return logo;
    }


    private BufferedImage leerLogoMarca(
            String ruta
    ) {

        try (
                InputStream is =
                        getClass()
                                .getResourceAsStream(
                                        ruta
                                )
        ) {

            if (is == null) {

                return null;
            }

            BufferedImage original =
                    ImageIO.read(is);

            if (original == null) {

                return null;
            }

            return recortarRecuadro(
                    original
            );

        } catch (Exception e) {

            System.out.println(
                    "No se pudo cargar el logo: "
                    + ruta
            );

            return null;
        }
    }


    private BufferedImage recortarRecuadro(
            BufferedImage origen
    ) {

        int w =
                origen.getWidth();

        int h =
                origen.getHeight();

        int minX = w;
        int minY = h;
        int maxX = -1;
        int maxY = -1;

        for (
                int y = 0;
                y < h;
                y += 2
        ) {

            for (
                    int x = 0;
                    x < w;
                    x += 2
            ) {

                int rgb =
                        origen.getRGB(
                                x,
                                y
                        );

                if (
                        (rgb >>> 24)
                        < 200
                ) {

                    continue;
                }

                int r =
                        (rgb >> 16)
                        & 0xFF;

                int g =
                        (rgb >> 8)
                        & 0xFF;

                int b =
                        rgb & 0xFF;

                if (
                        Math.max(
                                r,
                                Math.max(g, b)
                        ) < 10
                ) {

                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }

        int cw =
                maxX - minX + 2;

        int ch =
                maxY - minY + 2;

        if (
                maxX < 0
                || cw < w / 10
                || ch < h / 10
        ) {

            return origen;
        }

        if (
                minX + cw > w
        ) {

            cw =
                    w - minX;
        }

        if (
                minY + ch > h
        ) {

            ch =
                    h - minY;
        }

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

        float arco =
                cw * RADIO_RECUADRO;

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

        g2.setComposite(
                AlphaComposite.SrcIn
        );

        g2.drawImage(
                origen,
                -minX,
                -minY,
                null
        );

        g2.dispose();

        return recorte;
    }


    private BufferedImage escalarImagen(
            BufferedImage origen,
            int anchoFinal,
            int altoFinal
    ) {

        BufferedImage actual =
                origen;

        int w =
                origen.getWidth();

        int h =
                origen.getHeight();

        while (
                w / 2 >= anchoFinal
                && h / 2 >= altoFinal
        ) {

            w /= 2;
            h /= 2;

            actual =
                    redimensionarImagen(
                            actual,
                            w,
                            h
                    );
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


    // ============================================================
    // LOGO MARCA
    // ============================================================

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

            int anchoDibujo =
                    lado;

            int altoDibujo =
                    lado;

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

            int x =
                    (getWidth() - anchoDibujo)
                    / 2;

            int y =
                    (getHeight() - altoDibujo)
                    / 2;

            float arco =
                    anchoDibujo
                    * RADIO_RECUADRO;

            if (logo != null) {

                Graphics2D gi =
                        (Graphics2D) g2.create();

                double sx =
                        gi.getTransform()
                                .getScaleX();

                double sy =
                        gi.getTransform()
                                .getScaleY();

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

                gi.translate(
                        x,
                        y
                );

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

                g2.setColor(
                        COLOR_ACENTO
                );

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

                RoundRectangle2D forma =
                        new RoundRectangle2D.Float(
                                x + 1,
                                y + 1,
                                anchoDibujo - 2,
                                altoDibujo - 2,
                                arco,
                                arco
                        );

                g2.setColor(
                        COLOR_CAMPO
                );

                g2.fill(forma);

                if (hayMarca) {

                    g2.setColor(
                            COLOR_ACENTO
                    );

                    g2.setStroke(
                            new BasicStroke(2f)
                    );

                } else {

                    g2.setColor(
                            COLOR_BORDE
                    );

                    g2.setStroke(
                            new BasicStroke(
                                    1.5f,
                                    BasicStroke.CAP_BUTT,
                                    BasicStroke.JOIN_MITER,
                                    10f,
                                    new float[]{
                                        4f,
                                        4f
                                    },
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
                                    .substring(
                                            0,
                                            3
                                    )
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

                g2.setColor(
                        COLOR_GRIS
                );

                FontMetrics fm =
                        g2.getFontMetrics();

                int tx =
                        x
                        + (
                                anchoDibujo
                                - fm.stringWidth(
                                        texto
                                )
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


    // ============================================================
    // PANEL MARCA
    // ============================================================

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

    /** Actualiza las marcas disponibles según el tipo de vehículo seleccionado. */
    private void actualizarMarcasPorTipo() {
        if (comboMarca == null || comboTipo == null) {
            return;
        }

        Object seleccionAnterior = comboMarca.getSelectedItem();
        boolean moto = "MOTO".equalsIgnoreCase(getTipo());
        String[] marcasMoto = {
            "Selecciona una marca...", "AKT", "Auteco", "Bajaj", "Benelli",
            "BMW Motorrad", "CFMoto", "Ducati", "Hero", "Honda", "Husqvarna",
            "Kawasaki", "KTM", "Kymco", "Royal Enfield", "Suzuki", "SYM",
            "TVS", "Triumph", "Victory", "Yamaha", "Otra"
        };
        String[] marcasAuto = {
            "Selecciona una marca...", "Audi", "BMW", "BYD", "Chery",
            "Chevrolet", "Citroën", "Fiat", "Ford", "Honda", "Hyundai",
            "Jeep", "Kia", "Mazda", "Mercedes-Benz", "Mitsubishi", "Nissan",
            "Peugeot", "Renault", "Subaru", "Suzuki", "Tesla", "Toyota",
            "Volkswagen", "Volvo", "Otra"
        };
        String[] marcas = moto ? marcasMoto : marcasAuto;

        comboMarca.removeAllItems();
        for (String marca : marcas) {
            comboMarca.addItem(marca);
        }

        if (seleccionAnterior != null) {
            for (String marca : marcas) {
                if (marca.equalsIgnoreCase(seleccionAnterior.toString())) {
                    comboMarca.setSelectedItem(marca);
                    break;
                }
            }
        }
        if (comboMarca.getSelectedIndex() < 0) {
            comboMarca.setSelectedIndex(0);
        }
        if (logoMarca != null) {
            logoMarca.setMarca(getMarca());
        }
    }

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
                new JComboBox<>(
                        marcas
                );

        combo.setFont(
                orbitron(
                        false,
                        11f
                )
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
                                    COLOR_TEXTO
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
                orbitron(
                        false,
                        11f
                )
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
                orbitron(
                        true,
                        18f
                )
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
                    orbitron(
                            true,
                            18f
                    )
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
                    orbitron(
                            true,
                            10f
                    )
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
    // PRECIO EN TABLA - CORREGIDO
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
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            long numero = 0;

            try {

                if (value instanceof Number) {

                    numero =
                            Math.round(
                                    (
                                            (Number) value
                                    ).doubleValue()
                            );

                } else if (value != null) {

                    numero =
                            Math.round(
                                    Double.parseDouble(
                                            value.toString()
                                                    .trim()
                                    )
                            );
                }

            } catch (Exception e) {

                numero = 0;
            }

            label.setText(
                    "$ "
                    + formatearMiles(numero)
                    + " / DÍA"
            );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setFont(
                    orbitron(
                            true,
                            14f
                    )
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
                        TemaRentCar.c(253, 238, 238)
                );
            }

            return label;
        }
    }


    // ============================================================
    // FORMATEAR MILES
    // ============================================================

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
                    orbitron(
                            true,
                            11f
                    )
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
                    TemaRentCar.c(255, 255, 255,
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
                    orbitron(
                            false,
                            14f
                    )
            );

            if (isSelected) {

                label.setBackground(
                        COLOR_SELECCION
                );

                label.setForeground(
                        COLOR_TEXTO
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
                    orbitron(
                            true,
                            12f
                    )
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
                orbitron(
                        true,
                        10f
                )
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
                orbitron(
                        true,
                        10f
                )
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


    private String[] obtenerLugaresColombia() {
        return new String[]{
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


    private JPanel crearSeccionFormulario(String titulo) {
        return crearSeccionFormulario(titulo, 2);
    }

    private JPanel crearSeccionFormulario(String titulo, int columnas) {
        JPanel seccion = new JPanel(new BorderLayout(0, 12));
        seccion.setBackground(COLOR_PANEL);
        seccion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        JLabel encabezado = new JLabel(titulo);
        encabezado.setForeground(COLOR_ACENTO_HOVER);
        encabezado.setFont(orbitron(true, 11f));
        seccion.add(encabezado, BorderLayout.NORTH);
        JPanel campos = new JPanel(new GridLayout(0, Math.max(1, columnas), 12, 12));
        campos.setBackground(COLOR_PANEL);
        seccion.add(campos, BorderLayout.CENTER);
        seccion.putClientProperty("rentcar.campos", campos);
        seccion.setAlignmentX(Component.LEFT_ALIGNMENT);
        seccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return seccion;
    }

    private void agregarCampoFormulario(JPanel seccion, String texto, Component componente) {
        JPanel campos = (JPanel) seccion.getClientProperty("rentcar.campos");
        JPanel campo = new JPanel(new BorderLayout(0, 6));
        campo.setOpaque(false);
        JLabel etiqueta = crearEtiqueta(texto);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 11));
        etiqueta.setForeground(COLOR_GRIS);
        campo.add(etiqueta, BorderLayout.NORTH);
        if (componente instanceof JTextField || componente instanceof JComboBox) {
            componente.setPreferredSize(new Dimension(190, 36));
        }
        campo.add(componente, BorderLayout.CENTER);
        campo.setMinimumSize(new Dimension(0, 62));
        campos.add(campo);
    }

    private void agregarSeccionAlFormulario(JPanel formulario, JPanel seccion) {
        formulario.add(seccion);
        formulario.add(javax.swing.Box.createVerticalStrut(12));
    }

    private void aplicarTemaMenus() {
        Color fondo = TemaRentCar.c(255, 255, 255);
        Color campo = TemaRentCar.c(238, 241, 246);
        Color texto = TemaRentCar.c(23, 27, 34);
        Color acento = TemaRentCar.c(225, 6, 0);
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

    private void estilizarBarraDesplazamiento(JScrollPane scroll) {
        java.awt.Color pista = TemaRentCar.c(255, 255, 255);
        java.awt.Color pulgar = TemaRentCar.c(183, 183, 187);
        java.awt.Color pulgarHover = new java.awt.Color(225, 6, 0);
        for (javax.swing.JScrollBar barra : new javax.swing.JScrollBar[]{
                scroll.getVerticalScrollBar(), scroll.getHorizontalScrollBar()}) {
            barra.setUnitIncrement(18);
            barra.setBackground(pista);
            barra.setForeground(pulgar);
            barra.setPreferredSize(new Dimension(12, 12));
            barra.setUI(new BasicScrollBarUI() {
                @Override protected void configureScrollBarColors() {
                    this.thumbColor = pulgar;
                    this.trackColor = pista;
                }
                @Override protected JButton createDecreaseButton(int orientation) {
                    return botonVacioBarra();
                }
                @Override protected JButton createIncreaseButton(int orientation) {
                    return botonVacioBarra();
                }
                @Override protected void paintThumb(Graphics g, javax.swing.JComponent c, java.awt.Rectangle r) {
                    if (r.isEmpty() || !scrollbar.isEnabled()) return;
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(isDragging ? pulgarHover : pulgar);
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

    private void estilizarComboDetalle(JComboBox<String> combo) {
        combo.setBackground(COLOR_CAMPO); combo.setForeground(COLOR_TEXTO);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        combo.setPreferredSize(new Dimension(180, 34));
    }

    public String getTipo(){ Object v = comboTipo.getSelectedItem(); return v == null ? "" : v.toString().trim(); }
    public int getAnio(){ return numero(txtAnio.getText(), 0); }
    public String getAnioTexto(){ return txtAnio.getText().trim(); }
    public String getColor(){ return txtColor.getText().trim(); }
    public String getTransmision(){ Object item = comboTransmision.getSelectedItem(); String v = item == null ? "" : item.toString(); return v.startsWith("No ")?"":v; }
    public String getCombustible(){ Object item = comboCombustible.getSelectedItem(); String v = item == null ? "" : item.toString(); return v.startsWith("No ")?"":v; }
    public int getCapacidad(){ return numero(txtCapacidad.getText(), 0); }
    public String getCapacidadTexto(){ return txtCapacidad.getText().trim(); }
    public int getKilometraje(){ return numero(txtKilometraje.getText(), 0); }
    public String getKilometrajeTexto(){ return txtKilometraje.getText().trim(); }
    public String getPuertasTexto(){ return txtPuertas.getText().trim(); }
    public int getPuertas(){ return getTipo().equalsIgnoreCase("MOTO") ? 0 : numero(txtPuertas.getText(), 0); }
    public String getCategoria(){ Object item = comboCategoria.getSelectedItem(); String v = item == null ? "" : item.toString(); return v.startsWith("No ")?"":v; }
    public String getCiudad(){ Object ciudad = comboCiudad.getSelectedItem(); return ciudad == null ? "" : ciudad.toString().trim(); }
    public String getDescripcion(){ return txtDescripcion.getText().trim(); }
    public boolean isDisponible(){ return chkDisponible.isSelected(); }
    private int numero(String s,int defecto){ try{return Integer.parseInt(s.trim());}catch(Exception ex){return defecto;} }

    /** Devuelve los campos obligatorios que faltan para impedir registros incompletos. */
    public String validarCamposObligatorios() {
        java.util.List<String> faltantes = new java.util.ArrayList<>();
        if (getPlaca().isBlank()) faltantes.add("placa");
        if (getMarca().isBlank()) faltantes.add("marca");
        if (getModelo().isBlank()) faltantes.add("modelo");
        if (txtPrecio.getText().trim().isBlank() || getPrecio() <= 0) faltantes.add("precio por día válido");
        if (getTipo().isBlank()) faltantes.add("tipo de vehículo");
        if (getAnioTexto().isBlank()) faltantes.add("año");
        if (getColor().isBlank()) faltantes.add("color");
        if (getTransmision().isBlank()) faltantes.add("transmisión");
        if (getCombustible().isBlank()) faltantes.add("combustible");
        if (getCapacidadTexto().isBlank()) faltantes.add("capacidad");
        if (!getTipo().equalsIgnoreCase("MOTO") && getPuertasTexto().isBlank()) faltantes.add("número de puertas");
        if (getKilometrajeTexto().isBlank()) faltantes.add("kilometraje");
        else if (!getKilometrajeTexto().matches("\\d+")) faltantes.add("kilometraje numérico");
        if (getCategoria().isBlank()) faltantes.add("categoría");
        if (getCiudad().isBlank()) faltantes.add("ciudad");
        if (getDescripcion().isBlank()) faltantes.add("descripción");
        if (getFoto().isBlank() || FotoVehiculoUtil.resolver(getFoto()) == null) faltantes.add("fotografía válida del vehículo");
        return String.join(", ", faltantes);
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


    public String getFoto() {

        return rutaFotoSeleccionada == null
                ? ""
                : rutaFotoSeleccionada;
    }


    public JButton getBtnSubirFoto() {

        return btnSubirFoto;
    }


    public JButton getBtnGuardar() {

        return btnGuardar;
    }


    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public JButton getBtnModificar() {
        return btnModificar;
    }

    public void setModoEdicion(boolean editando) {
        txtPlaca.setEditable(!editando);
        txtPlaca.setBackground(editando ? TemaRentCar.c(230, 234, 241) : COLOR_CAMPO);
        btnGuardar.setText(editando ? "Guardar modificación" : "Registrar vehículo");
        btnModificar.setText(editando ? "Editar otro" : "Modificar seleccionado");
    }

    public void cargarVehiculoEnFormulario(Vehiculo v) {
        if (v == null) return;
        mostrarFormulario();
        txtPlaca.setText(v.getPlaca());
        // Primero seleccionar el tipo para cargar la lista de marcas correspondiente.
        comboTipo.setSelectedItem(v.getTipo());
        comboMarca.setSelectedItem(v.getMarca());
        txtModelo.setText(v.getModelo());
        txtPrecio.setText(String.valueOf(v.getPrecioPorDia()));
        txtAnio.setText(v.getAnio() > 0 ? String.valueOf(v.getAnio()) : "");
        txtColor.setText(v.getColor());
        comboTransmision.setSelectedItem(v.getTransmision().isBlank() ? "No especificada" : v.getTransmision());
        comboCombustible.setSelectedItem(v.getCombustible().isBlank() ? "No especificado" : v.getCombustible());
        txtCapacidad.setText(String.valueOf(v.getCapacidad()));
        txtKilometraje.setText(String.valueOf(v.getKilometraje()));
        txtPuertas.setText(v.getTipo().equalsIgnoreCase("MOTO") ? "0" : String.valueOf(v.getPuertas()));
        comboCategoria.setSelectedItem(v.getCategoria().isBlank() ? "No especificada" : v.getCategoria());
        comboCiudad.setSelectedItem(v.getCiudad());
        txtDescripcion.setText(v.getDescripcion());
        chkDisponible.setSelected(v.isDisponible());
        rutaFotoSeleccionada = v.getFoto() == null ? "" : v.getFoto();
        if (lblFotoPreview != null) {
            lblFotoPreview.setIcon(null);
            if (!rutaFotoSeleccionada.isBlank()) {
                try {
                    File archivoFoto = FotoVehiculoUtil.resolver(rutaFotoSeleccionada);
                    BufferedImage imagen = archivoFoto == null ? null : leerFotoReducida(archivoFoto);
                    if (imagen != null) {
                        lblFotoPreview.setIcon(new ImageIcon(TemaRentCar.escalarImagen(imagen, 125, 78)));
                        lblFotoPreview.setText("");
                    } else {
                        lblFotoPreview.setText("FOTO GUARDADA");
                    }
                } catch (IOException ex) {
                    lblFotoPreview.setText("FOTO GUARDADA");
                }
            } else {
                lblFotoPreview.setText("SIN FOTO");
            }
        }
        setModoEdicion(true);
        txtModelo.requestFocusInWindow();
    }


    public DefaultTableModel getModeloTabla() {

        return modeloTabla;
    }


    // ============================================================
    // PLACA SELECCIONADA
    // IMPORTANTE: AHORA LA FOTO ES LA COLUMNA 0
    // ============================================================

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
                        1
                );

        return valor == null
                ? null
                : valor.toString();
    }


    // ============================================================
    // LIMPIAR CAMPOS
    // ============================================================

    public void limpiarCampos() {

        setModoEdicion(false);
        txtPlaca.setText("");

        comboTipo.setSelectedItem("AUTO");
        actualizarMarcasPorTipo();
        comboMarca.setSelectedIndex(0);

        txtModelo.setText("");

        txtPrecio.setText("");
        comboTipo.setSelectedIndex(-1); txtAnio.setText(""); txtColor.setText("");
        comboTransmision.setSelectedIndex(-1); comboCombustible.setSelectedIndex(-1);
        txtCapacidad.setText(""); txtKilometraje.setText(""); txtPuertas.setText(""); txtPuertas.setEnabled(true); comboCategoria.setSelectedIndex(-1);
        comboCiudad.setSelectedIndex(-1); txtDescripcion.setText(""); chkDisponible.setSelected(true);

        rutaFotoSeleccionada = "";

        if (lblFotoPreview != null) {

            lblFotoPreview.setIcon(null);

            lblFotoPreview.setText(
                    "SIN FOTO"
            );
        }

        txtPlaca.requestFocus();
    }
}