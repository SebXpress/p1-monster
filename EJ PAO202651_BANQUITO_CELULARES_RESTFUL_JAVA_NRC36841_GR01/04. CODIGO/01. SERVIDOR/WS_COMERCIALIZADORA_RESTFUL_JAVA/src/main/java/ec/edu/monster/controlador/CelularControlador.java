package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Celular;
import ec.edu.monster.servicio.ServicioCelular;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Mantenimiento del catálogo de teléfonos celulares a la venta.
 *
 * <pre>
 * GET    /api/celulares?buscar=&amp;activos=true   listar
 * GET    /api/celulares/{codigo}               consultar
 * POST   /api/celulares                        crear
 * PUT    /api/celulares/{codigo}               actualizar
 * DELETE /api/celulares/{codigo}               eliminar
 * </pre>
 *
 * @author Grupo 1 - NRC 36841
 */
@Path("celulares")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CelularControlador extends ControladorBase {

    private final ServicioCelular servicio = ServicioCelular.getInstancia();

    @GET
    public Response listar(@QueryParam("buscar") @DefaultValue("") String buscar, @QueryParam("activos") @DefaultValue("false") boolean activos) {
        return ejecutar(() -> servicio.listar(buscar, activos), l -> l.size() + " celular(es)");
    }

    @GET
    @Path("{codigo}")
    public Response consultar(@PathParam("codigo") int codigo) {
        return ejecutar(() -> servicio.consultar(codigo), c -> c.getMarca() + " " + c.getModelo());
    }

    @POST
    public Response crear(Celular celular) {
        return ejecutar(201, () -> servicio.crear(celular), c -> "Celular " + c.getMarca() + " " + c.getModelo() + " registrado");
    }

    @PUT
    @Path("{codigo}")
    public Response actualizar(@PathParam("codigo") int codigo, Celular celular) {
        return ejecutar(() -> servicio.actualizar(codigo, celular), c -> "Celular " + c.getMarca() + " " + c.getModelo() + " actualizado");
    }

    @DELETE
    @Path("{codigo}")
    public Response eliminar(@PathParam("codigo") int codigo) {
        return ejecutar(() -> servicio.eliminar(codigo), c -> "Celular " + c.getMarca() + " " + c.getModelo() + " eliminado");
    }
}
