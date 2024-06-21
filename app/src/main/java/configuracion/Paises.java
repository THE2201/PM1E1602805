package configuracion;

public class Paises {

    // Propiedades
    private Integer id;
    private String pais;
    private String ext;

    public Paises(Integer id, String pais, String ext) {
        this.id = id;
        this.pais = pais;
        this.ext = ext;
    }

    public Paises() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
