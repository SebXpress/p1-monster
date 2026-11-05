package ec.edu.monster.vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Componentes visuales reutilizables: paneles redondeados y con degradado,
 * botones con efecto y animaciones simples.
 *
 * @author Grupo 1 - NRC 36841
 */
public final class Componentes {

    private Componentes() {
    }

    /** Panel con esquinas redondeadas. */
    public static class Tarjeta extends JPanel {

        private Color borde = Estilo.BORDE;

        public Tarjeta(LayoutManager l) {
            super(l);
            setOpaque(false);
            setBackground(Estilo.PANEL);
            setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        }

        public void setColorBorde(Color c) {
            this.borde = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            g2.setColor(borde);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Panel con degradado diagonal. */
    public static class Degradado extends JPanel {

        private final Color a;
        private final Color b;

        public Degradado(LayoutManager l, Color a, Color b) {
            super(l);
            this.a = a;
            this.b = b;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(0, 0, a, getWidth(), getHeight(), b));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /** Botón plano con color y efecto al pasar el mouse. */
    public static JButton boton(String texto, Color fondo) {
        JButton b = new JButton(texto);
        b.setFont(Estilo.SUBTITULO);
        b.setForeground(Estilo.FONDO);
        b.setBackground(fondo);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.putClientProperty("JButton.buttonType", "roundRect");
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color base;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                base = b.getBackground();
                b.setBackground(base.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (base != null) {
                    b.setBackground(base);
                }
            }
        });
        return b;
    }

    public static JLabel etiqueta(String texto, java.awt.Font f, Color c) {
        JLabel l = new JLabel(texto);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    /** Sacude horizontalmente un componente (error). */
    public static void sacudir(JComponent c) {
        final int x0 = c.getX();
        final int[] paso = {0};
        int[] desplazamientos = {-10, 10, -8, 8, -5, 5, -2, 2, 0};
        Timer t = new Timer(28, null);
        t.addActionListener(e -> {
            c.setLocation(x0 + desplazamientos[paso[0]], c.getY());
            if (++paso[0] >= desplazamientos.length) {
                t.stop();
            }
        });
        t.start();
    }

    /** Hace flotar un componente arriba y abajo de forma continua (variando su margen). */
    public static Timer flotar(JComponent c) {
        final double[] fase = {0};
        Timer t = new Timer(40, e -> {
            fase[0] += 0.12;
            int d = (int) Math.round(Math.sin(fase[0]) * 6);
            c.setBorder(BorderFactory.createEmptyBorder(8 + d, 0, 8 - d, 0));
            c.revalidate();
        });
        t.start();
        return t;
    }

    /** Anima un número desde 0 hasta el valor final en la etiqueta. */
    public static void contar(JLabel l, double valorFinal, String sufijo) {
        final long inicio = System.currentTimeMillis();
        Timer t = new Timer(16, null);
        t.addActionListener(e -> {
            double k = Math.min((System.currentTimeMillis() - inicio) / 700.0, 1.0);
            double suave = 1 - Math.pow(1 - k, 3);
            l.setText(formato(valorFinal * suave) + " " + sufijo);
            if (k >= 1) {
                l.setText(formato(valorFinal) + " " + sufijo);
                t.stop();
            }
        });
        t.start();
    }

    public static String formato(double v) {
        return Estilo.dinero(v);
    }

    /** Campo de texto con marcador (placeholder) y fuente estándar. */
    public static javax.swing.JTextField campo(String marcador) {
        javax.swing.JTextField t = new javax.swing.JTextField();
        t.setFont(Estilo.NORMAL);
        t.putClientProperty("JTextField.placeholderText", marcador);
        return t;
    }

    /** Tabla de solo lectura con estilo oscuro. */
    public static javax.swing.JTable tabla(javax.swing.table.DefaultTableModel modelo) {
        javax.swing.JTable t = new javax.swing.JTable(modelo);
        t.setRowHeight(28);
        t.setFont(Estilo.NORMAL);
        t.setFillsViewportHeight(true);
        t.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        t.getTableHeader().setFont(Estilo.PEQUENA.deriveFont(java.awt.Font.BOLD));
        t.getTableHeader().setReorderingAllowed(false);
        return t;
    }

    /** Modelo de tabla no editable. */
    public static javax.swing.table.DefaultTableModel modelo(String... columnas) {
        return new javax.swing.table.DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    /** Desvanece un color hacia otro en ~400 ms (efecto "destello" en tarjetas). */
    public static void destello(Tarjeta t, Color color) {
        t.setColorBorde(color);
        Timer timer = new Timer(900, e -> t.setColorBorde(Estilo.BORDE));
        timer.setRepeats(false);
        timer.start();
    }

    /** Etiqueta pequeña encima del componente (para formularios verticales). */
    public static JPanel campoConTitulo(String titulo, JComponent c) {
        JPanel p = new JPanel(new java.awt.BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(etiqueta(titulo, Estilo.PEQUENA, Estilo.TENUE), java.awt.BorderLayout.NORTH);
        p.add(c, java.awt.BorderLayout.CENTER);
        return p;
    }

    /** Desplazamiento con fondo oscuro para tablas. */
    public static javax.swing.JScrollPane scroll(JComponent c) {
        javax.swing.JScrollPane s = new javax.swing.JScrollPane(c);
        s.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        s.getViewport().setBackground(Estilo.PANEL);
        return s;
    }

    /** Alinea a la derecha las columnas indicadas (montos). */
    public static void alinearDerecha(javax.swing.JTable t, int... columnas) {
        javax.swing.table.DefaultTableCellRenderer r = new javax.swing.table.DefaultTableCellRenderer();
        r.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        for (int c : columnas) {
            t.getColumnModel().getColumn(c).setCellRenderer(r);
        }
    }

    /** Título de sección con subtítulo. */
    public static JPanel encabezado(String titulo, String subtitulo) {
        JPanel p = new JPanel(new java.awt.GridLayout(2, 1));
        p.setOpaque(false);
        p.add(etiqueta(titulo, Estilo.TITULO, Estilo.TEXTO));
        p.add(etiqueta(subtitulo, Estilo.PEQUENA, Estilo.TENUE));
        return p;
    }

    public static Dimension dim(int w, int h) {
        return new Dimension(w, h);
    }
}
