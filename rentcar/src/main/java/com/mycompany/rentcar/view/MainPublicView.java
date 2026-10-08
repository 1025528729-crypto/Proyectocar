package com.mycompany.rentcar.view;

import com.mycompany.rentcar.presenter.MarcaItem;
import com.mycompany.rentcar.model.Vehiculo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainPublicView extends JFrame {

    private JComboBox<MarcaItem> comboMarcas;
    private JTable tablaVehiculos;
    private DefaultTableModel tableModel;
    private JButton btnLogin;
    private JButton btnVerTodos;
    private JLabel lblBannerMarca;

    public MainPublicView() {
        setTitle("RentCar - Catálogo de Vehículos");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL SUPERIOR: Encabezado y Botón de Login ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(40, 53, 147));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("🚗 RentCar - Alquiler de Vehículos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        btnLogin = new JButton("🔑 Iniciar Sesión / Registrarse");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 12));
        btnLogin.setBackground(new Color(255, 152, 0));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);

        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(btnLogin, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // --- PANEL CENTRAL: Filtro por Marca y Tabla de Vehículos ---
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Filtros superiores
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelFiltros.add(new JLabel("Filtrar por Marca:"));
        
        comboMarcas = new JComboBox<>();
        comboMarcas.setPreferredSize(new Dimension(200, 30));
        panelFiltros.add(comboMarcas);

        btnVerTodos = new JButton("Mostrar Todos");
        panelFiltros.add(btnVerTodos);

        lblBannerMarca = new JLabel("", SwingConstants.CENTER);
        lblBannerMarca.setPreferredSize(new Dimension(100, 40));

        panelFiltros.add(lblBannerMarca);
        panelCentral.add(panelFiltros, BorderLayout.NORTH);

        // Tabla de vehículos
        String[] columnas = {"Placa", "Marca", "Modelo", "Precio por Día ($)"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura para el público
            }
        };
        tablaVehiculos = new JTable(tableModel);
        tablaVehiculos.setRowHeight(25);
        tablaVehiculos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scrollTable = new JScrollPane(tablaVehiculos);
        panelCentral.add(scrollTable, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);
    }

    // Getters para el Presenter
    public JComboBox<MarcaItem> getComboMarcas() { return comboMarcas; }
    public JTable getTablaVehiculos() { return tablaVehiculos; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JButton getBtnLogin() { return btnLogin; }
    public JButton getBtnVerTodos() { return btnVerTodos; }
    public JLabel getLblBannerMarca() { return lblBannerMarca; }
}