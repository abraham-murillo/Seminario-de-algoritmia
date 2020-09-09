package Proyecto1;
import javax.swing.*;
import java.io.*;

public class GestionDeImagenes {
    private FileInputStream entrada;
    private FileOutputStream salida;
    private File archivo;

    public GestionDeImagenes() {}

    // Abrir una imagen
    public byte[] abrirImagen(File archivo) {
        byte[] imagen = new byte[2345 * 100];
        try {
            entrada = new FileInputStream(archivo);
            entrada.read(imagen);
        } catch(Exception e) {
        }
        return imagen;
    }

    // Guardar imagen
    public String guardarImagen(File archivo, byte[] imagen) {
        String respuesta = null;
        try {
            salida = new FileOutputStream(archivo);
            salida.write(imagen);
            respuesta = "La imagen se guardó con exito c:";
        } catch (Exception e) {
            respuesta = "Error al guardar la imagen";
        }
        return respuesta;
    }
}
