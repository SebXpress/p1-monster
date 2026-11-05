package ec.edu.monster.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Conexión a la base de datos BanQuito CORE (MariaDB) - patrón Singleton.
 *
 * <p>Existe una sola instancia de esta clase en toda la aplicación
 * ({@link #getInstancia()}). La instancia lee una sola vez la configuración
 * desde {@code db.properties} y registra el driver de MariaDB.</p>
 *
 * <p>{@link #getConexion()} entrega una conexión JDBC lista para usar. Cada
 * operación del DAO abre su conexión con try-with-resources y la cierra al
 * terminar: así varias peticiones REST simultáneas no comparten la misma
 * transacción (otorgar un crédito usa commit/rollback).</p>
 *
 * Configuración por defecto: jdbc:mariadb://localhost:3307/banquito_core (db.properties).
 *
 * @author Grupo 1 - NRC 36841
 */
public final class ConexionBD {

    private static volatile ConexionBD instancia;

    private final String url;
    private final String usuario;
    private final String clave;

    /** Constructor privado: nadie fuera de la clase puede crear otra instancia. */
    private ConexionBD() {
        Properties p = new Properties();
        try (InputStream in = ConexionBD.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (IOException e) {
            System.err.println("[ConexionBD] No se pudo leer db.properties, se usan valores por defecto.");
        }
        this.url = p.getProperty("db.url", "jdbc:mariadb://localhost:3307/banquito_core");
        this.usuario = p.getProperty("db.usuario", "root");
        this.clave = p.getProperty("db.clave", "rootroot");
        try {
            Class.forName(p.getProperty("db.driver", "org.mariadb.jdbc.Driver"));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No se encontró el driver de MariaDB", e);
        }
    }

    /** Devuelve la única instancia (double-checked locking, seguro entre hilos). */
    public static ConexionBD getInstancia() {
        if (instancia == null) {
            synchronized (ConexionBD.class) {
                if (instancia == null) {
                    instancia = new ConexionBD();
                }
            }
        }
        return instancia;
    }

    /** Abre una conexión a banquito_core. Quien la pide debe cerrarla (try-with-resources). */
    public Connection getConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, clave);
    }

    /** Cierra la conexión de forma segura (se usa en los finally de las pruebas). */
    public static void cerrar(Connection cn) {
        if (cn != null) {
            try {
                if (!cn.isClosed()) {
                    cn.close();
                }
            } catch (SQLException e) {
                System.err.println("[ConexionBD] Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    public String getUrl() {
        return url;
    }
}
