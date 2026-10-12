package quindioexpress.servicio;

import quindioexpress.modelo.Paquete;
import quindioexpress.modelo.Repartidor;

import java.text.Normalizer;
import java.util.*;


public class RegistroCentro {

    private final HashMap<String, Paquete> paquetesPorCodigo = new HashMap<>();
    private final ArrayList<Paquete> ordenLlegada = new ArrayList<>();
    private final HashSet<String> municipios = new HashSet<>();
    private final TreeSet<String> municipiosOrdenados = new TreeSet<>();
    private final HashMap<String, String> nombresMunicipios = new HashMap<>();
    private final HashMap<String, List<Paquete>> paquetesPorMunicipio = new HashMap<>();
    private final HashMap<String, Repartidor> repartidoresPorIdentificacion = new HashMap<>();


    public void registrarPaquete(Paquete paquete) {
        if (paquete == null) {
            throw new IllegalArgumentException("No se puede registrar un paquete nulo.");
        }
        String codigo = paquete.getCodigo();
        if (paquetesPorCodigo.containsKey(codigo)) {
            throw new IllegalArgumentException("Ya existe un paquete con ese codigo " + codigo + ".");
        }
        paquetesPorCodigo.put(codigo, paquete);
        ordenLlegada.add(paquete);
        String clave = normalizarMunicipio(paquete.getDestino());
        String nombre = nombresMunicipios.computeIfAbsent(clave, c -> paquete.getDestino().trim());
        municipios.add(nombre);
        municipiosOrdenados.add(nombre);
        paquetesPorMunicipio.computeIfAbsent(clave, c -> new ArrayList<>()).add(paquete);
    }

    public void registrarRepartidor(Repartidor repartidor) {
        if (repartidor == null) {
            throw new IllegalArgumentException("No se puede registrar un repartidor nulo.");
        }
        String id = repartidor.getIdentificacion();
        if (repartidoresPorIdentificacion.containsKey(id)) {
            throw new IllegalArgumentException("Ya existe un repartidor con esa identificación " + id + ".");
        }
        repartidoresPorIdentificacion.put(id, repartidor);
    }

    public Optional<Paquete> buscarPaquete(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(paquetesPorCodigo.get(codigo.trim()));
    }

    public Optional<Repartidor> buscarRepartidor(String identificacion) {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(repartidoresPorIdentificacion.get(identificacion.trim()));
    }

    public boolean existePaquete(String codigo) {
        return buscarPaquete(codigo).isPresent();
    }
    public boolean existeRepartidor(String identificacion) {
        return buscarRepartidor(identificacion).isPresent();
    }
    public List<Paquete> obtenerPaquetesRegistrados() {
        return new ArrayList<>(ordenLlegada);
    }
    public Set<String> obtenerCodigosRegistrados() {
        return new HashSet<>(paquetesPorCodigo.keySet());
    }
    public HashSet<String> obtenerMunicipiosSinDuplicados() {
        return new HashSet<>(municipios);
    }
    public TreeSet<String> obtenerMunicipiosOrdenados() {
        return new TreeSet<>(municipiosOrdenados);
    }


    public List<Paquete> obtenerPaquetesPorMunicipio(String municipio) {
        if (municipio == null || municipio.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Paquete> paquetes = paquetesPorMunicipio.get(normalizarMunicipio(municipio));
        return paquetes == null ? Collections.<Paquete>emptyList() : new ArrayList<>(paquetes);
    }

    public Map<String, List<Paquete>> obtenerMapaPaquetesPorMunicipio() {
        Map<String, List<Paquete>> copia = new LinkedHashMap<>();
        for (Map.Entry<String, List<Paquete>> e : paquetesPorMunicipio.entrySet()) {
            copia.put(nombresMunicipios.getOrDefault(e.getKey(), e.getKey()), new ArrayList<>(e.getValue()));
        }
        return copia;
    }

    public Paquete[] obtenerArregloOrdenadoPorCodigo() {
        Paquete[] arreglo = ordenLlegada.toArray(new Paquete[0]);
        Arrays.sort(arreglo);
        return arreglo;
    }

    public List<Paquete> ordenarPaquetes(Comparator<Paquete> comparador) {
        if (comparador == null) {
            throw new IllegalArgumentException("El comparador no puede ser nulo.");
        }
        List<Paquete> copia = new ArrayList<>(ordenLlegada);
        copia.sort(comparador);
        return copia;
    }

    public List<Repartidor> obtenerRepartidores() {
        return new ArrayList<>(repartidoresPorIdentificacion.values());
    }
    public int cantidadPaquetesRegistrados() {
        return paquetesPorCodigo.size();
    }
    public int cantidadRepartidoresRegistrados() {
        return repartidoresPorIdentificacion.size();
    }
    static String normalizarMunicipio(String municipio) {
        String limpio = municipio.trim().toLowerCase(Locale.ROOT);
        return Normalizer.normalize(limpio, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").replaceAll("\\s+", " ");
    }
}
