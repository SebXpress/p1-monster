package ec.edu.monster.climov.modelo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Datos compartidos por todas las pantallas (Singleton): el ServicioRest y la
 * URL del servidor, que se recuerda entre ejecuciones.
 *
 * @author Grupo 1 - NRC 36841
 */
public final class SesionApp {

    private static final SesionApp INSTANCIA = new SesionApp();
    private static final String PREFS = "comercializadora";
    private final ServicioRest servicio = new ServicioRest(ServicioRest.URL_POR_DEFECTO, "MOVIL");
    private boolean cargada;
    /** Factura recién emitida (para mostrarla sin volver a pedirla). */
    private Factura ultimaFactura;

    private SesionApp() {
    }

    public static SesionApp get() {
        return INSTANCIA;
    }

    /** Carga la URL guardada la primera vez. */
    public ServicioRest getServicio(Context c) {
        if (!cargada) {
            SharedPreferences p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            servicio.setUrlBase(p.getString("url", ServicioRest.URL_POR_DEFECTO));
            cargada = true;
        }
        return servicio;
    }

    public void guardarUrl(Context c, String url) {
        servicio.setUrlBase(url);
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("url", servicio.getUrlBase()).apply();
        cargada = true;
    }

    public Factura getUltimaFactura() {
        return ultimaFactura;
    }

    public void setUltimaFactura(Factura f) {
        this.ultimaFactura = f;
    }
}
