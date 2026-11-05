package ec.edu.monster.climov.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Línea de una factura.
 *
 * @author Grupo 1 - NRC 36841
 */
public class DetalleFactura {

    private int linea;
    private int codigoCelular;
    private String celular;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    /** Convierte el JSON del servidor en DetalleFactura. */
    public static DetalleFactura desdeMapa(Map<?, ?> m) {
        DetalleFactura o = new DetalleFactura();
        o.linea = i(m.get("linea"));
        o.codigoCelular = i(m.get("codigoCelular"));
        o.celular = s(m.get("celular"));
        o.cantidad = i(m.get("cantidad"));
        o.precioUnitario = d(m.get("precioUnitario"));
        o.subtotal = d(m.get("subtotal"));
        return o;
    }

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

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
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
