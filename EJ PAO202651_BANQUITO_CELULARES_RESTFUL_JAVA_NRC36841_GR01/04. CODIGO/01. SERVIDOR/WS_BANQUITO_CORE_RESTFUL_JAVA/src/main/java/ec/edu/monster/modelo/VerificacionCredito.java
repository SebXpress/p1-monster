package ec.edu.monster.modelo;

/**
 * Resultado de verificar si una persona es sujeto de crédito (reglas 1 a 4 del enunciado).
 *
 * @author Grupo 1 - NRC 36841
 */
public class VerificacionCredito {

    private String cedula;
    private Integer codigoCliente;
    private String nombre;
    private boolean esCliente;
    private boolean depositoUltimoMes;
    private boolean edadValida;
    private boolean sinCreditoActivo;
    private boolean sujetoCredito;
    private java.util.List<String> motivos;

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

    public boolean isDepositoUltimoMes() {
        return depositoUltimoMes;
    }

    public void setDepositoUltimoMes(boolean depositoUltimoMes) {
        this.depositoUltimoMes = depositoUltimoMes;
    }

    public boolean isEdadValida() {
        return edadValida;
    }

    public void setEdadValida(boolean edadValida) {
        this.edadValida = edadValida;
    }

    public boolean isSinCreditoActivo() {
        return sinCreditoActivo;
    }

    public void setSinCreditoActivo(boolean sinCreditoActivo) {
        this.sinCreditoActivo = sinCreditoActivo;
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

}
