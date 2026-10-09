package com.mycompany.rentcar.presenter;

import com.mycompany.rentcar.model.Vehiculo;
import com.mycompany.rentcar.model.VehiculoDAO;
import com.mycompany.rentcar.view.LoginView;
import com.mycompany.rentcar.view.MainPublicView;

import java.util.List;

public class MainPublicPresenter {

    private MainPublicView view;
    private VehiculoDAO vehiculoDAO;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public MainPublicPresenter(MainPublicView view) {

        this.view = view;
        this.vehiculoDAO = new VehiculoDAO();

        initPresenter();
    }

    // =========================================================
    // INICIALIZAR
    // =========================================================

    private void initPresenter() {

        cargarMarcas();

        cargarTodosLosVehiculos();

        // Filtro por marca
        view.getComboMarcas()
                .addActionListener(
                        e -> filtrarPorMarca()
                );

        // Mostrar todos
        view.getBtnVerTodos()
                .addActionListener(
                        e -> cargarTodosLosVehiculos()
                );

        // Iniciar sesión
        view.getBtnLogin()
                .addActionListener(
                        e -> abrirLogin()
                );
    }

    // =========================================================
    // CARGAR MARCAS
    // =========================================================

    private void cargarMarcas() {

        view.getComboMarcas()
                .removeAllItems();

        List<MarcaItem> marcas =
                vehiculoDAO.obtenerMarcas();

        // Opción explícita para no dejar un filtro de marca activo por defecto.
        view.getComboMarcas().addItem(new MarcaItem(0, "Todas las marcas", ""));

        for (MarcaItem marca : marcas) {

            view.getComboMarcas()
                    .addItem(marca);
        }
    }

    // =========================================================
    // CARGAR TODOS LOS VEHÍCULOS
    // =========================================================

    private void cargarTodosLosVehiculos() {

        view.getTableModel()
                .setRowCount(0);

        List<Vehiculo> lista =
                vehiculoDAO.listar();

        for (Vehiculo v : lista) {

            view.getTableModel()
                    .addRow(
                            new Object[]{
                                v.getFoto(),
                                v.getPlaca(),
                                v.getMarca(),
                                v.getModelo(), v.getPrecioPorDia(), v.getTipo(), v.getAnio(), v.getColor(),
                                v.getTransmision(), v.getCombustible(), v.getCapacidad(), v.getKilometraje(),
                                v.getCategoria(), v.getDescripcion(), v.getCiudad(), v.isDisponible(), v.getPuertas()
                            }
                    );
        }
    }

    // =========================================================
    // FILTRAR POR MARCA
    // =========================================================

    private void filtrarPorMarca() {

        MarcaItem seleccion =
                (MarcaItem)
                        view.getComboMarcas()
                                .getSelectedItem();

        if (seleccion == null) {
            return;
        }
        if (seleccion.getId() == 0 || "Todas las marcas".equalsIgnoreCase(seleccion.getNombre())) {
            cargarTodosLosVehiculos();
            return;
        }

        view.getTableModel()
                .setRowCount(0);

        List<Vehiculo> lista =
                vehiculoDAO.listarPorMarca(
                        seleccion.getNombre()
                );

        for (Vehiculo v : lista) {

            view.getTableModel()
                    .addRow(
                            new Object[]{
                                v.getFoto(),
                                v.getPlaca(),
                                v.getMarca(),
                                v.getModelo(), v.getPrecioPorDia(), v.getTipo(), v.getAnio(), v.getColor(),
                                v.getTransmision(), v.getCombustible(), v.getCapacidad(), v.getKilometraje(),
                                v.getCategoria(), v.getDescripcion(), v.getCiudad(), v.isDisponible(), v.getPuertas()
                            }
                    );
        }
    }

    // =========================================================
    // ABRIR LOGIN
    // =========================================================

    private void abrirLogin() {

        view.dispose();

        LoginView loginView =
                new LoginView();

        new LoginPresenter(
                loginView
        );

        loginView.setVisible(true);
    }
}