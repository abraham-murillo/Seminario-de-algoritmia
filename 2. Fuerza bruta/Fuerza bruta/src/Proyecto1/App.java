package Proyecto1;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class App extends JFrame {
    private JPanel ventanaPrincipal;
    private JPanel panelIzquierdo;
    private JPanel panelDerecho;
    private JPanel panelInferior;
    private JScrollPane scrollImagenOriginal;
    private JScrollPane scrollImagenModificada;
    private JLabel cuadroImagenOriginal;
    private JLabel cuadroImagenModificada;
    private JButton btnSeleccionarImagen;
    private JButton btnGuardarImagenModificada;
    private JTable tablaCirculos;
    private JScrollPane scrollTablaCirculos;
    private JButton btnOrdenarCirculosPorRadio;

    JFileChooser seleccion = new JFileChooser();
    File archivo;
    byte[] imagenOriginal;
    GestionDeImagenes gestorImagenes = new GestionDeImagenes();
    Solucionador solver = new Solucionador();

    public void muestraMensaje(String texto) {
        // Muestra un mensaje de acuerdo a la string proporcionada
        JOptionPane.showMessageDialog(null, texto);
    }

    public boolean esArchivoValido(String[] cual) {
        // Determina si el archivo tiene alguna de las terminaciones dadas en cual[]
        for (int i = 0; i < cual.length; i++) {
            if (archivo.getName().endsWith(cual[i])) {
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
                    imagenOriginal = gestorImagenes.abrirImagen(archivo);
                    solver.imagenModificada = byteToBufferedImage(gestorImagenes.abrirImagen(archivo));
                    cuadroImagenOriginal.setIcon(new ImageIcon(imagenOriginal));
                    cuadroImagenModificada.setIcon(null);
                    tablaCirculos.setModel(new javax.swing.table.DefaultTableModel(
                            null,
                            new String[] {"ID", "Centro", "Radio", "Adyacentes"}));
                } else {
                    muestraMensaje("Se esperaba una extension de tipo imagen");
                }
            } else {
                muestraMensaje("Algo falló D:,¡no se puede leer!");
            }
        }
    }

    public void guardaImagen() {
        // Guarda la imagen usando un JFileChooser
        if (seleccion.showDialog(this, "Guardar imagen") == JFileChooser.APPROVE_OPTION) {
            archivo = seleccion.getSelectedFile();
            if (esArchivoValido(new String[] {"png", "jpg", "gif"})) {
                byte[] imagen = bufferedImageToByte(solver.imagenModificada);
                String respuesta = gestorImagenes.guardarImagen(archivo, imagen);
                muestraMensaje(respuesta);
            } else {
                muestraMensaje("La imagen se debe guardar en algún formato de imagen");
            }
        }
    }

    public void crearTabla() {
        // Crea la tabla de información a ser desplegada
        String[][] filasTablaCirculos = new String[solver.vertices.size()][4];
        for (int i = 0; i < solver.vertices.size(); i++) {
            Vertice a = solver.vertices.get(i);
            filasTablaCirculos[i][0] = String.valueOf(a.id);
            filasTablaCirculos[i][1] = "(" + String.valueOf(a.cir.centro.x) + ", " + String.valueOf(a.cir.centro.y) + ")";
            filasTablaCirculos[i][2] = String.valueOf(a.cir.radio()) + " px";
            filasTablaCirculos[i][3] = "{";
            for (int j: solver.listaAdyacencia.get(a.id)){
                Arista ari = solver.aristas.get(j);
                filasTablaCirculos[i][3] += String.valueOf((a.compareTo(ari.a) == 0 ? ari.b: ari.a).id) + ",  ";
            }
            filasTablaCirculos[i][3] += "}";
        }
        tablaCirculos.setModel(new javax.swing.table.DefaultTableModel(
            filasTablaCirculos,
            new String[] {"ID", "Centro", "Radio", "Adyacentes"}
        ));
    }

    public App() {
        // Para que funcionen los botones
        btnSeleccionarImagen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Resetear todo, hacerlo todo D:
                solver.vaciar();
                seleccionaImagen();
                solver.quitarRuido();
                solver.buscarTodosLosCirculos();
                solver.construirGrafo();
                cuadroImagenModificada.setIcon(new ImageIcon(solver.imagenModificada));
                crearTabla();
            }
        });

        btnGuardarImagenModificada.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Guarda la imagen modificada
                guardaImagen();
            }
        });

        tablaCirculos.addComponentListener(new ComponentAdapter() {});

        btnOrdenarCirculosPorRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ordena los círculos por radio1 > radio2
                solver.ordenarCirculos();
                crearTabla();
            }
        });

        cuadroImagenModificada.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            // Desde el pixel(x, y) presionado por el mouse evalúa si es un nodo y lo elimina junto con sus aristas
            super.mouseClicked(e);
            solver.eliminaVertice(e.getX(), e.getY());
            cuadroImagenModificada.setIcon(new ImageIcon(solver.imagenModificada));
            crearTabla();
            }
        });
    }

    public static void main(String[] args) {
        // Main que jala, wtf!
        JFrame ventana = new JFrame("Fuerza bruta");
        ventana.setContentPane(new App().ventanaPrincipal);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.pack();
        ventana.setVisible(true);
    }
}