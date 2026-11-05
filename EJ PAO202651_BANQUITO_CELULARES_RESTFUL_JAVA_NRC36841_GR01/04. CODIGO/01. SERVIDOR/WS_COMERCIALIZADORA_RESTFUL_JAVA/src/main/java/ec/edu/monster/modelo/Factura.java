package ec.edu.monster.modelo;

/**
 * Factura de venta (tabla FACTURA) con su detalle y, si es a crédito, el crédito del banco.
 *
 * @author Grupo 1 - NRC 36841
 */
public class Factura {

    private int numero;
    private String fecha;
    private String cedula;
    private String cliente;
    private String formaPago;
    private java.math.BigDecimal subtotal;
    private java.math.BigDecimal descuento;
    private java.math.BigDecimal total;
    private Integer codigoCredito;
    private Integer numeroCuotas;
    private java.math.BigDecimal valorCuota;
    private java.util.List<DetalleFactura> detalles;
    private CreditoBanco credito;

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

    public java.math.BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(java.math.BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public java.math.BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(java.math.BigDecimal descuento) {
        this.descuento = descuento;
    }

    public java.math.BigDecimal getTotal() {
        return total;
    }

    public void setTotal(java.math.BigDecimal total) {
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

    public java.math.BigDecimal getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(java.math.BigDecimal valorCuota) {
        this.valorCuota = valorCuota;
    }

    public java.util.List<DetalleFactura> getDetalles() {
        return detalles;
    }

    public void setDetalles(java.util.List<DetalleFactura> detalles) {
        this.detalles = detalles;
    }

    public CreditoBanco getCredito() {
        return credito;
    }

    public void setCredito(CreditoBanco credito) {
        this.credito = credito;
    }

}
