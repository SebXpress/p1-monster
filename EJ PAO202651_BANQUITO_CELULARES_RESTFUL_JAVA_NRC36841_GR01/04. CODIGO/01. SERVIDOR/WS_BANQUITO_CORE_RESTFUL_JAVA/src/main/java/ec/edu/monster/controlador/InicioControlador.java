package ec.edu.monster.controlador;

import ec.edu.monster.modelo.RespuestaApi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GET /api: lista los servicios web que expone el Core BanQuito.
 *
 * @author Grupo 1 - NRC 36841
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class InicioControlador {

    @GET
    public RespuestaApi<Map<String, String>> inicio() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("GET /api/creditos/sujeto/{cedula}", "Valida si una persona es sujeto de crédito (por cédula)");
        m.put("GET /api/creditos/monto-maximo/{codigoCliente}", "Monto máximo de crédito (por código de cliente)");
        m.put("POST /api/creditos", "Otorga el crédito y crea la tabla de amortización {codigoCliente, precio, numeroCuotas}");
        m.put("GET /api/creditos/{codigoCredito}/amortizacion", "Tabla de amortización de un crédito");
        m.put("GET /api/creditos/cliente/{cedula}", "Créditos del cliente con sus tablas de amortización");
        return RespuestaApi.ok("Core Bancario BanQuito - Módulo de Crédito", m);
    }
}
