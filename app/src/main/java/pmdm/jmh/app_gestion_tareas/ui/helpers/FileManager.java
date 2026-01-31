package pmdm.jmh.app_gestion_tareas.ui.helpers;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import pmdm.jmh.app_gestion_tareas.database.entity.Tarea;

public class FileManager {
    public static boolean createUriCopyForApplication(Context c, Uri uri, File dest, boolean sd) {
        if (uri == null) return false;

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

    public static void deleteTareaFiles(Tarea t) {
        // helper para crear File solo si la ruta es válida
        Function<String, File> fileFromPath = path -> {
            if (path == null || path.isEmpty()) return null;
            return new File(path);
        };

        File img = fileFromPath.apply(t.getURL_img());
        File aud = fileFromPath.apply(t.getURL_aud());
        File vid = fileFromPath.apply(t.getURL_vid());
        File doc = fileFromPath.apply(t.getURL_doc());

        if (img != null && img.exists()) img.delete();
        if (aud != null && aud.exists()) aud.delete();
        if (vid != null && vid.exists()) vid.delete();
        if (doc != null && doc.exists()) doc.delete();
    }

}
