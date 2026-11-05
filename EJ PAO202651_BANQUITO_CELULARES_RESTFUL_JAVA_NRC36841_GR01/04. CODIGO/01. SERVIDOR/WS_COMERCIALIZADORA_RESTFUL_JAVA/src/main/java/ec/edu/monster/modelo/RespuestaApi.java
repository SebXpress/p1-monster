package ec.edu.monster.modelo;

/**
 * Formato estándar de todas las respuestas del servidor.
 *
 * @param <T> tipo de los datos devueltos
 * @author Grupo 1 - NRC 36841
 */
public class RespuestaApi<T> {

    private boolean exito;
    private String mensaje;
    private T datos;

    public RespuestaApi() {
    }

    public RespuestaApi(boolean exito, String mensaje, T datos) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public static <T> RespuestaApi<T> ok(String mensaje, T datos) {
        return new RespuestaApi<>(true, mensaje, datos);
    }

    public static RespuestaApi<Object> error(String mensaje) {
        return new RespuestaApi<>(false, mensaje, null);
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public T getDatos() {
        return datos;
    }

    public void setDatos(T datos) {
        this.datos = datos;
    }
}
