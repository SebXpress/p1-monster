package ec.edu.monster.climov.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fila de la tabla de amortización.
 *
 * @author Grupo 1 - NRC 36841
 */
public class Cuota {

    private int numeroCuota;
    private double valorCuota;
    private double interesPagado;
    private double capitalPagado;
    private double saldo;

    /** Convierte el JSON del servidor en Cuota. */
    public static Cuota desdeMapa(Map<?, ?> m) {
        Cuota o = new Cuota();
        o.numeroCuota = i(m.get("numeroCuota"));
        o.valorCuota = d(m.get("valorCuota"));
        o.interesPagado = d(m.get("interesPagado"));
        o.capitalPagado = d(m.get("capitalPagado"));
        o.saldo = d(m.get("saldo"));
        return o;
    }

    public int getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(int numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public double getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(double valorCuota) {
        this.valorCuota = valorCuota;
    }

    public double getInteresPagado() {
        return interesPagado;
    }

    public void setInteresPagado(double interesPagado) {
        this.interesPagado = interesPagado;
    }

    public double getCapitalPagado() {
        return capitalPagado;
    }

    public void setCapitalPagado(double capitalPagado) {
        this.capitalPagado = capitalPagado;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
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
