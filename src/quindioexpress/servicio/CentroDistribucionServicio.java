package quindioexpress.servicio;

import quindioexpress.estructuras.ListaEnlazada;
import quindioexpress.modelo.Paquete;
import quindioexpress.modelo.Repartidor;

import java.util.*;

public class CentroDistribucionServicio {

    private final RegistroCentro registro = new RegistroCentro();
    private final DespachoCentro despacho = new DespachoCentro(registro);

    //Registro y consultas


    public void registrarPaquete(Paquete paquete) {
        registro.registrarPaquete(paquete);
        despacho.encolar(paquete);
    }

    public void registrarRepartidor(Repartidor repartidor) { registro.registrarRepartidor(repartidor); }
    public Optional<Paquete> buscarPaquete(String codigo) { return registro.buscarPaquete(codigo); }
    public Optional<Repartidor> buscarRepartidor(String id) { return registro.buscarRepartidor(id); }
    public boolean existePaquete(String codigo) { return registro.existePaquete(codigo); }
    public boolean existeRepartidor(String id) { return registro.existeRepartidor(id); }
    public List<Paquete> obtenerPaquetesRegistrados() { return registro.obtenerPaquetesRegistrados(); }
    public Set<String> obtenerCodigosRegistrados() { return registro.obtenerCodigosRegistrados(); }
    public HashSet<String> obtenerMunicipiosSinDuplicados() { return registro.obtenerMunicipiosSinDuplicados(); }
    public TreeSet<String> obtenerMunicipiosOrdenados() { return registro.obtenerMunicipiosOrdenados(); }
    public List<Paquete> obtenerPaquetesPorMunicipio(String m) { return registro.obtenerPaquetesPorMunicipio(m); }
    public Map<String, List<Paquete>> obtenerMapaPaquetesPorMunicipio() {
        return registro.obtenerMapaPaquetesPorMunicipio();
    }
    public Paquete[] obtenerArregloOrdenadoPorCodigo() { return registro.obtenerArregloOrdenadoPorCodigo(); }
    public List<Paquete> ordenarPaquetes(Comparator<Paquete> c) { return registro.ordenarPaquetes(c); }
    public List<Repartidor> obtenerRepartidores() { return registro.obtenerRepartidores(); }
    public int cantidadPaquetesRegistrados() { return registro.cantidadPaquetesRegistrados(); }
    public int cantidadRepartidoresRegistrados() { return registro.cantidadRepartidoresRegistrados(); }

    // ------------------------------------------------------------------ Despacho

    public Optional<Paquete> obtenerSiguientePorLlegada() { return despacho.obtenerSiguientePorLlegada(); }
    public Optional<Paquete> obtenerSiguientePorPrioridad() { return despacho.obtenerSiguientePorPrioridad(); }
    public List<Paquete> obtenerPaquetesPendientes() { return despacho.obtenerPaquetesPendientes(); }
    public Optional<Repartidor> buscarRepartidorDisponiblePorZona(String m) {
        return despacho.buscarRepartidorDisponiblePorZona(m);
    }
    public void asignarPaquete(String codigo, String idRepartidor) { despacho.asignarPaquete(codigo, idRepartidor); }
    public Paquete asignarSiguientePorPrioridad(String idRepartidor) {
        return despacho.asignarSiguientePorPrioridad(idRepartidor);
    }
    public Paquete asignarSiguientePorLlegada(String idRepartidor) {
        return despacho.asignarSiguientePorLlegada(idRepartidor);
    }
    public Optional<Paquete> asignarAutomaticamentePorPrioridad() {
        return despacho.asignarAutomaticamentePorPrioridad();
    }
    public void cancelarAsignacion(String codigo) { despacho.cancelarAsignacion(codigo); }
    public Paquete registrarEntrega(String codigo, String idRepartidor) {
        return despacho.registrarEntrega(codigo, idRepartidor);
    }
    public void cambiarDisponibilidadRepartidor(String id, boolean disponible) {
        despacho.cambiarDisponibilidadRepartidor(id, disponible);
    }
    public int cantidadPaquetesPendientes() { return despacho.cantidadPaquetesPendientes(); }
    public int cantidadEntregasRealizadas() { return despacho.cantidadEntregasRealizadas(); }
    public ListaEnlazada<Paquete> obtenerHistorialEntregas() { return despacho.obtenerHistorialEntregas(); }

    //Análisis

    public double calcularPesoTotalPorMunicipio(String municipio) {
        if (municipio == null || municipio.trim().isEmpty()) {
            throw new IllegalArgumentException("El municipio no puede ser nulo ni vacío.");
        }
        return AnalisisPaquetes.sumarPesos(registro.obtenerPaquetesPorMunicipio(municipio), 0);
    }

    public int contarPaquetesConPrioridadMinima(int prioridadMinima) {
        if (prioridadMinima < Paquete.PRIORIDAD_MINIMA || prioridadMinima > Paquete.PRIORIDAD_MAXIMA) {
            throw new IllegalArgumentException("La prioridad mínima debe estar entre 1 y 5.");
        }
        return AnalisisPaquetes.contarConPrioridadMinima(registro.obtenerPaquetesRegistrados(), 0, prioridadMinima);
    }

    public Optional<Paquete> obtenerPaqueteMayorPesoDivideYVenceras() {
        Paquete[] arreglo = registro.obtenerPaquetesRegistrados().toArray(new Paquete[0]);
        if (arreglo.length == 0) {
            return Optional.empty();
        }
        return Optional.of(AnalisisPaquetes.mayorPeso(arreglo, 0, arreglo.length - 1));
    }

    public Paquete buscarBinariaPorCodigo(Paquete[] paquetesOrdenados, String codigo) {
        return AnalisisPaquetes.buscarBinariaPorCodigo(paquetesOrdenados, codigo);
    }
}
