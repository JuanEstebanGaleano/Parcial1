package quindioexpress.estructuras;

public interface MiColeccion<T> {

    void agregar(T elemento);

    boolean eliminar(T elemento);

    T obtener(int indice);

    int tamano();

    boolean estaVacia();
}
