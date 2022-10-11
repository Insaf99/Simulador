package com.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CPU {

    private Proceso proceso = null;
    private int TIactual = 1;

    public void aumentarTIactual(){
        this.TIactual +=1;
    }
    public void terminarProceso(){
        System.out.println("Proceso "+this.proceso.getIdp()+" finalizado");
        this.proceso = null;
        this.TIactual = 1;
    }
    public void mostrarCPU(){
        if (!this.proceso.equals(null)){
            System.out.println("-- Proceso en la CPU --");
            System.out.println("");
            System.out.println(" --Proceso-- | --TI-- | --Tejec-- ");
            System.out.println(" --"+this.proceso.getIdp()+"-- | --"+ this.proceso.getTi()+"-- | --"+this.TIactual);
        }else{
            System.out.println("Procesador Vacio");
        }
    }
    public void ejecutarProceso(){
        System.out.println("");
        System.out.println("Proceso : "+this.proceso.getIdp()+" se esta ejecutando");
        System.out.println("");
    }

}