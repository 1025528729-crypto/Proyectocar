package com.mycompany.rentcar.presenter;

import com.mycompany.rentcar.model.Vehiculo;
import com.mycompany.rentcar.model.VehiculoDAO;
import com.mycompany.rentcar.view.VehiculoView;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class VehiculoPresenter {
    private final VehiculoView view;
    private final VehiculoDAO model;

    public VehiculoPresenter(VehiculoView view, VehiculoDAO model) {
        this.view = view;
        this.model = model;

        // Registrar listeners de los botones
        this.view.getBtnGuardar().addActionListener(e -> guardarVehiculo());
        this.view.getBtnEliminar().addActionListener(e -> eliminarVehiculo());

        // Cargar vehículos guardados en la tabla al iniciar la app
        listarVehiculos();
    }

    private void listarVehiculos() {
        DefaultTableModel tablaModel = view.getModeloTabla();
        tablaModel.setRowCount(0); // Limpiar filas anteriores

        List<Vehiculo> lista = model.listar();
        for (Vehiculo v : lista) {
            Object[] fila = {
                v.getPlaca(),
                v.getMarca(),
                v.getModelo(),
                v.getPrecioPorDia()
            };
            tablaModel.addRow(fila);
        }
    }

    private void guardarVehiculo() {
        try {
            String placa = view.getPlaca().trim();
            String marca = view.getMarca().trim();
            String modelo = view.getModelo().trim();
            double precioPorDia = view.getPrecio();

            if (placa.isEmpty() || marca.isEmpty() || modelo.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Todos los campos son obligatorios.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Vehiculo nuevoVehiculo = new Vehiculo(placa, marca, modelo, precioPorDia);
            boolean resultado = model.insertar(nuevoVehiculo);

            if (resultado) {
                JOptionPane.showMessageDialog(view, "¡Vehículo registrado con éxito!");
                view.limpiarCampos();
                listarVehiculos(); // Recargar la tabla con el nuevo dato
            } else {
                JOptionPane.showMessageDialog(view, "Error al registrar el vehículo. Verifique si la placa ya existe o el formato del precio.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "El precio ingresado no es válido.", "Error de formato", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarVehiculo() {
        String placa = view.getPlacaSeleccionada();

        if (placa == null) {
            JOptionPane.showMessageDialog(view, "Por favor, selecciona un vehículo de la tabla para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
            view, 
            "¿Estás seguro de eliminar el vehículo con placa: " + placa + "?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean resultado = model.eliminar(placa);
            if (resultado) {
                JOptionPane.showMessageDialog(view, "Vehículo eliminado correctamente.");
                view.limpiarCampos();
                listarVehiculos(); // Refrescar la tabla
            } else {
                JOptionPane.showMessageDialog(view, "No se pudo eliminar el vehículo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}