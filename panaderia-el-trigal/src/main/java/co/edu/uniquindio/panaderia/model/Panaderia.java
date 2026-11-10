package co.edu.uniquindio.panaderia.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidad principal y fachada del modelo de negocio de la panadería.
 * Administra la lista de empleados y el historial completo de pedidos.
 */
public class Panaderia {
    private final String nombre;
    private final List<Empleado> empleados;
    private final List<Pedido> pedidos;

    public Panaderia() {
        this("Panadería El Trigal");
    }

    public Panaderia(String nombre) {
        this.nombre = (nombre == null || nombre.isBlank()) ? "Panadería El Trigal" : nombre.trim();
        this.empleados = new ArrayList<>();
        this.pedidos = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Retorna una vista inmodificable de la lista de empleados en orden de registro.
     */
    public List<Empleado> getEmpleados() {
        return Collections.unmodifiableList(empleados);
    }

    /**
     * Retorna una vista inmodificable del historial de pedidos (incluye cancelados).
     */
    public List<Pedido> getPedidos() {
        return Collections.unmodifiableList(pedidos);
    }

    /**
     * Registra un nuevo empleado en la panadería en orden de llegada.
     * @throws IllegalArgumentException si el empleado es nulo o su código ya existe.
     */
    public void registrarEmpleado(Empleado empleado) {
        if (empleado == null) {
            throw new IllegalArgumentException("El empleado no puede ser nulo.");
        }
        if (buscarEmpleado(empleado.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe un empleado con el código: " + empleado.getCodigo());
        }
        empleados.add(empleado);
    }

    /**
     * Registra un nuevo pedido en estado PENDIENTE.
     * NO reserva minutos a ningún empleado.
     * @throws IllegalArgumentException si los datos son inválidos, cantidad <= 0 o el código ya existe en el historial.
     */
    public Pedido registrarPedido(String codigo, String nombreCliente, TipoPan tipoPan, int cantidadUnidades) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del pedido no puede ser nulo ni vacío.");
        }
        if (nombreCliente == null || nombreCliente.isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente no puede ser nulo ni vacío.");
        }
        if (tipoPan == null) {
            throw new IllegalArgumentException("El tipo de pan no puede ser nulo.");
        }
        if (cantidadUnidades <= 0) {
            throw new IllegalArgumentException("La cantidad de panes debe ser mayor que cero.");
        }
        if (existePedido(codigo.trim())) {
            throw new IllegalArgumentException("Ya existe un pedido con el código: " + codigo.trim());
        }

        Pedido pedido = new Pedido(codigo.trim(), nombreCliente.trim(), tipoPan, cantidadUnidades);
        pedidos.add(pedido);
        return pedido;
    }

    /**
     * Asigna un pedido en estado PENDIENTE al panadero más rápido con minutos suficientes.
     * En caso de empate en tiempo, selecciona al primero según el orden de registro.
     * @throws IllegalArgumentException si el código no existe.
     * @throws IllegalStateException si el pedido no es PENDIENTE o ningún panadero tiene tiempo.
     */
    public Pedido asignarPedido(String codigoPedido) {
        Pedido pedido = obtenerPedidoOLanzar(codigoPedido);

        if (pedido.getEstado() == EstadoPedido.ASIGNADO) {
            throw new IllegalStateException("El pedido ya se encuentra asignado.");
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("Un pedido cancelado no puede ser asignado.");
        }

        Panadero elegido = seleccionarPanaderoMasRapido(pedido);
        if (elegido == null) {
            throw new IllegalStateException("Ningún panadero cuenta con tiempo suficiente para atender este pedido.");
        }

        int minutos = elegido.calcularTiempoProduccion(pedido);
        elegido.descontarMinutos(minutos);
        pedido.marcarComoAsignado(elegido.getCodigo(), minutos);
        return pedido;
    }

    /**
     * Cancela un pedido existente.
     * Si era ASIGNADO, devuelve los minutos reservados al empleado asignado.
     * Si era PENDIENTE, no altera los minutos.
     * Si ya estaba CANCELADO, lanza excepción.
     * El pedido permanece en el historial en todos los casos.
     * @throws IllegalArgumentException si el pedido no existe.
     * @throws IllegalStateException si el pedido ya está cancelado.
     */
    public Pedido cancelarPedido(String codigoPedido) {
        Pedido pedido = obtenerPedidoOLanzar(codigoPedido);

        switch (pedido.getEstado()) {
            case PENDIENTE -> pedido.marcarComoCancelado();
            case ASIGNADO -> {
                Empleado empleado = buscarEmpleado(pedido.getCodigoEmpleadoAsignado());
                if (empleado == null) {
                    throw new IllegalStateException("Inconsistencia: el empleado asignado no fue encontrado.");
                }
                empleado.devolverMinutos(pedido.getMinutosReservados());
                pedido.marcarComoCancelado();
            }
            case CANCELADO -> throw new IllegalStateException("El pedido ya se encuentra CANCELADO.");
        }

        return pedido;
    }

    /**
     * Busca un pedido por su código en el historial completo.
     * @return el Pedido correspondiente o null si no existe.
     */
    public Pedido buscarPedido(String codigo) {
        if (codigo == null) return null;
        for (Pedido p : pedidos) {
            if (p.getCodigo().equals(codigo.trim())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Busca un empleado por su código en la lista.
     * @return el Empleado correspondiente o null si no existe.
     */
    public Empleado buscarEmpleado(String codigo) {
        if (codigo == null) return null;
        for (Empleado e : empleados) {
            if (e.getCodigo().equals(codigo.trim())) {
                return e;
            }
        }
        return null;
    }

    // --- Métodos Privados Auxiliares ---

    private boolean existePedido(String codigo) {
        return buscarPedido(codigo) != null;
    }

    private Pedido obtenerPedidoOLanzar(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del pedido no puede ser nulo ni vacío.");
        }
        Pedido pedido = buscarPedido(codigo);
        if (pedido == null) {
            throw new IllegalArgumentException("No existe ningún pedido con el código: " + codigo.trim());
        }
        return pedido;
    }

    private Panadero seleccionarPanaderoMasRapido(Pedido pedido) {
        Panadero mejor = null;
        int mejorTiempo = Integer.MAX_VALUE;

        // Recorre la lista de empleados en el orden exacto de registro
        for (Empleado e : empleados) {
            if (e instanceof Panadero p) {
                int t = p.calcularTiempoProduccion(pedido);
                // Comparación estricta (<) para desempatar a favor del primero registrado
                if (p.tieneMinutosSuficientes(t) && t < mejorTiempo) {
                    mejor = p;
                    mejorTiempo = t;
                }
            }
        }

        return mejor;
    }
}