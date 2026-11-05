package ec.edu.monster.climov.controlador;

import android.content.Context;
import ec.edu.monster.climov.modelo.Celular;
import ec.edu.monster.climov.modelo.Credito;
import ec.edu.monster.climov.modelo.Evaluacion;
import ec.edu.monster.climov.modelo.Factura;
import ec.edu.monster.climov.modelo.ServicioRest;
import ec.edu.monster.climov.modelo.SesionApp;
import java.util.List;
import java.util.Map;

/**
 * Controlador (MVC) de la app: las pantallas le piden las operaciones y él
 * las ejecuta contra el servicio REST en segundo plano (ver Tareas).
 *
 * @author Grupo 1 - NRC 36841
 */
public class ControladorComercializadora {

    private final ServicioRest servicio;

    public ControladorComercializadora(Context c) {
        this.servicio = SesionApp.get().getServicio(c);
    }

    public String getUrl() {
        return servicio.getUrlBase();
    }

    public String getUltimoMensaje() {
        return servicio.getUltimoMensaje();
    }

    // ---------------------------------------------------------------- catálogo
    public void listarCelulares(String buscar, boolean activos, Tareas.Exito<List<Celular>> ok, Tareas.Fallo error) {
        Tareas.ejecutar(() -> servicio.listarCelulares(buscar, activos), ok, error);
    }

    public void guardarCelular(Celular c, Tareas.Exito<Celular> ok, Tareas.Fallo error) {
        Tareas.ejecutar(() -> c.getCodigo() == null ? servicio.crearCelular(c) : servicio.actualizarCelular(c), ok, error);
    }

    public void eliminarCelular(int codigo, Tareas.Exito<String> ok, Tareas.Fallo error) {
        Tareas.ejecutar(() -> servicio.eliminarCelular(codigo), ok, error);
    }

    // ---------------------------------------------------------------- facturación
    public void evaluarCredito(String cedula, double monto, Tareas.Exito<Evaluacion> ok, Tareas.Fallo error) {
        Tareas.ejecutar(() -> servicio.evaluarCredito(cedula, monto), ok, error);
    }

    public void facturar(String cedula, String nombre, String direccion, String telefono, String correo, boolean credito,
                         int cuotas, Map<Integer, Integer> items, Tareas.Exito<Factura> ok, Tareas.Fallo error) {
        Tareas.ejecutar(() -> servicio.facturar(cedula, nombre, direccion, telefono, correo, credito ? "CREDITO" : "EFECTIVO",
                credito ? cuotas : null, items), ok, error);
    }

    public void listarFacturas(String cedula, Tareas.Exito<List<Factura>> ok, Tareas.Fallo error) {
        Tareas.ejecutar(() -> servicio.listarFacturas(cedula), ok, error);
    }

    public void consultarFactura(int numero, Tareas.Exito<Factura> ok, Tareas.Fallo error) {
        Tareas.ejecutar(() -> servicio.consultarFactura(numero), ok, error);
    }

    // ---------------------------------------------------------------- amortización
    public void creditosCliente(String cedula, Tareas.Exito<List<Credito>> ok, Tareas.Fallo error) {
        Tareas.ejecutar(() -> servicio.creditosCliente(cedula), ok, error);
    }
}
