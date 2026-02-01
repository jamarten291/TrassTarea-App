package pmdm.jmh.app_gestion_tareas.ui.tarea.create;

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

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.database.repository.TareaRepository;
import pmdm.jmh.app_gestion_tareas.ui.helpers.BaseFilePickerActivity;
import pmdm.jmh.app_gestion_tareas.ui.helpers.FileUtils;
import pmdm.jmh.app_gestion_tareas.util.HelperClass;
import pmdm.jmh.app_gestion_tareas.ui.interfaces.DataArguments;
import pmdm.jmh.app_gestion_tareas.database.entity.Tarea;
import pmdm.jmh.app_gestion_tareas.ui.fragments.FragmentoA;
import pmdm.jmh.app_gestion_tareas.ui.fragments.FragmentoB;
import pmdm.jmh.app_gestion_tareas.ui.helpers.TipoArchivo;

public class CrearTareaActivity extends BaseFilePickerActivity implements
        FragmentoA.ComunicacionFragmentoA,
        FragmentoB.ComunicacionFragmentoB,
        DataArguments
{
    private final int OPERACION_ACTUAL = 1;
    private String titulo, fechaInicio, fechaObjetivo, descripcion;
    private Uri URL_img_src, URL_aud_src, URL_vid_src, URL_doc_src;
    private String URL_img_dst, URL_aud_dst, URL_vid_dst, URL_doc_dst;
    private int progresoIndex;
    private byte progresoValue;
    private boolean prioridad;
    private boolean sd = false;
    private FragmentoA fragmentoA;
    private FragmentoB fragmentoB;
    private FragmentManager fragmentManager;
    private TareaRepository repository;

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

        Bundle extras = getIntent().getExtras();
        if (extras != null) sd = extras.getBoolean(ARG_SD_STORAGE);

        fragmentoA = new FragmentoA();
        fragmentManager = getSupportFragmentManager();

        repository = new TareaRepository(getApplication());

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

            FileUtils.attachFilesToTarea(
                    this,
                    nuevaTarea,
                    URL_img_src,
                    URL_vid_src,
                    URL_aud_src,
                    URL_doc_src,
                    sd
            );

            intent.putExtra(ARG_OP, OPERACION_ACTUAL);

            // Actualizar tarea devuelve un boolean indicando si ha salido bien la operación
            // Esto actualiza la misma tarea y añade el URI de los archivos
            if (repository.insertarTarea(nuevaTarea)) {
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED, intent);
            }

            finish();
        }
    }

    @Override
    protected void onFilePicked(Uri uri, TipoArchivo tipo) {
        // Consigo la URI de origen y la guardo en una variable a la cual accederé posteriormente
        switch (tipo) {
            case IMAGEN:
                URL_img_src = uri;
                break;
            case VIDEO:
                URL_vid_src = uri;
                break;
            case AUDIO:
                URL_aud_src = uri;
                break;
            case DOCUMENTO:
                URL_doc_src = uri;
                break;
            default:
        }
    }

    @Override
    public void onFileAttached(View view) {
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

    @Override
    public void onFileDeleted(View view) {
        int id = view.getId();

        // Se establece a null la URI del archivo asociado al ImageButton pulsado
        // De este modo, no podrá crearse ni adjuntarse ese archivo
        if (id == R.id.bt_imagen) {
            URL_img_src = null;
        } else if (id == R.id.bt_video) {
            URL_vid_src = null;
        } else if (id == R.id.bt_audio) {
            URL_aud_src = null;
        } else if (id == R.id.bt_documento) {
            URL_doc_src = null;
        }
    }
}