package ec.edu.monster.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Respuesta del banco: sujeto de crédito, motivos y monto máximo.
 *
 * @author Grupo 1 - NRC 36841
 */
public class Evaluacion {

    private String cedula;
    private Integer codigoCliente;
    private String nombre;
    private boolean esCliente;
    private boolean sujetoCredito;
    private List<String> motivos;
    private double montoMaximo;
    private boolean alcanza;

    /** Convierte el JSON del servidor en Evaluacion. */
    public static Evaluacion desdeMapa(Map<?, ?> m) {
        Evaluacion o = new Evaluacion();
        o.cedula = s(m.get("cedula"));
        o.codigoCliente = ni(m.get("codigoCliente"));
        o.nombre = s(m.get("nombre"));
        o.esCliente = Boolean.TRUE.equals(m.get("esCliente"));
        o.sujetoCredito = Boolean.TRUE.equals(m.get("sujetoCredito"));
        o.motivos = motivos(m.get("motivos"));
        o.montoMaximo = d(m.get("montoMaximo"));
        o.alcanza = Boolean.TRUE.equals(m.get("alcanza"));
        return o;
    }

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

    public List<String> getMotivos() {
        return motivos;
    }

    public void setMotivos(List<String> motivos) {
        this.motivos = motivos;
    }

    public double getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(double montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    public boolean isAlcanza() {
        return alcanza;
    }

    public void setAlcanza(boolean alcanza) {
        this.alcanza = alcanza;
    }

    static List<String> motivos(Object o) {
        List<String> l = new ArrayList<>();
        if (o instanceof List<?> x) {
            for (Object e : x) {
                l.add(String.valueOf(e));
            }
        }
        return l;
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
