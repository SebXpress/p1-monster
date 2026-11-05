package ec.edu.monster.controlador;

import ec.edu.monster.modelo.RespuestaApi;
import ec.edu.monster.util.Configuracion;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GET /api: servicios de la comercializadora y URL del banco que consume.
 *
 * @author Grupo 1 - NRC 36841
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class InicioControlador {

    @GET
    public RespuestaApi<Map<String, String>> inicio() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("GET|POST /api/celulares", "Catálogo de celulares (listar / crear)");
        m.put("GET|PUT|DELETE /api/celulares/{codigo}", "Consultar / actualizar / eliminar celular");
        m.put("POST /api/facturas", "Facturar: EFECTIVO (42 % desc.) o CREDITO directo BanQuito");
        m.put("GET /api/facturas, /api/facturas/{numero}", "Facturas emitidas");
        m.put("GET /api/credito/evaluar/{cedula}?monto=", "Sujeto de crédito y monto máximo (banco)");
        m.put("GET /api/credito/cliente/{cedula}", "Tablas de amortización de los préstamos del cliente");
        m.put("GET /api/credito/{codigoCredito}", "Tabla de amortización de un préstamo");
        m.put("Banco BanQuito", Configuracion.urlBanQuito());
        return RespuestaApi.ok("Comercializadora de Teléfonos Celulares", m);
    }
}
