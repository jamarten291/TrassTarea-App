package pmdm.jmh.app_gestion_tareas.ui.crud;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
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

public class EditarTareaActivity extends AppCompatActivity implements
        FragmentoA.ComunicacionFragmentoA,
        FragmentoB.ComunicacionFragmentoB,
        DataArguments
{

    private int idTarea;
    private Tarea tarea;
    private final int OPERACION_ACTUAL = 2;
    private String titulo;
    private String fechaInicioStr;
    private String fechaObjetivoStr;
    private LocalDate fechaInicioValue;
    private LocalDate fechaObjetivoValue;
    private int progresoIndex;
    private byte progresoValue;
    private boolean prioridad;
    private String descripcion;
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

        // Recupero los datos lanzados desde la actividad ListadoTareas
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            // TODO add persistence
            tarea = extras.getParcelable(ARG_TAREA, Tarea.class);

            // Identificador estático de la tarea
            idTarea = tarea.getIdTarea();

            descripcion = tarea.getDescripcion();
            fragmentoA = FragmentoA.newInstance(
                    tarea.getTitulo(),
                    tarea.getFechaCreacion(),
                    tarea.getFechaObjetivo(),
                    tarea.getProgreso(),
                    tarea.isPrioritaria()
            );

            fragmentManager = getSupportFragmentManager();

            databaseApp = DatabaseApp.getInstance(getApplicationContext());
            if(savedInstanceState == null)
                fragmentManager.beginTransaction().add(R.id.frag_container, fragmentoA).commit();
        } else {
            finish();
        }
    }

    @Override
    public void onBotonSiguienteClicked() {
        titulo = fragmentoA.getTitulo();
        fechaInicioStr = fragmentoA.getFechaInicio();
        fechaObjetivoStr = fragmentoA.getFechaObjetivo();
        progresoIndex = fragmentoA.getProgresoIndex();
        prioridad = fragmentoA.isPrioridad();

        fragmentoB = FragmentoB.newInstance(titulo, fechaInicioStr, fechaObjetivoStr, progresoIndex, prioridad, descripcion);
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

        fragmentoA = FragmentoA.newInstance(titulo, fechaInicioStr, fechaObjetivoStr, progresoIndex, prioridad);
        if (!fragmentoA.isAdded()) {
            fragmentManager.beginTransaction().replace(R.id.frag_container, fragmentoA).commit();
        }
    }

    @Override
    public void onBotonGuardarClicked() {
        descripcion = fragmentoB.getDescripcion();

        if (titulo.isEmpty() || fechaInicioStr.isEmpty() || fechaObjetivoStr.isEmpty() ||
                progresoIndex == Spinner.INVALID_POSITION || descripcion.isEmpty()) {
            HelperClass.showBasicAlertDialog(this, R.string.error, R.string.invalid_input_error);
        } else {
            Intent intent = new Intent();

            fechaInicioValue = HelperClass.stringToDate(fechaInicioStr);
            fechaObjetivoValue = HelperClass.stringToDate(fechaObjetivoStr);
            progresoValue = (byte) (25 * progresoIndex);

            Tarea tareaEditada = new Tarea(
                    titulo,
                    fechaInicioValue,
                    fechaObjetivoValue,
                    progresoValue,
                    prioridad,
                    descripcion
            );
            tareaEditada.setId(idTarea);

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

            // TODO fix exception here

            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onBotonAdjuntarArchivoClicked() {

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