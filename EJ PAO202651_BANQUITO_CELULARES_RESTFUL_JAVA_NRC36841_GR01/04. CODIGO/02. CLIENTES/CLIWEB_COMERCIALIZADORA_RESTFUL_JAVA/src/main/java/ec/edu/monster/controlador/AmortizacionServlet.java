package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Credito;
import ec.edu.monster.modelo.ExcepcionCliente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Consulta de la tabla de amortización: con la cédula se obtienen del Banco
 * BanQuito (vía la comercializadora) los créditos del cliente y se muestra la
 * tabla del crédito elegido (por defecto el más reciente).
 *
 * @author Grupo 1 - NRC 36841
 */
@WebServlet(name = "AmortizacionServlet", urlPatterns = {"/amortizacion"})
public class AmortizacionServlet extends ServletBase {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String cedula = param(req, "cedula");
        req.setAttribute("cedula", cedula);
        if (!cedula.isEmpty()) {
            try {
                List<Credito> creditos = servicio(req).creditosCliente(cedula);
                req.setAttribute("creditos", creditos);
                int elegido = entero(req, "credito", 0);
                Credito sel = creditos.stream().filter(c -> c.getCodigoCredito() == elegido).findFirst()
                        .orElse(creditos.isEmpty() ? null : creditos.get(0));
                req.setAttribute("credito", sel);
                if (creditos.isEmpty()) {
                    req.setAttribute("error", "El cliente no tiene créditos en el Banco BanQuito");
                }
            } catch (ExcepcionCliente e) {
                req.setAttribute("error", e.getMessage());
            }
        }
        vista(req, res, "amortizacion.jsp", "amortizacion");
    }
}
