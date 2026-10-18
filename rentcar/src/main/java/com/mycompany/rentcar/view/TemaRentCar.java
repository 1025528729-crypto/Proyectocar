package com.mycompany.rentcar.view;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * Paleta clara de RentCar (se ve bien con luz de día) y utilidades de imagen
 * compartidas por todas las ventanas.
 */
public final class TemaRentCar {

    public static Color FONDO = new Color(242, 244, 248);
    public static Color PANEL = Color.WHITE;
    public static Color CAMPO = new Color(238, 241, 246);
    public static Color BORDE = new Color(211, 217, 226);
    public static Color TEXTO = new Color(23, 27, 34);
    public static Color GRIS = new Color(98, 105, 118);
    public static Color ROJO = new Color(220, 38, 38);
    public static Color ROJO_OSCURO = new Color(165, 25, 25);

    private static boolean oscuro = false;
    private static final java.util.Map<Integer, Color> A_OSCURO = new java.util.HashMap<>();
    private static final String[] CLASES_CON_PALETA = {"MainPublicView", "VehiculoView", "AccesoView",
        "VentanaRentCar", "BotonRentCarUI", "MensajeRentCar", "DialogoInterno", "RecuperarPasswordDialog",
        "LoginView", "RegistroView"};

    static {
        // Equivalencia de los colores claros del proyecto en el modo oscuro
        mapa(242, 244, 248, 13, 15, 19);     // fondo de página
        mapa(255, 255, 255, 24, 27, 33);     // paneles / barra
        mapa(238, 241, 246, 24, 27, 33);     // tarjetas
        mapa(230, 234, 241, 33, 37, 45);     // campos
        mapa(238, 241, 246, 24, 27, 33);
        mapa(211, 217, 226, 52, 58, 68);     // bordes
        mapa(205, 211, 221, 52, 58, 68);
        mapa(23, 27, 34, 236, 239, 244);     // texto
        mapa(98, 105, 118, 150, 158, 172);   // texto secundario
        mapa(183, 183, 187, 110, 116, 128);
        mapa(253, 238, 238, 58, 30, 32);     // fondos rojizos suaves
        mapa(254, 228, 228, 70, 32, 34);
        mapa(255, 244, 244, 48, 28, 30);
        mapa(232, 168, 168, 140, 70, 70);
        mapa(224, 43, 49, 239, 68, 68);      // rojo de marca
        mapa(220, 38, 38, 235, 60, 60);
        mapa(165, 25, 25, 190, 40, 40);
        try {
            oscuro = java.util.prefs.Preferences.userNodeForPackage(TemaRentCar.class).getBoolean("oscuro", false);
        } catch (Exception e) {
            oscuro = false;
        }
        pintarPaleta();
    }

    private static void mapa(int r, int g, int b, int r2, int g2, int b2) {
        A_OSCURO.put((r << 16) | (g << 8) | b, new Color(r2, g2, b2));
    }

    private static void pintarPaleta() {
        if (oscuro) {
            FONDO = new Color(13, 15, 19);
            PANEL = new Color(24, 27, 33);
            CAMPO = new Color(33, 37, 45);
            BORDE = new Color(52, 58, 68);
            TEXTO = new Color(236, 239, 244);
            GRIS = new Color(150, 158, 172);
            ROJO = new Color(235, 60, 60);
            ROJO_OSCURO = new Color(190, 40, 40);
        } else {
            FONDO = new Color(242, 244, 248);
            PANEL = Color.WHITE;
            CAMPO = new Color(238, 241, 246);
            BORDE = new Color(211, 217, 226);
            TEXTO = new Color(23, 27, 34);
            GRIS = new Color(98, 105, 118);
            ROJO = new Color(220, 38, 38);
            ROJO_OSCURO = new Color(165, 25, 25);
        }
        logoCache = null;   // el logo se vuelve a teñir con el color de texto del tema
    }

    public static boolean isOscuro() {
        return oscuro;
    }

    /** Cambia de tema y recuerda la elección. Después hay que reconstruir la ventana. */
    public static void aplicar(boolean modoOscuro) {
        oscuro = modoOscuro;
        try {
            java.util.prefs.Preferences.userNodeForPackage(TemaRentCar.class).putBoolean("oscuro", modoOscuro);
        } catch (Exception ignorada) {
        }
        pintarPaleta();
        for (String nombre : CLASES_CON_PALETA) {
            try {
                Class<?> k = Class.forName(TemaRentCar.class.getPackageName() + "." + nombre);
                java.lang.reflect.Method m = k.getDeclaredMethod("refrescarPaleta");
                m.setAccessible(true);
                m.invoke(null);
            } catch (Exception ignorada) {
                // la clase no tiene paleta estática
            }
        }
    }

    /** Color del tema: en modo claro es el mismo; en oscuro se cambia por su equivalente. */
    public static Color c(int r, int g, int b) {
        return c(r, g, b, 255);
    }

    public static Color c(int r, int g, int b, int a) {
        if (oscuro) {
            Color m = A_OSCURO.get((r << 16) | (g << 8) | b);
            if (m != null) {
                return a == 255 ? m : new Color(m.getRed(), m.getGreen(), m.getBlue(), a);
            }
        }
        if (oscuro && r >= 200 && g >= 200 && b >= 200
                && Math.max(r, Math.max(g, b)) - Math.min(r, Math.min(g, b)) <= 30) {
            // gris casi blanco que no está en la tabla (líneas divisorias, etc.): se oscurece
            return a == 255 ? new Color(52, 58, 68) : new Color(52, 58, 68, a);
        }
        return a == 255 ? new Color(r, g, b) : new Color(r, g, b, a);
    }

    /**
     * Combo con los colores del tema: muestra siempre el valor elegido (con "…" y tooltip si no cabe),
     * resalta la opción seleccionada y abre la lista más ancha que el campo para leer los nombres completos.
     */
    public static void estilizarCombo(final javax.swing.JComboBox<String> combo) {
        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override protected javax.swing.JButton createArrowButton() {
                javax.swing.JButton b = new javax.swing.plaf.basic.BasicArrowButton(
                        javax.swing.SwingConstants.SOUTH, CAMPO, CAMPO, GRIS, CAMPO);
                b.setBorder(javax.swing.BorderFactory.createEmptyBorder());
                return b;
            }

            @Override protected javax.swing.plaf.basic.ComboPopup createPopup() {
                javax.swing.plaf.basic.BasicComboPopup popup = new javax.swing.plaf.basic.BasicComboPopup(comboBox) {
                    @Override protected java.awt.Rectangle computePopupBounds(int px, int py, int pw, int ph) {
                        java.awt.FontMetrics fm = comboBox.getFontMetrics(comboBox.getFont());
                        int ancho = pw;
                        for (int i = 0; i < comboBox.getItemCount(); i++) {
                            ancho = Math.max(ancho, fm.stringWidth(String.valueOf(comboBox.getItemAt(i))) + 48);
                        }
                        return super.computePopupBounds(px, py, ancho, ph);
                    }
                };
                popup.setBorder(javax.swing.BorderFactory.createLineBorder(BORDE));
                return popup;
            }

            @Override public void paintCurrentValueBackground(Graphics g, java.awt.Rectangle b, boolean foco) {
                g.setColor(CAMPO);
                g.fillRect(b.x, b.y, b.width, b.height);
            }

            @Override public void paintCurrentValue(Graphics g, java.awt.Rectangle b, boolean foco) {
                Object sel = comboBox.getSelectedItem();
                if (sel == null) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(comboBox.getFont());
                g2.setColor(TEXTO);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                String t = sel.toString();
                int max = b.width - 10;
                if (fm.stringWidth(t) > max) {
                    while (t.length() > 1 && fm.stringWidth(t + "…") > max) t = t.substring(0, t.length() - 1);
                    t = t + "…";
                }
                g2.drawString(t, b.x + 6, b.y + (b.height + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        });

        combo.setBackground(CAMPO);
        combo.setForeground(TEXTO);
        combo.setBorder(javax.swing.BorderFactory.createLineBorder(BORDE));
        combo.setMaximumRowCount(10);
        combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                    int index, boolean seleccionado, boolean foco) {
                javax.swing.JLabel l = (javax.swing.JLabel) super.getListCellRendererComponent(list, value, index, seleccionado, foco);
                l.setText(value == null ? "" : value.toString());
                l.setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10));
                l.setOpaque(true);
                boolean resaltar = seleccionado && index >= 0;
                l.setBackground(resaltar ? ROJO : PANEL);
                l.setForeground(resaltar ? Color.WHITE : TEXTO);
                return l;
            }
        });
        combo.addActionListener(e -> combo.setToolTipText(
                combo.getSelectedItem() == null ? null : combo.getSelectedItem().toString()));
        combo.setToolTipText(combo.getSelectedItem() == null ? null : combo.getSelectedItem().toString());
    }

    /** Escala cualquier imagen con calidad (en pasos, sin pixelarse). */
    public static java.awt.Image escalarImagen(java.awt.Image origen, int ancho, int alto) {
        BufferedImage b;
        if (origen instanceof BufferedImage) {
            b = (BufferedImage) origen;
        } else {
            int w = Math.max(1, origen.getWidth(null)), h = Math.max(1, origen.getHeight(null));
            b = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = b.createGraphics();
            g2.drawImage(origen, 0, 0, null);
            g2.dispose();
        }
        return escalar(b, Math.max(1, ancho), Math.max(1, alto));
    }

    private static BufferedImage logoCache;

    private TemaRentCar() {
    }

    /** Logo del proyecto (blanco sobre transparente) pintado en color oscuro y sin márgenes. */
    public static synchronized BufferedImage logoOscuro() {
        if (logoCache != null) {
            return logoCache;
        }
        try {
            URL url = TemaRentCar.class.getResource("/images/rentcar_logo.png");
            BufferedImage origen = url == null ? null : ImageIO.read(url);
            if (origen == null) {
                return null;
            }
            int w = origen.getWidth(), h = origen.getHeight();
            int minX = w, minY = h, maxX = -1, maxY = -1;
            BufferedImage teñida = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            int rgbTinta = TEXTO.getRGB() & 0x00ffffff;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int a = (origen.getRGB(x, y) >>> 24) & 0xff;
                    teñida.setRGB(x, y, (a << 24) | rgbTinta);
                    if (a > 40) {
                        if (x < minX) minX = x;
                        if (x > maxX) maxX = x;
                        if (y < minY) minY = y;
                        if (y > maxY) maxY = y;
                    }
                }
            }
            if (maxX > minX && maxY > minY) {
                BufferedImage recorte = new BufferedImage(maxX - minX + 1, maxY - minY + 1, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = recorte.createGraphics();
                g2.drawImage(teñida, -minX, -minY, null);
                g2.dispose();
                teñida = recorte;
            }
            logoCache = teñida;
        } catch (Exception e) {
            logoCache = null;
        }
        return logoCache;
    }

    /** Icono del logo que cabe en maxAncho x maxAlto y se dibuja nítido en cualquier pantalla. */
    public static Icon logoIcono(int maxAncho, int maxAlto) {
        BufferedImage img = logoOscuro();
        if (img == null) {
            return null;
        }
        double escala = Math.min((double) maxAncho / img.getWidth(), (double) maxAlto / img.getHeight());
        return new IconoNitido(img,
                Math.max(1, (int) Math.round(img.getWidth() * escala)),
                Math.max(1, (int) Math.round(img.getHeight() * escala)));
    }

    /** Icono que se redibuja a la resolución real de la pantalla (no se ve pixelado). */
    public static class IconoNitido implements Icon {

        private final BufferedImage origen;
        private final int ancho, alto;
        private BufferedImage cache;
        private int cacheW = -1, cacheH = -1;

        public IconoNitido(BufferedImage origen, int ancho, int alto) {
            this.origen = origen;
            this.ancho = ancho;
            this.alto = alto;
        }

        @Override
        public int getIconWidth() {
            return ancho;
        }

        @Override
        public int getIconHeight() {
            return alto;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            AffineTransform t = g2.getTransform();
            double sx = t.getScaleX() > 0 ? t.getScaleX() : 1;
            double sy = t.getScaleY() > 0 ? t.getScaleY() : 1;
            int pw = Math.max(1, (int) Math.round(ancho * sx));
            int ph = Math.max(1, (int) Math.round(alto * sy));
            if (cache == null || cacheW != pw || cacheH != ph) {
                cache = escalar(origen, pw, ph);
                cacheW = pw;
                cacheH = ph;
            }
            g2.scale(1.0 / sx, 1.0 / sy);
            g2.drawImage(cache, (int) Math.round(x * sx), (int) Math.round(y * sy), null);
            g2.dispose();
        }
    }

    public static BufferedImage escalar(BufferedImage origen, int anchoFinal, int altoFinal) {
        BufferedImage actual = origen;
        int w = origen.getWidth(), h = origen.getHeight();
        while (w / 2 >= anchoFinal && h / 2 >= altoFinal) {
            w /= 2;
            h /= 2;
            actual = redimensionar(actual, w, h);
        }
        return redimensionar(actual, anchoFinal, altoFinal);
    }

    private static BufferedImage redimensionar(BufferedImage origen, int ancho, int alto) {
        BufferedImage destino = new BufferedImage(Math.max(1, ancho), Math.max(1, alto), BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g2 = destino.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(origen, 0, 0, destino.getWidth(), destino.getHeight(), null);
        g2.dispose();
        return destino;
    }
}