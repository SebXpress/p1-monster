package ec.edu.monster;

import ec.edu.monster.controlador.ControladorEscritorio;
import ec.edu.monster.modelo.ServicioRest;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada del cliente de escritorio.
 * Uso: java -jar CLIESC_COMERCIALIZADORA_RESTFUL_JAVA.jar [url_servidor]
 *
 * @author Grupo 1 - NRC 36841
 */
public class ClienteEscritorio {

    public static void main(String[] args) {
        String url = args.length > 0 ? args[0] : ServicioRest.URL_POR_DEFECTO;
        SwingUtilities.invokeLater(() -> {
            aplicarApariencia();
            new ControladorEscritorio(new ServicioRest(url, "ESCRITORIO")).iniciar();
        });
    }

    /** Usa FlatLaf (tema oscuro) si está disponible; si no, la apariencia del sistema. */
    private static void aplicarApariencia() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            UIManager.put("Button.arc", 14);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Table.showHorizontalLines", true);
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorada) {
                // se mantiene la apariencia por defecto
            }
        }
    }
}
