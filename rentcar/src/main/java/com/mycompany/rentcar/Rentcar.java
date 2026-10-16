package com.mycompany.rentcar;

import com.mycompany.rentcar.presenter.MainPublicPresenter;
import com.mycompany.rentcar.view.MainPublicView;
import javax.swing.SwingUtilities;

public class Rentcar {

    public static void main(String[] args) {
        // Abre la conexión a la base de datos MIENTRAS se construye la ventana
        com.mycompany.rentcar.config.Conexion.precalentar();

        SwingUtilities.invokeLater(() -> {
            // Inicia la vista pública accesible para cualquier persona
            MainPublicView publicView = new MainPublicView();
            new MainPublicPresenter(publicView);
            publicView.setVisible(true);
        });
    }
}