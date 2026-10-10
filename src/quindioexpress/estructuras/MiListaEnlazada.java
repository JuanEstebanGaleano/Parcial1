package quindioexpress.estructuras;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class MiListaEnlazada<T> implements MiColeccion<T>, Iterable<T> {

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamano;
    private int modificaciones;

    private static final class Nodo<E> {
        private final E valor;
        private Nodo<E> siguiente;

        private Nodo(E valor) {
            this.valor = valor;
        }
    }

    public MiListaEnlazada() {
        cabeza = null;
        cola = null;
        tamano = 0;
        modificaciones = 0;
    }


    @Override
    public void agregar(T elemento) {
        Objects.requireNonNull(elemento, "La lista no admite elementos nulos.");
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            cola = nuevo;
        }
        tamano++;
        modificaciones++;
    }


    @Override
    public boolean eliminar(T elemento) {
        if (cabeza == null || elemento == null) {
            return false;
        }

        if (Objects.equals(cabeza.valor, elemento)) {
            cabeza = cabeza.siguiente;
            tamano--;
            modificaciones++;
            if (cabeza == null) {
                cola = null;
            }
            return true;
        }

        Nodo<T> anterior = cabeza;
        Nodo<T> actual = cabeza.siguiente;
        while (actual != null) {
            if (Objects.equals(actual.valor, elemento)) {
                anterior.siguiente = actual.siguiente;
                if (actual == cola) {
                    cola = anterior;
                }
                tamano--;
                modificaciones++;
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }


    @Override
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException(
                    "Índice " + indice + " fuera de rango para una lista de tamaño " + tamano + ".");
        }
        return nodoEn(indice).valor;
    }

    @Override
    public int tamano() {
        return tamano;
    }

    @Override
    public boolean estaVacia() {
        return tamano == 0;
    }

    public boolean contiene(T elemento) {
        for (T valor : this) {
            if (Objects.equals(valor, elemento)) {
                return true;
            }
        }
        return false;
    }

    public void limpiar() {
        if (!estaVacia()) {
            cabeza = null;
            cola = null;
            tamano = 0;
            modificaciones++;
        }
    }
    @Override
    public Iterator<T> iterator() {
        return new IteradorLista();
    }

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder("[");
        Nodo<T> actual = cabeza;
        while (actual != null) {
            texto.append(actual.valor);
            if (actual.siguiente != null) {
                texto.append(" -> ");
            }
            actual = actual.siguiente;
        }
        return texto.append(']').toString();
    }

    private Nodo<T> nodoEn(int indice) {
        Nodo<T> actual = cabeza;
        int posicion = 0;
        while (posicion < indice) {
            actual = actual.siguiente;
            posicion++;
        }
        return actual;
    }
    private final class IteradorLista implements Iterator<T> {
        private Nodo<T> siguiente = cabeza;
        private final int modificacionesEsperadas = modificaciones;

        @Override
        public boolean hasNext() {
            comprobarModificaciones();
            return siguiente != null;
        }

        @Override
        public T next() {
            comprobarModificaciones();
            if (siguiente == null) {
                throw new NoSuchElementException("No quedan elementos por recorrer.");
            }
            T valor = siguiente.valor;
            siguiente = siguiente.siguiente;
            return valor;
        }

        private void comprobarModificaciones() {
            if (modificacionesEsperadas != modificaciones) {
                throw new ConcurrentModificationException(
                        "La lista cambió durante el recorrido con el iterador.");
            }
        }
    }
}
