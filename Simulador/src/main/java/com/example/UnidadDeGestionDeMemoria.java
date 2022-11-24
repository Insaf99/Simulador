package com.example;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

@Data
@NoArgsConstructor
public class UnidadDeGestionDeMemoria {

    public void aniadirProcesoACola(List<Proceso> colaIngresada, Proceso proceso) {

        colaIngresada.add(proceso);
    }

    public void aniadirProcesosACola(List<Proceso> colaIngresada, List<Proceso> procesosIngresados) {

        colaIngresada.addAll(procesosIngresados);
    }

    public void eliminarProcesosDeCola(List<Proceso> colaIngresada, List<Proceso> procesosAEliminar) {

        colaIngresada.removeAll(procesosAEliminar);
    }

    public Proceso obtenerProcesoDeCola(List<Proceso> colaIngresada, Integer indice) {

        return colaIngresada.get(indice);
    }

    public Proceso eliminarProcesoDeCola(List<Proceso> colaIngresada, int indice) {

        return colaIngresada.remove(indice);
    }

    // ordena la cola recibida por parámetro, por tiempo de arribo de menor a mayor.
    public void ordenarColaPorTiempoDeArribo(List<Proceso> procesos) {
        procesos.sort(Comparator.comparing(p -> p.getTiempoArribo()));
    }

    // ordena la cola recibida por tiempo de irrupcion de menor a mayor.
    public void ordenarColaPorTiempoDeIrrupcion(List<Proceso> procesos) {
        procesos.sort(Comparator.comparing(p -> p.getTiempoIrrupcion()));
    }
}
