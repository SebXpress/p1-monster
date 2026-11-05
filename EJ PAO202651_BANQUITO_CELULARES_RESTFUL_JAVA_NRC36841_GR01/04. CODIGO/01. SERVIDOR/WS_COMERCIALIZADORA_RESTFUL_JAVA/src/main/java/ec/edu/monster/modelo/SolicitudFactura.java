package ec.edu.monster.modelo;

/**
 * Datos para facturar: comprador, forma de pago (EFECTIVO o CREDITO), cuotas y teléfonos.
 *
 * @author Grupo 1 - NRC 36841
 */
public class SolicitudFactura {

    private String cedula;
    private String nombre;
    private String direccion;
    private String telefono;
    private String correo;
    private String formaPago;
    private Integer numeroCuotas;
    private java.util.List<ItemFactura> items;

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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public Integer getNumeroCuotas() {
        return numeroCuotas;
    }

    public void setNumeroCuotas(Integer numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }

    public java.util.List<ItemFactura> getItems() {
        return items;
    }

    public void setItems(java.util.List<ItemFactura> items) {
        this.items = items;
    }

}
