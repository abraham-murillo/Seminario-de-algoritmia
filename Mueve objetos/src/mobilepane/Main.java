package mobilepane;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // TODO code application logic here
        JFrame ventana = new JFrame("Agentes en un grafo");
        ventana.setContentPane(new MobilePanel().ventanaPrincipal);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.pack();
        ventana.setVisible(true);
    }
}
