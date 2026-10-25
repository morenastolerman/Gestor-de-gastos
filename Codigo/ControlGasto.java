package Codigo;

import java.util.ArrayList;
import java.util.List;

public class ControlGasto {
    private Repositorio repositorio;
    private List<Limite> limites = new ArrayList<>();

    
    public ControlGasto(Repositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void agregarLimite(Limite l) {
        if (l == null)
            throw new IllegalArgumentException("El límite no puede ser null");
        for (Limite existente : limites) {
            if (existente.mismoPeriodo(l))
                throw new IllegalStateException("Ya existe un límite para ese período");
        }
        limites.add(l);
    }


   public List<String> registrar(Consumo c) {
        repositorio.guardar(c);
        List<String> alertas = new ArrayList<>();

        for (Limite l : limites) {
            if (!l.aplica(c)) continue; // solo los límites que corresponden a este consumo

            Semaforo estado = l.estado(calcularGastado(l));
            if (estado == Semaforo.ROJO) {
                alertas.add("ALERTA: Límite excedido (ROJO)");
            } else if (estado == Semaforo.AMARILLO) {
                alertas.add("ADVERTENCIA: Cerca del límite (AMARILLO)");
            }
        }
        return alertas;
    }

    // suma los consumos guardados que caen dentro del límite
    private double calcularGastado(Limite l) {
        double total = 0;
        for (Consumo c : repositorio.listar()) {
            if (l.aplica(c)) total += c.getMonto();
        }
        return total;
    }
}