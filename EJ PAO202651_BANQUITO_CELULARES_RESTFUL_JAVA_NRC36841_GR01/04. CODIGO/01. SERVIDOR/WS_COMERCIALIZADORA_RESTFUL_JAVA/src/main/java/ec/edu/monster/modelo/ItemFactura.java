package ec.edu.monster.modelo;

/**
 * Teléfono y cantidad que se quiere facturar.
 *
 * @author Grupo 1 - NRC 36841
 */
public class ItemFactura {

    private Integer codigoCelular;
    private Integer cantidad;

    public Integer getCodigoCelular() {
        return codigoCelular;
    }

    public void setCodigoCelular(Integer codigoCelular) {
        this.codigoCelular = codigoCelular;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

}
