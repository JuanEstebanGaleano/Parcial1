package quindioexpress.servicio;

import quindioexpress.modelo.Paquete;

import java.util.List;

public final class AnalisisPaquetes {

    private AnalisisPaquetes() {
        throw new IllegalStateException("Esta clase no se instancia.");
    }

    // Recursividad

    public static double sumarPesos(List<Paquete> paquetes, int indice) {
        if (indice >= paquetes.size()) {
            return 0.0;
        }
        return paquetes.get(indice).getPeso() + sumarPesos(paquetes, indice + 1);
    }


    public static int contarConPrioridadMinima(List<Paquete> paquetes, int indice, int prioridadMinima) {
        if (indice >= paquetes.size()) {
            return 0;
        }
        int coincidencia = paquetes.get(indice).getPrioridad() >= prioridadMinima ? 1 : 0;
        return coincidencia + contarConPrioridadMinima(paquetes, indice + 1, prioridadMinima);
    }

    // Divide y vencerás


    public static Paquete mayorPeso(Paquete[] paquetes, int inicio, int fin) {
        if (inicio == fin) {
            return paquetes[inicio];
        }
        int medio = inicio + (fin - inicio) / 2;
        Paquete mayorIzquierdo = mayorPeso(paquetes, inicio, medio);
        Paquete mayorDerecho = mayorPeso(paquetes, medio + 1, fin);
        return mayorIzquierdo.getPeso() >= mayorDerecho.getPeso() ? mayorIzquierdo : mayorDerecho;
    }

    // Búsqueda binaria


    public static Paquete buscarBinariaPorCodigo(Paquete[] ordenados, String codigo) {
        if (ordenados == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
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
                throw new IllegalArgumentException("El arreglo no puede contener elementos nulos.");
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
