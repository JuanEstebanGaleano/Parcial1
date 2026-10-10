package quindioexpress.modelo;

import java.util.Locale;
import java.util.Objects;

public class Paquete implements Comparable<Paquete> {

    public static final int PRIORIDAD_POR_DEFECTO = 5;
    public static final int PRIORIDAD_MINIMA = 1;
    public static final int PRIORIDAD_MAXIMA = 5;

    private final String codigo;
    private final String destino;
    private final double peso;
    private final int prioridad;
    private final int tiempoEstimado;

    public Paquete(String codigo, String destino, double peso, int tiempoEstimado) {
        this(codigo, destino, peso, PRIORIDAD_POR_DEFECTO, tiempoEstimado);
    }
    public Paquete(String codigo, String destino, double peso, int prioridad, int tiempoEstimado) {
        this.codigo = validarTexto(codigo, "El código del paquete no puede ser nulo ni vacío.");
        this.destino = validarTexto(destino, "El destino no puede ser nulo ni vacío.");
        this.peso = validarPeso(peso);
        this.prioridad = validarPrioridad(prioridad);
        this.tiempoEstimado = validarTiempo(tiempoEstimado);
    }
    public String getCodigo() {
        return codigo;
    }
    public String getDestino() {
        return destino;
    }
    public double getPeso() {
        return peso;
    }
    public int getPrioridad() {
        return prioridad;
    }
    public int getTiempoEstimado() {
        return tiempoEstimado;
    }
    @Override
    public int compareTo(Paquete otro) {
        Objects.requireNonNull(otro, "El paquete de comparación no puede ser nulo.");
        return this.codigo.compareTo(otro.codigo);
    }
    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Paquete)) {
            return false;
        }
        Paquete otro = (Paquete) objeto;
        return codigo.equals(otro.codigo);
    }
    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "Paquete{codigo='%s', destino='%s', peso=%.2f kg, prioridad=%d, tiempoEstimado=%d min}",
                codigo, destino, peso, prioridad, tiempoEstimado);
    }
    private static double validarPeso(double peso) {
        if (!Double.isFinite(peso) || peso <= 0.0) {
            throw new IllegalArgumentException("El peso debe ser un número finito mayor que cero.");
        }
        return peso;
    }
    private static int validarPrioridad(int prioridad) {
        if (prioridad < PRIORIDAD_MINIMA || prioridad > PRIORIDAD_MAXIMA) {
            throw new IllegalArgumentException("La prioridad debe estar entre 1 y 5, inclusive.");
        }
        return prioridad;
    }
    private static int validarTiempo(int tiempoEstimado) {
        if (tiempoEstimado <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser un entero mayor que cero.");
        }
        return tiempoEstimado;
    }
    private static String validarTexto(String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor.trim();
    }
}
