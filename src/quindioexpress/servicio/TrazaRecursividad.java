package quindioexpress.servicio;

import quindioexpress.modelo.Paquete;

import java.util.List;


public final class TrazaRecursividad {

    private TrazaRecursividad() {
        throw new IllegalStateException("Esta clase no se instancia.");
    }
    public static double sumarPesos(List<Paquete> paquetes, int indice, int profundidad) {
        System.out.println(sangria(profundidad) + "-> sumarPesos(indice=" + indice + ")");
        double resultado;
        if (indice >= paquetes.size()) {
            System.out.println(sangria(profundidad) + "   CASO BASE");
            resultado = 0.0;
        } else {
            double peso = paquetes.get(indice).getPeso();
            resultado = peso + sumarPesos(paquetes, indice + 1, profundidad + 1);
        }
        System.out.println(sangria(profundidad) + "<- retorna " + resultado + " (indice=" + indice + ")");
        return resultado;
    }

    public static int contarPrioridad(List<Paquete> paquetes, int indice, int minima, int profundidad) {
        System.out.println(sangria(profundidad) + "-> contarPrioridad(indice=" + indice
                + ", minima=" + minima + ")");
        int resultado;
        if (indice >= paquetes.size()) {
            System.out.println(sangria(profundidad) + "   CASO BASE");
            resultado = 0;
        } else {
            int suma = paquetes.get(indice).getPrioridad() >= minima ? 1 : 0;
            resultado = suma + contarPrioridad(paquetes, indice + 1, minima, profundidad + 1);
        }
        System.out.println(sangria(profundidad) + "<- retorna " + resultado + " (indice=" + indice + ")");
        return resultado;
    }

    public static double mayorPeso(double[] pesos, int inicio, int fin, int profundidad) {
        System.out.println(sangria(profundidad) + "-> mayorPeso(inicio=" + inicio + ", fin=" + fin + ")");
        double resultado;
        if (inicio == fin) {
            System.out.println(sangria(profundidad) + "   CASO BASE: " + pesos[inicio]);
            resultado = pesos[inicio];
        } else {
            int medio = inicio + (fin - inicio) / 2;
            System.out.println(sangria(profundidad) + "   DIVIDE: izquierda=[" + inicio + "," + medio
                    + "] derecha=[" + (medio + 1) + "," + fin + "]");
            double izquierdo = mayorPeso(pesos, inicio, medio, profundidad + 1);
            double derecho = mayorPeso(pesos, medio + 1, fin, profundidad + 1);
            resultado = Math.max(izquierdo, derecho);
            System.out.println(sangria(profundidad) + "   COMBINA: max(" + izquierdo + ", " + derecho + ")");
        }
        System.out.println(sangria(profundidad) + "<- retorna " + resultado + " ([" + inicio + "," + fin + "])");
        return resultado;
    }

    private static String sangria(int profundidad) {
        return profundidad == 0 ? "" : "  " + sangria(profundidad - 1);
    }
}
