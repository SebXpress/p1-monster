package ec.edu.monster.vista;

import ec.edu.monster.modelo.Factura;
import java.awt.BorderLayout;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Historial de facturas (filtro opcional por cédula). Doble clic abre el
 * detalle de la factura con su tabla de amortización si fue a crédito.
 *
 * @author Grupo 1 - NRC 36841
 */
public class PanelFacturas extends JPanel {

    private final JTextField txtCedula = Componentes.campo("Cédula (vacío = todas)");
    private final JButton btnBuscar = Componentes.boton("Buscar", Estilo.AZUL);
    private final JButton btnVer = Componentes.boton("Ver detalle", Estilo.CIAN);
    private final DefaultTableModel modelo = Componentes.modelo("N.º", "Fecha", "Cédula", "Cliente", "Forma de pago",
            "Subtotal", "Descuento", "Total", "Crédito");
    private final JTable tabla = Componentes.tabla(modelo);
    private List<Factura> facturas = List.of();
    private Consumer<Integer> alAbrir = n -> { };

    public PanelFacturas() {
        super(new BorderLayout(16, 16));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        add(Componentes.encabezado("Facturas emitidas", "Doble clic sobre una factura para ver su detalle."), BorderLayout.NORTH);

        Componentes.Tarjeta t = new Componentes.Tarjeta(new BorderLayout(0, 12));
        JPanel filtro = new JPanel(new BorderLayout(8, 0));
        filtro.setOpaque(false);
        filtro.add(txtCedula, BorderLayout.CENTER);
        JPanel b = new JPanel(new java.awt.GridLayout(1, 2, 8, 0));
        b.setOpaque(false);
        b.add(btnBuscar);
        b.add(btnVer);
        filtro.add(b, BorderLayout.EAST);
        t.add(filtro, BorderLayout.NORTH);
        tabla.getColumnModel().getColumn(0).setMaxWidth(60);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(240);
        Componentes.alinearDerecha(tabla, 5, 6, 7);
        t.add(Componentes.scroll(tabla), BorderLayout.CENTER);
        add(t, BorderLayout.CENTER);

        txtCedula.addActionListener(e -> btnBuscar.doClick());
        btnVer.addActionListener(e -> abrirSeleccionada());
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirSeleccionada();
                }
            }
        });
    }

    private void abrirSeleccionada() {
        int f = tabla.getSelectedRow();
        if (f >= 0 && f < facturas.size()) {
            alAbrir.accept(facturas.get(f).getNumero());
        }
    }

    public void mostrar(List<Factura> lista) {
        facturas = lista;
        modelo.setRowCount(0);
        for (Factura f : lista) {
            modelo.addRow(new Object[]{f.getNumero(), f.getFecha(), f.getCedula(), f.getCliente(), f.getFormaPago(),
                Estilo.dinero(f.getSubtotal()), Estilo.dinero(f.getDescuento()), Estilo.dinero(f.getTotal()),
                f.esCredito() ? "N.º " + f.getCodigoCredito() + " · " + f.getNumeroCuotas() + " x " + Estilo.dinero(f.getValorCuota()) : ""});
        }
    }

    public void setAlAbrir(Consumer<Integer> c) {
        this.alAbrir = c;
    }

    public String getCedula() {
        return txtCedula.getText().trim();
    }

    public JButton getBtnBuscar() {
        return btnBuscar;
    }
}
