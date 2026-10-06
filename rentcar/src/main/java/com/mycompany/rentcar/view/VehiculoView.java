package com.mycompany.rentcar.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class VehiculoView extends JFrame {

    // ==============================
    // CAMPOS DEL FORMULARIO
    // ==============================

    private JTextField txtPlaca;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtPrecio;

    // ==============================
    // BOTONES
    // ==============================

    private JButton btnGuardar;
    private JButton btnEliminar;

    // ==============================
    // TABLA
    // ==============================

    private JTable tablaVehiculos;
    private DefaultTableModel modeloTabla;

    // ==============================
    // COLORES
    // ==============================

    private final Color COLOR_PRINCIPAL = new Color(31, 41, 55);
    private final Color COLOR_SECUNDARIO = new Color(55, 65, 81);
    private final Color COLOR_ACENTO = new Color(37, 99, 235);
    private final Color COLOR_ELIMINAR = new Color(220, 38, 38);
    private final Color COLOR_FONDO = new Color(243, 244, 246);
    private final Color COLOR_BLANCO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(31, 41, 55);
    private final Color COLOR_GRIS = new Color(107, 114, 128);

    // ==============================
    // CONSTRUCTOR
    // ==============================

    public VehiculoView() {
        initComponents();
        setLocationRelativeTo(null);
    }

    // ==============================
    // INICIALIZAR INTERFAZ
    // ==============================

    private void initComponents() {

        setTitle("RentCar - Gestión de Vehículos");
        setSize(1100, 650);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Layout principal
        setLayout(new BorderLayout());

        // Fondo general
        getContentPane().setBackground(COLOR_FONDO);

        // ==========================================
        // ENCABEZADO
        // ==========================================

        JPanel panelEncabezado = new JPanel(new BorderLayout());
        panelEncabezado.setBackground(COLOR_PRINCIPAL);
        panelEncabezado.setBorder(
                BorderFactory.createEmptyBorder(18, 25, 18, 25)
        );

        // Título
        JLabel lblTitulo = new JLabel("RENTCAR");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));

        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Gestión de vehículos y alquileres");
        lblSubtitulo.setForeground(new Color(209, 213, 219));
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel panelTitulos = new JPanel();
        panelTitulos.setLayout(new GridLayout(2, 1));
        panelTitulos.setOpaque(false);

        panelTitulos.add(lblTitulo);
        panelTitulos.add(lblSubtitulo);

        panelEncabezado.add(panelTitulos, BorderLayout.WEST);

        // Texto derecho
        JLabel lblModulo = new JLabel("MÓDULO DE VEHÍCULOS");
        lblModulo.setForeground(new Color(219, 234, 254));
        lblModulo.setFont(new Font("Arial", Font.BOLD, 13));

        panelEncabezado.add(lblModulo, BorderLayout.EAST);

        add(panelEncabezado, BorderLayout.NORTH);

        // ==========================================
        // CONTENEDOR PRINCIPAL
        // ==========================================

        JPanel panelPrincipal = new JPanel(new BorderLayout(20, 20));
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // ==========================================
        // PANEL IZQUIERDO - FORMULARIO
        // ==========================================

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(COLOR_BLANCO);
        panelFormulario.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(229, 231, 235)
                        ),
                        BorderFactory.createEmptyBorder(
                                20, 20, 20, 20
                        )
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Título formulario
        JLabel lblFormulario = new JLabel("Registrar vehículo");
        lblFormulario.setFont(
                new Font("Arial", Font.BOLD, 20)
        );
        lblFormulario.setForeground(COLOR_TEXTO);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 5, 20, 5);

        panelFormulario.add(lblFormulario, gbc);

        // Descripción
        JLabel lblDescripcion = new JLabel(
                "<html>Ingresa los datos del vehículo<br>que deseas registrar.</html>"
        );

        lblDescripcion.setFont(
                new Font("Arial", Font.PLAIN, 12)
        );
        lblDescripcion.setForeground(COLOR_GRIS);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 15, 5);

        panelFormulario.add(lblDescripcion, gbc);

        // ==========================================
        // CAMPO PLACA
        // ==========================================

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.insets = new Insets(7, 5, 5, 5);

        panelFormulario.add(crearEtiqueta("Placa"), gbc);

        txtPlaca = crearCampoTexto();

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        panelFormulario.add(txtPlaca, gbc);

        // ==========================================
        // CAMPO MARCA
        // ==========================================

        gbc.gridy = 4;
        gbc.gridwidth = 1;

        panelFormulario.add(crearEtiqueta("Marca"), gbc);

        txtMarca = crearCampoTexto();

        gbc.gridy = 5;
        gbc.gridwidth = 2;

        panelFormulario.add(txtMarca, gbc);

        // ==========================================
        // CAMPO MODELO
        // ==========================================

        gbc.gridy = 6;
        gbc.gridwidth = 1;

        panelFormulario.add(crearEtiqueta("Modelo"), gbc);

        txtModelo = crearCampoTexto();

        gbc.gridy = 7;
        gbc.gridwidth = 2;

        panelFormulario.add(txtModelo, gbc);

        // ==========================================
        // CAMPO PRECIO
        // ==========================================

        gbc.gridy = 8;
        gbc.gridwidth = 1;

        panelFormulario.add(crearEtiqueta("Precio por día"), gbc);

        txtPrecio = crearCampoTexto();

        gbc.gridy = 9;
        gbc.gridwidth = 2;

        panelFormulario.add(txtPrecio, gbc);

        // ==========================================
        // BOTÓN GUARDAR
        // ==========================================

        btnGuardar = new JButton("Registrar vehículo");

        configurarBoton(
                btnGuardar,
                COLOR_ACENTO
        );

        gbc.gridy = 10;
        gbc.insets = new Insets(20, 5, 8, 5);

        panelFormulario.add(btnGuardar, gbc);

        // ==========================================
        // BOTÓN ELIMINAR
        // ==========================================

        btnEliminar = new JButton("Eliminar seleccionado");

        configurarBoton(
                btnEliminar,
                COLOR_ELIMINAR
        );

        gbc.gridy = 11;
        gbc.insets = new Insets(5, 5, 5, 5);

        panelFormulario.add(btnEliminar, gbc);

        // ==========================================
        // ESPACIO INFERIOR
        // ==========================================

        gbc.gridy = 12;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        panelFormulario.add(
                new JPanel() {{
                    setOpaque(false);
                }},
                gbc
        );

        // ==========================================
        // PANEL DERECHO
        // ==========================================

        JPanel panelDerecho = new JPanel(new BorderLayout(0, 15));
        panelDerecho.setOpaque(false);

        // ==========================================
        // TARJETAS DE INFORMACIÓN
        // ==========================================

        JPanel panelTarjetas = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        panelTarjetas.setOpaque(false);

        JPanel tarjetaTotal = crearTarjeta(
                "VEHÍCULOS REGISTRADOS",
                "Consulta la lista de vehículos disponibles"
        );

        JPanel tarjetaInfo = crearTarjeta(
                "GESTIÓN",
                "Selecciona un vehículo para eliminarlo"
        );

        panelTarjetas.add(tarjetaTotal);
        panelTarjetas.add(tarjetaInfo);

        panelDerecho.add(
                panelTarjetas,
                BorderLayout.NORTH
        );

        // ==========================================
        // PANEL DE TABLA
        // ==========================================

        JPanel panelTabla = new JPanel(new BorderLayout());

        panelTabla.setBackground(COLOR_BLANCO);

        panelTabla.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(229, 231, 235)
                        ),
                        BorderFactory.createEmptyBorder(
                                15, 15, 15, 15
                        )
                )
        );

        // Título tabla
        JLabel lblLista = new JLabel(
                "Vehículos registrados"
        );

        lblLista.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        lblLista.setForeground(COLOR_TEXTO);

        lblLista.setBorder(
                BorderFactory.createEmptyBorder(
                        0, 0, 12, 0
                )
        );

        panelTabla.add(
                lblLista,
                BorderLayout.NORTH
        );

        // ==========================================
        // MODELO DE TABLA
        // ==========================================

        modeloTabla = new DefaultTableModel(
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

        tablaVehiculos = new JTable(modeloTabla);

        // Configuración visual
        tablaVehiculos.setRowHeight(38);

        tablaVehiculos.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        tablaVehiculos.setForeground(COLOR_TEXTO);

        tablaVehiculos.setBackground(Color.WHITE);

        tablaVehiculos.setSelectionBackground(
                new Color(219, 234, 254)
        );

        tablaVehiculos.setSelectionForeground(
                COLOR_TEXTO
        );

        tablaVehiculos.setShowVerticalLines(false);

        tablaVehiculos.setShowHorizontalLines(
                true
        );

        tablaVehiculos.setGridColor(
                new Color(229, 231, 235)
        );

        tablaVehiculos.setFillsViewportHeight(true);

        // ==========================================
        // CABECERA TABLA
        // ==========================================

        tablaVehiculos.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        tablaVehiculos.getTableHeader().setForeground(
                Color.WHITE
        );

        tablaVehiculos.getTableHeader().setBackground(
                COLOR_SECUNDARIO
        );

        tablaVehiculos.getTableHeader().setPreferredSize(
                new Dimension(0, 40)
        );

        // ==========================================
        // ALINEACIÓN
        // ==========================================

        DefaultTableCellRenderer centro =
                new DefaultTableCellRenderer();

        centro.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        tablaVehiculos
                .getColumnModel()
                .getColumn(0)
                .setCellRenderer(centro);

        tablaVehiculos
                .getColumnModel()
                .getColumn(1)
                .setCellRenderer(centro);

        tablaVehiculos
                .getColumnModel()
                .getColumn(2)
                .setCellRenderer(centro);

        tablaVehiculos
                .getColumnModel()
                .getColumn(3)
                .setCellRenderer(centro);

        // Ancho columnas
        tablaVehiculos
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(100);

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
                .setPreferredWidth(120);

        // Scroll
        JScrollPane scrollTabla =
                new JScrollPane(tablaVehiculos);

        scrollTabla.setBorder(
                BorderFactory.createLineBorder(
                        new Color(229, 231, 235)
                )
        );

        panelTabla.add(
                scrollTabla,
                BorderLayout.CENTER
        );

        panelDerecho.add(
                panelTabla,
                BorderLayout.CENTER
        );

        // ==========================================
        // AGREGAR PANELES
        // ==========================================

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

    // =====================================================
    // CREAR CAMPO DE TEXTO
    // =====================================================

    private JTextField crearCampoTexto() {

        JTextField campo = new JTextField();

        campo.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        campo.setPreferredSize(
                new Dimension(200, 38)
        );

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(209, 213, 219)
                        ),
                        BorderFactory.createEmptyBorder(
                                5, 10, 5, 10
                        )
                )
        );

        return campo;
    }

    // =====================================================
    // CREAR ETIQUETA
    // =====================================================

    private JLabel crearEtiqueta(String texto) {

        JLabel etiqueta =
                new JLabel(texto);

        etiqueta.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
                )
        );

        etiqueta.setForeground(
                COLOR_TEXTO
        );

        return etiqueta;
    }

    // =====================================================
    // CONFIGURAR BOTÓN
    // =====================================================

    private void configurarBoton(
            JButton boton,
            Color color
    ) {

        boton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        boton.setForeground(
                Color.WHITE
        );

        boton.setBackground(
                color
        );

        boton.setFocusPainted(false);

        boton.setBorderPainted(false);

        boton.setPreferredSize(
                new Dimension(200, 42)
        );

        boton.setCursor(
                new java.awt.Cursor(
                        java.awt.Cursor.HAND_CURSOR
                )
        );
    }

    // =====================================================
    // CREAR TARJETA
    // =====================================================

    private JPanel crearTarjeta(
            String titulo,
            String descripcion
    ) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout()
                );

        tarjeta.setBackground(
                COLOR_BLANCO
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(229, 231, 235)
                        ),
                        BorderFactory.createEmptyBorder(
                                15, 18, 15, 18
                        )
                )
        );

        JLabel lblTitulo =
                new JLabel(titulo);

        lblTitulo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
                )
        );

        lblTitulo.setForeground(
                COLOR_ACENTO
        );

        JLabel lblDescripcion =
                new JLabel(
                        "<html>" + descripcion + "</html>"
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

    // =====================================================
    // MÉTODOS PARA EL PRESENTER
    // =====================================================

    public String getPlaca() {

        return txtPlaca
                .getText();
    }

    public String getMarca() {

        return txtMarca
                .getText();
    }

    public String getModelo() {

        return txtModelo
                .getText();
    }

    public double getPrecio()
            throws NumberFormatException {

        String textoPrecio =
                txtPrecio
                        .getText()
                        .trim();

        if (textoPrecio.isEmpty()) {

            throw new NumberFormatException(
                    "El campo precio no puede estar vacío."
            );
        }

        return Double.parseDouble(
                textoPrecio
        );
    }

    // =====================================================
    // BOTÓN GUARDAR
    // =====================================================

    public JButton getBtnGuardar() {

        return btnGuardar;
    }

    // =====================================================
    // BOTÓN ELIMINAR
    // =====================================================

    public JButton getBtnEliminar() {

        return btnEliminar;
    }

    // =====================================================
    // MODELO DE TABLA
    // =====================================================

    public DefaultTableModel getModeloTabla() {

        return modeloTabla;
    }

    // =====================================================
    // VEHÍCULO SELECCIONADO
    // =====================================================

    public String getPlacaSeleccionada() {

        int fila =
                tablaVehiculos
                        .getSelectedRow();

        if (fila != -1) {

            return tablaVehiculos
                    .getValueAt(
                            fila,
                            0
                    )
                    .toString();
        }

        return null;
    }

    // =====================================================
    // LIMPIAR CAMPOS
    // =====================================================

    public void limpiarCampos() {

        txtPlaca.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtPrecio.setText("");

        txtPlaca.requestFocus();
    }
}