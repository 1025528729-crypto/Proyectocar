package com.mycompany.rentcar.presenter;

import com.mycompany.rentcar.model.Usuario;
import com.mycompany.rentcar.model.Vehiculo;
import com.mycompany.rentcar.model.VehiculoDAO;
import com.mycompany.rentcar.view.VehiculoView;

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class VehiculoPresenter {

    private VehiculoView view;
    private VehiculoDAO dao;
    private Usuario usuarioLogueado;
    private String placaEditando = null;

    // Constructor normal
    public VehiculoPresenter(VehiculoView view) {
        this.view = view;
        this.dao = new VehiculoDAO();

        initPresenter();
    }

    // Constructor con usuario
    public VehiculoPresenter(VehiculoView view, Usuario usuario) {
        this.view = view;
        this.dao = new VehiculoDAO();
        this.usuarioLogueado = usuario;

        initPresenter();
        aplicarPermisosPorRol();
    }

    // =========================================================
    // INICIALIZAR PRESENTER
    // =========================================================

    private void initPresenter() {

        view.getBtnGuardar().addActionListener(e -> registrarVehiculo());
        view.getBtnModificar().addActionListener(e -> prepararModificacion());
        view.getBtnEliminar().addActionListener(e -> eliminarVehiculo());

        cargarVehiculos();
    }

    // =========================================================
    // REGISTRAR VEHÍCULO
    // =========================================================

    private void registrarVehiculo() {

        String placa = view.getPlaca();
        String marca = view.getMarca();
        String modelo = view.getModelo();
        double precio = view.getPrecio();
        String foto = view.getFoto();

        String camposFaltantes = view.validarCamposObligatorios();
        if (!camposFaltantes.isBlank()) {
            mostrarCamposIncompletos(camposFaltantes);
            return;
        }

        // Validar placa
        if (placa == null || placa.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Ingrese la placa del vehículo.",
                    "Campo obligatorio",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Validar marca
        if (marca == null || marca.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Seleccione o ingrese una marca.",
                    "Campo obligatorio",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Validar modelo
        if (modelo == null || modelo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Ingrese el modelo del vehículo.",
                    "Campo obligatorio",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Validar precio
        if (precio <= 0) {
            JOptionPane.showMessageDialog(
                    view,
                    "Ingrese un precio válido mayor a 0.",
                    "Precio inválido",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Al registrar, la placa debe ser nueva. En modo edición se conserva la placa seleccionada.
        Vehiculo existente = dao.buscarPorPlaca(placa);

        if (placaEditando == null && existente != null) {
            JOptionPane.showMessageDialog(
                    view,
                    "Ya existe un vehículo registrado con la placa: " + placa,
                    "Vehículo existente",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Crear vehículo
        Vehiculo vehiculo = new Vehiculo();

        vehiculo.setPlaca(placa);
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setPrecioPorDia(precio);

        // Guardar la ruta de la foto
        vehiculo.setFoto(foto);
        vehiculo.setTipo(view.getTipo());
        vehiculo.setAnio(view.getAnio());
        vehiculo.setColor(view.getColor());
        vehiculo.setTransmision(view.getTransmision());
        vehiculo.setCombustible(view.getCombustible());
        vehiculo.setCapacidad(view.getCapacidad());
        vehiculo.setKilometraje(view.getKilometraje());
        vehiculo.setPuertas(view.getPuertas());
        vehiculo.setCategoria(view.getCategoria());
        vehiculo.setCiudad(view.getCiudad());
        vehiculo.setDescripcion(view.getDescripcion());
        vehiculo.setDisponible(view.isDisponible());

        if (vehiculo.getAnio() < 1886 || vehiculo.getCapacidad() < 1 || vehiculo.getKilometraje() < 0
                || (!vehiculo.getTipo().equalsIgnoreCase("MOTO") && vehiculo.getPuertas() < 1)) {
            JOptionPane.showMessageDialog(view, "Revisa el año, la capacidad, el kilometraje y el número de puertas: deben tener valores válidos.", "Datos inválidos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Insertar o actualizar el vehículo en la base de datos.
        boolean editando = placaEditando != null;
        boolean registrado = editando ? dao.actualizar(vehiculo) : dao.insertar(vehiculo);

        if (registrado) {

            // No mostrar ventanas emergentes de éxito; actualizar la interfaz silenciosamente.
            // Actualizar tabla
            cargarVehiculos();

            // Limpiar formulario y salir del modo de edición.
            placaEditando = null;
            view.limpiarCampos();

        } else {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo registrar el vehículo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Ventana de validación personalizada con el estilo oscuro/rojo de RentCar. */
    private void mostrarCamposIncompletos(String camposFaltantes) {
        final Color fondo = new Color(13, 15, 19);
        final Color panel = new Color(23, 26, 32);
        final Color borde = new Color(55, 61, 72);
        final Color rojo = new Color(225, 6, 0);
        final Color texto = new Color(245, 247, 250);
        final Color gris = new Color(177, 183, 194);

        Window owner = SwingUtilities.getWindowAncestor(view);
        JDialog dialogo = new JDialog(owner, "Formulario incompleto", JDialog.ModalityType.APPLICATION_MODAL);
        dialogo.setUndecorated(true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout(0, 0));
        raiz.setBackground(fondo);
        raiz.setBorder(BorderFactory.createLineBorder(borde, 1));

        JPanel cabecera = new JPanel(new BorderLayout(12, 0));
        cabecera.setBackground(rojo);
        cabecera.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 18));

        JLabel icono = new JLabel("!");
        icono.setHorizontalAlignment(SwingConstants.CENTER);
        icono.setForeground(Color.WHITE);
        icono.setFont(new Font("Segoe UI", Font.BOLD, 25));
        icono.setOpaque(true);
        icono.setBackground(new Color(175, 5, 0));
        icono.setPreferredSize(new Dimension(38, 38));

        JPanel textosCabecera = new JPanel(new BorderLayout(0, 3));
        textosCabecera.setOpaque(false);
        JLabel titulo = new JLabel("FALTAN DATOS POR COMPLETAR");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        JLabel subtitulo = new JLabel("Revisa el formulario antes de continuar");
        subtitulo.setForeground(new Color(255, 225, 225));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textosCabecera.add(titulo, BorderLayout.NORTH);
        textosCabecera.add(subtitulo, BorderLayout.CENTER);
        cabecera.add(icono, BorderLayout.WEST);
        cabecera.add(textosCabecera, BorderLayout.CENTER);

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBackground(panel);
        contenido.setBorder(BorderFactory.createEmptyBorder(22, 24, 20, 24));

        JLabel mensaje = new JLabel("Para guardar el vehículo, completa estos campos:");
        mensaje.setForeground(texto);
        mensaje.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        javax.swing.JTextArea lista = new javax.swing.JTextArea(camposFaltantes);
        lista.setEditable(false);
        lista.setLineWrap(true);
        lista.setWrapStyleWord(true);
        lista.setOpaque(false);
        lista.setForeground(gris);
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lista.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        lista.setFocusable(false);

        JPanel bloque = new JPanel(new BorderLayout(0, 8));
        bloque.setOpaque(false);
        bloque.add(mensaje, BorderLayout.NORTH);
        bloque.add(lista, BorderLayout.CENTER);
        contenido.add(bloque, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acciones.setOpaque(false);
        JButton entendido = new JButton("ENTENDIDO");
        entendido.setFont(new Font("Segoe UI", Font.BOLD, 12));
        entendido.setForeground(Color.WHITE);
        entendido.setBackground(rojo);
        entendido.setFocusPainted(false);
        entendido.setBorderPainted(false);
        entendido.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        entendido.setPreferredSize(new Dimension(125, 39));
        entendido.addActionListener(e -> dialogo.dispose());
        entendido.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { entendido.setBackground(new Color(255, 42, 35)); }
            @Override public void mouseExited(MouseEvent e) { entendido.setBackground(rojo); }
        });
        acciones.add(entendido);
        contenido.add(acciones, BorderLayout.SOUTH);

        raiz.add(cabecera, BorderLayout.NORTH);
        raiz.add(contenido, BorderLayout.CENTER);
        dialogo.setContentPane(raiz);
        dialogo.getRootPane().setDefaultButton(entendido);
        dialogo.pack();
        dialogo.setSize(new Dimension(500, Math.max(275, dialogo.getPreferredSize().height)));
        dialogo.setLocationRelativeTo(view);
        dialogo.setVisible(true);
    }

    private void prepararModificacion() {
        String placa = view.getPlacaSeleccionada();
        if (placa == null || placa.isBlank()) {
            JOptionPane.showMessageDialog(view, "Selecciona primero un vehículo de la tabla.",
                    "Vehículo no seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Vehiculo vehiculo = dao.buscarPorPlaca(placa);
        if (vehiculo == null) {
            JOptionPane.showMessageDialog(view, "No se encontró el vehículo seleccionado.",
                    "Vehículo no encontrado", JOptionPane.WARNING_MESSAGE);
            cargarVehiculos();
            return;
        }
        placaEditando = vehiculo.getPlaca();
        view.cargarVehiculoEnFormulario(vehiculo);
    }

    // =========================================================
    // CARGAR VEHÍCULOS EN LA TABLA
    // =========================================================

    private void cargarVehiculos() {

        try {

            List<Vehiculo> vehiculos = dao.listar();

            view.getModeloTabla().setRowCount(0);

            for (Vehiculo vehiculo : vehiculos) {

                view.getModeloTabla().addRow(
                        new Object[]{
                            vehiculo.getFoto(),          // FOTO
                            vehiculo.getPlaca(),         // PLACA
                            vehiculo.getMarca(),         // MARCA
                            vehiculo.getModelo(),        // MODELO
                            vehiculo.getPrecioPorDia()   // PRECIO
                        }
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Error al cargar los vehículos: "
                    + e.getMessage()
            );

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    view,
                    "Ocurrió un error al cargar los vehículos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // ELIMINAR VEHÍCULO
    // =========================================================

    private void eliminarVehiculo() {

        String placa = view.getPlacaSeleccionada();

        // Verificar que haya un vehículo seleccionado
        if (placa == null || placa.trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccione un vehículo de la tabla.",
                    "Vehículo no seleccionado",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Confirmación con una ventana personalizada acorde al diseño de RentCar.
        if (!confirmarEliminacion(placa)) {
            return;
        }

        // Eliminar de la base de datos
        boolean eliminado = dao.eliminar(placa);

        if (eliminado) {
            if (placaEditando != null && placaEditando.equalsIgnoreCase(placa)) {
                placaEditando = null;
                view.limpiarCampos();
            }

            // No mostrar ventanas emergentes al eliminar; actualizar la interfaz silenciosamente.
            // Actualizar tabla
            cargarVehiculos();

        } else {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo eliminar el vehículo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    /**
     * Muestra una confirmación personalizada para eliminar un vehículo.
     * Devuelve true únicamente si el usuario pulsa el botón Eliminar.
     */
    private boolean confirmarEliminacion(String placa) {
        final Color fondo = new Color(11, 13, 17);
        final Color panel = new Color(20, 23, 28);
        final Color borde = new Color(48, 53, 62);
        final Color texto = new Color(240, 242, 245);
        final Color gris = new Color(156, 163, 175);
        final Color rojo = new Color(225, 6, 0);
        final Color rojoHover = new Color(255, 45, 38);

        Window owner = SwingUtilities.getWindowAncestor(view);
        JDialog dialogo = new JDialog(owner, "Confirmar eliminación", JDialog.ModalityType.APPLICATION_MODAL);
        dialogo.setUndecorated(true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.setSize(450, 245);
        dialogo.setMinimumSize(new Dimension(450, 245));

        JPanel raiz = new JPanel(new BorderLayout(0, 0));
        raiz.setBackground(fondo);
        raiz.setBorder(BorderFactory.createLineBorder(borde, 1));

        JPanel franja = new JPanel(new BorderLayout());
        franja.setBackground(rojo);
        franja.setBorder(BorderFactory.createEmptyBorder(13, 20, 13, 16));

        JLabel titulo = new JLabel("CONFIRMAR ELIMINACIÓN");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        franja.add(titulo, BorderLayout.CENTER);

        JLabel cerrar = new JLabel("×", SwingConstants.CENTER);
        cerrar.setForeground(Color.WHITE);
        cerrar.setFont(new Font("SansSerif", Font.BOLD, 24));
        cerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cerrar.setPreferredSize(new Dimension(28, 25));
        cerrar.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { dialogo.dispose(); }
            @Override public void mouseEntered(MouseEvent e) { cerrar.setForeground(new Color(255, 220, 220)); }
            @Override public void mouseExited(MouseEvent e) { cerrar.setForeground(Color.WHITE); }
        });
        franja.add(cerrar, BorderLayout.EAST);
        raiz.add(franja, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBackground(panel);
        contenido.setBorder(BorderFactory.createEmptyBorder(22, 24, 12, 24));

        JLabel pregunta = new JLabel("¿Deseas eliminar este vehículo?");
        pregunta.setForeground(texto);
        pregunta.setFont(new Font("SansSerif", Font.BOLD, 17));
        contenido.add(pregunta, BorderLayout.NORTH);

        JPanel detalle = new JPanel(new BorderLayout(8, 0));
        detalle.setBackground(panel);
        detalle.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        JLabel placaLabel = new JLabel("PLACA  " + placa);
        placaLabel.setForeground(new Color(255, 205, 0));
        placaLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        detalle.add(placaLabel, BorderLayout.NORTH);
        JLabel aviso = new JLabel("Esta acción no se puede deshacer.");
        aviso.setForeground(gris);
        aviso.setFont(new Font("SansSerif", Font.PLAIN, 12));
        detalle.add(aviso, BorderLayout.SOUTH);
        contenido.add(detalle, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setBackground(panel);
        acciones.setBorder(BorderFactory.createEmptyBorder(8, 24, 20, 24));

        JButton cancelar = crearBotonConfirmacion("Cancelar", new Color(38, 43, 51), new Color(55, 60, 70), texto);
        JButton eliminar = crearBotonConfirmacion("Eliminar vehículo", rojo, rojoHover, Color.WHITE);
        cancelar.addActionListener(e -> dialogo.dispose());
        eliminar.addActionListener(e -> {
            dialogo.getRootPane().putClientProperty("rentcar.confirmado", Boolean.TRUE);
            dialogo.dispose();
        });
        acciones.add(cancelar);
        acciones.add(eliminar);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(panel);
        centro.add(contenido, BorderLayout.CENTER);
        centro.add(acciones, BorderLayout.SOUTH);
        raiz.add(centro, BorderLayout.CENTER);
        dialogo.setContentPane(raiz);
        dialogo.getRootPane().setDefaultButton(eliminar);
        dialogo.pack();
        dialogo.setSize(450, 245);
        dialogo.setLocationRelativeTo(view);
        dialogo.setVisible(true);
        return Boolean.TRUE.equals(dialogo.getRootPane().getClientProperty("rentcar.confirmado"));
    }

    private JButton crearBotonConfirmacion(String texto, Color normal, Color hover, Color colorTexto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setForeground(colorTexto);
        boton.setBackground(normal);
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(11, 17, 11, 17));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { boton.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e) { boton.setBackground(normal); }
        });
        return boton;
    }

    // =========================================================
    // PERMISOS SEGÚN EL ROL
    // =========================================================

    private void aplicarPermisosPorRol() {

        if (usuarioLogueado != null) {

            if (usuarioLogueado.esAdmin()) {

                System.out.println(
                        "Modo Administrador activado"
                );

            } else {

                System.out.println(
                        "Modo Cliente/Usuario activado"
                );
            }
        }
    }
}