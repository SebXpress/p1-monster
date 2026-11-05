package ec.edu.monster.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Lee una sola vez db.properties (base de datos y URL del Core BanQuito).
 *
 * @author Grupo 1 - NRC 36841
 */
public final class Configuracion {

    private static final Properties P = new Properties();

    static {
        try (InputStream in = Configuracion.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                P.load(in);
            }
        } catch (IOException e) {
            System.err.println("[Configuracion] No se pudo leer db.properties");
        }
    }

    private Configuracion() {
    }

    public static String valor(String clave, String porDefecto) {
        return System.getProperty(clave, P.getProperty(clave, porDefecto));
    }

    /** URL base de los servicios web del Core BanQuito. */
    public static String urlBanQuito() {
        String u = valor("banquito.url", "http://localhost:8080/WS_BANQUITO_CORE_RESTFUL_JAVA/api");
        return u.endsWith("/") ? u.substring(0, u.length() - 1) : u;
    }
}
