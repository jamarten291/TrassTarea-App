package pmdm.jmh.app_gestion_tareas.vista;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.modelo.DataArguments;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentoB#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentoB extends Fragment implements DataArguments {
    public interface ComunicacionFragmentoB {
        void onBotonVolverClicked();
        void onBotonGuardarClicked();
        void onBotonAdjuntarArchivoClicked();
    }
    private ComunicacionFragmentoB comunicador;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // Implementado en la interfaz

    private String titulo;
    private String fechaInicio;
    private String fechaObjetivo;
    private int progresoIndex;
    private boolean prioridad;
    private String descripcion;

    private EditText etDescripcion;
    private Button btVolver, btGuardar;
    private ImageButton btAdjImagen, btAdjVideo, btAdjDocumento, btAdjAudio;

    public FragmentoB() {
        // Required empty public constructor
    }

    public static FragmentoB newInstance(String param1, String param2, String param3, int param4, boolean param5, String param6) {
        FragmentoB fragment = new FragmentoB();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putInt(ARG_PARAM4, param4);
        args.putBoolean(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        fragment.setArguments(args);
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
            titulo = getArguments().getString(ARG_PARAM1);
            fechaInicio = getArguments().getString(ARG_PARAM2);
            fechaObjetivo = getArguments().getString(ARG_PARAM3);
            progresoIndex = getArguments().getInt(ARG_PARAM4);
            prioridad = getArguments().getBoolean(ARG_PARAM5);
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

        btAdjImagen.setOnClickListener(v -> comunicador.onBotonAdjuntarArchivoClicked());
        btAdjVideo.setOnClickListener(v -> comunicador.onBotonAdjuntarArchivoClicked());
        btAdjAudio.setOnClickListener(v -> comunicador.onBotonAdjuntarArchivoClicked());
        btAdjDocumento.setOnClickListener(v -> comunicador.onBotonAdjuntarArchivoClicked());

        // Datos de la descripción en caso de que hubiese una escrita
        etDescripcion.setText(descripcion);

        return fragmentoB;
    }

    public String getDescripcion() {
        return etDescripcion.getText().toString();
    }

    // Getters para recuperar los datos desde la actividad (recupera las variables)
    public String getTitulo() {
        return titulo;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaObjetivo() {
        return fechaObjetivo;
    }

    public boolean isPrioridad() {
        return prioridad;
    }

    public int getProgresoIndex() {
        return progresoIndex;
    }
}