package com.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Cpu {

    private Proceso procesoEnEjecucion;
    public static Integer instanteActual;

    // empieza el instanteActual en cero
    public Cpu() {
        instanteActual = 0;
    }

    // se incrementa el instante y se reduce el tiempo de irrupcion del proceso en ejecucion
    public void incrementarInstante() {
        instanteActual++;

        if (this.procesoEnEjecucion != null) {
            this.procesoEnEjecucion.decrementarTiempoIrrupcion();
        }
    }
}