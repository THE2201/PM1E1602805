package configuracion;

public class TransContactos {
    public static final int Version = 1;
    // Nombre de la base de datos
    public static final String DBname = "PM1E1";

    public static final String TablaContactos = "contactos";

    // Propiedades
    public static final String id = "id";
    public static final String pais = "pais";
    public static final String nombre = "nombre";
    public static final String telefono = "telefono";
    public static final String nota = "nota";
    public static final String imagen = "imagen";

    public static final String CreateTablaContactos = "CREATE TABLE " + TablaContactos + " ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, pais TEXT, nombre TEXT, telefono TEXT, nota TEXT, imagen TEXT )";

    public static final String SelectAllContacts = "SELECT * FROM " + TablaContactos;

    public static final String DropTableContacts = "DROP TABLE IF EXISTS " + TablaContactos;

}
