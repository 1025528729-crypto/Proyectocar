package com.mycompany.rentcar.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Estilo único para TODOS los botones del programa: esquinas redondeadas,
 * efecto al presionar y aspecto moderno. Se activa una sola vez con
 * BotonRentCarUI.instalar() y no hace falta tocar cada botón.
 */
public class BotonRentCarUI extends BasicButtonUI {

    private static final int ARCO = 14;
    private static Color BORDE = TemaRentCar.c(205, 211, 221);
    private static Color TEXTO = TemaRentCar.c(23, 27, 34);

    /** Activar antes de crear cualquier ventana. */
    public static void instalar() {
        UIManager.put("ButtonUI", BotonRentCarUI.class.getName());
    }

    public static ComponentUI createUI(JComponent c) {
        return new BotonRentCarUI();
    }

    private final PropertyChangeListener alCambiarBorde = e -> {
        AbstractButton b = (AbstractButton) e.getSource();
        if ("border".equals(e.getPropertyName())) {
            redondearBorde(b);
        } else if ("opaque".equals(e.getPropertyName())) {
            if (Boolean.TRUE.equals(e.getNewValue())) {
                b.putClientProperty(QUIERE_FONDO, Boolean.TRUE);
                forzarTransparente(b);
            } else if (!Boolean.TRUE.equals(b.getClientProperty(FORZANDO))) {
                b.putClientProperty(QUIERE_FONDO, Boolean.FALSE);
            }
        }
    };

    private static final String QUIERE_FONDO = "rentcar.quiereFondo";
    private static final String FORZANDO = "rentcar.forzando";

    /**
     * El botón es redondeado: si fuera opaco, Swing solo repintaría el botón y los píxeles suavizados
     * de las esquinas se irían acumulando (puntos blancos/rojos al pasar el mouse). Siendo no opaco,
     * Swing repinta primero el fondo de atrás y las esquinas quedan limpias.
     */
    private static void forzarTransparente(AbstractButton b) {
        b.putClientProperty(FORZANDO, Boolean.TRUE);
        b.setOpaque(false);
        b.putClientProperty(FORZANDO, null);
    }

    @Override
    protected void installListeners(AbstractButton b) {
        super.installListeners(b);
        b.addPropertyChangeListener("border", alCambiarBorde);
        b.addPropertyChangeListener("opaque", alCambiarBorde);
    }

    @Override
    protected void uninstallListeners(AbstractButton b) {
        b.removePropertyChangeListener("border", alCambiarBorde);
        b.removePropertyChangeListener("opaque", alCambiarBorde);
        super.uninstallListeners(b);
    }

    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        b.setRolloverEnabled(true);
        b.putClientProperty(QUIERE_FONDO, b.isOpaque());
        forzarTransparente(b);
        if (b.getBorder() instanceof UIResource) {
            // Botón "sin estilo propio": se le da un aspecto limpio por defecto
            b.setBorder(new javax.swing.plaf.BorderUIResource(BorderFactory.createEmptyBorder(9, 18, 9, 18)));
            b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }
        redondearBorde(b);
    }

    /** Los botones con borde de línea cuadrado pasan a tenerlo con esquinas redondeadas. */
    private static void redondearBorde(AbstractButton b) {
        Border borde = b.getBorder();
        if (borde instanceof LineBorder lb) {
            b.setBorder(new BordeRedondo(lb.getLineColor(), lb.getThickness(), ARCO));
        } else if (borde instanceof CompoundBorder cb && cb.getOutsideBorder() instanceof LineBorder lb) {
            b.setBorder(new CompoundBorder(
                    new BordeRedondo(lb.getLineColor(), lb.getThickness(), ARCO), cb.getInsideBorder()));
        }
    }

    private static boolean sinEstilo(AbstractButton b) {
        return b.getBackground() instanceof UIResource;
    }

    @Override
    public void update(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        if (b.isContentAreaFilled() && Boolean.TRUE.equals(b.getClientProperty(QUIERE_FONDO))) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fondo = sinEstilo(b) ? TemaRentCar.PANEL : b.getBackground();
            if (b.getModel().isPressed() && b.getModel().isArmed()) {
                fondo = fondo.darker();
            }
            if (!b.isEnabled()) {
                fondo = mezclar(fondo, Color.WHITE, 0.55f);
            }
            int w = b.getWidth(), h = b.getHeight();
            int arco = Math.min(ARCO, Math.max(6, h / 2 * 2));
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, w, h, arco, arco);
            if (sinEstilo(b)) {
                g2.setColor(BORDE);
                g2.drawRoundRect(0, 0, w - 1, h - 1, arco, arco);
                if (b.isEnabled()) b.setForeground(TEXTO);
            }
            g2.dispose();
        }
        paint(g, c);
    }

    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        // el efecto de presionado ya se dibuja en update()
    }

    private static Color mezclar(Color a, Color b, float t) {
        return new Color(
                Math.round(a.getRed() * (1 - t) + b.getRed() * t),
                Math.round(a.getGreen() * (1 - t) + b.getGreen() * t),
                Math.round(a.getBlue() * (1 - t) + b.getBlue() * t));
    }

    @Override
    public java.awt.Dimension getPreferredSize(JComponent c) {
        java.awt.Dimension d = super.getPreferredSize(c);
        if (d != null) {
            d.height = Math.max(d.height, 30);
        }
        return d;
    }

    /** Vuelve a calcular la paleta cuando cambia el tema claro/oscuro. */
    static void refrescarPaleta() {
        BORDE = TemaRentCar.c(205, 211, 221);
        TEXTO = TemaRentCar.c(23, 27, 34);
    }
}