package ec.edu.monster.servicio;

import ec.edu.monster.dao.CelularDAO;
import ec.edu.monster.dao.FacturaDAO;
import ec.edu.monster.modelo.Celular;
import ec.edu.monster.modelo.Comprador;
import ec.edu.monster.modelo.CreditoBanco;
import ec.edu.monster.modelo.DetalleFactura;
import ec.edu.monster.modelo.EvaluacionCredito;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.ItemFactura;
import ec.edu.monster.modelo.SolicitudFactura;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Facturación de la venta de teléfonos celulares. Patrón Singleton.
 *
 * <ul>
 *   <li><b>EFECTIVO</b>: descuento del 42 % sobre el precio de venta.</li>
 *   <li><b>CREDITO</b> (crédito directo): se invoca al Banco BanQuito; si el comprador es
 *   sujeto de crédito y el valor es menor o igual a su monto máximo, el banco otorga el
 *   crédito, crea la tabla de amortización y recién entonces se factura.</li>
 * </ul>
 *
 * @author Grupo 1 - NRC 36841
 */
public final class ServicioFacturacion {

    private static final ServicioFacturacion INSTANCIA = new ServicioFacturacion();

    /** Descuento por pago en efectivo: 42 %. */
    public static final BigDecimal DESCUENTO_EFECTIVO = new BigDecimal("0.42");

    private final CelularDAO celularDAO = new CelularDAO();
    private final FacturaDAO facturaDAO = new FacturaDAO();
    private final ServicioCredito credito = ServicioCredito.getInstancia();

    private ServicioFacturacion() {
    }

    public static ServicioFacturacion getInstancia() {
        return INSTANCIA;
    }

    public Factura facturar(SolicitudFactura s) {
        if (s == null) {
            throw ExcepcionNegocio.validacion("Envíe los datos de la factura");
        }
        Comprador comprador = comprador(s);
        String forma = s.getFormaPago() == null ? "" : s.getFormaPago().trim().toUpperCase();
        if (!forma.equals("EFECTIVO") && !forma.equals("CREDITO")) {
            throw ExcepcionNegocio.validacion("La forma de pago debe ser EFECTIVO o CREDITO");
        }
        if (s.getItems() == null || s.getItems().isEmpty()) {
            throw ExcepcionNegocio.validacion("Agregue al menos un teléfono a la factura");
        }
        Factura f = new Factura();
        f.setFormaPago(forma);
        f.setDetalles(detalles(s.getItems()));
        BigDecimal subtotal = f.getDetalles().stream().map(DetalleFactura::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        f.setSubtotal(subtotal);
        if (forma.equals("EFECTIVO")) {
            f.setDescuento(subtotal.multiply(DESCUENTO_EFECTIVO).setScale(2, RoundingMode.HALF_UP));
            f.setTotal(subtotal.subtract(f.getDescuento()));
        } else {
            Integer n = s.getNumeroCuotas();
            if (n == null || n < 3 || n > 18) {
                throw ExcepcionNegocio.validacion("El plazo del crédito debe ser de 3 a 18 meses");
            }
            f.setDescuento(BigDecimal.ZERO.setScale(2));
            f.setTotal(subtotal);
            EvaluacionCredito ev = credito.evaluar(comprador.getCedula(), subtotal);
            if (!ev.isSujetoCredito()) {
                throw ExcepcionNegocio.conflicto("Crédito directo negado por el Banco BanQuito: " + String.join("; ", ev.getMotivos()));
            }
            if (!ev.isAlcanza()) {
                throw ExcepcionNegocio.conflicto("Crédito directo negado: el valor $" + subtotal + " supera el monto máximo aprobado de $"
                        + ev.getMontoMaximo());
            }
            CreditoBanco cr = credito.otorgar(ev.getCodigoCliente(), subtotal, n);
            f.setCodigoCredito(cr.getCodigoCredito());
            f.setNumeroCuotas(cr.getNumeroCuotas());
            f.setValorCuota(cr.getCuota());
            f.setCredito(cr);
        }
        try {
            int numero = facturaDAO.insertar(comprador, f);
            Factura guardada = facturaDAO.seleccionar(numero);
            guardada.setCredito(f.getCredito());
            return guardada;
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    public List<Factura> listar(String cedula) {
        try {
            return facturaDAO.listar(cedula);
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    public Factura consultar(int numero) {
        try {
            Factura f = facturaDAO.seleccionar(numero);
            if (f == null) {
                throw ExcepcionNegocio.noEncontrado("No existe la factura " + numero);
            }
            if (f.getCodigoCredito() != null) {
                f.setCredito(credito.amortizacion(f.getCodigoCredito()));
            }
            return f;
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    private List<DetalleFactura> detalles(List<ItemFactura> items) {
        List<DetalleFactura> l = new ArrayList<>();
        try {
            int linea = 1;
            for (ItemFactura it : items) {
                if (it == null || it.getCodigoCelular() == null) {
                    throw ExcepcionNegocio.validacion("Seleccione el teléfono de cada línea");
                }
                int cant = it.getCantidad() == null ? 1 : it.getCantidad();
                if (cant < 1 || cant > 100) {
                    throw ExcepcionNegocio.validacion("La cantidad debe estar entre 1 y 100");
                }
                Celular c = celularDAO.seleccionar(it.getCodigoCelular());
                if (c == null || !"ACTIVO".equals(c.getEstado())) {
                    throw ExcepcionNegocio.validacion("El celular " + it.getCodigoCelular() + " no está disponible para la venta");
                }
                DetalleFactura d = new DetalleFactura();
                d.setLinea(linea++);
                d.setCodigoCelular(c.getCodigo());
                d.setCelular(c.getMarca() + " " + c.getModelo());
                d.setCantidad(cant);
                d.setPrecioUnitario(c.getPrecio());
                d.setSubtotal(c.getPrecio().multiply(BigDecimal.valueOf(cant)));
                l.add(d);
            }
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
        return l;
    }

    private Comprador comprador(SolicitudFactura s) {
        Comprador c = new Comprador();
        c.setCedula(validarCedula(s.getCedula()));
        String nombre = s.getNombre() == null ? "" : s.getNombre().trim().toUpperCase();
        if (nombre.length() < 3) {
            throw ExcepcionNegocio.validacion("Ingrese el nombre del cliente");
        }
        c.setNombre(nombre);
        c.setDireccion(vacio(s.getDireccion()));
        c.setTelefono(vacio(s.getTelefono()));
        c.setCorreo(vacio(s.getCorreo()));
        return c;
    }

    private static String vacio(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    /** Cédula ecuatoriana: 10 dígitos, provincia 01-24 o 30, tercer dígito menor a 6, módulo 10. */
    public static String validarCedula(String cedula) {
        String c = cedula == null ? "" : cedula.trim();
        if (!c.matches("\\d{10}")) {
            throw ExcepcionNegocio.validacion("La cédula debe tener 10 dígitos");
        }
        int prov = Integer.parseInt(c.substring(0, 2));
        if (!((prov >= 1 && prov <= 24) || prov == 30) || c.charAt(2) - '0' >= 6) {
            throw ExcepcionNegocio.validacion("La cédula " + c + " no es válida");
        }
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int v = (c.charAt(i) - '0') * (i % 2 == 0 ? 2 : 1);
            suma += v > 9 ? v - 9 : v;
        }
        if ((10 - suma % 10) % 10 != c.charAt(9) - '0') {
            throw ExcepcionNegocio.validacion("La cédula " + c + " no es válida (dígito verificador)");
        }
        return c;
    }
}
