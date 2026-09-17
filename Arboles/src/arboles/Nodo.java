package arboles;

public class Nodo {
    //atributos 

    private boolean sw;
    private Nodo LigaLista;
    private Persona Dato;
    private Nodo Liga;

    //constructor
    public Nodo(Persona Dato) {
        this.sw = false;
        this.LigaLista = null;
        this.Dato = Dato;
        this.Liga = null;
    }

    //getter and setter
    public boolean isSw() {
        return sw;
    }

    public void setSw(boolean sw) {
        this.sw = sw;
    }

    public Nodo getLigaLista() {
        return LigaLista;
    }

    public void setLigaLista(Nodo LigaLista) {
        this.LigaLista = LigaLista;
    }

    public Persona getDato() {
        return Dato;
    }

    public void setDato(Persona Dato) {
        this.Dato = Dato;
    }

    public Nodo getLiga() {
        return Liga;
    }

    public void setLiga(Nodo Liga) {
        this.Liga = Liga;
    }

}
