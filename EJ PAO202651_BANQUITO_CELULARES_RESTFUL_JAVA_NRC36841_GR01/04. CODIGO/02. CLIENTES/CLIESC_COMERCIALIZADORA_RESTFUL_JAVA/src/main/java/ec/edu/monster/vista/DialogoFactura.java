package ec.edu.monster.vista;

import ec.edu.monster.modelo.DetalleFactura;
import ec.edu.monster.modelo.Factura;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Muestra una factura: cabecera, detalle de teléfonos, totales y, si fue a
 * crédito directo, la tabla de amortización del Banco BanQuito.
 *
 * @author Grupo 1 - NRC 36841
 */
public class DialogoFactura extends JDialog {

    public DialogoFactura(Window padre, Factura f) {
        super(padre, "Factura N.º " + f.getNumero(), ModalityType.APPLICATION_MODAL);
        JPanel raiz = new JPanel(new BorderLayout(0, 14));
        raiz.setBackground(Estilo.FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        // Cabecera
        Componentes.Tarjeta cab = new Componentes.Tarjeta(new BorderLayout(14, 0));
        cab.add(new JLabel(Estilo.monster(56)), BorderLayout.WEST);
        JPanel datos = new JPanel(new GridLayout(0, 2, 18, 2));
        datos.setOpaque(false);
        datos.add(Componentes.etiqueta("FACTURA N.º " + f.getNumero(), Estilo.SUBTITULO, Estilo.DORADO));
        datos.add(Componentes.etiqueta(f.getFecha(), Estilo.NORMAL, Estilo.TENUE));
        datos.add(Componentes.etiqueta("Cliente: " + f.getCliente(), Estilo.NORMAL, Estilo.TEXTO));
        datos.add(Componentes.etiqueta("Cédula: " + f.getCedula(), Estilo.NORMAL, Estilo.TEXTO));
        String forma = f.esCredito() ? "CRÉDITO DIRECTO BANQUITO · " + f.getNumeroCuotas() + " cuotas de $ " + Estilo.dinero(f.getValorCuota())
                : "EFECTIVO (42 % de descuento)";
        JPanel bloque = new JPanel(new BorderLayout(0, 2));
        bloque.setOpaque(false);
        bloque.add(datos, BorderLayout.CENTER);
        bloque.add(Componentes.etiqueta("Forma de pago: " + forma, Estilo.NORMAL, f.esCredito() ? Estilo.CIAN : Estilo.VERDE), BorderLayout.SOUTH);
        cab.add(bloque, BorderLayout.CENTER);
        raiz.add(cab, BorderLayout.NORTH);

        // Detalle
        DefaultTableModel m = Componentes.modelo("Línea", "Celular", "Cantidad", "P. Unitario", "Subtotal");
        for (DetalleFactura d : f.getDetalles()) {
            m.addRow(new Object[]{d.getLinea(), d.getCelular(), d.getCantidad(), Estilo.dinero(d.getPrecioUnitario()), Estilo.dinero(d.getSubtotal())});
        }
        JTable t = Componentes.tabla(m);
        t.getColumnModel().getColumn(0).setMaxWidth(60);
        Componentes.alinearDerecha(t, 2, 3, 4);
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        javax.swing.JScrollPane sDet = Componentes.scroll(t);
        sDet.setPreferredSize(Componentes.dim(0, Math.min(170, 40 + 28 * f.getDetalles().size())));
        centro.add(sDet, BorderLayout.NORTH);

        JPanel totales = new JPanel(new GridLayout(3, 2, 10, 2));
        totales.setOpaque(false);
        totales.add(Componentes.etiqueta("Subtotal", Estilo.NORMAL, Estilo.TENUE));
        totales.add(Componentes.etiqueta("$ " + Estilo.dinero(f.getSubtotal()), Estilo.NORMAL, Estilo.TEXTO));
        totales.add(Componentes.etiqueta("Descuento", Estilo.NORMAL, Estilo.TENUE));
        totales.add(Componentes.etiqueta("- $ " + Estilo.dinero(f.getDescuento()), Estilo.NORMAL, Estilo.VERDE));
        totales.add(Componentes.etiqueta("TOTAL", Estilo.SUBTITULO, Estilo.TEXTO));
        totales.add(Componentes.etiqueta("$ " + Estilo.dinero(f.getTotal()), Estilo.SUBTITULO, Estilo.DORADO));
        JPanel tot = new JPanel(new BorderLayout());
        tot.setOpaque(false);
        tot.add(totales, BorderLayout.EAST);

        if (f.getCredito() != null) {
            JPanel conTabla = new JPanel(new BorderLayout(0, 10));
            conTabla.setOpaque(false);
            conTabla.add(tot, BorderLayout.NORTH);
            Componentes.Tarjeta am = new Componentes.Tarjeta(new BorderLayout());
            TablaAmortizacion ta = new TablaAmortizacion();
            ta.mostrar(f.getCredito());
            am.add(ta, BorderLayout.CENTER);
            conTabla.add(am, BorderLayout.CENTER);
            centro.add(conTabla, BorderLayout.CENTER);
        } else {
            JPanel envoltura = new JPanel(new BorderLayout());
            envoltura.setOpaque(false);
            envoltura.add(tot, BorderLayout.NORTH);
            centro.add(envoltura, BorderLayout.CENTER);
        }
        raiz.add(centro, BorderLayout.CENTER);

        JButton cerrar = Componentes.boton("Cerrar", Estilo.AZUL);
        cerrar.addActionListener(e -> dispose());
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.add(cerrar, BorderLayout.EAST);
        raiz.add(pie, BorderLayout.SOUTH);

        setContentPane(raiz);
        setSize(820, f.getCredito() != null ? 760 : 460);
        setLocationRelativeTo(padre);
    }
}
