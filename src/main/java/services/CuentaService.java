package services;

import model.Cuenta;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CuentaService {

    private final String carpetaBase;
    private final String archivoJSON;
    private final String carpetaSalida;
    private List<Cuenta> todasLasCuentas;

    public CuentaService(String carpetaBase) {
        this.carpetaBase = carpetaBase;
        this.archivoJSON = carpetaBase + "\\cuentas.json";
        this.carpetaSalida = carpetaBase + "\\salidas";
        this.todasLasCuentas = new ArrayList<>();
    }

    public boolean procesarCuentas() {
        try {
            if (!cargarCuentasDesdeJSON()) {
                return false;
            }

            return procesarCuentasConHilos();

        } catch (Exception e) {
            System.err.println("(ERROR!!) Error al procesar cuentas: " + e.getMessage());
            return false;
        }
    }

    private boolean cargarCuentasDesdeJSON() {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(archivoJSON)));
            JSONArray array = new JSONArray(contenido);

            todasLasCuentas.clear();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Cuenta cuenta = new Cuenta(
                        obj.getBoolean("estado"),
                        obj.getInt("nro_cuenta"),
                        obj.getDouble("saldo"),
                        obj.getString("banco")
                );
                todasLasCuentas.add(cuenta);
            }

            System.out.println("(SUCCESS!!) JSON cargado exitosamente. Total cuentas: " + todasLasCuentas.size());
            return true;

        } catch (IOException e) {
            System.err.println("(ERROR!!) Error al leer archivo JSON: " + e.getMessage());
            return false;
        }
    }
    //---------------------PROCESAMOS CON HILOS----------------------
    private boolean procesarCuentasConHilos() {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            System.out.println("Procesando cuentas activas con hilos...");

            todasLasCuentas.stream()
                    .filter(Cuenta::isEstado)
                    .forEach(cuenta -> executor.submit(() -> generarArchivo(cuenta)));

            executor.shutdown();

            if (executor.awaitTermination(30, TimeUnit.SECONDS)) {
                mostrarMensajeSincronizado("(SUCCESS!!) Procesamiento completado exitosamente");
                return true;
            } else {
                mostrarMensajeSincronizado("(ALERT!!) Timeout: Algunos hilos no terminaron a tiempo");
                return false;
            }

        } catch (InterruptedException e) {
            mostrarMensajeSincronizado("(ERROR!!) Procesamiento interrumpido: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    //----------------------GENERAMOS LOS ARCHIVOS TXT------------------------
    private void generarArchivo(Cuenta cuenta) {
        String contenido = construirContenidoArchivo(cuenta);
        String nombreArchivo = carpetaSalida + "\\cuenta_" + cuenta.getNroCuenta() + ".txt";

        try (FileWriter fw = new FileWriter(nombreArchivo)) {
            fw.write(contenido);

            String mensaje = String.format("[HILO-%d] %s Cuenta %d: %s (saldo: %.2f)",
                    Thread.currentThread().getId(),
                    cuenta.getSaldo() > 5000 ? "(=>)" : "(X)",
                    cuenta.getNroCuenta(),
                    cuenta.getSaldo() > 5000 ? "Apto para concurso" : "No apto",
                    cuenta.getSaldo()
            );

            mostrarMensajeSincronizado(mensaje);

        } catch (IOException e) {
            mostrarMensajeSincronizado("(ERROR!!) Error al escribir archivo para cuenta " +
                    cuenta.getNroCuenta() + ": " + e.getMessage());
        }
    }
    //--------------------CONTENIDO DEL .TXT------------------
    private String construirContenidoArchivo(Cuenta cuenta) {
        if (cuenta.getSaldo() > 5000) {
            return "Banco de origen: " + cuenta.getBanco() + "\n"
                    + "La cuenta con el nro de cuenta: " + cuenta.getNroCuenta() + "tiene un saldo de ." + cuenta.getSaldo() + "\n"
                    + "Usted es apto a participar en el  concurso de la SBS por 10000.00 soles.Suerte!";
        } else {
            return "Banco de origen: " + cuenta.getBanco() + "\n"
                    + "La cuenta con el nro de cuenta: " + cuenta.getNroCuenta() + " no tiene un saldo superior a 5000.00.\n"
                    + "Lamentablemente no podrá acceder al concurso de la SBS por 10000.00 soles. Gracias";
        }
    }

    public List<Cuenta> obtenerCuentasActivas() {
        return todasLasCuentas.stream()
                .filter(Cuenta::isEstado)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private synchronized void mostrarMensajeSincronizado(String mensaje) {
        System.out.println(mensaje);
    }
}