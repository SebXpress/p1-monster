package ec.edu.monster;

import ec.edu.monster.controlador.ControladorConsola;
import ec.edu.monster.modelo.ServicioRest;
import ec.edu.monster.vista.VistaConsola;
import java.util.Arrays;

/**
 * Punto de entrada del cliente de consola de la Comercializadora de Celulares.
 * Uso: java -jar CLICON_COMERCIALIZADORA_RESTFUL_JAVA.jar [url_servidor] [--sin-color]
 *
 * @author Grupo 1 - NRC 36841
 */
public class ClienteConsola {

    public static void main(String[] args) {
        boolean color = Arrays.stream(args).noneMatch(a -> a.equalsIgnoreCase("--sin-color"));
        String url = Arrays.stream(args).filter(a -> !a.startsWith("--")).findFirst().orElse(ServicioRest.URL_POR_DEFECTO);
        new ControladorConsola(new VistaConsola(color), new ServicioRest(url, "CONSOLA")).iniciar();
    }
}
