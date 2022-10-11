package com.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

}
