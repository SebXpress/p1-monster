package ec.edu.monster.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Factura de venta (efectivo o crédito directo).
 *
 * @author Grupo 1 - NRC 36841
 */
public class Factura {

    private int numero;
    private String fecha;
    private String cedula;
    private String cliente;
    private String formaPago;
    private double subtotal;
    private double descuento;
    private double total;
    private Integer codigoCredito;
    private Integer numeroCuotas;
    private double valorCuota;
    private List<DetalleFactura> detalles;
    private Credito credito;

    /** Convierte el JSON del servidor en Factura. */
    public static Factura desdeMapa(Map<?, ?> m) {
        Factura o = new Factura();
        o.numero = i(m.get("numero"));
        o.fecha = s(m.get("fecha"));
        o.cedula = s(m.get("cedula"));
        o.cliente = s(m.get("cliente"));
        o.formaPago = s(m.get("formaPago"));
        o.subtotal = d(m.get("subtotal"));
        o.descuento = d(m.get("descuento"));
        o.total = d(m.get("total"));
        o.codigoCredito = ni(m.get("codigoCredito"));
        o.numeroCuotas = ni(m.get("numeroCuotas"));
        o.valorCuota = d(m.get("valorCuota"));
        o.detalles = detalles(m.get("detalles"));
        o.credito = m.get("credito") instanceof Map<?, ?> c ? Credito.desdeMapa(c) : null;
        return o;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Integer getCodigoCredito() {
        return codigoCredito;
    }

    public void setCodigoCredito(Integer codigoCredito) {
        this.codigoCredito = codigoCredito;
    }

    public Integer getNumeroCuotas() {
        return numeroCuotas;
    }

    public void setNumeroCuotas(Integer numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }

    public double getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(double valorCuota) {
        this.valorCuota = valorCuota;
    }

    public List<DetalleFactura> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFactura> detalles) {
        this.detalles = detalles;
    }

    public Credito getCredito() {
        return credito;
    }

    public void setCredito(Credito credito) {
        this.credito = credito;
    }

    static List<DetalleFactura> detalles(Object o) {
        List<DetalleFactura> l = new ArrayList<>();
        if (o instanceof List<?> x) {
            for (Object e : x) {
                l.add(DetalleFactura.desdeMapa((Map<?, ?>) e));
            }
        }
        return l;
    }

    public boolean esCredito() {
        return "CREDITO".equals(formaPago);
    }

    static String s(Object o) {
        return o == null ? null : o.toString();
    }

    static double d(Object o) {
        return o == null ? 0 : ((Number) o).doubleValue();
    }

    static int i(Object o) {
        return o == null ? 0 : ((Number) o).intValue();
    }

    static Integer ni(Object o) {
        return o == null ? null : ((Number) o).intValue();
    }
}
