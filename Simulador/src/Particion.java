import java.util.List;

public class Particion {

    private int idPar;
    private int dirIni;
    private int tamPar;
    private int idProc;
    private int fragInt;
    private boolean vacia;
    private Proceso proceso;

    void cargarProceso(Proceso procesoACargar){
        this.vacia = false;
        this.idProc = procesoACargar.getIdp();
        this.fragInt = this.tamPar-procesoACargar.getTam();
        this.proceso = procesoACargar;
    }

    void liberar(){
        this.vacia = true;
        this.idProc = 0;
        this.fragInt = 0;
        this.proceso = new Proceso();
    }

    void mostrarParticion(){
        System.out.println(this.idPar +" "+ this.dirIni + " " +  this.tamPar +"K" + " " + this.idProc+ "" +this.fragInt);
    }

    public Particion(int idPar, int dirIni, int tamPar, int idProc, int fragInt, boolean vacio, Proceso proceso) {
        this.idPar = idPar;
        this.dirIni = dirIni;
        this.tamPar = tamPar;
        this.idProc = idProc;
        this.fragInt = fragInt;
        this.vacia = vacio;
        this.proceso = proceso;
    }

    public int getIdPar() {
        return idPar;
    }

    public void setIdPar(int idPar) {
        this.idPar = idPar;
    }

    public int getDirIni() {
        return dirIni;
    }

    public void setDirIni(int dirIni) {
        this.dirIni = dirIni;
    }

    public int getTamPar() {
        return tamPar;
    }

    public void setTamPar(int tamPar) {
        this.tamPar = tamPar;
    }

    public int getIdProc() {
        return idProc;
    }

    public void setIdProc(int idProc) {
        this.idProc = idProc;
    }

    public int getFragInt() {
        return fragInt;
    }

    public void setFragInt(int fragInt) {
        this.fragInt = fragInt;
    }

    public boolean isVacio() {
        return vacia;
    }

    public void setVacio(boolean vacio) {
        this.vacia = vacio;
    }

    public Proceso getProceso() {
        return proceso;
    }

    public void setProceso(Proceso proceso) {
        this.proceso = proceso;
    }
}
