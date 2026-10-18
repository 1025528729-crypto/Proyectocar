package com.mycompany.rentcar;

import com.mycompany.rentcar.view.AppFrame;
import com.mycompany.rentcar.view.BotonRentCarUI;
import javax.swing.SwingUtilities;

public class Rentcar {

    public static void main(String[] args) {
        // Abre la conexión a la base de datos MIENTRAS se construye la ventana
        com.mycompany.rentcar.config.Conexion.precalentar();

        SwingUtilities.invokeLater(() -> {
            BotonRentCarUI.instalar();              // estilo común de todos los botones
            new AppFrame().setVisible(true);        // la única ventana del programa
        });
    }
}