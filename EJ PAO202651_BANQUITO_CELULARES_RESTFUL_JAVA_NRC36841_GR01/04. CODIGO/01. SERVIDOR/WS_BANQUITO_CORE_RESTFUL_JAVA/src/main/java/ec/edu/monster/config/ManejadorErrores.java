package ec.edu.monster.config;

import ec.edu.monster.modelo.RespuestaApi;
import ec.edu.monster.servicio.ExcepcionNegocio;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Convierte cualquier excepción no controlada en una respuesta JSON estándar.
 *
 * @author Grupo 1 - NRC 36841
 */
@Provider
public class ManejadorErrores implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(ManejadorErrores.class.getName());

    @Override
    public Response toResponse(Exception e) {
        int estado;
        String mensaje;
        if (e instanceof ExcepcionNegocio n) {
            estado = n.getEstado();
            mensaje = n.getMessage();
        } else if (e instanceof WebApplicationException w) {
            estado = w.getResponse().getStatus();
            mensaje = switch (estado) {
                case 400 -> "Solicitud inválida: revise el formato JSON enviado";
                case 404 -> "Recurso no encontrado";
                case 405 -> "Método HTTP no permitido";
                case 415 -> "Envíe los datos en formato JSON (Content-Type: application/json)";
                default -> "Error en la solicitud";
            };
        } else {
            estado = 500;
            mensaje = "Error interno del servidor";
            LOG.log(Level.SEVERE, "Error no controlado", e);
        }
        return Response.status(estado).type(MediaType.APPLICATION_JSON).entity(RespuestaApi.error(mensaje)).build();
    }
}
