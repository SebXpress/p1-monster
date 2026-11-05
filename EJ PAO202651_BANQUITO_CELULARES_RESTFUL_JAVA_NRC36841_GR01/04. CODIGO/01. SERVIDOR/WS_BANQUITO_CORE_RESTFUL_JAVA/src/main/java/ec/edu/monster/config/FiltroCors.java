package ec.edu.monster.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

/**
 * Habilita CORS para que el cliente web (u otros orígenes) consuma la API.
 *
 * @author Grupo 1 - NRC 36841
 */
@Provider
@PreMatching
public class FiltroCors implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext req) {
        // Responde de inmediato a las peticiones de verificación (preflight)
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            req.abortWith(Response.ok().build());
        }
    }

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) {
        res.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
        res.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        res.getHeaders().putSingle("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Cliente");
        res.getHeaders().putSingle("Access-Control-Max-Age", "3600");
    }
}
