package ec.edu.monster.controlador;

import ec.edu.monster.servicio.ServicioCredito;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;

/**
 * Crédito directo: consultas al Banco BanQuito desde la comercializadora.
 *
 * <pre>
 * GET /api/credito/evaluar/{cedula}?monto=   ¿es sujeto de crédito? ¿le alcanza?
 * GET /api/credito/cliente/{cedula}          tablas de amortización de sus préstamos
 * GET /api/credito/{codigoCredito}           tabla de amortización de un préstamo
 * </pre>
 *
 * @author Grupo 1 - NRC 36841
 */
@Path("credito")
@Produces(MediaType.APPLICATION_JSON)
public class CreditoControlador extends ControladorBase {

    private final ServicioCredito servicio = ServicioCredito.getInstancia();

    @GET
    @Path("evaluar/{cedula}")
    public Response evaluar(@PathParam("cedula") String cedula, @QueryParam("monto") BigDecimal monto) {
        return ejecutar(() -> servicio.evaluar(cedula, monto), e -> !e.isSujetoCredito()
                ? "NO es sujeto de crédito: " + String.join("; ", e.getMotivos())
                : "Sujeto de crédito con monto máximo de $" + e.getMontoMaximo() + (e.isAlcanza() ? "" : " (no alcanza para $" + e.getMontoSolicitado() + ")"));
    }

    @GET
    @Path("cliente/{cedula}")
    public Response creditosCliente(@PathParam("cedula") String cedula) {
        return ejecutar(() -> servicio.creditosCliente(cedula), l -> l.isEmpty() ? "El cliente no tiene préstamos" : l.size() + " préstamo(s)");
    }

    @GET
    @Path("{codigoCredito}")
    public Response amortizacion(@PathParam("codigoCredito") int codigoCredito) {
        return ejecutar(() -> servicio.amortizacion(codigoCredito), c -> "Tabla de amortización del crédito " + c.getCodigoCredito());
    }
}
