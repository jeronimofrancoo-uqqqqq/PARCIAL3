package co.edu.uniquindio.panaderia.viewcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

/**
 * Controlador principal que orquesta las pestañas de Empleados y Pedidos.
 */
public class MainViewController {

    @FXML private TabPane tabPane;
    @FXML private EmpleadoViewController empleadoViewController;
    @FXML private PedidoViewController pedidoViewController;

    @FXML
    public void initialize() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            refrescarVistas();
        });
        refrescarVistas();
    }

    private void refrescarVistas() {
        if (empleadoViewController != null) {
            empleadoViewController.refrescarTabla();
        }
        if (pedidoViewController != null) {
            pedidoViewController.refrescarTablas();
        }
    }
}