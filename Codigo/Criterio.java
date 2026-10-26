package Codigo;
 
// Filtro que decide si un consumo entra o no en un reporte
public interface Criterio {
    boolean cumple(Consumo c);
}
 