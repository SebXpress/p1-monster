package ec.edu.monster.dao;

import ec.edu.monster.db.ConexionBD;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Consultas sobre MOVIMIENTO de todas las cuentas de un cliente.
 * TIPO: 'DEP' = depósito, 'RET' = retiro.
 *
 * @author Grupo 1 - NRC 36841
 */
public class MovimientoDAO {

    /** Promedio y cantidad de movimientos de un tipo en los últimos N meses. */
    public record Resumen(BigDecimal promedio, int cantidad) {
    }

    /** Regla 2 de sujeto de crédito: ¿tiene al menos un depósito en el último mes? */
    public boolean tieneDepositoUltimoMes(int codigoCliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM MOVIMIENTO m JOIN CUENTA c ON c.NUM_CUENTA = m.NUM_CUENTA "
                + "WHERE c.COD_CLIENTE = ? AND m.TIPO = 'DEP' AND m.FECHA >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";
        try (Connection cn = ConexionBD.getInstancia().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, codigoCliente);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /** Promedio de todos los movimientos del tipo indicado en los últimos 'meses' meses. */
    public Resumen resumen(int codigoCliente, String tipo, int meses) throws SQLException {
        String sql = "SELECT COALESCE(AVG(m.VALOR), 0), COUNT(*) FROM MOVIMIENTO m JOIN CUENTA c ON c.NUM_CUENTA = m.NUM_CUENTA "
                + "WHERE c.COD_CLIENTE = ? AND m.TIPO = ? AND m.FECHA >= DATE_SUB(CURDATE(), INTERVAL ? MONTH)";
        try (Connection cn = ConexionBD.getInstancia().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, codigoCliente);
            ps.setString(2, tipo);
            ps.setInt(3, meses);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new Resumen(rs.getBigDecimal(1), rs.getInt(2));
            }
        }
    }
}
