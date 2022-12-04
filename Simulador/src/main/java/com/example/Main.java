package com.example;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.*;

import static com.example.Cpu.instanteActual;

public class Main {

    public static Scanner scannerTeclas = new Scanner(System.in);

    public static void main(String[] args) {

        JFileChooser jfc = new JFileChooser();
        jfc.addChoosableFileFilter(new FileNameExtensionFilter("Archivo txt", "txt"));
        jfc.setAcceptAllFileFilterUsed(true);
        int opcionSeleccionada = jfc.showOpenDialog(null);

        if (opcionSeleccionada == JFileChooser.APPROVE_OPTION) {
            File archivo = jfc.getSelectedFile();
            List<Proceso> procesos = leerArchivo(archivo);

            if (!procesos.isEmpty()) {
                new SistemaOperativo(procesos);
            } else {
                System.err.println("Error, no se ha podido cargar los procesos");
            }

        } else {
            System.err.println("Error, no ha seleccionado un archivo. Reinicie la aplicacion");
        }

        scannerTeclas.close();
    }

    private static List<Proceso> leerArchivo(File archivo) {

        List<Proceso> procesos = new ArrayList<>();

        // aca abro el archivo, si termina normalmente o si da error, lo cierra cuando termina el try
        try (Scanner scannerArchivo = new Scanner(archivo)) {

            while (scannerArchivo.hasNextLine() && procesos.size() <= 10) {
                String linea = scannerArchivo.nextLine();

                // pregunto si el primer caracter, correspondiente al id del procesoEnEjecucion, es un numero
                if(!linea.matches("[0-9;\n]+")) {
                    throw new Exception("El archivo tiene un formato invalido");
                }

                List<String> lineaDeProceso = Arrays.asList(linea.split(";"));

                if (Integer.parseInt(lineaDeProceso.get(1)) > 250 || Integer.parseInt(lineaDeProceso.get(1)) <= 0) {
                    throw new Exception("El tamaño del proceso no puede ser mayor a 250kb ni menor o igual a 0kb");
                }

                Proceso proceso = new Proceso(lineaDeProceso);
                procesos.add(proceso);
            }

        } catch (Exception e) {
            System.err.println(String.format("A ocurrido un error: %s", e.getMessage()));
            procesos = new ArrayList<>();
        }

        return procesos;
    }

    // imprime por pantalla la situacion actual del sistema:
    //  - Estado del Procesador (procesoEnEjecucion que se encuentra corriendo en ese instanteActual) ✔
    //  - Tabla de particiones de memoria, la cual debe contener (id de particion, direccion de comienzo de particion, ✔
    //    tamaño de particion, id de procesoEnEjecucion asignado a la particion, fragmentacion interna ✔
    //  - Estado de la cola de procesos colaDeProcesosListos ✔
    //  - Listado de procesos que no se encuentran en estado de listo ni ejecucion (informar el estado en que se encuentran) ✔
    // TODO ver si todos estos println se pueden cambiar por toString() en cada objeto
    public static void imprimirEstadoDelSistema(SistemaOperativo sistemaOperativo) {

        System.out.println("--------------------------------------------------------------------------------------------");

        // imprimo el Estado del Procesador: el instanteActual y el procesoEnEjecucion que se encuentra ejecutandose en ese instanteActual
        System.out.println("Instante: " + instanteActual.toString() + "\n");
        System.out.println("CPU:");

        if (sistemaOperativo.getCpu().getProcesoEnEjecucion() != null) {
            System.out.println("\tProceso: " + sistemaOperativo.getCpu().getProcesoEnEjecucion().getId());
            System.out.println("\tTiempo de Irrupcion restante: " + sistemaOperativo.getCpu().getProcesoEnEjecucion().getTiempoIrrupcion() + "\n");
        } else {
            System.out.println("\tProceso: VACIO");
        }


        // imprimo la tabla de particiones de memoria
        System.out.println("Tabla de Particiones:");
        for (Particion particion : sistemaOperativo.getMemoria().getParticiones()) {

            // busco el id del procesoEnEjecucion por fuera para no tener null pointer exception
            String idProceso = Optional.ofNullable(particion.getProceso())
                    .map(proceso -> proceso.getId())
                    .map(id -> id.toString()).orElse("LIBRE");

            System.out.println("\tParticion: " + particion.getId());
            System.out.println("\t\tDireccion de Inicio: " + particion.getDireccionInicio());
            System.out.println("\t\tDireccion de Fin: " + particion.getDireccionFin());
            System.out.println("\t\tTamanio: " + particion.getTamanio() + "kb");

            if (particion.getId().equals(0)) {
                System.out.println("\t\tProceso: SISTEMA OPERATIVO");
            } else {
                System.out.println("\t\tProceso: " + idProceso);
            }

            System.out.println("\t\tFramentacion Interna: " + particion.getFragmentacionInterna());
        }

        // imprimo la cola de procesos en estado nuevo
        System.out.println("\nCola de Procesos Nuevos:");
        if (!sistemaOperativo.getColaDeProcesosNuevos().isEmpty()) {
            for (Proceso proceso : sistemaOperativo.getColaDeProcesosNuevos()) {
                System.out.println("\tProceso: " + proceso.getId() + ", Tiempo de Irrupcion: " + proceso.getTiempoIrrupcion() + ", Tamaño: " + proceso.getTamanio() + "kb");
            }
        } else {
            System.out.println("\tNo hay procesos en Estado NUEVO");
        }

        System.out.println("\nCola de Procesos Listos-Suspendidos");
        if (!sistemaOperativo.getColaDeProcesosListosSuspendidos().isEmpty()) {
            for (Proceso proceso : sistemaOperativo.getColaDeProcesosListosSuspendidos()) {
                System.out.println("\tProceso: " + proceso.getId() + ", Tiempo de Irrupcion: " + proceso.getTiempoIrrupcion() + ", Tamaño: " + proceso.getTamanio() + "kb");
            }
        } else {
            System.out.println("\tNo hay procesos en Estado estado LISTO-SUSPENDIDO");
        }

        // imprimo la cola de procesos en estado listo
        System.out.println("\nCola de Procesos Listos:");
        if (!sistemaOperativo.getColaDeProcesosListos().isEmpty()) {
            for (Proceso proceso : sistemaOperativo.getColaDeProcesosListos()) {
                System.out.println("\tProceso: " + proceso.getId() + ", Tiempo de Irrupcion: " + proceso.getTiempoIrrupcion() + ", Tamaño: " + proceso.getTamanio() + "kb");
            }
        } else {
            System.out.println("\tNo hay procesos en Estado LISTO");
        }

        System.out.println("\nProceso en EJECUCION:");
        if(sistemaOperativo.getCpu().getProcesoEnEjecucion() != null) {
            Proceso proceso = sistemaOperativo.getCpu().getProcesoEnEjecucion();
            System.out.println("\tProceso: " + proceso.getId() + ", Tiempo de Irrupcion: " + proceso.getTiempoIrrupcion() + ", Tamaño: " + proceso.getTamanio() + "kb");
        } else {
            System.out.println("\tNo hay ningun proceso en EJECUCION");
        }

        System.out.println("\nCola de Procesos TERMINADOS:");
        if(!sistemaOperativo.getColaDeProcesosTerminados().isEmpty()) {
            for (Proceso proceso : sistemaOperativo.getColaDeProcesosTerminados()) {
                System.out.println("\tProceso: " + proceso.getId() + ", Tiempo de Irrupcion: " + proceso.getTiempoIrrupcion() + ", Tamaño: " + proceso.getTamanio() + "kb");
            }
        } else {
            System.out.println("\tNo hay procesos TERMINADOS");
        }

        System.out.println("--------------------------------------------------------------------------------------------");

        System.out.println("Ingrese un caracter para continuar");
        String continuar = scannerTeclas.next();
    }
}
