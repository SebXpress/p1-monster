package ec.edu.monster.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Activa JAX-RS: todos los servicios quedan bajo /api.
 *
 * @author Grupo 1 - NRC 36841
 */
@ApplicationPath("api")
public class ConfiguracionApp extends Application {
}
