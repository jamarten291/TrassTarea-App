package pmdm.jmh.app_gestion_tareas.vista;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.controlador.DatePickerFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentoA#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentoA extends Fragment {
    public interface ComunicacionFragmentoA {
        void onBotonSiguienteClicked();
    }
    private ComunicacionFragmentoA comunicador;
    private List<String> progresoItems = Arrays.asList("No iniciada", "Iniciada", "Avanzada", "Casi finalizada", "Finalizada");

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "titulo";
    private static final String ARG_PARAM2 = "fechaInicio";
    private static final String ARG_PARAM3 = "fechaObjetivo";
    private static final String ARG_PARAM4 = "progresoIndex";
    private static final String ARG_PARAM5 = "prioridad";

    // Elementos de la vista
    private EditText etTitulo;
    private EditText etFechaCreacion;
    private EditText etFechaObjetivo;
    private Spinner spProgreso;
    private CheckBox cbPrioritaria;
    private Button btSiguiente;

    // Valores
    private String titulo;
    private String fechaInicio;
    private String fechaObjetivo;
    private int progresoIndex;
    private boolean prioridad;

    public FragmentoA() {
        // Required empty public constructor
    }

    public static FragmentoA newInstance(String param1, String param2, String param3, int param4, boolean param5) {
        FragmentoA fragment = new FragmentoA();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putInt(ARG_PARAM4, param4);
        args.putBoolean(ARG_PARAM5, param5);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ComunicacionFragmentoA) {
            comunicador = (ComunicacionFragmentoA) context;
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentoA = inflater.inflate(R.layout.fragmento_primero, container, false);

        // Bindings
        etTitulo = fragmentoA.findViewById(R.id.et_titulo);
        etFechaCreacion = fragmentoA.findViewById(R.id.et_fecha_inicio);
        etFechaObjetivo= fragmentoA.findViewById(R.id.et_fecha_objetivo);
        spProgreso = fragmentoA.findViewById(R.id.sp_progreso);
        cbPrioritaria = fragmentoA.findViewById(R.id.cb_prioridad);
        btSiguiente = fragmentoA.findViewById(R.id.bt_siguiente);

        // Adaptador
        ArrayAdapter<String> adaptadorStrings = new ArrayAdapter<>(
                Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_dropdown_item,
                progresoItems
        );
        spProgreso.setAdapter(adaptadorStrings);

        // Listeners
        btSiguiente.setOnClickListener(v -> comunicador.onBotonSiguienteClicked());

        // Para evitar dobles clicks
        etFechaCreacion.setFocusable(false);
        etFechaObjetivo.setFocusable(false);
        etFechaCreacion.setClickable(true);
        etFechaObjetivo.setClickable(true);

        etFechaCreacion.setOnClickListener(this::onDatePickerClicked);
        etFechaObjetivo.setOnClickListener(this::onDatePickerClicked);

        // Datos de la tarea (en caso de haber recibido datos)
        if (getArguments() != null) {
            etTitulo.setText(titulo);
            etFechaCreacion.setText(fechaInicio);
            etFechaObjetivo.setText(fechaObjetivo);
            spProgreso.setSelection(progresoIndex);
            cbPrioritaria.setChecked(prioridad);
        }

        return fragmentoA;
    }

    public void onDatePickerClicked(View view) {
        // Creo una nueva instancia y le paso el id que referencia al edit text que lo llama
        DatePickerFragment dateFragment = DatePickerFragment.newInstance(view.getId());
        dateFragment.show(getParentFragmentManager(), "datePicker");
    }

    // Getters para recuperar los datos desde la actividad
    public String getTitulo() {
        return etTitulo.getText().toString();
    }

    public String getFechaInicio() {
        return etFechaCreacion.getText().toString();
    }

    public String getFechaObjetivo() {
        return etFechaObjetivo.getText().toString();
    }

    public int getProgresoIndex() {
        return spProgreso.getSelectedItemPosition();
    }

    public boolean isPrioridad() {
        return cbPrioritaria.isChecked();
    }
}