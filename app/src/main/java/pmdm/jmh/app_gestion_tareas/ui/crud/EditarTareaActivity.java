package pmdm.jmh.app_gestion_tareas.ui.crud;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.basedatos.DatabaseApp;
import pmdm.jmh.app_gestion_tareas.controlador.BaseFilePickerActivity;
import pmdm.jmh.app_gestion_tareas.controlador.HelperClass;
import pmdm.jmh.app_gestion_tareas.interfaces.DataArguments;
import pmdm.jmh.app_gestion_tareas.entidades.Tarea;
import pmdm.jmh.app_gestion_tareas.ui.fragmentos.FragmentoA;
import pmdm.jmh.app_gestion_tareas.ui.fragmentos.FragmentoB;

public class EditarTareaActivity extends BaseFilePickerActivity implements
        FragmentoA.ComunicacionFragmentoA,
        FragmentoB.ComunicacionFragmentoB,
        DataArguments
{
    private Tarea tareaPorEditar;
    private final int OPERACION_ACTUAL = 2;
    private String titulo, fechaInicio, fechaObjetivo, descripcion;
    private String URL_img, URL_aud, URL_vid, URL_doc;
    private int progresoIndex;
    private byte progresoValue;
    private boolean prioridad;
    private FragmentoA fragmentoA;
    private FragmentoB fragmentoB;
    private FragmentManager fragmentManager;
    private DatabaseApp databaseApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_tarea);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Instancio un DatabaseApp
        databaseApp = DatabaseApp.getInstance(getApplicationContext());

        // Recupero los datos lanzados desde la actividad ListadoTareas
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            // Almaceno la tarea mandada en la intención en una variable
            tareaPorEditar = extras.getParcelable(ARG_TAREA, Tarea.class);

            // Se actualizan los datos con la tarea encontrada
            descripcion = tareaPorEditar.getDescripcion();
            fragmentoA = FragmentoA.newInstance(
                    tareaPorEditar.getTitulo(),
                    tareaPorEditar.getFechaCreacion(),
                    tareaPorEditar.getFechaObjetivo(),
                    tareaPorEditar.getProgreso(),
                    tareaPorEditar.isPrioritaria()
            );

            // Se inicializan los fragmentos
            fragmentManager = getSupportFragmentManager();
            if(savedInstanceState == null)
                fragmentManager.beginTransaction().add(R.id.frag_container, fragmentoA).commit();
        } else {
            finish();
        }
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
            Tarea tareaEditada = new Tarea(
                    titulo,
                    descripcion,
                    progresoValue,
                    fechaInicio,
                    fechaObjetivo,
                    prioridad,
                    URL_doc,
                    URL_img,
                    URL_aud,
                    URL_vid
            );
            tareaEditada.setId(tareaPorEditar.getId());

            intent.putExtra(ARG_OP, OPERACION_ACTUAL);
            try {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(new EditarTarea(tareaEditada));
                setResult(RESULT_OK, intent);
            } catch (Exception e) {
                setResult(RESULT_CANCELED, intent);
            } finally {
                finish();
            }

            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onFilePicked(Uri uri, TipoArchivo tipo) {
        // Dependiendo del tipo de archivo seleccionado, se guarda su path en una determinada variable
        switch (tipo) {
            case IMAGEN:
                URL_img = uri.getPath();
                break;
            case VIDEO:
                URL_vid = uri.getPath();
                break;
            case AUDIO:
                URL_aud = uri.getPath();
                break;
            case DOCUMENTO:
                URL_doc = uri.getPath();
                break;
            default:
        }
    }

    @Override
    public void onFilePickerClicked(View view) {
        int id = view.getId();

        // Dependiendo del botón pulsado, se lanza un FilePicker con un MIME type específico
        // Se usa el method heredado de la superclase para lanzar el FilePicker
        if (id == R.id.bt_imagen) {
            launchFilePicker("image");
        } else if (id == R.id.bt_video) {
            launchFilePicker("video");
        } else if (id == R.id.bt_audio) {
            launchFilePicker("audio");
        } else if (id == R.id.bt_documento) {
            launchFilePicker("text/plain");
        }
    }

    class EditarTarea implements Runnable {
        private Tarea tarea;

        public EditarTarea(Tarea tarea) {
            this.tarea = tarea;
        }

        @Override
        public void run() {
            databaseApp.tareaDAO().update(this.tarea);
        }
    }
}