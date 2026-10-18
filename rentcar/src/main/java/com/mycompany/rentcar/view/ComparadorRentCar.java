package com.mycompany.rentcar.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * Comparador de alquileres.
 *
 * Es genérico: no sabe nada de "autos" o "motos". Compara cualquier vehículo
 * (auto, moto, patineta, bicicleta, camión, camper…) con los mismos criterios y
 * muestra "N/A" cuando un dato no aplica (por ejemplo, puertas en una bicicleta
 * o consumo en una patineta sin motor). Así los tipos nuevos funcionan sin
 * tocar este archivo.
 */
public final class ComparadorRentCar {

    /** Máximo de vehículos que se pueden comparar a la vez. */
    public static final int MAX = 4;

    // La selección vive mientras el programa esté abierto (también al cambiar de tema).
    private static final Set<String> SELECCION = new LinkedHashSet<>();

    private ComparadorRentCar() {
    }

    // =========================================================
    // SELECCIÓN
    // =========================================================
    public static synchronized boolean contiene(String placa) {
        return SELECCION.contains(placa);
    }

    /** Agrega o quita. Devuelve false solo si se intentó agregar y ya está lleno. */
    public static synchronized boolean alternar(String placa) {
        if (SELECCION.remove(placa)) {
            return true;
        }
        if (SELECCION.size() >= MAX) {
            return false;
        }
        SELECCION.add(placa);
        return true;
    }

    public static synchronized void quitar(String placa) {
        SELECCION.remove(placa);
    }

    public static synchronized void limpiar() {
        SELECCION.clear();
    }

    public static synchronized int cantidad() {
        return SELECCION.size();
    }

    public static synchronized List<String> seleccionados() {
        return new ArrayList<>(SELECCION);
    }

    // =========================================================
    // DATOS Y ACCIONES
    // =========================================================
    /** Datos de un vehículo tal como se muestran en el comparador. */
    public static final class Item {

        public String placa = "", marca = "", modelo = "", tipo = "AUTO";
        public String transmision = "", combustible = "", categoria = "", ciudad = "", color = "";
        public double precioDia;
        public int anio, capacidad, kilometraje, puertas;
        public double consumo;            // 0 = sin dato
        public boolean disponible = true;
        public String estado = "Disponible";
        public BufferedImage imagen;

        String nombre() {
            return (marca + " " + modelo).trim();
        }
    }

    /** Lo que el comparador necesita de la pantalla que lo abre. */
    public interface Acciones {

        void reservar(String placa);

        /** Se llama cuando cambia la selección (se quitó un vehículo, se vació, etc.). */
        void seleccionCambio();

        JButton botonPrimario(String texto);

        JButton botonSecundario(String texto);

        void estilizar(JScrollPane scroll);
    }

    // =========================================================
    // BOTÓN PARA LA TARJETA DEL CATÁLOGO
    // =========================================================
    /** Pastilla «+ Comparar» para poner sobre la foto de cada vehículo. */
    public static JComponent crearBotonTarjeta(String placa, Runnable alCambiar, Runnable alLlenarse) {
        return new BotonComparar(placa, alCambiar, alLlenarse);
    }

    private static final class BotonComparar extends JComponent {

        private final String placa;
        private boolean encima;

        BotonComparar(String placa, Runnable alCambiar, Runnable alLlenarse) {
            this.placa = placa;
            setPreferredSize(new Dimension(118, 28));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText(contiene(placa) ? "Quitar del comparador" : "Agregar al comparador");
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    encima = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    encima = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!alternar(placa)) {
                        if (alLlenarse != null) {
                            alLlenarse.run();
                        }
                        return;
                    }
                    setToolTipText(contiene(placa) ? "Quitar del comparador" : "Agregar al comparador");
                    repaint();
                    if (alCambiar != null) {
                        alCambiar.run();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            Graphics2DHelper.pintarPastilla((java.awt.Graphics2D) g.create(), this, contiene(placa), encima);
        }
    }

    /** Pintado de la pastilla (separado para no mezclar con la lógica del botón). */
    private static final class Graphics2DHelper {

        static void pintarPastilla(java.awt.Graphics2D g2, JComponent c, boolean activo, boolean encima) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = c.getWidth(), h = c.getHeight();
            Color fondo = activo ? TemaRentCar.c(224, 43, 49)
                    : TemaRentCar.c(255, 255, 255, encima ? 245 : 225);
            Color texto = activo ? Color.WHITE : TemaRentCar.c(23, 27, 34);
            g2.setColor(fondo);
            g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, h, h));
            if (!activo) {
                g2.setColor(TemaRentCar.c(211, 217, 226));
                g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, h, h));
            }
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            String t = activo ? "✓ Comparando" : "+ Comparar";
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(texto);
            g2.drawString(t, (w - fm.stringWidth(t)) / 2, (h - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    // =========================================================
    // PANEL DEL COMPARADOR
    // =========================================================
    /**
     * @param items   vehículos a comparar (en orden)
     * @param dias    días del alquiler (se pueden cambiar dentro del comparador)
     * @param periodo texto del periodo, por ejemplo «12/10/2026 → 15/10/2026»
     * @param cerrar  cierra la ventana que contiene el panel
     */
    public static JPanel crearPanel(List<Item> items, int dias, String periodo, Acciones acciones, Runnable cerrar) {
        return new Vista(items, dias, periodo, acciones, cerrar);
    }

    private static String dinero(double v) {
        return "$ " + String.format(Locale.GERMANY, "%,.0f", v);
    }

    private static String miles(long v) {
        return String.format(Locale.GERMANY, "%,d", v);
    }

    private static boolean esElectrico(Item it) {
        String c = it.combustible.toLowerCase(Locale.ROOT);
        return c.contains("léctric") || c.contains("electric");
    }

    private static boolean sinMotor(Item it) {
        String t = it.tipo.toUpperCase(Locale.ROOT);
        return !esElectrico(it) && (t.contains("BICI") || t.contains("PATIN"));
    }

    private static String textoConsumo(Item it) {
        if (it.consumo > 0) {
            String n = it.consumo == Math.rint(it.consumo)
                    ? String.valueOf((long) it.consumo)
                    : String.format(Locale.GERMANY, "%.1f", it.consumo);
            return n + (esElectrico(it) ? " km por carga" : " km/gal");
        }
        return sinMotor(it) ? "No aplica" : "N/A";
    }

    private interface Texto {

        String de(Item it);
    }

    private static final class Vista extends JPanel {

        private final List<Item> items;
        private int dias;
        private final String periodo;
        private final Acciones acciones;
        private final Runnable cerrar;

        private final Color FONDO = TemaRentCar.c(238, 241, 246);
        private final Color CAMPO = TemaRentCar.c(230, 234, 241);
        private final Color BORDE = TemaRentCar.c(211, 217, 226);
        private final Color TEXTO = TemaRentCar.c(23, 27, 34);
        private final Color SECUNDARIO = TemaRentCar.c(98, 105, 118);
        private final Color ROJO = TemaRentCar.c(224, 43, 49);
        private final Color MEJOR_FONDO = TemaRentCar.isOscuro() ? new Color(22, 62, 46) : new Color(222, 243, 232);
        private final Color MEJOR_TEXTO = TemaRentCar.isOscuro() ? new Color(110, 226, 164) : new Color(27, 120, 78);

        private final JPanel zonaGrid = new JPanel(new BorderLayout());
        private final JPanel pie = new JPanel(new BorderLayout(16, 0));
        private JLabel lblDias;
        private JLabel lblSubtitulo;
        private JScrollPane scroll;

        // resultados del último cálculo
        private int[] victorias;
        private boolean[][] premios;   // [item][0..2] económico, capacidad, eficiente

        Vista(List<Item> items, int dias, String periodo, Acciones acciones, Runnable cerrar) {
            super(new BorderLayout(0, 14));
            this.items = new ArrayList<>(items);
            this.dias = Math.max(1, dias);
            this.periodo = periodo;
            this.acciones = acciones;
            this.cerrar = cerrar;

            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(22, 28, 20, 28));

            add(crearEncabezado(), BorderLayout.NORTH);

            zonaGrid.setOpaque(false);
            add(zonaGrid, BorderLayout.CENTER);

            pie.setOpaque(false);
            add(pie, BorderLayout.SOUTH);

            reconstruir();
        }

        // ---------------- ENCABEZADO ----------------
        private JPanel crearEncabezado() {
            JPanel p = new JPanel(new BorderLayout(16, 0));
            p.setOpaque(false);

            JPanel textos = new JPanel();
            textos.setOpaque(false);
            textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
            JLabel peq = new JLabel("ELIGE LA MEJOR OPCIÓN");
            peq.setForeground(ROJO);
            peq.setFont(new Font("SansSerif", Font.BOLD, 10));
            JLabel titulo = new JLabel("Comparador de alquileres");
            titulo.setForeground(TEXTO);
            titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
            lblSubtitulo = new JLabel(" ");
            lblSubtitulo.setForeground(SECUNDARIO);
            lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
            textos.add(peq);
            textos.add(Box.createVerticalStrut(3));
            textos.add(titulo);
            textos.add(Box.createVerticalStrut(3));
            textos.add(lblSubtitulo);
            p.add(textos, BorderLayout.WEST);

            // selector de días: − N días +
            JPanel selector = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            selector.setOpaque(false);
            JLabel et = new JLabel("Días de alquiler");
            et.setForeground(SECUNDARIO);
            et.setFont(new Font("SansSerif", Font.PLAIN, 12));
            JButton menos = botonPaso("−");
            JButton mas = botonPaso("+");
            lblDias = new JLabel("", SwingConstants.CENTER);
            lblDias.setForeground(TEXTO);
            lblDias.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblDias.setPreferredSize(new Dimension(70, 30));
            menos.addActionListener(e -> cambiarDias(-1));
            mas.addActionListener(e -> cambiarDias(1));
            selector.add(et);
            selector.add(menos);
            selector.add(lblDias);
            selector.add(mas);
            p.add(selector, BorderLayout.EAST);
            return p;
        }

        private JButton botonPaso(String t) {
            JButton b = new JButton(t);
            b.setForeground(TEXTO);
            b.setBackground(CAMPO);
            b.setFont(new Font("SansSerif", Font.BOLD, 16));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createLineBorder(BORDE));
            b.setPreferredSize(new Dimension(32, 30));
            return b;
        }

        private void cambiarDias(int d) {
            dias = Math.max(1, Math.min(365, dias + d));
            reconstruir();
        }

        // ---------------- CÁLCULOS ----------------
        private double total(Item it) {
            return it.precioDia * dias;
        }

        private double[] valores(java.util.function.ToDoubleFunction<Item> f) {
            double[] v = new double[items.size()];
            for (int i = 0; i < v.length; i++) {
                v[i] = f.applyAsDouble(items.get(i));
            }
            return v;
        }

        /** Marca los mejores. No marca nada si hay menos de 2 datos válidos o todos son iguales. */
        private boolean[] mejores(double[] v, boolean mayorEsMejor) {
            boolean[] r = new boolean[v.length];
            int validos = 0;
            double mejor = Double.NaN;
            boolean todosIguales = true;
            Double primero = null;
            for (double x : v) {
                if (Double.isNaN(x)) {
                    continue;
                }
                validos++;
                if (primero == null) {
                    primero = x;
                } else if (x != primero) {
                    todosIguales = false;
                }
                if (Double.isNaN(mejor) || (mayorEsMejor ? x > mejor : x < mejor)) {
                    mejor = x;
                }
            }
            if (validos < 2 || todosIguales) {
                return r;
            }
            for (int i = 0; i < v.length; i++) {
                r[i] = !Double.isNaN(v[i]) && v[i] == mejor;
            }
            return r;
        }

        private static double valido(double x) {
            return x > 0 ? x : Double.NaN;
        }

        // ---------------- RECONSTRUIR TODO ----------------
        private void reconstruir() {
            lblDias.setText(dias + (dias == 1 ? " día" : " días"));
            StringBuilder sub = new StringBuilder();
            sub.append(items.size()).append(items.size() == 1 ? " vehículo" : " vehículos");
            if (periodo != null && !periodo.isBlank()) {
                sub.append("  ·  ").append(periodo);
            }
            lblSubtitulo.setText(sub.toString());

            int n = items.size();
            double[] tot = valores(this::total);
            double[] cap = valores(it -> valido(it.capacidad));
            double[] con = valores(it -> valido(it.consumo));
            double[] anio = valores(it -> valido(it.anio));
            double[] km = valores(it -> it.kilometraje > 0 ? it.kilometraje : Double.NaN);

            boolean[] mTot = mejores(tot, false);
            boolean[] mCap = mejores(cap, true);
            boolean[] mCon = mejores(con, true);
            boolean[] mAnio = mejores(anio, true);
            boolean[] mKm = mejores(km, false);

            victorias = new int[n];
            premios = new boolean[n][3];
            for (int i = 0; i < n; i++) {
                if (mTot[i]) { victorias[i]++; premios[i][0] = true; }
                if (mCap[i]) { victorias[i]++; premios[i][1] = true; }
                if (mCon[i]) { victorias[i]++; premios[i][2] = true; }
                if (mAnio[i]) { victorias[i]++; }
                if (mKm[i]) { victorias[i]++; }
            }

            JPanel grid = new Rejilla();
            grid.setOpaque(false);
            grid.setLayout(new GridBagLayout());
            int[] y = {0};

            // fila de tarjetas de vehículo
            celdaEtiqueta(grid, y[0], "");
            for (int i = 0; i < n; i++) {
                agregar(grid, crearTarjetaVehiculo(i), i + 1, y[0], 1, new Insets(0, 4, 8, 4));
            }
            y[0]++;

            // COSTO
            seccion(grid, y, "COSTO");
            fila(grid, y, "Precio por día", it -> dinero(it.precioDia), valores(it -> it.precioDia > 0 ? it.precioDia : Double.NaN), false, false);
            filaTotal(grid, y, tot, mTot);
            filaDiferencia(grid, y, tot);

            // RENDIMIENTO
            seccion(grid, y, "CAPACIDAD Y CONSUMO");
            fila(grid, y, "Capacidad", it -> it.capacidad > 0 ? it.capacidad + (it.capacidad == 1 ? " persona" : " personas") : "N/A", cap, true, false);
            fila(grid, y, "Consumo", ComparadorRentCar::textoConsumo, con, true, false);
            fila(grid, y, "Combustible", it -> it.combustible.isBlank() ? (sinMotor(it) ? "No aplica" : "N/A") : it.combustible, null, true, false);

            // CARACTERÍSTICAS
            seccion(grid, y, "CARACTERÍSTICAS");
            fila(grid, y, "Tipo", it -> it.tipo.isBlank() ? "N/A" : capitalizar(it.tipo), null, true, false);
            fila(grid, y, "Categoría", it -> it.categoria.isBlank() ? "N/A" : it.categoria, null, true, false);
            fila(grid, y, "Año", it -> it.anio > 0 ? String.valueOf(it.anio) : "N/A", anio, true, false);
            fila(grid, y, "Transmisión", it -> it.transmision.isBlank() ? "N/A" : it.transmision, null, true, false);
            fila(grid, y, "Puertas", it -> it.puertas > 0 ? String.valueOf(it.puertas) : "N/A", null, true, false);
            fila(grid, y, "Kilometraje", it -> it.kilometraje > 0 ? miles(it.kilometraje) + " km" : "N/A", km, false, false);
            fila(grid, y, "Color", it -> it.color.isBlank() ? "N/A" : it.color, null, true, false);
            fila(grid, y, "Ciudad", it -> it.ciudad.isBlank() ? "N/A" : it.ciudad, null, true, false);

            // DISPONIBILIDAD + RESERVAR
            seccion(grid, y, "DISPONIBILIDAD");
            filaEstado(grid, y);
            filaReservar(grid, y);

            // columnas de relleno para que el contenido quede arriba
            GridBagConstraints relleno = new GridBagConstraints();
            relleno.gridx = 0;
            relleno.gridy = y[0];
            relleno.weighty = 1;
            relleno.gridwidth = n + 1;
            grid.add(Box.createVerticalGlue(), relleno);

            if (scroll != null) {
                scroll.getViewport().removeAll();
            }
            zonaGrid.removeAll();
            scroll = new JScrollPane(grid, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scroll.setBorder(BorderFactory.createLineBorder(BORDE));
            scroll.getViewport().setBackground(FONDO);
            scroll.getVerticalScrollBar().setUnitIncrement(18);
            scroll.getHorizontalScrollBar().setUnitIncrement(18);
            acciones.estilizar(scroll);
            zonaGrid.add(scroll, BorderLayout.CENTER);

            reconstruirPie();
            revalidate();
            repaint();
        }

        private String capitalizar(String s) {
            String t = s.trim().toLowerCase(Locale.ROOT);
            return t.isEmpty() ? t : Character.toUpperCase(t.charAt(0)) + t.substring(1);
        }

        // ---------------- PIE ----------------
        private void reconstruirPie() {
            pie.removeAll();
            JPanel rec = new JPanel();
            rec.setOpaque(false);
            rec.setLayout(new BoxLayout(rec, BoxLayout.Y_AXIS));

            int n = items.size();
            int mejor = -1;
            for (int i = 0; i < n; i++) {
                if (mejor < 0 || victorias[i] > victorias[mejor]
                        || (victorias[i] == victorias[mejor] && total(items.get(i)) < total(items.get(mejor)))) {
                    mejor = i;
                }
            }
            JLabel l1 = new JLabel();
            JLabel l2 = new JLabel();
            l1.setFont(new Font("SansSerif", Font.BOLD, 14));
            l2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            l2.setForeground(SECUNDARIO);
            if (n >= 2 && mejor >= 0 && victorias[mejor] > 0) {
                l1.setForeground(MEJOR_TEXTO);
                l1.setText("Mejor balance: " + items.get(mejor).nombre());
                l2.setText("Gana en " + victorias[mejor] + (victorias[mejor] == 1 ? " criterio" : " criterios")
                        + " · total " + dinero(total(items.get(mejor))) + " por " + dias + (dias == 1 ? " día" : " días")
                        + ". Los datos marcados con ✓ son los mejores de cada fila.");
            } else {
                l1.setForeground(TEXTO);
                l1.setText(n < 2 ? "Agrega otro vehículo para comparar" : "Los vehículos están muy parejos");
                l2.setText(n < 2 ? "Usa «+ Comparar» en las tarjetas del catálogo." : "No hay una opción que gane claramente en los criterios numéricos.");
            }
            rec.add(l1);
            rec.add(Box.createVerticalStrut(3));
            rec.add(l2);
            pie.add(rec, BorderLayout.CENTER);

            JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            botones.setOpaque(false);
            JButton vaciar = acciones.botonSecundario("Vaciar");
            vaciar.addActionListener(e -> {
                limpiar();
                acciones.seleccionCambio();
                cerrar.run();
            });
            JButton cerrarBtn = acciones.botonPrimario("Cerrar");
            cerrarBtn.addActionListener(e -> cerrar.run());
            botones.add(vaciar);
            botones.add(cerrarBtn);
            pie.add(botones, BorderLayout.EAST);
        }

        // ---------------- TARJETA DE VEHÍCULO ----------------
        private JPanel crearTarjetaVehiculo(int i) {
            Item it = items.get(i);
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setBackground(FONDO);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDE),
                    BorderFactory.createEmptyBorder(0, 0, 10, 0)));

            Miniatura mini = new Miniatura(it.imagen);
            mini.setAlignmentX(Component.CENTER_ALIGNMENT);
            p.add(mini);
            p.add(Box.createVerticalStrut(8));

            JLabel nombre = new JLabel(it.nombre(), SwingConstants.CENTER);
            nombre.setForeground(TEXTO);
            nombre.setFont(new Font("SansSerif", Font.BOLD, 14));
            nombre.setAlignmentX(Component.CENTER_ALIGNMENT);
            nombre.setToolTipText(it.nombre());
            p.add(nombre);

            JLabel sub = new JLabel(it.placa + "  ·  " + capitalizar(it.tipo), SwingConstants.CENTER);
            sub.setForeground(SECUNDARIO);
            sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);
            p.add(sub);
            p.add(Box.createVerticalStrut(6));

            JPanel etiquetas = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
            etiquetas.setOpaque(false);
            etiquetas.setAlignmentX(Component.CENTER_ALIGNMENT);
            if (items.size() >= 2) {
                if (premios[i][0]) etiquetas.add(pastilla("MÁS ECONÓMICO", MEJOR_TEXTO));
                if (premios[i][1]) etiquetas.add(pastilla("MAYOR CAPACIDAD", MEJOR_TEXTO));
                if (premios[i][2]) etiquetas.add(pastilla("MÁS EFICIENTE", MEJOR_TEXTO));
            }
            etiquetas.setPreferredSize(new Dimension(190, 46));
            etiquetas.setMaximumSize(new Dimension(1000, 46));
            p.add(etiquetas);

            JButton quitar = new JButton("Quitar");
            quitar.setForeground(SECUNDARIO);
            quitar.setFont(new Font("SansSerif", Font.PLAIN, 11));
            quitar.setContentAreaFilled(false);
            quitar.setBorderPainted(false);
            quitar.setFocusPainted(false);
            quitar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            quitar.setAlignmentX(Component.CENTER_ALIGNMENT);
            quitar.addActionListener(e -> quitarItem(it));
            p.add(quitar);
            return p;
        }

        private JLabel pastilla(String texto, Color color) {
            JLabel l = new JLabel(texto);
            l.setOpaque(true);
            l.setBackground(MEJOR_FONDO);
            l.setForeground(color);
            l.setFont(new Font("SansSerif", Font.BOLD, 9));
            l.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 7));
            return l;
        }

        private void quitarItem(Item it) {
            quitar(it.placa);
            items.remove(it);
            acciones.seleccionCambio();
            if (items.isEmpty()) {
                cerrar.run();
                return;
            }
            reconstruir();
        }

        // ---------------- FILAS ----------------
        private void agregar(JPanel grid, Component c, int x, int y, int ancho, Insets in) {
            GridBagConstraints g = new GridBagConstraints();
            g.gridx = x;
            g.gridy = y;
            g.gridwidth = ancho;
            g.fill = GridBagConstraints.BOTH;
            g.weightx = x == 0 ? 0 : 1;
            g.insets = in;
            grid.add(c, g);
        }

        private void celdaEtiqueta(JPanel grid, int y, String texto) {
            JLabel l = new JLabel(texto);
            l.setForeground(SECUNDARIO);
            l.setFont(new Font("SansSerif", Font.BOLD, 12));
            l.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
            l.setPreferredSize(new Dimension(150, 30));
            l.setOpaque(false);
            agregar(grid, l, 0, y, 1, new Insets(0, 0, 0, 0));
        }

        private void seccion(JPanel grid, int[] y, String titulo) {
            JLabel l = new JLabel(titulo);
            l.setForeground(ROJO);
            l.setFont(new Font("SansSerif", Font.BOLD, 10));
            l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE),
                    BorderFactory.createEmptyBorder(14, 12, 6, 0)));
            agregar(grid, l, 0, y[0]++, items.size() + 1, new Insets(0, 0, 0, 0));
        }

        private JPanel celda(String texto, boolean mejor, boolean grande, boolean negrita) {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(mejor ? MEJOR_FONDO : FONDO);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE),
                    BorderFactory.createEmptyBorder(grande ? 10 : 7, 8, grande ? 10 : 7, 8)));
            boolean na = texto.equals("N/A") || texto.equals("No aplica");
            JLabel l = new JLabel(mejor ? texto + "  ✓" : texto, SwingConstants.CENTER);
            l.setForeground(mejor ? MEJOR_TEXTO : (na ? SECUNDARIO : TEXTO));
            l.setFont(new Font("SansSerif", (mejor || negrita || grande) ? Font.BOLD : Font.PLAIN, grande ? 17 : 13));
            p.add(l, BorderLayout.CENTER);
            p.setPreferredSize(new Dimension(190, grande ? 46 : 34));
            return p;
        }

        private void fila(JPanel grid, int[] y, String etiqueta, Texto t, double[] ranking, boolean mayor, boolean grande) {
            celdaEtiqueta(grid, y[0], etiqueta);
            boolean[] m = ranking == null ? new boolean[items.size()] : mejores(ranking, mayor);
            for (int i = 0; i < items.size(); i++) {
                agregar(grid, celda(t.de(items.get(i)), m[i], grande, false), i + 1, y[0], 1, new Insets(0, 4, 0, 4));
            }
            y[0]++;
        }

        private void filaTotal(JPanel grid, int[] y, double[] tot, boolean[] mejores) {
            celdaEtiqueta(grid, y[0], "Total " + dias + (dias == 1 ? " día" : " días"));
            for (int i = 0; i < items.size(); i++) {
                agregar(grid, celda(dinero(tot[i]), mejores[i], true, true), i + 1, y[0], 1, new Insets(0, 4, 0, 4));
            }
            y[0]++;
        }

        private void filaDiferencia(JPanel grid, int[] y, double[] tot) {
            if (items.size() < 2) {
                return;
            }
            double min = Double.MAX_VALUE;
            for (double t : tot) {
                min = Math.min(min, t);
            }
            celdaEtiqueta(grid, y[0], "Frente al más barato");
            for (int i = 0; i < items.size(); i++) {
                double d = tot[i] - min;
                agregar(grid, celda(d <= 0 ? "—" : "+ " + dinero(d), false, false, false), i + 1, y[0], 1, new Insets(0, 4, 0, 4));
            }
            y[0]++;
        }

        private void filaEstado(JPanel grid, int[] y) {
            celdaEtiqueta(grid, y[0], "Estado");
            for (int i = 0; i < items.size(); i++) {
                Item it = items.get(i);
                boolean libre = it.disponible && "Disponible".equals(it.estado);
                JPanel c = celda(it.estado, libre, false, false);
                agregar(grid, c, i + 1, y[0], 1, new Insets(0, 4, 0, 4));
            }
            y[0]++;
        }

        private void filaReservar(JPanel grid, int[] y) {
            celdaEtiqueta(grid, y[0], "");
            for (int i = 0; i < items.size(); i++) {
                Item it = items.get(i);
                boolean libre = it.disponible && "Disponible".equals(it.estado);
                JPanel p = new JPanel(new BorderLayout());
                p.setOpaque(false);
                p.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
                JButton b = acciones.botonPrimario(libre ? "RESERVAR" : "NO DISPONIBLE");
                b.setEnabled(libre);
                b.addActionListener(e -> {
                    cerrar.run();
                    acciones.reservar(it.placa);
                });
                p.add(b, BorderLayout.CENTER);
                agregar(grid, p, i + 1, y[0], 1, new Insets(0, 4, 0, 4));
            }
            y[0]++;
        }
    }

    // =========================================================
    // COMPONENTES AUXILIARES
    // =========================================================
    /** Rejilla que se estira al ancho de la ventana, pero activa el scroll horizontal si no cabe. */
    private static final class Rejilla extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle r, int o, int d) {
            return 18;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle r, int o, int d) {
            return o == SwingConstants.VERTICAL ? r.height - 30 : r.width - 30;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return getParent() instanceof JViewport && getParent().getWidth() >= getPreferredSize().width;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    /** Foto del vehículo recortada para llenar el recuadro (o un fondo liso si no hay imagen). */
    private static final class Miniatura extends JComponent {

        private final BufferedImage img;

        Miniatura(BufferedImage img) {
            this.img = img;
            setPreferredSize(new Dimension(190, 112));
            setMinimumSize(new Dimension(100, 112));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth(), h = getHeight();
            g2.setColor(TemaRentCar.c(230, 234, 241));
            g2.fillRect(0, 0, w, h);
            if (img != null && img.getWidth() > 0 && img.getHeight() > 0) {
                double esc = Math.max((double) w / img.getWidth(), (double) h / img.getHeight());
                int dw = (int) Math.ceil(img.getWidth() * esc), dh = (int) Math.ceil(img.getHeight() * esc);
                g2.setClip(0, 0, w, h);
                g2.drawImage(img, (w - dw) / 2, (h - dh) / 2, dw, dh, null);
            } else {
                g2.setColor(TemaRentCar.c(98, 105, 118));
                g2.setStroke(new BasicStroke(1.4f));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                String t = "Sin foto";
                g2.drawString(t, (w - g2.getFontMetrics().stringWidth(t)) / 2, h / 2);
            }
            g2.dispose();
        }
    }
}