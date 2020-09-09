package ProyectoFinal;

import javax.swing.*;
import java.awt.*;
import java.util.Deque;
import java.util.LinkedList;

import static java.lang.Math.max;

public class Objeto extends JPanel {
    Punto pos;
    int IDUltimaArista;
    int velocidad;
    Deque<Punto> porCaminar;
    Deque<Integer> IDArista;
    JLabel labelObjeto;
    boolean[] vis;
    String nombre;
    int vida;
    Objeto pareja;
    boolean quitarVidaPareja;
    Color color;

    Objeto(){
    }

    public void avanza(){
        // Si ya no tengo nada por caminar, no camino, ni lo intento
        if( porCaminar.isEmpty() )
            return;
        // Avanzo en el camino que me han dado
        // Si son el mismo entonces lo ignoro y tomo el siguiente :D
        while( !porCaminar.isEmpty() && porCaminar.getFirst() == pos ){
            porCaminar.poll();
            IDArista.poll();
        }
        pos = porCaminar.poll();
        IDUltimaArista = IDArista.poll();
        Dimension dim = this.getPreferredSize();
        this.setBounds(pos.x - dim.width / 2, pos.y - dim.height / 2, dim.width, dim.height);
        this.repaint();
    }

    public String toString(){
        String tmp = nombre;
        tmp += ", pos: (" + pos.x + "," + pos.y +  ")";
        tmp += ", velocidad: " + velocidad;
        tmp += ", por caminar: " + porCaminar.size();
        return tmp;
    }

    Objeto(String nombre, Punto pt, int tamano, Color c, int v){
        this.color = c;
        this.vida = 5;
        this.velocidad = v;
        this.nombre = nombre;
        pos = pt;
        this.IDUltimaArista = -1;
        this.pareja = null;
        porCaminar = new LinkedList<Punto>();
        IDArista = new LinkedList<Integer>();
        labelObjeto = new JLabel(nombre);
        this.quitarVidaPareja = false;
        this.add(labelObjeto);
        this.setBackground(c);
        this.setPreferredSize(new Dimension(tamano, tamano));
        this.setBounds(pos.x - tamano / 2, pos.y - tamano / 2, tamano, tamano);
    }
}

