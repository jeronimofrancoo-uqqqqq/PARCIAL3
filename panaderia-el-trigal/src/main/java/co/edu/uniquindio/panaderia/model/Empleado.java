package co.edu.uniquindio.panaderia.model;

/**
 * Clase base abstracta que representa a un empleado de la panadería.
 */
public abstract class Empleado {
    private final String nombreCompleto;
    private final String documento;
    private final String codigo;
    private int minutosDisponibles;

    protected Empleado(String nombreCompleto, String documento, String codigo, int minutosDisponibles) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new IllegalArgumentException("El nombre completo no puede ser nulo ni vacío.");
        }
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("El documento no puede ser nulo ni vacío.");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código no puede ser nulo ni vacío.");
        }
        if (minutosDisponibles < 0) {
            throw new IllegalArgumentException("Los minutos disponibles no pueden ser negativos.");
        }
        this.nombreCompleto = nombreCompleto.trim();
        this.documento = documento.trim();
        this.codigo = codigo.trim();
        this.minutosDisponibles = minutosDisponibles;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getDocumento() {
        return documento;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getMinutosDisponibles() {
        return minutosDisponibles;
    }

    public boolean tieneMinutosSuficientes(int minutos) {
        return minutosDisponibles >= minutos;
    }

    public void descontarMinutos(int minutos) {
        if (minutos < 0) {
            throw new IllegalArgumentException("No se pueden descontar minutos negativos.");
        }
        if (minutos > minutosDisponibles) {
            throw new IllegalStateException("Minutos insuficientes para realizar el descuento.");
        }
        this.minutosDisponibles -= minutos;
    }

    public void devolverMinutos(int minutos) {
        if (minutos < 0) {
            throw new IllegalArgumentException("No se pueden devolver minutos negativos.");
        }
        this.minutosDisponibles += minutos;
    }

    public abstract TipoEmpleado getTipoEmpleado();

    @Override
    public String toString() {
        return codigo + " - " + nombreCompleto + " (" + minutosDisponibles + " min)";
    }
}