# Simulado rde Asignación de Memoria y
Planificación de procesos
--------------------------------------
Implementación de un simulador que permita mostrar los aspectos de la planificación a Corto Plazo y la gestión de la memoria con particiones Fijas dentro de un esquema de un solo
procesador, tratando el ciclo de vida completo de un proceso desde su ingreso al sistema hasta su finalización.
--------------------------------------
Consigna:
Implementar un simulador de asignación de memoria y planificación de procesos según los siguientes
requerimientos:
- El simulador deberá brindarla posibilidad de cargar N procesos. Para facilitar la implementación se permitirán como
máximo 10 procesos y la asignación de memoria se realizará con particiones fijas. El esquema de particiones será
el siguiente:
  - 100K destinados al Sistema Operativo
  - 250K para trabajos los más grandes.
  - 120K para trabajos medianos .
  - 60K para trabajos pequeños.
  
- El programa debe permitir ingreso de nuevos procesos cuando sea posible (manteniendo en grado de
multiprogramación en 5.
- La política de asignación de memoria será Worst-Fit.
- Por cada proceso se debe ingresar o leer desde un archivo el Id de proceso, tamaño del proceso, tiempo de arribo y tiempo de irrupción. 
- La planificación de CPU será dirigida por un algoritmo SJF.

- El simulador deberá presentar como salida la siguiente información:
  - El estado del procesador (proceso que se encuentra corriendo en ese instante)
  - La tabla de particiones de memoria, la cual deberá contener (Id de partición, dirección de comienzo de
partición, tamaño de la partición, id de proceso asignado a la partición, fragmentación interna)
  - El estado de la cola de procesos listos.
  - Listado de procesos que no se encuentran en estado de listo ni ejecución (informar el estado en que se
encuentran)

--------------------------------------
Consideraciones:
- Las presentaciones de salida deberán realizarse cada vez que llega un nuevo proceso, se termina un proceso
en ejecución.
- No se permiten corridas ininterrumpidas de simulador, desde que se inicia la simulación hasta que termina el
último proceso.
- El trabajos deberá ser implementado por equipos de trabajo (los mismos equipos que ya fueron conformados al
inicio del cuatrimestre)
- El programa deberá ser implementado en el lenguaje de programacion que elija el grupo.
- El simular debe funcionar en una máquina de escritorio, no se permiten simuladores que funcionen on-line.
- No es necesario realizar el simulador con entorno gráfico.
- El simulador será entregado, vía campus virtual, en un paquete que contenga: el programa ejecutable, el
código fuente acompañado de un howto que indique como ejecutar el simulador.
- Se realizarán presentaciones de avances, antes de la entrega final, las cuales serán consideradas
obligatorias ya que conformarán el coloquio del TPI. Las fechas estipuladas son:
◦ 13/10
◦ 10/11
◦ 17/11
 La entrega final será el 25 de noviembre.
 El coloquio de defensa del TPI se llevará a cabo el 1/12 o 10/12 (a confirmar para cada grupo)
