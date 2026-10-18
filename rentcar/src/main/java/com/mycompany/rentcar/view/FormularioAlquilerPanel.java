package com.mycompany.rentcar.view;

import com.mycompany.rentcar.model.Alquiler;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

/**
 * Formulario para rentar un vehículo: datos de quien alquila y forma de pago.
 * Por seguridad el número completo de la tarjeta y el CVV NUNCA salen de este
 * panel: solo se entregan los últimos 4 dígitos.
 */
public class FormularioAlquilerPanel extends JPanel {

    public interface Escucha {
        void confirmar(Alquiler datos);
        void cancelar();
    }

    private static final String[] METODOS = {
        "Tarjeta de crédito", "Tarjeta débito", "PSE", "Nequi", "Daviplata", "Efectivo al recoger"
    };
    private static final String[] SUBTITULOS = {
        "Visa, Mastercard, Amex", "Visa, Mastercard", "Transferencia bancaria", "Pago con tu celular", "Pago con tu celular", "Pagas al recoger"
    };
    private static final String[] CODIGOS = {"TARJETA_CREDITO", "TARJETA_DEBITO", "PSE", "NEQUI", "DAVIPLATA", "EFECTIVO"};

    private final Alquiler base;
    private final Escucha escucha;

    private final Color cFondoCampo = TemaRentCar.c(230, 234, 241);
    private final Color cBorde = TemaRentCar.c(211, 217, 226);
    private final Color cTexto = TemaRentCar.c(23, 27, 34);
    private final Color cGris = TemaRentCar.c(98, 105, 118);
    private final Color cRojo = TemaRentCar.c(224, 43, 49);
    private final Color cRojoClaro = TemaRentCar.c(244, 61, 68);

    private final JTextField txtNombres = new JTextField(1);
    private final JTextField txtApellidos = new JTextField(1);
    private final JTextField txtDocumento = new JTextField(1);
    private final JTextField txtEmail = new JTextField(1);
    private final JTextField txtTelefono = new JTextField(1);
    private int metodo = 0;
    private final JToggleButton[] botonesMetodo = new JToggleButton[METODOS.length];
    private final JTextField txtNumero = new JTextField(1);
    private final JTextField txtTitular = new JTextField(1);
    private final JTextField txtVence = new JTextField(1);
    private final JPasswordField txtCvv = new JPasswordField(1);
    private final JLabel lblMarca = new JLabel(" ");
    private final JCheckBox chkTerminos = new JCheckBox("Acepto los términos del alquiler y la política de cancelación");
    private final JLabel lblError = new JLabel(" ");
    private final JLabel lblNotaPago = new JLabel();
    private final JPanel panelTarjeta = new JPanel();
    private final JButton btnConfirmar = new JButton();
    private final JScrollPane scroll;

    public FormularioAlquilerPanel(Alquiler base, Escucha escucha) {
        super(new BorderLayout());
        this.base = base;
        this.escucha = escucha;
        setOpaque(false);

        // ----- contenido que se desplaza -----
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(BorderFactory.createEmptyBorder(0, 32, 8, 32));

        contenido.add(crearResumen());
        contenido.add(Box.createVerticalStrut(18));

        contenido.add(titulo("DATOS DE QUIEN ALQUILA"));
        contenido.add(Box.createVerticalStrut(8));
        contenido.add(fila(campo("Nombres", txtNombres), campo("Apellidos", txtApellidos)));
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(fila(campo("Cédula o pasaporte", txtDocumento), campo("Celular", txtTelefono)));
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(fila(campo("Correo electrónico", txtEmail)));
        contenido.add(Box.createVerticalStrut(20));

        contenido.add(titulo("FORMA DE PAGO"));
        contenido.add(Box.createVerticalStrut(8));
        contenido.add(crearSelectorMetodos());
        contenido.add(Box.createVerticalStrut(10));

        construirPanelTarjeta();
        contenido.add(panelTarjeta);

        lblNotaPago.setForeground(cGris);
        lblNotaPago.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblNotaPago.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(lblNotaPago);
        contenido.add(Box.createVerticalStrut(14));

        chkTerminos.setOpaque(false);
        chkTerminos.setForeground(cTexto);
        chkTerminos.setFont(new Font("SansSerif", Font.PLAIN, 13));
        chkTerminos.setFocusPainted(false);
        chkTerminos.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(chkTerminos);

        JPanel envoltura = new JPanel(new BorderLayout());
        envoltura.setOpaque(false);
        envoltura.add(contenido, BorderLayout.NORTH);

        scroll = new JScrollPane(envoltura,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(24);

        // ----- encabezado -----
        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBorder(BorderFactory.createEmptyBorder(22, 32, 14, 32));
        JLabel l1 = new JLabel("RENTAR VEHÍCULO");
        l1.setForeground(cRojoClaro);
        l1.setFont(new Font("SansSerif", Font.BOLD, 12));
        l1.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l2 = new JLabel("Completa tu reserva");
        l2.setForeground(cTexto);
        l2.setFont(new Font("SansSerif", Font.BOLD, 26));
        l2.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.add(l1);
        encabezado.add(Box.createVerticalStrut(5));
        encabezado.add(l2);

        // ----- pie con error y botones -----
        lblError.setForeground(cRojoClaro);
        lblError.setFont(new Font("SansSerif", Font.BOLD, 12));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setForeground(cTexto);
        btnCancelar.setBackground(cFondoCampo);
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cBorde), BorderFactory.createEmptyBorder(10, 26, 10, 26)));
        btnCancelar.addActionListener(e -> escucha.cancelar());

        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setBackground(cRojo);
        btnConfirmar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConfirmar.setBorder(BorderFactory.createEmptyBorder(11, 28, 11, 28));
        btnConfirmar.addActionListener(e -> intentarConfirmar());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botones.setOpaque(false);
        botones.add(btnCancelar);
        botones.add(btnConfirmar);

        JPanel pie = new JPanel(new BorderLayout(0, 8));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(10, 32, 22, 32));
        pie.add(lblError, BorderLayout.NORTH);
        pie.add(botones, BorderLayout.SOUTH);

        add(encabezado, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(pie, BorderLayout.SOUTH);

        configurarCampos();
        metodoCambio();
    }

    /** Para que la ventana le ponga a la barra de desplazamiento el mismo estilo que las demás. */
    public JScrollPane getScroll() {
        return scroll;
    }

    // ------------------------------------------------------------------
    // Piezas visuales
    // ------------------------------------------------------------------
    private JPanel crearResumen() {
        JPanel r = new JPanel(new BorderLayout(14, 0));
        r.setBackground(cFondoCampo);
        r.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cBorde),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        JLabel v = new JLabel((base.marca + " " + base.modelo).trim() + "  ·  " + base.placa);
        v.setForeground(cTexto);
        v.setFont(new Font("SansSerif", Font.BOLD, 16));
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        JLabel d = new JLabel(base.lugar + "  ·  " + base.recogida.format(f) + " → " + base.devolucion.format(f)
                + "  ·  " + base.dias + (base.dias == 1 ? " día" : " días"));
        d.setForeground(cGris);
        d.setFont(new Font("SansSerif", Font.PLAIN, 13));
        izq.add(v);
        izq.add(Box.createVerticalStrut(4));
        izq.add(d);

        JPanel der = new JPanel();
        der.setOpaque(false);
        der.setLayout(new BoxLayout(der, BoxLayout.Y_AXIS));
        JLabel t1 = new JLabel("TOTAL A PAGAR");
        t1.setForeground(cGris);
        t1.setFont(new Font("SansSerif", Font.BOLD, 10));
        t1.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel t2 = new JLabel(dinero(base.total));
        t2.setForeground(cTexto);
        t2.setFont(new Font("SansSerif", Font.BOLD, 22));
        t2.setAlignmentX(Component.RIGHT_ALIGNMENT);
        der.add(t1);
        der.add(t2);

        r.add(izq, BorderLayout.CENTER);
        r.add(der, BorderLayout.EAST);
        return r;
    }

    /** Opciones de pago a la vista: se elige con un clic, sin menú desplegable. */
    private JPanel crearSelectorMetodos() {
        JPanel grilla = new JPanel(new GridLayout(0, 3, 10, 10));
        grilla.setOpaque(false);
        grilla.setAlignmentX(Component.LEFT_ALIGNMENT);
        ButtonGroup grupo = new ButtonGroup();
        for (int i = 0; i < METODOS.length; i++) {
            final int indice = i;
            JToggleButton b = new JToggleButton("<html><b>" + METODOS[i] + "</b><br><span style='font-size:9px'>"
                    + SUBTITULOS[i] + "</span></html>") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean sel = isSelected();
                    g2.setColor(sel ? cRojo : cFondoCampo);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                    g2.setColor(sel ? cRojo : cBorde);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                    // logos reales del método de pago (arriba a la izquierda)
                    int xLogo = 12;
                    for (String clave : LogosPago.clavesDe(CODIGOS[indice])) {
                        javax.swing.Icon logo = LogosPago.icono(clave, 24);
                        logo.paintIcon(this, g2, xLogo, 10);
                        xLogo += logo.getIconWidth() + 6;
                    }

                    g2.dispose();
                    setForeground(sel ? Color.WHITE : cTexto);
                    super.paintComponent(g);
                }
            };
            b.setFont(new Font("SansSerif", Font.PLAIN, 12));
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setOpaque(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            // el texto va debajo de los logos
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setVerticalAlignment(SwingConstants.TOP);
            b.setBorder(BorderFactory.createEmptyBorder(42, 12, 8, 8));
            b.setPreferredSize(new Dimension(100, 92));
            b.setSelected(i == 0);
            b.addActionListener(e -> {
                metodo = indice;
                metodoCambio();
            });
            grupo.add(b);
            botonesMetodo[i] = b;
            grilla.add(b);
        }
        grilla.setMaximumSize(new Dimension(Integer.MAX_VALUE, 204));
        return grilla;
    }

    private JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(cRojoClaro);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel campo(String etiqueta, JComponent c) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(etiqueta);
        l.setForeground(cGris);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        p.add(l, BorderLayout.NORTH);
        if (c instanceof JTextComponent) estilizar((JTextComponent) c);
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    private JPanel fila(JComponent... celdas) {
        JPanel f = new JPanel(new GridLayout(1, celdas.length, 12, 0));
        f.setOpaque(false);
        for (JComponent c : celdas) f.add(c);
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        return f;
    }

    private void estilizar(JTextComponent c) {
        c.setBackground(cFondoCampo);
        c.setForeground(cTexto);
        c.setCaretColor(cTexto);
        c.setFont(new Font("SansSerif", Font.PLAIN, 14));
        Border normal = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cBorde), BorderFactory.createEmptyBorder(8, 10, 8, 10));
        Border foco = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cRojo), BorderFactory.createEmptyBorder(8, 10, 8, 10));
        c.setBorder(normal);
        c.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { c.setBorder(foco); }
            @Override public void focusLost(FocusEvent e) { c.setBorder(normal); }
        });
    }

    private void construirPanelTarjeta() {
        panelTarjeta.setOpaque(false);
        panelTarjeta.setLayout(new BoxLayout(panelTarjeta, BoxLayout.Y_AXIS));
        panelTarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblMarca.setForeground(cRojoClaro);
        lblMarca.setFont(new Font("SansSerif", Font.BOLD, 11));
        JPanel num = campo("Número de la tarjeta", txtNumero);
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        // la marca detectada (VISA, MASTERCARD...) se muestra junto a la etiqueta
        JLabel etiqueta = (JLabel) ((BorderLayout) num.getLayout()).getLayoutComponent(BorderLayout.NORTH);
        num.remove(etiqueta);
        cab.add(etiqueta, BorderLayout.WEST);
        cab.add(lblMarca, BorderLayout.EAST);
        num.add(cab, BorderLayout.NORTH);

        panelTarjeta.add(fila(num));
        panelTarjeta.add(Box.createVerticalStrut(10));
        panelTarjeta.add(fila(campo("Nombre del titular (como aparece en la tarjeta)", txtTitular)));
        panelTarjeta.add(Box.createVerticalStrut(10));
        panelTarjeta.add(fila(campo("Vence (MM/AA)", txtVence), campo("CVV", txtCvv)));
        panelTarjeta.add(Box.createVerticalStrut(8));
    }

    // ------------------------------------------------------------------
    // Comportamiento
    // ------------------------------------------------------------------
    private void configurarCampos() {
        solo((AbstractDocument) txtDocumento.getDocument(), "[A-Za-z0-9]", 12);
        solo((AbstractDocument) txtTelefono.getDocument(), "[0-9+ ]", 15);
        solo((AbstractDocument) txtCvv.getDocument(), "[0-9]", 4);

        ((AbstractDocument) txtNumero.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override public void replace(FilterBypass fb, int off, int len, String txt, javax.swing.text.AttributeSet a)
                    throws BadLocationException {
                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String nuevo = actual.substring(0, off) + (txt == null ? "" : txt) + actual.substring(off + len);
                String digitos = nuevo.replaceAll("\\D", "");
                if (digitos.length() > 19) digitos = digitos.substring(0, 19);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < digitos.length(); i++) {
                    if (i > 0 && i % 4 == 0) sb.append(' ');
                    sb.append(digitos.charAt(i));
                }
                fb.replace(0, actual.length(), sb.toString(), a);
                lblMarca.setText(marcaTarjeta(digitos));
            }
            @Override public void insertString(FilterBypass fb, int off, String txt, javax.swing.text.AttributeSet a)
                    throws BadLocationException { replace(fb, off, 0, txt, a); }
            @Override public void remove(FilterBypass fb, int off, int len) throws BadLocationException {
                replace(fb, off, len, "", null);
            }
        });

        ((AbstractDocument) txtVence.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override public void replace(FilterBypass fb, int off, int len, String txt, javax.swing.text.AttributeSet a)
                    throws BadLocationException {
                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String nuevo = actual.substring(0, off) + (txt == null ? "" : txt) + actual.substring(off + len);
                String d = nuevo.replaceAll("\\D", "");
                if (d.length() > 4) d = d.substring(0, 4);
                String f = d.length() > 2 ? d.substring(0, 2) + "/" + d.substring(2) : d;
                fb.replace(0, actual.length(), f, a);
            }
            @Override public void insertString(FilterBypass fb, int off, String txt, javax.swing.text.AttributeSet a)
                    throws BadLocationException { replace(fb, off, 0, txt, a); }
            @Override public void remove(FilterBypass fb, int off, int len) throws BadLocationException {
                replace(fb, off, len, "", null);
            }
        });
    }

    private void solo(AbstractDocument doc, String permitido, int max) {
        doc.setDocumentFilter(new DocumentFilter() {
            @Override public void replace(FilterBypass fb, int off, int len, String txt, javax.swing.text.AttributeSet a)
                    throws BadLocationException {
                if (txt == null) txt = "";
                StringBuilder sb = new StringBuilder();
                for (char ch : txt.toCharArray()) if (String.valueOf(ch).matches(permitido)) sb.append(ch);
                int espacio = max - (fb.getDocument().getLength() - len);
                if (espacio <= 0 && sb.length() > 0) return;
                if (sb.length() > espacio) sb.setLength(Math.max(0, espacio));
                super.replace(fb, off, len, sb.toString(), a);
            }
            @Override public void insertString(FilterBypass fb, int off, String txt, javax.swing.text.AttributeSet a)
                    throws BadLocationException { replace(fb, off, 0, txt, a); }
        });
    }

    private boolean esTarjeta() {
        return metodo <= 1;
    }

    private void metodoCambio() {
        int i = metodo;
        panelTarjeta.setVisible(i <= 1);
        switch (i) {
            case 2:
                lblNotaPago.setText("Al confirmar te llevaremos a tu banco para completar el pago por PSE.");
                btnConfirmar.setText("Confirmar y pagar " + dinero(base.total));
                break;
            case 3:
            case 4:
                lblNotaPago.setText("Te enviaremos la solicitud de pago al celular que escribiste arriba.");
                btnConfirmar.setText("Confirmar y pagar " + dinero(base.total));
                break;
            case 5:
                lblNotaPago.setText("Pagas el valor total al recoger el vehículo. Lleva tu documento y licencia.");
                btnConfirmar.setText("Confirmar reserva");
                break;
            default:
                lblNotaPago.setText("Solo guardamos los últimos 4 dígitos de tu tarjeta; nunca el número completo ni el CVV.");
                btnConfirmar.setText("Pagar " + dinero(base.total));
        }
        lblError.setText(" ");
        revalidate();
        repaint();
    }

    private void error(String mensaje, JComponent foco) {
        lblError.setText(mensaje);
        if (foco != null) {
            foco.requestFocusInWindow();
            Rectangle r = SwingUtilities.convertRectangle(foco.getParent(), foco.getBounds(), scroll.getViewport().getView());
            r.grow(0, 40);
            ((JComponent) scroll.getViewport().getView()).scrollRectToVisible(r);
        }
    }

    private void intentarConfirmar() {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String documento = txtDocumento.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();

        if (nombres.length() < 2 || !nombres.matches("[\\p{L} .'-]+")) {
            error("Escribe tus nombres (solo letras).", txtNombres); return;
        }
        if (apellidos.length() < 2 || !apellidos.matches("[\\p{L} .'-]+")) {
            error("Escribe tus apellidos (solo letras).", txtApellidos); return;
        }
        if (documento.length() < 5) {
            error("El documento debe tener entre 5 y 12 caracteres.", txtDocumento); return;
        }
        if (telefono.replaceAll("\\D", "").length() < 7) {
            error("Escribe un número de celular válido.", txtTelefono); return;
        }
        if (!email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")) {
            error("Escribe un correo electrónico válido.", txtEmail); return;
        }

        String ultimos4 = null;
        String titular = null;
        if (esTarjeta()) {
            String numero = txtNumero.getText().replaceAll("\\D", "");
            if (numero.length() < 13 || !luhn(numero)) {
                error("El número de la tarjeta no es válido.", txtNumero); return;
            }
            titular = txtTitular.getText().trim();
            if (titular.length() < 3) {
                error("Escribe el nombre del titular de la tarjeta.", txtTitular); return;
            }
            if (!vencimientoValido(txtVence.getText())) {
                error("La fecha de vencimiento no es válida o la tarjeta está vencida.", txtVence); return;
            }
            int largoCvv = numero.startsWith("34") || numero.startsWith("37") ? 4 : 3;
            if (txtCvv.getPassword().length != largoCvv) {
                error("El CVV debe tener " + largoCvv + " dígitos.", txtCvv); return;
            }
            ultimos4 = numero.substring(numero.length() - 4);
        }
        if (!chkTerminos.isSelected()) {
            error("Debes aceptar los términos para continuar.", chkTerminos); return;
        }

        Alquiler a = new Alquiler();
        a.usuarioId = base.usuarioId;
        a.placa = base.placa;
        a.marca = base.marca;
        a.modelo = base.modelo;
        a.tipo = base.tipo;
        a.foto = base.foto;
        a.lugar = base.lugar;
        a.recogida = base.recogida;
        a.devolucion = base.devolucion;
        a.dias = base.dias;
        a.total = base.total;
        a.nombres = nombres;
        a.apellidos = apellidos;
        a.documento = documento;
        a.telefono = telefono;
        a.email = email;
        a.metodoPago = CODIGOS[metodo];
        a.titular = titular;
        a.ultimos4 = ultimos4;

        // Se borran los datos sensibles de la pantalla apenas se usan
        txtNumero.setText("");
        txtCvv.setText("");

        lblError.setText(" ");
        btnConfirmar.setEnabled(false);
        btnConfirmar.setText("Procesando…");
        escucha.confirmar(a);
    }

    /** Se llama si el guardado falló, para que el usuario pueda corregir y volver a intentar. */
    public void mostrarFallo(String mensaje) {
        btnConfirmar.setEnabled(true);
        metodoCambio();
        lblError.setText("<html>" + mensaje.replace("&", "&amp;").replace("<", "&lt;") + "</html>");
    }

    // ------------------------------------------------------------------
    // Validaciones y formato
    // ------------------------------------------------------------------
    static boolean luhn(String digitos) {
        int suma = 0;
        boolean doble = false;
        for (int i = digitos.length() - 1; i >= 0; i--) {
            int d = digitos.charAt(i) - '0';
            if (doble) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            suma += d;
            doble = !doble;
        }
        return suma % 10 == 0;
    }

    static boolean vencimientoValido(String texto) {
        if (!texto.matches("\\d{2}/\\d{2}")) return false;
        int mes = Integer.parseInt(texto.substring(0, 2));
        int anio = 2000 + Integer.parseInt(texto.substring(3));
        if (mes < 1 || mes > 12) return false;
        return !YearMonth.of(anio, mes).isBefore(YearMonth.now());
    }

    static String marcaTarjeta(String d) {
        if (d.startsWith("4")) return "VISA";
        if (d.matches("^(5[1-5]|2[2-7]).*")) return "MASTERCARD";
        if (d.startsWith("34") || d.startsWith("37")) return "AMERICAN EXPRESS";
        if (d.startsWith("36") || d.startsWith("30")) return "DINERS";
        return " ";
    }

    private static String dinero(double v) {
        NumberFormat f = NumberFormat.getNumberInstance(new Locale("es", "CO"));
        f.setMaximumFractionDigits(0);
        return "$" + f.format(v);
    }
}