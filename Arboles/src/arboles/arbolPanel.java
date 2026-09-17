package arboles;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;//Lienzo en blanco para dibujar

public class arbolPanel extends JPanel {

    //atributos de configuracion del dibujo (coordenadas "reales" del arbol, sin zoom)
    private Nodo raiz;
    private int anchoCasillero = 130;
    private int altoNivel = 100;
    private int anchoCaja = 110;
    private int altoCaja = 40;
    private int margen = 30;

    //=========================================================
    // NUEVO: el panel mueve y hace zoom por su cuenta, sin depender
    // de que la ventana tenga un JScrollPane. offsetX/offsetY mueven
    // la vista, escala hace zoom in/out.
    //=========================================================
    private double escala = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;
    private Point puntoArrastre;
    private boolean ajusteInicialHecho = false;

    public arbolPanel(Nodo raiz) {
        this.raiz = raiz;
        this.setBackground(Color.WHITE);
        habilitarPanYZoom();
    }

    private void habilitarPanYZoom() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                puntoArrastre = e.getPoint();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    verArbolCompleto(); //doble clic = ajustar zoom para ver todo
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (puntoArrastre == null) {
                    return;
                }
                offsetX += e.getX() - puntoArrastre.x;
                offsetY += e.getY() - puntoArrastre.y;
                puntoArrastre = e.getPoint();
                repaint();
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double factor = (e.getWheelRotation() < 0) ? 1.1 : 0.9;
                double nuevaEscala = escala * factor;

                //limites razonables de zoom (10% a 300%)
                if (nuevaEscala < 0.1) {
                    nuevaEscala = 0.1;
                }
                if (nuevaEscala > 3.0) {
                    nuevaEscala = 3.0;
                }

                //hacer zoom centrado en la posicion del mouse, no en la esquina
                double mx = e.getX();
                double my = e.getY();
                offsetX = mx - (mx - offsetX) * (nuevaEscala / escala);
                offsetY = my - (my - offsetY) * (nuevaEscala / escala);

                escala = nuevaEscala;
                repaint();
            }
        });

        //la primera vez que el panel tiene un tamaño real, ajustar el zoom
        //automaticamente para que se vea el arbol completo
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!ajusteInicialHecho && getWidth() > 0 && getHeight() > 0) {
                    verArbolCompleto();
                    ajusteInicialHecho = true;
                }
            }
        });
    }

    //ajusta escala y offset para que el arbol completo entre en el panel visible.
    //Tambien se puede llamar desde un boton "Ver todo" si lo agregas al menu.
    public void verArbolCompleto() {
        if (raiz == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }

        int anchoArbol = margen * 2 + calcularAncho(raiz) * anchoCasillero;
        int altoArbol = margen * 2 + calcularProfundidad(raiz) * altoNivel;

        double escalaX = (double) getWidth() / anchoArbol;
        double escalaY = (double) getHeight() / altoArbol;
        double nuevaEscala = Math.min(escalaX, escalaY);

        if (nuevaEscala > 1.0) {
            nuevaEscala = 1.0; //no agrandar arboles chicos, solo achicar los grandes
        }
        if (nuevaEscala < 0.1) {
            nuevaEscala = 0.1;
        }

        escala = nuevaEscala;
        //centrar el arbol en el panel
        offsetX = (getWidth() - anchoArbol * escala) / 2.0;
        offsetY = (getHeight() - altoArbol * escala) / 2.0;

        repaint();
    }

    public void setRaiz(Nodo raiz) {
        this.raiz = raiz;
        ajusteInicialHecho = false; //recalcular el zoom para el nuevo arbol
        revalidate();
        repaint();
    }

    public void refrescar() {
        revalidate();
        repaint();
    }

    public int calcularAncho(Nodo r) {
        int resultado;
        if (r.isSw() == false) {
            resultado = 1;
        } else {
            int suma = 0;
            Nodo p = r.getLigaLista();
            while (p != null) {
                suma = suma + calcularAncho(p);
                p = p.getLiga();
            }
            resultado = suma;
        }
        return resultado;
    }

    public int calcularProfundidad(Nodo r) {
        int resultado;
        if (r.isSw() == false) {
            resultado = 1;
        } else {
            int maxProfundidadHijos = 0;
            Nodo p = r.getLigaLista();
            while (p != null) {
                int profundidadHijo = calcularProfundidad(p);
                if (profundidadHijo > maxProfundidadHijos) {
                    maxProfundidadHijos = profundidadHijo;
                }
                p = p.getLiga();
            }
            resultado = 1 + maxProfundidadHijos;
        }
        return resultado;
    }

    //el panel ya NO le pide a la ventana un tamaño gigante: el zoom/pan
    //propio se encarga de mostrar el arbol completo dentro del tamaño
    //que la ventana le de. Un tamaño por defecto razonable como sugerencia.
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(900, 650);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (raiz != null) {
            //aplicar el pan (offset) y el zoom (escala) antes de dibujar
            g2.translate(offsetX, offsetY);
            g2.scale(escala, escala);

            int xIzquierda = margen;
            int xDerecha = margen + calcularAncho(raiz) * anchoCasillero;
            dibujarNodo(g2, raiz, xIzquierda, xDerecha, 0);
        }
    }

    private String recortarTexto(FontMetrics fm, String texto, int anchoMaximo) {
        if (fm.stringWidth(texto) <= anchoMaximo) {
            return texto;
        }
        String recortado = texto;
        while (recortado.length() > 1 && fm.stringWidth(recortado + "...") > anchoMaximo) {
            recortado = recortado.substring(0, recortado.length() - 1);
        }
        return recortado + "...";
    }

    private void dibujarNodo(Graphics2D g2, Nodo r, int xIzquierda, int xDerecha, int nivel) {
        int centroX = (xIzquierda + xDerecha) / 2;
        int y = margen + nivel * altoNivel;

        if (r.isSw() == true) {
            int anchoTotalUnidades = calcularAncho(r);
            int anchoTotalPixeles = xDerecha - xIzquierda;
            int xActual = xIzquierda;

            Nodo p = r.getLigaLista();

            while (p != null) {
                int anchoHijoUnidades = calcularAncho(p);
                int anchoHijoPixeles = anchoTotalPixeles * anchoHijoUnidades / anchoTotalUnidades;

                int hijoXIzquierda = xActual;
                int hijoXDerecha = xActual + anchoHijoPixeles;
                int hijoCentroX = (hijoXIzquierda + hijoXDerecha) / 2;
                int hijoY = margen + (nivel + 1) * altoNivel;

                g2.setColor(Color.GRAY);
                g2.drawLine(centroX, y + altoCaja / 2, hijoCentroX, hijoY - altoCaja / 2);

                dibujarNodo(g2, p, hijoXIzquierda, hijoXDerecha, nivel + 1);

                xActual = xActual + anchoHijoPixeles;
                p = p.getLiga();
            }
        }

        int cajaX = centroX - anchoCaja / 2;
        int cajaY = y - altoCaja / 2;

        g2.setColor(new Color(220, 237, 250));
        g2.fillRoundRect(cajaX, cajaY, anchoCaja, altoCaja, 12, 12);
        g2.setColor(new Color(60, 120, 170));
        g2.drawRoundRect(cajaX, cajaY, anchoCaja, altoCaja, 12, 12);

        String nombre = r.getDato().getNombre();
        String cedula = "CC " + r.getDato().getCedula();

        g2.setColor(Color.BLACK);
        FontMetrics fm = g2.getFontMetrics();
        int anchoMaximoTexto = anchoCaja - 10;

        String nombreRecortado = recortarTexto(fm, nombre, anchoMaximoTexto);
        int anchoTextoNombre = fm.stringWidth(nombreRecortado);
        g2.drawString(nombreRecortado, centroX - anchoTextoNombre / 2, y - 3);

        String cedulaRecortada = recortarTexto(fm, cedula, anchoMaximoTexto);
        int anchoTextoCedula = fm.stringWidth(cedulaRecortada);
        g2.drawString(cedulaRecortada, centroX - anchoTextoCedula / 2, y + 13);
    }
}