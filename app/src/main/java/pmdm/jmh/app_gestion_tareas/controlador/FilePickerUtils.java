package pmdm.jmh.app_gestion_tareas.controlador;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

public class FilePickerUtils {
    public static String classifyUri(Uri uri, Context context) {
        if (uri == null) return "unknown";

        String mime = context.getContentResolver().getType(uri);

        // 2) Si no hay MIME, obtener nombre y extraer extensión
        String name = null;
        try (Cursor cursor = context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
            }
        } catch (Exception e) {
            // ignorar
        }

        String extension = null;
        if (mime == null && name != null && name.contains(".")) {
            extension = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        if (mime != null) {
            if (mime.startsWith("image/")) return "imagen";
            if (mime.startsWith("video/")) return "video";
            if (mime.startsWith("audio/")) return "audio";
            // documentos comunes
            switch (mime) {
                case "application/pdf":
                case "application/msword":
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                case "application/vnd.ms-excel":
                case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                case "application/vnd.ms-powerpoint":
                case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                case "text/plain":
                case "text/html":
                    return "documento";
                default:
                    return "otro";
            }
        }
        return "unknown";
    }

    public static Intent createFilePickerIntent(String file) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(file + "/*");
        return intent;
    }
}
