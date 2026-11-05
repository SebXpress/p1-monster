package ec.edu.monster.modelo;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
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
 * Consume la API REST del servidor WS_COMERCIALIZADORA_RESTFUL_JAVA.
 * La comercializadora, a su vez, invoca los servicios web del Core BanQuito
 * cuando la forma de pago es crédito directo.
 *
 * @author Grupo 1 - NRC 36841
 */
public class ServicioRest {

    public static final String URL_POR_DEFECTO = "http://localhost:8080/WS_COMERCIALIZADORA_RESTFUL_JAVA/api";

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final String cliente;
    private String urlBase;
    private String ultimoMensaje;

    public ServicioRest(String urlBase, String cliente) {
        setUrlBase(urlBase);
        this.cliente = cliente;
    }

    public final void setUrlBase(String url) {
        String u = url == null || url.isBlank() ? URL_POR_DEFECTO : url.trim();
        if (!u.startsWith("http")) {
            u = "http://" + u;
        }
        this.urlBase = u.endsWith("/") ? u.substring(0, u.length() - 1) : u;
    }

    public String getUrlBase() {
        return urlBase;
    }

    /** Mensaje que devolvió el servidor en la última operación. */
    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    // ------------------------------------------------------------ catálogo de celulares

    public List<Celular> listarCelulares(String buscar, boolean soloActivos) throws ExcepcionCliente {
        String q = "?activos=" + soloActivos + (buscar == null || buscar.isBlank() ? "" : "&buscar=" + cod(buscar));
        List<Celular> l = new ArrayList<>();
        for (Object o : lista(enviar("GET", "/celulares" + q, null))) {
            l.add(Celular.desdeMapa((Map<?, ?>) o));
        }
        return l;
    }

    public Celular consultarCelular(int codigo) throws ExcepcionCliente {
        return Celular.desdeMapa(datos(enviar("GET", "/celulares/" + codigo, null)));
    }

    public Celular crearCelular(Celular c) throws ExcepcionCliente {
        return Celular.desdeMapa(datos(enviar("POST", "/celulares", Json.escribir(mapa(c)))));
    }

    public Celular actualizarCelular(Celular c) throws ExcepcionCliente {
        return Celular.desdeMapa(datos(enviar("PUT", "/celulares/" + c.getCodigo(), Json.escribir(mapa(c)))));
    }

    public String eliminarCelular(int codigo) throws ExcepcionCliente {
        enviar("DELETE", "/celulares/" + codigo, null);
        return ultimoMensaje;
    }

    private static Map<String, Object> mapa(Celular c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("marca", c.getMarca());
        m.put("modelo", c.getModelo());
        m.put("descripcion", c.getDescripcion());
        m.put("precio", c.getPrecio());
        m.put("estado", c.getEstado());
        return m;
    }

    // ------------------------------------------------------------ facturación

    /**
     * Factura la venta.
     *
     * @param items código de celular → cantidad
     * @param formaPago EFECTIVO o CREDITO
     * @param numeroCuotas solo para CREDITO (3 a 18)
     */
    public Factura facturar(String cedula, String nombre, String direccion, String telefono, String correo,
                            String formaPago, Integer numeroCuotas, Map<Integer, Integer> items) throws ExcepcionCliente {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("cedula", cedula);
        m.put("nombre", nombre);
        m.put("direccion", direccion);
        m.put("telefono", telefono);
        m.put("correo", correo);
        m.put("formaPago", formaPago);
        m.put("numeroCuotas", numeroCuotas);
        List<Map<String, Object>> l = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : items.entrySet()) {
            Map<String, Object> it = new LinkedHashMap<>();
            it.put("codigoCelular", e.getKey());
            it.put("cantidad", e.getValue());
            l.add(it);
        }
        m.put("items", l);
        return Factura.desdeMapa(datos(enviar("POST", "/facturas", Json.escribir(m))));
    }

    public List<Factura> listarFacturas(String cedula) throws ExcepcionCliente {
        String q = cedula == null || cedula.isBlank() ? "" : "?cedula=" + cod(cedula);
        List<Factura> l = new ArrayList<>();
        for (Object o : lista(enviar("GET", "/facturas" + q, null))) {
            l.add(Factura.desdeMapa((Map<?, ?>) o));
        }
        return l;
    }

    public Factura consultarFactura(int numero) throws ExcepcionCliente {
        return Factura.desdeMapa(datos(enviar("GET", "/facturas/" + numero, null)));
    }

    // ------------------------------------------------------------ crédito directo (Banco BanQuito)

    /** ¿Es sujeto de crédito? ¿Le alcanza el monto máximo para 'monto'? */
    public Evaluacion evaluarCredito(String cedula, double monto) throws ExcepcionCliente {
        return Evaluacion.desdeMapa(datos(enviar("GET", "/credito/evaluar/" + cod(cedula) + "?monto=" + monto, null)));
    }

    /** Préstamos del cliente con sus tablas de amortización. */
    public List<Credito> creditosCliente(String cedula) throws ExcepcionCliente {
        List<Credito> l = new ArrayList<>();
        for (Object o : lista(enviar("GET", "/credito/cliente/" + cod(cedula), null))) {
            l.add(Credito.desdeMapa((Map<?, ?>) o));
        }
        return l;
    }

    public Credito consultarCredito(int codigoCredito) throws ExcepcionCliente {
        return Credito.desdeMapa(datos(enviar("GET", "/credito/" + codigoCredito, null)));
    }

    // ------------------------------------------------------------ HTTP

    private static String cod(String t) {
        return URLEncoder.encode(t == null ? "" : t.trim(), StandardCharsets.UTF_8);
    }

    private static Map<?, ?> datos(Map<String, Object> r) {
        return (Map<?, ?>) r.get("datos");
    }

    private static List<?> lista(Map<String, Object> r) {
        Object d = r.get("datos");
        return d instanceof List<?> l ? l : List.of();
    }

    private Map<String, Object> enviar(String metodo, String ruta, String json) throws ExcepcionCliente {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(urlBase + ruta))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json")
                .header("X-Cliente", cliente);
        if (json != null) {
            b.header("Content-Type", "application/json; charset=UTF-8");
            b.method(metodo, HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));
        } else {
            b.method(metodo, HttpRequest.BodyPublishers.noBody());
        }
        HttpResponse<String> res;
        try {
            res = http.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ExcepcionCliente("No se pudo conectar con la comercializadora (" + urlBase + ")", 0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExcepcionCliente("Operación interrumpida", 0);
        }
        Map<String, Object> r;
        try {
            r = Json.objeto(res.body());
        } catch (RuntimeException e) {
            throw new ExcepcionCliente("Respuesta inesperada del servidor (HTTP " + res.statusCode() + ")", res.statusCode());
        }
        ultimoMensaje = String.valueOf(r.get("mensaje"));
        if (!Boolean.TRUE.equals(r.get("exito"))) {
            throw new ExcepcionCliente(ultimoMensaje, res.statusCode());
        }
        return r;
    }
}
