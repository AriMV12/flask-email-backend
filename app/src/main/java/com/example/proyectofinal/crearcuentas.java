package com.example.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class crearcuentas extends AppCompatActivity {

    EditText emailEditText, passwordEditText, confirmEditText;
    Button loginButton;
    TextView regresarTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crearcuentas);

        // Inicializamos las vistas
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmEditText = findViewById(R.id.confirmEditText);
        loginButton = findViewById(R.id.loginButton);
        regresarTextView = findViewById(R.id.regresarTextView);

        // Ocultar íconos al escribir
        setupEditTextBehavior(emailEditText, R.drawable.iconocorreo);
        setupEditTextBehavior(passwordEditText, R.drawable.iconopassword);
        setupEditTextBehavior(confirmEditText, R.drawable.iconoconfirm);

        // Evento de registrar
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirm = confirmEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirm)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            } else {
                // Guardamos la cuenta localmente
                SharedPreferences preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();

                Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();

                // Ir a la pantalla de inicio de sesión
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Evento de volver al inicio de sesión
        regresarTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
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
                    editText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                } else {
                    editText.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
