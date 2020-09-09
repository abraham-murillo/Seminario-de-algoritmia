package necesitoAyuda;

public class Punto {
    public int x, y;

    Punto() {}

    Punto(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int compareTo(Punto pt){
        if( x == pt.x ){
            return y - pt.y;
        }
        return -1;
    }

    public String toString(){
        String tmp = "";
        tmp += "(" + this.x + "," + this.y + ")";
        return tmp;
    }
}
