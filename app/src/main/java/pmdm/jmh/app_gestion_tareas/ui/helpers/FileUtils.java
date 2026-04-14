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
import java.util.Objects;
import java.util.function.Function;

import pmdm.jmh.app_gestion_tareas.database.entity.Tarea;

/**
 * Clase que ofrece varias funciones para gestión de archivos en las tareas
 */
public class FileUtils {
    private static boolean createUriCopy(Context c, Uri uri, File dest) {
        if (uri == null) return false;

        // Comprobamos si el origen y destino son el mismo archivo físico.
        // Esto es crucial para evitar truncar el archivo al abrir el FileOutputStream.
        
        // Caso 1: URI de tipo file
        if ("file".equals(uri.getScheme())) {
            File sourceFile = new File(Objects.requireNonNull(uri.getPath()));
            if (sourceFile.getAbsolutePath().equals(dest.getAbsolutePath())) {
                return true; 
            }
        }
        
        // Caso 2: URI de tipo content (como los de FileProvider)
        // Intentamos comparar el nombre del archivo si está en la misma carpeta de la app
        if ("content".equals(uri.getScheme())) {
            String fileName = getFileNameByUri(c, uri);
            File appFilesDir = c.getFilesDir();
            File appExternalFilesDir = c.getExternalFilesDir(null);
            
            boolean isSameFile = false;
            if (dest.getParentFile() != null) {
                if (dest.getParentFile().getAbsolutePath().equals(appFilesDir.getAbsolutePath()) ||
                    (appExternalFilesDir != null && dest.getParentFile().getAbsolutePath().equals(appExternalFilesDir.getAbsolutePath()))) {
                    
                    if (dest.getName().equals(fileName)) {
                        isSameFile = true;
                    }
                }
            }
            
            if (isSameFile) return true;
        }

        try (InputStream in = c.getContentResolver().openInputStream(uri);
             OutputStream out = new FileOutputStream(dest)) {
            if (in == null)
                throw new FileNotFoundException("InputStream nulo para URI: " + uri);

            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
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
        Function<String, File> fileFromPath = path -> {
            if (path == null || path.isEmpty()) return null;
            return new File(path);
        };

        File img = fileFromPath.apply(t.getURL_img());
        File aud = fileFromPath.apply(t.getURL_aud());
        File vid = fileFromPath.apply(t.getURL_vid());
        File doc = fileFromPath.apply(t.getURL_doc());

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
        File appFolder = sd && FileUtils.checkIfExternalStorageIsAvailable()
                ? c.getExternalFilesDir(null)
                : c.getFilesDir();

        if (img_src != null) {
            File img = new File(appFolder, getFileNameByUri(c, img_src));
            if (createUriCopy(c, img_src, img)) t.setURL_img(img.getPath());
        }
        if (vid_src != null) {
            File vid = new File(appFolder, getFileNameByUri(c, vid_src));
            if (createUriCopy(c, vid_src, vid)) t.setURL_vid(vid.getPath());
        }
        if (doc_src != null) {
            File doc = new File(appFolder, getFileNameByUri(c, doc_src));
            if (createUriCopy(c, doc_src, doc)) t.setURL_doc(doc.getPath());
        }
        if (aud_src != null) {
            File aud = new File(appFolder, getFileNameByUri(c, aud_src));
            if (createUriCopy(c, aud_src, aud)) t.setURL_aud(aud.getPath());
        }
    }

    public static TipoArchivo classifyUriByType(Uri uri, Context context) {
        if (uri == null) return TipoArchivo.DESCONOCIDO;

        String mime = context.getContentResolver().getType(uri);
        String name = getFileNameByUri(context, uri);

        if (mime == null && name != null && name.contains(".")) {
            String extension = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        if (mime != null) {
            if (mime.startsWith("image/")) return TipoArchivo.IMAGEN;
            if (mime.startsWith("video/")) return TipoArchivo.VIDEO;
            if (mime.startsWith("audio/")) return TipoArchivo.AUDIO;
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
        if (uri == null) return "";
        try (Cursor cursor = context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
            }
        } catch (Exception e) {
            // ignorar
        }

        String path = uri.getPath();
        if (path != null && path.contains("/")) {
            return path.substring(path.lastIndexOf('/') + 1);
        }

        return "archivo_" + System.currentTimeMillis();
    }

    public static String getFileNameFromPath(String path) {
        if (path == null || path.isEmpty()) return null;
        String[] parts = path.split("/");
        if (parts.length == 0) return null;
        return parts[parts.length - 1];
    }

    public static File crearArchivoTemporal(Context c) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String nombreArchivoImagen = "JPEG_" + timeStamp + "_";
        File rutaFoto = c.getFilesDir();
        return File.createTempFile(
                nombreArchivoImagen,
                ".jpg",
                rutaFoto
        );
    }
}