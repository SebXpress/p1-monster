package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Celular;
import ec.edu.monster.modelo.Credito;
import ec.edu.monster.modelo.Evaluacion;
import ec.edu.monster.modelo.ExcepcionCliente;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.ServicioRest;
import ec.edu.monster.vista.VistaConsola;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador del cliente de consola (MVC): toma la opción de la vista,
 * llama al servicio REST de la comercializadora y muestra el resultado.
 *
 * @author Grupo 1 - NRC 36841
 */
public class ControladorConsola {

    private final VistaConsola vista;
    private final ServicioRest servicio;

    public ControladorConsola(VistaConsola vista, ServicioRest servicio) {
        this.vista = vista;
        this.servicio = servicio;
    }

    public void iniciar() {
        vista.banner(servicio.getUrlBase());
        while (true) {
            int op = vista.menu();
            if (op == 0) {
                vista.ok("Hasta pronto");
                return;
            }
            try {
                switch (op) {
                    case 1 -> catalogo();
                    case 2 -> registrar();
                    case 3 -> actualizar();
                    case 4 -> eliminar();
                    case 5 -> facturar();
                    case 6 -> amortizacion();
                    case 7 -> facturas();
                    default -> vista.error("Opción no válida");
                }
            } catch (ExcepcionCliente e) {
                vista.error(e.getMessage());
            }
        }
    }

    private void catalogo() throws ExcepcionCliente {
        vista.titulo("CATÁLOGO DE CELULARES");
        String f = vista.leerTexto("Buscar por marca o modelo (Enter = todos)", "");
        vista.tablaCelulares(servicio.listarCelulares(f, false));
    }

    private Celular leerDatos(Celular base) {
        Celular c = new Celular();
        c.setMarca(vista.leerTexto("Marca", base == null ? null : base.getMarca()));
        c.setModelo(vista.leerTexto("Modelo", base == null ? null : base.getModelo()));
        c.setDescripcion(vista.leerTexto("Descripción", base == null ? "" : base.getDescripcion()));
        c.setPrecio(vista.leerPrecio("Precio de venta", base == null ? null : base.getPrecio()));
        String e = vista.leerTexto("Estado (ACTIVO/INACTIVO)", base == null ? "ACTIVO" : base.getEstado()).toUpperCase();
        c.setEstado(e);
        return c;
    }

    private void registrar() throws ExcepcionCliente {
        vista.titulo("REGISTRAR CELULAR");
        Celular c = servicio.crearCelular(leerDatos(null));
        vista.ok(servicio.getUltimoMensaje() + " con código " + c.getCodigo());
    }

    private void actualizar() throws ExcepcionCliente {
        vista.titulo("ACTUALIZAR CELULAR");
        vista.tablaCelulares(servicio.listarCelulares("", false));
        int cod = vista.leerEntero("Código del celular", 1, Integer.MAX_VALUE);
        Celular actual = servicio.consultarCelular(cod);
        vista.info("Enter mantiene el valor actual");
        Celular c = leerDatos(actual);
        c.setCodigo(cod);
        servicio.actualizarCelular(c);
        vista.ok(servicio.getUltimoMensaje());
    }

    private void eliminar() throws ExcepcionCliente {
        vista.titulo("ELIMINAR CELULAR");
        vista.tablaCelulares(servicio.listarCelulares("", false));
        int cod = vista.leerEntero("Código del celular", 1, Integer.MAX_VALUE);
        Celular c = servicio.consultarCelular(cod);
        if (vista.confirmar("¿Eliminar el " + c.nombre() + "?")) {
            vista.ok(servicio.eliminarCelular(cod));
        }
    }

    private void facturar() throws ExcepcionCliente {
        vista.titulo("FACTURAR VENTA");
        List<Celular> catalogo = servicio.listarCelulares("", true);
        vista.tablaCelulares(catalogo);
        Map<Integer, Integer> items = new LinkedHashMap<>();
        double total = 0;
        do {
            int cod = vista.leerEntero("Código del celular", 1, Integer.MAX_VALUE);
            Celular c = catalogo.stream().filter(x -> x.getCodigo() == cod).findFirst().orElse(null);
            if (c == null) {
                vista.error("El código " + cod + " no está en el catálogo activo");
                continue;
            }
            int cant = vista.leerEntero("Cantidad", 1, 100);
            items.merge(cod, cant, Integer::sum);
            total += c.getPrecio() * cant;
            vista.info("Agregado: " + cant + " x " + c.nombre() + " · subtotal $" + VistaConsola.dinero(total));
        } while (vista.confirmar("¿Agregar otro celular?"));
        if (items.isEmpty()) {
            vista.error("La factura no tiene celulares");
            return;
        }
        String cedula = vista.leerCedula("Cédula del cliente");
        String nombre = vista.leerTexto("Nombre del cliente", null);
        String direccion = vista.leerTexto("Dirección", "");
        String telefono = vista.leerTexto("Teléfono", "");
        String correo = vista.leerTexto("Correo", "");
        vista.info(String.format("Efectivo: $%s (42 %% de descuento)  ·  Crédito directo: $%s", VistaConsola.dinero(total * 0.58), VistaConsola.dinero(total)));
        int forma = vista.leerEntero("Forma de pago: 1 = Efectivo, 2 = Crédito directo BanQuito", 1, 2);
        Integer cuotas = null;
        if (forma == 2) {
            vista.esperando("Consultando al Banco BanQuito");
            Evaluacion ev = servicio.evaluarCredito(cedula, total);
            if (!ev.isSujetoCredito()) {
                vista.error("El Banco BanQuito indica que NO es sujeto de crédito:");
                ev.getMotivos().forEach(m -> vista.error("  · " + m));
                return;
            }
            vista.ok(ev.getNombre() + " es sujeto de crédito · monto máximo $" + VistaConsola.dinero(ev.getMontoMaximo()));
            if (!ev.isAlcanza()) {
                vista.error("El valor $" + VistaConsola.dinero(total) + " supera el monto máximo aprobado: no se puede facturar a crédito");
                return;
            }
            cuotas = vista.leerEntero("Número de cuotas mensuales (3 a 18)", 3, 18);
        }
        vista.esperando("Facturando");
        Factura f = servicio.facturar(cedula, nombre, direccion, telefono, correo, forma == 1 ? "EFECTIVO" : "CREDITO", cuotas, items);
        vista.ok(servicio.getUltimoMensaje());
        vista.factura(f);
    }

    private void amortizacion() throws ExcepcionCliente {
        vista.titulo("TABLA DE AMORTIZACIÓN");
        String cedula = vista.leerCedula("Cédula del cliente");
        vista.esperando("Consultando al Banco BanQuito");
        List<Credito> l = servicio.creditosCliente(cedula);
        if (l.isEmpty()) {
            vista.info("El cliente no tiene préstamos");
            return;
        }
        l.forEach(vista::amortizacion);
    }

    private void facturas() throws ExcepcionCliente {
        vista.titulo("FACTURAS EMITIDAS");
        String ced = vista.leerTexto("Cédula (Enter = todas)", "");
        List<Factura> l = servicio.listarFacturas(ced);
        vista.tablaFacturas(l);
        if (!l.isEmpty() && vista.confirmar("¿Ver el detalle de una factura?")) {
            vista.factura(servicio.consultarFactura(vista.leerEntero("N.º de factura", 1, Integer.MAX_VALUE)));
        }
    }
}
