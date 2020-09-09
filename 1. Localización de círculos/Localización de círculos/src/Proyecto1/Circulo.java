package Proyecto1;

class Punto {
    public int x, y;

    Punto(){}

    Punto(int theX, int theY){
        x = theX;
        y = theY;
    }
}

public class Circulo {
    public Punto inicial;
    public Punto arriba, abajo;
    public Punto izquierda, derecha;
    public Punto centroide;
    public boolean tocaBorde;

    Circulo(){}

    Circulo(int x, int y){
        inicial = new Punto(x, y);
        arriba = new Punto(x, y);
        abajo = new Punto(x, y);
        izquierda = new Punto(x, y);
        derecha = new Punto(x, y);
        centroide = new Punto(x, y);
        tocaBorde = false;
    }

    public boolean esCirculo(){
        return Math.abs(radioHorizontal() - radioVertical()) <= 10;
    }

    public void calculaCentroide(){
        centroide.x = (izquierda.x + derecha.x) / 2;
        centroide.y = (arriba.y + abajo.y) / 2;
    }

    public void expande(int x, int y){
        arriba.y = Math.min(arriba.y, y);
        abajo.y = Math.max(abajo.y, y);
        izquierda.x = Math.min(izquierda.x, x);
        derecha.x = Math.max(derecha.x, x);
    }

    public int radioHorizontal(){
        return Math.abs(centroide.x - derecha.x);
    }

    public int radioVertical(){
        return Math.abs(centroide.y - arriba.y);
    }

    public int radio(){
        return (radioHorizontal() + radioVertical()) / 2;
    }
}


/*
        arriba

izquierda     derecha

        abajo

 */
