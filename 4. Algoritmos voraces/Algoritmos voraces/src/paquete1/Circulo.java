package paquete1;

public class Circulo {
    private int arriba, abajo;
    private int izquierda, derecha;
    public Punto centro;
    public boolean tocaBorde;

    Circulo() {}

    Circulo(int x, int y) {
        arriba = y;
        abajo = y;
        izquierda = x;
        derecha = x;
        centro = new Punto(x, y);
        tocaBorde = false;
    }

    public boolean esCirculo() {
        //return Math.abs(radioHorizontal() - radioVertical()) <= 10;
        return true;
    }

    public void calculaCentro() {
        centro.x = (izquierda + derecha) / 2;
        centro.y = (arriba + abajo) / 2;
    }

    public void expande(int x, int y) {
        arriba = Math.min(arriba, y);
        abajo = Math.max(abajo, y);
        izquierda = Math.min(izquierda, x);
        derecha = Math.max(derecha, x);
    }

    public int radioHorizontal() {
        return Math.abs(centro.x - derecha);
    }

    private int radioVertical() {
        return Math.abs(centro.y - arriba);
    }

    public int radio() {
        return (radioHorizontal() + radioVertical()) / 2;
    }

    public double distancia(Circulo cir) {
        return Math.hypot(centro.x - cir.centro.x, centro.y - cir.centro.y);
    }

    public int compareTo(Circulo cir) {
        return centro.x == cir.centro.x && centro.y == cir.centro.y ? 0: -1;
    }
}


/*
        arriba

izquierda     derecha

        abajo

 */