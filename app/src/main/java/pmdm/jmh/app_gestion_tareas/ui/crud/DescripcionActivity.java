package pmdm.jmh.app_gestion_tareas.ui.crud;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.interfaces.DataArguments;

public class DescripcionActivity extends AppCompatActivity
    implements DataArguments {

    private TextView tvDesc, tvImg, tvVid, tvAud, tvDoc;
    private Button btnSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_descripcion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDesc = findViewById(R.id.tv_descripcion);
        tvImg = findViewById(R.id.cell_img);
        tvVid = findViewById(R.id.cell_vid);
        tvAud = findViewById(R.id.cell_aud);
        tvDoc = findViewById(R.id.cell_doc);

        btnSalir = findViewById(R.id.bt_salir);

        btnSalir.setOnClickListener(v -> finish());

        Bundle data = getIntent().getExtras();
        if (data != null) {
            tvDesc.setText(data.getString(ARG_PARAM6));
            tvImg.setText(data.getString(ARG_IMAGEN));
            tvVid.setText(data.getString(ARG_VIDEO));
            tvAud.setText(data.getString(ARG_AUDIO));
            tvDoc.setText(data.getString(ARG_DOC));
        } else {
            tvDesc.setText("");
            tvImg.setText("");
            tvVid.setText("");
            tvAud.setText("");
            tvDoc.setText("");
        }
    }
}