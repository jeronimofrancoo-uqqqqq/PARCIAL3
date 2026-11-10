package co.edu.uniquindio.panaderia.model;

public abstract class Panadero extends Empleado {

    protected Panadero(String nombreCompleto, String documento, String codigo, int minutosDisponibles) {
        super(nombreCompleto, documento, codigo, minutosDisponibles);
    }
    public abstract int calcularTiempoProduccion(Pedido pedido);
    public boolean puedeAtender(Pedido pedido) {
        if (pedido == null) {
            return false;
        }
        return tieneMinutosSuficientes(calcularTiempoProduccion(pedido));
    }
}