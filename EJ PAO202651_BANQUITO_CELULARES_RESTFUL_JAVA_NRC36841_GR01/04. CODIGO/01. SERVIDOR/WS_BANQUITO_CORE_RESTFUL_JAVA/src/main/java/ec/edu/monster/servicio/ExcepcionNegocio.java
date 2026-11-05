package ec.edu.monster.servicio;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTransientConnectionException;

/**
 * Error de negocio con el código HTTP que debe devolverse al cliente.
 *
 * @author Grupo 1 - NRC 36841
 */
public class ExcepcionNegocio extends RuntimeException {

    private final int estado;

    public ExcepcionNegocio(int estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    /** 400 - datos inválidos. */
    public static ExcepcionNegocio validacion(String mensaje) {
        return new ExcepcionNegocio(400, mensaje);
    }

    /** 404 - recurso inexistente. */
    public static ExcepcionNegocio noEncontrado(String mensaje) {
        return new ExcepcionNegocio(404, mensaje);
    }

    /** 409 - conflicto con el estado actual (monto excedido, crédito activo...). */
    public static ExcepcionNegocio conflicto(String mensaje) {
        return new ExcepcionNegocio(409, mensaje);
    }

    /** Traduce un error de JDBC a un mensaje entendible. */
    public static ExcepcionNegocio deBaseDatos(SQLException e) {
        String estadoSql = e.getSQLState() == null ? "" : e.getSQLState();
        if (e instanceof SQLNonTransientConnectionException || e instanceof SQLTransientConnectionException
                || estadoSql.startsWith("08")) {
            return new ExcepcionNegocio(503, "Sin conexión con la base de datos BanQuito. Verifique que MariaDB esté encendido.");
        }
        if (e instanceof SQLIntegrityConstraintViolationException || estadoSql.startsWith("23")) {
            return new ExcepcionNegocio(409, "La operación viola una restricción de la base de datos: " + e.getMessage());
        }
        return new ExcepcionNegocio(500, "Error de base de datos: " + e.getMessage());
    }

    public int getEstado() {
        return estado;
    }
}
