package com.example.proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class menu extends AppCompatActivity {

    CardView cardCafe, cardSnack, cardCake, cardGrocery, cardFastFood, cardHealthyFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Referenciar los CardViews
        cardCafe = findViewById(R.id.cardCafe);         // Bebidas
        cardSnack = findViewById(R.id.cardSnack);       // Café
        cardCake = findViewById(R.id.cardCake);         // Pastelitos
        cardGrocery = findViewById(R.id.cardGrocery);   // Desayunos
        cardFastFood = findViewById(R.id.cardFastFood); // Escanear
        cardHealthyFood = findViewById(R.id.cardHealthyFood); // Historial de escaneos

        // Listener para Bebidas
        cardCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(menu.this, interfazbebidas.class);
                startActivity(intent);
            }
        });

        // Listener para Café
        cardSnack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(menu.this, interfazcafe.class);
                startActivity(intent);
            }
        });

        // Listener para Pastelitos
        cardCake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(menu.this, interfazpasteles.class);
                startActivity(intent);
            }
        });

        // Listener para Desayunos
        cardGrocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(menu.this, interfazdesayunos.class);
                startActivity(intent);
            }
        });

        // Listener para Escanear
        cardFastFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(menu.this, escaneo.class);
                startActivity(intent);
            }
        });

        // Listener para Historial de escaneos
        cardHealthyFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(menu.this, historialproducto.class);
                startActivity(intent);
            }
        });
    }
}