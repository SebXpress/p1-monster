package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ExcepcionCliente;
import ec.edu.monster.modelo.Factura;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Muestra una factura y, si fue a crédito directo, su tabla de amortización.
 *
 * @author Grupo 1 - NRC 36841
 */
@WebServlet(name = "FacturaServlet", urlPatterns = {"/factura"})
public class FacturaServlet extends ServletBase {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        int numero = entero(req, "numero", 0);
        Object nueva = req.getSession().getAttribute("facturaNueva");
        try {
            Factura f = nueva instanceof Factura fn && fn.getNumero() == numero ? fn : servicio(req).consultarFactura(numero);
            req.getSession().removeAttribute("facturaNueva");
            req.setAttribute("factura", f);
            req.setAttribute("credito", f.getCredito());
        } catch (ExcepcionCliente e) {
            req.setAttribute("error", e.getMessage());
        }
        vista(req, res, "factura.jsp", "facturas");
    }
}
