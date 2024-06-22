package com.example.pm1e1602805;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import android.os.Bundle;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.net.Uri;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import configuracion.Contactos;
import configuracion.SQLiteconexion;
import configuracion.TransContactos;

public class ListaActivity extends AppCompatActivity {

    SQLiteconexion conexion;
    SimpleAdapter adp;
    SearchView txtSearch;
    ListView listcontactos;
    GestureDetector gestureDetector;
    ArrayList<Contactos> lista;
    ArrayList<String> arreglo;
    String idC = "0";
    Contactos contactoSeleccionado;
    Button btnVer, btnEliminar, btnCompartir, btnActualizar;
    ImageButton btnRegresar;

    ArrayList<Contactos> contactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        conexion = new SQLiteconexion(this, TransContactos.DBname, null, TransContactos.Version);
        listcontactos = (ListView) findViewById(R.id.listacontactos);
        ObtenerlistContactos();

        //Regresar a pantalla inicial
        btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Compartir contacto
        btnCompartir = findViewById(R.id.btnCompartir);
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactoSeleccionado != null) {
                    String msg = "Datos del Contacto" + "\n" +
                            "Nombre: " + contactoSeleccionado.getNombre() + "\n" +
                            "Pais: " + contactoSeleccionado.getPais() + "\n" +
                            "Telefono: " + contactoSeleccionado.getTelefono();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, msg);
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, "Compartir en:"));
                } else {
                    Toast.makeText(getApplicationContext(), "Seleccione un contacto", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Eliminar contacto
        btnEliminar = findViewById(R.id.btnEliminar);
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idC == "0") {
                    Toast.makeText(getApplicationContext(), "Seleccione un contacto", Toast.LENGTH_LONG).show();
                } else {
                    EliminarMensaje();
                }
            }
        });

        //Actualizar contacto
        btnActualizar = findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idC == "0") {
                    Toast.makeText(getApplicationContext(), "Seleccione un contacto", Toast.LENGTH_LONG).show();
                } else {
                    Actualizar();
                }
            }
        });


        //Ver imagen en un AlertDialog
        btnVer = findViewById(R.id.btnVer);
        btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactoSeleccionado == null) {
                    Toast.makeText(getApplicationContext(), "Seleccione un contacto", Toast.LENGTH_LONG).show();
                } else {
                    if (contactoSeleccionado.getImagen() == null || contactoSeleccionado.getImagen().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "No hay imagen", Toast.LENGTH_LONG).show();
                    } else {
                        byte[] decodificarimage = Base64.decode(contactoSeleccionado.getImagen(), android.util.Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodificarimage, 0, decodificarimage.length);


                        //Basicamente crea un alert pero de la imagen
                        AlertDialog.Builder imagenbuilder = new AlertDialog.Builder(ListaActivity.this);
                        LayoutInflater inflater = LayoutInflater.from(ListaActivity.this);
                        View verfotocontacto = inflater.inflate(R.layout.activity_main, null);

                        ImageView imageView = verfotocontacto.findViewById(R.id.imageView);
                        imageView.setImageBitmap(bitmap);

                        imagenbuilder.setView(verfotocontacto);
                        AlertDialog imagen = imagenbuilder.create();
                        imagen.show();
                    }

                }
            }
        });

        //Realizar Llamada
        listcontactos.setOnItemClickListener(new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contactoSeleccionado= lista.get(position);
                idC= contactoSeleccionado.getId().toString();
                for(int i=0; i<parent.getChildCount(); i++){
                    parent.getChildAt(i).setBackgroundResource(com.google.android.material.R.color.m3_ref_palette_white);
                }
                view.setBackgroundResource(com.google.android.material.R.color.material_dynamic_neutral90);
            }
        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ListaActivity.this);
                builder.setTitle("¿Llamar a: " + contactoSeleccionado.getNombre() + "?")
                        .setMessage("¿Desea llamar a este contacto? +" + " " + contactoSeleccionado.getPais() + " " + contactoSeleccionado.getTelefono())
                        .setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String tel = contactoSeleccionado.getTelefono();
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + tel));

                                if (ActivityCompat.checkSelfPermission(ListaActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(ListaActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, 1);
                                } else {
                                    startActivity(intent);
                                }

                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
                return true;
            }

        });

        listcontactos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });


    }

    private void ObtenerlistContactos() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Contactos contactos = null;
        lista = new ArrayList<Contactos>();

        Cursor cursor = db.rawQuery(TransContactos.SelectAllContacts, null);

        List<HashMap<String, String>> items = new ArrayList<>();
        adp = new SimpleAdapter(this, items, R.layout.listcontact_items,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.txtItems, R.id.txtSubItems});


        while (cursor.moveToNext()) {

            HashMap<String, String> result = new HashMap<>();
            result.put("First Line", cursor.getString(2));
            result.put("Second Line", cursor.getString(3));
            items.add(result);

            contactos = new Contactos();
            contactos.setId(cursor.getInt(0));
            contactos.setPais(cursor.getString(1));
            contactos.setNombre(cursor.getString(2));
            contactos.setTelefono(cursor.getString(3));
            contactos.setNota(cursor.getString(4));
            contactos.setImagen(cursor.getString(5));
            lista.add(contactos);
        }
        listcontactos.setAdapter(adp);

    }




    private void Eliminar() {
        SQLiteconexion conexion = new SQLiteconexion(this, TransContactos.DBname, null, TransContactos.Version);
        SQLiteDatabase db = conexion.getWritableDatabase();

        int rowDelete = db.delete(TransContactos.TablaContactos, "id=?", new String[]{idC});
        Toast.makeText(getApplicationContext(), "Registro eliminado con exito", Toast.LENGTH_LONG).show();
        db.close();
    }

    private void Actualizar() {
        Intent intent = new Intent(ListaActivity.this, MainActivity.class);

        intent.putExtra("id", contactoSeleccionado.getId().toString());
        intent.putExtra("pais", contactoSeleccionado.getPais());
        intent.putExtra("nombre", contactoSeleccionado.getNombre());
        intent.putExtra("telefono", contactoSeleccionado.getTelefono());
        intent.putExtra("nota", contactoSeleccionado.getNota());
        intent.putExtra("imagen", contactoSeleccionado.getImagen());
        startActivity(intent);
    }

    private void EliminarMensaje() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea eliminar este contacto?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Eliminar();
                        ObtenerlistContactos();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
    }

}