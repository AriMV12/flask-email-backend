package com.example.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import androidx.appcompat.app.AppCompatActivity;

public class compraproducto extends AppCompatActivity {

    private String productoNombre;
    private double productoPrecio;
    private int productoImagen;
    private int cantidad = 1;
    private double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compraproducto);

        // Obtener datos del producto
        Intent intent = getIntent();
        productoNombre = intent.getStringExtra("productoNombre");
        productoPrecio = intent.getDoubleExtra("productoPrecio", 0.0);
        productoImagen = intent.getIntExtra("productoImagen", R.drawable.iconobebidas);

        // Inicializar total
        total = productoPrecio;

        // Referencias UI
        ImageView imgProducto = findViewById(R.id.imgProducto);
        TextView txtNombre = findViewById(R.id.txtNombre);
        TextView txtPrecio = findViewById(R.id.txtPrecio);
        TextView txtCantidad = findViewById(R.id.txtCantidad);
        TextView txtTotal = findViewById(R.id.txtTotal);
        Button btnMenos = findViewById(R.id.btnMenos);
        Button btnMas = findViewById(R.id.btnMas);
        Button btnComprar = findViewById(R.id.btnComprar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        // Configurar UI
        imgProducto.setImageResource(productoImagen);
        txtNombre.setText(productoNombre);
        txtPrecio.setText("$" + String.format("%.2f", productoPrecio));
        txtCantidad.setText(String.valueOf(cantidad));
        txtTotal.setText("Total: $" + String.format("%.2f", total));

        // Botón menos
        btnMenos.setOnClickListener(v -> {
            if (cantidad > 1) {
                cantidad--;
                txtCantidad.setText(String.valueOf(cantidad));
                actualizarTotal(txtTotal);
            }
        });

        // Botón más
        btnMas.setOnClickListener(v -> {
            cantidad++;
            txtCantidad.setText(String.valueOf(cantidad));
            actualizarTotal(txtTotal);
        });

        // Botón comprar
        btnComprar.setOnClickListener(v -> comprarProducto());

        // Botón cancelar
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void actualizarTotal(TextView txtTotal) {
        total = productoPrecio * cantidad;
        txtTotal.setText("Total: $" + String.format("%.2f", total));
    }

    private void comprarProducto() {
        // Obtener el correo electrónico del usuario actual
        SharedPreferences preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String email = preferences.getString("email", "");

        if (email.isEmpty()) {
            Toast.makeText(this, "Error al obtener el correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar mensaje de procesamiento
        Toast.makeText(this, "Procesando tu compra...", Toast.LENGTH_SHORT).show();

        // Enviar solicitud HTTP a EmailJS en segundo plano
        new Thread(() -> {
            try {
                URL url = new URL("https://api.emailjs.com/api/v1.0/email/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // JSON con datos a enviar a EmailJS
                String jsonInputString = "{"
                        + "\"service_id\": \"service_4pzy4w8\","  // Tu ID de servicio
                        + "\"template_id\": \"template_z02d8qe\"," // Tu ID de plantilla
                        + "\"user_id\": \"rnOObqazssZG2_vCP\","    // Tu User ID público
                        + "\"template_params\": {"
                        + "  \"to_email\": \"" + email + "\","
                        + "  \"producto_nombre\": \"" + productoNombre + "\","
                        + "  \"cantidad\": \"" + cantidad + "\","
                        + "  \"total\": \"$" + String.format("%.2f", total) + "\""
                        + "}"
                        + "}";

                // Enviar el JSON al servidor
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                boolean success = (responseCode == 200);

                // Mostrar respuesta detallada si hay error
                if (!success) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    String errorMsg = response.toString();

                    runOnUiThread(() -> Toast.makeText(compraproducto.this, "Error al enviar correo: " + errorMsg, Toast.LENGTH_LONG).show());
                    return;
                }

                // Si todo sale bien
                runOnUiThread(() -> {
                    Toast.makeText(compraproducto.this, "¡Compra exitosa! Revisa tu correo", Toast.LENGTH_LONG).show();

                    // Regresar al menú principal
                    Intent intent = new Intent(compraproducto.this, menu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                });

            } catch (Exception e) {
                // Mostrar error si ocurre una excepción
                runOnUiThread(() -> {
                    Toast.makeText(compraproducto.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                });
            }
        }).start();
    }

}