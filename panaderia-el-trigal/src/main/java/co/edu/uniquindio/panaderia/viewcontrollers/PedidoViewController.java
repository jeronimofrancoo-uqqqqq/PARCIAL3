package co.edu.uniquindio.panaderia.viewcontrollers;

import co.edu.uniquindio.panaderia.controllers.PanaderiaController;
import co.edu.uniquindio.panaderia.model.Empleado;
import co.edu.uniquindio.panaderia.model.Pedido;
import co.edu.uniquindio.panaderia.model.TipoPan;
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

public class PedidoViewController {

    private final PanaderiaController controller = PanaderiaController.getInstance();
    private final ObservableList<Pedido> listaPedidos = FXCollections.observableArrayList();
    private final ObservableList<Empleado> listaEmpleados = FXCollections.observableArrayList();

    // Formulario de registro de pedido
    @FXML private TextField txtCodigoPedido;
    @FXML private TextField txtCliente;
    @FXML private ComboBox<TipoPan> cmbTipoPan;
    @FXML private TextField txtCantidad;
    @FXML private Button btnRegistrarPedido;

    // Botones de acción sobre pedido seleccionado
    @FXML private Button btnAsignarPedido;
    @FXML private Button btnCancelarPedido;

    // Tabla de historial de pedidos
    @FXML private TableView<Pedido> tblPedidos;
    @FXML private TableColumn<Pedido, String> colPedCodigo;
    @FXML private TableColumn<Pedido, String> colPedCliente;
    @FXML private TableColumn<Pedido, String> colPedTipoPan;
    @FXML private TableColumn<Pedido, Number> colPedCantidad;
    @FXML private TableColumn<Pedido, String> colPedEstado;
    @FXML private TableColumn<Pedido, String> colPedEmpleado;
    @FXML private TableColumn<Pedido, Number> colPedMinutos;

    // Tabla de disponibilidad de empleados
    @FXML private TableView<Empleado> tblDisponibilidad;
    @FXML private TableColumn<Empleado, String> colEmpCodigo;
    @FXML private TableColumn<Empleado, String> colEmpNombre;
    @FXML private TableColumn<Empleado, String> colEmpTipo;
    @FXML private TableColumn<Empleado, Number> colEmpMinutos;

    @FXML
    public void initialize() {
        cmbTipoPan.setItems(FXCollections.observableArrayList(TipoPan.values()));

        // Configuración de columnas de pedidos
        colPedCodigo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCodigo()));
        colPedCliente.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombreCliente()));
        colPedTipoPan.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipoPan().name()));
        colPedCantidad.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getCantidadUnidades()));
        colPedEstado.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEstado().name()));
        colPedEmpleado.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getCodigoEmpleadoAsignado() != null ? cell.getValue().getCodigoEmpleadoAsignado() : "-"
        ));
        colPedMinutos.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getMinutosReservados()));

        // Configuración de columnas de disponibilidad de empleados
        colEmpCodigo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCodigo()));
        colEmpNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombreCompleto()));
        colEmpTipo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipoEmpleado().getEtiqueta()));
        colEmpMinutos.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getMinutosDisponibles()));

        tblPedidos.setItems(listaPedidos);
        tblDisponibilidad.setItems(listaEmpleados);

        refrescarTablas();
    }

    @FXML
    public void onRegistrarPedido() {
        String codigo = txtCodigoPedido.getText();
        String cliente = txtCliente.getText();
        TipoPan tipoPan = cmbTipoPan.getValue();
        String cantidadStr = txtCantidad.getText();

        if (codigo == null || codigo.isBlank() ||
                cliente == null || cliente.isBlank() ||
                tipoPan == null ||
                cantidadStr == null || cantidadStr.isBlank()) {
            AlertUtil.mostrarAdvertencia("Campos requeridos", "Por favor ingrese todos los datos del pedido.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr.trim());
        } catch (NumberFormatException e) {
            AlertUtil.mostrarAdvertencia("Cantidad inválida", "La cantidad debe ser un número entero mayor a cero.");
            return;
        }

        try {
            controller.registrarPedido(codigo.trim(), cliente.trim(), tipoPan, cantidad);
            AlertUtil.mostrarInfo("Pedido registrado", "El pedido " + codigo.trim() + " fue registrado en estado PENDIENTE.");
            limpiarFormulario();
            refrescarTablas();
        } catch (IllegalArgumentException e) {
            AlertUtil.mostrarError("Error al registrar pedido", e.getMessage());
        }
    }

    @FXML
    public void onAsignarPedido() {
        Pedido seleccionado = tblPedidos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertUtil.mostrarAdvertencia("Selección requerida", "Por favor seleccione un pedido de la tabla para asignar.");
            return;
        }

        try {
            Pedido asignado = controller.asignarPedido(seleccionado.getCodigo());
            AlertUtil.mostrarInfo("Asignación exitosa",
                    "Pedido " + asignado.getCodigo() + " asignado al empleado " +
                    asignado.getCodigoEmpleadoAsignado() + " (" + asignado.getMinutosReservados() + " minutos reservados).");
            refrescarTablas();
        } catch (IllegalArgumentException | IllegalStateException e) {
            AlertUtil.mostrarError("Error en asignación", e.getMessage());
        }
    }

    @FXML
    public void onCancelarPedido() {
        Pedido seleccionado = tblPedidos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertUtil.mostrarAdvertencia("Selección requerida", "Por favor seleccione un pedido de la tabla para cancelar.");
            return;
        }

        boolean confirma = AlertUtil.confirmar("Confirmar cancelación",
                "¿Está seguro de cancelar el pedido " + seleccionado.getCodigo() +
                (seleccionado.getCodigoEmpleadoAsignado() != null ? " y devolver " + seleccionado.getMinutosReservados() + " minutos?" : "?"));

        if (!confirma) {
            return;
        }

        try {
            Pedido cancelado = controller.cancelarPedido(seleccionado.getCodigo());
            AlertUtil.mostrarInfo("Cancelación confirmada",
                    "El pedido " + cancelado.getCodigo() + " quedó en estado CANCELADO.");
            refrescarTablas();
        } catch (IllegalArgumentException | IllegalStateException e) {
            AlertUtil.mostrarError("Error al cancelar", e.getMessage());
        }
    }

    public void refrescarTablas() {
        listaPedidos.setAll(controller.obtenerPedidos());
        listaEmpleados.setAll(controller.obtenerEmpleados());
        tblPedidos.refresh();
        tblDisponibilidad.refresh();
    }

    private void limpiarFormulario() {
        txtCodigoPedido.clear();
        txtCliente.clear();
        cmbTipoPan.setValue(null);
        txtCantidad.clear();
    }
}