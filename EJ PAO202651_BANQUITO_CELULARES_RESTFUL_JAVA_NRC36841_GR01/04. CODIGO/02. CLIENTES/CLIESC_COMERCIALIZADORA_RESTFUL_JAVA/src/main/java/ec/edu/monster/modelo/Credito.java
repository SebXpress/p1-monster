package ec.edu.monster.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Préstamo otorgado por el Banco BanQuito con su tabla de amortización.
 *
 * @author Grupo 1 - NRC 36841
 */
public class Credito {

    private int codigoCredito;
    private String cedula;
    private String nombre;
    private double monto;
    private int numeroCuotas;
    private double tasaAnual;
    private double cuota;
    private double totalIntereses;
    private double totalPagar;
    private String fecha;
    private String estado;
    private List<Cuota> tabla;

    /** Convierte el JSON del servidor en Credito. */
    public static Credito desdeMapa(Map<?, ?> m) {
        Credito o = new Credito();
        o.codigoCredito = i(m.get("codigoCredito"));
        o.cedula = s(m.get("cedula"));
        o.nombre = s(m.get("nombre"));
        o.monto = d(m.get("monto"));
        o.numeroCuotas = i(m.get("numeroCuotas"));
        o.tasaAnual = d(m.get("tasaAnual"));
        o.cuota = d(m.get("cuota"));
        o.totalIntereses = d(m.get("totalIntereses"));
        o.totalPagar = d(m.get("totalPagar"));
        o.fecha = s(m.get("fecha"));
        o.estado = s(m.get("estado"));
        o.tabla = lista(m.get("tabla"));
        return o;
    }

    public int getCodigoCredito() {
        return codigoCredito;
    }

    public void setCodigoCredito(int codigoCredito) {
        this.codigoCredito = codigoCredito;
    }

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

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public int getNumeroCuotas() {
        return numeroCuotas;
    }

    public void setNumeroCuotas(int numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }

    public double getTasaAnual() {
        return tasaAnual;
    }

    public void setTasaAnual(double tasaAnual) {
        this.tasaAnual = tasaAnual;
    }

    public double getCuota() {
        return cuota;
    }

    public void setCuota(double cuota) {
        this.cuota = cuota;
    }

    public double getTotalIntereses() {
        return totalIntereses;
    }

    public void setTotalIntereses(double totalIntereses) {
        this.totalIntereses = totalIntereses;
    }

    public double getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Cuota> getTabla() {
        return tabla;
    }

    public void setTabla(List<Cuota> tabla) {
        this.tabla = tabla;
    }

    static List<Cuota> lista(Object o) {
        List<Cuota> l = new ArrayList<>();
        if (o instanceof List<?> x) {
            for (Object e : x) {
                l.add(Cuota.desdeMapa((Map<?, ?>) e));
            }
        }
        return l;
    }

    @Override
    public String toString() {
        return String.format("Crédito %d - $%,.2f en %d cuotas (%s)", codigoCredito, monto, numeroCuotas, estado);
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
