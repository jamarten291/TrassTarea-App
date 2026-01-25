package pmdm.jmh.app_gestion_tareas.ui.crud.listado;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.basedatos.DatabaseApp;
import pmdm.jmh.app_gestion_tareas.controlador.HelperClass;
import pmdm.jmh.app_gestion_tareas.adaptadores.TareaAdapter;
import pmdm.jmh.app_gestion_tareas.interfaces.DataArguments;
import pmdm.jmh.app_gestion_tareas.entidades.Tarea;
import pmdm.jmh.app_gestion_tareas.ui.crud.CrearTareaActivity;
import pmdm.jmh.app_gestion_tareas.ui.crud.EditarTareaActivity;
import pmdm.jmh.app_gestion_tareas.ui.prefs.SettingsActivity;

public class ListadoTareasActivity extends AppCompatActivity implements DataArguments {
    private List<Tarea> listaTareas = new ArrayList<>();
    private TareaAdapter adaptadorTarea;
    private RecyclerView rvTareas;
    private TextView tvSinTareas;
    private boolean filtradoActualmente = false;
    private DatabaseApp databaseApp;
    private ListadoTareasViewModel viewModel;
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();

                    int op = data.getIntExtra(ARG_OP, 0);
                    switch (op) {
                        case 1:
                            Toast.makeText(this, R.string.operacion_agregar, Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            // TODO implement edit functionality
                            break;
                        default:
                            Toast.makeText(this, R.string.operacion_error, Toast.LENGTH_SHORT).show();
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

        // Se instancia un DatabaseApp
        databaseApp = DatabaseApp.getInstance(getApplicationContext());

        // Inicialización de adaptador del RecyclerView
        adaptadorTarea = new TareaAdapter(listaTareas);
        rvTareas.setAdapter(adaptadorTarea);
        rvTareas.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL,
                        false)
        );

        // Se inicia el ViewModel para poder ver los cambios de los datos en tiempo real
        viewModel = new ViewModelProvider(this).get(ListadoTareasViewModel.class);
        viewModel.getTareas().observe(this, v -> {
            // Se actualiza la lista del RecyclerView
            adaptadorTarea.setDatos(v);

            // Se actualiza la lista actual
            listaTareas.clear();
            listaTareas.addAll(v);

            // Visibilidad según haya notas o no
            rvTareas.setVisibility(v.isEmpty() ?
                    RecyclerView.INVISIBLE :
                    RecyclerView.VISIBLE
            );
            tvSinTareas.setVisibility(v.isEmpty() ?
                    TextView.VISIBLE :
                    TextView.INVISIBLE
            );
        });


        if (adaptadorTarea.getDatos().isEmpty()) {
            rvTareas.setVisibility(RecyclerView.INVISIBLE);
            tvSinTareas.setVisibility(TextView.VISIBLE);
        } else {
            rvTareas.setVisibility(RecyclerView.VISIBLE);
            tvSinTareas.setVisibility(TextView.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences userDetails = PreferenceManager.getDefaultSharedPreferences(this);

        // Se aplican las preferencias
        boolean temaClaro = userDetails.getBoolean("tema", true);
        AppCompatDelegate.setDefaultNightMode(
                temaClaro
                        ? AppCompatDelegate.MODE_NIGHT_NO
                        : AppCompatDelegate.MODE_NIGHT_YES
        );

        String fuente = userDetails.getString("fuente", "2");
        if (fuente.equals("1")) {
            setTheme(R.style.Theme_App_SmallText);
        } else if (fuente.equals("3")) {
            setTheme(R.style.Theme_App_LargeText);
        } else {
            setTheme(R.style.Theme_App_NormalText);
        }

        String criterioOrden = userDetails.getString("criterio", "2");
        boolean ordenAsc = userDetails.getBoolean("orden", true);
        boolean almacenamientoSd = userDetails.getBoolean("sd", false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        menu.setGroupVisible(R.id.item_acerca, true);
        menu.setGroupVisible(R.id.item_agregar, true);
        menu.setGroupVisible(R.id.item_prioritarias, true);
        menu.setGroupVisible(R.id.item_preferencias, true);
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
                // TODO implement filter
                filtradoActualmente = true;
            } else {
                // Si está filtrado, establece el adaptador al adaptador original
                rvTareas.setAdapter(adaptadorTarea);
                filtradoActualmente = false;
            }
        } else if (id == R.id.item_preferencias) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.item_salir) {
            Toast.makeText(this, R.string.despedida_toast, Toast.LENGTH_SHORT).show();
            finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // Identificador del elemento del menú seleccionado que indica si se trata de editado o borrado
        int itemId = item.getItemId();

        // Se intenta obtener la posición de la tarea seleccionada
        int posicion = -1;
        try {
            posicion = adaptadorTarea.getPosicion();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }

        // Se instancia un objeto que representa la tarea seleccionada actualmente
        Tarea tareaSeleccionada = adaptadorTarea.getDatos().get(posicion);

        if (itemId == R.id.mc_editar) {
            Intent intent = new Intent(this, EditarTareaActivity.class);
            intent.putExtra(ARG_TAREA, tareaSeleccionada);
            launcher.launch(intent);
            return true;
        } else if (itemId == R.id.mc_borrar) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.titulo_dialog_borrar)
                    .setMessage(R.string.mensaje_dialog_borrar)
                    .setPositiveButton(R.string.alert_aceptar,
                            (dialog, which) -> {
                                Executor executor = Executors.newSingleThreadExecutor();
                                executor.execute(new EliminarTarea(tareaSeleccionada));
                            })
                    .setNegativeButton(R.string.alert_cancelar, null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    class EliminarTarea implements Runnable {
        private Tarea tarea;

        public EliminarTarea(Tarea tarea) {
            this.tarea = tarea;
        }

        public EliminarTarea(int id) {
            // Busca por id y devuelve la primera ocurrencia
            this.tarea = databaseApp
                    .tareaDAO()
                    .loadAllByIds(new int[] {id})
                    .get(0);
        }

        @Override
        public void run() {
            databaseApp.tareaDAO().delete(this.tarea);
        }
    }
}