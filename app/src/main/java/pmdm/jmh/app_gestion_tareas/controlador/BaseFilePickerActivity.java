package pmdm.jmh.app_gestion_tareas.controlador;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import pmdm.jmh.app_gestion_tareas.ui.crud.TipoArchivo;

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

                            // Llama a un method abstracto para hacer algo con el archivo
                            onFilePicked(uri, tipo);

                            // TODO give information to the user after file attachment via text fields
                        }
                    }
                }
        );
    }

    // Method que lanza una intención FilePicker de un tipo pasado por parámetro usando el lanzador de la clase
    // Toda clase que extienda esta clase podrá usar este method
    protected void launchFilePicker(String mimeType) {
        openDocumentLauncher.launch(FilePickerUtils.createFilePickerIntent(mimeType));
    }

    // Clase abstracta que especifica lo que se debe hacer con el archivo seleccionado dependiendo de su implementación
    protected abstract void onFilePicked(Uri uri, TipoArchivo tipo);
}