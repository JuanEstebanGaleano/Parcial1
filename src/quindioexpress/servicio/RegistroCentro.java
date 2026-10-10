package quindioexpress.servicio;

import quindioexpress.modelo.Paquete;
import quindioexpress.modelo.Repartidor;

import java.text.Normalizer;
import java.util.*;

/**
 * Registro y consulta de paquetes, repartidores y municipios.
 * Aquí viven las colecciones de datos "estáticos" de la jornada.
 */
public class RegistroCentro {

    /** Consulta de paquetes por código en O(1) esperado. */
    private final HashMap<String, Paquete> paquetesPorCodigo = new HashMap<>();
    /** Orden exacto de llegada (auditoría). */
    private final ArrayList<Paquete> ordenLlegada = new ArrayList<>();
    /** Municipios sin repetición. */
    private final HashSet<String> municipios = new HashSet<>();
    /** Municipios sin repetición y en orden alfabético. */
    private final TreeSet<String> municipiosOrdenados = new TreeSet<>();
    /** Clave normalizada -> primer nombre legible registrado. */
    private final HashMap<String, String> nombresMunicipios = new HashMap<>();
    /** Clave normalizada de municipio -> paquetes destinados a él. */
    private final HashMap<String, List<Paquete>> paquetesPorMunicipio = new HashMap<>();
    /** Repartidores indexados por identificación. */
    private final HashMap<String, Repartidor> repartidoresPorIdentificacion = new HashMap<>();

    /**
     * Registra un paquete rechazando códigos duplicados.
     *
     * @param paquete paquete a registrar
     * @throws IllegalArgumentException si es nulo o su código ya existe
     */
    public void registrarPaquete(Paquete paquete) {
        if (paquete == null) {
            throw new IllegalArgumentException("No se puede registrar un paquete nulo.");
        }
        String codigo = paquete.getCodigo();
        if (paquetesPorCodigo.containsKey(codigo)) {
            throw new IllegalArgumentException("Ya existe un paquete con el código " + codigo + ".");
        }
        paquetesPorCodigo.put(codigo, paquete);
        ordenLlegada.add(paquete);
        String clave = normalizarMunicipio(paquete.getDestino());
        String nombre = nombresMunicipios.computeIfAbsent(clave, c -> paquete.getDestino().trim());
        municipios.add(nombre);
        municipiosOrdenados.add(nombre);
        paquetesPorMunicipio.computeIfAbsent(clave, c -> new ArrayList<>()).add(paquete);
    }

    /**
     * Registra un repartidor garantizando identificación única.
     *
     * @param repartidor repartidor a registrar
     * @throws IllegalArgumentException si es nulo o la identificación ya existe
     */
    public void registrarRepartidor(Repartidor repartidor) {
        if (repartidor == null) {
            throw new IllegalArgumentException("No se puede registrar un repartidor nulo.");
        }
        String id = repartidor.getIdentificacion();
        if (repartidoresPorIdentificacion.containsKey(id)) {
            throw new IllegalArgumentException("Ya existe un repartidor con la identificación " + id + ".");
        }
        repartidoresPorIdentificacion.put(id, repartidor);
    }

    /** @return paquete con ese código, o vacío si no existe */
    public Optional<Paquete> buscarPaquete(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(paquetesPorCodigo.get(codigo.trim()));
    }

    /** @return repartidor con esa identificación, o vacío si no existe */
    public Optional<Repartidor> buscarRepartidor(String identificacion) {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(repartidoresPorIdentificacion.get(identificacion.trim()));
    }

    /** @return {@code true} si el código está registrado */
    public boolean existePaquete(String codigo) {
        return buscarPaquete(codigo).isPresent();
    }

    /** @return {@code true} si la identificación está registrada */
    public boolean existeRepartidor(String identificacion) {
        return buscarRepartidor(identificacion).isPresent();
    }

    /** @return copia de los paquetes en orden exacto de llegada */
    public List<Paquete> obtenerPaquetesRegistrados() {
        return new ArrayList<>(ordenLlegada);
    }

    /** @return copia del conjunto de códigos registrados */
    public Set<String> obtenerCodigosRegistrados() {
        return new HashSet<>(paquetesPorCodigo.keySet());
    }

    /** @return municipios sin duplicados */
    public HashSet<String> obtenerMunicipiosSinDuplicados() {
        return new HashSet<>(municipios);
    }

    /** @return municipios sin duplicados en orden alfabético */
    public TreeSet<String> obtenerMunicipiosOrdenados() {
        return new TreeSet<>(municipiosOrdenados);
    }

    /** @return paquetes de un municipio en orden de llegada (lista vacía si no hay) */
    public List<Paquete> obtenerPaquetesPorMunicipio(String municipio) {
        if (municipio == null || municipio.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Paquete> paquetes = paquetesPorMunicipio.get(normalizarMunicipio(municipio));
        return paquetes == null ? Collections.<Paquete>emptyList() : new ArrayList<>(paquetes);
    }

    /** @return copia defensiva del mapa municipio (nombre legible) -> paquetes */
    public Map<String, List<Paquete>> obtenerMapaPaquetesPorMunicipio() {
        Map<String, List<Paquete>> copia = new LinkedHashMap<>();
        for (Map.Entry<String, List<Paquete>> e : paquetesPorMunicipio.entrySet()) {
            copia.put(nombresMunicipios.getOrDefault(e.getKey(), e.getKey()), new ArrayList<>(e.getValue()));
        }
        return copia;
    }

    /** @return copia del arreglo de paquetes ordenado por el orden natural (código) */
    public Paquete[] obtenerArregloOrdenadoPorCodigo() {
        Paquete[] arreglo = ordenLlegada.toArray(new Paquete[0]);
        Arrays.sort(arreglo);
        return arreglo;
    }

    /** @return copia ordenada con el comparador dado; no altera el registro original */
    public List<Paquete> ordenarPaquetes(Comparator<Paquete> comparador) {
        if (comparador == null) {
            throw new IllegalArgumentException("El comparador no puede ser nulo.");
        }
        List<Paquete> copia = new ArrayList<>(ordenLlegada);
        copia.sort(comparador);
        return copia;
    }

    /** @return copia de los repartidores registrados */
    public List<Repartidor> obtenerRepartidores() {
        return new ArrayList<>(repartidoresPorIdentificacion.values());
    }

    /** @return cantidad de paquetes registrados */
    public int cantidadPaquetesRegistrados() {
        return paquetesPorCodigo.size();
    }

    /** @return cantidad de repartidores registrados */
    public int cantidadRepartidoresRegistrados() {
        return repartidoresPorIdentificacion.size();
    }

    /** Normaliza un municipio: minúsculas, sin tildes y sin espacios sobrantes. */
    static String normalizarMunicipio(String municipio) {
        String limpio = municipio.trim().toLowerCase(Locale.ROOT);
        return Normalizer.normalize(limpio, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").replaceAll("\\s+", " ");
    }
}
