package com.mycompany.rentcar.view;

import com.mycompany.rentcar.presenter.MarcaItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Locale;

public class MainPublicView extends JFrame {

    private JComboBox<MarcaItem> comboMarcas;
    private JTable tablaVehiculos;
    private DefaultTableModel tableModel;
    private JButton btnLogin;
    private JButton btnVerTodos;
    private JLabel lblBannerMarca;

    private Font orbitronRegular;
    private Font orbitronBold;

    // =========================================================
    // COLORES RENTCAR
    // =========================================================
    private static final Color COLOR_FONDO
            = new Color(18, 18, 22);

    private static final Color COLOR_ENCABEZADO
            = new Color(22, 22, 27);

    private static final Color COLOR_PANEL
            = new Color(27, 27, 32);

    private static final Color COLOR_CAMPO
            = new Color(35, 35, 41);

    private static final Color COLOR_BORDE
            = new Color(58, 58, 66);

    private static final Color COLOR_TEXTO
            = new Color(245, 245, 245);

    private static final Color COLOR_GRIS
            = new Color(160, 160, 168);

    private static final Color COLOR_ACENTO
            = new Color(220, 38, 38);

    private static final Color COLOR_ACENTO_HOVER
            = new Color(239, 68, 68);

    private static final Color COLOR_FILA
            = new Color(27, 27, 32);

    private static final Color COLOR_FILA_ALT
            = new Color(32, 32, 38);

    private static final Color COLOR_SELECCION
            = new Color(75, 28, 30);

    private static final Color COLOR_PLACA
            = new Color(246, 205, 34);

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public MainPublicView() {

        cargarFuentes();

        inicializarVentana();

        crearInterfaz();
    }

    // =========================================================
    // CONFIGURACIÓN DE LA VENTANA
    // =========================================================
    private void inicializarVentana() {

        setTitle("RentCar - Catálogo de Vehículos");

        setSize(1050, 680);

        setTitle("RentCar - Catálogo de Vehículos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

// Pantalla maximizada
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setMinimumSize(new Dimension(1100, 700));

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        getContentPane().setBackground(
                COLOR_FONDO
        );

        setLayout(
                new BorderLayout()
        );
    }

    // =========================================================
    // INTERFAZ PRINCIPAL
    // =========================================================
    private void crearInterfaz() {

        JPanel principal
                = new JPanel(new BorderLayout());

        principal.setBackground(
                COLOR_FONDO
        );

        // -----------------------------------------------------
        // ENCABEZADO
        // -----------------------------------------------------
        principal.add(
                crearEncabezado(),
                BorderLayout.NORTH
        );

        // -----------------------------------------------------
        // CONTENIDO
        // -----------------------------------------------------
        principal.add(
                crearContenido(),
                BorderLayout.CENTER
        );

        add(principal);
    }

    // =========================================================
    // ENCABEZADO
    // =========================================================
    private JPanel crearEncabezado() {

        JPanel header
                = new JPanel(new BorderLayout());

        header.setBackground(
                COLOR_ENCABEZADO
        );

        header.setBorder(
                new EmptyBorder(
                        18,
                        28,
                        18,
                        28
                )
        );

        // -----------------------------------------------------
        // PARTE IZQUIERDA
        // -----------------------------------------------------
        JPanel izquierda
                = new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                15,
                                0
                        )
                );

        izquierda.setOpaque(false);

        JLabel logo
                = crearLogo(150);

        izquierda.add(logo);

        JPanel textos
                = new JPanel();

        textos.setOpaque(false);

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo
                = new JLabel(
                        "CATÁLOGO DE VEHÍCULOS"
                );

        titulo.setFont(
                orbitronBold.deriveFont(17f)
        );

        titulo.setForeground(
                Color.WHITE
        );

        JLabel subtitulo
                = new JLabel(
                        "Encuentra el vehículo ideal para tu próximo viaje"
                );

        subtitulo.setFont(
                orbitronRegular.deriveFont(9f)
        );

        subtitulo.setForeground(
                COLOR_GRIS
        );

        textos.add(titulo);

        textos.add(
                Box.createVerticalStrut(5)
        );

        textos.add(subtitulo);

        izquierda.add(textos);

        header.add(
                izquierda,
                BorderLayout.WEST
        );

        // -----------------------------------------------------
        // BOTÓN LOGIN
        // -----------------------------------------------------
        btnLogin
                = crearBotonPrincipal(
                        "INICIAR SESIÓN"
                );

        btnLogin.setPreferredSize(
                new Dimension(
                        190,
                        42
                )
        );

        header.add(
                btnLogin,
                BorderLayout.EAST
        );

        return header;
    }

    // =========================================================
    // CONTENIDO
    // =========================================================
    private JPanel crearContenido() {

        JPanel contenido
                = new JPanel(
                        new BorderLayout(
                                0,
                                18
                        )
                );

        contenido.setBackground(
                COLOR_FONDO
        );

        contenido.setBorder(
                new EmptyBorder(
                        22,
                        28,
                        28,
                        28
                )
        );

        // -----------------------------------------------------
        // BARRA DE FILTROS
        // -----------------------------------------------------
        contenido.add(
                crearPanelFiltros(),
                BorderLayout.NORTH
        );

        // -----------------------------------------------------
        // TABLA
        // -----------------------------------------------------
        contenido.add(
                crearPanelTabla(),
                BorderLayout.CENTER
        );

        return contenido;
    }

    // =========================================================
    // FILTROS
    // =========================================================
    private JPanel crearPanelFiltros() {

        JPanel panel
                = new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                COLOR_PANEL
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE,
                                1
                        ),
                        new EmptyBorder(
                                15,
                                18,
                                15,
                                18
                        )
                )
        );

        // -----------------------------------------------------
        // IZQUIERDA
        // -----------------------------------------------------
        JPanel izquierda
                = new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                12,
                                0
                        )
                );

        izquierda.setOpaque(false);

        JLabel etiqueta
                = new JLabel(
                        "FILTRAR POR MARCA"
                );

        etiqueta.setFont(
                orbitronBold.deriveFont(10f)
        );

        etiqueta.setForeground(
                COLOR_GRIS
        );

        izquierda.add(etiqueta);

        // -----------------------------------------------------
        // COMBO
        // -----------------------------------------------------
        comboMarcas
                = new JComboBox<>();

        comboMarcas.setPreferredSize(
                new Dimension(
                        220,
                        38
                )
        );

        comboMarcas.setFont(
                orbitronRegular.deriveFont(10f)
        );

        comboMarcas.setForeground(
                COLOR_TEXTO
        );

        comboMarcas.setBackground(
                COLOR_CAMPO
        );

        comboMarcas.setFocusable(false);

        comboMarcas.setRenderer(
                new MarcaComboRenderer()
        );

        izquierda.add(comboMarcas);

        // -----------------------------------------------------
        // BOTÓN TODOS
        // -----------------------------------------------------
        btnVerTodos
                = crearBotonSecundario(
                        "MOSTRAR TODOS"
                );

        btnVerTodos.setPreferredSize(
                new Dimension(
                        150,
                        38
                )
        );

        izquierda.add(btnVerTodos);

        panel.add(
                izquierda,
                BorderLayout.WEST
        );

        // -----------------------------------------------------
        // BANNER DE MARCA
        // -----------------------------------------------------
        lblBannerMarca
                = new JLabel(
                        "",
                        SwingConstants.CENTER
                );

        lblBannerMarca.setPreferredSize(
                new Dimension(
                        180,
                        50
                )
        );

        lblBannerMarca.setFont(
                orbitronBold.deriveFont(12f)
        );

        lblBannerMarca.setForeground(
                COLOR_ACENTO
        );

        panel.add(
                lblBannerMarca,
                BorderLayout.EAST
        );

        return panel;
    }

    // =========================================================
    // PANEL DE TABLA
    // =========================================================
    private JPanel crearPanelTabla() {

        JPanel panel
                = new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                COLOR_PANEL
        );

        panel.setBorder(
                BorderFactory.createLineBorder(
                        COLOR_BORDE,
                        1
                )
        );

        // -----------------------------------------------------
        // TÍTULO
        // -----------------------------------------------------
        JPanel tituloPanel
                = new JPanel(
                        new BorderLayout()
                );

        tituloPanel.setBackground(
                COLOR_PANEL
        );

        tituloPanel.setBorder(
                new EmptyBorder(
                        15,
                        18,
                        12,
                        18
                )
        );

        JLabel titulo
                = new JLabel(
                        "VEHÍCULOS DISPONIBLES"
                );

        titulo.setFont(
                orbitronBold.deriveFont(13f)
        );

        titulo.setForeground(
                Color.WHITE
        );

        tituloPanel.add(
                titulo,
                BorderLayout.WEST
        );

        panel.add(
                tituloPanel,
                BorderLayout.NORTH
        );

        // -----------------------------------------------------
        // MODELO DE TABLA
        // -----------------------------------------------------
        String[] columnas = {
            "PLACA",
            "MARCA",
            "MODELO",
            "PRECIO / DÍA"
        };

        tableModel
                = new DefaultTableModel(
                        columnas,
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

        tablaVehiculos
                = new JTable(tableModel);

        configurarTabla();

        // -----------------------------------------------------
        // SCROLL SIN BARRAS VISIBLES
        // -----------------------------------------------------
        JScrollPane scrollTable
                = new JScrollPane(
                        tablaVehiculos,
                        JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                );

        scrollTable.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scrollTable.getViewport()
                .setBackground(
                        COLOR_PANEL
                );

        panel.add(
                scrollTable,
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // CONFIGURACIÓN TABLA
    // =========================================================
    private void configurarTabla() {

        tablaVehiculos.setBackground(
                COLOR_FILA
        );

        tablaVehiculos.setForeground(
                COLOR_TEXTO
        );

        tablaVehiculos.setSelectionBackground(
                COLOR_SELECCION
        );

        tablaVehiculos.setSelectionForeground(
                Color.WHITE
        );

        tablaVehiculos.setRowHeight(48);

        tablaVehiculos.setFont(
                orbitronRegular.deriveFont(10f)
        );

        tablaVehiculos.setShowGrid(false);

        tablaVehiculos.setIntercellSpacing(
                new Dimension(0, 1)
        );

        tablaVehiculos.setFillsViewportHeight(
                true
        );

        tablaVehiculos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        // -----------------------------------------------------
        // CABECERA
        // -----------------------------------------------------
        JTableHeader header
                = tablaVehiculos.getTableHeader();

        header.setBackground(
                new Color(38, 38, 44)
        );

        header.setForeground(
                COLOR_GRIS
        );

        header.setFont(
                orbitronBold.deriveFont(10f)
        );

        header.setPreferredSize(
                new Dimension(
                        header.getWidth(),
                        42
                )
        );

        header.setReorderingAllowed(false);

        // -----------------------------------------------------
        // RENDERERS
        // -----------------------------------------------------
        tablaVehiculos
                .getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        new PlacaRenderer()
                );

        tablaVehiculos
                .getColumnModel()
                .getColumn(1)
                .setCellRenderer(
                        new MarcaRenderer()
                );

        tablaVehiculos
                .getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        new TextoRenderer()
                );

        tablaVehiculos
                .getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        new PrecioRenderer()
                );

        // -----------------------------------------------------
        // ANCHOS
        // -----------------------------------------------------
        tablaVehiculos
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(130);

        tablaVehiculos
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(190);

        tablaVehiculos
                .getColumnModel()
                .getColumn(2)
                .setPreferredWidth(220);

        tablaVehiculos
                .getColumnModel()
                .getColumn(3)
                .setPreferredWidth(180);
    }

    // =========================================================
    // LOGO
    // =========================================================
    private JLabel crearLogo(int ancho) {

        JLabel label
                = new JLabel();

        try {

            ImageIcon icono
                    = new ImageIcon(
                            getClass().getResource(
                                    "/images/rentcar_logo.png"
                            )
                    );

            Image imagen
                    = icono.getImage();

            int alto
                    = (int) (((double) icono.getIconHeight()
                    / icono.getIconWidth())
                    * ancho);

            imagen
                    = imagen.getScaledInstance(
                            ancho,
                            alto,
                            Image.SCALE_SMOOTH
                    );

            label.setIcon(
                    new ImageIcon(imagen)
            );

        } catch (Exception e) {

            label.setText("RENTCAR");

            label.setFont(
                    orbitronBold.deriveFont(22f)
            );

            label.setForeground(
                    Color.WHITE
            );
        }

        return label;
    }

    // =========================================================
    // BOTÓN PRINCIPAL
    // =========================================================
    private JButton crearBotonPrincipal(
            String texto
    ) {

        JButton boton
                = new JButton(texto);

        boton.setFont(
                orbitronBold.deriveFont(10f)
        );

        boton.setForeground(
                Color.WHITE
        );

        boton.setBackground(
                COLOR_ACENTO
        );

        boton.setFocusPainted(false);

        boton.setBorderPainted(false);

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.addMouseListener(
                new MouseAdapter() {

            @Override
            public void mouseEntered(
                    MouseEvent e
            ) {

                boton.setBackground(
                        COLOR_ACENTO_HOVER
                );
            }

            @Override
            public void mouseExited(
                    MouseEvent e
            ) {

                boton.setBackground(
                        COLOR_ACENTO
                );
            }
        }
        );

        return boton;
    }

    // =========================================================
    // BOTÓN SECUNDARIO
    // =========================================================
    private JButton crearBotonSecundario(
            String texto
    ) {

        JButton boton
                = new JButton(texto);

        boton.setFont(
                orbitronBold.deriveFont(9f)
        );

        boton.setForeground(
                COLOR_ACENTO
        );

        boton.setBackground(
                COLOR_PANEL
        );

        boton.setFocusPainted(false);

        boton.setBorder(
                BorderFactory.createLineBorder(
                        COLOR_ACENTO,
                        1
                )
        );

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.addMouseListener(
                new MouseAdapter() {

            @Override
            public void mouseEntered(
                    MouseEvent e
            ) {

                boton.setBackground(
                        new Color(
                                45,
                                30,
                                30
                        )
                );

                boton.setForeground(
                        COLOR_ACENTO_HOVER
                );
            }

            @Override
            public void mouseExited(
                    MouseEvent e
            ) {

                boton.setBackground(
                        COLOR_PANEL
                );

                boton.setForeground(
                        COLOR_ACENTO
                );
            }
        }
        );

        return boton;
    }

    // =========================================================
    // RENDERER COMBO MARCAS
    // =========================================================
    private class MarcaComboRenderer
            extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {

            JLabel label
                    = (JLabel) super.getListCellRendererComponent(
                            list,
                            value,
                            index,
                            isSelected,
                            cellHasFocus
                    );

            label.setFont(
                    orbitronRegular.deriveFont(10f)
            );

            label.setBorder(
                    new EmptyBorder(
                            8,
                            10,
                            8,
                            10
                    )
            );

            if (isSelected) {

                label.setBackground(
                        COLOR_ACENTO
                );

                label.setForeground(
                        Color.WHITE
                );

            } else {

                label.setBackground(
                        COLOR_CAMPO
                );

                label.setForeground(
                        COLOR_TEXTO
                );
            }

            return label;
        }
    }

    // =========================================================
    // RENDERER PLACA
    // =========================================================
    private class PlacaRenderer
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

            JLabel label
                    = new JLabel();

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setFont(
                    orbitronBold.deriveFont(10f)
            );

            label.setOpaque(true);

            label.setBorder(
                    BorderFactory.createLineBorder(
                            new Color(
                                    190,
                                    160,
                                    20
                            ),
                            1
                    )
            );

            label.setText(
                    value == null
                            ? ""
                            : value.toString()
            );

            label.setBackground(
                    COLOR_PLACA
            );

            label.setForeground(
                    Color.BLACK
            );

            return label;
        }
    }

    // =========================================================
    // RENDERER MARCA
    // =========================================================
    private class MarcaRenderer
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

            JLabel label
                    = (JLabel) super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            label.setFont(
                    orbitronBold.deriveFont(10f)
            );

            label.setForeground(
                    isSelected
                            ? Color.WHITE
                            : COLOR_ACENTO
            );

            label.setBorder(
                    new EmptyBorder(
                            0,
                            10,
                            0,
                            10
                    )
            );

            return label;
        }
    }

    // =========================================================
    // RENDERER TEXTO
    // =========================================================
    private class TextoRenderer
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

            JLabel label
                    = (JLabel) super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            label.setFont(
                    orbitronRegular.deriveFont(10f)
            );

            label.setForeground(
                    isSelected
                            ? Color.WHITE
                            : COLOR_TEXTO
            );

            label.setBorder(
                    new EmptyBorder(
                            0,
                            10,
                            0,
                            10
                    )
            );

            return label;
        }
    }

    // =========================================================
    // RENDERER PRECIO
    // =========================================================
    private class PrecioRenderer
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

            JLabel label
                    = (JLabel) super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            label.setFont(
                    orbitronBold.deriveFont(11f)
            );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setForeground(
                    isSelected
                            ? Color.WHITE
                            : COLOR_ACENTO
            );

            label.setBorder(
                    new EmptyBorder(
                            0,
                            8,
                            0,
                            8
                    )
            );

            if (value instanceof Number) {

                double precio
                        = ((Number) value)
                                .doubleValue();

                NumberFormat formato
                        = NumberFormat.getNumberInstance(
                                new Locale(
                                        "es",
                                        "CO"
                                )
                        );

                formato.setMaximumFractionDigits(0);

                label.setText(
                        "$ "
                        + formato.format(precio)
                );
            }

            return label;
        }
    }

    // =========================================================
    // FUENTES ORBITRON
    // =========================================================
    private void cargarFuentes() {

        try {

            InputStream regularStream
                    = getClass().getResourceAsStream(
                            "/fonts/Orbitron-Regular.ttf"
                    );

            InputStream boldStream
                    = getClass().getResourceAsStream(
                            "/fonts/Orbitron-Bold.ttf"
                    );

            if (regularStream != null) {

                orbitronRegular
                        = Font.createFont(
                                Font.TRUETYPE_FONT,
                                regularStream
                        );
            }

            if (boldStream != null) {

                orbitronBold
                        = Font.createFont(
                                Font.TRUETYPE_FONT,
                                boldStream
                        );
            }

        } catch (Exception e) {

            orbitronRegular
                    = new Font(
                            "SansSerif",
                            Font.PLAIN,
                            12
                    );

            orbitronBold
                    = new Font(
                            "SansSerif",
                            Font.BOLD,
                            12
                    );
        }

        if (orbitronRegular == null) {

            orbitronRegular
                    = new Font(
                            "SansSerif",
                            Font.PLAIN,
                            12
                    );
        }

        if (orbitronBold == null) {

            orbitronBold
                    = new Font(
                            "SansSerif",
                            Font.BOLD,
                            12
                    );
        }
    }

    // =========================================================
    // GETTERS PARA EL PRESENTER
    // =========================================================
    public JComboBox<MarcaItem> getComboMarcas() {

        return comboMarcas;
    }

    public JTable getTablaVehiculos() {

        return tablaVehiculos;
    }

    public DefaultTableModel getTableModel() {

        return tableModel;
    }

    public JButton getBtnLogin() {

        return btnLogin;
    }

    public JButton getBtnVerTodos() {

        return btnVerTodos;
    }

    public JLabel getLblBannerMarca() {

        return lblBannerMarca;
    }
}
