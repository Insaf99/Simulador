public class Proceso {

    private int tr = 0;
    private int tejec = 0;
    private int te = 0;
    private int idp;
    private int ta;
    private int ti;
    private int tam;

    public Proceso(int tr, int tejec, int te, int idp, int ta, int ti, int tam) {
        this.tr = tr;
        this.tejec = tejec;
        this.te = te;
        this.idp = idp;
        this.ta = ta;
        this.ti = ti;
        this.tam = tam;
    }

    public Proceso() {
    }

    public Proceso(int idp, int ta, int ti, int tam) {
        this.idp = idp;
        this.ta = ta;
        this.ti = ti;
        this.tam = tam;
    }

    public int getTr() {
        return tr;
    }

    public void setTr(int tr) {
        this.tr = tr;
    }

    public int getTejec() {
        return tejec;
    }

    public void setTejec(int tejec) {
        this.tejec = tejec;
    }

    public int getTe() {
        return te;
    }

    public void setTe(int te) {
        this.te = te;
    }

    public int getIdp() {
        return idp;
    }

    public void setIdp(int idp) {
        this.idp = idp;
    }

    public int getTa() {
        return ta;
    }

    public void setTa(int ta) {
        this.ta = ta;
    }

    public int getTi() {
        return ti;
    }

    public void setTi(int ti) {
        this.ti = ti;
    }
    public int getTam() {
        return tam;
    }

    public void setTam(int tam) {
        this.tam = tam;
    }

    public void aumentarTE(){
        this.te += 1;
    }

    public void aumentarTejec(){
        this.tejec += 1;
    }
}
