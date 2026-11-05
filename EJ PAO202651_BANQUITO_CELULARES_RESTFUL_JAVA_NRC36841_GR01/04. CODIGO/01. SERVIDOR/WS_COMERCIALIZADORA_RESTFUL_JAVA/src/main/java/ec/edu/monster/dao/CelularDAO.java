package ec.edu.monster.dao;

import ec.edu.monster.db.ConexionBD;
import ec.edu.monster.modelo.Celular;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD del catálogo de teléfonos celulares (tabla CELULAR):
 * insertar, seleccionar, listar, actualizar y eliminar.
 *
 * @author Grupo 1 - NRC 36841
 */
public class CelularDAO {

    private static final String CAMPOS = "SELECT COD_CELULAR, MARCA, MODELO, DESCRIPCION, PRECIO, ESTADO FROM CELULAR ";

    public int insertar(Celular c) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO CELULAR (MARCA, MODELO, DESCRIPCION, PRECIO, ESTADO) VALUES (?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, c);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                k.next();
                return k.getInt(1);
            }
        }
    }

    public Celular seleccionar(int codigo) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion(); PreparedStatement ps = cn.prepareStatement(CAMPOS + "WHERE COD_CELULAR = ?")) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** Lista el catálogo; filtro opcional por marca/modelo y solo los ACTIVO si se pide. */
    public List<Celular> listar(String filtro, boolean soloActivos) throws SQLException {
        String sql = CAMPOS + "WHERE (? = '' OR CONCAT(MARCA, ' ', MODELO) LIKE ?)" + (soloActivos ? " AND ESTADO = 'ACTIVO'" : "") + " ORDER BY PRECIO";
        String f = filtro == null ? "" : filtro.trim();
        List<Celular> l = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, f);
            ps.setString(2, "%" + f + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    l.add(mapear(rs));
                }
            }
        }
        return l;
    }

    public boolean actualizar(Celular c) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement("UPDATE CELULAR SET MARCA = ?, MODELO = ?, DESCRIPCION = ?, PRECIO = ?, ESTADO = ? WHERE COD_CELULAR = ?")) {
            asignar(ps, c);
            ps.setInt(6, c.getCodigo());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean eliminar(int codigo) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion(); PreparedStatement ps = cn.prepareStatement("DELETE FROM CELULAR WHERE COD_CELULAR = ?")) {
            ps.setInt(1, codigo);
            return ps.executeUpdate() == 1;
        }
    }

    /** ¿Aparece en alguna factura? (entonces no se puede eliminar, solo inactivar). */
    public boolean estaFacturado(int codigo) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM DETALLE_FACTURA WHERE COD_CELULAR = ?")) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private void asignar(PreparedStatement ps, Celular c) throws SQLException {
        ps.setString(1, c.getMarca());
        ps.setString(2, c.getModelo());
        ps.setString(3, c.getDescripcion());
        ps.setBigDecimal(4, c.getPrecio());
        ps.setString(5, c.getEstado());
    }

    private Celular mapear(ResultSet rs) throws SQLException {
        Celular c = new Celular();
        c.setCodigo(rs.getInt(1));
        c.setMarca(rs.getString(2));
        c.setModelo(rs.getString(3));
        c.setDescripcion(rs.getString(4));
        c.setPrecio(rs.getBigDecimal(5));
        c.setEstado(rs.getString(6));
        return c;
    }
}
