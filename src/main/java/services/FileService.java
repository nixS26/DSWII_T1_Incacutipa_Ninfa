package services;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileService {

    private final String carpetaBase;
    private final String archivoJSON;
    private final String carpetaSalida;

    public FileService(String carpetaBase) {
        this.carpetaBase = carpetaBase;
        this.archivoJSON = carpetaBase + "\\cuentas.json";
        this.carpetaSalida = carpetaBase + "\\salidas";
    }

    public boolean verificarYCrearDirectorios() {
        try {
            File carpetaBaseFille = new File(carpetaBase);
            if (!carpetaBaseFille.exists()) {
                System.err.println("(ERROR!!) Carpeta base no existe: " + carpetaBase);
                return false;
            }

            //------------------CREAMOS CARPETA DE SALIDA-----------------------------
            File carpetaSalidaFile = new File(carpetaSalida);
            if (!carpetaSalidaFile.exists()) {
                if (carpetaSalidaFile.mkdirs()) {
                    System.out.println("(SUCCESS!!) Carpeta de salida creada: " + carpetaSalida);
                } else {
                    System.err.println("(ERROR!!) No se pudo crear carpeta de salida");
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println("(ERROR!!) Error al verificar directorios: " + e.getMessage());
            return false;
        }
    }

    public boolean verificarArchivoJSON() {
        return Files.exists(Paths.get(archivoJSON));
    }
}