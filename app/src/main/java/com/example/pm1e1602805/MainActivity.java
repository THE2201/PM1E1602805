package com.example.pm1e1602805;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import configuracion.Paises;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    EditText nombre, telefono, nota;

    Spinner SpinnerPaises;
    Button btnAgregarPais, btnGuardar, btnLista, btnCapturar;

    Integer extPais;

    ArrayList<Paises> Paises;
    ArrayList<String> listaPaises;



    //Permisos
    static final int peticionFoto = 101;
    static final int peticionCamara = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        SpinnerPaises = findViewById(R.id.spinnerPaises);
        nombre = findViewById(R.id.nombre);
        telefono = findViewById(R.id.telefono);
        nota = findViewById(R.id.nota);

        btnCapturar = findViewById(R.id.btnCapturar);
        btnAgregarPais = findViewById(R.id.btnAgregarPais);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnLista = findViewById(R.id.btnLista);




    }
}