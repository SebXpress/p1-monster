package ec.edu.monster.modelo;

/**
 * Crédito otorgado con su tabla de amortización (tablas CREDITO y AMORTIZACION).
 *
 * @author Grupo 1 - NRC 36841
 */
public class Credito {

    private int codigoCredito;
    private int codigoCliente;
    private String cedula;
    private String nombre;
    private java.math.BigDecimal monto;
    private int numeroCuotas;
    private java.math.BigDecimal tasaAnual;
    private java.math.BigDecimal cuota;
    private java.math.BigDecimal totalIntereses;
    private java.math.BigDecimal totalPagar;
    private String fecha;
    private String estado;
    private java.util.List<CuotaAmortizacion> tabla;

    public int getCodigoCredito() {
        return codigoCredito;
    }

    public void setCodigoCredito(int codigoCredito) {
        this.codigoCredito = codigoCredito;
    }

    public int getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(int codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public java.math.BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(java.math.BigDecimal monto) {
        this.monto = monto;
    }

    public int getNumeroCuotas() {
        return numeroCuotas;
    }

    public void setNumeroCuotas(int numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }

    public java.math.BigDecimal getTasaAnual() {
        return tasaAnual;
    }

    public void setTasaAnual(java.math.BigDecimal tasaAnual) {
        this.tasaAnual = tasaAnual;
    }

    public java.math.BigDecimal getCuota() {
        return cuota;
    }

    public void setCuota(java.math.BigDecimal cuota) {
        this.cuota = cuota;
    }

    public java.math.BigDecimal getTotalIntereses() {
        return totalIntereses;
    }

    public void setTotalIntereses(java.math.BigDecimal totalIntereses) {
        this.totalIntereses = totalIntereses;
    }

    public java.math.BigDecimal getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(java.math.BigDecimal totalPagar) {
        this.totalPagar = totalPagar;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public java.util.List<CuotaAmortizacion> getTabla() {
        return tabla;
    }

    public void setTabla(java.util.List<CuotaAmortizacion> tabla) {
        this.tabla = tabla;
    }

}
