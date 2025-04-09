package com.example.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    TextView crearCuentaTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Asegúrate que sea el nombre correcto del layout

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        crearCuentaTextView = findViewById(R.id.crearCuentaTextView);

        // Ocultar íconos al escribir
        setupEditTextBehavior(emailEditText, R.drawable.iconocorreo);
        setupEditTextBehavior(passwordEditText, R.drawable.iconopassword);

        loginButton.setOnClickListener(v -> {
            String inputEmail = emailEditText.getText().toString().trim();
            String inputPassword = passwordEditText.getText().toString().trim();

            SharedPreferences preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
            String savedEmail = preferences.getString("email", null);
            String savedPassword = preferences.getString("password", null);

            if (savedEmail == null || savedPassword == null) {
                Toast.makeText(this, "No hay cuenta registrada. Crea una cuenta primero.", Toast.LENGTH_SHORT).show();
            } else if (inputEmail.equals(savedEmail) && inputPassword.equals(savedPassword)) {
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, menu.class));
            } else {
                Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        crearCuentaTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, crearcuentas.class);
            startActivity(intent);
        });
    }

    private void setupEditTextBehavior(EditText editText, int iconResId) {
        // Mostrar el ícono por defecto
        editText.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    // Quitar ícono al escribir
                    editText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                } else {
                    // Mostrar ícono si está vacío
                    editText.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
