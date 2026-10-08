package com.mycompany.rentcar.presenter;

import com.mycompany.rentcar.model.Usuario;
import com.mycompany.rentcar.model.VehiculoDAO;
import com.mycompany.rentcar.view.VehiculoView;

public class VehiculoPresenter {
    private VehiculoView view;
    private VehiculoDAO dao;
    private Usuario usuarioLogueado;

    // Constructor existente
    public VehiculoPresenter(VehiculoView view) {
        this.view = view;
        this.dao = new VehiculoDAO();
        initPresenter();
    }

    // NUEVO CONSTRUCTOR: Recibe la vista y el usuario autenticado
    public VehiculoPresenter(VehiculoView view, Usuario usuario) {
        this.view = view;
        this.dao = new VehiculoDAO();
        this.usuarioLogueado = usuario;
        initPresenter();
        aplicarPermisosPorRol();
    }

    private void initPresenter() {
        // Tu lógica actual para enlazar botones, eventos y cargar datos...
    }

    // Método para ocultar o habilitar funciones según el rol (ADMIN o CLIENTE)
    private void aplicarPermisosPorRol() {
        if (usuarioLogueado != null) {
            if (usuarioLogueado.esAdmin()) {
                System.out.println("Modo Administrador activado");
                // Aquí mantienes o activas botones de registrar, eliminar, etc.
            } else {
                System.out.println("Modo Cliente/Usuario activado");
                // Aquí puedes desactivar/ocultar botones de administración en la vista si lo deseas
            }
        }
    }
}