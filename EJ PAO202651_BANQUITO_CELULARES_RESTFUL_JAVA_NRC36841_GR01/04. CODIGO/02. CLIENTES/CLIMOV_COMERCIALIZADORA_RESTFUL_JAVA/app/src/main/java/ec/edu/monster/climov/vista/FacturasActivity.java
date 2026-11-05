package ec.edu.monster.climov.vista;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.climov.R;
import ec.edu.monster.climov.controlador.ControladorComercializadora;
import ec.edu.monster.climov.modelo.Factura;
import java.util.List;

/**
 * Historial de facturas (filtro opcional por cédula); toque una para ver su
 * detalle.
 *
 * @author Grupo 1 - NRC 36841
 */
public class FacturasActivity extends AppCompatActivity {

    private ControladorComercializadora controlador;
    private EditText txtCedula;
    private LinearLayout contenido;
    private ProgressBar progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        Pantalla.ajustarTeclado(this);
        controlador = new ControladorComercializadora(this);
        ((TextView) findViewById(R.id.lblTitulo)).setText("Facturas");
        ((TextView) findViewById(R.id.lblSubtitulo)).setText("Ventas emitidas");
        findViewById(R.id.btnCabecera).setOnClickListener(v -> finish());
        txtCedula = findViewById(R.id.txtBuscar);
        txtCedula.setHint("Cédula (vacío = todas)");
        txtCedula.setInputType(InputType.TYPE_CLASS_NUMBER);
        contenido = findViewById(R.id.contenido);
        progreso = findViewById(R.id.progreso);
        findViewById(R.id.btnBuscar).setOnClickListener(v -> cargar());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargar();
    }

    private void cargar() {
        progreso.setVisibility(View.VISIBLE);
        controlador.listarFacturas(txtCedula.getText().toString().trim(), this::mostrar, e -> {
            progreso.setVisibility(View.INVISIBLE);
            contenido.removeAllViews();
            Filas.aviso(contenido, "✖ " + e.getMessage(), Filas.ROJO);
        });
    }

    private void mostrar(List<Factura> lista) {
        progreso.setVisibility(View.INVISIBLE);
        contenido.removeAllViews();
        if (lista.isEmpty()) {
            Filas.aviso(contenido, "No hay facturas", Filas.TENUE);
        }
        int i = 0;
        for (Factura f : lista) {
            LinearLayout t = Filas.tarjeta(this);
            LinearLayout arriba = new LinearLayout(this);
            TextView n = Filas.texto(this, "N.º " + f.getNumero() + " · " + f.getCliente(), Filas.TEXTO, 15, true);
            n.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            arriba.addView(n);
            arriba.addView(Filas.texto(this, "$ " + Filas.dinero(f.getTotal()), Filas.DORADO, 15, true));
            t.addView(arriba);
            t.addView(Filas.texto(this, f.getFecha() + " · C.I. " + f.getCedula(), Filas.TENUE, 12, false));
            t.addView(Filas.texto(this, f.esCredito()
                    ? "CRÉDITO · " + f.getNumeroCuotas() + " cuotas de $ " + Filas.dinero(f.getValorCuota())
                    : "EFECTIVO · descuento $ " + Filas.dinero(f.getDescuento()), f.esCredito() ? Filas.CIAN : Filas.VERDE, 12, true));
            t.setOnClickListener(v -> startActivity(new Intent(this, FacturaActivity.class).putExtra("numero", f.getNumero())));
            contenido.addView(t);
            Animaciones.aparecer(t, 40L * i++);
        }
    }
}
