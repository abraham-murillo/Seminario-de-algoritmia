package Proyecto1;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class Solucionador extends Grafo {
    public BufferedImage imagenModificada;
    public Color colorActual;
    public Color negro = new Color(0, 0, 0);
    public Color gris = new Color(50, 50, 50);
    public Color azul = new Color(21, 65, 245);
    public Color naranja = new Color(255, 160, 90);
    public Color verde = new Color(0, 255, 10);
    public Color morado = new Color(120, 31, 221);
    public Color rojo = new Color(199, 0, 0);
   // public Color naranja = new Color(255, 160, 90);
    public Color amarillo = new Color(247, 250, 43);
    public Color blanco = new Color(255, 255, 255);
    public Color prob = new Color(254, 254, 254);
    public boolean hecho;
    ArrayList<Linea> wtf;

    Solucionador() {
        // Reinicia los valores por cualquier cosa
        vaciar();
    }

    void vaciar() {
        hecho = false;
        this.vaciarGrafo();
        wtf = new ArrayList<Linea>();
    }

    public boolean ruido(Color color){
        return color.getRed() == color.getGreen() && color.getGreen() == color.getBlue()
                && 0 < color.getRed() && color.getRed() < 255
                && 0 < color.getGreen() && color.getGreen() < 255
                && 0 < color.getBlue() && color.getBlue() < 255;
    }

    public void quitarRuido() {
        // Pinta lo que tenga colores "negros" de negro
        if (!hecho) {
            for (int y = 0; y < imagenModificada.getHeight(); y++) {
                for (int x = 0; x < imagenModificada.getWidth(); x++) {
                    colorActual = new Color(imagenModificada.getRGB(x, y));
                    if ( ruido(colorActual) ) {
                        imagenModificada.setRGB(x, y, blanco.getRGB());
                    }
                }
            }
        }
    }

    private boolean esValido(int x, int y) {
        // Valida que una pixel(x, y) se encuentre en la imagen
        return 0 <= x && x < imagenModificada.getWidth() && 0 <= y && y < imagenModificada.getHeight();
    }


    private Circulo buscarCirculo(int x, int y, Color colorInicio, Color colorFinal) {
        /*
         Empezando desde el pixel(x, y) nos expandiremos a todos sus adyacentes siempre y cuando esos tengan el color inicio y
         para ahorrarnos nuestra matriz de visitados[][] vamos a pintar de color fin, de está manera no procesaremos un pixel dos veces.
         Corremos una bfs que nos resuelve todo el proceso anteriormente descrito.
         */
        int[] dx = new int[] { 1, 0, -1, 0};
        int[] dy = new int[] { 0, 1, 0, -1};
        Circulo cir = new Circulo(x, y); // Actualmente es un punto (x, y)
        Queue < Punto > qu = new LinkedList < > ();
        qu.add(new Punto(x, y));
        imagenModificada.setRGB(x, y, colorFinal.getRGB());
        while (!qu.isEmpty()) {
            Punto actual = qu.poll();
            for (int k = 0; k < 4; k++) {
                int nx = actual.x + dx[k];
                int ny = actual.y + dy[k];
                if (!esValido(nx, ny)) {
                    cir.tocaBorde = true;
                    continue;
                }
                colorActual = new Color(imagenModificada.getRGB(nx, ny));
                if (colorActual.getRGB() == colorInicio.getRGB()) {
                    qu.add(new Punto(nx, ny));
                    cir.expande(nx, ny);
                    imagenModificada.setRGB(nx, ny, colorFinal.getRGB());
                }
            }
        }
        cir.calculaCentro(); // Calculamos el centro de nuestro círculo
        return cir;
    }

    public void buscarTodosLosCirculos() {
        /*
         Para cada pixel(x, y) vemos que sea de color negro (indicando que es un círculo) y pasamos a pintarlo de color verde, después verificamos que
         sea efectivamente un círculo (desechar óvalos, figuras extrañas) y lo agregamos a nuestra lista de círculos.
         */
        if( hecho ){
            return;
        }
        Circulo cir;
        for (int y = 0; y < imagenModificada.getHeight(); y++) {
            for (int x = 0; x < imagenModificada.getWidth(); x++) {
                colorActual = new Color(imagenModificada.getRGB(x, y));
                if (colorActual.getRGB() == negro.getRGB()) {
                    cir = buscarCirculo(x, y, negro, verde);
                    if (cir.esCirculo()) {
                        anadirVertice(cir);
                    } else {
                        pintarFigura(cir, verde, azul);
                    }
                }
            }
        }
        // System.out.println("Num de aristas: " + grafo.numAristas());
        // System.out.println("Num de vertices: " + grafo.numVertices());
    }


    private void pintarFigura(int x, int y, Color colorInicio, Color colorFinal) {
        // Desde el pixel(x, y) mientras sea de "colorInicio" lo cambiamos a "colorFinal"
        int dx[] = new int[] {-1, +0, +1, -1, +1, -1, +0, +1};
        int dy[] = new int[] {-1, -1, -1, +0, +0, +1, +1, +1};
        Queue < Punto > qu = new LinkedList < > ();
        qu.add(new Punto(x, y));
        imagenModificada.setRGB(x, y, colorFinal.getRGB());
        while (!qu.isEmpty()) {
            Punto actual = qu.poll();
            for (int k = 0; k < 8; k++) {
                int nx = actual.x + dx[k];
                int ny = actual.y + dy[k];
                if (!esValido(nx, ny)) {
                    continue;
                }
                colorActual = new Color(imagenModificada.getRGB(nx, ny));
                if (colorActual.getRGB() == colorInicio.getRGB()) {
                    qu.add(new Punto(nx, ny));
                    imagenModificada.setRGB(nx, ny, colorFinal.getRGB());
                }
            }
        }
    }

    private void pintarFigura(Circulo cir, Color colorInicio, Color colorFinal) {
        pintarFigura(cir.centro.x, cir.centro.y, colorInicio, colorFinal);
    }

    private void pintarFigura(Vertice v, Color colorInicio, Color colorFinal) {
        pintarFigura(v.cir, colorInicio, colorFinal);
    }

    private void pintarFigura(Linea li, Color colorInicio, Color colorFinal) {
        pintarFigura(li.a.x, li.a.y, colorInicio, colorFinal);
    }


    /*
    private boolean puedeSerLinea(int x, int y, Color colorInicio){
        int dx[] = new int[] {-1, +0, +1, -1, +1, -1, +0, +1};
        int dy[] = new int[] {-1, -1, -1, +0, +0, +1, +1, +1};
        int cuenta = 0;
        for (int i = 0; i < 8; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if( esValido(nx, ny) && imagenModificada.getRGB(nx, ny) == colorInicio.getRGB() ){
                cuenta++;
            }
        }
        // Si son más me chingo D':
        return cuenta == 2;
    }

    private Linea buscarLinea(int x, int y, Color colorInicio, Color colorFinal) {
        int dx[] = new int[] {-1, +0, +1, -1, +1, -1, +0, +1};
        int dy[] = new int[] {-1, -1, -1, +0, +0, +1, +1, +1};
        Linea li = new Linea(x, y, colorInicio);
        Queue < Punto > qu = new LinkedList < > ();
        qu.add(new Punto(x, y));
        imagenModificada.setRGB(x, y, colorFinal.getRGB());
        while (!qu.isEmpty()) {
            Punto actual = qu.poll();
            for (int k = 0; k < 8; k++) {
                int nx = actual.x + dx[k];
                int ny = actual.y + dy[k];
                if (!esValido(nx, ny)) {
                    continue;
                }
                colorActual = new Color(imagenModificada.getRGB(nx, ny));
                if (colorActual.getRGB() == colorInicio.getRGB()) {
                    qu.add(new Punto(nx, ny));
                    li.expande(x, y);
                    imagenModificada.setRGB(nx, ny, colorFinal.getRGB());
                }
            }
        }
        return li;
    }

    public void buscarTodasLasLineas(boolean adiosRuido){
        if( hecho ){
            return;
        }
        for (int y = 0; y < imagenModificada.getHeight(); y++) {
            for (int x = 0; x < imagenModificada.getWidth(); x++) {
                colorActual = new Color(imagenModificada.getRGB(x, y));
                if( colorActual.getRGB() != blanco.getRGB() && colorActual.getRGB() != verde.getRGB() && puedeSerLinea(x, y, colorActual) ) {
                    // Hago la línea un pseudocírculo
                    wtf.add(buscarLinea(x, y, colorActual, verde));
                }
            }
        }
        System.out.println("Número de líneas: " + wtf.size());
        int[] dx = new int[] {1, 0, -1, 0};
        int[] dy = new int[] {0, 1, 0, -1};
        for (Linea li: wtf) {
            boolean esRuido = false;
            for (int i = 0; i < 4; i++) {
                int nx = li.a.x + dx[i];
                int ny = li.a.y + dy[i];
                if( imagenModificada.getRGB(nx, ny) != blanco.getRGB() ){
                    esRuido = true;
                }
            }
            if( adiosRuido && esRuido ){
                pintarFigura(li, verde, blanco);
            }else{
                pintarFigura(li, verde, li.colorOriginal);
            }
        }
    }

    public void quitaRuidoFeik(boolean adiosRuido){
        buscarTodasLasLineas(adiosRuido);
        wtf.clear();
    }
*/

    private void pintarLinea(ArrayList < Punto > linea, Color colorLinea) {
        for (int i = 0; i < linea.size(); i++) {
            Punto pt = linea.get(i);
            imagenModificada.setRGB(pt.x, pt.y, colorLinea.getRGB());
        }
    }


    public double calcularClosestPairOfPoints(Color colorInicio, Color colorFinal, Color colorLetras) {
        /*
         Inicialmente la peor distancia es dist = Double.POSITIVE_INFINITY
         Para cada par (i, j) calculamos la distancia que hay entre ellos y si es menor a nuestra peor distancia, los dos nuevos círculos
         más cercanos son circulo(i) y circulo(j) que guardamos en a, b respectivamente.
         */
        double dist = Double.POSITIVE_INFINITY;
        Vertice a = new Vertice();
        Vertice b = new Vertice();
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                if (vertices.get(i).cir.distancia(vertices.get(j).cir) < dist) {
                    a = vertices.get(i);
                    b = vertices.get(j);
                    dist = a.cir.distancia(b.cir);
                }
            }
        }
        // Debo de tener al menos 2 círculos para poder calculara el par de puntos más cercanos.
        if (vertices.size() >= 2) {
            System.out.println("Si tengo >= 2");
            anadirId(a, colorInicio);
            anadirId(b, colorInicio);
            pintarFigura(a, colorInicio, colorFinal);
            pintarFigura(b, colorInicio, colorFinal);
            anadirId(a, colorLetras);
            anadirId(b, colorLetras);
        }
        return dist;
    }


    public void ordenarCirculos() {
        /*
         Revisamos cada par (i, j) y si el j.radio() (derecha) es mayor que i.radio(), quiere decir que j debería de estar
         donde se ecuentra el i y viceversa.
         */
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                if (vertices.get(j).cir.radio() > vertices.get(i).cir.radio()) {
                    Collections.swap(vertices, i, j);
                }
            }
        }
    }


    private boolean hayCamino(Circulo a, Circulo b, Color colorInicio, Color colorFinal) {
        int x1 = a.centro.x, y1 = a.centro.y;
        int x2 = b.centro.x, y2 = b.centro.y;
        int dx = Math.abs(x2 - x1);
        int sx = x1 < x2 ? 1 : -1;
        int dy = -Math.abs(y2 - y1);
        int sy = y1 < y2 ? 1 : -1;
        int err = dx + dy;
        while (true) {
            if (imagenModificada.getRGB(x1, y1) == colorInicio.getRGB() ||
                imagenModificada.getRGB(x1, y1) == colorFinal.getRGB() ||
                imagenModificada.getRGB(x1, y1) == blanco.getRGB() ) {
                // Por aquí debe de pasar la línea, así que es correcto
            }else{
                // Estás en un pixel de otro color a los que son válidos
                return false;
            }
            if (x1 == x2 && y1 == y2) {
                break;
            }
            int err2 = 2 * err;
            if (err2 >= dy) {
                err += dy;
                x1 += sx;
            }
            if (err2 <= dx) {
                err += dx;
                y1 += sy;
            }
        }
        return true;
    }

    private ArrayList<Punto> buscarCamino(Circulo a, Circulo b, Color colorInicio, Color colorFinal) {
        // Bresenham's principles of integer incremental error
        ArrayList < Punto > linea = new ArrayList < > ();
        int x1 = a.centro.x, y1 = a.centro.y;
        int x2 = b.centro.x, y2 = b.centro.y;
        int dx = Math.abs(x2 - x1);
        int sx = x1 < x2 ? 1 : -1;
        int dy = -Math.abs(y2 - y1);
        int sy = y1 < y2 ? 1 : -1;
        int err = dx + dy;
        while (true) {
            if (imagenModificada.getRGB(x1, y1) == colorInicio.getRGB() || imagenModificada.getRGB(x1, y1) == colorFinal.getRGB()) {
                // Estás sobre uno de los dos círculos
            }else if( imagenModificada.getRGB(x1, y1) == blanco.getRGB() ){
                // Es un pixel blanco por lo que puedo pintarlo
                linea.add(new Punto(x1, y1));
            }else{
                // Choca con algo, así que no me regreses la línea, regrésame nada :c
                linea.clear();
                break;
            }
            if (x1 == x2 && y1 == y2) {
                break;
            }
            int err2 = 2 * err;
            if (err2 >= dy) {
                err += dy;
                x1 += sx;
            }
            if (err2 <= dx) {
                err += dx;
                y1 += sy;
            }
        }
        return linea;
    }

    private ArrayList<Punto> buscarCamino(Vertice a, Vertice b, Color colorInicio, Color colorFinal) {
        return buscarCamino(a.cir, b.cir, colorInicio, colorFinal);
    }


    private void anadirId(Vertice v, Color colorLetras) {
        if( imagenModificada.getRGB(v.cir.centro.x, v.cir.centro.y) == blanco.getRGB() ){
            // El circulo/vertice ha sido borrado, no ha necesidad de hacer algo
        }else{
            // Se añade el v.id a partir del centro del círculo
            BufferedImage tmp = new BufferedImage(imagenModificada.getWidth(), imagenModificada.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = tmp.createGraphics();
            g2d.drawImage(imagenModificada, 0, 0, imagenModificada.getWidth(), imagenModificada.getHeight(), null);
            g2d.setPaint(colorLetras);
            g2d.setFont(new Font("Calibri", Font.ITALIC, 20));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(String.valueOf(v.id), v.cir.centro.x - 10, v.cir.centro.y + 10);
            g2d.dispose();
            imagenModificada = tmp;
        }
    }

    private void agregarTodosLosIds(Color colorLetras) {
       for (Vertice v: vertices) {
           anadirId(v, colorLetras);
        }
    }

    public boolean hayCaminoLibre(Linea a){
        for (Linea b: wtf) {
            if( b.intersecta(a) ){
                return false;
            }
        }
        return true;
    }

    public void construirGrafo() {
        if (hecho) {
            return;
        }
        for (int i = 0; i < vertices.size(); i++) {
            Vertice a = vertices.get(i);
            pintarFigura(a, verde, morado);
            for (int j = i + 1; j < vertices.size(); j++) {
                Vertice b = vertices.get(j);
                pintarFigura(b, verde, amarillo);
                ArrayList<Punto> linea = buscarCamino(a, b, morado, amarillo);
                if( !linea.isEmpty() ){
                    anadirArista(a, b, linea);
                }
                pintarFigura(b, amarillo, verde);
            }
            pintarFigura(a, morado, verde);
        }
        System.out.println("Num de aristas: " + numAristas());
        System.out.println("Num de vertices: " + numVertices());
        for (Arista ari: aristas) {
            pintarLinea(ari.linea, negro);
        }
        calcularClosestPairOfPoints(verde, naranja, gris);
        agregarTodosLosIds(gris);
        hecho = true;
    }

    public void anadirVertice(Circulo cir) {
        Vertice v = new Vertice(cir, vertices.size());
        vertices.add(v);
        listaAdyacencia.put(v.id, new ArrayList<Integer>());
        nVertices++;
    }

    public void eliminaVertice(int x, int y) {
        calcularClosestPairOfPoints(naranja, verde, verde);
        agregarTodosLosIds(verde);
        Circulo cir = buscarCirculo(x, y, verde, blanco);
        cir.calculaCentro();
        System.out.println("yo: (" + x + "," + y + ")");
        System.out.println("cir: (" + cir.centro.x + "," + cir.centro.y + ")");
        Vertice a = quien(cir);
        if( a == null ){
            System.out.println("Nodo ya eliminado!");
        }else{
            System.out.println("a: (" + a.cir.centro.x + "," + a.cir.centro.y + ")");
            ArrayList<Integer> ady = listaAdyacencia.get(a.id);
            for (int i: ady) {
                Arista ari = aristas.get(i);
                Vertice b = a.compareTo(ari.a) == 0 ? ari.b: ari.a;
                // System.out.println("Adios arista " + ari.a.id + " <-> "+ ari.b.id);
                listaAdyacencia.get(b.id).remove(Integer.valueOf(ari.id));
                pintarLinea(ari.linea, blanco);
                eliminaArista(ari);
            }
            listaAdyacencia.remove(a.id);
            vertices.remove(a);
            nVertices--;
        }
        calcularClosestPairOfPoints(verde, naranja, gris);
        agregarTodosLosIds(gris);
        System.out.println("Nodo " + a.id + " eliminado satisfactoriamente.");
    }

    public void anadirArista(Vertice a, Vertice b, ArrayList<Punto> linea) {
        Arista ari = new Arista(a, b, linea, aristas.size());
        listaAdyacencia.get(a.id).add(ari.id);
        listaAdyacencia.get(b.id).add(ari.id);
        aristas.add(ari);
        nAristas++;
    }

    public void eliminaArista(Arista arista){
        // Ahorita vemos qué pedo D:
       nAristas--;
    }
}