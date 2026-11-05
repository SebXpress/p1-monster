package ec.edu.monster.servicio;

import ec.edu.monster.dao.CelularDAO;
import ec.edu.monster.modelo.Celular;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

/**
 * Mantenimiento del catálogo de teléfonos celulares a la venta. Patrón Singleton.
 *
 * @author Grupo 1 - NRC 36841
 */
public final class ServicioCelular {

    private static final ServicioCelular INSTANCIA = new ServicioCelular();
    private final CelularDAO dao = new CelularDAO();

    private ServicioCelular() {
    }

    public static ServicioCelular getInstancia() {
        return INSTANCIA;
    }

    public List<Celular> listar(String filtro, boolean soloActivos) {
        try {
            return dao.listar(filtro, soloActivos);
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    public Celular consultar(int codigo) {
        try {
            Celular c = dao.seleccionar(codigo);
            if (c == null) {
                throw ExcepcionNegocio.noEncontrado("No existe el celular " + codigo);
            }
            return c;
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    public Celular crear(Celular c) {
        validar(c);
        try {
            return dao.seleccionar(dao.insertar(c));
        } catch (SQLException e) {
            throw duplicado(e, c);
        }
    }

    public Celular actualizar(int codigo, Celular c) {
        consultar(codigo);
        validar(c);
        c.setCodigo(codigo);
        try {
            dao.actualizar(c);
            return dao.seleccionar(codigo);
        } catch (SQLException e) {
            throw duplicado(e, c);
        }
    }

    public Celular eliminar(int codigo) {
        Celular c = consultar(codigo);
        try {
            if (dao.estaFacturado(codigo)) {
                throw ExcepcionNegocio.conflicto("El " + c.getMarca() + " " + c.getModelo()
                        + " ya fue facturado: no se puede eliminar, cámbielo a INACTIVO");
            }
            dao.eliminar(codigo);
            return c;
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    private void validar(Celular c) {
        if (c == null) {
            throw ExcepcionNegocio.validacion("Envíe los datos del celular");
        }
        c.setMarca(texto(c.getMarca()));
        c.setModelo(texto(c.getModelo()));
        c.setDescripcion(c.getDescripcion() == null ? null : c.getDescripcion().trim());
        if (c.getMarca().isEmpty() || c.getMarca().length() > 40) {
            throw ExcepcionNegocio.validacion("La marca es obligatoria (máximo 40 caracteres)");
        }
        if (c.getModelo().isEmpty() || c.getModelo().length() > 60) {
            throw ExcepcionNegocio.validacion("El modelo es obligatorio (máximo 60 caracteres)");
        }
        if (c.getPrecio() == null || c.getPrecio().compareTo(BigDecimal.ZERO) <= 0 || c.getPrecio().compareTo(new BigDecimal("99999")) > 0) {
            throw ExcepcionNegocio.validacion("El precio debe ser mayor que cero");
        }
        c.setPrecio(c.getPrecio().setScale(2, RoundingMode.HALF_UP));
        String est = c.getEstado() == null || c.getEstado().isBlank() ? "ACTIVO" : c.getEstado().trim().toUpperCase();
        if (!est.equals("ACTIVO") && !est.equals("INACTIVO")) {
            throw ExcepcionNegocio.validacion("El estado debe ser ACTIVO o INACTIVO");
        }
        c.setEstado(est);
    }

    private static String texto(String s) {
        return s == null ? "" : s.trim();
    }

    private static ExcepcionNegocio duplicado(SQLException e, Celular c) {
        if ("23000".equals(e.getSQLState())) {
            return ExcepcionNegocio.conflicto("Ya existe el celular " + c.getMarca() + " " + c.getModelo());
        }
        return ExcepcionNegocio.deBaseDatos(e);
    }
}
