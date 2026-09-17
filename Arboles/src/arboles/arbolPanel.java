package arboles;

import java.awt.Color;
import java.awt.Dimension;//saber tamaño en total de JPanel
import java.awt.FontMetrics;
import java.awt.Graphics;//Dibujar 
import java.awt.Graphics2D;//Dibujar en una mejor version
import java.awt.RenderingHints;
import javax.swing.JPanel;//Lienzo en blanco para dibujar

public class arbolPanel extends JPanel {

    //atributos de configuracion del dibujo
    private Nodo raiz;
    private int anchoCasillero = 110;//pixeles horizontales que ocupa cada persona sin hijos
    private int altoNivel = 100;//separacion vertical entre generaciones
    private int anchoCaja = 90;
    private int altoCaja = 40;
    private int margen = 30;//separacion del Jpanel del bordo

    //constructor: recibe la raiz del arbol ya construido
    public arbolPanel(Nodo raiz) {
        this.raiz = raiz;
        this.setBackground(Color.WHITE);//un poco de CSS para dar color 
    }

    //calcula cuantos "casilleros" de ancho necesita el subarbol de r
    public int calcularAncho(Nodo r) {
        int resultado;

        if (r.isSw() == false) {
            //no tiene hijos: ocupa un solo casillero
            resultado = 1;
        } else {
            int suma = 0;//contador para guardar la cantidad de hijos
            Nodo p = r.getLigaLista();

            while (p != null) {
                //conocer la cantidad de hijos en total
                suma = suma + calcularAncho(p);//sumar los datos ya encontrados con los que tiene el contador
                p = p.getLiga();
            }

            resultado = suma;
        }

        return resultado;
    }

    //calcula cuantos niveles de profundidad tiene el subarbol de r
    public int calcularProfundidad(Nodo r) {
        int resultado;

        if (r.isSw() == false) {//no tiene hijos: ocupa un solo casillero
            resultado = 1;
        } else {
            int maxProfundidadHijos = 0;//guarda la cantidad de hijos encontrados
            Nodo p = r.getLigaLista();//recorre hijos

            while (p != null) {
                int profundidadHijo = calcularProfundidad(p);//buscar mas sobre los hijos y si estos tienen hijos
                if (profundidadHijo > maxProfundidadHijos) {//comparar la cantidad de hijos entre hermanos
                    maxProfundidadHijos = profundidadHijo;
                }
                p = p.getLiga();
            }

            resultado = 1 + maxProfundidadHijos;//sumar contador
        }

        return resultado;//dar resultado del que tiene mayor cantidad de hijos
    }

    //el tamaño que el panel necesita para mostrar el arbol completo sin cortarse
    @Override
    public Dimension getPreferredSize() {
        int ancho = margen * 2;//se multiplica por dos para que genere mas espacio 
        int alto = margen * 2;

        if (raiz != null) {
            ancho = ancho + calcularAncho(raiz) * anchoCasillero;//formula para saber el espacio que necesita la familia 
            alto = alto + calcularProfundidad(raiz) * altoNivel;
        }

        return new Dimension(ancho, alto);//indica el tamaño de JPanel
    }

    @Override
    //basicamente el metodo que prepara todo para dibujar
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);//limpiar el JPanel
        Graphics2D g2 = (Graphics2D) g;//convertir el lapiz en uno mas avanzado
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//estetica

        if (raiz != null) {//verificar si existe el padre principal
            int xIzquierda = margen;//mirar donde empieza el dibujo
            int xDerecha = margen + calcularAncho(raiz) * anchoCasillero;//mira donde termina el dibujo
            dibujarNodo(g2, raiz, xIzquierda, xDerecha, 0);//informacion que necesita el metodo para que realmente dubuje
        }
    }

    //dibuja el nodo r centrado en el rango [xIzquierda, xDerecha], y reparte ese rango entre sus hijos
    private void dibujarNodo(Graphics2D g2, Nodo r, int xIzquierda, int xDerecha, int nivel) {
        //calcular donde debe de ir el nodo Raiz
        int centroX = (xIzquierda + xDerecha) / 2;//buscar la mitad en x
        int y = margen + nivel * altoNivel;//tamaño en y

        //si tiene hijos, primero dibujamos las lineas y los hijos (para que queden detras de la caja del padre al final)
        if (r.isSw() == true) {//si tiene hijos
            int anchoTotalUnidades = calcularAncho(r);//espacio total de todos los hijos
            int anchoTotalPixeles = xDerecha - xIzquierda;//espacio en pixeles que se necesita en JPanel
            int xActual = xIzquierda;//cursor se mueve izq-der a medida que se asigna espacio a cada hijo

            Nodo p = r.getLigaLista();

            while (p != null) {//recorrer y asignar valores por hijo
                int anchoHijoUnidades = calcularAncho(p);
                int anchoHijoPixeles = anchoTotalPixeles * anchoHijoUnidades / anchoTotalUnidades;

                int hijoXIzquierda = xActual;
                int hijoXDerecha = xActual + anchoHijoPixeles;
                int hijoCentroX = (hijoXIzquierda + hijoXDerecha) / 2;
                int hijoY = margen + (nivel + 1) * altoNivel;

                //linea desde el borde inferior del padre hasta el borde superior del hijo
                g2.setColor(Color.GRAY);//poner color
                g2.drawLine(centroX, y + altoCaja / 2, hijoCentroX, hijoY - altoCaja / 2);//decir donde empieza y termina la linea en padre e hijo

                dibujarNodo(g2, p, hijoXIzquierda, hijoXDerecha, nivel + 1);

                xActual = xActual + anchoHijoPixeles;
                p = p.getLiga();
            }
        }

        //la caja del nodo actual, dibujada al final para que quede sobre las lineas
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

        int anchoTextoNombre = fm.stringWidth(nombre);
        g2.drawString(nombre, centroX - anchoTextoNombre / 2, y - 3);

        int anchoTextoCedula = fm.stringWidth(cedula);
        g2.drawString(cedula, centroX - anchoTextoCedula / 2, y + 13);
    }
}
