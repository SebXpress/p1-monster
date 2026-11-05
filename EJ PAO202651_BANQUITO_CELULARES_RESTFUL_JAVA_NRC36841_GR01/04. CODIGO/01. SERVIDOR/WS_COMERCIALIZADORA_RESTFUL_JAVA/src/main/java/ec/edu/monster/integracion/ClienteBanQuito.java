package ec.edu.monster.integracion;

import ec.edu.monster.modelo.CreditoBanco;
import ec.edu.monster.modelo.CuotaAmortizacion;
import ec.edu.monster.servicio.ExcepcionNegocio;
import ec.edu.monster.util.Configuracion;
import ec.edu.monster.util.Json;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Consume los servicios web REST del Core BanQuito (la otra aplicación web).
 * Cada método corresponde a un servicio del módulo de crédito del banco.
 *
 * @author Grupo 1 - NRC 36841
 */
public final class ClienteBanQuito {

    private static final ClienteBanQuito INSTANCIA = new ClienteBanQuito();

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    private ClienteBanQuito() {
    }

    public static ClienteBanQuito getInstancia() {
        return INSTANCIA;
    }

    /** Respuesta del banco: éxito, mensaje y datos (Map o List). */
    public record Respuesta(boolean exito, String mensaje, Object datos) {

        @SuppressWarnings("unchecked")
        public Map<String, Object> mapa() {
            return (Map<String, Object>) datos;
        }
    }

    /** GET /creditos/sujeto/{cedula} */
    public Respuesta sujetoCredito(String cedula) {
        return llamar("GET", "/creditos/sujeto/" + cedula, null);
    }

    /** GET /creditos/monto-maximo/{codigoCliente} */
    public Respuesta montoMaximo(int codigoCliente) {
        return llamar("GET", "/creditos/monto-maximo/" + codigoCliente, null);
    }

    /** POST /creditos {codigoCliente, precio, numeroCuotas} */
    public Respuesta otorgarCredito(int codigoCliente, BigDecimal precio, int numeroCuotas) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("codigoCliente", codigoCliente);
        cuerpo.put("precio", precio);
        cuerpo.put("numeroCuotas", numeroCuotas);
        return llamar("POST", "/creditos", Json.escribir(cuerpo));
    }

    /** GET /creditos/{codigoCredito}/amortizacion */
    public Respuesta amortizacion(int codigoCredito) {
        return llamar("GET", "/creditos/" + codigoCredito + "/amortizacion", null);
    }

    /** GET /creditos/cliente/{cedula} */
    public Respuesta creditosCliente(String cedula) {
        return llamar("GET", "/creditos/cliente/" + cedula, null);
    }

    private Respuesta llamar(String metodo, String ruta, String cuerpo) {
        String url = Configuracion.urlBanQuito() + ruta;
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json");
        if (cuerpo != null) {
            b.header("Content-Type", "application/json").method(metodo, HttpRequest.BodyPublishers.ofString(cuerpo, StandardCharsets.UTF_8));
        } else {
            b.method(metodo, HttpRequest.BodyPublishers.noBody());
        }
        try {
            HttpResponse<String> r = http.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            Map<String, Object> j = Json.objeto(r.body());
            return new Respuesta(Boolean.TRUE.equals(j.get("exito")), String.valueOf(j.get("mensaje")), j.get("datos"));
        } catch (ConnectException e) {
            throw new ExcepcionNegocio(503, "No se pudo conectar con el Banco BanQuito (" + Configuracion.urlBanQuito()
                    + "). Verifique que el Core esté desplegado.");
        } catch (IOException | IllegalArgumentException e) {
            throw new ExcepcionNegocio(502, "Respuesta no válida del Banco BanQuito: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExcepcionNegocio(503, "Se interrumpió la llamada al Banco BanQuito");
        }
    }

    // ---------------------------------------------------------------- conversión JSON -> modelo
    public static BigDecimal dec(Object o) {
        return o == null ? null : new BigDecimal(String.valueOf(o)).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public static int ent(Object o) {
        return o == null ? 0 : ((Number) o).intValue();
    }

    @SuppressWarnings("unchecked")
    public static CreditoBanco credito(Map<String, Object> m) {
        CreditoBanco c = new CreditoBanco();
        c.setCodigoCredito(ent(m.get("codigoCredito")));
        c.setCodigoCliente(ent(m.get("codigoCliente")));
        c.setCedula((String) m.get("cedula"));
        c.setNombre((String) m.get("nombre"));
        c.setMonto(dec(m.get("monto")));
        c.setNumeroCuotas(ent(m.get("numeroCuotas")));
        c.setTasaAnual(dec(m.get("tasaAnual")));
        c.setCuota(dec(m.get("cuota")));
        c.setTotalIntereses(dec(m.get("totalIntereses")));
        c.setTotalPagar(dec(m.get("totalPagar")));
        c.setFecha((String) m.get("fecha"));
        c.setEstado((String) m.get("estado"));
        List<CuotaAmortizacion> tabla = new ArrayList<>();
        for (Object o : (List<Object>) m.getOrDefault("tabla", List.of())) {
            Map<String, Object> f = (Map<String, Object>) o;
            CuotaAmortizacion q = new CuotaAmortizacion();
            q.setNumeroCuota(ent(f.get("numeroCuota")));
            q.setValorCuota(dec(f.get("valorCuota")));
            q.setInteresPagado(dec(f.get("interesPagado")));
            q.setCapitalPagado(dec(f.get("capitalPagado")));
            q.setSaldo(dec(f.get("saldo")));
            tabla.add(q);
        }
        c.setTabla(tabla);
        return c;
    }
}
