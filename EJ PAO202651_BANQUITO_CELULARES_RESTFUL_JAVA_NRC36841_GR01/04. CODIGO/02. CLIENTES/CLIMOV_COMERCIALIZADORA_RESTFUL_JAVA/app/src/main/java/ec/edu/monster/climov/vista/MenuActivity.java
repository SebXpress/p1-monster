package ec.edu.monster.climov.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.climov.R;
import ec.edu.monster.climov.controlador.ControladorComercializadora;
import ec.edu.monster.climov.modelo.SesionApp;

/**
 * Pantalla de inicio: menú de la comercializadora y URL del servidor REST.
 *
 * @author Grupo 1 - NRC 36841
 */
public class MenuActivity extends AppCompatActivity {

    private EditText txtServidor;
    private TextView lblMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Pantalla.ajustarTeclado(this);
        ((TextView) findViewById(R.id.lblSubtitulo)).setText("Cliente móvil · Grupo 1 · NRC 36841");
        findViewById(R.id.btnCabecera).setVisibility(View.GONE);
        ImageView monster = findViewById(R.id.imgMonster);
        Animaciones.flotar(monster);

        txtServidor = findViewById(R.id.txtServidor);
        lblMensaje = findViewById(R.id.lblMensaje);
        txtServidor.setText(SesionApp.get().getServicio(this).getUrlBase());

        abrir(R.id.btnCatalogo, CatalogoActivity.class, 0);
        abrir(R.id.btnFacturar, FacturarActivity.class, 1);
        abrir(R.id.btnAmortizacion, AmortizacionActivity.class, 2);
        abrir(R.id.btnFacturas, FacturasActivity.class, 3);

        Button guardar = findViewById(R.id.btnGuardarUrl);
        guardar.setOnClickListener(v -> probarConexion());
    }

    private void abrir(int id, Class<?> pantalla, int orden) {
        View b = findViewById(id);
        Animaciones.aparecer(b, 120L * orden);
        b.setOnClickListener(v -> startActivity(new Intent(this, pantalla)));
    }

    private void probarConexion() {
        SesionApp.get().guardarUrl(this, txtServidor.getText().toString());
        txtServidor.setText(SesionApp.get().getServicio(this).getUrlBase());
        lblMensaje.setTextColor(Filas.TENUE);
        lblMensaje.setText("Probando conexión…");
        new ControladorComercializadora(this).listarCelulares(null, true,
                l -> {
                    lblMensaje.setTextColor(Filas.VERDE);
                    lblMensaje.setText("✔ Conectado: " + l.size() + " celulares activos en el catálogo");
                },
                e -> {
                    lblMensaje.setTextColor(Filas.ROJO);
                    lblMensaje.setText("✖ " + e.getMessage());
                    Animaciones.sacudir(findViewById(R.id.tarjetaServidor));
                });
    }
}
