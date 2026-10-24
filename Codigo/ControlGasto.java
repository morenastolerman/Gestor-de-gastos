package Codigo;

import java.util.ArrayList;
import java.util.List;

public class ControlGasto {
    private Repositorio repositorio;
    private List<Limite> limites = new ArrayList<>();
    private Controller controller;

    
    public ControlGasto(Repositorio repositorio, Controller controller) {
        this.repositorio = repositorio;
        this.controller = controller;
    }


    public void agregarLimite(Limite l){
        limites.add(l);
    }

    public boolean registrar(Consumo c){
        repositorio.guardar(c);
        //limite.estado(c.getMonto());
      for (Limite l : limites) {
            if (!l.aplica(c)) continue;          // solo los límites que corresponden a este consumo

            double gastado = calcularGastado(l);
            Semaforo estado = l.estado(gastado);

            switch (estado) {
                case ROJO:
                    notificar(estado, "Límite excedido (ROJO)");
                    break;
                case AMARILLO:
                    notificar(estado, "Advertencia (AMARILLO)");
                    break;
                case VERDE:
                    notificar(estado, "Estado OK (VERDE)");
                    break;
            }
        }
        return true;
    }

    // suma los consumos guardados que caen dentro del límite
    private double calcularGastado(Limite l){
        double total = 0;
        for (Consumo c : repositorio.listar()) {
            if (l.aplica(c)) total += c.getMonto();
        }
        return total;
    }

    private void notificar(Semaforo estado, String mensaje){
        controller.actualizar(estado, mensaje);
    }

}
