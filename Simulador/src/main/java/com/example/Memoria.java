package com.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Memoria {
    private List<Particion> particiones = new ArrayList<>();

    public Memoria(Integer tamanioParticionSistemaOperativo) {
        Integer posicionInicio = 0;
        Integer posicionFin = tamanioParticionSistemaOperativo - 1;

        this.particiones.add(new Particion(0, tamanioParticionSistemaOperativo, posicionInicio, posicionFin));
    }

    public void crearNuevaParticion(Integer id, Integer tamanio) {

        Integer posicionInicio = particiones.get(0).getDireccionFin() + 1;
        Integer posicionFin = posicionInicio + tamanio - 1;

        particiones.add(0, new Particion(id, tamanio, posicionInicio, posicionFin));
    }

    public boolean existeParticionVacia() {

        boolean existeParticionVacia = false;
        for (Particion particion : particiones) {
            // la particion 0 es el sistema operativo, NO se puede tocar
            existeParticionVacia = (!particion.getId().equals(0) && particion.getProceso() == null) || existeParticionVacia;
        }

        return existeParticionVacia;
    }
}
