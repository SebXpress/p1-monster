package ec.edu.monster.modelo;

/**
 * Datos para otorgar un crédito: código de cliente, precio del teléfono y número de cuotas.
 *
 * @author Grupo 1 - NRC 36841
 */
public class SolicitudCredito {

    private Integer codigoCliente;
    private java.math.BigDecimal precio;
    private Integer numeroCuotas;

    public Integer getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(Integer codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public java.math.BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(java.math.BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getNumeroCuotas() {
        return numeroCuotas;
    }

    public void setNumeroCuotas(Integer numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }

}
