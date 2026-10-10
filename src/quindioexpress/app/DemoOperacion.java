package quindioexpress.app;

import quindioexpress.modelo.Paquete;
import quindioexpress.modelo.Repartidor;
import quindioexpress.servicio.CentroDistribucionServicio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class DemoOperacion {

    private DemoOperacion() {
        throw new IllegalStateException("Clase de utilidades.");
    }

    public static void demostrarAsignacionYEntrega(CentroDistribucionServicio servicio) {
        System.out.println("\n--- Asignación y entrega de tres paquetes prioritarios ---");
        for (int i = 0; i < 3; i++) {
            Optional<Paquete> siguiente = servicio.obtenerSiguientePorPrioridad();
            if (!siguiente.isPresent()) {
                System.out.println("No quedan paquetes para asignar.");
                break;
            }
            Paquete paquete = siguiente.get();
            Optional<Repartidor> repartidor =
                    servicio.buscarRepartidorDisponiblePorZona(paquete.getDestino());
            if (!repartidor.isPresent()) {
                System.out.println("Sin repartidor disponible para " + paquete.getDestino());
                break;
            }
            Repartidor elegido = repartidor.get();
            servicio.asignarPaquete(paquete.getCodigo(), elegido.getIdentificacion());
            Paquete entregado = servicio.registrarEntrega(
                    paquete.getCodigo(), elegido.getIdentificacion());
            System.out.println("Entregado: " + entregado.getCodigo() + " por " + elegido.getNombre());
        }
        System.out.println("\nHistorial propio, en orden real de entrega:");
        for (Paquete entregado : servicio.obtenerHistorialEntregas()) {
            System.out.println("  " + entregado.getCodigo());
        }
        System.out.println("Entregas registradas: " + servicio.cantidadEntregasRealizadas());
        System.out.println("Pendientes al finalizar la demostración: "
                + servicio.cantidadPaquetesPendientes());
        System.out.println("Siguiente por llegada después de esas entregas: "
                + servicio.obtenerSiguientePorLlegada().orElse(null));
    }
    public static void demostrarRepartidoresYModalidades(CentroDistribucionServicio servicio) {
        System.out.println("\n--- Repartidores, disponibilidad y modalidades de atención ---");
        System.out.println("Repartidores registrados:");
        for (Repartidor r : servicio.obtenerRepartidores()) {
            System.out.println("  " + r);
        }
        System.out.println("Existe R003: " + servicio.existeRepartidor("R003")
                + " | Existe PQ731: " + servicio.existePaquete("PQ731")
                + " | Existe PQ999: " + servicio.existePaquete("PQ999"));
        System.out.println("Códigos registrados: " + servicio.obtenerCodigosRegistrados().size());
        System.out.println("Mapa municipio -> paquetes:");
        servicio.obtenerMapaPaquetesPorMunicipio().forEach((municipio, lista) -> {
            List<String> codigos = new ArrayList<>();
            for (Paquete p : lista) {
                codigos.add(p.getCodigo());
            }
            System.out.println("  " + municipio + " -> " + codigos);
        });
        // Atención por orden de llegada (Deque FIFO).
        Paquete porLlegada = servicio.asignarSiguientePorLlegada("R001");
        System.out.println("Atención por llegada asignó " + porLlegada.getCodigo()
                + " a R001 (zona " + porLlegada.getDestino() + ")");
        System.out.println("R001 disponible tras asignar: "
                + servicio.buscarRepartidor("R001").get().isDisponible());
        servicio.cancelarAsignacion(porLlegada.getCodigo());
        System.out.println("Asignación cancelada; R001 disponible de nuevo: "
                + servicio.buscarRepartidor("R001").get().isDisponible());
        // Despacho prioritario automático (PriorityQueue + zona del repartidor).
        servicio.cambiarDisponibilidadRepartidor("R002", false);
        Optional<Paquete> auto = servicio.asignarAutomaticamentePorPrioridad();
        System.out.println("Despacho prioritario automático (R002 no disponible): "
                + auto.map(Paquete::getCodigo).orElse("ninguno"));
        auto.ifPresent(p -> servicio.cancelarAsignacion(p.getCodigo()));
        servicio.cambiarDisponibilidadRepartidor("R002", true);
        System.out.println("Pendientes: " + servicio.cantidadPaquetesPendientes());
    }
    public static void demostrarValidaciones(CentroDistribucionServicio servicio) {
        System.out.println("\n--- Validaciones de reglas de negocio ---");
        try {
            servicio.registrarPaquete(new Paquete("PQ731", "Armenia", 1.0, 2, 10));
        } catch (IllegalArgumentException excepcionEsperada) {
            System.out.println("Código duplicado rechazado: " + excepcionEsperada.getMessage());
        }
        try {
            new Paquete("PQ000", "Armenia", 1.0, 6, 10);
        } catch (IllegalArgumentException excepcionEsperada) {
            System.out.println("Prioridad fuera de rango rechazada: " + excepcionEsperada.getMessage());
        }
        try {
            servicio.registrarRepartidor(new Repartidor("R001", "Duplicado", "Armenia", true));
        } catch (IllegalArgumentException excepcionEsperada) {
            System.out.println("Identificación duplicada rechazada: " + excepcionEsperada.getMessage());
        }
        // PQ731 va a Armenia; R002 cubre Salento, por lo que la asignación debe rechazarse.
        try {
            servicio.asignarPaquete("PQ731", "R002");
        } catch (IllegalStateException excepcionEsperada) {
            System.out.println("Asignación con zona incorrecta rechazada: " + excepcionEsperada.getMessage());
        }
        Paquete paquetePorDefecto = servicio.buscarPaquete("PQ231").orElse(null);
        System.out.println("Prioridad predeterminada de PQ231: "
                + (paquetePorDefecto == null ? "no encontrado" : paquetePorDefecto.getPrioridad()));
    }
}
