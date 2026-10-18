package com.mycompany.rentcar.view;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.Icon;

/**
 * Logos de los métodos de pago.
 *
 * Si existe la imagen real en  src/main/resources/images/pagos/<clave>.png
 * (o .jpg), se usa esa. Si no existe, se dibuja una versión sencilla del logo
 * para que nunca quede un hueco.
 *
 * Claves:  visa, mastercard, amex, pse, nequi, daviplata, efectivo
 */
public final class LogosPago {

    private static final Map<String, BufferedImage> CACHE = new HashMap<>();
    private static final BufferedImage NO_HAY = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    private LogosPago() {
    }

    /** Icono con fondo de tarjeta, de la altura pedida. */
    public static Icon icono(String clave, int alto) {
        return new Logo(clave, alto);
    }

    // Claves de los logos de cada método (código usado en Alquiler.metodoPago)
    public static String[] clavesDe(String codigoMetodo) {
        if (codigoMetodo == null) return new String[0];
        switch (codigoMetodo) {
            case "TARJETA_CREDITO": return new String[]{"visa", "mastercard", "amex"};
            case "TARJETA_DEBITO":  return new String[]{"visa", "mastercard"};
            case "PSE":             return new String[]{"pse"};
            case "NEQUI":           return new String[]{"nequi"};
            case "DAVIPLATA":       return new String[]{"daviplata"};
            case "EFECTIVO":        return new String[]{"efectivo"};
            default:                return new String[0];
        }
    }

    // ---------------------------------------------------------
    // Imagen real (si el archivo existe)
    // ---------------------------------------------------------

    private static synchronized BufferedImage real(String clave) {

        BufferedImage img = CACHE.get(clave);

        if (img == null) {
            img = NO_HAY;
            for (String ext : new String[]{".png", ".jpg", ".jpeg"}) {
                try (InputStream in = LogosPago.class.getResourceAsStream("/images/pagos/" + clave + ext)) {
                    if (in != null) {
                        BufferedImage leida = ImageIO.read(in);
                        if (leida != null) {
                            img = leida;
                            break;
                        }
                    }
                } catch (Exception ignorada) {
                }
            }
            CACHE.put(clave, img);
        }

        return img == NO_HAY ? null : img;
    }

    // ---------------------------------------------------------
    // Icono
    // ---------------------------------------------------------

    private static final class Logo implements Icon {

        private final String clave;
        private final int alto;
        private final int ancho;

        Logo(String clave, int alto) {
            this.clave = clave;
            this.alto = alto;

            BufferedImage r = real(clave);

            if (r != null) {
                int interior = alto - 8;
                this.ancho = Math.max(alto, (int) Math.round(r.getWidth() * (interior / (double) r.getHeight())) + 12);
            } else {
                this.ancho = (int) Math.round(alto * proporcion(clave));
            }
        }

        private static double proporcion(String clave) {
            switch (clave) {
                case "visa":      return 1.85;
                case "mastercard": return 1.5;
                case "amex":      return 1.6;
                case "pse":       return 1.6;
                case "nequi":     return 2.1;
                case "daviplata": return 2.9;
                default:          return 1.5;
            }
        }

        @Override public int getIconWidth() { return ancho; }
        @Override public int getIconHeight() { return alto; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.translate(x, y);

            RoundRectangle2D chip = new RoundRectangle2D.Double(0, 0, ancho - 1, alto - 1, 8, 8);

            BufferedImage r = real(clave);

            if (r != null) {
                // logo real sobre fondo blanco
                g2.setColor(Color.WHITE);
                g2.fill(chip);
                int interior = alto - 8;
                int w = (int) Math.round(r.getWidth() * (interior / (double) r.getHeight()));
                g2.drawImage(r, (ancho - w) / 2, 4, w, interior, null);
                g2.setColor(new Color(0, 0, 0, 38));
                g2.draw(chip);
            } else {
                dibujar(g2, chip);
            }

            g2.dispose();
        }

        // ---- versiones dibujadas ----
        private void dibujar(Graphics2D g2, RoundRectangle2D chip) {

            double w = ancho, h = alto;
            Font base = new Font("SansSerif", Font.BOLD, 10);

            switch (clave) {

                case "mastercard": {
                    fondoBlanco(g2, chip);
                    double r = h * 0.30, cy = h / 2, cx = w / 2, dx = r * 0.62;
                    Ellipse2D a = new Ellipse2D.Double(cx - dx - r, cy - r, 2 * r, 2 * r);
                    Ellipse2D b = new Ellipse2D.Double(cx + dx - r, cy - r, 2 * r, 2 * r);
                    g2.setColor(new Color(0xEB001B));
                    g2.fill(a);
                    g2.setColor(new Color(0xF79E1B));
                    g2.fill(b);
                    Area cruce = new Area(a);
                    cruce.intersect(new Area(b));
                    g2.setColor(new Color(0xFF5F00));
                    g2.fill(cruce);
                    borde(g2, chip);
                    break;
                }

                case "visa": {
                    fondoBlanco(g2, chip);
                    g2.setColor(new Color(0x1A1F71));
                    g2.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, (int) Math.round(h * 0.62)));
                    centrar(g2, "VISA", w, h);
                    borde(g2, chip);
                    break;
                }

                case "amex": {
                    relleno(g2, chip, new Color(0x2E77BC));
                    g2.setColor(Color.WHITE);
                    g2.setFont(base.deriveFont(Font.BOLD, (float) (h * 0.34)));
                    centrar(g2, "AMEX", w, h);
                    break;
                }

                case "pse": {
                    fondoBlanco(g2, chip);
                    g2.setColor(new Color(0x0B4EA2));
                    g2.setFont(new Font("SansSerif", Font.BOLD, (int) Math.round(h * 0.52)));
                    centrar(g2, "PSE", w, h - 4);
                    g2.setColor(new Color(0x2EA3F2));
                    g2.fill(new RoundRectangle2D.Double(w * 0.22, h - 7, w * 0.56, 2.5, 2, 2));
                    borde(g2, chip);
                    break;
                }

                case "nequi": {
                    relleno(g2, chip, new Color(0x200020));
                    g2.setColor(new Color(0xDA0081));
                    g2.setFont(new Font("SansSerif", Font.BOLD, (int) Math.round(h * 0.50)));
                    centrar(g2, "nequi", w, h);
                    break;
                }

                case "daviplata": {
                    relleno(g2, chip, new Color(0xED1C24));
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, (int) Math.round(h * 0.42)));
                    centrar(g2, "daviplata", w, h);
                    break;
                }

                default: {   // efectivo: billete
                    fondoBlanco(g2, chip);
                    double bw = w * 0.66, bh = h * 0.46, bx = (w - bw) / 2, by = (h - bh) / 2;
                    g2.setColor(new Color(0xDDF3E4));
                    g2.fill(new RoundRectangle2D.Double(bx, by, bw, bh, 4, 4));
                    g2.setColor(new Color(0x2E9E5B));
                    g2.setStroke(new BasicStroke(1.4f));
                    g2.draw(new RoundRectangle2D.Double(bx, by, bw, bh, 4, 4));
                    g2.fill(new Ellipse2D.Double(w / 2 - bh * 0.28, h / 2 - bh * 0.28, bh * 0.56, bh * 0.56));
                    g2.fill(new Ellipse2D.Double(bx + 3, by + 3, 2.6, 2.6));
                    g2.fill(new Ellipse2D.Double(bx + bw - 5.6, by + bh - 5.6, 2.6, 2.6));
                    borde(g2, chip);
                }
            }
        }

        private void fondoBlanco(Graphics2D g2, RoundRectangle2D chip) {
            g2.setColor(Color.WHITE);
            g2.fill(chip);
        }

        private void relleno(Graphics2D g2, RoundRectangle2D chip, Color color) {
            g2.setColor(color);
            g2.fill(chip);
        }

        private void borde(Graphics2D g2, RoundRectangle2D chip) {
            g2.setColor(new Color(0, 0, 0, 38));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(chip);
        }

        private void centrar(Graphics2D g2, String texto, double w, double h) {
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(texto, (float) ((w - fm.stringWidth(texto)) / 2),
                    (float) ((h - fm.getHeight()) / 2 + fm.getAscent()));
        }
    }
}