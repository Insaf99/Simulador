package com.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class Proceso {
    private Integer id;
    private Integer tamanio;
    private Integer tiempoArribo;
    private Integer tiempoIrrupcion;
    private Particion particion;

    public Proceso(List<String> lineaDeProceso) {
        this.id = Integer.parseInt(lineaDeProceso.get(0));
        this.tamanio = Integer.parseInt(lineaDeProceso.get(1));
        this.tiempoArribo = Integer.parseInt(lineaDeProceso.get(2));
        this.tiempoIrrupcion = Integer.parseInt(lineaDeProceso.get(3));
    }

    public void decrementarTiempoIrrupcion() {
        this.tiempoIrrupcion--;
    }
}