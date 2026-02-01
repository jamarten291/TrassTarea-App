package pmdm.jmh.app_gestion_tareas.ui.tarea.update;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class EditarTareaActivity extends BaseFilePickerActivity implements
        FragmentoA.ComunicacionFragmentoA,
        FragmentoB.ComunicacionFragmentoB,
        DataArguments
{
    private Tarea tareaPorEditar;
    private final int OPERACION_ACTUAL = 2;
    private String titulo, fechaInicio, fechaObjetivo, descripcion;
    private Uri URL_img_src, URL_aud_src, URL_vid_src, URL_doc_src;

    // Falso por defecto, pero cambia si se marca para eliminación
    private boolean img_delete = false;
    private boolean aud_delete = false;
    private boolean vid_delete = false;
    private boolean doc_delete = false;
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
        setContentView(R.layout.activity_editar_tarea);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Instancio un Repository
        repository = new TareaRepository(getApplication());

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

            // Almaceno la preferencia de almacenamiento SD en una variable
            sd = extras.getBoolean(ARG_SD_STORAGE);

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
            Tarea tareaEditada = getTareaEditada();

            // Este method borra tanto la URI del archivo asignada a la tarea como el archivo
            FileUtils.deleteTareaFiles(tareaEditada,
                    img_delete,
                    aud_delete,
                    vid_delete,
                    doc_delete
            );

            FileUtils.attachFilesToTarea(
                    this,
                    tareaEditada,
                    // Si la URI es nula, no cambiará nada, de lo contrario actualizará la URI
                    URL_img_src,
                    URL_vid_src,
                    URL_aud_src,
                    URL_doc_src,
                    sd
            );

            intent.putExtra(ARG_OP, OPERACION_ACTUAL);

            // Actualizar tarea devuelve un boolean indicando si ha salido bien la operación
            if (repository.actualizarTarea(tareaEditada)) {
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED, intent);
            }

            finish();
        }
    }

    @NonNull
    private Tarea getTareaEditada() {
        Tarea tareaEditada = new Tarea(
                titulo,
                descripcion,
                progresoValue,
                fechaInicio,
                fechaObjetivo,
                prioridad,
                tareaPorEditar.getURL_doc(),
                tareaPorEditar.getURL_img(),
                tareaPorEditar.getURL_aud(),
                tareaPorEditar.getURL_vid()
        );
        tareaEditada.setId(tareaPorEditar.getId());
        return tareaEditada;
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
            launchFilePicker("doc");
        }
    }

    @Override
    public void onFileDeleted(View view) {
        int id = view.getId();

        // Dependiendo del botón pulsado, se marca el archivo asignado al botón para la eliminación
        // Esto lo hago solo en EditarTareaActivity, ya que los archivos no se guardan hasta que no
        // se pulse el botón de guardado en GuardarTareaActivity
        if (id == R.id.bt_imagen) {
            URL_img_src = null;
            img_delete = true;
        } else if (id == R.id.bt_video) {
            URL_vid_src = null;
            vid_delete = true;
        } else if (id == R.id.bt_audio) {
            URL_aud_src = null;
            aud_delete = true;
        } else if (id == R.id.bt_documento) {
            URL_doc_src = null;
            doc_delete = true;
        }
    }
}