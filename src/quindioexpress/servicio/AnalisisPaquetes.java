package quindioexpress.servicio;

import quindioexpress.modelo.Paquete;

import java.util.List;

/**
 * Algoritmos de análisis sobre paquetes: recursividad, búsqueda binaria y divide y vencerás.
 * No guarda estado; recibe los datos como parámetros.
 */
public final class AnalisisPaquetes {

    private AnalisisPaquetes() {
        throw new IllegalStateException("Esta clase de utilidades no se instancia.");
    }

    // ---------------------------------------------------------------- Recursividad

    /**
     * Suma recursiva de pesos (sin ciclos). Caso base: índice igual al tamaño -> 0.
     * Caso recursivo: peso actual + suma del resto.
     */
    public static double sumarPesos(List<Paquete> paquetes, int indice) {
        if (indice >= paquetes.size()) {
            return 0.0;
        }
        return paquetes.get(indice).getPeso() + sumarPesos(paquetes, indice + 1);
    }

    /**
     * Conteo recursivo de paquetes con prioridad mayor o igual al mínimo (sin ciclos).
     * Caso base: índice igual al tamaño -> 0.
     */
    public static int contarConPrioridadMinima(List<Paquete> paquetes, int indice, int prioridadMinima) {
        if (indice >= paquetes.size()) {
            return 0;
        }
        int coincidencia = paquetes.get(indice).getPrioridad() >= prioridadMinima ? 1 : 0;
        return coincidencia + contarConPrioridadMinima(paquetes, indice + 1, prioridadMinima);
    }

    // ---------------------------------------------------------------- Divide y vencerás

    /**
     * Mayor peso por divide y vencerás sobre el segmento [inicio, fin] (inclusive).
     * Caso base: un elemento. División: mitades. Combinación: el de mayor peso.
     */
    public static Paquete mayorPeso(Paquete[] paquetes, int inicio, int fin) {
        if (inicio == fin) {
            return paquetes[inicio];
        }
        int medio = inicio + (fin - inicio) / 2;
        Paquete mayorIzquierdo = mayorPeso(paquetes, inicio, medio);
        Paquete mayorDerecho = mayorPeso(paquetes, medio + 1, fin);
        return mayorIzquierdo.getPeso() >= mayorDerecho.getPeso() ? mayorIzquierdo : mayorDerecho;
    }

    // ---------------------------------------------------------------- Búsqueda binaria

    /**
     * Búsqueda binaria por código. Precondición: el arreglo está ordenado por código ascendente
     * (orden natural de Paquete) y no contiene nulos. Costo O(log n).
     *
     * @return paquete encontrado o {@code null}
     */
    public static Paquete buscarBinariaPorCodigo(Paquete[] ordenados, String codigo) {
        if (ordenados == null) {
            throw new IllegalArgumentException("El arreglo ordenado no puede ser nulo.");
        }
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }
        String buscado = codigo.trim();
        int izquierda = 0;
        int derecha = ordenados.length - 1;
        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;
            if (ordenados[medio] == null) {
                throw new IllegalArgumentException("El arreglo ordenado no puede contener elementos nulos.");
            }
            int comparacion = ordenados[medio].getCodigo().compareTo(buscado);
            if (comparacion == 0) {
                return ordenados[medio];
            }
            if (comparacion < 0) {
                izquierda = medio + 1;
            } else {
                derecha = medio - 1;
            }
        }
        return null;
    }
}
