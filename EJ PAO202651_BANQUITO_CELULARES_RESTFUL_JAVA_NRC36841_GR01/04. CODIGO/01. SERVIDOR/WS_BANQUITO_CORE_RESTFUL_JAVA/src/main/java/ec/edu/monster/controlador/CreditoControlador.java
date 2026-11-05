package ec.edu.monster.controlador;

import ec.edu.monster.modelo.SolicitudCredito;
import ec.edu.monster.servicio.ServicioCredito;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Servicios web REST del Módulo de Crédito del Banco BanQuito.
 * Son los que invoca la aplicación de la comercializadora de celulares.
 *
 * <pre>
 * GET  /api/creditos/sujeto/{cedula}              ¿es sujeto de crédito?
 * GET  /api/creditos/monto-maximo/{codigoCliente} monto máximo de crédito
 * POST /api/creditos                              otorga el crédito y crea la tabla
 *      {"codigoCliente":1,"precio":449.00,"numeroCuotas":12}
 * GET  /api/creditos/{codigoCredito}/amortizacion tabla de amortización
 * GET  /api/creditos/cliente/{cedula}             créditos del cliente con sus tablas
 * </pre>
 *
 * @author Grupo 1 - NRC 36841
 */
@Path("creditos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CreditoControlador extends ControladorBase {

    private final ServicioCredito servicio = ServicioCredito.getInstancia();

    @GET
    @Path("sujeto/{cedula}")
    public Response sujetoCredito(@PathParam("cedula") String cedula) {
        return ejecutar(() -> servicio.verificarSujeto(cedula),
                v -> v.isSujetoCredito() ? v.getNombre() + " SÍ es sujeto de crédito"
                        : "NO es sujeto de crédito: " + String.join("; ", v.getMotivos()));
    }

    @GET
    @Path("monto-maximo/{codigoCliente}")
    public Response montoMaximo(@PathParam("codigoCliente") int codigoCliente) {
        return ejecutar(() -> servicio.montoMaximo(codigoCliente),
                m -> "Monto máximo de crédito de " + m.getNombre() + ": $" + m.getMontoMaximo());
    }

    @POST
    public Response otorgar(SolicitudCredito solicitud) {
        return ejecutar(201, () -> servicio.otorgar(solicitud),
                c -> "Crédito " + c.getCodigoCredito() + " aprobado: " + c.getNumeroCuotas() + " cuotas de $" + c.getCuota());
    }

    @GET
    @Path("{codigoCredito}/amortizacion")
    public Response amortizacion(@PathParam("codigoCredito") int codigoCredito) {
        return ejecutar(() -> servicio.consultar(codigoCredito),
                c -> "Tabla de amortización del crédito " + c.getCodigoCredito());
    }

    @GET
    @Path("cliente/{cedula}")
    public Response creditosCliente(@PathParam("cedula") String cedula) {
        return ejecutar(() -> servicio.creditosCliente(cedula),
                l -> l.isEmpty() ? "El cliente no tiene créditos" : l.size() + " crédito(s) encontrado(s)");
    }
}
