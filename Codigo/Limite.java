package Codigo;
import java.time.LocalDate;

public abstract class Limite {
    protected double monto;
    // Fecha que identifica el período del límite (en LimiteMensual solo importan mes y año)
    protected LocalDate fechaReferencia;
    private static final double rojo = 0.95;
    private static final double amarillo = 0.80;

    public Limite(double monto, LocalDate fechaReferencia) {
        if (monto <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        if (fechaReferencia == null)
            throw new IllegalArgumentException("La fecha es obligatoria");

        this.monto = monto;
        this.fechaReferencia = fechaReferencia;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDate getFechaReferencia() {
        return fechaReferencia;
    }

    // +saldo(gastado: double) : double
    public double saldo(double gastado) {
        return monto - gastado;
    }

    // CU6: qué porcentaje del límite ya se gastó
    public double porcentajeConsumido(double gastado) {
        return gastado * 100 / monto;
    }

    // +estado(gastado: double) : Semaforo
    public Semaforo estado(double gastado) {
        if (gastado >= monto * rojo) {
            return Semaforo.ROJO;
        } else if (gastado >= monto * amarillo) {
            return Semaforo.AMARILLO;
        }
        return Semaforo.VERDE;
    }

    // Para ver si el consumo aplica a ese limite (CU1, CU2, CU6, CU12)
    public abstract boolean aplica(Consumo c);
    // Para ver si dos límites son del mismo período (CU5: definir y modificar)
    public abstract boolean mismoPeriodo(Limite otro);
    // Para ver si una fecha cae dentro del período del límite (CU6: consultar saldo)
    public abstract boolean incluye(LocalDate fecha);
}