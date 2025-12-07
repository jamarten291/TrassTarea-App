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

import pmdm.jmh.app_gestion_tareas.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentoB#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentoB extends Fragment {
    public interface ComunicacionFragmentoB {
        void onBotonVolverClicked();
        void onBotonGuardarClicked();
    }
    private ComunicacionFragmentoB comunicador;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "titulo";
    private static final String ARG_PARAM2 = "fechaInicio";
    private static final String ARG_PARAM3 = "fechaObjetivo";
    private static final String ARG_PARAM4 = "progreso";
    private static final String ARG_PARAM5 = "prioridad";
    private static final String ARG_PARAM6 = "descripcion";

    private String titulo;
    private String fechaInicio;
    private String fechaObjetivo;
    private byte progreso;
    private boolean prioridad;
    private String descripcion;

    private EditText etDescripcion;
    private Button btVolver;
    private Button btGuardar;

    public FragmentoB() {
        // Required empty public constructor
    }

    public static FragmentoB newInstance(String param1, String param2, String param3, byte param4, boolean param5, String param6) {
        FragmentoB fragment = new FragmentoB();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putByte(ARG_PARAM4, param4);
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
            progreso = getArguments().getByte(ARG_PARAM4);
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

        // Listeners
        btGuardar.setOnClickListener(v -> comunicador.onBotonGuardarClicked());
        btVolver.setOnClickListener(v -> comunicador.onBotonVolverClicked());

        return fragmentoB;
    }

    public String getDescripcion() {
        return etDescripcion.getText().toString();
    }
}