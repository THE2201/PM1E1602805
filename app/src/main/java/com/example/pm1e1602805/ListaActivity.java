package com.example.pm1e1602805;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import configuracion.Contactos;
import configuracion.SQLiteconexion;
import configuracion.TransContactos;

public class ListaActivity extends AppCompatActivity {

    SQLiteconexion conexion;
    SimpleAdapter adp;

    ListView listcontactos;

    ArrayList<Contactos> lista;

    String idC = "0";
    Contactos contactoSeleccionado;
    Button btnRegresar, btnVer,btnEliminar,btnCompartir,btnActualizar;

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

        conexion = new  SQLiteconexion(this, TransContactos.DBname, null, TransContactos.Version);
        listcontactos = (ListView) findViewById(R.id.listacontactos);

        btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaActivity.this, MainActivity.class);
                startActivity(intent);
            }
            });

        btnVer = findViewById(R.id.btnVer);
        btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           if(contactoSeleccionado==null){
               Toast.makeText(getApplicationContext(),"Seleccione un contacto",Toast.LENGTH_LONG).show();
           }
           else{
               if (contactoSeleccionado.getImagen()==null || contactoSeleccionado.getImagen().isEmpty()){
                   Toast.makeText(getApplicationContext(),"No hay imagen",Toast.LENGTH_LONG).show();
               }
               else{
                   byte[] decodificarimage = Base64.decode(contactoSeleccionado.getImagen(), android.util.Base64.DEFAULT);
                   Bitmap bitmap = BitmapFactory.decodeByteArray(decodificarimage, 0, decodificarimage.length);

                   AlertDialog.Builder imagenbuilder = new AlertDialog.Builder(ListaActivity.this);

               }
           }

            }
        });

    }

    private void ObtenerlistContactos(){
        SQLiteDatabase db=conexion.getReadableDatabase();
        Contactos contactos=null;
        lista= new ArrayList<Contactos>();

        Cursor cursor=db.rawQuery(TransContactos. SelectAllContacts,null);

        while(cursor.moveToNext()){
            contactos= new Contactos();
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

    private void Eliminar(){
        SQLiteconexion conexion= new SQLiteconexion(this, TransContactos.DBname, null, TransContactos.Version);
        SQLiteDatabase db = conexion.getWritableDatabase();

        int rowDelete = db.delete(TransContactos.TablaContactos, "id=?", new String[] {idC});
        Toast.makeText(getApplicationContext(),"Registro eliminado con exito",Toast.LENGTH_LONG).show();
        db.close();
    }

    private void Actualizar(){
        Intent intent = new Intent(ListaActivity.this, MainActivity.class);

        intent.putExtra("id", contactoSeleccionado.getId().toString());
        intent.putExtra("pais", contactoSeleccionado.getPais());
        intent.putExtra("nombre", contactoSeleccionado.getNombre());
        intent.putExtra("telefono", contactoSeleccionado.getTelefono());
        intent.putExtra("nota", contactoSeleccionado.getNota());
        intent.putExtra("imagen", contactoSeleccionado.getImagen());
        startActivity(intent);
    }

    private void EliminarMensaje(){
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