package quindioexpress.pruebas;

import quindioexpress.estructuras.MiListaEnlazada;
import quindioexpress.modelo.ComparadoresPaquete;
import quindioexpress.modelo.Paquete;
import quindioexpress.modelo.Repartidor;
import quindioexpress.servicio.CentroDistribucionServicio;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Pruebas automáticas sin bibliotecas externas para verificar los requisitos principales
 * del parcial. Ejecutar con: java PruebasParcial
 */
public final class PruebasParcial {
    private static int pruebasEjecutadas = 0;

    private PruebasParcial() { }

    public static void main(String[] args) {
        probarListaEnlazada();
        probarRegistroYValidaciones();
        probarMunicipiosYOrdenLlegada();
        probarOrdenamientosYBusquedaBinaria();
        probarRecursividadYDivideVenceras();
        probarAsignacionCancelacionYEntrega();
        probarProteccionesYAsignacionAutomatica();
        probarEjemplosDelEnunciado();
        probarDespachoAutomaticoYDisponibilidad();
        System.out.println("\nRESULTADO: " + pruebasEjecutadas + " comprobaciones superadas.");
    }

    private static void probarListaEnlazada() {
        MiListaEnlazada<String> lista = new MiListaEnlazada<>();
        comprobar(lista.estaVacia(), "lista inicialmente vacía");
        comprobar(!lista.eliminar("x"), "eliminar en lista vacía");
        lista.agregar("A");
        comprobar(lista.obtener(0).equals("A"), "obtener único elemento");
        lista.agregar("B");
        lista.agregar("C");
        comprobar(lista.tamano() == 3, "tamaño después de agregar");
        comprobar(lista.eliminar("A"), "eliminar cabeza");
        comprobar(lista.eliminar("B"), "eliminar nodo intermedio/primero restante");
        comprobar(lista.eliminar("C"), "eliminar cola y dejar vacía");
        comprobar(lista.estaVacia(), "lista vacía después de eliminar todo");
        comprobar(!lista.eliminar("no existe"), "elemento inexistente no se elimina");
        lista.agregar("uno");
        lista.agregar("dos");
        Iterator<String> iterador = lista.iterator();
        comprobar(iterador.hasNext() && iterador.next().equals("uno"), "Iterator propio");
        comprobar(iterador.next().equals("dos") && !iterador.hasNext(), "Iterator termina correctamente");
    }

    private static void probarRegistroYValidaciones() {
        CentroDistribucionServicio servicio = new CentroDistribucionServicio();
        servicio.registrarPaquete(new Paquete("PQ731", "Armenia", 4.5, 5, 45));
        comprobar(servicio.buscarPaquete("PQ731").isPresent(), "buscar paquete por HashMap");
        esperarExcepcion(IllegalArgumentException.class,
                () -> servicio.registrarPaquete(new Paquete("PQ731", "Salento", 1, 2, 10)),
                "código duplicado");
        esperarExcepcion(IllegalArgumentException.class,
                () -> new Paquete("PQ000", "Armenia", 0, 2, 10), "peso cero inválido");
        esperarExcepcion(IllegalArgumentException.class,
                () -> new Paquete("PQ000", "Armenia", 1, 0, 10), "prioridad cero inválida");
        esperarExcepcion(IllegalArgumentException.class,
                () -> new Paquete("PQ000", "Armenia", 1, 6, 10), "prioridad seis inválida");
        esperarExcepcion(IllegalArgumentException.class,
                () -> new Paquete("PQ000", "Armenia", 1, 2, 0), "tiempo cero inválido");
        esperarExcepcion(IllegalArgumentException.class,
                () -> new Paquete("PQ000", "   ", 1, 2, 10), "destino vacío inválido");
        servicio.registrarRepartidor(new Repartidor("R001", "Ana", "Armenia", true));
        esperarExcepcion(IllegalArgumentException.class,
                () -> servicio.registrarRepartidor(new Repartidor("R001", "Otra", "Armenia", true)),
                "identificación de repartidor duplicada");
        comprobar(new Paquete("PQ001", "Armenia", 1, 10).getPrioridad() == 5,
                "prioridad predeterminada igual a cinco");
    }

    private static void probarMunicipiosYOrdenLlegada() {
        CentroDistribucionServicio servicio = new CentroDistribucionServicio();
        servicio.registrarPaquete(new Paquete("PQ010", "Armenia", 2, 2, 20));
        servicio.registrarPaquete(new Paquete("PQ020", "Salento", 3, 3, 30));
        servicio.registrarPaquete(new Paquete("PQ030", "ARMENÍA", 4, 4, 40));
        comprobar(servicio.obtenerMunicipiosSinDuplicados().size() == 2,
                "municipios únicos, ignorando mayúsculas y tildes");
        comprobar(servicio.obtenerPaquetesPorMunicipio("armenia").size() == 2,
                "agrupar destino normalizado");
        comprobar(servicio.obtenerSiguientePorLlegada().get().getCodigo().equals("PQ010"),
                "cola FIFO conserva llegada");
        List<Paquete> pendientes = servicio.obtenerPaquetesPendientes();
        comprobar(pendientes.size() == 3 && pendientes.get(0).getCodigo().equals("PQ010"),
                "consulta de pendientes conserva llegada");
    }

    private static void probarOrdenamientosYBusquedaBinaria() {
        CentroDistribucionServicio servicio = new CentroDistribucionServicio();
        servicio.registrarPaquete(new Paquete("PQ300", "Armenia", 2, 3, 20));
        servicio.registrarPaquete(new Paquete("PQ100", "Salento", 8, 5, 30));
        servicio.registrarPaquete(new Paquete("PQ200", "Calarcá", 5, 5, 10));
        Paquete[] ordenados = servicio.obtenerArregloOrdenadoPorCodigo();
        comprobar(ordenados[0].getCodigo().equals("PQ100"), "Comparable ordena por código ascendente");
        comprobar(servicio.buscarBinariaPorCodigo(ordenados, "PQ200") != null,
                "búsqueda binaria encuentra elemento");
        comprobar(servicio.buscarBinariaPorCodigo(ordenados, "PQ999") == null,
                "búsqueda binaria informa elemento ausente");
        comprobar(servicio.ordenarPaquetes(ComparadoresPaquete.porPesoDesc()).get(0)
                .getCodigo().equals("PQ100"), "Comparator ordena por peso descendente");
        comprobar(servicio.obtenerSiguientePorPrioridad().get().getCodigo().equals("PQ200"),
                "prioridad, tiempo y código definen despacho");
    }

    private static void probarRecursividadYDivideVenceras() {
        CentroDistribucionServicio servicio = new CentroDistribucionServicio();
        servicio.registrarPaquete(new Paquete("PQ001", "Armenia", 4.5, 5, 20));
        servicio.registrarPaquete(new Paquete("PQ002", "Salento", 3.0, 2, 30));
        servicio.registrarPaquete(new Paquete("PQ003", "Armenia", 7.2, 4, 25));
        servicio.registrarPaquete(new Paquete("PQ004", "Armenia", 2.3, 1, 15));
        comprobar(Math.abs(servicio.calcularPesoTotalPorMunicipio("Armenia") - 14.0) < 0.0001,
                "suma recursiva de peso por municipio");
        comprobar(servicio.contarPaquetesConPrioridadMinima(4) == 2,
                "conteo recursivo de prioridad mínima");
        comprobar(servicio.obtenerPaqueteMayorPesoDivideYVenceras().get().getCodigo().equals("PQ003"),
                "divide y vencerás encuentra el paquete de mayor peso");
        comprobar(new CentroDistribucionServicio().obtenerPaqueteMayorPesoDivideYVenceras().isEmpty(),
                "divide y vencerás funciona con conjunto vacío");
    }

    private static void probarAsignacionCancelacionYEntrega() {
        CentroDistribucionServicio servicio = new CentroDistribucionServicio();
        servicio.registrarPaquete(new Paquete("PQ100", "Calarcá", 2, 5, 10));
        servicio.registrarPaquete(new Paquete("PQ200", "Armenia", 3, 4, 15));
        servicio.registrarRepartidor(new Repartidor("R001", "Ana", "Calarca", true));
        servicio.registrarRepartidor(new Repartidor("R002", "Luis", "Armenia", false));
        esperarExcepcion(IllegalStateException.class, () -> servicio.asignarPaquete("PQ100", "R002"),
                "repartidor no disponible o zona incorrecta rechazado");
        servicio.asignarPaquete("PQ100", "R001");
        comprobar(!servicio.buscarRepartidor("R001").get().isDisponible(),
                "repartidor queda no disponible tras asignación");
        comprobar(servicio.obtenerSiguientePorLlegada().get().getCodigo().equals("PQ200"),
                "paquete asignado se omite al buscar siguiente no asignado por llegada");
        servicio.cancelarAsignacion("PQ100");
        comprobar(servicio.buscarRepartidor("R001").get().isDisponible(), "cancelar asignación libera repartidor");
        servicio.asignarPaquete("PQ100", "R001");
        esperarExcepcion(IllegalStateException.class, () -> servicio.registrarEntrega("PQ100", "R002"),
                "repartidor distinto del asignado no puede entregar");
        servicio.registrarEntrega("PQ100", "R001");
        comprobar(servicio.cantidadEntregasRealizadas() == 1, "entrega agregada al historial propio");
        comprobar(servicio.cantidadPaquetesPendientes() == 1, "entrega sale de pendientes");
        comprobar(servicio.obtenerHistorialEntregas().obtener(0).getCodigo().equals("PQ100"),
                "historial conserva el orden de entrega");
    }

    private static void probarProteccionesYAsignacionAutomatica() {
        CentroDistribucionServicio servicio = new CentroDistribucionServicio();
        servicio.registrarPaquete(new Paquete("PQ300", "Armenia", 2.0, 3, 20));
        servicio.registrarPaquete(new Paquete("PQ100", "Salento", 4.0, 5, 10));
        servicio.registrarRepartidor(new Repartidor("R-A", "Ana", "Salento", true));
        servicio.registrarRepartidor(new Repartidor("R-B", "Luis", "Armenia", true));

        Map<String, List<Paquete>> mapa = servicio.obtenerMapaPaquetesPorMunicipio();
        comprobar(mapa.containsKey("Armenia") && mapa.containsKey("Salento"),
                "mapa de municipios devuelve nombres legibles");
        mapa.get("Armenia").clear();
        comprobar(servicio.obtenerPaquetesPorMunicipio("Armenia").size() == 1,
                "modificar mapa devuelto no altera agrupación interna");

        Paquete asignado = servicio.asignarSiguientePorPrioridad("R-A");
        comprobar(asignado.getCodigo().equals("PQ100"),
                "asignación automática selecciona mayor prioridad");
        servicio.registrarEntrega("PQ100", "R-A");
        MiListaEnlazada<Paquete> historialCopia = servicio.obtenerHistorialEntregas();
        historialCopia.eliminar(asignado);
        comprobar(servicio.cantidadEntregasRealizadas() == 1,
                "modificar copia del historial no altera historial interno");

        Paquete porLlegada = servicio.asignarSiguientePorLlegada("R-B");
        comprobar(porLlegada.getCodigo().equals("PQ300"),
                "asignación automática respeta orden de llegada");
    }

    private static void probarEjemplosDelEnunciado() {
        CentroDistribucionServicio s = new CentroDistribucionServicio();
        s.registrarPaquete(new Paquete("PQ420", "Armenia", 1, 3, 30));
        s.registrarPaquete(new Paquete("PQ731", "Armenia", 1, 5, 45));
        s.registrarPaquete(new Paquete("PQ105", "Armenia", 1, 5, 25));
        s.registrarPaquete(new Paquete("PQ942", "Armenia", 1, 4, 20));
        s.registrarRepartidor(new Repartidor("R1", "Ana", "Armenia", true));
        StringBuilder orden = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            Paquete p = s.asignarSiguientePorPrioridad("R1");
            orden.append(p.getCodigo()).append(' ');
            s.registrarEntrega(p.getCodigo(), "R1");
        }
        comprobar(orden.toString().trim().equals("PQ105 PQ731 PQ942 PQ420"),
                "orden de despacho del enunciado: PQ105, PQ731, PQ942, PQ420");
        comprobar(s.obtenerHistorialEntregas().toString().equals(
                "[" + "Paquete{codigo='PQ105', destino='Armenia', peso=1.00 kg, prioridad=5, tiempoEstimado=25 min}"
                + " -> Paquete{codigo='PQ731', destino='Armenia', peso=1.00 kg, prioridad=5, tiempoEstimado=45 min}"
                + " -> Paquete{codigo='PQ942', destino='Armenia', peso=1.00 kg, prioridad=4, tiempoEstimado=20 min}"
                + " -> Paquete{codigo='PQ420', destino='Armenia', peso=1.00 kg, prioridad=3, tiempoEstimado=30 min}]"),
                "historial enlazado conserva exactamente el orden de entrega");

        CentroDistribucionServicio m = new CentroDistribucionServicio();
        int[] prioridades = {5, 2, 4, 1, 5, 3, 4};
        for (int i = 0; i < prioridades.length; i++) {
            m.registrarPaquete(new Paquete("PQ" + (100 + i * 7), "Armenia", 1, prioridades[i], 10));
        }
        comprobar(m.contarPaquetesConPrioridadMinima(4) == 4, "enunciado: prioridades 5,2,4,1,5,3,4 con mínimo 4 -> 4");

        CentroDistribucionServicio mayor = new CentroDistribucionServicio();
        double[] kilos = {4.5, 12.0, 3.2, 18.7, 7.4, 5.1};
        for (int i = 0; i < kilos.length; i++) {
            mayor.registrarPaquete(new Paquete("PQ" + (200 + i * 11), "Armenia", kilos[i], 3, 10));
        }
        comprobar(mayor.obtenerPaqueteMayorPesoDivideYVenceras().get().getPeso() == 18.7,
                "enunciado: mayor peso 18.7 con 6 paquetes (cantidad par)");
        CentroDistribucionServicio unico = new CentroDistribucionServicio();
        unico.registrarPaquete(new Paquete("PQ001", "Armenia", 2.0, 3, 10));
        comprobar(unico.obtenerPaqueteMayorPesoDivideYVenceras().get().getCodigo().equals("PQ001"),
                "divide y vencerás con un solo paquete (caso base directo)");

        CentroDistribucionServicio ej = new CentroDistribucionServicio();
        String[][] datos = {{"PQ731", "Armenia"}, {"PQ105", "Salento"}, {"PQ942", "Armenia"},
                {"PQ318", "Calarcá"}, {"PQ567", "Salento"}, {"PQ829", "Armenia"}};
        for (String[] d : datos) {
            ej.registrarPaquete(new Paquete(d[0], d[1], 1, 3, 10));
        }
        comprobar(ej.obtenerMunicipiosOrdenados().toString().equals("[Armenia, Calarcá, Salento]"),
                "municipios únicos y ordenados del enunciado");
        comprobar(ej.obtenerPaquetesPorMunicipio("Armenia").size() == 3
                && ej.obtenerPaquetesPorMunicipio("Salento").size() == 2
                && ej.obtenerPaquetesPorMunicipio("Calarcá").size() == 1,
                "agrupación Armenia(3), Salento(2), Calarcá(1)");
        comprobar(ej.obtenerPaquetesPorMunicipio("Armenia").get(0).getCodigo().equals("PQ731")
                && ej.obtenerPaquetesPorMunicipio("Armenia").get(2).getCodigo().equals("PQ829"),
                "agrupación conserva orden de llegada dentro del municipio");
        comprobar(ej.obtenerPaquetesPendientes().get(3).getCodigo().equals("PQ318"),
                "orden de llegada PQ731, PQ105, PQ942, PQ318");
        comprobar(ej.obtenerPaquetesRegistrados().size() == 6, "registro no reordena paquetes");
        comprobar(ej.existePaquete("PQ731") && !ej.existePaquete("PQ000"), "existePaquete");
        Paquete[] natural = ej.obtenerArregloOrdenadoPorCodigo();
        comprobar(natural[0].getCodigo().equals("PQ105") && natural[5].getCodigo().equals("PQ942"),
                "orden natural PQ105 ... PQ942");
        comprobar(ej.ordenarPaquetes(ComparadoresPaquete.porPesoDesc()).size() == 6
                && ej.obtenerPaquetesRegistrados().get(0).getCodigo().equals("PQ731"),
                "ordenar temporalmente no cambia el registro original");
    }

    private static void probarDespachoAutomaticoYDisponibilidad() {
        CentroDistribucionServicio s = new CentroDistribucionServicio();
        s.registrarPaquete(new Paquete("PQ100", "Armenia", 1, 5, 10));
        s.registrarPaquete(new Paquete("PQ200", "Salento", 1, 3, 10));
        s.registrarRepartidor(new Repartidor("R1", "Ana", "Salento", true));
        Optional<Paquete> asignado = s.asignarAutomaticamentePorPrioridad();
        comprobar(asignado.isPresent() && asignado.get().getCodigo().equals("PQ200"),
                "despacho automático omite paquetes sin repartidor de su zona");
        comprobar(!s.asignarAutomaticamentePorPrioridad().isPresent(),
                "sin repartidores disponibles no se asigna nada");
        esperarExcepcion(IllegalStateException.class, () -> s.cambiarDisponibilidadRepartidor("R1", true),
                "no se libera a un repartidor con entrega en curso");
        s.registrarEntrega("PQ200", "R1");
        s.cambiarDisponibilidadRepartidor("R1", false);
        esperarExcepcion(IllegalStateException.class, () -> s.asignarPaquete("PQ100", "R1"),
                "repartidor marcado no disponible no recibe asignación");
        esperarExcepcion(IllegalStateException.class, () -> s.asignarPaquete("PQ200", "R1"),
                "paquete ya entregado no puede reasignarse");
        MiListaEnlazada<Integer> numeros = new MiListaEnlazada<>();
        numeros.agregar(1);
        numeros.agregar(2);
        numeros.agregar(3);
        comprobar(numeros.eliminar(3) && numeros.tamano() == 2 && numeros.toString().equals("[1 -> 2]"),
                "eliminar último y seguir agregando (cola actualizada)");
        numeros.agregar(4);
        comprobar(numeros.obtener(2) == 4, "agregar tras eliminar la cola conserva el enlace");
        esperarExcepcion(IndexOutOfBoundsException.class, () -> numeros.obtener(9), "índice fuera de rango");
    }

    private static void comprobar(boolean condicion, String descripcion) {
        if (!condicion) {
            throw new AssertionError("FALLÓ: " + descripcion);
        }
        pruebasEjecutadas++;
        System.out.println("[OK] " + descripcion);
    }

    private static void esperarExcepcion(Class<? extends Throwable> tipo, Runnable accion, String descripcion) {
        try {
            accion.run();
        } catch (Throwable error) {
            if (tipo.isInstance(error)) {
                comprobar(true, descripcion);
                return;
            }
            throw new AssertionError("Excepción inesperada en: " + descripcion, error);
        }
        throw new AssertionError("No se lanzó la excepción esperada en: " + descripcion);
    }
}
