package quindioexpress.modelo;

import java.util.Comparator;

public final class ComparadoresPaquete {

    public static final Comparator<Paquete> POR_PRIORIDAD_DESC =
            Comparator.comparingInt(Paquete::getPrioridad).reversed()
                    .thenComparing(Paquete::getCodigo);

    public static final Comparator<Paquete> POR_PESO_DESC =
            Comparator.comparingDouble(Paquete::getPeso).reversed()
                    .thenComparing(Paquete::getCodigo);

    public static final Comparator<Paquete> POR_TIEMPO_ASC =
            Comparator.comparingInt(Paquete::getTiempoEstimado)
                    .thenComparing(Paquete::getCodigo);

    public static final Comparator<Paquete> POR_DESPACHO =
            Comparator.comparingInt(Paquete::getPrioridad).reversed()
                    .thenComparingInt(Paquete::getTiempoEstimado)
                    .thenComparing(Paquete::getCodigo);

    private ComparadoresPaquete() {
        throw new IllegalStateException("Esta clase de utilidades no se instancia.");
    }

    public static Comparator<Paquete> porPrioridadDesc() {
        return POR_PRIORIDAD_DESC;
    }
    public static Comparator<Paquete> porPesoDesc() {
        return POR_PESO_DESC;
    }
    public static Comparator<Paquete> porTiempoAsc() {
        return POR_TIEMPO_ASC;
    }
    public static Comparator<Paquete> porCriterioDespacho() {
        return POR_DESPACHO;
    }
}
