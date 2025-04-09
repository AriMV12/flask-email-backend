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

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class productoescaneo extends AppCompatActivity {

    private String barcode;
    private String productoNombre;
    private double productoPrecio;
    private String productoDescripcion;
    private int productoImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productoescaneo);

        // Obtener referencias a los elementos de la interfaz
        TextView txtNombre = findViewById(R.id.txtNombre);
        TextView txtPrecio = findViewById(R.id.txtPrecio);
        TextView txtDescripcion = findViewById(R.id.txtDescripcion);
        ImageView imgProducto = findViewById(R.id.imgProducto);
        Button btnComprar = findViewById(R.id.btnComprar);
        Button btnVolver = findViewById(R.id.btnVolver);

        // Obtener el código de barras de la actividad anterior
        barcode = getIntent().getStringExtra("barcode");

        // Configurar información del producto (para este ejemplo)
        if (barcode.equals("351914619801") || barcode.equals("351914619801\n")) {
            productoNombre = "Café Americano";
            productoPrecio = 35.50;
            productoDescripcion = "Café americano preparado con granos 100% arábigos, molidos al momento. 12 oz.";
            productoImagen = R.drawable.iconobebidas; // Cambiar por una imagen real
        }

        // Mostrar la información en la interfaz
        txtNombre.setText(productoNombre);
        txtPrecio.setText("$" + String.format("%.2f", productoPrecio));
        txtDescripcion.setText(productoDescripcion);
        imgProducto.setImageResource(productoImagen);

        // Guardar en el historial
        guardarEnHistorial();

        // Configurar botón de compra
        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(productoescaneo.this, compraproducto.class);
                intent.putExtra("productoNombre", productoNombre);
                intent.putExtra("productoPrecio", productoPrecio);
                intent.putExtra("productoImagen", productoImagen);
                startActivity(intent);
            }
        });

        // Configurar botón de volver
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Volver a la actividad anterior
            }
        });
    }

    private void guardarEnHistorial() {
        // Obtener fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String fecha = dateFormat.format(new Date());

        // Crear objeto de escaneo
        ProductoEscaneo escaneo = new ProductoEscaneo(barcode, productoNombre, productoPrecio, fecha);

        // Obtener historial actual
        SharedPreferences preferences = getSharedPreferences("escaneos_historial", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("historial", null);
        Type type = new TypeToken<ArrayList<ProductoEscaneo>>() {}.getType();
        List<ProductoEscaneo> historial = gson.fromJson(json, type);

        if (historial == null) {
            historial = new ArrayList<>();
        }

        // Añadir nuevo escaneo
        historial.add(escaneo);

        // Guardar historial actualizado
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("historial", gson.toJson(historial));
        editor.apply();

        Toast.makeText(this, "Producto guardado en historial", Toast.LENGTH_SHORT).show();
    }

    // Clase para representar un producto escaneado
    public static class ProductoEscaneo {
        private String barcode;
        private String nombre;
        private double precio;
        private String fecha;

        public ProductoEscaneo(String barcode, String nombre, double precio, String fecha) {
            this.barcode = barcode;
            this.nombre = nombre;
            this.precio = precio;
            this.fecha = fecha;
        }

        public String getBarcode() {
            return barcode;
        }

        public String getNombre() {
            return nombre;
        }

        public double getPrecio() {
            return precio;
        }

        public String getFecha() {
            return fecha;
        }
    }
}