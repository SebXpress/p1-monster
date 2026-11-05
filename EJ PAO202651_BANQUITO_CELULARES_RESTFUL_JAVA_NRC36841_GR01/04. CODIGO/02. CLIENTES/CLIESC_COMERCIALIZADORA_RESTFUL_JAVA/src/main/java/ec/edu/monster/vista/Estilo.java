package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Paleta, tipografías e imagen del Monster compartidas por todas las ventanas.
 *
 * @author Grupo 1 - NRC 36841
 */
public final class Estilo {

    public static final Color FONDO = new Color(0x0B1426);
    public static final Color PANEL = new Color(0x13213B);
    public static final Color PANEL2 = new Color(0x1A2B4B);
    public static final Color BORDE = new Color(0x24395F);
    public static final Color TEXTO = new Color(0xE8EEFB);
    public static final Color TENUE = new Color(0x8FA3C7);
    public static final Color AZUL = new Color(0x3D8BFD);
    public static final Color CIAN = new Color(0x2DD4BF);
    public static final Color VERDE = new Color(0x22C55E);
    public static final Color ROJO = new Color(0xF43F5E);
    public static final Color AMARILLO = new Color(0xFBBF24);

    public static final Font TITULO = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SUBTITULO = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font PEQUENA = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font RESULTADO = new Font("Segoe UI", Font.BOLD, 34);

    private Estilo() {
    }

    public static final Color DORADO = new Color(0xF5B841);
    public static final Font DINERO = new Font("Segoe UI", Font.BOLD, 30);

    /** 1234.5 -> "1,234.50" */
    public static String dinero(double v) {
        return String.format("%,.2f", v);
    }

    /** Imagen del Monster (src/main/resources/img/monster.png) o una "M" si no existe. */
    public static ImageIcon monster(int lado) {
        URL url = Estilo.class.getResource("/img/monster.png");
        if (url != null) {
            Image img = new ImageIcon(url).getImage().getScaledInstance(lado, lado, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        BufferedImage b = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(PANEL2);
        g.fillRoundRect(0, 0, lado, lado, lado / 3, lado / 3);
        g.setColor(AZUL);
        g.setFont(new Font("Segoe UI", Font.BOLD, lado / 2));
        int w = g.getFontMetrics().stringWidth("M");
        g.drawString("M", (lado - w) / 2, lado * 2 / 3);
        g.dispose();
        return new ImageIcon(b);
    }
}
