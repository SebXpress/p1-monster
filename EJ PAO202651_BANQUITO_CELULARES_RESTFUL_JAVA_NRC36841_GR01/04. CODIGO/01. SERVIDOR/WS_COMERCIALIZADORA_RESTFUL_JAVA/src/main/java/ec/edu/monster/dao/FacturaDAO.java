package ec.edu.monster.dao;

import ec.edu.monster.db.ConexionBD;
import ec.edu.monster.modelo.Comprador;
import ec.edu.monster.modelo.DetalleFactura;
import ec.edu.monster.modelo.Factura;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Facturas (FACTURA + DETALLE_FACTURA) y compradores (CLIENTE).
 *
 * @author Grupo 1 - NRC 36841
 */
public class FacturaDAO {

    private static final String CAMPOS = "SELECT f.NUM_FACTURA, f.FECHA, f.CEDULA, c.NOMBRE, f.FORMA_PAGO, f.SUBTOTAL, f.DESCUENTO, f.TOTAL, "
            + "f.COD_CREDITO, f.NUM_CUOTAS, f.VALOR_CUOTA FROM FACTURA f JOIN CLIENTE c ON c.CEDULA = f.CEDULA ";

    /**
     * Guarda el comprador (lo crea o actualiza), la factura y sus líneas en una
     * sola transacción.
     *
     * @return número de factura generado
     */
    public int insertar(Comprador comprador, Factura f) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement("INSERT INTO CLIENTE (CEDULA, NOMBRE, DIRECCION, TELEFONO, CORREO) VALUES (?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE NOMBRE = VALUES(NOMBRE), DIRECCION = VALUES(DIRECCION), TELEFONO = VALUES(TELEFONO), CORREO = VALUES(CORREO)")) {
                    ps.setString(1, comprador.getCedula());
                    ps.setString(2, comprador.getNombre());
                    ps.setString(3, comprador.getDireccion());
                    ps.setString(4, comprador.getTelefono());
                    ps.setString(5, comprador.getCorreo());
                    ps.executeUpdate();
                }
                int numero;
                try (PreparedStatement ps = cn.prepareStatement("INSERT INTO FACTURA (FECHA, CEDULA, FORMA_PAGO, SUBTOTAL, DESCUENTO, TOTAL, COD_CREDITO, "
                        + "NUM_CUOTAS, VALOR_CUOTA) VALUES (NOW(), ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, comprador.getCedula());
                    ps.setString(2, f.getFormaPago());
                    ps.setBigDecimal(3, f.getSubtotal());
                    ps.setBigDecimal(4, f.getDescuento());
                    ps.setBigDecimal(5, f.getTotal());
                    if (f.getCodigoCredito() == null) {
                        ps.setNull(6, Types.INTEGER);
                        ps.setNull(7, Types.INTEGER);
                        ps.setNull(8, Types.DECIMAL);
                    } else {
                        ps.setInt(6, f.getCodigoCredito());
                        ps.setInt(7, f.getNumeroCuotas());
                        ps.setBigDecimal(8, f.getValorCuota());
                    }
                    ps.executeUpdate();
                    try (ResultSet k = ps.getGeneratedKeys()) {
                        k.next();
                        numero = k.getInt(1);
                    }
                }
                try (PreparedStatement ps = cn.prepareStatement("INSERT INTO DETALLE_FACTURA (NUM_FACTURA, LINEA, COD_CELULAR, CANTIDAD, PRECIO_UNITARIO, SUBTOTAL) "
                        + "VALUES (?, ?, ?, ?, ?, ?)")) {
                    for (DetalleFactura d : f.getDetalles()) {
                        ps.setInt(1, numero);
                        ps.setInt(2, d.getLinea());
                        ps.setInt(3, d.getCodigoCelular());
                        ps.setInt(4, d.getCantidad());
                        ps.setBigDecimal(5, d.getPrecioUnitario());
                        ps.setBigDecimal(6, d.getSubtotal());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                cn.commit();
                return numero;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    public Factura seleccionar(int numero) throws SQLException {
        List<Factura> l = listar(CAMPOS + "WHERE f.NUM_FACTURA = ?", numero);
        return l.isEmpty() ? null : l.get(0);
    }

    /** Facturas (la más reciente primero); filtro opcional por cédula. */
    public List<Factura> listar(String cedula) throws SQLException {
        String c = cedula == null ? "" : cedula.trim();
        return listar(CAMPOS + "WHERE (? = '' OR f.CEDULA = ?) ORDER BY f.NUM_FACTURA DESC", c, c);
    }

    private List<Factura> listar(String sql, Object... params) throws SQLException {
        List<Factura> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Factura f = new Factura();
                        f.setNumero(rs.getInt(1));
                        f.setFecha(rs.getTimestamp(2).toLocalDateTime().toString().replace('T', ' '));
                        f.setCedula(rs.getString(3));
                        f.setCliente(rs.getString(4));
                        f.setFormaPago(rs.getString(5));
                        f.setSubtotal(rs.getBigDecimal(6));
                        f.setDescuento(rs.getBigDecimal(7));
                        f.setTotal(rs.getBigDecimal(8));
                        f.setCodigoCredito((Integer) rs.getObject(9));
                        f.setNumeroCuotas((Integer) rs.getObject(10));
                        f.setValorCuota(rs.getBigDecimal(11));
                        lista.add(f);
                    }
                }
            }
            try (PreparedStatement ps = cn.prepareStatement("SELECT d.LINEA, d.COD_CELULAR, CONCAT(c.MARCA, ' ', c.MODELO), d.CANTIDAD, d.PRECIO_UNITARIO, d.SUBTOTAL "
                    + "FROM DETALLE_FACTURA d JOIN CELULAR c ON c.COD_CELULAR = d.COD_CELULAR WHERE d.NUM_FACTURA = ? ORDER BY d.LINEA")) {
                for (Factura f : lista) {
                    ps.setInt(1, f.getNumero());
                    List<DetalleFactura> det = new ArrayList<>();
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            DetalleFactura d = new DetalleFactura();
                            d.setLinea(rs.getInt(1));
                            d.setCodigoCelular(rs.getInt(2));
                            d.setCelular(rs.getString(3));
                            d.setCantidad(rs.getInt(4));
                            d.setPrecioUnitario(rs.getBigDecimal(5));
                            d.setSubtotal(rs.getBigDecimal(6));
                            det.add(d);
                        }
                    }
                    f.setDetalles(det);
                }
            }
        }
        return lista;
    }
}
