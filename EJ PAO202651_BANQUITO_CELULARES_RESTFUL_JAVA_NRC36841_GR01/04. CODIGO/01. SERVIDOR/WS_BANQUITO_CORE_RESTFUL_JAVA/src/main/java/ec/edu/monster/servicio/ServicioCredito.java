package ec.edu.monster.servicio;

import ec.edu.monster.dao.ClienteDAO;
import ec.edu.monster.dao.CreditoDAO;
import ec.edu.monster.dao.MovimientoDAO;
import ec.edu.monster.modelo.Cliente;
import ec.edu.monster.modelo.Credito;
import ec.edu.monster.modelo.MontoMaximo;
import ec.edu.monster.modelo.SolicitudCredito;
import ec.edu.monster.modelo.VerificacionCredito;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Reglas de negocio del Módulo de Crédito del Banco BanQuito. Patrón Singleton.
 *
 * <p><b>Sujeto de crédito</b> (las 4 reglas se evalúan siempre y se informan todas):</p>
 * <ol>
 *   <li>El solicitante es cliente del banco.</li>
 *   <li>Tiene al menos un depósito en el último mes.</li>
 *   <li>Si es de género masculino, no es menor de 25 años.</li>
 *   <li>No tiene actualmente un crédito activo.</li>
 * </ol>
 * <p><b>Monto máximo</b>: ((promedio depósitos 3 meses - promedio retiros 3 meses) * 30 %) * 6.</p>
 * <p><b>Otorgar</b>: si es sujeto de crédito y el precio es menor o igual al monto máximo, se
 * aprueba automáticamente y se registra la tabla de amortización (cuota fija mensual, 16.5 % anual,
 * plazo de 3 a 18 meses).</p>
 *
 * @author Grupo 1 - NRC 36841
 */
public final class ServicioCredito {

    private static final ServicioCredito INSTANCIA = new ServicioCredito();

    /** Porcentaje de la fórmula del monto máximo (la fórmula del enunciado usa 30 %). */
    public static final BigDecimal PORCENTAJE_MONTO = new BigDecimal("0.30");
    /** Número de veces que se multiplica: * 6. */
    public static final BigDecimal MULTIPLICADOR = new BigDecimal("6");
    /** Meses que se promedian para el monto máximo. */
    public static final int MESES_PROMEDIO = 3;
    /** Edad mínima para clientes de género masculino. */
    public static final int EDAD_MINIMA_MASCULINO = 25;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private final CreditoDAO creditoDAO = new CreditoDAO();

    private ServicioCredito() {
    }

    public static ServicioCredito getInstancia() {
        return INSTANCIA;
    }

    // ------------------------------------------------------------------ servicio 1
    /** Verifica si la persona con esa cédula es sujeto de crédito. */
    public VerificacionCredito verificarSujeto(String cedula) {
        String ced = cedula == null ? "" : cedula.trim();
        if (!ced.matches("\\d{10}")) {
            throw ExcepcionNegocio.validacion("La cédula debe tener 10 dígitos");
        }
        try {
            return evaluar(ced, clienteDAO.seleccionarPorCedula(ced));
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    private VerificacionCredito evaluar(String cedula, Cliente c) throws SQLException {
        VerificacionCredito v = new VerificacionCredito();
        List<String> motivos = new ArrayList<>();
        v.setCedula(cedula);
        v.setMotivos(motivos);
        // Regla 1
        v.setEsCliente(c != null);
        if (c == null) {
            motivos.add("La persona con cédula " + cedula + " no es cliente del Banco BanQuito");
            v.setSujetoCredito(false);
            return v;
        }
        v.setCodigoCliente(c.getCodigoCliente());
        v.setNombre(c.getNombre());
        // Regla 2
        v.setDepositoUltimoMes(movimientoDAO.tieneDepositoUltimoMes(c.getCodigoCliente()));
        if (!v.isDepositoUltimoMes()) {
            motivos.add("No registra ningún depósito en el último mes");
        }
        // Regla 3
        int edad = Period.between(c.getFechaNacimiento(), LocalDate.now()).getYears();
        v.setEdadValida(!"M".equalsIgnoreCase(c.getGenero()) || edad >= EDAD_MINIMA_MASCULINO);
        if (!v.isEdadValida()) {
            motivos.add("Cliente de género masculino menor de " + EDAD_MINIMA_MASCULINO + " años (tiene " + edad + ")");
        }
        // Regla 4
        v.setSinCreditoActivo(!creditoDAO.tieneCreditoActivo(c.getCodigoCliente()));
        if (!v.isSinCreditoActivo()) {
            motivos.add("Ya tiene un crédito activo en el banco");
        }
        v.setSujetoCredito(v.isDepositoUltimoMes() && v.isEdadValida() && v.isSinCreditoActivo());
        if (v.isSujetoCredito()) {
            motivos.add("Cumple las 4 reglas: es sujeto de crédito");
        }
        return v;
    }

    // ------------------------------------------------------------------ servicio 2
    /** Monto máximo de crédito del cliente según sus movimientos de los últimos 3 meses. */
    public MontoMaximo montoMaximo(int codigoCliente) {
        try {
            Cliente c = clienteDAO.seleccionarPorCodigo(codigoCliente);
            if (c == null) {
                throw ExcepcionNegocio.noEncontrado("No existe el cliente con código " + codigoCliente);
            }
            MovimientoDAO.Resumen dep = movimientoDAO.resumen(codigoCliente, "DEP", MESES_PROMEDIO);
            MovimientoDAO.Resumen ret = movimientoDAO.resumen(codigoCliente, "RET", MESES_PROMEDIO);
            BigDecimal monto = dep.promedio().subtract(ret.promedio()).multiply(PORCENTAJE_MONTO).multiply(MULTIPLICADOR);
            if (monto.signum() < 0) {
                monto = BigDecimal.ZERO;
            }
            MontoMaximo m = new MontoMaximo();
            m.setCodigoCliente(codigoCliente);
            m.setNombre(c.getNombre());
            m.setPromedioDepositos(CalculadoraAmortizacion.r2(dep.promedio()));
            m.setPromedioRetiros(CalculadoraAmortizacion.r2(ret.promedio()));
            m.setNumeroDepositos(dep.cantidad());
            m.setNumeroRetiros(ret.cantidad());
            m.setMontoMaximo(CalculadoraAmortizacion.r2(monto));
            return m;
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    // ------------------------------------------------------------------ servicio 3
    /** Otorga el crédito y registra la tabla de amortización. */
    public Credito otorgar(SolicitudCredito s) {
        if (s == null || s.getCodigoCliente() == null) {
            throw ExcepcionNegocio.validacion("Envíe el código de cliente, el precio del teléfono y el número de cuotas");
        }
        if (s.getPrecio() == null || s.getPrecio().signum() <= 0) {
            throw ExcepcionNegocio.validacion("El precio del teléfono debe ser mayor que cero");
        }
        Integer n = s.getNumeroCuotas();
        if (n == null || n < CalculadoraAmortizacion.CUOTAS_MIN || n > CalculadoraAmortizacion.CUOTAS_MAX) {
            throw ExcepcionNegocio.validacion("El plazo debe ser de " + CalculadoraAmortizacion.CUOTAS_MIN + " a "
                    + CalculadoraAmortizacion.CUOTAS_MAX + " meses");
        }
        BigDecimal precio = CalculadoraAmortizacion.r2(s.getPrecio());
        try {
            Cliente c = clienteDAO.seleccionarPorCodigo(s.getCodigoCliente());
            if (c == null) {
                throw ExcepcionNegocio.noEncontrado("No existe el cliente con código " + s.getCodigoCliente());
            }
            VerificacionCredito v = evaluar(c.getCedula(), c);
            if (!v.isSujetoCredito()) {
                throw ExcepcionNegocio.conflicto("Crédito negado: " + String.join("; ", v.getMotivos()));
            }
            MontoMaximo m = montoMaximo(c.getCodigoCliente());
            if (precio.compareTo(m.getMontoMaximo()) > 0) {
                throw ExcepcionNegocio.conflicto("Crédito negado: el precio $" + precio + " supera el monto máximo aprobado de $"
                        + m.getMontoMaximo());
            }
            Credito cr = new Credito();
            cr.setCodigoCliente(c.getCodigoCliente());
            cr.setMonto(precio);
            cr.setNumeroCuotas(n);
            cr.setTasaAnual(CalculadoraAmortizacion.TASA_ANUAL);
            cr.setCuota(CalculadoraAmortizacion.cuota(precio, n));
            cr.setTabla(CalculadoraAmortizacion.tabla(precio, n));
            int codigo = creditoDAO.insertar(cr);
            return creditoDAO.seleccionar(codigo);
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    // ------------------------------------------------------------------ servicio 4
    /** Tabla de amortización de un crédito otorgado. */
    public Credito consultar(int codigoCredito) {
        try {
            Credito c = creditoDAO.seleccionar(codigoCredito);
            if (c == null) {
                throw ExcepcionNegocio.noEncontrado("No existe el crédito " + codigoCredito);
            }
            return c;
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    /** Créditos (con sus tablas) del cliente con esa cédula. */
    public List<Credito> creditosCliente(String cedula) {
        String ced = cedula == null ? "" : cedula.trim();
        if (!ced.matches("\\d{10}")) {
            throw ExcepcionNegocio.validacion("La cédula debe tener 10 dígitos");
        }
        try {
            if (clienteDAO.seleccionarPorCedula(ced) == null) {
                throw ExcepcionNegocio.noEncontrado("La persona con cédula " + ced + " no es cliente del Banco BanQuito");
            }
            return creditoDAO.listarPorCedula(ced);
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }
}
