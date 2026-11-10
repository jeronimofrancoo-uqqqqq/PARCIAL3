package co.edu.uniquindio.panaderia.model;

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