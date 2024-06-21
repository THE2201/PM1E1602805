package configuracion;

public class TransPaises {
    public static final int Version = 1;
    // Nombre de la base de datos
    public static final String DBname = "PM1E1";

    public static final String TablaPaises = "TablaPaises";

    // Propiedades
    public static final String id = "id";
    public static final String pais = "pais";
    public static final String ext = "ext";


    public static final String CreateTablaPaises = "CREATE TABLE " + TablaPaises + " ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, pais TEXT, ext TEXT )";

    public static final String SelectAllPaises = "SELECT * FROM " + TablaPaises;

    public static final String DropTablePaises = "DROP TABLE IF EXISTS " + TablaPaises;

}
