package arboles;

import javax.swing.JFrame;

public class ventanaArbol extends JFrame {

    public ventanaArbol(Nodo raiz) {
        this.setTitle("Árbol Genealógico");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        arbolPanel panel = new arbolPanel(raiz);
        this.add(panel);
    }
}
