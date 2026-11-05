package ec.edu.monster.controlador;

import ec.edu.monster.modelo.SolicitudFactura;
import ec.edu.monster.servicio.ServicioFacturacion;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Facturación de la comercializadora.
 *
 * <pre>
 * POST /api/facturas            venta con pago en EFECTIVO (42 % desc.) o CREDITO directo
 * GET  /api/facturas?cedula=    facturas emitidas
 * GET  /api/facturas/{numero}   factura con su detalle (y su tabla si es a crédito)
 * </pre>
 *
 * @author Grupo 1 - NRC 36841
 */
@Path("facturas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FacturaControlador extends ControladorBase {

    private final ServicioFacturacion servicio = ServicioFacturacion.getInstancia();

    @POST
    public Response facturar(SolicitudFactura solicitud) {
        return ejecutar(201, () -> servicio.facturar(solicitud), f -> "Factura " + f.getNumero() + " emitida: "
                + (f.getCodigoCredito() == null ? "pago en efectivo, total $" + f.getTotal()
                        : "crédito directo " + f.getCodigoCredito() + ", " + f.getNumeroCuotas() + " cuotas de $" + f.getValorCuota()));
    }

    @GET
    public Response listar(@QueryParam("cedula") @DefaultValue("") String cedula) {
        return ejecutar(() -> servicio.listar(cedula), l -> l.size() + " factura(s)");
    }

    @GET
    @Path("{numero}")
    public Response consultar(@PathParam("numero") int numero) {
        return ejecutar(() -> servicio.consultar(numero), f -> "Factura " + f.getNumero());
    }
}
