package com.example.proyectofinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class historialproducto extends AppCompatActivity implements SensorEventListener {

    private RecyclerView recyclerView;
    private HistorialAdapter adapter;
    private List<productoescaneo.ProductoEscaneo> historial;
    private SensorManager sensorManager;
    private Sensor acelerometro;
    private long ultimoTiempo;
    private float ultimoX, ultimoY, ultimoZ;
    private static final int UMBRAL_SACUDIDA = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historialproducto);

        // Inicializar el acelerómetro
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewHistorial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar historial
        cargarHistorial();

        // Mensaje instructivo
        TextView tvInstrucciones = findViewById(R.id.tvInstrucciones);
        tvInstrucciones.setText("Sacude tu teléfono para borrar el historial.");
    }

    private void cargarHistorial() {
        SharedPreferences preferences = getSharedPreferences("escaneos_historial", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("historial", null);
        Type type = new TypeToken<ArrayList<productoescaneo.ProductoEscaneo>>() {}.getType();
        historial = gson.fromJson(json, type);

        if (historial == null) {
            historial = new ArrayList<>();
            TextView tvNoHistorial = findViewById(R.id.tvNoHistorial);
            tvNoHistorial.setVisibility(View.VISIBLE);
        } else {
            TextView tvNoHistorial = findViewById(R.id.tvNoHistorial);
            tvNoHistorial.setVisibility(historial.isEmpty() ? View.VISIBLE : View.GONE);
        }

        adapter = new HistorialAdapter(historial);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registrar el sensor cuando la actividad está activa
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistrar el sensor cuando la actividad no está activa
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long tiempoActual = System.currentTimeMillis();

            // Solo detectar cada 100ms para evitar demasiadas actualizaciones
            if ((tiempoActual - ultimoTiempo) > 100) {
                long difTiempo = tiempoActual - ultimoTiempo;
                ultimoTiempo = tiempoActual;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float velocidad = Math.abs(x + y + z - ultimoX - ultimoY - ultimoZ) / difTiempo * 10000;

                if (velocidad > UMBRAL_SACUDIDA) {
                    // Detectó sacudida, borrar historial
                    borrarHistorial();
                }

                ultimoX = x;
                ultimoY = y;
                ultimoZ = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario implementar para este caso
    }

    private void borrarHistorial() {
        // Borrar historial de SharedPreferences
        SharedPreferences preferences = getSharedPreferences("escaneos_historial", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Limpiar la lista y notificar al adaptador
        historial.clear();
        adapter.notifyDataSetChanged();

        // Mostrar mensaje
        Toast.makeText(this, "Historial borrado", Toast.LENGTH_SHORT).show();

        // Actualizar mensaje de "No hay historial"
        TextView tvNoHistorial = findViewById(R.id.tvNoHistorial);
        tvNoHistorial.setVisibility(View.VISIBLE);
    }

    // Adaptador para el RecyclerView
    private class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

        private List<productoescaneo.ProductoEscaneo> escaneos;

        public HistorialAdapter(List<productoescaneo.ProductoEscaneo> escaneos) {
            this.escaneos = escaneos;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_historial, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            productoescaneo.ProductoEscaneo escaneo = escaneos.get(position);
            holder.tvProducto.setText(escaneo.getNombre());
            holder.tvCodigo.setText("Código: " + escaneo.getBarcode());
            holder.tvPrecio.setText("$" + String.format("%.2f", escaneo.getPrecio()));
            holder.tvFecha.setText(escaneo.getFecha());
        }

        @Override
        public int getItemCount() {
            return escaneos.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvProducto, tvCodigo, tvPrecio, tvFecha;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvProducto = itemView.findViewById(R.id.tvProducto);
                tvCodigo = itemView.findViewById(R.id.tvCodigo);
                tvPrecio = itemView.findViewById(R.id.tvPrecio);
                tvFecha = itemView.findViewById(R.id.tvFecha);
            }
        }
    }
}