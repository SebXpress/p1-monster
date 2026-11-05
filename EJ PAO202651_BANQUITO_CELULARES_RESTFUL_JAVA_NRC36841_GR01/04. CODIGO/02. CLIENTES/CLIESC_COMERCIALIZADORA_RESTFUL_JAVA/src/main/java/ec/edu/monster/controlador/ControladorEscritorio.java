package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Celular;
import ec.edu.monster.modelo.Credito;
import ec.edu.monster.modelo.Evaluacion;
import ec.edu.monster.modelo.ExcepcionCliente;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.ServicioRest;
import ec.edu.monster.vista.DialogoFactura;
import ec.edu.monster.vista.Estilo;
import ec.edu.monster.vista.PanelAmortizacion;
import ec.edu.monster.vista.PanelCatalogo;
import ec.edu.monster.vista.PanelFacturacion;
import ec.edu.monster.vista.PanelFacturas;
import ec.edu.monster.vista.VentanaPrincipal;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Controlador (MVC) del cliente de escritorio: conecta los eventos de las
 * vistas con el servicio REST de la comercializadora. Cada llamada al servidor
 * se ejecuta en segundo plano (SwingWorker) para no congelar la ventana.
 *
 * @author Grupo 1 - NRC 36841
 */
public class ControladorEscritorio {

    private final ServicioRest servicio;
    private final VentanaPrincipal ventana = new VentanaPrincipal();

    /** Tarea que llama al servidor y puede fallar con ExcepcionCliente. */
    @FunctionalInterface
    private interface Tarea<T> {
        T ejecutar() throws ExcepcionCliente;
    }

    public ControladorEscritorio(ServicioRest servicio) {
        this.servicio = servicio;
    }

    public void iniciar() {
        ventana.getTxtUrl().setText(servicio.getUrlBase());
        ventana.getBtnConectar().addActionListener(e -> {
            servicio.setUrlBase(ventana.getTxtUrl().getText());
            ventana.getTxtUrl().setText(servicio.getUrlBase());
            cargarCatalogo();
            cargarCelularesVenta();
        });
        configurarCatalogo();
        configurarFacturacion();
        configurarAmortizacion();
        configurarFacturas();
        ventana.setAlCambiarVista(v -> {
            switch (v) {
                case VentanaPrincipal.CATALOGO -> cargarCatalogo();
                case VentanaPrincipal.FACTURAR -> cargarCelularesVenta();
                case VentanaPrincipal.FACTURAS -> cargarFacturas();
                default -> { }
            }
        });
        ventana.mostrar(VentanaPrincipal.CATALOGO);
        ventana.setVisible(true);
    }

    // ---------------------------------------------------------------- catálogo

    private void configurarCatalogo() {
        PanelCatalogo p = ventana.getPanelCatalogo();
        p.getBtnBuscar().addActionListener(e -> cargarCatalogo());
        p.getTxtBuscar().addActionListener(e -> cargarCatalogo());
        p.getChkActivos().addActionListener(e -> cargarCatalogo());
        p.getBtnGuardar().addActionListener(e -> {
            Celular c;
            try {
                c = p.leerFormulario();
            } catch (IllegalArgumentException ex) {
                p.error();
                estadoError(ex.getMessage());
                return;
            }
            boolean nuevo = c.getCodigo() == null;
            ejecutar(p.getBtnGuardar(), () -> nuevo ? servicio.crearCelular(c) : servicio.actualizarCelular(c), r -> {
                p.exito();
                estadoOk(servicio.getUltimoMensaje() + ": " + r.nombre());
                p.limpiar();
                cargarCatalogo();
            }, p::error);
        });
        p.getBtnEliminar().addActionListener(e -> {
            Integer codigo = p.getCodigoSeleccionado();
            if (codigo == null || JOptionPane.showConfirmDialog(ventana, "¿Eliminar el celular N.º " + codigo + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            ejecutar(p.getBtnEliminar(), () -> servicio.eliminarCelular(codigo), m -> {
                estadoOk(m);
                p.limpiar();
                cargarCatalogo();
            }, p::error);
        });
    }

    private void cargarCatalogo() {
        PanelCatalogo p = ventana.getPanelCatalogo();
        ejecutar(p.getBtnBuscar(), () -> servicio.listarCelulares(p.getFiltro(), p.isSoloActivos()), l -> {
            p.mostrar(l);
            estadoOk(l.size() + " celular(es) en el catálogo · " + servicio.getUrlBase());
        }, null);
    }

    // ---------------------------------------------------------------- facturación

    private void configurarFacturacion() {
        PanelFacturacion p = ventana.getPanelFacturacion();
        p.getBtnRecargar().addActionListener(e -> cargarCelularesVenta());
        p.getBtnEvaluar().addActionListener(e -> {
            if (p.subtotal() <= 0) {
                p.error();
                estadoError("Agregue al menos un teléfono para consultar el crédito");
                return;
            }
            String cedula = p.getCedula();
            double monto = p.subtotal();
            ejecutar(p.getBtnEvaluar(), () -> servicio.evaluarCredito(cedula, monto), (Evaluacion ev) -> {
                p.mostrarEvaluacion(ev);
                if (ev.isSujetoCredito() && ev.isAlcanza()) {
                    estadoOk(servicio.getUltimoMensaje());
                } else {
                    estadoError(servicio.getUltimoMensaje());
                }
            }, p::error);
        });
        p.getBtnFacturar().addActionListener(e -> {
            List<String> d = p.getDatosComprador();
            String forma = p.isCredito() ? "CREDITO" : "EFECTIVO";
            Integer cuotas = p.isCredito() ? p.getNumeroCuotas() : null;
            if (p.isCredito()) {
                p.mensajePago("Consultando al Banco BanQuito y otorgando el crédito…", Estilo.AMARILLO);
            }
            ejecutar(p.getBtnFacturar(), () -> servicio.facturar(d.get(0), d.get(1), d.get(2), d.get(3), d.get(4), forma, cuotas, p.getItems()),
                    (Factura f) -> {
                        estadoOk(servicio.getUltimoMensaje());
                        p.limpiar();
                        new DialogoFactura(ventana, f).setVisible(true);
                    }, () -> {
                        p.error();
                        p.mensajePago("✖ " + ultimoError, Estilo.ROJO);
                    });
        });
    }

    private void cargarCelularesVenta() {
        PanelFacturacion p = ventana.getPanelFacturacion();
        ejecutar(p.getBtnRecargar(), () -> servicio.listarCelulares(null, true), p::setCelulares, null);
    }

    // ---------------------------------------------------------------- amortización

    private void configurarAmortizacion() {
        PanelAmortizacion p = ventana.getPanelAmortizacion();
        p.getBtnConsultar().addActionListener(e -> {
            String cedula = p.getCedula();
            ejecutar(p.getBtnConsultar(), () -> servicio.creditosCliente(cedula), (List<Credito> l) -> {
                p.mostrar(l);
                estadoOk(l.isEmpty() ? "El cliente no tiene créditos en el Banco BanQuito" : servicio.getUltimoMensaje());
            }, p::error);
        });
    }

    // ---------------------------------------------------------------- facturas

    private void configurarFacturas() {
        PanelFacturas p = ventana.getPanelFacturas();
        p.getBtnBuscar().addActionListener(e -> cargarFacturas());
        p.setAlAbrir(numero -> ejecutar(null, () -> servicio.consultarFactura(numero),
                f -> new DialogoFactura(ventana, f).setVisible(true), null));
    }

    private void cargarFacturas() {
        PanelFacturas p = ventana.getPanelFacturas();
        ejecutar(p.getBtnBuscar(), () -> servicio.listarFacturas(p.getCedula()), (List<Factura> l) -> {
            p.mostrar(l);
            estadoOk(l.size() + " factura(s)");
        }, null);
    }

    // ---------------------------------------------------------------- utilidades

    private String ultimoError = "";

    /**
     * Ejecuta la tarea en segundo plano; deshabilita el botón mientras tanto.
     * En error muestra el mensaje en la barra de estado y llama a 'alFallar'.
     */
    private <T> void ejecutar(JButton boton, Tarea<T> tarea, Consumer<T> alTerminar, Runnable alFallar) {
        if (boton != null) {
            boton.setEnabled(false);
        }
        ventana.setEstado("Procesando…", Estilo.AMARILLO);
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarea.ejecutar();
            }

            @Override
            protected void done() {
                if (boton != null) {
                    boton.setEnabled(true);
                }
                try {
                    alTerminar.accept(get());
                } catch (ExecutionException ex) {
                    Throwable c = ex.getCause();
                    ultimoError = c instanceof ExcepcionCliente ? c.getMessage() : "Error inesperado: " + c;
                    estadoError(ultimoError);
                    if (alFallar != null) {
                        alFallar.run();
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    private void estadoOk(String m) {
        ventana.setEstado("✔ " + m, Estilo.VERDE);
    }

    private void estadoError(String m) {
        ventana.setEstado("✖ " + m, Estilo.ROJO);
    }
}
