package quindioexpress.app;

import quindioexpress.estructuras.ListaEnlazada;
import quindioexpress.modelo.ComparadoresPaquete;
import quindioexpress.modelo.Paquete;
import quindioexpress.servicio.CentroDistribucionServicio;

import java.util.Arrays;
import java.util.List;

public final class DemoConsultas {

    private DemoConsultas() {
        throw new IllegalStateException("Clase de utilidades.");
    }

    public static void demostrarConsultasYMunicipios(CentroDistribucionServicio servicio) {
        System.out.println("\n--- Consulta por código: registro HashMap ---");
        System.out.println("PQ731: " + servicio.buscarPaquete("PQ731").orElse(null));
        System.out.println("PQ999: " + servicio.buscarPaquete("PQ999").orElse(null));

        System.out.println("\n--- Municipios y agrupación ---");
        System.out.println("Municipios sin duplicados (HashSet): " + servicio.obtenerMunicipiosSinDuplicados());
        System.out.println("Municipios alfabéticos (TreeSet): " + servicio.obtenerMunicipiosOrdenados());
        System.out.println("Paquetes para Armenia: " + servicio.obtenerPaquetesPorMunicipio("Armenia"));
        System.out.println("Siguiente paquete según llegada: "
                + servicio.obtenerSiguientePorLlegada().orElse(null));
        System.out.println("Siguiente paquete según prioridad: "
                + servicio.obtenerSiguientePorPrioridad().orElse(null));
    }
    public static void demostrarOrdenamientosYBusqueda(CentroDistribucionServicio servicio) {
        System.out.println("\n--- Ordenamientos alternativos (sin alterar la llegada) ---");
        imprimirPrimeros("Prioridad descendente", servicio.ordenarPaquetes(
                ComparadoresPaquete.porPrioridadDesc()), 5);
        imprimirPrimeros("Peso descendente", servicio.ordenarPaquetes(
                ComparadoresPaquete.porPesoDesc()), 5);
        imprimirPrimeros("Tiempo ascendente", servicio.ordenarPaquetes(
                ComparadoresPaquete.porTiempoAsc()), 5);

        Paquete[] ordenados = servicio.obtenerArregloOrdenadoPorCodigo();
        System.out.println("\nPrimeros códigos del arreglo natural: "
                + Arrays.toString(Arrays.copyOf(ordenados, Math.min(8, ordenados.length))));
        System.out.println("Búsqueda binaria PQ731: "
                + servicio.buscarBinariaPorCodigo(ordenados, "PQ731"));
        System.out.println("Búsqueda binaria PQ999: "
                + servicio.buscarBinariaPorCodigo(ordenados, "PQ999"));
    }
    public static void demostrarAlgoritmos(CentroDistribucionServicio servicio) {
        System.out.println("\n--- Recursividad y divide y vencerás ---");
        System.out.printf("Peso total para Armenia: %.2f kg%n",
                servicio.calcularPesoTotalPorMunicipio("Armenia"));
        System.out.println("Paquetes con prioridad mínima 4: "
                + servicio.contarPaquetesConPrioridadMinima(4));
        System.out.println("Paquete de mayor peso (divide y vencerás): "
                + servicio.obtenerPaqueteMayorPesoDivideYVenceras().orElse(null));
    }
    public static void demostrarListaPropia() {
        System.out.println("\n--- Prueba de la lista simplemente enlazada propia ---");
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        System.out.println("Vacía al inicio: " + lista.estaVacia());
        lista.agregar("inicio");
        lista.agregar("centro");
        lista.agregar("fin");
        System.out.println("Contenido con Iterator propio:");
        for (String valor : lista) {
            System.out.println("  " + valor);
        }
        System.out.println("Elemento de índice 1: " + lista.obtener(1));
        System.out.println("Eliminar inicio: " + lista.eliminar("inicio"));
        System.out.println("Eliminar elemento intermedio: " + lista.eliminar("centro"));
        System.out.println("Eliminar fin: " + lista.eliminar("fin"));
        System.out.println("Eliminar inexistente: " + lista.eliminar("no existe"));
        lista.agregar("único");
        System.out.println("Lista con un elemento: tamaño=" + lista.tamano() + ", valor=" + lista.obtener(0));
        lista.limpiar();
        System.out.println("Vacía después de limpiar: " + lista.estaVacia());
    }
    private static void imprimirPrimeros(String titulo, List<Paquete> paquetes, int limite) {
        System.out.println(titulo + ":");
        int cantidad = Math.min(limite, paquetes.size());
        for (int i = 0; i < cantidad; i++) {
            System.out.println("  " + paquetes.get(i));
        }
    }
}
