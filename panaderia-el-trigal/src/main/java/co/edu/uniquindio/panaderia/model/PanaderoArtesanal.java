package co.edu.uniquindio.panaderia.model;

/**
 * Representa a un panadero artesanal.
 * Utiliza bandejas con capacidad de hasta 12 panes.
 * Una bandeja incompleta toma el mismo tiempo que una llena.
 */
public class PanaderoArtesanal extends Panadero {
    public static final int CAPACIDAD_BANDEJA = 12;

    public PanaderoArtesanal(String nombreCompleto, String documento, String codigo, int minutosDisponibles) {
        super(nombreCompleto, documento, codigo, minutosDisponibles);
    }

    @Override
    public int calcularTiempoProduccion(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo.");
        }
        // División con techo (ceiling) en enteros: (n + k - 1) / k
        int bandejas = (pedido.getCantidadUnidades() + CAPACIDAD_BANDEJA - 1) / CAPACIDAD_BANDEJA;
        return bandejas * pedido.getTipoPan().getMinutosPorBandejaArtesanal();
    }

    @Override
    public TipoEmpleado getTipoEmpleado() {
        return TipoEmpleado.PANADERO_ARTESANAL;
    }
}