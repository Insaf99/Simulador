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
    private static Integer idUltimoProceso = 0;
    private Integer id;
    private Integer tamanio;
    private Integer direccionInicio;
    private Integer direccionFin;
    private Integer fragmentacionInterna;
    private Proceso proceso;

    public Particion(Integer tamanio, Integer direccionInicio, Integer direccionFin) {
        this.id = idUltimoProceso;
        idUltimoProceso++;
        this.tamanio = tamanio;
        this.direccionInicio = direccionInicio;
        this.direccionFin = direccionFin;
        this.proceso = null;
        this.fragmentacionInterna = 0;
    }

    public void vaciarParticion() {
        this.setProceso(null);
    }

    public Boolean isEmpty() {
        return this.proceso == null;
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
