package com.example.pm1e1602805;

import android.Manifest;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import configuracion.Paises;
import configuracion.SQLiteconexion;
import configuracion.TransContactos;
import configuracion.TransPaises;

public class MainActivity extends AppCompatActivity {
    
    static final int peticion_camara = 100;
    static final int  peticion_foto = 101;
    EditText nombre, telefono, nota;
    Spinner spinnerPais;
    Integer extPais;
    Button btnLista, btnGuardar,btnCapturar, btnAgregarPais;
    String extPaisSel, img64, idContacto;
    ImageView imageView;
    ArrayList<Paises> list;
    ArrayList<String> listPaises;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombre = (EditText) findViewById(R.id.nombre);
        telefono = (EditText) findViewById(R.id.telefono);
        spinnerPais = (Spinner) findViewById(R.id.spinnerPaises);
        nota = (EditText) findViewById(R.id.nota);
        imageView = (ImageView) findViewById(R.id.imageView);


        
        Intent intent = getIntent();

        if(intent.getExtras() != null){
            idContacto = intent.getStringExtra("Id");
            extPaisSel = intent.getStringExtra("Pais");
            String valName = intent.getStringExtra("Nombre");
            String valPhone = intent.getStringExtra("Telefono");
            String valNote = intent.getStringExtra("Nota");
            String img64 = intent.getStringExtra("Imagen");

            nombre.setText(valName);
            telefono.setText(String.valueOf(valPhone));
            nota.setText(valNote);
            if(img64 != null){
                VerImagen(img64);
            }
        }


        btnLista = (Button) findViewById(R.id.btnLista);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnAgregarPais = (Button) findViewById(R.id.btnAgregarPais);
        btnCapturar = (Button)  findViewById(R.id.btnCapturar);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent.getExtras() != null){
                    EditarContacto();
                }else{
                    AgregarContacto();
                }
            }
        });

        btnLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent send = new Intent(MainActivity.this, ListaActivity.class);
                startActivity(send);
            }
        });

        btnAgregarPais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderPais = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View viewPais = inflater.inflate(R.layout.dialogo_paises, null);

                builderPais.setView(viewPais);
                AlertDialog dialogPais = builderPais.create();
                dialogPais.show();

                EditText txtCodPais = viewPais.findViewById(R.id.txtCodPais);
                EditText txtPais = viewPais.findViewById(R.id.txtPais);

                viewPais.findViewById(R.id.btnGuardarPais).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String txtCodPaisVal = txtCodPais.getText().toString();
                        String txtPaisVal = txtPais.getText().toString();

                        if(txtCodPaisVal.isEmpty() || txtPaisVal.isEmpty()){
                            Toast.makeText(getApplicationContext(), "*Campos Obligatorios", Toast.LENGTH_SHORT).show();
                        }else{
                            AgregarPais(txtCodPaisVal,txtPaisVal);
                            llenarCombo();
                            // Toast.makeText(getApplicationContext(), "Pais guardado con exito!", Toast.LENGTH_SHORT).show();
                            dialogPais.dismiss();
                        }
                    }
                });

                viewPais.findViewById(R.id.btnCancelarPais).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogPais.dismiss();
                    }
                });
            }
        });
        
        
        btnCapturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObtenerPermisos();
            }
        });

        //**--------------------ComboBox Paises----------------------**//
        llenarCombo();
        spinnerPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try
                {
                    extPais = list.get(position).getId();
                }
                catch (Exception ex)
                {
                    Log.i("Error", ex.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ///********  Fin Combo Paises------------------*****************////////////
    }

    private void VerImagen(String img) {
        byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView.setImageBitmap(bitmap);
    }

    private void EditarContacto(){
        Pattern p = Pattern.compile("[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]");
        if(nombre.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Nombre vacio", Toast.LENGTH_SHORT).show();
        }
        else if(telefono.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Telefono vacio", Toast.LENGTH_SHORT).show();
        }else if(nota.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Nota vacia", Toast.LENGTH_SHORT).show();
        }else if(!p.matcher(telefono.getText().toString()).matches()){
            Toast.makeText(getApplicationContext(), "Formato no valido de telefono", Toast.LENGTH_SHORT).show();
        }
        else{
            SQLiteconexion conexion = new SQLiteconexion(this, TransContactos.DBname, null, TransContactos.Version);
            SQLiteDatabase db = conexion.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put(TransContactos.pais, extPais);
            valores.put(TransContactos.nombre, nombre.getText().toString());
            valores.put(TransContactos.telefono, telefono.getText().toString());
            valores.put(TransContactos.nota, nota.getText().toString());
            valores.put(TransContactos.imagen, img64);
            long resultado = db.update(TransContactos.TablaContactos, valores, TransContactos.id+"=?", new String[]{idContacto});
            Toast.makeText(getApplicationContext(), "Exito", Toast.LENGTH_SHORT).show();
            db.close();
        }
    }
    private void llenarCombo(){
        ObtenerPaises();
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, listPaises);

        spinnerPais.setAdapter(adapter);

        int position = getPositionByCode(listPaises, extPaisSel);

        if(position != -1){
            spinnerPais.setSelection(position);
        }
    }

    private void ClearTexts(){
        nombre.setText(""); telefono.setText(""); nota.setText("");
    }

    private int getPositionByCode(List<String> list, String code) {
        for (int i = 0; i < list.size(); i++) {
            String item = list.get(i);
            String[] parts = item.split(" - ");
            if (parts.length == 2 && parts[1].equals(code)) {
                return i;
            }
        }
        return -1;
    }

    private void ObtenerPaises()
    {
        SQLiteconexion conexion = new SQLiteconexion(this, TransPaises.DBname, null, TransPaises.Version);
        SQLiteDatabase db = conexion.getReadableDatabase();
        Paises paises = null;
        list = new ArrayList<Paises>();

        // Cursor de base de datos para recorrer los datos
        Cursor cursor = db.rawQuery(TransPaises.SelectAllPaises, null);

        while (cursor.moveToNext())
        {
            paises = new Paises();
            paises.setId(cursor.getInt(0));
            paises.setPais(cursor.getString(2));
            paises.setExt(cursor.getString(1));

            list.add(paises);
        }
        cursor.close();

        FillData();
    }

    private void FillData()
    {
        listPaises = new ArrayList<String>();
        for(int i = 0; i < list.size(); i ++)
        {
            listPaises.add(list.get(i).getPais()  +" - "+ list.get(i).getExt()) ;
        }
    }
    private void AgregarContacto()
    {
        Pattern p = Pattern.compile("[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]");
        if(nombre.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "¡Es obligatorio el campo nombre!", Toast.LENGTH_SHORT).show();
        }
        else if(telefono.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "¡Es obligatorio el campo telefono!", Toast.LENGTH_SHORT).show();
        }else if(nota.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "¡Es obligatorio el campo nota!", Toast.LENGTH_SHORT).show();
        }else if(!p.matcher(telefono.getText().toString()).matches()){
            Toast.makeText(getApplicationContext(), "¡Formato invalido para campo telefono!", Toast.LENGTH_SHORT).show();
        } else if (extPais == null) {
            Toast.makeText(getApplicationContext(), "¡Debe registrar un pais para registrar un contacto!", Toast.LENGTH_SHORT).show();
        } else{
            SQLiteconexion conexion = new SQLiteconexion(this, TransContactos.DBname, null, TransContactos.Version);
            SQLiteDatabase db = conexion.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put(TransContactos.pais, extPais);
            valores.put(TransContactos.nombre, nombre.getText().toString());
            valores.put(TransContactos.telefono, telefono.getText().toString());
            valores.put(TransContactos.nota, nota.getText().toString());
            valores.put(TransContactos.imagen, img64);

            long resultado = db.insert(TransContactos.TablaContactos, TransContactos.id, valores);
            Toast.makeText(getApplicationContext(), "Exito" + resultado,  Toast.LENGTH_SHORT).show();
            db.close();
            ClearTexts();
        }

    }
    private void AgregarPais(String codigo, String nombre)
    {
        SQLiteconexion conexion = new SQLiteconexion(this, TransPaises.DBname, null, TransPaises.Version);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(TransPaises.pais, nombre);
        valores.put(TransPaises.ext, codigo);

        long resultado = db.insert(TransPaises.TablaPaises, TransPaises.id, valores);
        Toast.makeText(getApplicationContext(), "Exito" + resultado, Toast.LENGTH_SHORT).show();

        db.close();
    }
    private void ObtenerPermisos(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, peticion_camara);
        }
        else{
            CapturarFoto();
        }
    }
    private void CapturarFoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, peticion_foto);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
    int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == peticion_camara){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                CapturarFoto();
            }else{
                Toast.makeText(getApplicationContext(), "Otorgue permisos a camara", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == peticion_foto && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imagen = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imagen);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imagen.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            img64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
    }
}