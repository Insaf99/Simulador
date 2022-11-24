package com.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.example.Cpu.instanteActual;
import static com.example.Main.imprimirEstadoDelSistema;

@Data
@Builder
@AllArgsConstructor
public class SistemaOperativo {

    private static Integer tamanioParticionSO = 100;
    private Memoria memoria;
    private Cpu cpu;

    private List<Proceso> colaDeProcesosListos = new ArrayList<>();
    private List<Proceso> colaDeProcesosTerminados = new ArrayList<>();
    private List<Proceso> colaDeProcesosNuevos = new ArrayList<>();
    private List<Proceso> colaDeProcesosListosSuspendidos = new ArrayList<>();

    public SistemaOperativo(List<Proceso> procesos) {

        // inicializamos la Memoria con las Particiones Indicadas, la Cpu y se cargan los procesos
        this.inicializarMemoria();
        this.inicializarCpu();
        this.cargarProcesos(procesos);

        // imprimo el estado inicial del sistema, con las particiones y la cpu en vacio, pero con los procesos cargados
        imprimirEstadoDelSistema(this);

        // aca empieza la ejecucion
        this.ejecutarProcesos();
    }

    public void ejecutarProcesos() {

        // mientras existan procesos en alguna de las colas o en la cpu, significa que todavia quedan procesos por ejecutar
        while (!colaDeProcesosNuevos.isEmpty() || !colaDeProcesosListosSuspendidos.isEmpty() || !colaDeProcesosListos.isEmpty() || !(cpu.getProcesoEnEjecucion() == null)) {
            planificadorLargoPlazo();
            planificadorMedianoPlazo();
            planificadorCortoPlazo();
        }
    }

    // el planificar de largo plazo mueve los procesos de la cola de Nuevos a Listos Suspendidos dependiendo del tiempo
    // de arribo, o sea, si el proceso "llego" o no. Si los muevo, los elimino de la cola de nuevos
    private void planificadorLargoPlazo() {
        for (Proceso proceso : colaDeProcesosNuevos) {
            if (proceso.getTiempoArribo().equals(instanteActual)) {
                colaDeProcesosListosSuspendidos.add(proceso);
            }
        }
        colaDeProcesosNuevos.removeAll(colaDeProcesosListosSuspendidos);
    }

    // el planificador de mediano plazo mueve los procesos de la cola de listos-suspendidos a la cola de listos si existen
    // particiones libres, y buscara la que genere MAYOR fragmentacion interna (Worst Fit)
    private void planificadorMedianoPlazo() {

        // ordeno la cola de procesos listos-suspendidos por tiempo de irrupcion (de menor a mayor, SJF)
        ordenarColaPorTiempoDeIrrupcion(colaDeProcesosListosSuspendidos);

        // pregunto existen particiones vacias, si las hay, puedo asignarle un proceso
        if (memoria.existeParticionVacia()) {

            boolean flag = false;
            List<Particion> particiones = this.memoria.getParticiones();
            int ixLyS = 0;
            List<Proceso> tmp = new ArrayList<>();
            while (memoria.existeParticionVacia() && colaDeProcesosListosSuspendidos.size() > ixLyS) {
                Proceso p = colaDeProcesosListosSuspendidos.get(ixLyS);
                // ACA ESTA EL ALGORITMO WORST FIT
                Integer minDif = Integer.MIN_VALUE;
                int partIX = -1;
                for (Particion part : particiones) {
                    // la particion con id 0 es el sistema operativo, NO se puede tocar
                    if (!part.getId().equals(0) && part.isEmpty()) {
                        if ((part.getTamanio() >= p.getTamanio()) && (part.getTamanio() - p.getTamanio() > minDif)) {
                            minDif = part.getTamanio() - p.getTamanio();
                            partIX = particiones.indexOf(part);
                        }
                    }
                }
                if (partIX >= 0) {
                    particiones.get(partIX).setProceso(p);
                    tmp.add(p);
                    flag = true;
                }
                ixLyS++;
            }
            if (flag) {
                colaDeProcesosListos.addAll(tmp);
                colaDeProcesosListosSuspendidos.removeAll(tmp);
            } else {
                VerificarCpuListos();
            }
        } else {
            // Todas las particiones están llenas
            VerificarCpuListos();
        }
    }


    public void VerificarCpuListos() {
        if (colaDeProcesosListosSuspendidos.size() > 0) {
            Proceso plYs = colaDeProcesosListosSuspendidos.get(0);
            /*
             * Compara el tiempo de irrupción del procesoEnEjecucion que quiere entrar con el que se encuentra en CPU. Si es menor procede a buscar una partición
             */
            if (null != cpu.getProcesoEnEjecucion() && plYs.getTiempoIrrupcion() < cpu.getProcesoEnEjecucion().getTiempoIrrupcion()) {
                ordenarColaPorTiempoDeIrrupcion(colaDeProcesosListos);
                Integer n = colaDeProcesosListos.size() - 1;
                Integer nEncontrado = -1;
                /*
                 * Busca una posicion de memoria en la cola de LISTOS
                 */
                while (n > -1 && nEncontrado < 0) {
                    Proceso pn = colaDeProcesosListos.get(n);
                    if (plYs.getTamanio() <= pn.getParticion().getTamanio()) {
                        nEncontrado = n;
                    }
                    n--;
                }
                /*
                 * Se encontró una particion en la cola de LISTOS y hace la expropiación del procesoEnEjecucion que esta en dicha partición
                 */
                if (nEncontrado > -1) {
                    Proceso saliente = colaDeProcesosListos.remove((int) nEncontrado);
                    Particion part = saliente.getParticion();
                    saliente.getParticion().vaciarParticion();
                    colaDeProcesosListosSuspendidos.add(saliente);
                    part.setProceso(plYs);
                    colaDeProcesosListosSuspendidos.remove(plYs);
                    colaDeProcesosListos.add(plYs);
                } else {
                    /*
                     * si no encuentró comprueba que la partición del procesoEnEjecucion que está en CPU sea igual o mayor al tamaño que requiere el procesoEnEjecucion
                     */

                    if (plYs.getTamanio() <= cpu.getProcesoEnEjecucion().getParticion().getTamanio()) {
                        Proceso saliente = cpu.getProcesoEnEjecucion();
                        cpu.setProcesoEnEjecucion(null);
                        Particion part = saliente.getParticion();
                        saliente.getParticion().vaciarParticion();
                        colaDeProcesosListosSuspendidos.add(saliente);
                        part.setProceso(plYs);
                        colaDeProcesosListosSuspendidos.remove(plYs);
                        colaDeProcesosListos.add(plYs);
                    }

                }
            } else {
                /*
                 * Busca una posición de memoria en la cola de LISTOS comparando tiempo de irrupción y tamaño de partición
                 */
                ordenarColaPorTiempoDeIrrupcion(colaDeProcesosListos);
                Integer n = colaDeProcesosListos.size() - 1;
                Integer nEncontrado = -1;
                while (n > -1 && nEncontrado < 0) {
                    Proceso pp = colaDeProcesosListos.get(n);
                    if (plYs.getTiempoIrrupcion() < pp.getTiempoIrrupcion()) {
                        if (plYs.getTamanio() <= pp.getParticion().getTamanio()) {
                            nEncontrado = n;
                        }
                    }
                    n--;
                }
                /*
                 * Se encontró una particion en la cola de LISTOS y hace la expropiación del procesoEnEjecucion que esta en dicha partición
                 */
                if (nEncontrado > -1) {
                    Proceso saliente = colaDeProcesosListos.remove((int) nEncontrado);
                    Particion part = saliente.getParticion();
                    saliente.getParticion().vaciarParticion();
                    colaDeProcesosListosSuspendidos.add(saliente);
                    part.setProceso(plYs);
                    colaDeProcesosListosSuspendidos.remove(plYs);
                    colaDeProcesosListos.add(plYs);
                }
            }
        }
    }

    /*
     * Selecciona el procesoEnEjecucion a ejecutarse aplicando SJF. (Comparando los Tiempos de Irrupción).
     */

    public void planificadorCortoPlazo() {
        ordenarColaPorTiempoDeIrrupcion(colaDeProcesosListos);
        this.terminarProceso();
        if (colaDeProcesosListos.size() > 0) {
            Proceso victima = colaDeProcesosListos.remove((int) 0);
            if (cpu.getProcesoEnEjecucion() == null) {
                // El procesador está Vacio
                cpu.setProcesoEnEjecucion(victima);
            } else {
                // procesador tiene algún procesoEnEjecucion
                colaDeProcesosListos.add(victima);
                //}
            }
        }
        cpu.incrementarInstante();
    }

    /*
     * Termina el procesoEnEjecucion actual (TI=0) moviéndolo a la cola SALIENTES y liberando la partición que ocupaba.
     */

    public void terminarProceso() {
        if (null != cpu.getProcesoEnEjecucion()) {
            if (cpu.getProcesoEnEjecucion().getTiempoIrrupcion() == 0) {
                imprimirEstadoDelSistema(this);
                colaDeProcesosTerminados.add(cpu.getProcesoEnEjecucion());
                cpu.getProcesoEnEjecucion().getParticion().setProceso(null);
                cpu.getProcesoEnEjecucion().getParticion().vaciarParticion();
                cpu.setProcesoEnEjecucion(null);
                planificadorMedianoPlazo();
                ordenarColaPorTiempoDeIrrupcion(colaDeProcesosListos);
            }
        }
    }

    /*
     * Crea las particiones fijas en memoria
     */
    private void inicializarMemoria() {
        this.memoria = new Memoria(tamanioParticionSO);
        this.memoria.crearNuevaParticion(250);
        this.memoria.crearNuevaParticion(120);
        this.memoria.crearNuevaParticion(60);
    }

    private void inicializarCpu() {
        this.cpu = new Cpu();
    }

    private void cargarProcesos(List<Proceso> procesos) {
        ordenarColaPorTiempoDeArribo(procesos);
        this.colaDeProcesosNuevos = procesos;
    }

    // ordena la cola recibida por parámetro, por tiempo de arribo de menor a mayor.
    private void ordenarColaPorTiempoDeArribo(List<Proceso> procesos) {
        procesos.sort(Comparator.comparing(p -> p.getTiempoArribo()));
    }

    // ordena la cola recibida por tiempo de irrupcion de menor a mayor.
    private void ordenarColaPorTiempoDeIrrupcion(List<Proceso> procesos) {
        procesos.sort(Comparator.comparing(p -> p.getTiempoIrrupcion()));
    }
}