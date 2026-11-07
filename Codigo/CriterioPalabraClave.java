package Codigo;

// CU4: filtra los consumos cuya descripción contiene una palabra clave o un hashtag (ej.: "super" o "#viaje").
// No distingue mayúsculas de minúsculas.
public class CriterioPalabraClave implements Criterio {
    private final String palabraClave;

    public CriterioPalabraClave(String palabraClave) {
        if (palabraClave == null || palabraClave.isBlank())
            throw new IllegalArgumentException("La palabra clave no puede estar vacía");
        this.palabraClave = palabraClave.trim().toLowerCase();
    }

    @Override
    public boolean cumple(Consumo c) {
        return c.getDescripcion() != null && c.getDescripcion().toLowerCase().contains(palabraClave);
    }
}
