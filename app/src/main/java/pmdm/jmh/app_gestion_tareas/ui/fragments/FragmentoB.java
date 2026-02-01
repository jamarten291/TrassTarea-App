package pmdm.jmh.app_gestion_tareas.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.ui.interfaces.DataArguments;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentoB#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentoB extends Fragment implements DataArguments {
    public interface ComunicacionFragmentoB {
        void onBotonVolverClicked();
        void onBotonGuardarClicked();
        void onFileAttached(View view);
        void onFileDeleted(View view);
    }
    private ComunicacionFragmentoB comunicador;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // Implementado en la interfaz
    private String descripcion;

    private EditText etDescripcion;
    private Button btVolver, btGuardar;
    private ImageButton btAdjImagen, btAdjVideo, btAdjDocumento, btAdjAudio;

    public FragmentoB() {
        // Required empty public constructor
    }

    public static FragmentoB newInstance(Bundle args, String desc) {
        FragmentoB fragment = new FragmentoB();
        fragment.setArguments(args);
        args.putString(ARG_PARAM6, desc);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ComunicacionFragmentoB) {
            comunicador = (ComunicacionFragmentoB) context;
        } else {
            throw new ClassCastException("La actividad debe implementar ComunicacionFragmentoA");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            descripcion = getArguments().getString(ARG_PARAM6);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentoB = inflater.inflate(R.layout.fragmento_segundo, container, false);

        // Bindings
        etDescripcion = fragmentoB.findViewById(R.id.et_descripcion);
        btGuardar = fragmentoB.findViewById(R.id.bt_guardar);
        btVolver = fragmentoB.findViewById(R.id.bt_volver);

        btAdjImagen = fragmentoB.findViewById(R.id.bt_imagen);
        btAdjVideo = fragmentoB.findViewById(R.id.bt_video);
        btAdjAudio = fragmentoB.findViewById(R.id.bt_audio);
        btAdjDocumento = fragmentoB.findViewById(R.id.bt_documento);

        // Listeners
        btGuardar.setOnClickListener(v -> comunicador.onBotonGuardarClicked());
        btVolver.setOnClickListener(v -> comunicador.onBotonVolverClicked());

        btAdjImagen.setOnClickListener(this::onClick);
        btAdjVideo.setOnClickListener(this::onClick);
        btAdjAudio.setOnClickListener(this::onClick);
        btAdjDocumento.setOnClickListener(this::onClick);

        // Datos de la descripción en caso de que hubiese una escrita
        etDescripcion.setText(descripcion);

        return fragmentoB;
    }

    public void onClick(View view) {
        // Construyo un AlertDialog en el que se puede Agregar o Borrar un archivo adjunto
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.titulo_dialog_archivo)
                .setMessage(R.string.mensaje_dialog_archivo)

                // Establezco un botón positivo el cual si es pulsado, lanzará el method abstracto
                // que adjunta archivos a la Tarea
                .setPositiveButton(R.string.item_agregar,
                        (dialog, which) -> {
                            comunicador.onFileAttached(view);
                        })

                // Establezco un botón negativo el cual si es pulsado, lanzará el method abstracto
                // que establece a null la URI del archivo, y si existe, lo borra
                .setNegativeButton(R.string.mc_borrar,
                        (dialog, which) -> {
                            comunicador.onFileDeleted(view);

                            // Muestro una pequeña advertencia al usuario y le recuerdo que la
                            // eliminación puede cancelarse si se cancela la edición o creación de
                            // la tarea
                            Toast.makeText(requireContext(), R.string.archivo_eliminado, Toast.LENGTH_SHORT).show();
                        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Getter para recuperar la descripción
    public String getDescripcion() {
        return etDescripcion.getText().toString();
    }
}