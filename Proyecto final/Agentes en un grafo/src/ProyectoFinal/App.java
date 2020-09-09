package ProyectoFinal;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class App extends JFrame {
    int velocidadAnimacion = 25;
    int velocidadPresa = 3;
    int velocidadDepredador = 5;

    JPanel panelVentanaPrincipal;

    JPanel panelIzquierdo;
    Dimension dimImagenFondo = new Dimension(1000, 800);
    JLayeredPane panelPorCapasImagenFondo = new JLayeredPane();
    JLabel labelImagenFondo = new JLabel();
    JPanel panelImagenFondo = new JPanel();
    int tamanoObjeto = 0;

    JPanel panelDerecho;
    JButton btnSeleccionarImagen;
    JButton btnAgregarPresa;
    JButton btnAgregarDepredador;
    JButton btnAgregarSenuelo;
    JButton btnPausar;

    JTable tabla;
    JScrollPane scrollTabla;
    private JButton btnContinuar;

    JFileChooser panelSeleccionImagen = new JFileChooser();
    GestionDeImagenes gestorImagenes = new GestionDeImagenes();
    File archivo;

    Color colorActual;
    Color negro = new Color(0, 0, 0);
    Color gris = new Color(50, 50, 50);
    Color roca = new Color(150, 150, 150);
    Color moradoAzul = new Color(120, 31, 255);
    Color azul = new Color(21, 65, 245);
    Color agua = new Color(20, 180, 255);
    Color verdeOscuro = new Color(53, 104, 45);
    Color verdePasto = new Color(0, 180, 90);
    Color verde = new Color(0, 255, 0);
    Color cafe = new Color(163, 120, 60);
    Color rosa = new Color(255, 65, 255);
    Color morado = new Color(120, 31, 221);
    Color rojo = new Color(199, 0, 0);
    Color rojoChido = new Color(255, 20, 0);
    Color naranjaOscuro = new Color(255, 80, 0);
    Color naranja = new Color(255, 128, 0);
    Color amarillo = new Color(247, 250, 43);
    Color amarilloBrillante = new Color(255, 255, 0);
    Color blanco = new Color(255, 255, 255);
    Color colorVertice = rosa;
    Color colorLetras = gris;
    Color colorSenuelo = morado;

    BufferedImage imagen;

    Vertice verticeSenuelo;


    Grafo grafo;
    int[][] caminoVertice;
    int[][] IDAristaUsada;
    Hilo hilo;

    class Pareja{
        Objeto presa;
        Objeto depredador;

        double distancia(){
            return presa.pos.distancia(depredador.pos);
        }

        Pareja(Objeto presa, Objeto depredador){
            this.presa = presa;
            this.depredador = depredador;
        }
    }

    public class Hilo implements Runnable{
        boolean detener = false;
        Thread t;
        int tiempoDeEspera;
        boolean haySenuelo;
        Collection<Objeto> presas;
        Collection<Objeto> depredadores;
        boolean primeraVez;

        public Hilo(){
            System.out.println("Hilo nuevo");
            this.haySenuelo = true;
            this.tiempoDeEspera = velocidadAnimacion;
            t = new Thread(this, "Hilito chingon");
            presas = new ArrayList<>();
            depredadores = new ArrayList<>();
            this.primeraVez = true;
        }

        public void pintarCaminoDepredador(){
            for (Arista ari: grafo.aristas)
                pintarLinea(ari.linea, negro);

            for (Objeto depredador: depredadores)
                for (Punto pt: depredador.porCaminar){
                    imagen.setRGB(pt.x, pt.y, depredador.color.getRGB());
                    imagen.setRGB(pt.x + 1, pt.y, depredador.color.getRGB());
                    imagen.setRGB(pt.x, pt.y + 1, depredador.color.getRGB());
                    imagen.setRGB(pt.x + 1, pt.y + 1, depredador.color.getRGB());
                }
            panelPorCapasImagenFondo.repaint();
        }

        public boolean esVertice(Punto pt){
            for(Vertice v: grafo.vertices)
                if( v.cir.centro.compareTo(pt) == 0 ){
                    // System.out.println("Pos del círculo: " + v.cir.centro);
                    return true;
                }
            return false;
        }

        public boolean algunDepredadorEnVertice(){
            boolean siHay = false;
            for(Objeto depredador: depredadores)
                if( esVertice(depredador.pos) ){
                    // System.out.println(depredador.nombre + " está en vértice");
                    siHay = true;
                }
            return siHay;
        }

        public void escapaLejos(Objeto objeto, int otro){
            objeto.porCaminar = new LinkedList<Punto>();
            objeto.IDArista = new LinkedList<Integer>();
            Arista ari = grafo.aristas.get(objeto.IDUltimaArista ^ otro);
            int i = 0;
            while( i < ari.linea.size() ){
                if( ari.linea.get(i).equals(objeto.pos) )
                    break;
                i++;
            }
            while( i < ari.linea.size() ){
                objeto.porCaminar.add(ari.linea.get(i));
                objeto.IDArista.add(objeto.IDUltimaArista ^ otro);
                i += objeto.velocidad;
            }
            i = ari.linea.size() - 1;
            objeto.porCaminar.add(ari.linea.get(i));
            objeto.IDArista.add(objeto.IDUltimaArista ^ otro);
        }

        public String getRuta(Objeto objeto){
            String ruta = "";
            Punto ultimo = null;
            for(Punto pt: objeto.porCaminar)
                if( esVertice(pt) )
                    if( ultimo == null || !ultimo.equals(pt) ){
                        ruta += verticeMasCercano(pt).id + " -> ";
                        ultimo = pt;
                    }
            return ruta;
        }

        void agregarCamino(Objeto objeto, ArrayList<Integer> camino){
            for(int id: camino){
                Arista ari = grafo.aristas.get(id);
                int i = 0;
                while( i < ari.linea.size() ){
                    objeto.porCaminar.add(ari.linea.get(i));
                    objeto.IDArista.add(id);
                    i += objeto.velocidad;
                }
                i = ari.linea.size() - 1;
                objeto.porCaminar.add(ari.linea.get(i));
                objeto.IDArista.add(id);
            }
        }

        public int caminaMenos(Objeto objeto, Vertice A, Vertice B){
            int pasos = (int) objeto.pos.distancia(A.cir.centro);
            ArrayList<Integer> camino = getCamino(A, B);
            for(int id: camino)
                pasos += grafo.aristas.get(id).linea.size();
            return pasos;
        }

        public void acechaOtraVez(){

            System.out.println("Acecha a todos otra vez");

            for(Objeto presa: presas){
                presa.pareja = null;
                presa.quitarVidaPareja = false;
            }

            for(Objeto depredador: depredadores) {
                depredador.pareja = null;
                depredador.quitarVidaPareja = false;
            }

            // Como un depredador ya está en un vértice, entonces buscará cuál es la presa más cercana a él
            // a un radio R cambiando a los demás depredadores su presa
            ArrayList<Pareja> posiblesParejas = new ArrayList<>();
            for(Objeto presa: presas){
                for(Objeto depredador: depredadores){
                    posiblesParejas.add(new Pareja(presa, depredador));
                }
            }

            // Ordenamos todas las parejas posibles depredador <-> presa por las distancia euclidiana a la que se encuentran
            Comparator<Pareja> masCercanos = (Pareja a, Pareja b) -> {
                return a.distancia() < b.distancia() ? -1 : 1;
            };
            Collections.sort(posiblesParejas, masCercanos);

            System.out.println("Ya todos están ordenados");
            HashSet<String> st = new HashSet<>();
            ArrayList<Pareja> parejasAcechadas = new ArrayList<>();

            for(Pareja par: posiblesParejas){
                if( st.contains(par.presa.nombre) || st.contains(par.depredador.nombre) )
                    continue;
                // Esta pareja la puedo unir
                st.add(par.presa.nombre);
                st.add(par.depredador.nombre);
                parejasAcechadas.add(par);
            }

            // Para cada pareja (cada Objeto) retiramos su ruta anteriormente calculada y la volvemos a calcular
            for(Pareja par: parejasAcechadas) {
                Objeto presa = par.presa;
                Objeto depredador = par.depredador;
                depredador.pareja = presa;
                presa.pareja = depredador;

                Arista ari = grafo.aristas.get(presa.IDUltimaArista);

                Vertice verticePresa;
                if( caminaMenos(presa, ari.a, verticeSenuelo) < caminaMenos(presa, ari.b, verticeSenuelo) ){
                    escapaLejos(presa, 1);
                    agregarCamino(presa, getCamino(ari.a, verticeSenuelo));
                    verticePresa = ari.a;
                }else{
                    escapaLejos(presa, 0);
                    agregarCamino(presa, getCamino(ari.b, verticeSenuelo));
                    verticePresa = ari.b;
                }

                escapaLejos(depredador, 0); // Sigue derecho lo que te queda de ruta
                Vertice verticeDepredador = grafo.aristas.get(depredador.IDUltimaArista).b;
                agregarCamino(depredador, getCamino(verticeDepredador, verticePresa));
                agregarCamino(depredador, getCamino(verticePresa, verticeSenuelo));

            }

            noDejesDeMoverte();

            System.out.println("Terminado de calcular, ya puedes animar");

        }

        void algunoASalvo(){
            for(Objeto presa: presas)
                if( presa.pos.equals(verticeSenuelo.cir.centro) ){
                    presa.vida += 1;
                    haySenuelo = false;
                }
        }

        void quitarVida(){
            // Si un objeto está a una distancia <= 2
            // - El depredador está en la misma arista y acaba que quitarle una cantidad 1 de vida, además deben de estar en la misma arista
            // - Está en otra arista que comparten el mismo punto de adyacencia
            ArrayList<Objeto> presasAtacadas = new ArrayList<Objeto>();
            for(Objeto depredador: depredadores)
                for(Objeto presa: presas)
                    if( depredador.pos.distancia(presa.pos) <= 5
                            && depredador.pareja != null
                            && depredador.pareja.nombre.equals(presa.nombre)
                            && !depredador.quitarVidaPareja ){
                        depredador.quitarVidaPareja = true;
                        presa.quitarVidaPareja = true;
                        presa.vida--;
                        if( presa.vida > 0 )
                            presasAtacadas.add(presa);
                    }
            // Eliminamos a las presas que ya no tienen vida :'c
            ArrayList<Objeto> comidos = new ArrayList<>();
            for(Objeto presa: presas)
                if( presa.vida == 0 )
                    comidos.add(presa);
            // No estoy seguro que funcione, tal vez se necesite borrar al elemento
            for(Objeto presa: comidos){
                panelPorCapasImagenFondo.remove(presa);
                presas.remove(presa);
            }
            // Hacemos maniobras evasivas para cada una de las presas atacadas
            // regresando al vértice de donde veníamos y de ahí buscamos un vértice cualquiera
            // Y reintentamos ir al señuelo
            System.out.println("Presas atacadas");
            for(Objeto presa: presasAtacadas){
                System.out.println(presa.nombre);
                presa.porCaminar = new LinkedList<Punto>();
                presa.IDArista = new LinkedList<Integer>();
                Vertice verticeAnterior = grafo.aristas.get(presa.IDUltimaArista).a;
                int j = 0;
                for(int i = 0; i < grafo.ady.get(verticeAnterior.id).size(); i++){
                    int id = grafo.ady.get(verticeAnterior.id).get(i);
                    if( id == presa.IDUltimaArista ){
                        j = (i + 1) % grafo.ady.get(verticeAnterior.id).size();
                        break;
                    }
                }
                Arista ari = grafo.aristas.get(grafo.ady.get(verticeAnterior.id).get(j));
                escapaLejos(presa, 1);
                agregarCamino(presa, getCamino(verticeAnterior, ari.b));
                agregarCamino(presa, getCamino(ari.b, verticeSenuelo));
            }
            // Repintamos para ver los cambios hechos
            panelPorCapasImagenFondo.repaint();
        }

        void imprimir(){
            System.out.println("-------------------------------------");
            for(Objeto presa: presas){
                System.out.println(presa);
                System.out.println(getRuta(presa));
            }
            for(Objeto depredador: depredadores){
                System.out.println(depredador);
                System.out.println(getRuta(depredador));
            }
            System.out.println("-------------------------------------\n");
        }

        void moverUnaVez(){
            System.out.println("Muévete");
            // Se mueve cada uno de los objetos un movimiento hacia su ruta planeada
            for(Objeto presa: presas){
                presa.avanza();
                panelPorCapasImagenFondo.add(presa, 0);
            }
            for(Objeto depredador: depredadores){
                depredador.avanza();
                panelPorCapasImagenFondo.add(depredador, 0);
            }
            quitarVida();
            algunoASalvo();
            panelPorCapasImagenFondo.repaint();
        }

        void noDejesDeMoverte(){
            System.out.println("No dejes de moverte, weyyyy");

            for(Objeto presa: presas)
                if( presa.porCaminar.size() <= 20 ){
                    Arista ari = grafo.aristas.get(presa.IDUltimaArista);
                    Vertice vPresa = null;
                    if( caminaMenos(presa, ari.a, verticeSenuelo) < caminaMenos(presa, ari.b, verticeSenuelo) ){
                        // Me voy al vértice A
                        escapaLejos(presa, 1);
                        agregarCamino(presa, getCamino(ari.a, verticeSenuelo));
                        vPresa = ari.a;
                    }else{
                        escapaLejos(presa, 0);
                        agregarCamino(presa, getCamino(ari.b, verticeSenuelo));
                        vPresa = ari.b;
                    }
                }

            ArrayList<Vertice> ultimosVerticePresas = new ArrayList<>();
            for(Objeto presa: presas){
                Arista ari = grafo.aristas.get(presa.IDUltimaArista);
                ultimosVerticePresas.add(ari.b);
            }

            if( ultimosVerticePresas.size() == 0 )
                return;

            for(Objeto depredador: depredadores)
                if( depredador.porCaminar.size() <= 20 ){
                    depredador.porCaminar = new LinkedList<Punto>();
                    depredador.IDArista = new LinkedList<Integer>();
                    escapaLejos(depredador, 0);
                    Vertice verticeDepredador = grafo.aristas.get(depredador.IDUltimaArista).b;
                    int k = (new Random().nextInt() & Integer.MAX_VALUE) % ultimosVerticePresas.size();
                    Vertice verticeX = null;
                    for(int i = 0; i < ultimosVerticePresas.size(); i++){
                        int j = (i + k) % ultimosVerticePresas.size();
                        if( verticeDepredador.id == ultimosVerticePresas.get(j).id || verticeSenuelo.id == ultimosVerticePresas.get(j).id ){
                           verticeX = grafo.vertices.get(j);
                           break;
                        }
                    }
                    if( verticeX == null ){
                        for(Vertice v: grafo.vertices)
                            if( v.id != verticeDepredador.id && v.id != verticeSenuelo.id ){
                                verticeX = v;
                                break;
                            }
                    }
                    agregarCamino(depredador, getCamino(verticeDepredador, verticeX));
                    agregarCamino(depredador, getCamino(verticeX, verticeSenuelo));

                    System.out.println(depredador.nombre + ": " + verticeDepredador.id + " -> " + verticeX.id + " -> " + verticeSenuelo.id);
                }

            imprimir();
        }

        public void run(){
            try{
                while( haySenuelo && !depredadores.isEmpty() && !presas.isEmpty() ){
                    moverUnaVez();
                    noDejesDeMoverte();
                    pintarCaminoDepredador();
                    if( algunDepredadorEnVertice() )
                        acechaOtraVez();
                    crearTabla();
                    Thread.currentThread().sleep(tiempoDeEspera);
                    if( !haySenuelo ){
                        System.out.println("Alguno llegó al señuelo");
                        System.out.println("------------------------------------\n");
                        pausar();
                    }
                    synchronized(this){
                        while(detener == true){
                            wait();
                        }
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        synchronized void iniciar(){
            if( !primeraVez )
                return;
            System.out.println("-------------------------------");
            System.out.println("Iniciar");
            // Como un depredador ya está en un vértice, entonces buscará cuál es la presa más cercana a él
            // a un radio R cambiando a los demás depredadores su presa
            ArrayList<Pareja> posiblesParejas = new ArrayList<>();
            for(Objeto presa: presas){
                for(Objeto depredador: depredadores){
                    posiblesParejas.add(new Pareja(presa, depredador));
                }
            }
            // Ordenamos todas las parejas posibles depredador <-> presa por las distancia euclidiana a la que se encuentran
            Comparator<Pareja> masCercanos = (Pareja a, Pareja b) -> {
                return a.distancia() < b.distancia() ? -1 : 1;
            };
            Collections.sort(posiblesParejas, masCercanos);
            System.out.println("Ya todos están ordenados");
            HashSet<String> st = new HashSet<>();
            ArrayList<Pareja> parejasAcechadas = new ArrayList<>();
            for(Pareja par: posiblesParejas){
                if( st.contains(par.presa.nombre) || st.contains(par.depredador.nombre) )
                    continue;
                // Esta pareja la puedo unir
                st.add(par.presa.nombre);
                st.add(par.depredador.nombre);
                parejasAcechadas.add(par);
            }
            // Para cada pareja (cada Objeto) retiramos su ruta anteriormente calculada y la volvemos a calcular
            for(Pareja par: parejasAcechadas) {
                // Sólo dejar el camino al depredador/presa para llegar al vértice siguiente y desde ahí calcular el nuevo camino a seguir
                Objeto depredador = par.depredador;
                Objeto presa = par.presa;
                depredador.pareja = presa;
                presa.pareja = depredador;

                Vertice vDepredador = verticeMasCercano(depredador.pos);
                Vertice vPresa = verticeMasCercano(presa.pos);

                // Ya saben qué hacer para llegar al primer vértice de su lista, ahora por Dijkstra calculan su nueva ruta

                ArrayList<Integer> caminoPresaSenuelo = getCamino(vPresa, verticeSenuelo);
                ArrayList<Integer> caminoDepredadorPresa = getCamino(vDepredador, vPresa);

                agregarCamino(depredador, caminoDepredadorPresa);
                agregarCamino(depredador, caminoPresaSenuelo);

                agregarCamino(presa, caminoPresaSenuelo);
            }

            for(Objeto presa: presas)
                if( !st.contains(presa.nombre) ){
                    Vertice vPresa = verticeMasCercano(presa.pos);
                    // System.out.println(presa.nombre + " está en vértice " + vPresa.id);
                    ArrayList<Integer> caminoPresaSenuelo = getCamino(vPresa, verticeSenuelo);
                    agregarCamino(presa, caminoPresaSenuelo);
                    st.add(presa.nombre);
                }
            for(Objeto depredador: depredadores)
                if( !st.contains(depredador.nombre) ){
                    Vertice vDepredador = verticeMasCercano(depredador.pos);
                    // System.out.println(presa.nombre + " está en vértice " + verticePresa.id);
                    ArrayList<Integer> caminoDepredadorSenuelo = getCamino(vDepredador, verticeSenuelo);
                    agregarCamino(depredador, caminoDepredadorSenuelo);
                    st.add(depredador.nombre);
                }
            t.start();
            imprimir();
            primeraVez = false;
        }

        synchronized void pausar(){
            detener = true;
            System.out.println("Pausa");
        }

        synchronized void continuar(){
            detener = false;
            notify();
            System.out.println("Continuar");
        }
    }

    final static int NADA = 0;
    final static int PRESA = 1;
    final static int DEPREDADOR = 2;
    final static int SENUELO = 3;
    int opc;

    public App() {
        panelPorCapasImagenFondo.setMinimumSize(dimImagenFondo);
        panelPorCapasImagenFondo.setMaximumSize(dimImagenFondo);
        panelPorCapasImagenFondo.setPreferredSize(dimImagenFondo);
        panelPorCapasImagenFondo.setBounds(0, 0, (int) dimImagenFondo.getWidth(), (int) dimImagenFondo.getHeight());
        panelIzquierdo.add(panelPorCapasImagenFondo);

        btnSeleccionarImagen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                // Resetear D:
                panelPorCapasImagenFondo.removeAll();
                opc = NADA;

                verticeSenuelo = null;

                grafo = new Grafo();
                hilo = new Hilo();
                btnContinuar.setEnabled(false);
                btnPausar.setEnabled(false);
                btnAgregarDepredador.setEnabled(true);
                btnAgregarPresa.setEnabled(true);

                seleccionarImagen();

                quitarRuido();
                buscarCirculos();
                construirGrafo();

                tamanoObjeto = 2 * grafo.vertices.get(0).cir.radio() + 5;
                ImageIcon imagenSeleccionada = new ImageIcon(new ImageIcon(imagen).getImage());
                labelImagenFondo.setIcon(imagenSeleccionada);
                panelImagenFondo.setBounds(0, 0, (int) dimImagenFondo.getWidth(), (int) dimImagenFondo.getHeight());
                panelImagenFondo.add(labelImagenFondo);
                labelImagenFondo.repaint();
                panelPorCapasImagenFondo.add(panelImagenFondo);

                crearTabla();
            }
        });

        btnAgregarPresa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opc = PRESA;
            }
        });

        btnAgregarDepredador.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opc = DEPREDADOR;
            }
        });

        btnAgregarSenuelo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opc = SENUELO;
            }
        });

        panelImagenFondo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            switch (opc) {
                case NADA:
                    mostrarMensaje("No ha seleccionado ninguna acción a realizar");
                    break;

                case PRESA:
                    agregarPresa(e.getX(), e.getY());
                    break;

                case DEPREDADOR:
                    agregarDepredador(e.getX(), e.getY());
                    break;

                case SENUELO:
                    agregarSenuelo(e.getX(), e.getY());
                    break;

                default:
                    break;
            }
            }
        });

        btnPausar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hilo.pausar();
            }
        });

        btnContinuar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if( hilo.haySenuelo )
                    hilo.continuar();
            }
        });
    }


    public void mostrarMensaje(String textoMensaje) {
        // Muestra un mensaje de acuerdo a la string proporcionada
        JOptionPane.showMessageDialog(null, textoMensaje);
    }

    public boolean esArchivoValido() {
        // Determina si el archivo tiene alguna de las terminaciones dadas en cual[]
        String[] terminacion = new String[]{"png", "jpg", "gif"};
        for (int i = 0; i < terminacion.length; i++)
            if (archivo.getName().endsWith(terminacion[i]))
                return true;
        return false;
    }

    public BufferedImage convierteByte2BufferedImage(byte[] a) {
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

    public byte[] convierteBufferedImage2Byte(BufferedImage a) {
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

    public void seleccionarImagen() {
        // Selecciona la imagen usando un JFileChooser
        if( panelSeleccionImagen.showDialog(this, "Abrir imagen") == JFileChooser.APPROVE_OPTION ){
            archivo = panelSeleccionImagen.getSelectedFile();
            if( archivo.canRead() ){
                if( esArchivoValido() ){
                    BufferedImage imagenOriginal = (BufferedImage) convierteByte2BufferedImage(gestorImagenes.abrirImagen(archivo));
                    System.out.println("w: " + imagenOriginal.getWidth() + " h:" + imagenOriginal.getHeight());
                    Image tmp = imagenOriginal.getScaledInstance((int) dimImagenFondo.getWidth(), (int) dimImagenFondo.getHeight(), Image.SCALE_SMOOTH);
                    BufferedImage imagenEscalada = new BufferedImage((int) dimImagenFondo.getWidth(), (int) dimImagenFondo.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = imagenEscalada.createGraphics();
                    g2d.drawImage(tmp, 0, 0, null);
                    g2d.dispose();
                    imagen = imagenEscalada;
                    tabla.setModel(new javax.swing.table.DefaultTableModel(
                            null,
                            new String[] {""}));
                } else {
                    mostrarMensaje("Se esperaba una extensión de tipo imagen");
                }
            } else {
                mostrarMensaje("Algo falló D:,¡no se puede leer!");
            }
        }
    }

    public void crearTabla() {
        // Crea la tabla de información a ser desplegada
        String[][] fila = new String[hilo.presas.size() + hilo.depredadores.size()][4];
        int i = 0;
        for(Objeto presa: hilo.presas){
            fila[i][0] = presa.nombre;
            fila[i][1] = presa.pos.toString();
            fila[i][2] = String.valueOf(presa.vida);
            fila[i][3] = (presa.pareja == null ? "": presa.pareja.nombre);
            i++;
        }
        for(Objeto depredador: hilo.depredadores){
            fila[i][0] = depredador.nombre;
            fila[i][1] = depredador.pos.toString();
            fila[i][2] = "inf";
            fila[i][3] = (depredador.pareja == null ? "": depredador.pareja.nombre);
            i++;
        }
        tabla.setModel(new javax.swing.table.DefaultTableModel(
                fila,
                new String[] {"Nombre", "Posición", "Vida", "Pareja"}
        ));
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
                if( esRuido(colorActual) )
                    imagen.setRGB(x, y, blanco.getRGB());
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
        Queue<Punto> qu = new LinkedList< >();
        qu.add(new Punto(x, y));
        imagen.setRGB(x, y, colorFinal.getRGB());
        while( !qu.isEmpty() ){
            Punto actual = qu.poll();
            for (int k = 0; k < 4; k++) {
                int nx = actual.x + dx[k];
                int ny = actual.y + dy[k];
                if( !esCoordenadaValida(nx, ny) ){
                    cir.tocaBorde = true;
                    continue;
                }
                colorActual = new Color(imagen.getRGB(nx, ny));
                if( colorActual.getRGB() == colorInicial.getRGB() ){
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
        // marcaFondo(blanco, verde);
        for (int y = 0; y < imagen.getHeight(); y++){
            for (int x = 0; x < imagen.getWidth(); x++) {
                colorActual = new Color(imagen.getRGB(x, y));
                if (colorActual.getRGB() == negro.getRGB()) {
                    Circulo cir = buscarCirculo(x, y, negro, colorVertice);
                    if (cir.esCirculo()) {
                        anadirVertice(cir);
                    } else {
                        pintarFigura(cir, colorVertice, azul);
                    }
                }
            }
        }
    }


    public boolean esFantasma(int x, int y){
        return -1 <= x && x < imagen.getWidth() + 1 && -1 <= y && y < imagen.getHeight() + 1;
    }

    public void marcaFondo(Color colorInicial, Color colorFinal) {
        boolean[][] vis = new boolean[imagen.getWidth() + 2][imagen.getHeight() + 2];
        // "Pintar" tod el fondo de color "colorFinal"
        for (int y = 0; y < imagen.getHeight(); y++) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                vis[x + 1][y + 1] = true;
            }
        }
        // "Pintar" lo que es color "colorInicial"
        for (int y = 0; y < imagen.getHeight(); y++) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                colorActual = new Color(imagen.getRGB(x, y));
                if (colorActual.getRGB() == colorInicial.getRGB()) {
                    vis[x + 1][y + 1] = false;
                }
            }
        }
        int dx[] = new int[] {-1, +0, +1, -1, +1, -1, +0, +1};
        int dy[] = new int[] {-1, -1, -1, +0, +0, +1, +1, +1};
        Queue<Punto> qu = new LinkedList<>();
        qu.add(new Punto(-1, -1)); // Punto fantasma
        vis[-1 + 1][-1 + 1] = true;
        while (!qu.isEmpty()) {
            Punto actual = qu.poll();
            for (int k = 0; k < 8; k++) {
                int nx = actual.x + dx[k];
                int ny = actual.y + dy[k];
                if (esCoordenadaValida(nx, ny)) {
                    colorActual = new Color(imagen.getRGB(nx, ny));
                } else if (esFantasma(nx, ny)) {
                    colorActual = colorFinal;
                } else {
                    continue;
                }
                if (!vis[nx + 1][ny + 1]) {
                    qu.add(new Punto(nx, ny));
                    if (esCoordenadaValida(nx, ny)) {
                        imagen.setRGB(nx, ny, colorFinal.getRGB());
                    }
                    vis[nx + 1][ny + 1] = true;
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

    private void pintarFigura(Punto pt, Color colorInicial, Color colorFinal) {
        pintarFigura(pt.x, pt.y, colorInicial, colorFinal);
    }

    private void pintarFigura(Vertice v, Color colorInicial, Color colorFinal) {
        pintarFigura(v.cir, colorInicial, colorFinal);
    }

    private void pintarLinea(ArrayList<Punto> linea, Color colorLinea) {
        Color[] colores = new Color[]{rojo, rojoChido, naranjaOscuro, naranja, amarillo, amarilloBrillante};
        for(Punto pt: linea){
            colorActual = new Color(imagen.getRGB(pt.x, pt.y));
            for(int k = 0; k < colores.length; k++)
                if( colorActual.getRGB() == colores[k].getRGB() ){
                    imagen.setRGB(pt.x + 1, pt.y, blanco.getRGB());
                    imagen.setRGB(pt.x, pt.y + 1, blanco.getRGB());
                    imagen.setRGB(pt.x + 1, pt.y + 1, blanco.getRGB());
                    break;
                }
        }
        for(Punto pt: linea)
            imagen.setRGB(pt.x, pt.y, colorLinea.getRGB());

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



    private void agregarID(Vertice v, Color colorLetras) {
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

    private void agregarIDS(Color colorLetras) {
        for (Vertice v: grafo.vertices) {
            agregarID(v, colorLetras);
        }
    }

    public void construirGrafo() {
        for (int i = 0; i < grafo.vertices.size(); i++) {
            Vertice a = grafo.vertices.get(i);
            pintarFigura(a, colorVertice, morado);
            for (int j = i + 1; j < grafo.vertices.size(); j++) {
                Vertice b = grafo.vertices.get(j);
                pintarFigura(b, colorVertice, verde);
                ArrayList<Punto> linea = buscarCamino(a, b, morado, verde);
                if( !linea.isEmpty() ){
                    anadirArista(a, b, linea);
                    Collections.reverse(linea);
                    anadirArista(b, a, linea);
                }
                pintarFigura(b, verde, colorVertice);
            }
            pintarFigura(a, morado, colorVertice);
        }
        for (Arista ari: grafo.aristas) {
            pintarLinea(ari.linea, negro);
        }
        agregarIDS(colorLetras);
        caminoVertice = new int[grafo.vertices.size()][];
        IDAristaUsada = new int[grafo.vertices.size()][];
        for(int u = 0; u < grafo.vertices.size(); u++){
            caminoVertice[u] = new int[grafo.vertices.size() + 5];
            IDAristaUsada[u] = new int[grafo.vertices.size() + 5];
            Dijkstra(u, caminoVertice[u], IDAristaUsada[u]);
        }
    }

    public void anadirVertice(Circulo cir) {
        Vertice v = new Vertice(cir, grafo.vertices.size());
        grafo.vertices.add(v);
        grafo.ady.put(v.id, new ArrayList<Integer>());
        grafo.nVertices++;
    }

    public void anadirArista(Vertice a, Vertice b, ArrayList<Punto> linea) {
        Arista ari = new Arista(a, b, linea, grafo.aristas.size());
        grafo.ady.get(a.id).add(ari.id);
        grafo.aristas.add(ari);
        grafo.nAristas++;
    }

    Vertice verticeMasCercano(int x, int y){
        return verticeMasCercano(new Punto(x, y));
    }

    Vertice verticeMasCercano(Punto pt){
        // Busco el vértice más cercano a pt(x, y)
        Vertice v = new Vertice(new Circulo(100000000, 10000000), -1);
        for(Vertice v2: grafo.vertices)
            if( v.id == -1 || pt.distancia(v2.cir.centro) < pt.distancia(v.cir.centro) )
                v = v2;
        return v;
    }

    void agregarPresa(int x, int y){
        if (hilo.presas.size() == 20)
            return;
        Vertice v = verticeMasCercano(x, y);
        System.out.println("Añade presa " + hilo.presas.size() + " en el vertice: " + v.id);
        // int p = (new Random().nextInt() & Integer.MAX_VALUE) % 3;
        // Color[] colores = new Color[]{moradoAzul, azul, agua};
        Objeto ultimo = new Objeto("PRE-" + String.valueOf(hilo.presas.size()), v.cir.centro, tamanoObjeto, azul, velocidadPresa);
        hilo.presas.add(ultimo);
        panelPorCapasImagenFondo.add(ultimo, 0);
    }

    void agregarDepredador(int x, int y){
        if (hilo.depredadores.size() == 6)
            return;
        Vertice v = verticeMasCercano(x, y);
        System.out.println("Añade depredador " + hilo.depredadores.size() + " en el vertice: " + v.id);
        // int p = (new Random().nextInt() & Integer.MAX_VALUE) % 4;
        Color[] colores = new Color[]{rojo, amarilloBrillante, rojoChido, amarillo, naranjaOscuro, naranja};
        Objeto ultimo = new Objeto("DEP-" + String.valueOf(hilo.depredadores.size()), v.cir.centro, tamanoObjeto, colores[hilo.depredadores.size()], velocidadDepredador);
        hilo.depredadores.add(ultimo);
        panelPorCapasImagenFondo.add(ultimo, 0);
    }

    void agregarSenuelo(int x, int y){
        System.out.println("Agregar señuelo");

        // Agregamos el señuelo pintándolo
        if( verticeSenuelo != null )
            pintarFigura(verticeSenuelo, colorSenuelo, colorVertice);
        verticeSenuelo = verticeMasCercano(x, y);
        pintarFigura(verticeSenuelo, colorVertice, colorSenuelo);
        panelPorCapasImagenFondo.repaint();

        if( verticeSenuelo != null ){
            btnContinuar.setEnabled(true);
            btnPausar.setEnabled(true);
        }

        // Vemos si ya podemos animar o si todavía no
        if( hilo.presas.isEmpty() )
            mostrarMensaje("No hay presas");
        else if( verticeSenuelo == null )
            mostrarMensaje("No hay señuelo");
        else if( hilo.depredadores.isEmpty() )
            mostrarMensaje("No hay depredadores");
        else {
            btnAgregarDepredador.setEnabled(false);
            btnAgregarPresa.setEnabled(false);
            if( hilo.primeraVez == true ){
                hilo.iniciar();
            }else{
                hilo.haySenuelo = true;
                hilo.acechaOtraVez();
                hilo.continuar();
            }

        }
    }


    public class Estado{
        int u;
        double distancia;

        Estado(int uu, double d) {
            u = uu;
            distancia = d;
        }
    };

    public double distancia(Vertice a, Vertice b) {
        return a.cir.distancia(b.cir);
    }

    public void Dijkstra(int s, int[] camino, int[] usando){
        double[] menorDistancia = new double[grafo.vertices.size() + 5];
        for(int i = 0; i < menorDistancia.length; i++) {
            menorDistancia[i] = Double.MAX_VALUE / 4;
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
        menorDistancia[s] = 0;
        while( !pq.isEmpty() ){
            Estado anterior = pq.poll();
            if( anterior.distancia != menorDistancia[anterior.u] )
                continue;
            for(int id: grafo.ady.get(anterior.u)) {
                Arista ari = grafo.aristas.get(id);
                Estado actual = new Estado(ari.b.id, anterior.distancia + distancia(ari.a, ari.b));
                if( actual.distancia < menorDistancia[actual.u] ) {
                    menorDistancia[actual.u] = actual.distancia;
                    camino[actual.u] = anterior.u;
                    usando[actual.u] = id;
                    pq.add(actual);
                }
            }
        }
    }

    public ArrayList<Integer> getCamino(Vertice a, Vertice b){
        ArrayList<Integer> aristasUtilizadas = new ArrayList<>();
        if( caminoVertice[a.id][b.id] == -1 )
            return aristasUtilizadas;
        while( b.id != a.id ){
            int id = IDAristaUsada[a.id][b.id];
            aristasUtilizadas.add(id);
            b = grafo.aristas.get(id).a;
        }
        Collections.reverse(aristasUtilizadas);
        return aristasUtilizadas;
    }
}