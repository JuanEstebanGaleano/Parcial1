package quindioexpress.servicio;

import quindioexpress.estructuras.ListaEnlazada;
import quindioexpress.modelo.ComparadoresPaquete;
import quindioexpress.modelo.Paquete;
import quindioexpress.modelo.Repartidor;

import java.util.*;

public class DespachoCentro {

    private final RegistroCentro registro;
    private final Deque<Paquete> colaLlegada = new ArrayDeque<>();
    private final PriorityQueue<Paquete> colaPrioridad =
            new PriorityQueue<>(ComparadoresPaquete.porCriterioDespacho());
    private final ListaEnlazada<Paquete> historialEntregas = new ListaEnlazada<>();

    private final HashSet<String> codigosPendientes = new HashSet<>();
    private final HashMap<String, Repartidor> asignacionesPorCodigo = new HashMap<>();


    public DespachoCentro(RegistroCentro registro) {
        this.registro = registro;
    }


    public void encolar(Paquete paquete) {
        colaLlegada.offer(paquete);
        colaPrioridad.offer(paquete);
        codigosPendientes.add(paquete.getCodigo());
    }


    public Optional<Paquete> obtenerSiguientePorLlegada() {
        for (Paquete p : colaLlegada) {
            String codigo = p.getCodigo();
            if (codigosPendientes.contains(codigo) && !asignacionesPorCodigo.containsKey(codigo)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    public Optional<Paquete> obtenerSiguientePorPrioridad() {
        return Optional.ofNullable(colaPrioridad.peek());
    }

    public List<Paquete> obtenerPaquetesPendientes() {
        return new ArrayList<>(colaLlegada);
    }

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


    public void asignarPaquete(String codigoPaquete, String identificacionRepartidor) {
        Paquete paquete = registro.buscarPaquete(codigoPaquete)
                .orElseThrow(() -> new IllegalArgumentException("No existe el paquete "));
        Repartidor repartidor = registro.buscarRepartidor(identificacionRepartidor)
                .orElseThrow(() -> new IllegalArgumentException("No existe el repartidor "));
        String codigo = paquete.getCodigo();

        if (!codigosPendientes.contains(codigo)) {
            throw new IllegalStateException("El paquete ya fue entregado");
        }
        if (asignacionesPorCodigo.containsKey(codigo)) {
            throw new IllegalStateException("El paquete ya tiene un repartidor asignado.");
        }
        if (!repartidor.isDisponible()) {
            throw new IllegalStateException("El repartidor no está disponible.");
        }
        if (!repartidor.puedeRecibirPaqueteConDestino(paquete.getDestino())) {
            throw new IllegalStateException("La zona del repartidor no coincide con el destino");
        }
        asignacionesPorCodigo.put(codigo, repartidor);
        repartidor.setDisponible(false);
        colaPrioridad.remove(paquete);
    }

    public Paquete asignarSiguientePorPrioridad(String identificacionRepartidor) {
        Paquete siguiente = obtenerSiguientePorPrioridad().orElseThrow(
                () -> new IllegalStateException("No hay paquetes disponibles para despacho prioritario."));
        asignarPaquete(siguiente.getCodigo(), identificacionRepartidor);
        return siguiente;
    }

    public Paquete asignarSiguientePorLlegada(String identificacionRepartidor) {
        Paquete siguiente = obtenerSiguientePorLlegada().orElseThrow(
                () -> new IllegalStateException("No hay paquetes disponibles por orden de llegada."));
        asignarPaquete(siguiente.getCodigo(), identificacionRepartidor);
        return siguiente;
    }

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


    public void cancelarAsignacion(String codigoPaquete) {
        if (codigoPaquete == null || codigoPaquete.trim().isEmpty()) {
            throw new IllegalArgumentException("El código del paquete no puede ser nulo ni vacío.");
        }
        String codigo = codigoPaquete.trim();
        Repartidor repartidor = asignacionesPorCodigo.remove(codigo);
        if (repartidor == null) {
            throw new IllegalArgumentException("El paquete no esta activo");
        }
        repartidor.setDisponible(true);
        Paquete paquete = registro.buscarPaquete(codigo).orElse(null);
        if (paquete != null && codigosPendientes.contains(codigo)) {
            colaPrioridad.offer(paquete);
        }
    }

    public Paquete registrarEntrega(String codigoPaquete, String identificacionRepartidor) {
        Paquete paquete = registro.buscarPaquete(codigoPaquete)
                .orElseThrow(() -> new IllegalArgumentException("No existe el paquete"));
        Repartidor solicitado = registro.buscarRepartidor(identificacionRepartidor)
                .orElseThrow(() -> new IllegalArgumentException("No existe el repartidor"));
        String codigo = paquete.getCodigo();

        if (!codigosPendientes.contains(codigo)) {
            throw new IllegalStateException("El paquete ya se entregado.");
        }
        Repartidor asignado = asignacionesPorCodigo.get(codigo);
        if (asignado == null || !asignado.getIdentificacion().equals(solicitado.getIdentificacion())) {
            throw new IllegalStateException("La entrega no coincide");
        }
        historialEntregas.agregar(paquete);
        codigosPendientes.remove(codigo);
        colaLlegada.remove(paquete);
        asignacionesPorCodigo.remove(codigo);
        colaPrioridad.remove(paquete);
        asignado.setDisponible(true);
        return paquete;
    }

    public void cambiarDisponibilidadRepartidor(String identificacion, boolean disponible) {
        Repartidor repartidor = registro.buscarRepartidor(identificacion)
                .orElseThrow(() -> new IllegalArgumentException("No existe el repartidor indicado."));
        if (disponible && asignacionesPorCodigo.containsValue(repartidor)) {
            throw new IllegalStateException(
                    "El repartidor tiene una entrega en curso; no puede marcarse como disponible.");
        }
        repartidor.setDisponible(disponible);
    }

    public int cantidadPaquetesPendientes() {
        return codigosPendientes.size();
    }

    public int cantidadEntregasRealizadas() {
        return historialEntregas.tamano();
    }

    public ListaEnlazada<Paquete> obtenerHistorialEntregas() {
        ListaEnlazada<Paquete> copia = new ListaEnlazada<>();
        for (Paquete p : historialEntregas) {
            copia.agregar(p);
        }
        return copia;
    }
}
