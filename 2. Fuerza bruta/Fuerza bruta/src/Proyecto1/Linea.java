package Proyecto1;

import java.awt.*;

public class Linea {
    public Punto a, b;
    public Color colorOriginal;

    Linea(int x, int y, Color colorx){
        a = new Punto(x, y);
        b = new Punto(x, y);
        colorOriginal = colorx;
    }

    Linea(Punto a, Punto b, Color colorx){
        this.a = a;
        this.b = b;
        colorOriginal = colorx;
    }

    public double xDiff(){
        return a.x - b.x;
    }

    public double yDiff(){
        return a.y - b.y;
    }

    public void expande(int x, int y){
        if( x <= a.x ){
            a = new Punto(x, y);
        }else{
            b = new Punto(x, y);
        }
    }

    public boolean intersecta(Linea li){
        double numS = - yDiff() * (a.x - li.a.x) + xDiff() * (a.y - li.a.y);
        double denS = - li.xDiff() * yDiff() + xDiff() * li.yDiff();
        double numT = li.xDiff() * (a.y - li.a.y) - li.yDiff() * (a.x - li.a.x);
        double denT = - li.xDiff() * yDiff() + xDiff() * li.yDiff();
        if( denS == 0 || denT == 0 ){
            // Son paralelas o colineales, para mi intersectan :p
            return true;
        }else{
            double s = numS / denS;
            double t = numT / denT;
            if( 0 <= s && s <= 1 && 0 <= t && t <= 1 ){
                // Intersectan D:
                return true;
            }
        }
        // No intersectan
        return false;
    }
}
