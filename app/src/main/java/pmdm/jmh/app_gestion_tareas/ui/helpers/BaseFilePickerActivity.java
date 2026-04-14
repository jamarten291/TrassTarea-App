package pmdm.jmh.app_gestion_tareas.ui.helpers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import pmdm.jmh.app_gestion_tareas.R;

public abstract class BaseFilePickerActivity extends AppCompatActivity {
    protected Uri imageUri = null;
    private String pendingMimeType = null;
    protected ActivityResultLauncher<Intent> openDocumentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = (result.getData() != null) ? result.getData().getData() : null;
                    if (uri != null) {
                        // Clasifica el archivo seleccionado según su tipo
                        TipoArchivo tipo = FileUtils.classifyUriByType(uri, this);

                        // Llama a un method abstracto para hacer algo con el archivo
                        onFilePicked(uri, tipo);
                        Toast.makeText(this, R.string.archivo_seleccionado, Toast.LENGTH_SHORT).show();
                    } else {
                        // Si getData() o su uri es nulo y hay una uri de imagen, se da por hecho que se ha abierto la cámara
                        if (imageUri != null) {
                            onFilePicked(imageUri, TipoArchivo.IMAGEN);
                            Toast.makeText(this, R.string.archivo_seleccionado, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    // ActivityResultLauncher para la solicitud de permisos
    private final ActivityResultLauncher<String> lanzadorPermisos =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    if (pendingMimeType != null) {
                        launchFilePicker(pendingMimeType);
                    }
                } else {
                    Toast.makeText(this, "No se pudieron conceder los permisos", Toast.LENGTH_SHORT).show();
                }
                pendingMimeType = null;
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                imageUri = savedInstanceState.getParcelable("base_image_uri", Uri.class);
            } else {
                imageUri = savedInstanceState.getParcelable("base_image_uri");
            }
            pendingMimeType = savedInstanceState.getString("pending_mime_type");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageUri != null) {
            outState.putParcelable("base_image_uri", imageUri);
        }
        if (pendingMimeType != null) {
            outState.putString("pending_mime_type", pendingMimeType);
        }
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
        boolean shouldLaunchImmediately = true;

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
                    if(comprobarPermisoCamara()) {
                        lanzarChooserImagen(chooser);
                    } else {
                        shouldLaunchImmediately = false;
                        pendingMimeType = mimeType;
                        pedirPermisoCamara();
                    }
                    break;
                case "video":
                    if(comprobarPermisoCamara()) {
                        chooser.putExtra(Intent.EXTRA_TITLE, "Vídeos");
                        Intent aCamaraVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        if (aCamaraVideo.resolveActivity(getPackageManager()) != null) {
                            intentArray[0] = aCamaraVideo;
                            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                        }
                    } else {
                        shouldLaunchImmediately = false;
                        pendingMimeType = mimeType;
                        pedirPermisoCamara();
                    }
                    break;
                case "audio":
                    chooser.putExtra(Intent.EXTRA_TITLE, "Grabaciones");
                    Intent aGrabadora = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    if (aGrabadora.resolveActivity(getPackageManager()) != null) {
                        intentArray[0] = aGrabadora;
                        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    }
                    break;
            }

            // Si no hay un mime pendiente, se lanza inmediatamente el chooser
            if (shouldLaunchImmediately) openDocumentLauncher.launch(chooser);
        }
    }

    private void lanzarChooserImagen(Intent chooser) {
        chooser.putExtra(Intent.EXTRA_TITLE, "Fotos");
        Intent[] intentArray = new Intent[1];

        try {
            // Creamos el archivo temporal antes de lanzar la cámara
            File tempFile = FileUtils.crearArchivoTemporal(this);
            imageUri = FileProvider.getUriForFile(this,
                    "pmdm.jmh.app_gestion_tareas.FileProvider",
                    tempFile);

            Intent aCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (aCamara.resolveActivity(getPackageManager()) != null) {
                aCamara.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                aCamara.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                aCamara.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                intentArray[0] = aCamara;
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error al crear el archivo temporal", Toast.LENGTH_LONG).show();
            Log.e("Error", Objects.requireNonNull(e.getMessage()));
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

    //Metodo para comprobar si se ha concedido el permiso
    private boolean comprobarPermisoCamara() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    //Metodo para solicitar el permiso al usuario
    private void pedirPermisoCamara() {
        lanzadorPermisos.launch(Manifest.permission.CAMERA);
    }
}