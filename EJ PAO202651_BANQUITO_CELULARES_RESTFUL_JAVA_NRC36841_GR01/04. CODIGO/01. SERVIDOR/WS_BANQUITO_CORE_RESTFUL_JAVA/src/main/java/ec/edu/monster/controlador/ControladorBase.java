package ec.edu.monster.controlador;

import ec.edu.monster.modelo.RespuestaApi;
import ec.edu.monster.servicio.ExcepcionNegocio;
import jakarta.ws.rs.core.Response;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base de los controladores: ejecuta la acción del servicio y arma la
 * respuesta JSON estándar {exito, mensaje, datos} con el código HTTP correcto.
 *
 * @author Grupo 1 - NRC 36841
 */
public abstract class ControladorBase {

    protected <T> Response ejecutar(int estadoOk, Supplier<T> accion, Function<T, String> mensaje) {
        try {
            T datos = accion.get();
            return Response.status(estadoOk).entity(RespuestaApi.ok(mensaje.apply(datos), datos)).build();
        } catch (ExcepcionNegocio e) {
            return Response.status(e.getEstado()).entity(RespuestaApi.error(e.getMessage())).build();
        }
    }

    protected <T> Response ejecutar(Supplier<T> accion, Function<T, String> mensaje) {
        return ejecutar(200, accion, mensaje);
    }
}
