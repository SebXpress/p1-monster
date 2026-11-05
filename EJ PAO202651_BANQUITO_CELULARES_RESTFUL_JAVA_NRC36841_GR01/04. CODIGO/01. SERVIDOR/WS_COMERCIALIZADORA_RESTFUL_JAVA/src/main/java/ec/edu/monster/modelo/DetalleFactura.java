package ec.edu.monster.modelo;

/**
 * Línea de la factura (tabla DETALLE_FACTURA).
 *
 * @author Grupo 1 - NRC 36841
 */
public class DetalleFactura {

    private int linea;
    private int codigoCelular;
    private String celular;
    private int cantidad;
    private java.math.BigDecimal precioUnitario;
    private java.math.BigDecimal subtotal;

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public int getCodigoCelular() {
        return codigoCelular;
    }

    public void setCodigoCelular(int codigoCelular) {
        this.codigoCelular = codigoCelular;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public java.math.BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(java.math.BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public java.math.BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(java.math.BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

}
