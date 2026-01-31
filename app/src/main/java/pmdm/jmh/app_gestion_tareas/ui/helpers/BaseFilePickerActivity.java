package pmdm.jmh.app_gestion_tareas.ui.helpers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
                            TipoArchivo tipo = FilePickerUtils.classifyUri(uri, this);
                            String nombre = FilePickerUtils.getFileName(this, uri);

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
        openDocumentLauncher.launch(FilePickerUtils.createFilePickerIntent(mimeType));
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