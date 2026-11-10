package co.edu.uniquindio.panaderia.model;

/**
 * Enumeración auxiliar para el tipo de empleado, empleada para polimorfismo,
 * visualización en tablas y selección en la interfaz gráfica.
 */
public enum TipoEmpleado {
    PANADERO_ARTESANAL("Panadero artesanal"),
    PANADERO_INDUSTRIAL("Panadero industrial"),
    CAJERO("Cajero");

    private final String etiqueta;

    TipoEmpleado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}