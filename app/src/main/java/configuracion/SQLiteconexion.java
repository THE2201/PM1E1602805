package configuracion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteconexion   extends  SQLiteOpenHelper {

    public SQLiteconexion(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TransContactos.CreateTablaContactos);
        db.execSQL(TransPaises.CreateTablaPaises);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TransContactos.DropTableContacts);
        db.execSQL(TransPaises.DropTablePaises);
        onCreate(db);

    }
}