package arboles;
 
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
 
public class ventanaArbol extends JFrame {
 
    private arbolPanel panelArbol;
 
    public ventanaArbol(Nodo raiz) {
        setTitle("Árbol Genealógico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        panelArbol = new arbolPanel(raiz);
 
        // CLAVE: el arbolPanel va DENTRO de un JScrollPane.
        // Así, si el árbol es más grande que la ventana,
        // aparecen barras de desplazamiento en vez de cortarse.
        JScrollPane scroll = new JScrollPane(panelArbol);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);
 
        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
 
        // Tamaño inicial de la ventana (fijo, cómodo para el usuario).
        // No uses pack() con el tamaño del árbol completo porque si el
        // árbol crece mucho la ventana terminaría gigante o fuera de pantalla.
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }
 
    // Llamar este metodo cada vez que el arbol cambie (nuevo nodo, nueva raiz, etc.)
    public void actualizarArbol(Nodo nuevaRaiz) {
        panelArbol.setRaiz(nuevaRaiz);
        // no hace falta hacer nada mas: el JScrollPane detecta el nuevo
        // getPreferredSize() del panel (gracias al revalidate() interno
        // de setRaiz) y ajusta las barras de scroll automaticamente.
    }
}