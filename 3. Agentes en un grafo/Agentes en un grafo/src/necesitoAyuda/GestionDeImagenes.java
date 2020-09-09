package necesitoAyuda;
import java.io.*;

public class GestionDeImagenes {
    private FileInputStream entrada;
    private FileOutputStream salida;
    private File archivo;

    public GestionDeImagenes() {}

    public byte[] abrirImagen(File archivo) {
        // Abrir una imagen
        byte[] imagen = new byte[2500 * 2500];
        try {
            entrada = new FileInputStream(archivo);
            entrada.read(imagen);
        } catch (Exception e) {}
        return imagen;
    }

    public String guardarImagen(File archivo, byte[] imagen) {
        // Guardar imagen
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