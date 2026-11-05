package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ExcepcionCliente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Historial de facturas, con filtro opcional por cédula.
 *
 * @author Grupo 1 - NRC 36841
 */
@WebServlet(name = "FacturasServlet", urlPatterns = {"/facturas"})
public class FacturasServlet extends ServletBase {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String cedula = param(req, "cedula");
        req.setAttribute("cedula", cedula);
        try {
            req.setAttribute("facturas", servicio(req).listarFacturas(cedula));
        } catch (ExcepcionCliente e) {
            req.setAttribute("error", e.getMessage());
        }
        vista(req, res, "facturas.jsp", "facturas");
    }
}
