package com.mycompany.rentcar.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class VehiculoView extends JFrame {

    private JTextField txtPlaca;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtPrecio;
    private JButton btnGuardar;
    private JButton btnEliminar;

    // Componentes para la tabla
    private JTable tablaVehiculos;
    private DefaultTableModel modeloTabla;

    public VehiculoView() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Gestión de Vehículos");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel de Formulario (Izquierda) - 6 filas x 2 columnas
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 8, 8));

        txtPlaca = new JTextField();
        txtMarca = new JTextField();
        txtModelo = new JTextField();
        txtPrecio = new JTextField();
        btnGuardar = new JButton("Guardar");
        btnEliminar = new JButton("Eliminar");

        panelFormulario.add(new JLabel(" Placa:"));
        panelFormulario.add(txtPlaca);
        panelFormulario.add(new JLabel(" Marca:"));
        panelFormulario.add(txtMarca);
        panelFormulario.add(new JLabel(" Modelo:"));
        panelFormulario.add(txtModelo);
        panelFormulario.add(new JLabel(" Precio por día:"));
        panelFormulario.add(txtPrecio);
        panelFormulario.add(new JLabel("")); // Espacio vacío para alinear
        panelFormulario.add(btnGuardar);
        panelFormulario.add(new JLabel("")); // Espacio vacío para alinear
        panelFormulario.add(btnEliminar);

        add(panelFormulario, BorderLayout.WEST);

        // Tabla de Registros (Centro)
        modeloTabla = new DefaultTableModel(new String[]{"Placa", "Marca", "Modelo", "Precio/Día"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer las celdas de la tabla no editables directamente
            }
        };
        
        tablaVehiculos = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaVehiculos);

        add(scrollTabla, BorderLayout.CENTER);
    }

    // Métodos para interactuar con la vista
    public String getPlaca() {
        return txtPlaca.getText();
    }

    public String getMarca() {
        return txtMarca.getText();
    }

    public String getModelo() {
        return txtModelo.getText();
    }

    public double getPrecio() throws NumberFormatException {
        String textoPrecio = txtPrecio.getText().trim();
        if (textoPrecio.isEmpty()) {
            throw new NumberFormatException("El campo precio no puede estar vacío.");
        }
        return Double.parseDouble(textoPrecio);
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
        int fila = tablaVehiculos.getSelectedRow();
        if (fila != -1) {
            return tablaVehiculos.getValueAt(fila, 0).toString();
        }
        return null;
    }

    public void limpiarCampos() {
        txtPlaca.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtPrecio.setText("");
        txtPlaca.requestFocus();
    }
}