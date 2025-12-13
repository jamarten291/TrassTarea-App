package pmdm.jmh.app_gestion_tareas.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.controlador.HelperClass;
import pmdm.jmh.app_gestion_tareas.controlador.TareaAdapter;
import pmdm.jmh.app_gestion_tareas.modelo.DataArguments;
import pmdm.jmh.app_gestion_tareas.modelo.Tarea;

public class ListadoTareasActivity extends AppCompatActivity implements DataArguments {
    private ArrayList<Tarea> listaTareas;
    private TareaAdapter adaptadorTarea;
    private RecyclerView rvTareas;
    private TextView tvSinTareas;
    private boolean filtradoActualmente = false;
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();

                    int op = data.getIntExtra(ARG_OP, 0);
                    switch (op) {
                        case 1:
                            // getParcelable devuelve el objeto Tarea, que implementa Parcelable
                            Tarea nuevaTarea = data.getParcelableExtra(ARG_TAREA, Tarea.class);
                            listaTareas.add(nuevaTarea);

                            // Actualiza la vista del RecyclerView con la nueva tarea
                            rvTareas.setAdapter(new TareaAdapter(listaTareas));
                            Toast.makeText(this, R.string.operacion_agregar, Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            int idTarea = data.getIntExtra(ARG_ID_TAREA, -1);

                            if (idTarea != -1) {
                                // Optional devuelve un objeto que puede o no existir
                                Optional<Tarea> tareaOptional = listaTareas.stream()
                                        .filter(t -> t.getId() == idTarea)
                                        .findFirst();

                                // isPresent devuelve true si el objeto existe
                                if (tareaOptional.isPresent()) {
                                    Tarea tareaEditada = tareaOptional.get();

                                    tareaEditada.setTitulo(data.getStringExtra(ARG_PARAM1));
                                    tareaEditada.setFechaCreacion(data.getParcelableExtra(ARG_PARAM2, LocalDate.class));
                                    tareaEditada.setFechaObjetivo(data.getParcelableExtra(ARG_PARAM3, LocalDate.class));
                                    tareaEditada.setProgreso(data.getByteExtra(ARG_PARAM4, (byte) 0));
                                    tareaEditada.setPrioritaria(data.getBooleanExtra(ARG_PARAM5, false));
                                    tareaEditada.setDescripcion(data.getStringExtra(ARG_PARAM6));

                                    adaptadorTarea.notifyItemChanged(idTarea);
                                    Toast.makeText(this, R.string.operacion_actualizar, Toast.LENGTH_SHORT).show();
                                }
                            }
                        default:
                            Toast.makeText(this, R.string.operacion_error, Toast.LENGTH_SHORT);
                    }
                }
            });

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
        registerForContextMenu(rvTareas);

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
            HelperClass.showBasicAlertDialog(this, R.string.app_name, R.string.mensaje_acerca);
        } else if (id == R.id.item_agregar) {
            Intent intent = new Intent(this, CrearTareaActivity.class);
            launcher.launch(intent);
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

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // Recupero la posición actual del adaptador accediendo al id del grupo
        int position = item.getOrder();

        // Identificador del elemento del menú seleccionado que indica si se trata de editado o borrado
        int itemId = item.getItemId();

        if (position == RecyclerView.NO_POSITION) {
            return super.onContextItemSelected(item);
        }

        if (itemId == R.id.mc_editar) {
            Intent intent = new Intent(this, EditarTareaActivity.class);
            intent.putExtra(ARG_TAREA, listaTareas.get(position));
            launcher.launch(intent);
            return true;
        } else if (itemId == R.id.mc_borrar) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.titulo_dialog_borrar)
                    .setMessage(R.string.mensaje_dialog_borrar)
                    .setPositiveButton(R.string.alert_aceptar,
                            (dialog, which) -> {
                                listaTareas.remove(position);

                                // Notifico que ha habido una operación de borrado
                                adaptadorTarea.notifyItemRemoved(position);
                                Toast.makeText(this, R.string.operacion_borrar, Toast.LENGTH_SHORT).show();
                            })
                    .setNegativeButton(R.string.alert_cancelar, null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onContextItemSelected(item);
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
                LocalDate.now(),                  // fecha inicio
                LocalDate.now().plusDays(1),      // fecha fin
                (byte) 0,                             // progreso
                false,                                // prioritaria
                "Ir al supermercado y comprar pan, leche y huevos")); // descripción

        listaTareas.add(new Tarea(
                "Entrega informe",
                LocalDate.now().minusDays(3),
                LocalDate.now().plusDays(2),
                (byte) 45,
                true,
                "Redactar y enviar el informe mensual al jefe"));

        listaTareas.add(new Tarea(
                "Ejercicio diario",
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(19),
                (byte) 20,
                false,
                "30 minutos de cardio"));

        listaTareas.add(new Tarea(
                "Llamar al dentista",
                LocalDate.now().minusDays(2),
                LocalDate.now().plusWeeks(1),
                (byte) 0,
                true,
                "Programar cita para revisión dental"));

        listaTareas.add(new Tarea(
                "Leer libro",
                LocalDate.now().minusWeeks(1),
                LocalDate.now().plusWeeks(2),
                (byte) 60,
                false,
                "Avanzar 50 páginas en 'Sapiens'"));

        listaTareas.add(new Tarea(
                "Actualizar CV",
                LocalDate.now().minusDays(4),
                LocalDate.now().minusDays(3),
                (byte) 30,
                true,
                "Incluir últimos proyectos y certificaciones"));

        listaTareas.add(new Tarea(
                "Pagar facturas",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(5),
                (byte) 10,
                false,
                "Luz, agua e internet"));

        listaTareas.add(new Tarea(
                "Organizar escritorio",
                LocalDate.now().minusDays(8),
                LocalDate.now().plusDays(16),
                (byte) 80,
                false,
                "Despejar papeles y ordenar cables"));

        listaTareas.add(new Tarea(
                "Reunión de equipo",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                (byte) 0,
                true,
                "Planificar sprint de dos semanas"));

        listaTareas.add(new Tarea(
                "Backup de datos",
                LocalDate.now().minusDays(6),
                LocalDate.now().plusDays(2),
                (byte) 50,
                false,
                "Copiar documentos importantes a disco externo"));
    }

}