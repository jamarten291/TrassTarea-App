package pmdm.jmh.app_gestion_tareas.vista;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.util.ArrayList;

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.controlador.TareaAdapter;
import pmdm.jmh.app_gestion_tareas.modelo.Tarea;

public class ListadoTareasActivity extends AppCompatActivity {

    private ArrayList<Tarea> listaTareas;
    private TareaAdapter adaptadorTarea;
    private RecyclerView rvTareas;
    private TextView tvSinTareas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listado_tareas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bindings
        rvTareas = findViewById(R.id.rv_tareas);
        tvSinTareas = findViewById(R.id.tv_sin_tareas);

        // Creación de tareas
        listaTareas = new ArrayList<>();
        crearTareas();

        // Visibilidad según haya notas o no
        rvTareas.setVisibility(listaTareas.isEmpty() ?
                RecyclerView.INVISIBLE :
                RecyclerView.VISIBLE);

        tvSinTareas.setVisibility(listaTareas.isEmpty() ?
                TextView.VISIBLE :
                TextView.INVISIBLE);

        // Inicialización de adaptador del RecyclerView
        adaptadorTarea = new TareaAdapter(listaTareas);
        rvTareas.setAdapter(adaptadorTarea);
        rvTareas.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL,
                        false)
        );
        rvTareas.setOnClickListener(v -> {

        });
    }



    private void crearTareas() {
        listaTareas.add(new Tarea(
                "Comprar víveres",
                "Ir al supermercado y comprar pan, leche y huevos",
                (byte) 0,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                false));

        listaTareas.add(new Tarea(
                "Entrega informe",
                "Redactar y enviar el informe mensual al jefe",
                (byte) 45,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(2),
                true));

        listaTareas.add(new Tarea(
                "Ejercicio diario",
                "30 minutos de cardio",
                (byte) 20,
                LocalDateTime.now().minusHours(5),
                LocalDateTime.now().plusHours(19),
                false));

        listaTareas.add(new Tarea(
                "Llamar al dentista",
                "Programar cita para revisión dental",
                (byte) 0,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusWeeks(1),
                true));

        listaTareas.add(new Tarea(
                "Leer libro",
                "Avanzar 50 páginas en 'Sapiens'",
                (byte) 60,
                LocalDateTime.now().minusWeeks(1),
                LocalDateTime.now().plusWeeks(2),
                false));

        listaTareas.add(new Tarea(
                "Actualizar CV",
                "Incluir últimos proyectos y certificaciones",
                (byte) 30,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(3),
                true));

        listaTareas.add(new Tarea(
                "Pagar facturas",
                "Luz, agua e internet",
                (byte) 10,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(5),
                false));

        listaTareas.add(new Tarea(
                "Organizar escritorio",
                "Despejar papeles y ordenar cables",
                (byte) 80,
                LocalDateTime.now().minusHours(8),
                LocalDateTime.now().plusHours(16),
                false));

        listaTareas.add(new Tarea(
                "Reunión de equipo",
                "Planificar sprint de dos semanas",
                (byte) 0,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                true));

        listaTareas.add(new Tarea(
                "Backup de datos",
                "Copiar documentos importantes a disco externo",
                (byte) 50,
                LocalDateTime.now().minusDays(6),
                LocalDateTime.now().plusDays(2),
                false));
    }
}