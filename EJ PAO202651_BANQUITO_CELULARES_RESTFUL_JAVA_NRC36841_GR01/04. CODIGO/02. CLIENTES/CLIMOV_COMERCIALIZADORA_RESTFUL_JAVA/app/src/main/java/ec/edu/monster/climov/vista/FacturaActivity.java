package ec.edu.monster.climov.vista;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.climov.R;
import ec.edu.monster.climov.controlador.ControladorComercializadora;
import ec.edu.monster.climov.modelo.Factura;
import ec.edu.monster.climov.modelo.SesionApp;

/**
 * Detalle de una factura y, si fue a crédito directo, su tabla de amortización.
 *
 * @author Grupo 1 - NRC 36841
 */
public class FacturaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        int numero = getIntent().getIntExtra("numero", 0);
        ((TextView) findViewById(R.id.lblTitulo)).setText("Factura N.º " + numero);
        ((TextView) findViewById(R.id.lblSubtitulo)).setText("Comercializadora Monster");
        findViewById(R.id.btnCabecera).setOnClickListener(v -> finish());
        ((View) findViewById(R.id.txtBuscar).getParent()).setVisibility(View.GONE);
        LinearLayout contenido = findViewById(R.id.contenido);
        ProgressBar progreso = findViewById(R.id.progreso);

        Factura nueva = SesionApp.get().getUltimaFactura();
        if (nueva != null && nueva.getNumero() == numero) {
            SesionApp.get().setUltimaFactura(null);
            Filas.aviso(contenido, "✔ Factura emitida correctamente", Filas.VERDE);
            Filas.factura(contenido, nueva);
            return;
        }
        progreso.setVisibility(View.VISIBLE);
        new ControladorComercializadora(this).consultarFactura(numero, f -> {
            progreso.setVisibility(View.INVISIBLE);
            Filas.factura(contenido, f);
        }, e -> {
            progreso.setVisibility(View.INVISIBLE);
            Filas.aviso(contenido, "✖ " + e.getMessage(), Filas.ROJO);
        });
    }
}
