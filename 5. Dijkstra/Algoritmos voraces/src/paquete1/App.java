package paquete1;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class App extends JFrame {
    JPanel ventanaPrincipal;
    JPanel panelIzquierdo;
    Dimension dimImagenFondo = new Dimension(1000, 800);
    JLayeredPane panelPorCapasImagenFondo = new JLayeredPane();
    JLabel labelImagenFondo = new JLabel();
    JPanel panelImagenFondo = new JPanel();
    int tamanoObjeto = 0;

    JPanel panelDerecho;
    JButton btnSeleccionarImagen;
    JButton btnAgregarAgente;
    JButton btnAgregarSenuelo;
    JButton btnAnimar;
    JScrollPane scrollTabla;
    JTable tabla;
    private JButton btnMostarCaminos;

    ArrayList<Integer> caminoDijkstra = new ArrayList<>();

    JFileChooser seleccion = new JFileChooser();
    GestionDeImagenes gestorImagenes = new GestionDeImagenes();
    File archivo;
    int queHacer;

    BufferedImage imagen;
    Agente agente;
    Senuelo senuelo;
    Grafo grafo;
    int[][] caminoVertice;
    int[][] idUsadoArista;
    int accionAgregarAgente = 1;
    int accionAgregarSenuelo = 2;

    public App() {

        panelPorCapasImagenFondo.setMinimumSize(dimImagenFondo);
        panelPorCapasImagenFondo.setMaximumSize(dimImagenFondo);
        panelPorCapasImagenFondo.setPreferredSize(dimImagenFondo);
        panelPorCapasImagenFondo.setBounds(0, 0, (int) dimImagenFondo.getWidth(), (int) dimImagenFondo.getHeight());
        panelIzquierdo.add(panelPorCapasImagenFondo);

        btnSeleccionarImagen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                // Resetear todo, hacerlo todo D:
                panelPorCapasImagenFondo.removeAll();
                queHacer = 0;
                reiniciaTodo();
                seleccionaImagen();
                quitarRuido();
                buscarCirculos();
                construirGrafo();
                tamanoObjeto = 2 * grafo.vertices.get(0).cir.radio() + 5;
                // System.out.println("Tamaño objeto: " + tamanoObjeto);
                // System.out.println("w: " + imagen.getWidth() + " h: " + imagen.getHeight());
                ImageIcon imagenSeleccionada = new ImageIcon(new ImageIcon(imagen).getImage());
                labelImagenFondo.setIcon(imagenSeleccionada);
                panelImagenFondo.setBounds(0, 0, (int) dimImagenFondo.getWidth(), (int) dimImagenFondo.getHeight());
                panelImagenFondo.add(labelImagenFondo);
                labelImagenFondo.repaint();
                panelPorCapasImagenFondo.add(panelImagenFondo);
                crearTabla();
            }
        });

        btnAgregarAgente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queHacer = accionAgregarAgente;
            }
        });

        btnAgregarSenuelo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queHacer = accionAgregarSenuelo;
            }
        });

        panelImagenFondo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            switch (queHacer) {
                case 1: {
                    // System.out.println("Agregar agente");
                    agregarAgente(tamanoObjeto, e.getX(), e.getY());
                    break;
                }

                case 2: {
                    // System.out.println("Agregar señuelo");
                    agregarSenuelo(tamanoObjeto, e.getX(), e.getY());
                    break;
                }

                case 3:{
                    mostrarCaminoVisualmente(e.getX(), e.getY());
                    break;
                }

                default:
                    mostrarMensaje("No ha seleccionado ninguna acción a realizar");
                    break;
            }
            }
        });

        btnAnimar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if( agente == null  ){
                    mostrarMensaje("No hay agente en el grafo");
                }else if( !senuelo.existe ){
                    mostrarMensaje("No hay señuelo disponible");
                }else{
                    // System.out.println("Animar agentes");
                    moverAgente();
                    int movimientos = agente.porCaminar.size();
                    hilo = new Hilo(movimientos);
                }
            }
        });

        btnMostarCaminos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queHacer = 3;
                System.out.println("Mostrar caminos");
            }
        });
    }

    public void reiniciaTodo(){
        agente = null; // ¿O debe de tener valor?
        senuelo = new Senuelo(new Punto(-1, -1), 40);
        grafo = new Grafo();
    }

    public void mostrarMensaje(String textoMensaje) {
        // Muestra un mensaje de acuerdo a la string proporcionada
        JOptionPane.showMessageDialog(null, textoMensaje);
    }

    public boolean esArchivoValido(String[] nombreArchivo) {
        // Determina si el archivo tiene alguna de las terminaciones dadas en cual[]
        for (int i = 0; i < nombreArchivo.length; i++) {
            if (archivo.getName().endsWith(nombreArchivo[i])) {
                return true;
            }
        }
        return false;
    }

    public BufferedImage byteToBufferedImage(byte[] a) {
        // Parece que funciona, pero NO le creas, revísalo a conciencia
        ByteArrayInputStream bais = new ByteArrayInputStream(a);
        BufferedImage b = null;
        try {
            b = ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    public byte[] bufferedImageToByte(BufferedImage a) {
        // Parece que funciona, pero NO le creas, revísalo a conciencia
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(a, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] b = baos.toByteArray();
        return b;
    }

    public void seleccionaImagen() {
        // Selecciona la imagen usando un JFileChooser
        if (seleccion.showDialog(this, "Abrir imagen") == JFileChooser.APPROVE_OPTION) {
            archivo = seleccion.getSelectedFile();
            if (archivo.canRead()) {
                if (esArchivoValido(new String[] {"png", "jpg", "gif"})) {
                    BufferedImage imagenOriginal = (BufferedImage) byteToBufferedImage(gestorImagenes.abrirImagen(archivo));
                    // System.out.println("w: " + imagenOriginal.getWidth() + " h:" + imagenOriginal.getHeight());
                    Image tmp = imagenOriginal.getScaledInstance((int) dimImagenFondo.getWidth(), (int) dimImagenFondo.getHeight(), Image.SCALE_SMOOTH);
                    BufferedImage imagenEscalada = new BufferedImage((int) dimImagenFondo.getWidth(), (int) dimImagenFondo.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = imagenEscalada.createGraphics();
                    g2d.drawImage(tmp, 0, 0, null);
                    g2d.dispose();
                    imagen = imagenEscalada;
                    tabla.setModel(new javax.swing.table.DefaultTableModel(
                            null,
                            new String[] {"ID", "Adyacentes"}));
                } else {
                    mostrarMensaje("Se esperaba una extension de tipo imagen");
                }
            } else {
                mostrarMensaje("Algo falló D:,¡no se puede leer!");
            }
        }
    }

    public void crearTabla() {
        // Crea la tabla de información a ser desplegada
        // String[][] filasTablaCirculos = new String[grafo.vertices.size()][2];

        String[][] fila = new String[1][1];

        fila[0][0] = " ";
        if( caminoDijkstra.isEmpty() ){
            fila[0][0] = "Imposible";
        }else{
            for(Integer u: caminoDijkstra){
                fila[0][0] += u.toString() + " -> ";
            }
        }
        fila[0][0]  = fila[0][0].substring(0, fila[0][0].length() - 3);
        tabla.setModel(new javax.swing.table.DefaultTableModel(
                fila,
                new String[] {"Camino calculado: "}
                ));
    }


    Color colorActual;
    Color negro = new Color(0, 0, 0);
    Color gris = new Color(50, 50, 50);
    Color azul = new Color(21, 65, 245);
    Color verdeObscuro = new Color(0, 180, 90);
    Color verde = new Color(0, 255, 62);
    Color rosado = new Color(255, 65, 255);
    Color morado = new Color(120, 31, 221);
    Color rojo = new Color(199, 0, 0);
    Color rojo2 = new Color(245, 0, 0);
    Color naranja = new Color(255, 128, 0);
    Color amarillo = new Color(247, 250, 43);
    Color blanco = new Color(255, 255, 255);
    Color prob = new Color(254, 254, 254);

    public static Color blend(Color c0, Color c1) {
        double totalAlpha = c0.getAlpha() + c1.getAlpha();
        double weight0 = c0.getAlpha() / totalAlpha;
        double weight1 = c1.getAlpha() / totalAlpha;

        double r = weight0 * c0.getRed() + weight1 * c1.getRed();
        double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
        double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
        double a = Math.max(c0.getAlpha(), c1.getAlpha());

        return new Color((int) r, (int) g, (int) b, (int) a);
    }

    Hilo hilo;

    public class Hilo implements Runnable{
        boolean detener = false;
        Thread t;
        int tiempoDeEspera;
        int movimientos;
        boolean haySenuelo;

        public Hilo(int movimientos){
            this.haySenuelo = true;
            this.tiempoDeEspera = 25;
            this.movimientos = movimientos;
            t = new Thread(this, "Hilo que si jala");
            inicia();
        }

        public void run(){
            try{
                while( haySenuelo ){
                    System.out.println("Cantidad de movimientos " +  movimientos);
                    for(int i = 0; i < movimientos; i++){
                        // System.out.println(Thread.currentThread().getName() + ": " + i);
                        panelPorCapasImagenFondo.add(senuelo, 0);
                        agente.avanza();
                        panelPorCapasImagenFondo.add(agente, 0);
                        Thread.currentThread().sleep(tiempoDeEspera);

                        synchronized(this){
                            while(detener == true){
                                wait();
                            }
                        }
                    }
                    System.out.println("Ya no hay señuelo ");
                    haySenuelo = false;

                    System.out.println("Hay comida: " +  haySenuelo);
                    System.out.println("Me lo comí, yomi yomi");
                    // senuelo.setBackground(verde);
                    // panelPorCapasImagenFondo.add(senuelo, 0);

                    senuelo.setVisible(false);
                    panelPorCapasImagenFondo.add(senuelo, 0);
                    senuelo.existe = false;
                    detente();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        synchronized void inicia(){
            t.start();
            System.out.println("Inicia");
        }

        synchronized void detente(){
            detener = true;
            System.out.println("Pausa");
        }

        synchronized void continuar(){
            detener = false;
            notify();
            System.out.println("Continuar");
        }
    }

    public boolean esRuido(Color color){
        return color.getRed() == color.getGreen() && color.getGreen() == color.getBlue()
                && 0 < color.getRed() && color.getRed() < 255
                && 0 < color.getGreen() && color.getGreen() < 255
                && 0 < color.getBlue() && color.getBlue() < 255;
    }

    public void quitarRuido() {
        // Pinta lo que tenga colores "negros" de negro
        for (int y = 0; y < imagen.getHeight(); y++) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                colorActual = new Color(imagen.getRGB(x, y));
                if ( esRuido(colorActual) ) {
                    imagen.setRGB(x, y, blanco.getRGB());
                }
            }
        }
    }

    private boolean esCoordenadaValida(int x, int y) {
        // Valida que una pixel(x, y) se encuentre en la imagen
        return 0 <= x && x < imagen.getWidth() && 0 <= y && y < imagen.getHeight();
    }

    private Circulo buscarCirculo(int x, int y, Color colorInicial, Color colorFinal) {
        /*
         Empezando desde el pixel(x, y) nos expandiremos a todos sus adyacentes siempre y cuando esos tengan el color inicio y
         para ahorrarnos nuestra matriz de visitados[][] vamos a pintar de color fin, de está manera no procesaremos un pixel dos veces.
         Corremos una bfs que nos resuelve todo el proceso anteriormente descrito.
         */
        int[] dx = new int[] { 1, 0, -1, 0};
        int[] dy = new int[] { 0, 1, 0, -1};
        Circulo cir = new Circulo(x, y); // Actualmente es un punto (x, y)
        Queue< Punto > qu = new LinkedList< >();
        qu.add(new Punto(x, y));
        imagen.setRGB(x, y, colorFinal.getRGB());
        while (!qu.isEmpty()) {
            Punto actual = qu.poll();
            for (int k = 0; k < 4; k++) {
                int nx = actual.x + dx[k];
                int ny = actual.y + dy[k];
                if (!esCoordenadaValida(nx, ny)) {
                    cir.tocaBorde = true;
                    continue;
                }
                colorActual = new Color(imagen.getRGB(nx, ny));
                if (colorActual.getRGB() == colorInicial.getRGB()) {
                    qu.add(new Punto(nx, ny));
                    cir.expande(nx, ny);
                    imagen.setRGB(nx, ny, colorFinal.getRGB());
                }
            }
        }
        cir.calculaCentro(); // Calculamos el centro de nuestro círculo
        return cir;
    }

    public void buscarCirculos() {
        /*
         Para cada pixel(x, y) vemos que sea de color negro (indicando que es un círculo) y pasamos a pintarlo de color verde, después verificamos que
         sea efectivamente un círculo (desechar óvalos, figuras extrañas) y lo agregamos a nuestra lista de círculos.
         */
        Circulo cir;
        for (int y = 0; y < imagen.getHeight(); y++) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                colorActual = new Color(imagen.getRGB(x, y));
                if (colorActual.getRGB() == negro.getRGB()) {
                    cir = buscarCirculo(x, y, negro, rosado);
                    if (cir.esCirculo()) {
                        grafo.anadirVertice(cir);
                    } else {
                        pintarFigura(cir, rosado, azul);
                    }
                }
            }
        }
    }



    private void pintarFigura(int x, int y, Color colorInicial, Color colorFinal) {
        // Desde el pixel(x, y) mientras sea de "colorInicio" lo cambiamos a "colorFinal"
        int dx[] = new int[] {-1, +0, +1, -1, +1, -1, +0, +1};
        int dy[] = new int[] {-1, -1, -1, +0, +0, +1, +1, +1};
        Queue < Punto > qu = new LinkedList < > ();
        qu.add(new Punto(x, y));
        imagen.setRGB(x, y, colorFinal.getRGB());
        while (!qu.isEmpty()) {
            Punto actual = qu.poll();
            for (int k = 0; k < 8; k++) {
                int nx = actual.x + dx[k];
                int ny = actual.y + dy[k];
                if (!esCoordenadaValida(nx, ny)) {
                    continue;
                }
                colorActual = new Color(imagen.getRGB(nx, ny));
                if (colorActual.getRGB() == colorInicial.getRGB()) {
                    qu.add(new Punto(nx, ny));
                    imagen.setRGB(nx, ny, colorFinal.getRGB());
                }
            }
        }
    }

    private void pintarFigura(Circulo cir, Color colorInicial, Color colorFinal) {
        pintarFigura(cir.centro.x, cir.centro.y, colorInicial, colorFinal);
    }

    private void pintarFigura(Vertice v, Color colorInicial, Color colorFinal) {
        pintarFigura(v.cir, colorInicial, colorFinal);
    }

    private void pintarLinea(ArrayList<Punto> linea, Color colorLinea) {
        for (int i = 0; i < linea.size(); i++) {
            Punto pt = linea.get(i);
            imagen.setRGB(pt.x, pt.y, colorLinea.getRGB());
        }
    }

    private void pintarLineaBoldUp(ArrayList<Punto> linea, Color colorLinea) {
        for (int i = 0; i < linea.size(); i++) {
            Punto pt = linea.get(i);
            if (45d <= angulo(linea) && angulo(linea) <= 135d || 225d <= angulo(linea) && angulo(linea) <= 315d) {
                imagen.setRGB(pt.x, pt.y, colorLinea.getRGB());
                imagen.setRGB(pt.x + 1, pt.y, colorLinea.getRGB());
            }else{
                imagen.setRGB(pt.x, pt.y, colorLinea.getRGB());
                imagen.setRGB(pt.x, pt.y + 1, colorLinea.getRGB());
            }
        }
    }

    public double angulo(ArrayList<Punto> linea){
        int ult = linea.size() - 1;
        Punto a = linea.get(0);
        Punto b = linea.get(ult);
        double ang = Math.toDegrees(Math.atan2(a.y - b.y, b.x - a.x));
        return ang + (ang < 0 ? 360d: 0d);
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
            if (imagen.getRGB(x1, y1) == colorInicio.getRGB() ||
                    imagen.getRGB(x1, y1) == colorFinal.getRGB() ||
                    imagen.getRGB(x1, y1) == blanco.getRGB() ) {
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

    private ArrayList<Punto> buscarCamino(Circulo a, Circulo b, Color colorInicial, Color colorFinal) {
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
            if (imagen.getRGB(x1, y1) == colorInicial.getRGB() || imagen.getRGB(x1, y1) == colorFinal.getRGB()) {
                // Estás sobre uno de los dos círculos
            }else if( imagen.getRGB(x1, y1) == blanco.getRGB() ){
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

    private ArrayList<Punto> buscarCamino(Vertice a, Vertice b, Color colorInicial, Color colorFinal) {
        return buscarCamino(a.cir, b.cir, colorInicial, colorFinal);
    }



    private void anadirId(Vertice v, Color colorLetras) {
        if( imagen.getRGB(v.cir.centro.x, v.cir.centro.y) == blanco.getRGB() ){
            // El circulo/vertice ha sido borrado, no ha necesidad de hacer algo
        }else{
            // Se añade el v.id a partir del centro del círculo
            BufferedImage tmp = new BufferedImage(imagen.getWidth(), imagen.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = tmp.createGraphics();
            g2d.drawImage(imagen, 0, 0, imagen.getWidth(), imagen.getHeight(), null);
            g2d.setPaint(colorLetras);
            g2d.setFont(new Font("Calibri", Font.ITALIC, 14));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(String.valueOf(v.id), v.cir.centro.x - 10, v.cir.centro.y + 10);
            g2d.dispose();
            imagen = tmp;
        }
    }

    private void agregarTodosLosIds(Color colorLetras) {
        for (Vertice v: grafo.vertices) {
            anadirId(v, colorLetras);
        }
    }

    public void construirGrafo() {
        for (int i = 0; i < grafo.vertices.size(); i++) {
            Vertice a = grafo.vertices.get(i);
            pintarFigura(a, rosado, morado);
            for (int j = i + 1; j < grafo.vertices.size(); j++) {
                Vertice b = grafo.vertices.get(j);
                pintarFigura(b, rosado, verde);
                ArrayList<Punto> linea = buscarCamino(a, b, morado, verde);
                if( !linea.isEmpty() ){
                    grafo.anadirArista(a, b, linea);
                    Collections.reverse(linea);
                    grafo.anadirArista(b, a, linea);
                }
                pintarFigura(b, verde, rosado);
            }
            pintarFigura(a, morado, rosado);
        }

        System.out.println("Num de aristas: " + grafo.numAristas());
        System.out.println("Num de vertices: " + grafo.numVertices());

        for (Arista ari: grafo.aristas) {
            pintarLinea(ari.linea, negro);
        }
        agregarTodosLosIds(gris);
        caminoVertice = new int[grafo.vertices.size()][];
        idUsadoArista = new int[grafo.vertices.size()][];
        for(int u = 0; u < grafo.vertices.size(); u++){
            caminoVertice[u] = new int[grafo.vertices.size()];
            idUsadoArista[u] = new int[grafo.vertices.size()];
            dijkstra(u, caminoVertice[u], idUsadoArista[u]);
        }
    }


    void agregarAgente(int tamano, int x, int y){
        if( agente == null ){
            System.out.println("El agente es null");
        }else{
            System.out.println(agente);
        }
        // Busco el vértice más cercano a (x, y)
        Vertice v = new Vertice(new Circulo(100000000, 10000000), -1);
        for(Vertice v2: grafo.vertices){
            if( v.id == -1 || Math.abs(v2.cir.centro.x - x) + Math.abs(v2.cir.centro.y - y) <= Math.abs(v.cir.centro.x - x) + Math.abs(v.cir.centro.y - y) ){
                v = v2;
            }
        }
        boolean libre = true;
        if( v.cir.centro.compareTo(senuelo.pos) == 0 ){
            libre = false;
        }
        if( libre ){
            System.out.println("Añade al agente en el vertice: " + v.id);
            agente = new Agente("Mono ", v.cir.centro, tamano);
            panelPorCapasImagenFondo.add(agente, 0);
            // mostrarMensaje("Agente agregado con exito");
        }else{
            mostrarMensaje("Ya existe un objeto ahí, pruebe en otro lado\n");
        }
    }

    void agregarSenuelo(int tamano, int x, int y){
        if( senuelo.existe ){
            mostrarMensaje("Ya existe señuelo, ¡debe de animar!");
        }else{
            // Busco el vértice más cercano a (x, y)
            Vertice v = new Vertice(new Circulo(100000000, 10000000), -1);
            for(Vertice v2: grafo.vertices){
                if( v.id == -1 || Math.abs(v2.cir.centro.x - x) + Math.abs(v2.cir.centro.y - y) <= Math.abs(v.cir.centro.x - x) + Math.abs(v.cir.centro.y - y) ){
                    v = v2;
                }
            }
            System.out.println("Añade señuelo en el vertice: " + v.id);
            boolean libre = true;
            if( agente != null && agente.pos.compareTo(v.cir.centro) == 0 ){
                libre = false;
            }
            if( libre ){
                senuelo = new Senuelo(v.cir.centro, tamano);
                panelPorCapasImagenFondo.add(senuelo, 1);
                // mostrarMensaje("Señuelo agregado con exito en " + new Punto(x, y));
            }else{
                mostrarMensaje("Ya existe un objeto ahí, pruebe en otro lado\n");
            }
        }
    }

    boolean esVertice(Punto pt){
        for(Vertice v: grafo.vertices){
            if( v.cir.centro == pt ){
                return true;
            }
        }
        return false;
    }



    public class Estado{
        int u;
        double distancia;

        Estado(int uu, double d) {
            u = uu;
            distancia = d;
        }
    };

    public void dijkstra(int s, int[] camino, int[] usando){
        double[] menosDistancia = new double[grafo.vertices.size()];
        for(int i = 0; i < menosDistancia.length; i++) {
            menosDistancia[i] = Double.MAX_VALUE / 4;
            camino[i] = -1;
            usando[i] = -1;
        }
        PriorityQueue<Estado> pq = new PriorityQueue<Estado>(20, new Comparator<Estado>() {
            @Override
            public int compare(Estado o1, Estado o2) {
                return o1.distancia < o2.distancia ? -1: 1;
            }
        });
        Estado inicio = new Estado(s, 0);
        pq.add(inicio);
        menosDistancia[s] = 0;
        while( !pq.isEmpty() ){
            Estado anterior = pq.poll();
            if( anterior.distancia != menosDistancia[anterior.u] ){
                continue;
            }
            for(int id: grafo.ady.get(anterior.u)) {
                Arista ari = grafo.aristas.get(id);
                Estado actual = new Estado(ari.b.id, anterior.distancia + ari.a.cir.distancia(ari.b.cir));
                if( actual.distancia < menosDistancia[actual.u] ) {
                    menosDistancia[actual.u] = actual.distancia;
                    camino[actual.u] = anterior.u;
                    usando[actual.u] = id;
                    pq.add(actual);
                }
            }
        }
    }

    public ArrayList<Integer> getCamino(Vertice a, Vertice b){
        ArrayList<Integer> aristasUtilizadas = new ArrayList<>();
        caminoDijkstra = new ArrayList<>();
        if( idUsadoArista[a.id][b.id] == -1 ){
            return aristasUtilizadas;
        }
        while( b.id != a.id ){
            int id = idUsadoArista[a.id][b.id];
            caminoDijkstra.add(b.id);
            aristasUtilizadas.add(id);
            b = grafo.aristas.get(id).a;
        }
        caminoDijkstra.add(b.id);
        Collections.reverse(aristasUtilizadas);
        Collections.reverse(caminoDijkstra);
        return aristasUtilizadas;
    }

    public double distancia(Punto a, Punto b){
        return Math.hypot(a.y - b.y, a.x - b.x);
    }

    public void moverAgente(){
        Vertice fin = grafo.quien(new Circulo(senuelo.pos.x, senuelo.pos.y));

        Vertice inicio = grafo.quien(new Circulo(agente.pos.x, agente.pos.y));
        if( inicio == null && agente.porCaminar.size() > 0 ){
            inicio = grafo.quien(agente.porCaminar.getLast());
        }
        if( inicio == null ){
            System.out.println("Otro vertice null, algo hice mal");
        }

        System.out.print(agente.nombre + " está en ");

        // Regresa a un vertice
        if( !esVertice(agente.pos) ){
            System.out.println("una arista");
            // Debo de ir a un vertice que es el más cercano y de ahí ver qué hago
            Arista ari = grafo.aristas.get(agente.idUltimaArista);
            System.out.println("Va de " + ari.a.id + " -> " + ari.b.id);
            if( distancia(agente.pos, ari.a.cir.centro) + distancia(ari.a.cir.centro, fin.cir.centro) < distancia(agente.pos, ari.b.cir.centro) + distancia(ari.b.cir.centro, fin.cir.centro) ){
                agente.idUltimaArista ^= 1;
                ari = grafo.aristas.get(agente.idUltimaArista);
                System.out.println("Perdón, va de " + ari.a.id + " -> " + ari.b.id);
            }

            boolean agregar = false;
            int i = 0;
            while( i < ari.linea.size() ){
                if( agregar ){
                    agente.porCaminar.add(ari.linea.get(i));
                    agente.idArista.add(agente.idUltimaArista);
                }
                if( ari.linea.get(i).compareTo(agente.pos) == 0 ){
                    agregar = true;
                }
                if( agregar ){
                    i += agente.velocidad;
                    if( i >= ari.linea.size() ){
                        i = ari.linea.size() - 1;
                        agente.porCaminar.add(ari.linea.get(i));
                        agente.idArista.add(agente.idUltimaArista);
                        break;
                    }
                }else{
                    i++;
                }
            }
            inicio = ari.b;
        }else{
            System.out.println("el vertice " + inicio.id);
        }
        // Muévete desde el vértice
        ArrayList<Integer> camino = getCamino(inicio, fin);
        for(int id: camino){
            Arista ari = grafo.aristas.get(id);
            int i = 0;
            while( true ){
                agente.porCaminar.add(ari.linea.get(i));
                agente.idArista.add(id);
                i += agente.velocidad;
                if( i >= ari.linea.size() ){
                    i = ari.linea.size() - 1;
                    agente.porCaminar.add(ari.linea.get(i));
                    agente.idArista.add(id);
                    break;
                }
            }
        }
        crearTabla();
    }

    void mostrarCaminoVisualmente(int x, int y) {
        if (agente == null) {
            mostrarMensaje("No existe agente desde el cual calcular camino");
        } else {
            Vertice fin = new Vertice(new Circulo(100000000, 10000000), -1);
            for (Vertice v2 : grafo.vertices) {
                if (fin.id == -1 || Math.abs(v2.cir.centro.x - x) + Math.abs(v2.cir.centro.y - y) <= Math.abs(fin.cir.centro.x - x) + Math.abs(fin.cir.centro.y - y)) {
                    fin = v2;
                }
            }
            Vertice inicio = grafo.quien(new Circulo(agente.pos.x, agente.pos.y));
            if( inicio == null && agente.porCaminar.size() > 0 ){
                inicio = grafo.quien(agente.porCaminar.getLast());
            }
            System.out.println("Muestra camino " + inicio.id + " <-> " + fin.id);
            for(Arista ari: grafo.aristas){
                pintarLineaBoldUp(ari.linea, blanco);
                pintarLinea(ari.linea, negro);
            }
            ArrayList<Integer> camino = getCamino(inicio, fin);
            for(int id: camino) {
                Arista ari = grafo.aristas.get(id);
                System.out.println(ari.a.id + " <-> " + ari.b.id);
                pintarLineaBoldUp(ari.linea, naranja);
            }
            System.out.println("----");
            panelPorCapasImagenFondo.repaint();
            labelImagenFondo.repaint();
        }
    }
}