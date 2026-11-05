package ec.edu.monster.modelo;

/**
 * Monto máximo de crédito: ((promedio depósitos - promedio retiros) * 30 %) * 6, últimos 3 meses.
 *
 * @author Grupo 1 - NRC 36841
 */
public class MontoMaximo {

    private int codigoCliente;
    private String nombre;
    private java.math.BigDecimal promedioDepositos;
    private java.math.BigDecimal promedioRetiros;
    private int numeroDepositos;
    private int numeroRetiros;
    private java.math.BigDecimal montoMaximo;

    public int getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(int codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public java.math.BigDecimal getPromedioDepositos() {
        return promedioDepositos;
    }

    public void setPromedioDepositos(java.math.BigDecimal promedioDepositos) {
        this.promedioDepositos = promedioDepositos;
    }

    public java.math.BigDecimal getPromedioRetiros() {
        return promedioRetiros;
    }

    public void setPromedioRetiros(java.math.BigDecimal promedioRetiros) {
        this.promedioRetiros = promedioRetiros;
    }

    public int getNumeroDepositos() {
        return numeroDepositos;
    }

    public void setNumeroDepositos(int numeroDepositos) {
        this.numeroDepositos = numeroDepositos;
    }

    public int getNumeroRetiros() {
        return numeroRetiros;
    }

    public void setNumeroRetiros(int numeroRetiros) {
        this.numeroRetiros = numeroRetiros;
    }

    public java.math.BigDecimal getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(java.math.BigDecimal montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

}
