package controllers;

import model.Cuenta;
import services.CuentaService;
import services.FileService;

import java.util.List;

public class CuentaController {

    private CuentaService cuentaService;
    private FileService fileService;
    private String carpetaBase;

    public CuentaController(String carpetaBase) {
        this.carpetaBase = carpetaBase;
        this.cuentaService = new CuentaService(carpetaBase);
        this.fileService = new FileService(carpetaBase);
    }

    //------------PROCESAMOS TODAS LAS CUENTAS (JSON)-----------
    public boolean procesarCuentasConHilos() {
        try {
            System.out.println("Iniciando procesamiento de cuentas con hilos...");
            return cuentaService.procesarCuentas();
        } catch (Exception e) {
            System.err.println("(ERROR!!) Error en el controlador: " + e.getMessage());
            return false;
        }
    }


    public boolean verificarEstructuraDirectorios() {
        return fileService.verificarYCrearDirectorios();
    }

    //-----------------OBTENEMOS LAS ESTADISTICAS--------------------
    public void mostrarEstadisticas() {
        List<Cuenta> cuentas = cuentaService.obtenerCuentasActivas();

        long cuentasAptas = cuentas.stream()
                .filter(c -> c.getSaldo() > 5000)
                .count();

        long cuentasNoAptas = cuentas.size() - cuentasAptas;

        System.out.println("\nESTADÍSTICAS DEL PROCESAMIENTO:");
        System.out.println("Total de cuentas activas: " + cuentas.size());
        System.out.println("Cuentas aptas para concurso: " + cuentasAptas);
        System.out.println("Cuentas no aptas: " + cuentasNoAptas);
        System.out.println("Archivos generados en: " + carpetaBase + "\\salidas");
    }


    public boolean validarArchivoJSON() {
        return fileService.verificarArchivoJSON();
    }
}
