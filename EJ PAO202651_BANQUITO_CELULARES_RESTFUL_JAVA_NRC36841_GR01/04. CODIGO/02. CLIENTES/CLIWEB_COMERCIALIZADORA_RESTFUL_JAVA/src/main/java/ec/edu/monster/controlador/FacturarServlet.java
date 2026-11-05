package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Celular;
import ec.edu.monster.modelo.ExcepcionCliente;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.ServicioRest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Facturación de la venta: carrito (guardado en la sesión), datos del
 * comprador y forma de pago.
 * <ul>
 *   <li>EFECTIVO: 42 % de descuento.</li>
 *   <li>CREDITO: la comercializadora consulta al Banco BanQuito (sujeto de crédito
 *   y monto máximo) y, si aprueba, el banco otorga el crédito de 3 a 18 cuotas.</li>
 * </ul>
 *
 * @author Grupo 1 - NRC 36841
 */
@WebServlet(name = "FacturarServlet", urlPatterns = {"/facturar"})
public class FacturarServlet extends ServletBase {

    public static final double DESCUENTO_EFECTIVO = 0.42;
    public static final double TASA_ANUAL = 16.5;
    private static final String[] CAMPOS = {"cedula", "nombre", "direccion", "telefono", "correo", "formaPago", "numeroCuotas"};

    /** Línea del carrito para la vista. */
    public record Linea(int codigo, String celular, int cantidad, double precio, double subtotal) {

        public int getCodigo() {
            return codigo;
        }

        public String getCelular() {
            return celular;
        }

        public int getCantidad() {
            return cantidad;
        }

        public double getPrecio() {
            return precio;
        }

        public double getSubtotal() {
            return subtotal;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<Integer, Integer> carrito(HttpSession s) {
        Map<Integer, Integer> c = (Map<Integer, Integer>) s.getAttribute("carrito");
        if (c == null) {
            c = new LinkedHashMap<>();
            s.setAttribute("carrito", c);
        }
        return c;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> venta(HttpSession s) {
        Map<String, String> v = (Map<String, String>) s.getAttribute("venta");
        if (v == null) {
            v = new LinkedHashMap<>();
            v.put("formaPago", "EFECTIVO");
            v.put("numeroCuotas", "6");
            s.setAttribute("venta", v);
        }
        return v;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ServicioRest s = servicio(req);
        HttpSession ses = req.getSession();
        Map<String, String> venta = venta(ses);
        List<Linea> lineas = new ArrayList<>();
        double subtotal = 0;
        try {
            List<Celular> celulares = s.listarCelulares(null, true);
            req.setAttribute("celulares", celulares);
            Map<Integer, Celular> porCodigo = new LinkedHashMap<>();
            celulares.forEach(c -> porCodigo.put(c.getCodigo(), c));
            for (Map.Entry<Integer, Integer> e : carrito(ses).entrySet()) {
                Celular c = porCodigo.get(e.getKey());
                if (c != null) {
                    double st = c.getPrecio() * e.getValue();
                    lineas.add(new Linea(c.getCodigo(), c.nombre(), e.getValue(), c.getPrecio(), st));
                    subtotal += st;
                }
            }
        } catch (ExcepcionCliente e) {
            req.setAttribute("error", e.getMessage());
        }
        subtotal = Math.round(subtotal * 100) / 100.0;
        boolean credito = "CREDITO".equals(venta.get("formaPago"));
        double descuento = credito ? 0 : Math.round(subtotal * DESCUENTO_EFECTIVO * 100) / 100.0;
        int n = Integer.parseInt(venta.getOrDefault("numeroCuotas", "6"));
        double i = TASA_ANUAL / 100 / 12;
        req.setAttribute("lineas", lineas);
        req.setAttribute("venta", venta);
        req.setAttribute("subtotal", subtotal);
        req.setAttribute("descuento", descuento);
        req.setAttribute("total", subtotal - descuento);
        req.setAttribute("cuotaEstimada", subtotal > 0 ? subtotal / ((1 - Math.pow(1 + i, -n)) / i) : 0);
        vista(req, res, "facturar.jsp", "facturar");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ServicioRest s = servicio(req);
        HttpSession ses = req.getSession();
        Map<String, String> venta = venta(ses);
        for (String c : CAMPOS) {
            if (req.getParameter(c) != null) {
                venta.put(c, param(req, c));
            }
        }
        Map<Integer, Integer> carrito = carrito(ses);
        String accion = param(req, "accion");
        if (accion.startsWith("quitar:")) {
            try {
                carrito.remove(Integer.valueOf(accion.substring(7)));
            } catch (NumberFormatException ignorada) {
                // código no válido: no se quita nada
            }
            accion = "";
        }
        try {
            switch (accion) {
                case "agregar" -> {
                    int codigo = entero(req, "codigoCelular", 0);
                    int cantidad = Math.max(1, Math.min(100, entero(req, "cantidad", 1)));
                    if (codigo > 0) {
                        carrito.merge(codigo, cantidad, (a, b) -> Math.min(100, a + b));
                    }
                }
                case "limpiar" -> {
                    carrito.clear();
                    ses.removeAttribute("venta");
                }
                case "evaluar" -> {
                    double monto = 0;
                    for (Map.Entry<Integer, Integer> e : carrito.entrySet()) {
                        monto += s.consultarCelular(e.getKey()).getPrecio() * e.getValue();
                    }
                    if (monto <= 0) {
                        throw new ExcepcionCliente("Agregue al menos un teléfono para consultar el crédito", 400);
                    }
                    ses.setAttribute("evaluacion", s.evaluarCredito(venta.get("cedula"), Math.round(monto * 100) / 100.0));
                    if (venta.getOrDefault("nombre", "").isBlank()) {
                        venta.put("nombre", ((ec.edu.monster.modelo.Evaluacion) ses.getAttribute("evaluacion")).getNombre());
                    }
                    res.sendRedirect("facturar#pago");
                    return;
                }
                case "facturar" -> {
                    boolean credito = "CREDITO".equals(venta.get("formaPago"));
                    Factura f = s.facturar(venta.get("cedula"), venta.get("nombre"), venta.get("direccion"), venta.get("telefono"),
                            venta.get("correo"), credito ? "CREDITO" : "EFECTIVO",
                            credito ? Integer.valueOf(venta.getOrDefault("numeroCuotas", "6")) : null, carrito);
                    carrito.clear();
                    ses.removeAttribute("venta");
                    ses.removeAttribute("evaluacion");
                    ses.setAttribute("facturaNueva", f);
                    aviso(req, "ok", s.getUltimoMensaje());
                    res.sendRedirect("factura?numero=" + f.getNumero());
                    return;
                }
                default -> { }
            }
            ses.removeAttribute("evaluacion");
        } catch (ExcepcionCliente e) {
            aviso(req, "error", e.getMessage());
        }
        res.sendRedirect("facturar");
    }
}
