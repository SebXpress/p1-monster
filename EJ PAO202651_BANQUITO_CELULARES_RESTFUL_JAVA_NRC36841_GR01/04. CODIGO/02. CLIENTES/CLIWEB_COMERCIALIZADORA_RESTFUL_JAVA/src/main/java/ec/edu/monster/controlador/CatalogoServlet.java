package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Celular;
import ec.edu.monster.modelo.ExcepcionCliente;
import ec.edu.monster.modelo.ServicioRest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Mantenimiento del catálogo de teléfonos celulares (listar, buscar, crear,
 * editar y eliminar).
 *
 * @author Grupo 1 - NRC 36841
 */
@WebServlet(name = "CatalogoServlet", urlPatterns = {"/catalogo"})
public class CatalogoServlet extends ServletBase {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ServicioRest s = servicio(req);
        String buscar = param(req, "buscar");
        boolean activos = "on".equals(param(req, "activos"));
        req.setAttribute("buscar", buscar);
        req.setAttribute("activos", activos);
        try {
            req.setAttribute("celulares", s.listarCelulares(buscar, activos));
            int editar = entero(req, "editar", 0);
            if (editar > 0 && req.getAttribute("celular") == null) {
                req.setAttribute("celular", s.consultarCelular(editar));
            }
        } catch (ExcepcionCliente e) {
            req.setAttribute("error", e.getMessage());
        }
        vista(req, res, "catalogo.jsp", "catalogo");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ServicioRest s = servicio(req);
        String accion = param(req, "accion");
        Celular c = new Celular();
        int codigo = entero(req, "codigo", 0);
        c.setCodigo(codigo > 0 ? codigo : null);
        try {
            if (accion.equals("eliminar")) {
                aviso(req, "ok", s.eliminarCelular(codigo));
                res.sendRedirect("catalogo");
                return;
            }
            c.setMarca(param(req, "marca"));
            c.setModelo(param(req, "modelo"));
            c.setDescripcion(param(req, "descripcion"));
            c.setEstado(param(req, "estado"));
            try {
                c.setPrecio(Double.parseDouble(param(req, "precio").replace(',', '.')));
            } catch (NumberFormatException e) {
                throw new ExcepcionCliente("Ingrese un precio válido (ej.: 449.00)", 400);
            }
            Celular r = c.getCodigo() == null ? s.crearCelular(c) : s.actualizarCelular(c);
            aviso(req, "ok", s.getUltimoMensaje() + ": " + r.nombre());
            res.sendRedirect("catalogo");
        } catch (ExcepcionCliente e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("celular", c);
            doGet(req, res);
        }
    }
}
