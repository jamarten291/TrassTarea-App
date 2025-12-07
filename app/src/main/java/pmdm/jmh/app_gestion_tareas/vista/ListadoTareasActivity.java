package pmdm.jmh.app_gestion_tareas.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.controlador.HelperClass;
import pmdm.jmh.app_gestion_tareas.controlador.TareaAdapter;
import pmdm.jmh.app_gestion_tareas.modelo.Tarea;

public class ListadoTareasActivity extends AppCompatActivity {

    private ArrayList<Tarea> listaTareas;
    private TareaAdapter adaptadorTarea;
    private RecyclerView rvTareas;
    private TextView tvSinTareas;
    private boolean filtradoActualmente = false;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        menu.setGroupVisible(R.id.item_acerca, true);
        menu.setGroupVisible(R.id.item_agregar, true);
        menu.setGroupVisible(R.id.item_prioritarias, true);
        menu.setGroupVisible(R.id.item_salir, true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_acerca) {
            HelperClass.showBasicAlertDialog(this, R.string.app_actionbar_title, R.string.mensaje_acerca);
        } else if (id == R.id.item_agregar) {
            Intent intent = new Intent(this, CrearTareaActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.item_prioritarias) {
            if (!filtradoActualmente) {
                // Si no está filtrado, crea un adaptador con la lista de tareas filtradas
                rvTareas.setAdapter(getAdaptadorTareasFiltradas());
                filtradoActualmente = true;
            } else {
                // Si está filtrado, establece el adaptador al adaptador original
                rvTareas.setAdapter(adaptadorTarea);
                filtradoActualmente = false;
            }
        } else if (id == R.id.item_salir) {
            Toast.makeText(this, R.string.despedida_toast, Toast.LENGTH_SHORT).show();
            finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }

    private TareaAdapter getAdaptadorTareasFiltradas() {
        // Copia la lista de tareas, la filtra y construye un TareaAdapter con la lista filtrada
        ArrayList<Tarea> tareasFiltradas = (ArrayList<Tarea>) List.copyOf(listaTareas)
                .stream()
                .filter(Tarea::isPrioritaria)
                .collect(Collectors.toList());

        return new TareaAdapter(tareasFiltradas);
    }

    private void crearTareas() {
        listaTareas.add(new Tarea(
                "Comprar víveres",                     // título
                LocalDateTime.now(),                  // fecha inicio
                LocalDateTime.now().plusDays(1),      // fecha fin
                (byte) 0,                             // progreso
                false,                                // prioritaria
                "Ir al supermercado y comprar pan, leche y huevos")); // descripción

        listaTareas.add(new Tarea(
                "Entrega informe",
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(2),
                (byte) 45,
                true,
                "Redactar y enviar el informe mensual al jefe"));

        listaTareas.add(new Tarea(
                "Ejercicio diario",
                LocalDateTime.now().minusHours(5),
                LocalDateTime.now().plusHours(19),
                (byte) 20,
                false,
                "30 minutos de cardio"));

        listaTareas.add(new Tarea(
                "Llamar al dentista",
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusWeeks(1),
                (byte) 0,
                true,
                "Programar cita para revisión dental"));

        listaTareas.add(new Tarea(
                "Leer libro",
                LocalDateTime.now().minusWeeks(1),
                LocalDateTime.now().plusWeeks(2),
                (byte) 60,
                false,
                "Avanzar 50 páginas en 'Sapiens'"));

        listaTareas.add(new Tarea(
                "Actualizar CV",
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(3),
                (byte) 30,
                true,
                "Incluir últimos proyectos y certificaciones"));

        listaTareas.add(new Tarea(
                "Pagar facturas",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(5),
                (byte) 10,
                false,
                "Luz, agua e internet"));

        listaTareas.add(new Tarea(
                "Organizar escritorio",
                LocalDateTime.now().minusHours(8),
                LocalDateTime.now().plusHours(16),
                (byte) 80,
                false,
                "Despejar papeles y ordenar cables"));

        listaTareas.add(new Tarea(
                "Reunión de equipo",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                (byte) 0,
                true,
                "Planificar sprint de dos semanas"));

        listaTareas.add(new Tarea(
                "Backup de datos",
                LocalDateTime.now().minusDays(6),
                LocalDateTime.now().plusDays(2),
                (byte) 50,
                false,
                "Copiar documentos importantes a disco externo"));
    }

}