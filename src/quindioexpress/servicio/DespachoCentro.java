package quindioexpress.servicio;

import quindioexpress.estructuras.MiListaEnlazada;
import quindioexpress.modelo.ComparadoresPaquete;
import quindioexpress.modelo.Paquete;
import quindioexpress.modelo.Repartidor;

import java.util.*;

/**
 * Operación de la jornada: colas de atención, asignación a repartidores, entrega e historial.
 * Consulta datos al {@link RegistroCentro}.
 */
public class DespachoCentro {

    private final RegistroCentro registro;
    /** Deque como cola FIFO: conserva el orden de llegada de los paquetes pendientes. */
    private final Deque<Paquete> colaLlegada = new ArrayDeque<>();
    /** PriorityQueue: prioridad desc., tiempo asc., código asc. Solo paquetes sin asignar. */
    private final PriorityQueue<Paquete> colaPrioridad =
            new PriorityQueue<>(ComparadoresPaquete.porCriterioDespacho());
    /** Historial de entregas con la lista enlazada propia (no una colección de Java). */
    private final MiListaEnlazada<Paquete> historialEntregas = new MiListaEnlazada<>();
    /** Códigos pendientes de entrega. */
    private final HashSet<String> codigosPendientes = new HashSet<>();
    /** Repartidor asignado a cada paquete en proceso de entrega. */
    private final HashMap<String, Repartidor> asignacionesPorCodigo = new HashMap<>();

    /** @param registro registro del que se consultan paquetes y repartidores */
    public DespachoCentro(RegistroCentro registro) {
        this.registro = registro;
    }

    /** Incorpora un paquete recién registrado a las colas de atención. */
    public void encolar(Paquete paquete) {
        colaLlegada.offer(paquete);
        colaPrioridad.offer(paquete);
        codigosPendientes.add(paquete.getCodigo());
    }

    /** @return primer paquete pendiente sin asignación, por orden de llegada (no lo retira) */
    public Optional<Paquete> obtenerSiguientePorLlegada() {
        for (Paquete p : colaLlegada) {
            String codigo = p.getCodigo();
            if (codigosPendientes.contains(codigo) && !asignacionesPorCodigo.containsKey(codigo)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    /** @return paquete de mayor precedencia sin asignar (no lo retira) */
    public Optional<Paquete> obtenerSiguientePorPrioridad() {
        return Optional.ofNullable(colaPrioridad.peek());
    }

    /** @return copia de los pendientes en orden de llegada */
    public List<Paquete> obtenerPaquetesPendientes() {
        return new ArrayList<>(colaLlegada);
    }

    /** @return primer repartidor disponible cuya zona coincide con el municipio */
    public Optional<Repartidor> buscarRepartidorDisponiblePorZona(String municipio) {
        if (municipio == null || municipio.trim().isEmpty()) {
            return Optional.empty();
        }
        for (Repartidor r : registro.obtenerRepartidores()) {
            if (r.puedeRecibirPaqueteConDestino(municipio)) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }

    /**
     * Asigna un paquete pendiente a un repartidor disponible de la misma zona.
     *
     * @throws IllegalArgumentException si el paquete o el repartidor no existen
     * @throws IllegalStateException si el paquete no está pendiente o ya está asignado,
     *                               o el repartidor no está disponible o es de otra zona
     */
    public void asignarPaquete(String codigoPaquete, String identificacionRepartidor) {
        Paquete paquete = registro.buscarPaquete(codigoPaquete)
                .orElseThrow(() -> new IllegalArgumentException("No existe el paquete indicado."));
        Repartidor repartidor = registro.buscarRepartidor(identificacionRepartidor)
                .orElseThrow(() -> new IllegalArgumentException("No existe el repartidor indicado."));
        String codigo = paquete.getCodigo();

        if (!codigosPendientes.contains(codigo)) {
            throw new IllegalStateException("El paquete ya fue entregado y no está pendiente.");
        }
        if (asignacionesPorCodigo.containsKey(codigo)) {
            throw new IllegalStateException("El paquete ya tiene un repartidor asignado.");
        }
        if (!repartidor.isDisponible()) {
            throw new IllegalStateException("El repartidor no está disponible.");
        }
        if (!repartidor.puedeRecibirPaqueteConDestino(paquete.getDestino())) {
            throw new IllegalStateException("La zona del repartidor no coincide con el destino del paquete.");
        }
        asignacionesPorCodigo.put(codigo, repartidor);
        repartidor.setDisponible(false);
        colaPrioridad.remove(paquete);
    }

    /** Asigna el paquete de mayor precedencia al repartidor indicado (si su zona coincide). */
    public Paquete asignarSiguientePorPrioridad(String identificacionRepartidor) {
        Paquete siguiente = obtenerSiguientePorPrioridad().orElseThrow(
                () -> new IllegalStateException("No hay paquetes disponibles para despacho prioritario."));
        asignarPaquete(siguiente.getCodigo(), identificacionRepartidor);
        return siguiente;
    }

    /** Asigna el paquete pendiente más antiguo al repartidor indicado (si su zona coincide). */
    public Paquete asignarSiguientePorLlegada(String identificacionRepartidor) {
        Paquete siguiente = obtenerSiguientePorLlegada().orElseThrow(
                () -> new IllegalStateException("No hay paquetes disponibles por orden de llegada."));
        asignarPaquete(siguiente.getCodigo(), identificacionRepartidor);
        return siguiente;
    }

    /**
     * Despacho prioritario automático: asigna el primer paquete, en orden de precedencia,
     * que tenga un repartidor disponible en su zona. Costo O(n log n) en el peor caso.
     *
     * @return paquete asignado o vacío si ninguno tiene repartidor posible
     */
    public Optional<Paquete> asignarAutomaticamentePorPrioridad() {
        PriorityQueue<Paquete> copia = new PriorityQueue<>(colaPrioridad);
        while (!copia.isEmpty()) {
            Paquete candidato = copia.poll();
            Optional<Repartidor> repartidor = buscarRepartidorDisponiblePorZona(candidato.getDestino());
            if (repartidor.isPresent()) {
                asignarPaquete(candidato.getCodigo(), repartidor.get().getIdentificacion());
                return Optional.of(candidato);
            }
        }
        return Optional.empty();
    }

    /**
     * Cancela una asignación: el paquete vuelve a la cola prioritaria y el repartidor se libera.
     *
     * @throws IllegalArgumentException si el paquete no tiene asignación activa
     */
    public void cancelarAsignacion(String codigoPaquete) {
        if (codigoPaquete == null || codigoPaquete.trim().isEmpty()) {
            throw new IllegalArgumentException("El código del paquete no puede ser nulo ni vacío.");
        }
        String codigo = codigoPaquete.trim();
        Repartidor repartidor = asignacionesPorCodigo.remove(codigo);
        if (repartidor == null) {
            throw new IllegalArgumentException("El paquete no tiene una asignación activa.");
        }
        repartidor.setDisponible(true);
        Paquete paquete = registro.buscarPaquete(codigo).orElse(null);
        if (paquete != null && codigosPendientes.contains(codigo)) {
            colaPrioridad.offer(paquete);
        }
    }

    /**
     * Registra la entrega de un paquete asignado: lo agrega al historial propio, lo saca de
     * pendientes y libera al repartidor.
     *
     * @throws IllegalArgumentException si el paquete o el repartidor no existen
     * @throws IllegalStateException si el paquete no está pendiente o la asignación no coincide
     */
    public Paquete registrarEntrega(String codigoPaquete, String identificacionRepartidor) {
        Paquete paquete = registro.buscarPaquete(codigoPaquete)
                .orElseThrow(() -> new IllegalArgumentException("No existe el paquete indicado."));
        Repartidor solicitado = registro.buscarRepartidor(identificacionRepartidor)
                .orElseThrow(() -> new IllegalArgumentException("No existe el repartidor indicado."));
        String codigo = paquete.getCodigo();

        if (!codigosPendientes.contains(codigo)) {
            throw new IllegalStateException("El paquete ya fue entregado.");
        }
        Repartidor asignado = asignacionesPorCodigo.get(codigo);
        if (asignado == null || !asignado.getIdentificacion().equals(solicitado.getIdentificacion())) {
            throw new IllegalStateException("La entrega no coincide con la asignación vigente.");
        }
        historialEntregas.agregar(paquete);
        codigosPendientes.remove(codigo);
        colaLlegada.remove(paquete);
        asignacionesPorCodigo.remove(codigo);
        colaPrioridad.remove(paquete);
        asignado.setDisponible(true);
        return paquete;
    }

    /**
     * Cambia la disponibilidad de un repartidor. No se puede liberar a quien tiene una entrega en curso.
     *
     * @throws IllegalArgumentException si el repartidor no existe
     * @throws IllegalStateException si se intenta liberar a un repartidor con asignación activa
     */
    public void cambiarDisponibilidadRepartidor(String identificacion, boolean disponible) {
        Repartidor repartidor = registro.buscarRepartidor(identificacion)
                .orElseThrow(() -> new IllegalArgumentException("No existe el repartidor indicado."));
        if (disponible && asignacionesPorCodigo.containsValue(repartidor)) {
            throw new IllegalStateException(
                    "El repartidor tiene una entrega en curso; no puede marcarse como disponible.");
        }
        repartidor.setDisponible(disponible);
    }

    /** @return cantidad de paquetes pendientes de entrega */
    public int cantidadPaquetesPendientes() {
        return codigosPendientes.size();
    }

    /** @return cantidad de entregas realizadas */
    public int cantidadEntregasRealizadas() {
        return historialEntregas.tamano();
    }

    /** @return copia independiente del historial, construida con la lista enlazada propia */
    public MiListaEnlazada<Paquete> obtenerHistorialEntregas() {
        MiListaEnlazada<Paquete> copia = new MiListaEnlazada<>();
        for (Paquete p : historialEntregas) {
            copia.agregar(p);
        }
        return copia;
    }
}
