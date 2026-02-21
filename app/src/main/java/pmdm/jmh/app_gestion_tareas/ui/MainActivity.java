package pmdm.jmh.app_gestion_tareas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.ui.tarea.list.ListadoTareasActivity;

public class MainActivity extends AppCompatActivity {

    private Button btEntrar;
    private ImageView logo;
    private TextView eslogan;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bindings
        btEntrar = findViewById(R.id.bt_entrar);
        logo = findViewById(R.id.iv_logo);
        eslogan = findViewById(R.id.tv_slogan);

        // Animaciones
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up_down);
        Animation sloganAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Listener
        btEntrar.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListadoTareasActivity.class);
            startActivity(intent);
            finish();
        });

        // Executor que lanza las animaciones
        executor.execute(() -> runOnUiThread(() -> {
            logo.startAnimation(logoAnimation);
            eslogan.startAnimation(sloganAnimation);
        }));
    }
}