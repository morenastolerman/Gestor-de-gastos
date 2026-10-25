package Codigo;

import java.util.ArrayList;
import java.util.List;

public class ControlGasto {
    private Repositorio repositorio;
    private Limite limite;

    
    public ControlGasto(Repositorio repositorio) {
        this.repositorio = repositorio;
    }


    public void definirLimite(Limite l){
        if (l == null)
            throw new IllegalArgumentException("El límite no puede ser null");
        this.limite = l;
    }

    public List<String> registrar(Consumo c){
        repositorio.guardar(c);
        List<String> alertas = new ArrayList<>();
        if (limite != null && limite.aplica(c)) {
            Semaforo estado = limite.estado(calcularGastado());
            if (estado == Semaforo.ROJO) {
                alertas.add("ALERTA: Límite excedido (ROJO)");
            } else if (estado == Semaforo.AMARILLO) {
                alertas.add("ADVERTENCIA: Cerca del límite (AMARILLO)");
            }
        }
        return alertas;
      
    }

    // suma los consumos guardados que caen dentro del límite
    private double calcularGastado(){
        double total = 0;
        for (Consumo c : repositorio.listar()) {
            if (limite.aplica(c)) total += c.getMonto();
        }
        return total;
    }

}
