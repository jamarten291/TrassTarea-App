package pmdm.jmh.app_gestion_tareas.ui.helpers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import pmdm.jmh.app_gestion_tareas.R;

public abstract class BaseFilePickerActivity extends AppCompatActivity {
    protected ActivityResultLauncher<Intent> openDocumentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openDocumentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            // Clasifica el archivo seleccionado según su tipo
                            TipoArchivo tipo = FileUtils.classifyUriByType(uri, this);

                            // Llama a un method abstracto para hacer algo con el archivo
                            onFilePicked(uri, tipo);
                            Toast.makeText(this, R.string.archivo_seleccionado, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    /**
     * Method que lanza una intención FilePicker de un tipo pasado por parámetro. Se usa el lanzador
     * de esta clase para lanzar dicho intent.
     * @param mimeType Tipo de archivo admitido en el FilePicker que será lanzado.
     */
    protected void launchFilePicker(String mimeType) {
        // Mimes extra que son interpretados como documentos
        String[] docMimeTypes = {
                "application/pdf",
                "application/msword",
                "text/plain",
                "text/html"
        };

        Intent intentArchivos = new Intent();
        intentArchivos.setAction(Intent.ACTION_GET_CONTENT);
        intentArchivos.addCategory(Intent.CATEGORY_OPENABLE);

        if ("doc".equals(mimeType)) {
            intentArchivos.setType("*/*");
            // Se colocan varios mimes para el documento
            intentArchivos.putExtra(Intent.EXTRA_MIME_TYPES, docMimeTypes);
            intentArchivos.setAction(Intent.ACTION_OPEN_DOCUMENT);
            openDocumentLauncher.launch(intentArchivos);
        } else {
            intentArchivos.setType(mimeType + "/*");

            // Se inicializa un ActionChooser
            Intent chooser = new Intent(Intent.ACTION_CHOOSER);
            chooser.putExtra(Intent.EXTRA_INTENT, intentArchivos);
            Intent[] intentArray = new Intent[1];

            // Dependiendo del archivo se agrega otro intent
            switch (mimeType) {
                case "image":
                    chooser.putExtra(Intent.EXTRA_TITLE, "Fotos");

                    Intent aCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intentArray[0] = aCamara;
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    break;
                case "video":
                    chooser.putExtra(Intent.EXTRA_TITLE, "Vídeos");

                    Intent aCamaraVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intentArray[0] = aCamaraVideo; //Opciones secundarias, hay 1 sola
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    break;
                case "audio":
                    chooser.putExtra(Intent.EXTRA_TITLE, "Grabaciones");

                    Intent aGrabadora = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    intentArray[0] = aGrabadora; //Opciones secundarias, hay 1 sola
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    break;
            }

            openDocumentLauncher.launch(chooser);
        }
    }

    /**
     * Clase abstracta que especifica lo que se debe hacer con un archivo seleccionado dependiendo
     * de su implementación
     *
     * @param uri    Uri del archivo seleccionado por el usuario
     * @param tipo   Tipo del archivo seleccionado por el usuario
     */
    protected abstract void onFilePicked(Uri uri, TipoArchivo tipo);
}