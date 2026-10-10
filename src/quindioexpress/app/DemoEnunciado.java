package quindioexpress.app;

import quindioexpress.modelo.Paquete;
import quindioexpress.modelo.Repartidor;
import quindioexpress.servicio.CentroDistribucionServicio;
import quindioexpress.servicio.TrazaRecursividad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class DemoEnunciado {

    private DemoEnunciado() {
        throw new IllegalStateException("Clase de utilidades.");
    }

    public static void demostrarEjemplosDelEnunciado() {
        System.out.println("\n--- Ejemplos del enunciado ---");

        CentroDistribucionServicio despacho = new CentroDistribucionServicio();
        despacho.registrarPaquete(new Paquete("PQ420", "Armenia", 2.0, 3, 30));
        despacho.registrarPaquete(new Paquete("PQ731", "Armenia", 2.0, 5, 45));
        despacho.registrarPaquete(new Paquete("PQ105", "Armenia", 2.0, 5, 25));
        despacho.registrarPaquete(new Paquete("PQ942", "Armenia", 2.0, 4, 20));
        despacho.registrarRepartidor(new Repartidor("RX", "Repartidor Ejemplo", "Armenia", true));
        StringBuilder orden = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            Paquete p = despacho.asignarSiguientePorPrioridad("RX");
            orden.append(p.getCodigo()).append(i < 3 ? ", " : "");
            despacho.registrarEntrega(p.getCodigo(), "RX");
        }
        System.out.println("Orden de despacho (esperado PQ105, PQ731, PQ942, PQ420): " + orden);
        System.out.println("Historial propio: " + despacho.obtenerHistorialEntregas());

        CentroDistribucionServicio pesos = new CentroDistribucionServicio();
        pesos.registrarPaquete(new Paquete("PQ101", "Armenia", 4.5, 3, 10));
        pesos.registrarPaquete(new Paquete("PQ203", "Salento", 3.0, 3, 10));
        pesos.registrarPaquete(new Paquete("PQ415", "Armenia", 7.2, 3, 10));
        pesos.registrarPaquete(new Paquete("PQ730", "Armenia", 2.3, 3, 10));
        System.out.printf("Peso total Armenia (esperado 14.0): %.1f kg%n",
                pesos.calcularPesoTotalPorMunicipio("Armenia"));

        CentroDistribucionServicio prioridades = new CentroDistribucionServicio();
        int[] valores = {5, 2, 4, 1, 5, 3, 4};
        for (int i = 0; i < valores.length; i++) {
            prioridades.registrarPaquete(new Paquete("PQ" + (100 + i * 7), "Armenia", 1.0, valores[i], 10));
        }
        System.out.println("Prioridad >= 4 con 5,2,4,1,5,3,4 (esperado 4): "
                + prioridades.contarPaquetesConPrioridadMinima(4));

        CentroDistribucionServicio mayor = new CentroDistribucionServicio();
        double[] kilos = {4.5, 12.0, 3.2, 18.7, 7.4, 5.1};
        for (int i = 0; i < kilos.length; i++) {
            mayor.registrarPaquete(new Paquete("PQ" + (200 + i * 11), "Armenia", kilos[i], 3, 10));
        }
        System.out.println("Mayor peso (esperado 18.7): "
                + mayor.obtenerPaqueteMayorPesoDivideYVenceras().get().getPeso());

        CentroDistribucionServicio ejemplo = new CentroDistribucionServicio();
        String[][] datos = {{"PQ731", "Armenia"}, {"PQ105", "Salento"}, {"PQ942", "Armenia"},
                {"PQ318", "Calarcá"}, {"PQ567", "Salento"}, {"PQ829", "Armenia"}};
        for (String[] d : datos) {
            ejemplo.registrarPaquete(new Paquete(d[0], d[1], 1.0, 3, 10));
        }
        System.out.println("Municipios (esperado Armenia, Calarcá, Salento): "
                + ejemplo.obtenerMunicipiosOrdenados());
        List<String> naturales = new ArrayList<>();
        for (Paquete p : ejemplo.obtenerArregloOrdenadoPorCodigo()) {
            naturales.add(p.getCodigo());
        }
        System.out.println("Orden natural por código (esperado PQ105, PQ318, PQ567, PQ731, PQ829, PQ942): "
                + naturales);
    }

    public static void demostrarTrazasRecursivas() {
        System.out.println("\n--- Trazas de pila de llamadas (conjunto reducido) ---");
        List<Paquete> reducido = new ArrayList<>();
        reducido.add(new Paquete("PQ101", "Armenia", 4.5, 5, 10));
        reducido.add(new Paquete("PQ415", "Armenia", 7.2, 2, 10));
        reducido.add(new Paquete("PQ730", "Armenia", 2.3, 4, 10));
        System.out.println("[Suma recursiva de pesos: 4.5, 7.2, 2.3]");
        TrazaRecursividad.sumarPesos(reducido, 0, 0);
        System.out.println("[Conteo recursivo prioridad >= 4: 5, 2, 4]");
        TrazaRecursividad.contarPrioridad(reducido, 0, 4, 0);
        System.out.println("[Divide y vencerás, mayor peso: 4.5, 12.0, 3.2, 18.7]");
        TrazaRecursividad.mayorPeso(new double[] {4.5, 12.0, 3.2, 18.7}, 0, 3, 0);
    }
}
