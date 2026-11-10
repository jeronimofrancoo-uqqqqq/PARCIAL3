package co.edu.uniquindio.panaderia.model;

/**
 * Enumeración que representa los tipos de pan elaborados en la panadería,
 * con los tiempos de producción asociados por bandeja artesanal o lote industrial.
 */
public enum TipoPan {
    TRADICIONAL(30, 25),
    INTEGRAL(40, 35);

    private final int minutosPorBandejaArtesanal;
    private final int minutosPorLoteIndustrial;

    TipoPan(int minutosPorBandejaArtesanal, int minutosPorLoteIndustrial) {
        this.minutosPorBandejaArtesanal = minutosPorBandejaArtesanal;
        this.minutosPorLoteIndustrial = minutosPorLoteIndustrial;
    }

    public int getMinutosPorBandejaArtesanal() {
        return minutosPorBandejaArtesanal;
    }

    public int getMinutosPorLoteIndustrial() {
        return minutosPorLoteIndustrial;
    }
}