package quindioexpress.app;

import quindioexpress.modelo.Paquete;
import quindioexpress.modelo.Repartidor;
import quindioexpress.servicio.CentroDistribucionServicio;

public final class DatosIniciales {

    private DatosIniciales() {
        throw new IllegalStateException("Clase de utilidades.");
    }

    public static void cargar(CentroDistribucionServicio servicio) {
        cargarPaquetes(servicio);
        cargarRepartidores(servicio);
    }

    private static void cargarPaquetes(CentroDistribucionServicio servicio) {
        servicio.registrarPaquete(new Paquete("PQ731", "Armenia", 4.5, 5, 45));
        servicio.registrarPaquete(new Paquete("PQ105", "Salento", 3.0, 5, 25));
        servicio.registrarPaquete(new Paquete("PQ942", "Armenia", 12.0, 4, 20));
        servicio.registrarPaquete(new Paquete("PQ318", "Calarcá", 2.7, 3, 30));
        servicio.registrarPaquete(new Paquete("PQ567", "Salento", 6.4, 2, 55));
        servicio.registrarPaquete(new Paquete("PQ829", "Armenia", 7.3, 5, 35));
        servicio.registrarPaquete(new Paquete("PQ214", "Circasia", 1.8, 1, 15));
        servicio.registrarPaquete(new Paquete("PQ421", "Filandia", 8.1, 4, 35));
        servicio.registrarPaquete(new Paquete("PQ654", "Calarcá", 3.9, 1, 18));
        servicio.registrarPaquete(new Paquete("PQ882", "Salento", 10.2, 3, 42));
        servicio.registrarPaquete(new Paquete("PQ137", "Armenia", 5.1, 2, 22));
        servicio.registrarPaquete(new Paquete("PQ763", "Filandia", 9.6, 5, 50));
        servicio.registrarPaquete(new Paquete("PQ508", "Circasia", 2.2, 4, 16));
        servicio.registrarPaquete(new Paquete("PQ296", "Calarcá", 11.5, 5, 28));
        servicio.registrarPaquete(new Paquete("PQ617", "Salento", 4.6, 1, 33));
        servicio.registrarPaquete(new Paquete("PQ349", "Armenia", 6.7, 3, 27));
        servicio.registrarPaquete(new Paquete("PQ975", "Circasia", 12.4, 2, 40));
        servicio.registrarPaquete(new Paquete("PQ183", "Filandia", 4.1, 1, 12));
        servicio.registrarPaquete(new Paquete("PQ706", "Armenia", 13.2, 4, 19));
        servicio.registrarPaquete(new Paquete("PQ258", "Calarcá", 5.9, 5, 31));
        servicio.registrarPaquete(new Paquete("PQ843", "Salento", 7.7, 4, 24));
        servicio.registrarPaquete(new Paquete("PQ392", "Filandia", 3.4, 2, 26));
        servicio.registrarPaquete(new Paquete("PQ561", "Circasia", 6.8, 1, 17));
        servicio.registrarPaquete(new Paquete("PQ124", "Armenia", 8.9, 5, 23));
        servicio.registrarPaquete(new Paquete("PQ935", "Calarcá", 9.1, 3, 38));
        servicio.registrarPaquete(new Paquete("PQ477", "Salento", 2.6, 1, 14));
        servicio.registrarPaquete(new Paquete("PQ689", "Filandia", 11.1, 4, 29));
        servicio.registrarPaquete(new Paquete("PQ302", "Circasia", 5.5, 5, 33));
        servicio.registrarPaquete(new Paquete("PQ816", "Armenia", 4.3, 1, 21));
        servicio.registrarPaquete(new Paquete("PQ448", "Calarcá", 7.0, 2, 34));
        servicio.registrarPaquete(new Paquete("PQ590", "Salento", 14.4, 5, 26));

        // Este paquete usa el constructor abreviado y demuestra la prioridad predeterminada 5.
        
        servicio.registrarPaquete(new Paquete("PQ231", "Filandia", 6.1, 25));
        servicio.registrarPaquete(new Paquete("PQ767", "Circasia", 8.4, 1, 20));
        servicio.registrarPaquete(new Paquete("PQ155", "Armenia", 10.7, 4, 18));
        servicio.registrarPaquete(new Paquete("PQ698", "Calarcá", 3.1, 1, 16));
        servicio.registrarPaquete(new Paquete("PQ388", "Salento", 8.8, 3, 32));
    }

    private static void cargarRepartidores(CentroDistribucionServicio servicio) {
        servicio.registrarRepartidor(new Repartidor("R001", "María Gómez", "Armenia", true));
        servicio.registrarRepartidor(new Repartidor("R002", "Carlos Ruiz", "Salento", true));
        servicio.registrarRepartidor(new Repartidor("R003", "Lina Torres", "Calarcá", true));
        servicio.registrarRepartidor(new Repartidor("R004", "Diego Pérez", "Circasia", true));
        servicio.registrarRepartidor(new Repartidor("R005", "Sofía Ramírez", "Filandia", true));
        servicio.registrarRepartidor(new Repartidor("R006", "Andrés Mejía", "Armenia", true));
    }
}
