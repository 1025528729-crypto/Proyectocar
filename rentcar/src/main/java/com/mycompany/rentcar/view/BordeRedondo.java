package com.mycompany.rentcar.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.border.AbstractBorder;

/** Borde de una línea con esquinas redondeadas (bien visibles y suaves). */
public class BordeRedondo extends AbstractBorder {

    private final Color color;
    private final int grosor;
    private final int arco;

    public BordeRedondo(Color color, int grosor, int arco) {
        this.color = color;
        this.grosor = grosor;
        this.arco = arco;
    }

    public Color getColor() { return color; }
    public int getGrosor() { return grosor; }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(grosor));
        float m = grosor / 2f;
        g2.draw(new java.awt.geom.RoundRectangle2D.Float(x + m, y + m, w - grosor, h - grosor, arco, arco));
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(grosor, grosor, grosor, grosor);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets i) {
        i.set(grosor, grosor, grosor, grosor);
        return i;
    }
}