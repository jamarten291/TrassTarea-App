package pmdm.jmh.app_gestion_tareas.ui.tarea;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.ui.fragments.ImageDialogFragment;
import pmdm.jmh.app_gestion_tareas.ui.fragments.VideoDialogFragment;
import pmdm.jmh.app_gestion_tareas.ui.helpers.FileUtils;
import pmdm.jmh.app_gestion_tareas.ui.interfaces.DataArguments;

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

            String imageUri = data.getString(ARG_IMAGEN);
            if (imageUri != null) {
                tvImg.setText(FileUtils.getFileNameFromPath(imageUri));
                tvImg.setOnClickListener(v -> {
                    ImageDialogFragment dialog = new ImageDialogFragment(imageUri);
                    dialog.show(getSupportFragmentManager(), "image_dialog");
                });
            } else {
                tvImg.setText(R.string.no_archivo);
            }

            String videoUri = data.getString(ARG_VIDEO);
            if (videoUri != null) {
                tvVid.setText(FileUtils.getFileNameFromPath(videoUri));
                tvVid.setOnClickListener(v -> {
                    VideoDialogFragment dialog = new VideoDialogFragment(videoUri);
                    dialog.show(getSupportFragmentManager(), "video_dialog");
                });
            } else {
                tvVid.setText(R.string.no_archivo);
            }

            String audioUri = data.getString(ARG_AUDIO);
            if (audioUri != null) {
                tvAud.setText(FileUtils.getFileNameFromPath(audioUri));
                MediaPlayer mp = MediaPlayer.create(this, Uri.parse(audioUri));
                mp.setLooping(false);

                // Listener que reproduce el audio cuando se pulsa sobre el TextView
                tvAud.setOnClickListener(v -> {
                    if (mp.isPlaying()) {
                        mp.pause();
                    } else {
                        mp.start();
                    }
                });
            } else {
                tvAud.setText(R.string.no_archivo);
            }

            String docUri = data.getString(ARG_DOC);
            if (docUri != null) {
                tvDoc.setText(FileUtils.getFileNameFromPath(docUri));
                tvDoc.setOnClickListener(v -> {

                });
            } else {
                tvDoc.setText(R.string.no_archivo);
            }
        }
    }
}