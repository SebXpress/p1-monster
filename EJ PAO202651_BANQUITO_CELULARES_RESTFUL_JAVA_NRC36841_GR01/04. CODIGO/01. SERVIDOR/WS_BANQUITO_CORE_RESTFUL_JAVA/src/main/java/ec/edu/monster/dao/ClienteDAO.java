package ec.edu.monster.dao;

import ec.edu.monster.db.ConexionBD;
import ec.edu.monster.modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Acceso a la tabla CLIENTE del CORE (solo lectura: el módulo de crédito
 * no administra clientes).
 *
 * @author Grupo 1 - NRC 36841
 */
public class ClienteDAO {

    private static final String CAMPOS = "SELECT COD_CLIENTE, CEDULA, NOMBRE, GENERO, FECHA_NACIMIENTO FROM CLIENTE ";

    /** SELECT por cédula; null si la persona no es cliente del banco. */
    public Cliente seleccionarPorCedula(String cedula) throws SQLException {
        return seleccionar(CAMPOS + "WHERE CEDULA = ?", cedula);
    }

    /** SELECT por código de cliente; null si no existe. */
    public Cliente seleccionarPorCodigo(int codigo) throws SQLException {
        return seleccionar(CAMPOS + "WHERE COD_CLIENTE = ?", codigo);
    }

    private Cliente seleccionar(String sql, Object valor) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Cliente c = new Cliente();
                c.setCodigoCliente(rs.getInt(1));
                c.setCedula(rs.getString(2));
                c.setNombre(rs.getString(3));
                c.setGenero(rs.getString(4));
                c.setFechaNacimiento(rs.getDate(5).toLocalDate());
                return c;
            }
        }
    }
}
