package Proyecto1;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.image.BufferedImage;
import java.io.*;

public class App extends JFrame{
    private JPanel ventanaPrincipal;
    private JPanel panelIzquierdo;
    private JPanel panelDerecho;
    private JPanel panelInferior;
    private JScrollPane scrollImagenOriginal;
    private JScrollPane scrollImagenModificada;
    private JLabel cuadroImagenOriginal;
    private JLabel cuadroImagenModificada;
    private JButton btnSeleccionarImagen;
    private JButton btnAnalizarImagen;
    private JButton btnGuardarImagenModificada;
    private JTable tablaCirculos;
    private JScrollPane scrollTablaCirculos;

    JFileChooser seleccion = new JFileChooser();
    File archivo;
    byte[] imagenOriginal;
    GestionDeImagenes gestorImagenes = new GestionDeImagenes();
    Solucionador solver = new Solucionador();

    // Muestra un mensaje de acuerdo a la string proporcionada
    public void muestraMensaje(String texto){
        JOptionPane.showMessageDialog(null, texto);
    }

    // Determina si el archivo tiene alguna de las terminaciones dadas en cual[]
    public boolean esArchivoValido(String[] cual){
        for(int i = 0; i < cual.length; i++){
            if( archivo.getName().endsWith(cual[i])){
                return true;
            }
        }
        return false;
    }

    // Parece que funciona, pero NO le creas, revísalo a conciencia
    public BufferedImage byteToBufferedImage(byte[] a){
        ByteArrayInputStream bais = new ByteArrayInputStream(a);
        BufferedImage b = null;
        try {
            b = ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    // Parece que funciona, pero NO le creas, revísalo a conciencia
    public byte[] bufferedImageToByte(BufferedImage a){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(a, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] b = baos.toByteArray();
        return b;
    }

    // Selecciona la imagen usando un JFileChooser
    public void seleccionaImagen(){
        if( seleccion.showDialog(this, "Abrir imagen") == JFileChooser.APPROVE_OPTION ){
            archivo = seleccion.getSelectedFile();
            if( archivo.canRead() ){
                if( esArchivoValido(new String[]{"png", "jpg", "gif"}) ){
                    imagenOriginal = gestorImagenes.abrirImagen(archivo);
                    solver.imagenModificada = byteToBufferedImage(gestorImagenes.abrirImagen(archivo));
                    cuadroImagenOriginal.setIcon(new ImageIcon(imagenOriginal));
                    cuadroImagenModificada.setIcon(null);
                    tablaCirculos.setModel(new javax.swing.table.DefaultTableModel(
                            null,
                            new String[] {"ID", "Centro en x", "Centro en y", "Radio"}
                    ));
                }else{
                    muestraMensaje("Se esperaba una extension de tipo imagen");
                }
            }else{
                muestraMensaje("Algo falló D:,¡no se puede leer!");
            }
        }
    }

    // Guarda la imagen usando un JFileChooser
    public void guardaImagen(){
        if( seleccion.showDialog(this, "Guardar imagen") == JFileChooser.APPROVE_OPTION ){
            archivo = seleccion.getSelectedFile();
            if( esArchivoValido(new String[]{"png", "jpg", "gif"}) ){
                byte[] imagen = bufferedImageToByte(solver.imagenModificada);
                String respuesta = gestorImagenes.guardarImagen(archivo, imagen);
                muestraMensaje(respuesta);
            }else{
                muestraMensaje("La imagen se debe guardar en algún formato de imagen");
            }
        }
    }

    public void crearTabla(){
        String[][] filasTablaCirculos = new String[solver.circulos.size()][4];
        for(int i = 0; i < solver.circulos.size(); i++){
            Circulo cir = solver.circulos.get(i);
            filasTablaCirculos[i][0] = String.valueOf(i + 1);
            if( cir.tocaBorde ){
                filasTablaCirculos[i][1] = "Fuera de rango";
                filasTablaCirculos[i][2] = "Fuera de rango";
                filasTablaCirculos[i][3] = "No es posible calcularlo";
            }else{
                filasTablaCirculos[i][1] = String.valueOf(cir.centroide.x);
                filasTablaCirculos[i][2] = String.valueOf(cir.centroide.y);
                filasTablaCirculos[i][3] = String.valueOf(cir.radio()) + " px";
            }
        }
        tablaCirculos.setModel(new javax.swing.table.DefaultTableModel(
                filasTablaCirculos,
                new String[] {"ID", "Centro en x", "Centro en y", "Radio"}
        ));
    }

    // Para que funcionen los botones
    public App() {
        btnSeleccionarImagen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionaImagen();
            }
        });
        btnGuardarImagenModificada.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardaImagen();
            }
        });
        btnAnalizarImagen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*solver.quitarRuido();
                solver.marcaFondo(solver.blanco, solver.amarillo);
                solver.eliminaDonas();
                solver.marcaFondo(solver.amarillo, solver.blanco);
                solver.buscaCirculosEliminaOvalos();
                 */
                solver.extra();
                cuadroImagenModificada.setIcon(new ImageIcon(solver.imagenModificada));
                crearTabla();
            }
        });
        tablaCirculos.addComponentListener(new ComponentAdapter() {
        });
    }

    // Main que jala, wtf!
    public static void main(String[] args) {
        JFrame ventana = new JFrame("Localización de círculos");
        ventana.setContentPane(new App().ventanaPrincipal);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.pack();
        ventana.setVisible(true);
    }
}
