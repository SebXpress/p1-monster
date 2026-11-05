package ec.edu.monster.modelo;

/**
 * Respuesta del banco para el comprador: sujeto de crédito, motivos y monto máximo.
 *
 * @author Grupo 1 - NRC 36841
 */
public class EvaluacionCredito {

    private String cedula;
    private Integer codigoCliente;
    private String nombre;
    private boolean esCliente;
    private boolean sujetoCredito;
    private java.util.List<String> motivos;
    private java.math.BigDecimal montoMaximo;
    private java.math.BigDecimal montoSolicitado;
    private boolean alcanza;

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Integer getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(Integer codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEsCliente() {
        return esCliente;
    }

    public void setEsCliente(boolean esCliente) {
        this.esCliente = esCliente;
    }

    public boolean isSujetoCredito() {
        return sujetoCredito;
    }

    public void setSujetoCredito(boolean sujetoCredito) {
        this.sujetoCredito = sujetoCredito;
    }

    public java.util.List<String> getMotivos() {
        return motivos;
    }

    public void setMotivos(java.util.List<String> motivos) {
        this.motivos = motivos;
    }

    public java.math.BigDecimal getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(java.math.BigDecimal montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    public java.math.BigDecimal getMontoSolicitado() {
        return montoSolicitado;
    }

    public void setMontoSolicitado(java.math.BigDecimal montoSolicitado) {
        this.montoSolicitado = montoSolicitado;
    }

    public boolean isAlcanza() {
        return alcanza;
    }

    public void setAlcanza(boolean alcanza) {
        this.alcanza = alcanza;
    }

}
