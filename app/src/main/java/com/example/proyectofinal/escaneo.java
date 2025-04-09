package com.example.proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class escaneo extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Iniciamos el escaneo al abrir la actividad
            iniciarEscaneo();
        }

        private void iniciarEscaneo() {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Escanea el código de barras");
            integrator.setCameraId(0); // Usar cámara trasera
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.setOrientationLocked(false);
            integrator.initiateScan();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show();
                    finish(); // Volver al menú si se cancela
                } else {
                    String barcode = result.getContents();
                    // Para probar con el código específico
                    if (barcode.equals("351914619801") || barcode.equals("351914619801\n")) {
                        // Ejemplo de código fijo para pruebas
                        Intent intent = new Intent(this, productoescaneo.class);
                        intent.putExtra("barcode", barcode);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Código no reconocido: " + barcode, Toast.LENGTH_LONG).show();
                        iniciarEscaneo(); // Reintentar escaneo
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }