package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ServicioRest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Base de los controladores web (MVC): cada navegador tiene su propio
 * ServicioRest guardado en la sesión HTTP; las vistas son JSP en WEB-INF.
 *
 * @author Grupo 1 - NRC 36841
 */
public abstract class ServletBase extends HttpServlet {

    static final String SERVICIO = "servicioRest";

    /** ServicioRest de este navegador (se crea en la primera visita). */
    protected static ServicioRest servicio(HttpServletRequest req) throws IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession s = req.getSession();
        ServicioRest r = (ServicioRest) s.getAttribute(SERVICIO);
        if (r == null) {
            r = new ServicioRest(req.getServletContext().getInitParameter("urlServidor"), "WEB");
            s.setAttribute(SERVICIO, r);
        }
        return r;
    }

    /** Mensaje para mostrar después de una redirección (patrón POST-redirect-GET). */
    protected static void aviso(HttpServletRequest req, String tipo, String texto) {
        req.getSession().setAttribute("avisoTipo", tipo);
        req.getSession().setAttribute("avisoTexto", texto);
    }

    /** Muestra la vista JSP con los datos comunes de la cabecera. */
    protected static void vista(HttpServletRequest req, HttpServletResponse res, String jsp, String menu)
            throws ServletException, IOException {
        req.setAttribute("menu", menu);
        req.setAttribute("servidor", servicio(req).getUrlBase());
        HttpSession ses = req.getSession();
        Object texto = ses.getAttribute("avisoTexto");
        if (texto != null) {
            req.setAttribute(ses.getAttribute("avisoTipo").equals("ok") ? "ok" : "error", texto);
            ses.removeAttribute("avisoTexto");
            ses.removeAttribute("avisoTipo");
        }
        req.getRequestDispatcher("/WEB-INF/vistas/" + jsp).forward(req, res);
    }

    protected static String param(HttpServletRequest req, String nombre) {
        String v = req.getParameter(nombre);
        return v == null ? "" : v.trim();
    }

    protected static int entero(HttpServletRequest req, String nombre, int porDefecto) {
        try {
            return Integer.parseInt(param(req, nombre));
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}
