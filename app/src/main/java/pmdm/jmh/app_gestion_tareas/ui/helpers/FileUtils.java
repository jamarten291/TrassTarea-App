package pmdm.jmh.app_gestion_tareas.ui.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;

import pmdm.jmh.app_gestion_tareas.database.entity.Tarea;

/**
 * Clase que ofrece varias funciones para gestión de archivos en las tareas
 */
public class FileUtils {
    private static boolean createUriCopy(Context c, Uri uri, File dest) {
        // Si la URI es nula, devuelve false, esto es útil, ya que la URI es null por defecto si no
        // ha sido asignada
        if (uri == null) return false;

        try (InputStream in = c.getContentResolver().openInputStream(uri);
             OutputStream out = new FileOutputStream(dest)) {
            if (in == null)
                throw new FileNotFoundException("InputStream nulo para URI: " + uri);

            // Buffer que se rellena con los bytes del archivo origen
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                // Escribe en el destino los bytes del origen
                out.write(buf, 0, len);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean checkIfExternalStorageIsAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static void deleteTareaFiles(Tarea t, boolean deleteImage, boolean deleteAudio, boolean deleteVideo, boolean deleteDocument) {
        // helper para crear File solo si la ruta es válida
        Function<String, File> fileFromPath = path -> {
            if (path == null || path.isEmpty()) return null;
            return new File(path);
        };

        File img = fileFromPath.apply(t.getURL_img());
        File aud = fileFromPath.apply(t.getURL_aud());
        File vid = fileFromPath.apply(t.getURL_vid());
        File doc = fileFromPath.apply(t.getURL_doc());

        // Elimina tanto el archivo como la URI asignada a la tarea
        // Borra la URI si primero ha borrado el archivo exitosamente
        if (img != null && img.exists() && deleteImage) {
            if (img.delete()) t.setURL_img(null);
        }
        if (aud != null && aud.exists() && deleteAudio) {
            if (aud.delete()) t.setURL_aud(null);
        }
        if (vid != null && vid.exists() && deleteVideo) {
            if (vid.delete()) t.setURL_vid(null);
        }
        if (doc != null && doc.exists() && deleteDocument) {
            if (doc.delete()) t.setURL_doc(null);
        }
    }

    public static void attachFilesToTarea(Context c, Tarea t, Uri img_src, Uri vid_src, Uri aud_src, Uri doc_src, boolean sd) {
        // Si se quiere almacenar en el SD, comprueba que tenga disponible el almacenamiento externo.
        // De lo contrario, guarda en la memoria interna
        File appFolder = sd && FileUtils.checkIfExternalStorageIsAvailable()
                ? c.getExternalFilesDir(null)
                : c.getFilesDir();

        // Crea el archivo concatenando la ruta de almacenamiento de la app con el nombre de la URI
        File img = new File(appFolder, getFileNameByUri(c, img_src));
        File vid = new File(appFolder, getFileNameByUri(c, vid_src));
        File aud = new File(appFolder, getFileNameByUri(c, aud_src));
        File doc = new File(appFolder, getFileNameByUri(c, doc_src));

        // Si crea la copia en la app exitosamente, asigna la ruta del archivo a la tarea
        if (createUriCopy(c, img_src, img)) t.setURL_img(img.getPath());
        if (createUriCopy(c, vid_src, vid)) t.setURL_vid(vid.getPath());
        if (createUriCopy(c, doc_src, doc)) t.setURL_doc(doc.getPath());
        if (createUriCopy(c, aud_src, aud)) t.setURL_aud(aud.getPath());
    }

    // Method que clasifica una URI según su tipo
    public static TipoArchivo classifyUriByType(Uri uri, Context context) {
        if (uri == null) return TipoArchivo.DESCONOCIDO;

        String mime = context.getContentResolver().getType(uri);

        // Si no hay MIME, obtener nombre y extraer extensión
        String name = getFileNameByUri(context, uri);

        String extension = null;
        if (mime == null && name != null && name.contains(".")) {
            // Obtiene la extensión haciendo un substring
            extension = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        if (mime != null) {
            if (mime.startsWith("image/")) return TipoArchivo.IMAGEN;
            if (mime.startsWith("video/")) return TipoArchivo.VIDEO;
            if (mime.startsWith("audio/")) return TipoArchivo.AUDIO;
            // documentos comunes
            switch (mime) {
                case "application/pdf":
                case "application/msword":
                case "text/plain":
                case "text/html":
                    return TipoArchivo.DOCUMENTO;
                default:
                    return TipoArchivo.DESCONOCIDO;
            }
        }
        return TipoArchivo.DESCONOCIDO;
    }

    public static String getFileNameByUri(Context context, Uri uri) {
        // Abre un cursor para ver el nombre del archivo de la URI
        try (Cursor cursor = context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                // Busca el nombre del archivo en el cursor
                return cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
            }
        } catch (Exception e) {
            // ignorar
        }

        return "";
    }

    /**
     * Method que devuelve el nombre de un archivo en base a su ruta.
     * @param path Ruta completa del archivo.
     * @return Nombre del archivo.
     */
    public static String getFileNameFromPath(String path) {
        if (path == null || path.isEmpty()) return null;
        String[] parts = path.split("/");
        if (parts.length == 0) return null;

        // Devuelve el último elemento del array
        return parts[parts.length - 1];
    }

    public static File crearArchivoTemporal(Context c) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String nombreArchivoImagen = "JPEG_" + timeStamp + "_";
        File rutaFoto = c.getFilesDir();
        //File rutaExterna = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File archivoImagen = File.createTempFile(
                nombreArchivoImagen,    // prefijo
                ".jpg",                 // sufijo
                rutaFoto                // directorio
        );

        return archivoImagen;
    }
}
