package ec.edu.monster.modelo;

/**
 * Error al consumir el servicio (conexión, sesión expirada o datos inválidos).
 *
 * @author Grupo 1 - NRC 36841
 */
public class ExcepcionCliente extends Exception {

    private final int codigoHttp;

    public ExcepcionCliente(String mensaje, int codigoHttp) {
        super(mensaje);
        this.codigoHttp = codigoHttp;
    }

    /** true cuando el servidor respondió 401 (sesión no válida o expirada). */
    public boolean sesionExpirada() {
        return codigoHttp == 401;
    }

    public int getCodigoHttp() {
        return codigoHttp;
    }
}
