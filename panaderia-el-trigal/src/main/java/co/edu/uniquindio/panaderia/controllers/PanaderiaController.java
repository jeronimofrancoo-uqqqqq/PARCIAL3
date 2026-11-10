package co.edu.uniquindio.panaderia.controllers;

import co.edu.uniquindio.panaderia.model.Cajero;
import co.edu.uniquindio.panaderia.model.Empleado;
import co.edu.uniquindio.panaderia.model.Panaderia;
import co.edu.uniquindio.panaderia.model.PanaderoArtesanal;
import co.edu.uniquindio.panaderia.model.PanaderoIndustrial;
import co.edu.uniquindio.panaderia.model.Pedido;
import co.edu.uniquindio.panaderia.model.TipoEmpleado;
import co.edu.uniquindio.panaderia.model.TipoPan;

import java.util.List;

/**
 * Controlador de aplicación (Singleton).
 * Actúa como fachada entre las vistas (viewcontrollers) y el modelo de negocio (Panaderia).
 */
public class PanaderiaController {
    private static PanaderiaController instancia;
    private final Panaderia panaderia;

    private PanaderiaController() {
        this.panaderia = new Panaderia();
    }

    public static synchronized PanaderiaController getInstance() {
        if (instancia == null) {
            instancia = new PanaderiaController();
        }
        return instancia;
    }

    /**
     * Instancia la subclase correspondiente según el TipoEmpleado y la registra en la panadería.
     */
    public void registrarEmpleado(TipoEmpleado tipo, String nombreCompleto, String documento,
                                  String codigo, int minutosDisponibles, String cajaAsignada) {
        if (tipo == null) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de empleado.");
        }

        Empleado nuevoEmpleado = switch (tipo) {
            case PANADERO_ARTESANAL -> new PanaderoArtesanal(nombreCompleto, documento, codigo, minutosDisponibles);
            case PANADERO_INDUSTRIAL -> new PanaderoIndustrial(nombreCompleto, documento, codigo, minutosDisponibles);
            case CAJERO -> new Cajero(nombreCompleto, documento, codigo, minutosDisponibles, cajaAsignada);
        };

        panaderia.registrarEmpleado(nuevoEmpleado);
    }

    public Pedido registrarPedido(String codigo, String nombreCliente, TipoPan tipoPan, int cantidadUnidades) {
        return panaderia.registrarPedido(codigo, nombreCliente, tipoPan, cantidadUnidades);
    }

    public Pedido asignarPedido(String codigoPedido) {
        return panaderia.asignarPedido(codigoPedido);
    }

    public Pedido cancelarPedido(String codigoPedido) {
        return panaderia.cancelarPedido(codigoPedido);
    }

    public List<Empleado> obtenerEmpleados() {
        return panaderia.getEmpleados();
    }

    public List<Pedido> obtenerPedidos() {
        return panaderia.getPedidos();
    }

    public Empleado buscarEmpleado(String codigo) {
        return panaderia.buscarEmpleado(codigo);
    }

    public Panaderia getPanaderia() {
        return panaderia;
    }
}