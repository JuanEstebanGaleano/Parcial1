package quindioexpress;

import quindioexpress.app.DatosIniciales;
import quindioexpress.app.DemoConsultas;
import quindioexpress.app.DemoEnunciado;
import quindioexpress.app.DemoOperacion;
import quindioexpress.servicio.CentroDistribucionServicio;

public final class Main {

    private Main() {
        throw new IllegalStateException("La clase ejecutable no se instancia.");
    }

    public static void main(String[] args) {
        CentroDistribucionServicio servicio = new CentroDistribucionServicio();
        DatosIniciales.cargar(servicio);

        System.out.println("============================================================");
        System.out.println(" QUINDÍOEXPRESS - DEMOSTRACIÓN DE ESTRUCTURAS DE DATOS");
        System.out.println("============================================================");
        System.out.println("Paquetes registrados: " + servicio.cantidadPaquetesRegistrados());
        System.out.println("Repartidores registrados: " + servicio.cantidadRepartidoresRegistrados());

        DemoConsultas.demostrarConsultasYMunicipios(servicio);
        DemoConsultas.demostrarOrdenamientosYBusqueda(servicio);
        DemoConsultas.demostrarAlgoritmos(servicio);
        DemoConsultas.demostrarListaPropia();
        DemoOperacion.demostrarAsignacionYEntrega(servicio);
        DemoOperacion.demostrarRepartidoresYModalidades(servicio);
        DemoOperacion.demostrarValidaciones(servicio);
        DemoEnunciado.demostrarEjemplosDelEnunciado();
        DemoEnunciado.demostrarTrazasRecursivas();

        System.out.println("\nFin de la demostración.");
    }
}
