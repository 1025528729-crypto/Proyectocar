package com.mycompany.rentcar.presenter;

import com.mycompany.rentcar.model.Vehiculo;
import com.mycompany.rentcar.model.VehiculoDAO;
import com.mycompany.rentcar.view.MainPublicView;

import javax.swing.SwingWorker;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class MainPublicPresenter {

    private MainPublicView view;
    private VehiculoDAO vehiculoDAO;

    // Copia en memoria de los vehículos: filtrar por marca ya no consulta la base de datos
    private List<Vehiculo> vehiculos = new ArrayList<>();

    // Evita que un resultado viejo pise a uno más reciente
    private int cargaActual = 0;

    // Evita que el combo de marcas dispare el filtro mientras se llena
    private boolean llenandoMarcas = false;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public MainPublicPresenter(MainPublicView view) {

        this.view = view;

        initPresenter();
    }

    // El DAO se crea la primera vez que hace falta (y en segundo plano):
    // así la ventana aparece enseguida aunque la base de datos tarde.
    private synchronized VehiculoDAO dao() {

        if (vehiculoDAO == null) {
            vehiculoDAO = new VehiculoDAO();
        }

        return vehiculoDAO;
    }

    // =========================================================
    // INICIALIZAR
    // =========================================================

    private void initPresenter() {

        // Desde el inicio el combo ya tiene la opción "Todas las marcas"
        llenarMarcas(new ArrayList<>());

        // Filtro por marca
        view.getComboMarcas()
                .addActionListener(
                        e -> filtrarPorMarca()
                );

        // Mostrar todos (vuelve a leer la base de datos)
        view.getBtnVerTodos()
                .addActionListener(
                        e -> cargarTodosLosVehiculos()
                );

        cargarTodosLosVehiculos();
    }

    // =========================================================
    // CARGAR TODOS LOS VEHÍCULOS (segundo plano)
    // =========================================================

    private void cargarTodosLosVehiculos() {

        final int carga = ++cargaActual;

        view.setCargando(true);

        new SwingWorker<List<Vehiculo>, Void>() {

            @Override
            protected List<Vehiculo> doInBackground() {
                return dao().listar();
            }

            @Override
            protected void done() {

                if (carga != cargaActual) {
                    return; // llegó una carga más reciente
                }

                try {
                    vehiculos = get();
                } catch (Exception e) {
                    System.err.println("Error al cargar los vehículos: " + e.getMessage());
                    vehiculos = new ArrayList<>();
                }

                // Las marcas salen de la misma lista (antes era otra consulta)
                llenarMarcas(vehiculos);

                mostrarSegunMarca();

                view.setCargando(false);
            }
        }.execute();
    }

    // =========================================================
    // MARCAS
    // =========================================================

    private void llenarMarcas(List<Vehiculo> lista) {

        String seleccionada = null;
        Object actual = view.getComboMarcas().getSelectedItem();

        if (actual != null) {
            seleccionada = actual.toString();
        }

        llenandoMarcas = true;

        try {

            view.getComboMarcas().removeAllItems();

            // Opción explícita para no dejar un filtro de marca activo por defecto.
            view.getComboMarcas().addItem(new MarcaItem(0, "Todas las marcas", ""));

            // Marcas distintas, ordenadas sin distinguir mayúsculas
            TreeMap<String, String> distintas = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

            for (Vehiculo v : lista) {
                if (v.getMarca() != null && !v.getMarca().isBlank()) {
                    distintas.putIfAbsent(v.getMarca(), v.getMarca());
                }
            }

            int id = 1;

            for (String marca : distintas.values()) {

                MarcaItem item = new MarcaItem(
                        id++,
                        marca,
                        marca.toLowerCase(Locale.ROOT).trim() + ".jpg"
                );

                view.getComboMarcas().addItem(item);

                if (marca.equalsIgnoreCase(seleccionada)) {
                    view.getComboMarcas().setSelectedItem(item);
                }
            }

        } finally {

            llenandoMarcas = false;
        }
    }

    // =========================================================
    // FILTRAR POR MARCA (en memoria, sin consultar la base de datos)
    // =========================================================

    private void filtrarPorMarca() {

        if (llenandoMarcas) {
            return;
        }

        mostrarSegunMarca();
    }

    private void mostrarSegunMarca() {

        MarcaItem seleccion =
                (MarcaItem)
                        view.getComboMarcas()
                                .getSelectedItem();

        List<Vehiculo> aMostrar = new ArrayList<>();

        boolean todas = seleccion == null
                || seleccion.getId() == 0
                || "Todas las marcas".equalsIgnoreCase(seleccion.getNombre());

        for (Vehiculo v : vehiculos) {
            if (todas || seleccion.getNombre().equalsIgnoreCase(v.getMarca())) {
                aMostrar.add(v);
            }
        }

        view.getTableModel()
                .setRowCount(0);

        for (Vehiculo v : aMostrar) {

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
}