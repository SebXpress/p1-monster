package ec.edu.monster.servicio;

import ec.edu.monster.integracion.ClienteBanQuito;
import ec.edu.monster.modelo.CreditoBanco;
import ec.edu.monster.modelo.EvaluacionCredito;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Crédito directo a través del Banco BanQuito (invoca sus servicios web).
 *
 * @author Grupo 1 - NRC 36841
 */
public final class ServicioCredito {

    private static final ServicioCredito INSTANCIA = new ServicioCredito();
    private final ClienteBanQuito banco = ClienteBanQuito.getInstancia();

    private ServicioCredito() {
    }

    public static ServicioCredito getInstancia() {
        return INSTANCIA;
    }

    /**
     * Pregunta al banco si el comprador es sujeto de crédito y, si lo es, su
     * monto máximo; indica si alcanza para el valor a financiar.
     */
    @SuppressWarnings("unchecked")
    public EvaluacionCredito evaluar(String cedula, BigDecimal montoSolicitado) {
        String ced = ServicioFacturacion.validarCedula(cedula);
        ClienteBanQuito.Respuesta r = banco.sujetoCredito(ced);
        if (!r.exito()) {
            throw ExcepcionNegocio.validacion("Banco BanQuito: " + r.mensaje());
        }
        Map<String, Object> d = r.mapa();
        EvaluacionCredito e = new EvaluacionCredito();
        e.setCedula(ced);
        e.setEsCliente(Boolean.TRUE.equals(d.get("esCliente")));
        e.setSujetoCredito(Boolean.TRUE.equals(d.get("sujetoCredito")));
        e.setNombre((String) d.get("nombre"));
        e.setCodigoCliente(d.get("codigoCliente") == null ? null : ClienteBanQuito.ent(d.get("codigoCliente")));
        e.setMotivos(new ArrayList<>((List<String>) d.getOrDefault("motivos", List.of())));
        e.setMontoSolicitado(montoSolicitado);
        if (e.isSujetoCredito()) {
            ClienteBanQuito.Respuesta m = banco.montoMaximo(e.getCodigoCliente());
            if (!m.exito()) {
                throw ExcepcionNegocio.conflicto("Banco BanQuito: " + m.mensaje());
            }
            e.setMontoMaximo(ClienteBanQuito.dec(m.mapa().get("montoMaximo")));
            e.setAlcanza(montoSolicitado == null || montoSolicitado.compareTo(e.getMontoMaximo()) <= 0);
        }
        return e;
    }

    /** Pide al banco que otorgue el crédito y cree la tabla de amortización. */
    public CreditoBanco otorgar(int codigoCliente, BigDecimal precio, int numeroCuotas) {
        ClienteBanQuito.Respuesta r = banco.otorgarCredito(codigoCliente, precio, numeroCuotas);
        if (!r.exito()) {
            throw ExcepcionNegocio.conflicto("Banco BanQuito: " + r.mensaje());
        }
        return ClienteBanQuito.credito(r.mapa());
    }

    /** Tabla de amortización de un crédito otorgado (servicio del banco). */
    public CreditoBanco amortizacion(int codigoCredito) {
        ClienteBanQuito.Respuesta r = banco.amortizacion(codigoCredito);
        if (!r.exito()) {
            throw ExcepcionNegocio.noEncontrado("Banco BanQuito: " + r.mensaje());
        }
        return ClienteBanQuito.credito(r.mapa());
    }

    /** Créditos del cliente (por cédula) con sus tablas de amortización. */
    @SuppressWarnings("unchecked")
    public List<CreditoBanco> creditosCliente(String cedula) {
        String ced = ServicioFacturacion.validarCedula(cedula);
        ClienteBanQuito.Respuesta r = banco.creditosCliente(ced);
        if (!r.exito()) {
            throw ExcepcionNegocio.noEncontrado("Banco BanQuito: " + r.mensaje());
        }
        List<CreditoBanco> l = new ArrayList<>();
        for (Object o : (List<Object>) r.datos()) {
            l.add(ClienteBanQuito.credito((Map<String, Object>) o));
        }
        return l;
    }
}
