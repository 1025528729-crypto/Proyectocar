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

    public static final Color FONDO = new Color(242, 244, 248);
    public static final Color PANEL = Color.WHITE;
    public static final Color CAMPO = new Color(238, 241, 246);
    public static final Color BORDE = new Color(211, 217, 226);
    public static final Color TEXTO = new Color(23, 27, 34);
    public static final Color GRIS = new Color(98, 105, 118);
    public static final Color ROJO = new Color(220, 38, 38);
    public static final Color ROJO_OSCURO = new Color(165, 25, 25);

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