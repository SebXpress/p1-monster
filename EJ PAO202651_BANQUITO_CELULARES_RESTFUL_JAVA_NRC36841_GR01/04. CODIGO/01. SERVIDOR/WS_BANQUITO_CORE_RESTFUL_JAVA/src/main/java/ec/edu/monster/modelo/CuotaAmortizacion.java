package ec.edu.monster.modelo;

/**
 * Una fila de la tabla de amortización (# cuota, valor cuota, interés, capital y saldo).
 *
 * @author Grupo 1 - NRC 36841
 */
public class CuotaAmortizacion {

    private int numeroCuota;
    private java.math.BigDecimal valorCuota;
    private java.math.BigDecimal interesPagado;
    private java.math.BigDecimal capitalPagado;
    private java.math.BigDecimal saldo;

    public int getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(int numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public java.math.BigDecimal getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(java.math.BigDecimal valorCuota) {
        this.valorCuota = valorCuota;
    }

    public java.math.BigDecimal getInteresPagado() {
        return interesPagado;
    }

    public void setInteresPagado(java.math.BigDecimal interesPagado) {
        this.interesPagado = interesPagado;
    }

    public java.math.BigDecimal getCapitalPagado() {
        return capitalPagado;
    }

    public void setCapitalPagado(java.math.BigDecimal capitalPagado) {
        this.capitalPagado = capitalPagado;
    }

    public java.math.BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(java.math.BigDecimal saldo) {
        this.saldo = saldo;
    }

}
