package com.mycompany.rentcar.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

/**
 * Guarda y resuelve fotografías sin reutilizar el mismo archivo para vehículos distintos.
 * Las fotos nuevas quedan fuera de la carpeta del proyecto, por lo que no se pierden
 * al limpiar target/ ni al volver a abrir NetBeans.
 */
public final class FotoVehiculoUtil {
    private FotoVehiculoUtil() { }

    public static String guardarCopiaUnica(File origen, String placa) throws IOException {
        if (origen == null || !origen.isFile()) {
            throw new IOException("No se encontró el archivo de imagen seleccionado.");
        }
        String nombre = origen.getName();
        int punto = nombre.lastIndexOf('.');
        String extension = punto >= 0 ? nombre.substring(punto).toLowerCase(Locale.ROOT) : ".jpg";
        if (!extension.matches("\\.(jpg|jpeg|png)$")) extension = ".jpg";

        String placaSegura = placa == null ? "vehiculo" : placa.trim().toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9_-]", "_");
        if (placaSegura.isBlank()) placaSegura = "vehiculo";

        Path carpeta = Path.of(System.getProperty("user.home"), ".rentcar", "vehiculos");
        Files.createDirectories(carpeta);
        String archivoUnico = placaSegura + "_" + UUID.randomUUID() + extension;
        Path destino = carpeta.resolve(archivoUnico);
        Files.copy(origen.toPath(), destino);
        return destino.toAbsolutePath().normalize().toString();
    }

    /** Resuelve tanto rutas absolutas nuevas como rutas relativas guardadas por versiones anteriores. */
    public static File resolver(String ruta) {
        if (ruta == null || ruta.trim().isEmpty()) return null;
        try {
            Path indicada = Path.of(ruta.trim());
            if (indicada.isAbsolute() && Files.isRegularFile(indicada)) return indicada.toFile();
            if (Files.isRegularFile(indicada)) return indicada.toAbsolutePath().normalize().toFile();

            Path actual = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
            for (int nivel = 0; actual != null && nivel < 5; nivel++, actual = actual.getParent()) {
                Path candidata = actual.resolve(indicada).normalize();
                if (Files.isRegularFile(candidata)) return candidata.toFile();
            }
        } catch (Exception ignored) {
            // Una ruta antigua inválida no debe impedir que cargue la ventana.
        }
        return null;
    }
}
