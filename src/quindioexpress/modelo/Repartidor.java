package quindioexpress.modelo;

import java.text.Normalizer;
import java.util.Locale;

public class Repartidor {

    private final String identificacion;
    private final String nombre;
    private final String zona;
    private boolean disponible;

    public Repartidor(String identificacion, String nombre, String zona, boolean disponible) {
        this.identificacion = validarTexto(identificacion, "La identificación no puede ser nula ni vacía.");
        this.nombre = validarTexto(nombre, "El nombre no puede ser nulo ni vacío.");
        this.zona = validarTexto(zona, "La zona no puede ser nula ni vacía.");
        this.disponible = disponible;
    }

    public String getIdentificacion() { return identificacion; }
    public String getNombre() { return nombre; }
    public String getZona() { return zona; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public boolean puedeRecibirPaqueteConDestino(String destino) {
        return disponible && destino != null && !destino.trim().isEmpty()
                && normalizar(zona).equals(normalizar(destino));
    }

    private static String normalizar(String texto) {
        String minusculas = texto.trim().toLowerCase(Locale.ROOT);
        return Normalizer.normalize(minusculas, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").replaceAll("\\s+", " ");
    }

    private static String validarTexto(String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor.trim();
    }

    @Override
    public String toString() {
        return "Repartidor{" + "identificacion='" + identificacion + '\''
                + ", nombre='" + nombre + '\'' + ", zona='" + zona + '\''
                + ", disponible=" + disponible + '}';
    }
}
