package pmdm.jmh.app_gestion_tareas.ui.crud;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import pmdm.jmh.app_gestion_tareas.controlador.FilePickerUtils;

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
                            String tipo = FilePickerUtils.classifyUri(uri, this);
                            onFilePicked(uri, tipo);
                        }
                    }
                }
        );
    }

    protected void launchFilePicker(String mimeType) {
        openDocumentLauncher.launch(FilePickerUtils.createFilePickerIntent(mimeType));
    }

    protected abstract void onFilePicked(Uri uri, String tipo);
}