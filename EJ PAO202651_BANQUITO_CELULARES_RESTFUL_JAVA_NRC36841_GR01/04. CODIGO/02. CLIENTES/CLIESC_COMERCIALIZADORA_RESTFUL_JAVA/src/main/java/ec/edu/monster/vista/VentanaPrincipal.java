package ec.edu.monster.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * Ventana principal de la Comercializadora de Teléfonos Celulares: cabecera
 * con el Monster y la URL del servidor, menú lateral y las cuatro vistas
 * (Catálogo, Facturar, Tabla de amortización y Facturas).
 *
 * @author Grupo 1 - NRC 36841
 */
public class VentanaPrincipal extends JFrame {

    public static final String CATALOGO = "Catálogo";
    public static final String FACTURAR = "Facturar";
    public static final String AMORTIZACION = "Amortización";
    public static final String FACTURAS = "Facturas";

    private final JTextField txtUrl = Componentes.campo("URL del servidor");
    private final JButton btnConectar = Componentes.boton("Conectar", Estilo.CIAN);
    private final JLabel lblEstado = Componentes.etiqueta(" ", Estilo.PEQUENA, Estilo.TENUE);
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final Map<String, JButton> menu = new LinkedHashMap<>();

    private final PanelCatalogo panelCatalogo = new PanelCatalogo();
    private final PanelFacturacion panelFacturacion = new PanelFacturacion();
    private final PanelAmortizacion panelAmortizacion = new PanelAmortizacion();
    private final PanelFacturas panelFacturas = new PanelFacturas();
    private Consumer<String> alCambiarVista = v -> { };

    public VentanaPrincipal() {
        super("Comercializadora Monster - Teléfonos Celulares (Cliente Escritorio)");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1280, 780);
        setMinimumSize(Componentes.dim(1120, 700));
        setLocationRelativeTo(null);
        setIconImage(Estilo.monster(64).getImage());

        // Cabecera
        JPanel cabecera = new Componentes.Degradado(new BorderLayout(), new Color(0x1D3561), Estilo.FONDO);
        cabecera.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JPanel marca = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        marca.setOpaque(false);
        marca.add(new JLabel(Estilo.monster(52)));
        JPanel titulos = new JPanel(new GridLayout(2, 1));
        titulos.setOpaque(false);
        titulos.add(Componentes.etiqueta("Comercializadora Monster", Estilo.TITULO, Estilo.DORADO));
        titulos.add(Componentes.etiqueta("Venta de celulares · Crédito directo Banco BanQuito · Grupo 1 · NRC 36841",
                Estilo.PEQUENA, Estilo.CIAN));
        marca.add(titulos);
        JPanel servidor = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        servidor.setOpaque(false);
        servidor.add(Componentes.etiqueta("Servidor:", Estilo.PEQUENA, Estilo.TENUE));
        txtUrl.setColumns(34);
        txtUrl.setFont(Estilo.PEQUENA);
        servidor.add(txtUrl);
        btnConectar.setFont(Estilo.PEQUENA.deriveFont(java.awt.Font.BOLD));
        servidor.add(btnConectar);
        cabecera.add(marca, BorderLayout.WEST);
        cabecera.add(servidor, BorderLayout.EAST);

        // Menú lateral
        JPanel lateral = new JPanel();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBackground(Estilo.PANEL);
        lateral.setBorder(BorderFactory.createEmptyBorder(18, 12, 18, 12));
        String[][] opciones = {
            {CATALOGO, "Mantenimiento de celulares"},
            {FACTURAR, "Efectivo (-42 %) o crédito directo"},
            {AMORTIZACION, "Tabla de amortización por cédula"},
            {FACTURAS, "Historial de ventas"}};
        for (String[] o : opciones) {
            JButton b = new JButton("<html><b>" + o[0] + "</b><br><span style='font-size:9px'>" + o[1] + "</span></html>");
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setFont(Estilo.NORMAL);
            b.setForeground(Estilo.TEXTO);
            b.setBackground(Estilo.PANEL);
            b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setMaximumSize(Componentes.dim(240, 58));
            b.setAlignmentX(LEFT_ALIGNMENT);
            b.addActionListener(e -> mostrar(o[0]));
            menu.put(o[0], b);
            lateral.add(b);
            lateral.add(Box.createVerticalStrut(6));
        }
        lateral.add(Box.createVerticalGlue());
        lateral.add(Componentes.etiqueta("<html>Crédito: 16.5 % anual<br>Plazo: 3 a 18 meses<br>Efectivo: 42 % de descuento</html>",
                Estilo.PEQUENA, Estilo.TENUE));

        contenido.setBackground(Estilo.FONDO);
        contenido.add(panelCatalogo, CATALOGO);
        contenido.add(panelFacturacion, FACTURAR);
        contenido.add(panelAmortizacion, AMORTIZACION);
        contenido.add(panelFacturas, FACTURAS);

        JPanel pieEstado = new JPanel(new BorderLayout());
        pieEstado.setBackground(Estilo.PANEL);
        pieEstado.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        pieEstado.add(lblEstado);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Estilo.FONDO);
        raiz.add(cabecera, BorderLayout.NORTH);
        raiz.add(lateral, BorderLayout.WEST);
        raiz.add(contenido, BorderLayout.CENTER);
        raiz.add(pieEstado, BorderLayout.SOUTH);
        setContentPane(raiz);
    }

    /** Cambia la vista visible y resalta la opción del menú. */
    public final void mostrar(String vista) {
        tarjetas.show(contenido, vista);
        menu.forEach((k, b) -> {
            boolean activo = k.equals(vista);
            b.setBackground(activo ? Estilo.PANEL2 : Estilo.PANEL);
            b.setForeground(activo ? Estilo.DORADO : Estilo.TEXTO);
        });
        alCambiarVista.accept(vista);
    }

    public void setAlCambiarVista(Consumer<String> c) {
        this.alCambiarVista = c;
    }

    public void setEstado(String texto, Color color) {
        lblEstado.setForeground(color);
        lblEstado.setText(texto);
    }

    public JTextField getTxtUrl() {
        return txtUrl;
    }

    public JButton getBtnConectar() {
        return btnConectar;
    }

    public PanelCatalogo getPanelCatalogo() {
        return panelCatalogo;
    }

    public PanelFacturacion getPanelFacturacion() {
        return panelFacturacion;
    }

    public PanelAmortizacion getPanelAmortizacion() {
        return panelAmortizacion;
    }

    public PanelFacturas getPanelFacturas() {
        return panelFacturas;
    }
}
