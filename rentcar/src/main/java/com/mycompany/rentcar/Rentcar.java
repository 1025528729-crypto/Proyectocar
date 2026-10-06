package com.mycompany.rentcar;

import com.mycompany.rentcar.model.VehiculoDAO;
import com.mycompany.rentcar.presenter.VehiculoPresenter;
import com.mycompany.rentcar.view.VehiculoView;
import javax.swing.SwingUtilities;

public class Rentcar {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VehiculoView view = new VehiculoView();
            VehiculoDAO model = new VehiculoDAO();
            
            new VehiculoPresenter(view, model);
            
            view.setVisible(true);
        });
    }
}