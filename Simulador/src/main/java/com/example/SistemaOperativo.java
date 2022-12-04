package com.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.Cpu.instanteActual;
import static com.example.Main.imprimirEstadoDelSistema;

@Data
@Builder
@AllArgsConstructor
public class SistemaOperativo {

    private Memoria memoria;
    private Cpu cpu;
    private Integer nivelDeMultiprogramacion = 0;
    private UnidadDeGestionDeMemoria unidadDeGestionDeMemoria;

    private List<Proceso> colaDeProcesosListos = new ArrayList<>();
    private List<Proceso> colaDeProcesosTerminados = new ArrayList<>();
    private List<Proceso> colaDeProcesosNuevos = new ArrayList<>();
    private List<Proceso> colaDeProcesosListosSuspendidos = new ArrayList<>();

    public SistemaOperativo(List<Proceso> procesos) {

        // inicializamos la Memoria con las Particiones Indicadas, la Cpu, la MMU y se cargan los procesos
        this.inicializarMemoria();
        this.inicializarCpu();
        this.inicializarUnidadDeGestionDeMemoria();
        this.cargarProcesos(procesos);

        // imprimo el estado inicial del sistema, con las particiones y la cpu en vacio, pero con los procesos cargados
        imprimirEstadoDelSistema(this);

        // aca empieza la ejecucion
        this.ejecutarProcesos();
    }

    private void inicializarMemoria() {
        // creo la memoria con la particion inicial por defecto para el Sistema Operativo de 100kb
        this.memoria = new Memoria(100);

        // creo las particiones extras necesarias
        this.memoria.crearNuevaParticion(1, 250);
        this.memoria.crearNuevaParticion(2, 120);
        this.memoria.crearNuevaParticion(3, 60);
    }

    private void inicializarCpu() {
        // inicializo la CPU
        this.cpu = new Cpu();
    }

    private void inicializarUnidadDeGestionDeMemoria() {
        // inicializo la MMU
        this.unidadDeGestionDeMemoria = new UnidadDeGestionDeMemoria();
    }

    private void cargarProcesos(List<Proceso> procesos) {

        // ordeno los procesos que llegaron en el archivo por tiempo de arribo y los cargo en la cola de nuevos a travez
        // de la MMU
        unidadDeGestionDeMemoria.ordenarColaPorTiempoDeArribo(procesos);
        unidadDeGestionDeMemoria.aniadirProcesosACola(colaDeProcesosNuevos, procesos);
    }

    private void ejecutarProcesos() {

        // mientras existan procesos en alguna de las colas o en la cpu, significa que todavia quedan procesos por ejecutar
        while (!colaDeProcesosNuevos.isEmpty() || !colaDeProcesosListosSuspendidos.isEmpty() ||
                !colaDeProcesosListos.isEmpty() || !(cpu.getProcesoEnEjecucion() == null)) {

            planificadorLargoPlazo();
            planificadorMedianoPlazo();
            planificadorCortoPlazo();
        }

        // imprimir situacion final
        imprimirEstadoDelSistema(this);
    }

    // el planificar de largo plazo mueve los procesos de la cola de Nuevos a Listos Suspendidos dependiendo del tiempo
    // de arribo, o sea, si el proceso "llego" o no. Si los muevo, los elimino de la cola de nuevos
    private void planificadorLargoPlazo() {
        List<Proceso> procesosAdmitidos = new ArrayList<>();

        for (Proceso proceso : colaDeProcesosNuevos) {
            // añado los procesos que pueden entrar en ese momento
            if (proceso.getTiempoArribo() <= instanteActual && nivelDeMultiprogramacion <= 4) {
                procesosAdmitidos.add(proceso);
                nivelDeMultiprogramacion++;
            }
        }

        unidadDeGestionDeMemoria.ordenarColaPorTiempoDeIrrupcion(procesosAdmitidos);
        unidadDeGestionDeMemoria.ordenarColaPorTiempoDeIrrupcion(colaDeProcesosListosSuspendidos);

        List<Particion> todasLasParticiones = this.memoria.getParticiones();
        int indiceDeProcesosAdmitidos = 0;
        List<Proceso> procesosAniadidosAParticiones = new ArrayList<>();
        while (memoria.existeParticionVacia() && procesosAdmitidos.size() > indiceDeProcesosAdmitidos) {

            if (colaDeProcesosListosSuspendidos.isEmpty() ||
                    colaDeProcesosListosSuspendidos.get(0).getTiempoIrrupcion() > procesosAdmitidos.get(indiceDeProcesosAdmitidos).getTiempoIrrupcion()) {

                Proceso proceso = unidadDeGestionDeMemoria.obtenerProcesoDeCola(procesosAdmitidos, indiceDeProcesosAdmitidos);

                // Recorro las todas las particiones para encontrar la peor, para cumplir con el algoritmo Worst Fit
                int diferencia = Integer.MIN_VALUE;
                Integer particionElegida = Integer.MIN_VALUE;
                int indiceDeParticion = 0;
                for (Particion particion : todasLasParticiones) {
                    // la particion con id 0 es el sistema operativo, NO se puede tocar, y la particion con proceso == null es una particion vacia
                    if (!particion.getId().equals(0) && particion.getProceso() == null) {
                        if ((particion.getTamanio() >= proceso.getTamanio()) && (particion.getTamanio() - proceso.getTamanio() > diferencia)) {
                            diferencia = particion.getTamanio() - proceso.getTamanio();
                            particionElegida = indiceDeParticion;
                        }
                    }
                    indiceDeParticion++;
                }

                // seteo el proceso elegido a la particion elegida
                if (!particionElegida.equals(Integer.MIN_VALUE)) {
                    todasLasParticiones.get(particionElegida).setProceso(proceso);
                    procesosAniadidosAParticiones.add(proceso);
                }

                indiceDeProcesosAdmitidos++;
            } else {
                break;
            }
        }

        // luego de seleccionar los procesos con sus particiones correspondientes, los elimino de la cola de listos y suspendidos
        // y los muevo a la cola de listos
        //if (!procesosAniadidosAParticiones.isEmpty()) {
            unidadDeGestionDeMemoria.aniadirProcesosACola(colaDeProcesosListos, procesosAniadidosAParticiones);
            unidadDeGestionDeMemoria.eliminarProcesosDeCola(colaDeProcesosNuevos, procesosAniadidosAParticiones);
            unidadDeGestionDeMemoria.eliminarProcesosDeCola(procesosAdmitidos, procesosAniadidosAParticiones);
        //} else {
            // sino, llamo a la MMU para añadir los procesos que pueden entrar a la cola de Listos-Suspendidos que no pudieron entrar a memoria directamente
            unidadDeGestionDeMemoria.aniadirProcesosACola(colaDeProcesosListosSuspendidos, procesosAdmitidos);
            unidadDeGestionDeMemoria.eliminarProcesosDeCola(colaDeProcesosNuevos, procesosAdmitidos);
        //}
    }

    // el planificador de mediano plazo mueve los procesos de la cola de listos-suspendidos a la cola de listos si existen
    // particiones libres, y buscara la que genere MAYOR fragmentacion interna (Worst Fit)
    private void planificadorMedianoPlazo() {

        // ordeno la cola de procesos listos-suspendidos por tiempo de irrupcion (de menor a mayor, SJF)
        unidadDeGestionDeMemoria.ordenarColaPorTiempoDeIrrupcion(colaDeProcesosListosSuspendidos);

        // pregunto existen particiones vacias, si las hay, puedo asignarle un proceso
        if (memoria.existeParticionVacia()) {

            List<Particion> todasLasParticiones = this.memoria.getParticiones();
            int indiceDeListaDeProcesosListosYSuspendidos = 0;
            List<Proceso> procesosAniadidosAParticiones = new ArrayList<>();

            while (memoria.existeParticionVacia() && colaDeProcesosListosSuspendidos.size() > indiceDeListaDeProcesosListosYSuspendidos) {

                Proceso proceso = unidadDeGestionDeMemoria.obtenerProcesoDeCola(colaDeProcesosListosSuspendidos, indiceDeListaDeProcesosListosYSuspendidos);

                // Recorro las todas las particiones para encontrar la peor, para cumplir con el algoritmo Worst Fit
                int diferencia = Integer.MIN_VALUE;
                Integer particionElegida = Integer.MIN_VALUE;
                int indiceDeParticion = 0;
                for (Particion particion : todasLasParticiones) {
                    // la particion con id 0 es el sistema operativo, NO se puede tocar
                    if (!particion.getId().equals(0) /*&& particion.getProceso() == null*/) {
                        if ((particion.getTamanio() >= proceso.getTamanio()) && (particion.getTamanio() - proceso.getTamanio() > diferencia)) {
                            if (!(particion.getProceso() == cpu.getProcesoEnEjecucion()) && (particion.getProceso() == null || particion.getProceso().getTiempoIrrupcion() > proceso.getTiempoIrrupcion()) ) {
                                if (!(particion.getProceso() == null)) {
                                    unidadDeGestionDeMemoria.aniadirProcesoACola(colaDeProcesosListosSuspendidos, particion.getProceso());
                                }
                                diferencia = particion.getTamanio() - proceso.getTamanio();
                                particionElegida = indiceDeParticion;
                            }
                        }
                    }
                    indiceDeParticion++;
                }

                // seteo el proceso elegido a la particion elegida
                if (!particionElegida.equals(Integer.MIN_VALUE)) {
                    todasLasParticiones.get(particionElegida).setProceso(proceso);
                    procesosAniadidosAParticiones.add(proceso);
                }

                indiceDeListaDeProcesosListosYSuspendidos++;
            }

            // luego de mover los procesos a las particiones correspondientes, los elimino de la cola de listos y suspendidos
            // y los muevo a la cola de listos
            if (!procesosAniadidosAParticiones.isEmpty()) {
                unidadDeGestionDeMemoria.aniadirProcesosACola(colaDeProcesosListos, procesosAniadidosAParticiones);
                unidadDeGestionDeMemoria.eliminarProcesosDeCola(colaDeProcesosListosSuspendidos, procesosAniadidosAParticiones);
            }
        }
    }

    // Selecciona el proceso a ejecutarse aplicando SJF. (Comparando los Tiempos de Irrupción).
    private void planificadorCortoPlazo() {

        unidadDeGestionDeMemoria.ordenarColaPorTiempoDeIrrupcion(colaDeProcesosListos);

        this.terminarProcesoEnEjecucion();

        if (colaDeProcesosListos.size() > 0) {
            if (cpu.getProcesoEnEjecucion() == null) {
                // El procesador está Vacio
                Proceso procesoAEjecutar = unidadDeGestionDeMemoria.eliminarProcesoDeCola(colaDeProcesosListos, 0);
                cpu.setProcesoEnEjecucion(procesoAEjecutar);
            }
        }
        cpu.incrementarInstante();
    }

    // pregunta si la cpu tiene algun proceso en ejecucion, luego si ese proceso tiene tiempo de irrupcion restante en 0
    // y si es verdadero, imprime la situacion actual, lo añade a la cola de terminados, vacia la particion, setea el
    // proceso en ejecucion de la cpu en null, ejecuta el planificador a mediano plazo
    private void terminarProcesoEnEjecucion() {
        if (cpu.getProcesoEnEjecucion() != null) {
            if (cpu.getProcesoEnEjecucion().getTiempoIrrupcion() == 0) {

                imprimirEstadoDelSistema(this);

                unidadDeGestionDeMemoria.aniadirProcesoACola(colaDeProcesosTerminados, cpu.getProcesoEnEjecucion());
//                colaDeProcesosTerminados.add(cpu.getProcesoEnEjecucion());
                cpu.getProcesoEnEjecucion().getParticion().vaciarParticion();
                cpu.setProcesoEnEjecucion(null);

                nivelDeMultiprogramacion--;

                // luego ejecuto el planificador de mediano plazo de nuevo para ver si pueden entrar nuevos procesos
                planificadorMedianoPlazo();
            }
        }
    }
}