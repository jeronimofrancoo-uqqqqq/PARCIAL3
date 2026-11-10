package co.edu.uniquindio.panaderia.model;

/**
 * Representa un pedido de pan registrado por un cliente.
 */
public class Pedido {
    private final String codigo;
    private final String nombreCliente;
    private final TipoPan tipoPan;
    private final int cantidadUnidades;
    private EstadoPedido estado;
    private String codigoEmpleadoAsignado;
    private int minutosReservados;

    public Pedido(String codigo, String nombreCliente, TipoPan tipoPan, int cantidadUnidades) {
        this.codigo = codigo;
        this.nombreCliente = nombreCliente;
        this.tipoPan = tipoPan;
        this.cantidadUnidades = cantidadUnidades;
        this.estado = EstadoPedido.PENDIENTE;
        this.codigoEmpleadoAsignado = null;
        this.minutosReservados = 0;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public TipoPan getTipoPan() {
        return tipoPan;
    }

    public int getCantidadUnidades() {
        return cantidadUnidades;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public String getCodigoEmpleadoAsignado() {
        return codigoEmpleadoAsignado;
    }

    public int getMinutosReservados() {
        return minutosReservados;
    }

    /**
     * Pasa el pedido de estado PENDIENTE a ASIGNADO, registrando el código del
     * empleado y los minutos reservados.
     */
    public void marcarComoAsignado(String codigoEmpleado, int minutos) {
        if (estado != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo un pedido en estado PENDIENTE puede ser asignado.");
        }
        this.codigoEmpleadoAsignado = codigoEmpleado;
        this.minutosReservados = minutos;
        this.estado = EstadoPedido.ASIGNADO;
    }

    /**
     * Pasa el pedido a estado CANCELADO. Conserva el empleado y minutos reservados
     * como registro histórico.
     */
    public void marcarComoCancelado() {
        if (estado == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("El pedido ya se encuentra CANCELADO.");
        }
        this.estado = EstadoPedido.CANCELADO;
    }

    @Override
    public String toString() {
        return "Pedido " + codigo + " [" + estado + "] - " + nombreCliente + ": " +
                cantidadUnidades + " panes (" + tipoPan + ")" +
                (codigoEmpleadoAsignado != null ? " -> Asignado a: " + codigoEmpleadoAsignado + " (" + minutosReservados + " min)" : "");
    }
}