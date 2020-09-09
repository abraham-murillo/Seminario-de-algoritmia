package Proyecto1;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Solucionador {
    public BufferedImage imagenModificada;
    public Color colorActual;
    public Color negro = new Color(0, 0, 0);
    public Color gris = new Color(50, 50, 50);
    public Color azul = new Color(21, 65, 245);
    public Color verde = new Color(0, 192, 30);
    public Color morado = new Color(120, 31, 221);
    public Color rojo = new Color(199, 0, 0);
    public Color naranja = new Color(255, 160, 90);
    public Color amarillo = new Color(247, 250, 43);
    public Color blanco = new Color(255, 255, 255);
    public ArrayList<Circulo> circulos;

    int[] dx = {1, 0, -1, 0};
    int[] dy = {0, 1, 0, -1};

    Solucionador() { }

    // Pinta lo que NO sea blanco de color negro
    public void quitarRuido() {
        for (int y = 0; y < imagenModificada.getHeight(); y++) {
            for (int x = 0; x < imagenModificada.getWidth(); x++) {
                colorActual = new Color(imagenModificada.getRGB(x, y));
                if (colorActual.getRGB() != blanco.getRGB()) {
                    imagenModificada.setRGB(x, y, negro.getRGB());
                }
            }
        }
    }

    public boolean esValido(int x, int y) {
        return 0 <= x && x < imagenModificada.getWidth() && 0 <= y && y < imagenModificada.getHeight();
    }

    public boolean esFantasma(int x, int y){
        return -1 <= x && x < imagenModificada.getWidth() + 1 && -1 <= y && y < imagenModificada.getHeight() + 1;
    }

    public void pintar(int x, int y, Color from, Color to){
        Queue<Punto> qu = new LinkedList<>();
        qu.add(new Punto(x, y));
        imagenModificada.setRGB(x, y, to.getRGB());
        while (!qu.isEmpty()) {
            Punto actual = qu.poll();
            for (int k = 0; k < 4; k++) {
                int nx = actual.x + dx[k];
                int ny = actual.y + dy[k];
                if (!esValido(nx, ny)) {
                    continue;
                }
                colorActual = new Color(imagenModificada.getRGB(nx, ny));
                if (colorActual.getRGB() == from.getRGB() ) {
                    qu.add(new Punto(nx, ny));
                    imagenModificada.setRGB(nx, ny, to.getRGB());
                }
            }
        }
    }

    public void extra(){
        for(int y = 0; y < imagenModificada.getHeight(); y++){
            for(int x = 0; x < imagenModificada.getWidth(); x++){
                colorActual = new Color(imagenModificada.getRGB(x, y));
                if( colorActual.getRGB() == blanco.getRGB() ){
                    imagenModificada.setRGB(x, y, new Color(0.0f, 0.0f, 0.0f, 0.0f).getRGB());
                }
            }
        }
    }

    public void marcaFondo(Color from, Color to){
        boolean[][] vis = new boolean[imagenModificada.getWidth() + 2][imagenModificada.getHeight() + 2];
        // "Pintar" tod el fondo de color "to"
        for(int y = 0; y < imagenModificada.getHeight(); y++){
            for(int x = 0; x < imagenModificada.getWidth(); x++){
                vis[x + 1][y + 1] = true;
            }
        }
        // "Pintar" lo que es color "from"
        for(int y = 0; y < imagenModificada.getHeight(); y++){
            for(int x = 0; x < imagenModificada.getWidth(); x++){
                colorActual = new Color(imagenModificada.getRGB(x, y));
                if( colorActual.getRGB() == from.getRGB() ) {
                    vis[x + 1][y + 1] = false;
                }
            }
        }
        Queue<Punto> qu = new LinkedList<>();
        qu.add(new Punto(-1, -1)); // Punto fantasma
        vis[-1 + 1][-1 + 1] = true;
        while (!qu.isEmpty()) {
            Punto actual = qu.poll();
            for (int k = 0; k < 4; k++) {
                int nx = actual.x + dx[k];
                int ny = actual.y + dy[k];
                if( esValido(nx, ny) ){
                    colorActual = new Color(imagenModificada.getRGB(nx, ny));
                }else if( esFantasma(nx, ny) ){
                    colorActual = to;
                }else{
                    continue;
                }
                if (  !vis[nx + 1][ny + 1] ) {
                    qu.add(new Punto(nx, ny));
                    if( esValido(nx, ny) ){
                        imagenModificada.setRGB(nx, ny, to.getRGB());
                    }
                    vis[nx + 1][ny + 1] = true;
                }
            }
        }
        /*
        for(int y = 0; y < imagenModificada.getHeight(); y++){
            for(int x = 0; x < imagenModificada.getWidth(); x++){
                colorActual = new Color(imagenModificada.getRGB(x, y));
                if( colorActual.getRGB() == from.getRGB() ){
                    pintar(x, y, colorActual, to);
                    return;
                }
            }
        }
         */
    }

    public void eliminaDonas(){
        for(int y = 0; y < imagenModificada.getHeight(); y++){
            for(int x = 0; x < imagenModificada.getWidth(); x++){
                colorActual = new Color(imagenModificada.getRGB(x, y));
                if( colorActual.getRGB() == blanco.getRGB() ){
                    pintar(x, y, blanco, negro);
                    pintar(x, y, negro, azul);
                }
            }
        }
    }

    public Circulo busca(int x, int y, Color from, Color to){
        Circulo cir = new Circulo(x, y); // Actualmente es un punto (x, y)
        Queue<Punto> qu = new LinkedList<>();
        qu.add(new Punto(x, y));
        imagenModificada.setRGB(x, y, to.getRGB());
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
                if (colorActual.getRGB() == from.getRGB() ) {
                    qu.add(new Punto(nx, ny));
                    cir.expande(nx, ny);
                    imagenModificada.setRGB(nx, ny, to.getRGB());
                }
            }
        }
        cir.calculaCentroide();
        return cir;
    }

    public void pintaCentro(Circulo cir){
        for(int dx = -3; dx <= 3; dx++){
            for(int dy = -3; dy <= 3; dy++){
                if( esValido(cir.centroide.x + dx, cir.centroide.y + dy) ){
                    imagenModificada.setRGB(cir.centroide.x + dx, cir.centroide.y + dy, morado.getRGB());
                }
            }
        }
    }

    public void anadirId(Circulo cir, int id){
        BufferedImage tmp = new BufferedImage(imagenModificada.getWidth(), imagenModificada.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tmp.createGraphics();
        g2d.drawImage(imagenModificada, 0, 0, imagenModificada.getWidth(), imagenModificada.getHeight(), null);
        g2d.setPaint(gris);
        g2d.setFont(new Font("Calibri", Font.ITALIC, 20));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(String.valueOf(id), cir.centroide.x, cir.centroide.y - 5);
        g2d.dispose();
        imagenModificada = tmp;
    }

    public void buscaCirculosEliminaOvalos(){
        circulos = new ArrayList<>();
        Circulo cir;
        for(int y = 0; y < imagenModificada.getHeight(); y++) {
            for (int x = 0; x < imagenModificada.getWidth(); x++) {
                colorActual = new Color(imagenModificada.getRGB(x, y));
                if (colorActual.getRGB() == negro.getRGB()) {
                    cir = busca(x, y, negro, verde);
                    if( cir.tocaBorde ){
                        pintar(cir.inicial.x, cir.inicial.y, verde, naranja);
                    }else if( cir.esCirculo() ){
                        pintaCentro(cir);
                        circulos.add(cir);
                        anadirId(cir, circulos.size());
                    }else{
                        pintar(cir.inicial.x, cir.inicial.y, verde, rojo);
                    }
                }
            }
        }
        /*
        for(int i = 0; i < circulos.size(); i++) {
            cir = circulos.get(i);
            if (cir.esCirculo()) {
                System.out.println("Circulo");
                System.out.println("Arriba: " + cir.arriba.y + ", Abajo: " + cir.abajo.y);
                System.out.println("Izquierda: " + cir.izquierda.y + ", Derecha: " + cir.derecha.y);
                System.out.println("Centroide(" + cir.centroide.x + "," + cir.centroide.y + ")");
                System.out.println("Radio: " + cir.radio());
                System.out.println("Toca borde: " + cir.tocaBorde);
            }
            System.out.println("---");
        }
         */
    }
}
