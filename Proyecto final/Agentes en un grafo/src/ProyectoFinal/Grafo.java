package ProyectoFinal;

import java.util.ArrayList;
import java.util.HashMap;

class Vertice {
    public Circulo cir;
    public int id = -1;

    Vertice() {}

    Vertice(Circulo cir, int id) {
        this.cir = cir;
        this.id = id;
    }

    public int compareTo(Vertice v) {
        if (cir.compareTo(v.cir) == 0) {
            return id - v.id;
        }
        return cir.compareTo(v.cir);
    }
}

class Arista {
    public Vertice a, b;
    public ArrayList<Punto> linea;
    public int id = -1;

    Arista() {}

    Arista(Vertice a, Vertice b, ArrayList<Punto> linea, int id){
        this.a = a;
        this.b = b;
        this.linea = new ArrayList<>();
        this.linea.add(a.cir.centro);
        for(Punto p: linea){
            this.linea.add(p);
        }
        this.linea.add(b.cir.centro);
        this.id = id;
    }
}

public class Grafo{
    HashMap<Integer, ArrayList<Integer>> ady;
    ArrayList<Vertice> vertices;
    ArrayList<Arista> aristas;
    int nAristas;
    int nVertices;

    Grafo() {
        vaciarGrafo();
    }

    public void vaciarGrafo(){
        ady = new HashMap<Integer, ArrayList<Integer>>();
        vertices = new ArrayList<Vertice>();
        aristas = new ArrayList<Arista>();
        System.out.println("Grafo limpio :D");
        nAristas = 0;
        nVertices = 0;
    }

    public Vertice quien(Circulo cir){
        // System.out.println("sz(vertices): " + vertices.size());
        for (int i = 0; i < vertices.size(); i++) {
            // System.out.println("¿Es 0?: " + vertices.get(i).cir.compareTo(cir));
            // System.out.println("tmp: (" + vertices.get(i).cir.centro.x + "," + vertices.get(i).cir.centro.y + ")");
            if( vertices.get(i).cir.compareTo(cir) == 0 ){
                return vertices.get(i);
            }
        }
        return null;
    }

    public Vertice quien(Punto pt){
        return quien(new Circulo(pt.x, pt.y));
    }

    public int numVertices() {
        return ady.size();
    }

    public int numAristas() {
        return nAristas;
    }
}