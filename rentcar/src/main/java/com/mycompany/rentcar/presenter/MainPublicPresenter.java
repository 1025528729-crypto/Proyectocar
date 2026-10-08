package com.mycompany.rentcar.presenter;

import com.mycompany.rentcar.model.Vehiculo;
import com.mycompany.rentcar.model.VehiculoDAO;
import com.mycompany.rentcar.view.LoginView;
import com.mycompany.rentcar.view.MainPublicView;
import com.mycompany.rentcar.presenter.MarcaItem; // Importación para reconocer MarcaItem

import java.util.List;

public class MainPublicPresenter {

    private MainPublicView view;
    private VehiculoDAO vehiculoDAO;

    public MainPublicPresenter(MainPublicView view) {
        this.view = view;
        this.vehiculoDAO = new VehiculoDAO();

        initPresenter();
    }

    private void initPresenter() {
        // Cargar marcas e ítems de prueba en el ComboBox
        cargarMarcas();

        // Cargar catálogo completo al iniciar
        cargarTodosLosVehiculos();

        // Evento al cambiar selección de marcas
        view.getComboMarcas().addActionListener(e -> filtrarPorMarca());

        // Evento botón mostrar todos
        view.getBtnVerTodos().addActionListener(e -> cargarTodosLosVehiculos());

        // Evento botón Iniciar Sesión (Abre Login)
        view.getBtnLogin().addActionListener(e -> abrirLogin());
    }

    private void cargarMarcas() {
        view.getComboMarcas().removeAllItems();
        List<MarcaItem> marcas = vehiculoDAO.obtenerMarcas();
        for (MarcaItem m : marcas) {
            view.getComboMarcas().addItem(m);
        }
    }

    private void cargarTodosLosVehiculos() {
        view.getTableModel().setRowCount(0);
        List<Vehiculo> lista = vehiculoDAO.listar();
        for (Vehiculo v : lista) {
            view.getTableModel().addRow(new Object[]{
                v.getPlaca(),
                v.getMarca(),
                v.getModelo(),
                String.format("%.2f", v.getPrecioPorDia())
            });
        }
    }

    private void filtrarPorMarca() {
        MarcaItem seleccion = (MarcaItem) view.getComboMarcas().getSelectedItem();
        if (seleccion != null) {
            view.getTableModel().setRowCount(0);
            List<Vehiculo> lista = vehiculoDAO.listarPorMarca(seleccion.getNombre());
            for (Vehiculo v : lista) {
                view.getTableModel().addRow(new Object[]{
                    v.getPlaca(),
                    v.getMarca(),
                    v.getModelo(),
                    String.format("%.2f", v.getPrecioPorDia())
                });
            }
        }
    }

    private void abrirLogin() {
        view.dispose(); // Cierra el catálogo público
        LoginView loginView = new LoginView();
        new LoginPresenter(loginView);
        loginView.setVisible(true);
    }
}