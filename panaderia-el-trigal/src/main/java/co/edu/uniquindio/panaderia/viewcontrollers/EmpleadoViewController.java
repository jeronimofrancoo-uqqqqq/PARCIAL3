package co.edu.uniquindio.panaderia.viewcontrollers;

import co.edu.uniquindio.panaderia.controllers.PanaderiaController;
import co.edu.uniquindio.panaderia.model.Cajero;
import co.edu.uniquindio.panaderia.model.Empleado;
import co.edu.uniquindio.panaderia.model.TipoEmpleado;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Controlador de la vista de registro y listado de empleados.
 */
public class EmpleadoViewController {

    private final PanaderiaController controller = PanaderiaController.getInstance();
    private final ObservableList<Empleado> listaEmpleados = FXCollections.observableArrayList();

    @FXML private ComboBox<TipoEmpleado> cmbTipoEmpleado;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtMinutos;
    @FXML private TextField txtCaja;
    @FXML private Button btnRegistrar;
    @FXML private Button btnLimpiar;

    @FXML private TableView<Empleado> tblEmpleados;
    @FXML private TableColumn<Empleado, String> colCodigo;
    @FXML private TableColumn<Empleado, String> colNombre;
    @FXML private TableColumn<Empleado, String> colDocumento;
    @FXML private TableColumn<Empleado, String> colTipo;
    @FXML private TableColumn<Empleado, Number> colMinutos;
    @FXML private TableColumn<Empleado, String> colCaja;

    @FXML
    public void initialize() {
        cmbTipoEmpleado.setItems(FXCollections.observableArrayList(TipoEmpleado.values()));

        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodigo()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombreCompleto()));
        colDocumento.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDocumento()));
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipoEmpleado().getEtiqueta()));
        colMinutos.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMinutosDisponibles()));
        colCaja.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof Cajero cajero) {
                return new SimpleStringProperty(cajero.getCajaAsignada());
            }
            return new SimpleStringProperty("-");
        });

        tblEmpleados.setItems(listaEmpleados);
        txtCaja.setDisable(true);
        refrescarTabla();
    }

    @FXML
    public void onTipoEmpleadoSeleccionado() {
        TipoEmpleado seleccionado = cmbTipoEmpleado.getValue();
        boolean esCajero = seleccionado == TipoEmpleado.CAJERO;
        txtCaja.setDisable(!esCajero);
        if (!esCajero) {
            txtCaja.clear();
        }
    }

    @FXML
    public void onRegistrarEmpleado() {
        TipoEmpleado tipo = cmbTipoEmpleado.getValue();
        String nombre = txtNombre.getText();
        String doc = txtDocumento.getText();
        String codigo = txtCodigo.getText();
        String minutosStr = txtMinutos.getText();
        String caja = txtCaja.getText();

        if (tipo == null || nombre == null || nombre.isBlank() ||
                doc == null || doc.isBlank() ||
                codigo == null || codigo.isBlank() ||
                minutosStr == null || minutosStr.isBlank()) {
            AlertUtil.mostrarAdvertencia("Campos requeridos", "Por favor complete todos los campos obligatorios.");
            return;
        }

        if (tipo == TipoEmpleado.CAJERO && (caja == null || caja.isBlank())) {
            AlertUtil.mostrarAdvertencia("Caja requerida", "Debe indicar la caja asignada para el cajero.");
            return;
        }

        int minutos;
        try {
            minutos = Integer.parseInt(minutosStr.trim());
        } catch (NumberFormatException e) {
            AlertUtil.mostrarAdvertencia("Formato inválido", "Los minutos disponibles deben ser un valor entero.");
            return;
        }

        try {
            controller.registrarEmpleado(tipo, nombre.trim(), doc.trim(), codigo.trim(), minutos, caja);
            AlertUtil.mostrarInfo("Registro exitoso", "El empleado '" + nombre.trim() + "' fue registrado satisfactoriamente.");
            limpiarFormulario();
            refrescarTabla();
        } catch (IllegalArgumentException e) {
            AlertUtil.mostrarError("Error en registro", e.getMessage());
        }
    }

    @FXML
    public void onLimpiar() {
        limpiarFormulario();
    }

    public void refrescarTabla() {
        listaEmpleados.setAll(controller.obtenerEmpleados());
        tblEmpleados.refresh();
    }

    private void limpiarFormulario() {
        cmbTipoEmpleado.setValue(null);
        txtNombre.clear();
        txtDocumento.clear();
        txtCodigo.clear();
        txtMinutos.clear();
        txtCaja.clear();
        txtCaja.setDisable(true);
    }
}