package ec.edu.monster.dao;

import ec.edu.monster.db.ConexionBD;
import ec.edu.monster.modelo.Credito;
import ec.edu.monster.modelo.CuotaAmortizacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a CREDITO y AMORTIZACION (módulo de crédito en el esquema del CORE).
 *
 * @author Grupo 1 - NRC 36841
 */
public class CreditoDAO {

    private static final String CAMPOS = "SELECT cr.COD_CREDITO, cr.COD_CLIENTE, cl.CEDULA, cl.NOMBRE, cr.MONTO, cr.NUM_CUOTAS, "
            + "cr.TASA_ANUAL, cr.CUOTA, cr.FECHA, cr.ESTADO FROM CREDITO cr JOIN CLIENTE cl ON cl.COD_CLIENTE = cr.COD_CLIENTE ";

    /** Regla 4 de sujeto de crédito: ¿tiene un crédito ACTIVO? */
    public boolean tieneCreditoActivo(int codigoCliente) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM CREDITO WHERE COD_CLIENTE = ? AND ESTADO = 'ACTIVO'")) {
            ps.setInt(1, codigoCliente);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /**
     * INSERT del crédito y de todas las filas de su tabla de amortización en
     * una sola transacción (commit / rollback).
     *
     * @return código generado del crédito
     */
    public int insertar(Credito c) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            cn.setAutoCommit(false);
            try {
                int codigo;
                try (PreparedStatement ps = cn.prepareStatement("INSERT INTO CREDITO (COD_CLIENTE, MONTO, NUM_CUOTAS, TASA_ANUAL, CUOTA, FECHA, ESTADO) "
                        + "VALUES (?, ?, ?, ?, ?, CURDATE(), 'ACTIVO')", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, c.getCodigoCliente());
                    ps.setBigDecimal(2, c.getMonto());
                    ps.setInt(3, c.getNumeroCuotas());
                    ps.setBigDecimal(4, c.getTasaAnual());
                    ps.setBigDecimal(5, c.getCuota());
                    ps.executeUpdate();
                    try (ResultSet k = ps.getGeneratedKeys()) {
                        k.next();
                        codigo = k.getInt(1);
                    }
                }
                try (PreparedStatement ps = cn.prepareStatement("INSERT INTO AMORTIZACION (COD_CREDITO, NUM_CUOTA, VALOR_CUOTA, "
                        + "INTERES_PAGADO, CAPITAL_PAGADO, SALDO) VALUES (?, ?, ?, ?, ?, ?)")) {
                    for (CuotaAmortizacion f : c.getTabla()) {
                        ps.setInt(1, codigo);
                        ps.setInt(2, f.getNumeroCuota());
                        ps.setBigDecimal(3, f.getValorCuota());
                        ps.setBigDecimal(4, f.getInteresPagado());
                        ps.setBigDecimal(5, f.getCapitalPagado());
                        ps.setBigDecimal(6, f.getSaldo());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                cn.commit();
                return codigo;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    /** SELECT del crédito con su tabla; null si no existe. */
    public Credito seleccionar(int codigoCredito) throws SQLException {
        List<Credito> l = listar(CAMPOS + "WHERE cr.COD_CREDITO = ?", codigoCredito);
        return l.isEmpty() ? null : l.get(0);
    }

    /** Créditos de un cliente (por cédula), el más reciente primero, cada uno con su tabla. */
    public List<Credito> listarPorCedula(String cedula) throws SQLException {
        return listar(CAMPOS + "WHERE cl.CEDULA = ? ORDER BY cr.COD_CREDITO DESC", cedula);
    }

    private List<Credito> listar(String sql, Object valor) throws SQLException {
        List<Credito> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setObject(1, valor);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Credito c = new Credito();
                        c.setCodigoCredito(rs.getInt(1));
                        c.setCodigoCliente(rs.getInt(2));
                        c.setCedula(rs.getString(3));
                        c.setNombre(rs.getString(4));
                        c.setMonto(rs.getBigDecimal(5));
                        c.setNumeroCuotas(rs.getInt(6));
                        c.setTasaAnual(rs.getBigDecimal(7));
                        c.setCuota(rs.getBigDecimal(8));
                        c.setFecha(rs.getDate(9).toLocalDate().toString());
                        c.setEstado(rs.getString(10));
                        lista.add(c);
                    }
                }
            }
            try (PreparedStatement ps = cn.prepareStatement("SELECT NUM_CUOTA, VALOR_CUOTA, INTERES_PAGADO, CAPITAL_PAGADO, SALDO "
                    + "FROM AMORTIZACION WHERE COD_CREDITO = ? ORDER BY NUM_CUOTA")) {
                for (Credito c : lista) {
                    ps.setInt(1, c.getCodigoCredito());
                    List<CuotaAmortizacion> tabla = new ArrayList<>();
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            CuotaAmortizacion f = new CuotaAmortizacion();
                            f.setNumeroCuota(rs.getInt(1));
                            f.setValorCuota(rs.getBigDecimal(2));
                            f.setInteresPagado(rs.getBigDecimal(3));
                            f.setCapitalPagado(rs.getBigDecimal(4));
                            f.setSaldo(rs.getBigDecimal(5));
                            tabla.add(f);
                        }
                    }
                    c.setTabla(tabla);
                    c.setTotalIntereses(tabla.stream().map(CuotaAmortizacion::getInteresPagado).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
                    c.setTotalPagar(c.getCuota().multiply(java.math.BigDecimal.valueOf(c.getNumeroCuotas())));
                }
            }
        }
        return lista;
    }
}
