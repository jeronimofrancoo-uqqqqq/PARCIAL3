package co.edu.uniquindio.panaderia.model;

public class PanaderoIndustrial extends Panadero {
    public static final int CAPACIDAD_LOTE = 30;
    public static final int MINUTOS_PREPARACION_MAQUINA = 20;

    public PanaderoIndustrial(String nombreCompleto, String documento, String codigo, int minutosDisponibles) {
        super(nombreCompleto, documento, codigo, minutosDisponibles);
    }

    @Override
    public int calcularTiempoProduccion(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo.");
        }
        // División con techo (ceiling) en enteros: (n + k - 1) / k
        int lotes = (pedido.getCantidadUnidades() + CAPACIDAD_LOTE - 1) / CAPACIDAD_LOTE;
        return MINUTOS_PREPARACION_MAQUINA + (lotes * pedido.getTipoPan().getMinutosPorLoteIndustrial());
    }

    @Override
    public TipoEmpleado getTipoEmpleado() {
        return TipoEmpleado.PANADERO_INDUSTRIAL;
    }
}