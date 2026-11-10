package co.edu.uniquindio.panaderia.model;

/**
 * Clase abstracta que representa a un empleado que participa directamente en la producción de pan.
 */
public abstract class Panadero extends Empleado {

    protected Panadero(String nombreCompleto, String documento, String codigo, int minutosDisponibles) {
        super(nombreCompleto, documento, codigo, minutosDisponibles);
    }

    /**
     * Calcula los minutos totales requeridos para producir el pedido completo.
     * Este método NO consume ni modifica los minutos del panadero.
     */
    public abstract int calcularTiempoProduccion(Pedido pedido);

    /**
     * Determina si el panadero cuenta con tiempo suficiente para atender el pedido.
     */
    public boolean puedeAtender(Pedido pedido) {
        if (pedido == null) {
            return false;
        }
        return tieneMinutosSuficientes(calcularTiempoProduccion(pedido));
    }
}