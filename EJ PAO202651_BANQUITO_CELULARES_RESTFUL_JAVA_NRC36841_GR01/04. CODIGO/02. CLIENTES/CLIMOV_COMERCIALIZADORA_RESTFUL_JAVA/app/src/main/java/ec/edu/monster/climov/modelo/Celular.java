package ec.edu.monster.climov.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Teléfono del catálogo de la comercializadora.
 *
 * @author Grupo 1 - NRC 36841
 */
public class Celular {

    private Integer codigo;
    private String marca;
    private String modelo;
    private String descripcion;
    private double precio;
    private String estado;

    /** Convierte el JSON del servidor en Celular. */
    public static Celular desdeMapa(Map<?, ?> m) {
        Celular o = new Celular();
        o.codigo = ni(m.get("codigo"));
        o.marca = s(m.get("marca"));
        o.modelo = s(m.get("modelo"));
        o.descripcion = s(m.get("descripcion"));
        o.precio = d(m.get("precio"));
        o.estado = s(m.get("estado"));
        return o;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /** Nombre corto para listas: marca + modelo. */
    public String nombre() {
        return marca + " " + modelo;
    }

    @Override
    public String toString() {
        return String.format("%s %s - $%,.2f", marca, modelo, precio);
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
