package ec.edu.monster.vista;

import ec.edu.monster.modelo.Credito;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Consulta de la tabla de amortización: con la cédula del comprador se piden
 * al Banco BanQuito (a través de la comercializadora) sus créditos y se
 * muestra la tabla del crédito seleccionado.
 *
 * @author Grupo 1 - NRC 36841
 */
public class PanelAmortizacion extends JPanel {

    private final JTextField txtCedula = Componentes.campo("Cédula del cliente");
    private final JButton btnConsultar = Componentes.boton("Consultar", Estilo.AZUL);
    private final DefaultTableModel modelo = Componentes.modelo("Crédito", "Fecha", "Monto", "Cuotas", "Cuota", "Estado");
    private final JTable tabla = Componentes.tabla(modelo);
    private final TablaAmortizacion amortizacion = new TablaAmortizacion();
    private final Componentes.Tarjeta tarjetaBusqueda = new Componentes.Tarjeta(new BorderLayout(0, 10));
    private List<Credito> creditos = List.of();

    public PanelAmortizacion() {
        super(new BorderLayout(16, 16));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        add(Componentes.encabezado("Tabla de amortización",
                "Créditos directos otorgados por el Banco BanQuito · cuota fija mensual · 16.5 % anual"), BorderLayout.NORTH);

        JPanel busqueda = new JPanel(new BorderLayout(8, 0));
        busqueda.setOpaque(false);
        busqueda.add(txtCedula, BorderLayout.CENTER);
        busqueda.add(btnConsultar, BorderLayout.EAST);
        tarjetaBusqueda.add(busqueda, BorderLayout.NORTH);
        Componentes.alinearDerecha(tabla, 2, 3, 4);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(130);
        tarjetaBusqueda.add(Componentes.scroll(tabla), BorderLayout.CENTER);
        tarjetaBusqueda.setPreferredSize(Componentes.dim(460, 0));
        add(tarjetaBusqueda, BorderLayout.WEST);

        Componentes.Tarjeta detalle = new Componentes.Tarjeta(new BorderLayout());
        detalle.add(amortizacion, BorderLayout.CENTER);
        add(detalle, BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int f = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && f >= 0 && f < creditos.size()) {
                amortizacion.mostrar(creditos.get(f));
            }
        });
        txtCedula.addActionListener(e -> btnConsultar.doClick());
    }

    public void mostrar(List<Credito> lista) {
        creditos = lista;
        modelo.setRowCount(0);
        for (Credito c : lista) {
            modelo.addRow(new Object[]{c.getCodigoCredito(), c.getFecha(), Estilo.dinero(c.getMonto()), c.getNumeroCuotas(),
                Estilo.dinero(c.getCuota()), c.getEstado()});
        }
        amortizacion.mostrar(null);
        if (!lista.isEmpty()) {
            tabla.setRowSelectionInterval(0, 0);
        }
    }

    public void error() {
        Componentes.sacudir(tarjetaBusqueda);
        Componentes.destello(tarjetaBusqueda, Estilo.ROJO);
    }

    public void setCedula(String cedula) {
        txtCedula.setText(cedula);
    }

    public String getCedula() {
        return txtCedula.getText().trim();
    }

    public JButton getBtnConsultar() {
        return btnConsultar;
    }
}
