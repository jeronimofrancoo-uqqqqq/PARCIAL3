package co.edu.uniquindio.panaderia.model;

/**
 * Representa a un cajero de la panadería.
 * Atiende al público en una caja asignada y NO participa en la producción de pan.
 */
public class Cajero extends Empleado {
    private final String cajaAsignada;

    public Cajero(String nombreCompleto, String documento, String codigo, int minutosDisponibles, String cajaAsignada) {
        super(nombreCompleto, documento, codigo, minutosDisponibles);
        if (cajaAsignada == null || cajaAsignada.isBlank()) {
            throw new IllegalArgumentException("La caja asignada no puede ser nula ni vacía.");
        }
        this.cajaAsignada = cajaAsignada.trim();
    }

    public String getCajaAsignada() {
        return cajaAsignada;
    }

    @Override
    public TipoEmpleado getTipoEmpleado() {
        return TipoEmpleado.CAJERO;
    }
}