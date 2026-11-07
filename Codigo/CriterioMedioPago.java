package Codigo;

// CU4: filtra los consumos pagados con un medio de pago determinado
public class CriterioMedioPago implements Criterio {
    private final MedioPago medioDePago;

    public CriterioMedioPago(MedioPago medioDePago) {
        if (medioDePago == null)
            throw new IllegalArgumentException("El medio de pago es obligatorio");
        this.medioDePago = medioDePago;
    }

    @Override
    public boolean cumple(Consumo c) {
        return c.getMedioDePago().getId() == medioDePago.getId();
    }
}
