package pmdm.jmh.app_gestion_tareas.vista;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import java.time.LocalDate;

import pmdm.jmh.app_gestion_tareas.R;

public class EditarTareaActivity extends AppCompatActivity implements
        FragmentoA.ComunicacionFragmentoA,
        FragmentoB.ComunicacionFragmentoB
{

    private final String ARG_TAREA = "tarea";
    private final String ARG_OP = "operacion";
    private static final String ARG_PARAM1 = "titulo";
    private static final String ARG_PARAM2 = "fechaInicio";
    private static final String ARG_PARAM3 = "fechaObjetivo";
    private static final String ARG_PARAM4 = "progreso";
    private static final String ARG_PARAM5 = "prioridad";
    private static final String ARG_PARAM6 = "descripcion";
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

        fragmentoA = new FragmentoA();

        fragmentManager = getSupportFragmentManager();

        if(savedInstanceState == null)
            fragmentManager.beginTransaction().add(R.id.frag_container, fragmentoA).commit();
    }

    @Override
    public void onBotonSiguienteClicked() {
        titulo = fragmentoA.getTitulo();
        fechaInicioStr = fragmentoA.getFechaInicio();
        fechaObjetivoStr = fragmentoA.getFechaObjetivo();
        progresoIndex = fragmentoA.getProgresoIndex();
        prioridad = fragmentoA.isPrioridad();
        descripcion = fragmentoA.getDescripcion();

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

        fragmentoA = FragmentoA.newInstance(titulo, fechaInicioStr, fechaObjetivoStr, progresoIndex, prioridad, descripcion);
        if (!fragmentoA.isAdded()) {
            fragmentManager.beginTransaction().replace(R.id.frag_container, fragmentoA).commit();
        }
    }

    @Override
    public void onBotonGuardarClicked() {

    }
}