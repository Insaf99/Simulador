package com.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Particion {
    private Integer id;
    private Integer tamanio;
    private Integer direccionInicio;
    private Integer direccionFin;
    private Integer fragmentacionInterna;
    private Proceso proceso;

    public Particion(Integer id, Integer tamanio, Integer direccionInicio, Integer direccionFin) {
        this.id = id;
        this.tamanio = tamanio;
        this.direccionInicio = direccionInicio;
        this.direccionFin = direccionFin;
        this.fragmentacionInterna = 0;
        this.proceso = null;
    }

    public void vaciarParticion() {
        this.setProceso(null);
    }

    public void setProceso(Proceso proceso) {

        this.proceso = proceso;

        if (this.proceso != null) {
            this.proceso.setParticion(this);
        }

        this.calcularFragmentacionInterna();
    }

    private void calcularFragmentacionInterna() {
        if (this.proceso != null) {
            this.fragmentacionInterna = this.tamanio - this.proceso.getTamanio();
        } else {
            this.fragmentacionInterna = 0;
        }
    }
}
