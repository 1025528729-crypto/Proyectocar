package com.mycompany.rentcar.util;

import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.SwingWorker;

/**
 * Hace el trabajo lento (consultas a la base de datos) fuera del hilo de la
 * interfaz y entrega el resultado de vuelta en el hilo de Swing, para que la
 * ventana nunca se congele.
 */
public final class Async {

    private Async() { }

    public static <T> void run(Supplier<T> trabajo, Consumer<T> alTerminar) {
        run(trabajo, alTerminar, ex -> ex.printStackTrace());
    }

    public static <T> void run(Supplier<T> trabajo, Consumer<T> alTerminar, Consumer<Throwable> alFallar) {
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() {
                return trabajo.get();
            }
            @Override protected void done() {
                try {
                    alTerminar.accept(get());
                } catch (java.util.concurrent.ExecutionException e) {
                    alFallar.accept(e.getCause() == null ? e : e.getCause());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }
}