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

    private void inicializarMemoria() {
        // creo la memoria con la particion inicial por defecto para el Sistema Operativo de 100kb
        this.memoria = new Memoria(100);

        // creo las particiones extras necesarias
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

            List<Particion> todasLasParticiones = this.memoria.getParticiones();
            int indiceDeListaDeProcesosListosYSuspendidos = 0;
            List<Proceso> procesosAniadidosAParticiones = new ArrayList<>();

            while (memoria.existeParticionVacia() && colaDeProcesosListosSuspendidos.size() > indiceDeListaDeProcesosListosYSuspendidos) {

                Proceso proceso = colaDeProcesosListosSuspendidos.get(indiceDeListaDeProcesosListosYSuspendidos);

                // Recorro las todasLasParticiones para encontrar la peor, para cumplir con el algoritmo Worst Fit
                int diferencia = Integer.MIN_VALUE;
                Integer particionElegida = Integer.MIN_VALUE;
                int indiceDeParticion = 0;
                for (Particion particion : todasLasParticiones) {
                    // la particion con id 0 es el sistema operativo, NO se puede tocar
                    if (!particion.getId().equals(0) && particion.getProceso() == null) {
                        //Pregunta si el tamaño de la particion actual es > al tamañano del proceso y ademas si la diferencia de los
                        //tamaños entre la particion y el proceso es mayor a la difencia(representa la fragmentacion interna)
                        //la primera vez si es menor a -2**31 , entonces la primera vez va a asignar siempre la primera particion
                        //libre que mayor fragmentacion interna produzca tamaño produzca, luego va a recorrer las demas particiones
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

                indiceDeListaDeProcesosListosYSuspendidos++;
            }

            // luego de mover los procesos a las particiones correspondientes, los elimino de la cola de listos y suspendidos
            // y los muevo a la cola de listos
            if (!procesosAniadidosAParticiones.isEmpty()) {
                colaDeProcesosListos.addAll(procesosAniadidosAParticiones);
                colaDeProcesosListosSuspendidos.removeAll(procesosAniadidosAParticiones);
            }
        }

        // por ultimo veo si puedo mover los procesos con TI mas corto a la memoria
        //intercambiarProcesosEntreListosYListosYSuspendidos();
    }

    // Selecciona el proceso a ejecutarse aplicando SJF. (Comparando los Tiempos de Irrupción).
    private void planificadorCortoPlazo() {
        ordenarColaPorTiempoDeIrrupcion(colaDeProcesosListos);
        this.terminarProcesoEnEjecucion();
        if (colaDeProcesosListos.size() > 0) {
            Proceso victima = colaDeProcesosListos.remove((int) 0);
            if (cpu.getProcesoEnEjecucion() == null) {
                // El procesador está Vacio
                cpu.setProcesoEnEjecucion(victima);
            } else {
                // procesador tiene algún procesoEnEjecucion
                colaDeProcesosListos.add(victima);
            }
        }
        cpu.incrementarInstante();
    }

    private void intercambiarProcesosEntreListosYListosYSuspendidos() {

    }

    // pregunta si la cpu tiene algun proceso en ejecucion, luego si ese proceso tiene tiempo de irrupcion restante en 0
    // y si es verdadero, imprime la situacion actual, lo añade a la cola de terminados, vacia la particion, setea el
    // proceso en ejecucion de la cpu en null, ejecuta el planificador a mediano plazo y ordena la cola de procesos listos
    private void terminarProcesoEnEjecucion() {
        if (cpu.getProcesoEnEjecucion() != null) {
            if (cpu.getProcesoEnEjecucion().getTiempoIrrupcion() == 0) {

                imprimirEstadoDelSistema(this);

                colaDeProcesosTerminados.add(cpu.getProcesoEnEjecucion());
                cpu.getProcesoEnEjecucion().getParticion().vaciarParticion();
                cpu.setProcesoEnEjecucion(null);

                // luego ejecuto el planificador de mediano plazo de nuevo para ver si pueden entrar nuevos procesos
                planificadorMedianoPlazo();

                //ordenarColaPorTiempoDeIrrupcion(colaDeProcesosListos);
            }
        }
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