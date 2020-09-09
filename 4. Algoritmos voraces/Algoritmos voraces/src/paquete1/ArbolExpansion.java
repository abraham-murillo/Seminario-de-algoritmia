package paquete1;

import java.awt.*;
import java.util.ArrayList;

public class ArbolExpansion extends Grafo {
    String nombre;
    Color color;
    boolean[] vis;

    ArbolExpansion(String nombre, ArrayList<Vertice> vertices, Color color){
        this.nombre = nombre;
        this.color = color;
        this.vertices = vertices;
        for(Vertice v: vertices){
            ady.put(v.id, new ArrayList<Integer>());
        }
        this.vis = new boolean[vertices.size()];
        // System.out.println("Vertices: " + vertices.size());
    }

    public String toString(){
        String tmp = nombre + "\n";
        tmp += "Vertices: " + numVertices() + ", ";
        tmp += "Aristas: " + numAristas() / 2 + "\n";
        for(int i = 0; i < aristas.size(); i += 2){
            Arista ari = aristas.get(i);
            tmp += ari.a.id + " -> " + ari.b.id + "\n";
        }
        return tmp;
    }
}
