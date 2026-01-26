package pmdm.jmh.app_gestion_tareas.ui.crud;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import java.time.LocalDate;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.basedatos.DatabaseApp;
import pmdm.jmh.app_gestion_tareas.controlador.HelperClass;
import pmdm.jmh.app_gestion_tareas.interfaces.DataArguments;
import pmdm.jmh.app_gestion_tareas.entidades.Tarea;
import pmdm.jmh.app_gestion_tareas.ui.fragmentos.FragmentoA;
import pmdm.jmh.app_gestion_tareas.ui.fragmentos.FragmentoB;

public class CrearTareaActivity extends AppCompatActivity implements
        FragmentoA.ComunicacionFragmentoA,
        FragmentoB.ComunicacionFragmentoB,
        DataArguments
{
    private final int OPERACION_ACTUAL = 1;
    private String titulo, fechaInicio, fechaObjetivo, descripcion;
    private String URL_img, URL_aud, URL_vid, URL_doc;
    private int progresoIndex;
    private byte progresoValue;
    private boolean prioridad;
    private FragmentoA fragmentoA;
    private FragmentoB fragmentoB;
    private FragmentManager fragmentManager;
    private DatabaseApp databaseApp;

    // TODO implement file picker
    private final ActivityResultLauncher<Intent> openDocumentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        // Usa el URI (leer contenido, persistir permiso, etc.)
                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        );
                        Toast.makeText(this, "Archivo seleccionado: " + uri.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_tarea);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fragmentoA = new FragmentoA();
        fragmentManager = getSupportFragmentManager();

        databaseApp = DatabaseApp.getInstance(getApplicationContext());

        if(savedInstanceState == null)
            fragmentManager.beginTransaction().add(R.id.frag_container, fragmentoA).commit();
    }

    @Override
    public void onBotonSiguienteClicked() {
        titulo = fragmentoA.getTitulo();
        fechaInicio = fragmentoA.getFechaInicio();
        fechaObjetivo = fragmentoA.getFechaObjetivo();
        progresoIndex = fragmentoA.getProgresoIndex();
        prioridad = fragmentoA.isPrioridad();

        fragmentoB = FragmentoB.newInstance(titulo, fechaInicio, fechaObjetivo, progresoIndex, prioridad, descripcion);
        if (!fragmentoB.isAdded()) {
            fragmentManager.beginTransaction().replace(R.id.frag_container, fragmentoB).commit();
        }
    }

    @Override
    public void onBotonSalirClicked() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onBotonVolverClicked() {
        descripcion = fragmentoB.getDescripcion();

        fragmentoA = FragmentoA.newInstance(titulo, fechaInicio, fechaObjetivo, progresoIndex, prioridad);
        if (!fragmentoA.isAdded()) {
            fragmentManager.beginTransaction().replace(R.id.frag_container, fragmentoA).commit();
        }
    }

    @Override
    public void onBotonGuardarClicked() {
        descripcion = fragmentoB.getDescripcion();

        if (titulo.isEmpty() || fechaInicio.isEmpty() || fechaObjetivo.isEmpty() ||
            progresoIndex == Spinner.INVALID_POSITION || descripcion.isEmpty()) {
            HelperClass.showBasicAlertDialog(this, R.string.error, R.string.invalid_input_error);
        } else {
            Intent intent = new Intent();

            progresoValue = (byte) (25 * progresoIndex);
            Tarea nuevaTarea = new Tarea(
                    titulo,
                    descripcion,
                    progresoValue,
                    fechaInicio,
                    fechaObjetivo,
                    prioridad,
                    null,
                    null,
                    null,
                    null
            );

            intent.putExtra(ARG_OP, OPERACION_ACTUAL);
            try {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(new CrearTarea(nuevaTarea));
                setResult(RESULT_OK, intent);
            } catch (Exception e) {
                setResult(RESULT_CANCELED, intent);
            } finally {
                finish();
            }
        }
    }

    @Override
    public void onFilePickerClicked(String file) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(file + "/*");
        openDocumentLauncher.launch(intent);
    }

    class CrearTarea implements Runnable {
        private Tarea tarea;

        public CrearTarea(Tarea tarea) {
            this.tarea = tarea;
        }

        @Override
        public void run() {
            databaseApp.tareaDAO().insertAll(this.tarea);
        }
    }
}