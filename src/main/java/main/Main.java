package main;

import controllers.CuentaController;

public class Main {
    public static void main(String[] args) {
        String carpetaBase = "C:\\DSW-II\\T1-DSW_II";

        //---------------------CONTROLADOR--------------------------
        CuentaController controller = new CuentaController(carpetaBase);

        System.out.println("SISTEMA DE PROCESAMIENTO DE CUENTAS BANCARIAS");
        System.out.println("================================================");

        //-----------------------VALIDAMOS---------------------------
        if (!controller.verificarEstructuraDirectorios()) {
            System.err.println("(ERROR!!) Error en la estructura de directorios");
            return;
        }

        if (!controller.validarArchivoJSON()) {
            System.err.println("(ERROR!!) Archivo JSON no encontrado");
            return;
        }

        //---------------------PROCESAMOS CUENTAS-----------------------
        boolean exito = controller.procesarCuentasConHilos();

        if (exito) {
            //--------------MOSTRAMOS ESTADISTICAS-------------------
            controller.mostrarEstadisticas();
            System.out.println("\nSUCESS!! Proceso completado exitosamente");
        } else {
            System.err.println("(ERROR!!) El proceso falló");
        }
    }
}