package paquete1;

import javax.swing.*;
import java.awt.*;
import java.util.Deque;
import java.util.LinkedList;

import static java.lang.Math.max;

public class Objeto extends JPanel {
    Punto pos;
    int idUltimaArista;
    int velocidad;
    Deque<Punto> porCaminar;
    Deque<Integer> idArista;
    JLabel labelObjeto;
    boolean[] vis;
    String nombre;

    Objeto(){
        velocidad = 5;
    }

    public void avanza(){
        if( porCaminar.isEmpty() ){
            return;
        }
        pos = porCaminar.poll();
        idUltimaArista = idArista.poll();
        Dimension dim = this.getPreferredSize();
        this.setBounds(pos.x - dim.width / 2, pos.y - dim.height / 2, dim.width, dim.height);
        this.repaint();
    }

    public String toString(){
        String tmp = nombre;
        tmp += "pos: (" + pos.x + "," + pos.y +  "), ";
        tmp += "velocidad: " + velocidad;
        return tmp;
    }
}

class Agente extends Objeto{
    ArbolExpansion arbol;

    Agente(String nombre, Punto pt, int tamano, ArbolExpansion arbol){
        this.nombre = nombre;
        pos = pt;
        this.idUltimaArista = 0;
        /*imagen = new ImageIcon(new ImageIcon("/Users/abrahammurillo/Desktop/bobEsponja.png").getImage().getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH));
        labelObjeto.setIcon(imagen);
        this.setBounds(pos.x - tamano / 2, pos.y - tamano / 2, tamano, tamano);
        this.add(labelObjeto);
        labelObjeto.repaint();
        System.out.println(this);*/

        porCaminar = new LinkedList<Punto>();
        idArista = new LinkedList<Integer>();
        labelObjeto = new JLabel(nombre);
        this.add(labelObjeto);
        this.setBackground(new Color(255, 0, 80));
        this.setPreferredSize(new Dimension(tamano, tamano));
        this.setBounds(pos.x - tamano / 2, pos.y - tamano / 2, tamano, tamano);
        this.arbol = arbol;
    }
};

class Senuelo extends Objeto{
    boolean existe;

    Senuelo(Punto pt, int tamano){
        if( pt.x != -1 ){
            existe = true;
        }else{
            existe = false;
        }
        pos = pt;
        /* imagen = new ImageIcon(new ImageIcon("/Users/abrahammurillo/Desktop/caracola.png").getImage().getScaledInstance(tamano, tamano, Image.SCALE_DEFAULT));
        labelObjeto.setIcon(imagen);
        this.setBounds(pos.x - tamano / 2, pos.y - tamano / 2, tamano, tamano);
        this.add(labelObjeto);
        labelObjeto.repaint();
        System.out.println(this);*/
        labelObjeto = new JLabel("Señuelo");
        this.add(labelObjeto);
        this.setBackground(new Color(120, 31, 221));
        this.setPreferredSize(new Dimension(tamano, tamano));
        this.setBounds(pos.x - tamano / 2, pos.y - tamano / 2, tamano, tamano);
    }
}

