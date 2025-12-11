package pmdm.jmh.app_gestion_tareas.controlador;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.modelo.Tarea;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {
    private final ArrayList<Tarea> adaptadorTarea;

    public TareaAdapter(ArrayList<Tarea> adaptadorTarea) {
        this.adaptadorTarea = adaptadorTarea;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new TareaViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tareaActual = adaptadorTarea.get(position);
        holder.setTarea(tareaActual);
    }

    @Override
    public int getItemCount() {
        return adaptadorTarea.size();
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombreTarea;
        private ProgressBar progressTarea;
        private TextView tvFechaLimite;
        private TextView tvCantDias;
        private Context c;
        private Tarea tareaActual;

        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreTarea = itemView.findViewById(R.id.tv_nombre_tarea);
            progressTarea = itemView.findViewById(R.id.progress_tarea);
            tvFechaLimite = itemView.findViewById(R.id.tv_fecha_limite);
            tvCantDias = itemView.findViewById(R.id.tv_cant_dias);

            // Guarda el contexto del ItemView, desde el cual se podrá acceder a los recursos de la tarea
            c = itemView.getContext();

            itemView.setOnClickListener(this::onItemViewClick);
            itemView.setOnCreateContextMenuListener(
                    (menu, v, menuInfo) -> {
                        int position = getBindingAdapterPosition();

                        if (position == RecyclerView.NO_POSITION) return;

                        // Asigno como groupId el valor de la posición actual en el adaptador
                        menu.add(position, R.id.mc_editar,
                                Menu.NONE, R.string.mc_editar);
                        menu.add(position, R.id.mc_borrar,
                                Menu.NONE, R.string.mc_borrar);
                    });
        }

        public void setTarea(Tarea t) {
            tvNombreTarea.setText(t.getTitulo());
            progressTarea.setProgress(t.getProgreso(), true);
            tvFechaLimite.setText(HelperClass.getFormattedDate(t.getFechaLimite()));

            if (t.isPrioritaria()) {
                // El operador | agrega otro flag sin perder los flags anteriores
                tvNombreTarea.setPaintFlags(tvNombreTarea.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

                // Añado un drawable al inicio (left) y dependiendo de si es prioritaria o no, le asigno un ícono
                tvNombreTarea.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(c, android.R.drawable.btn_star_big_on),
                        null,
                        null,
                        null
                );
            } else {
                tvNombreTarea.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(c, android.R.drawable.btn_star_big_off),
                        null,
                        null,
                        null
                );
            }

            // Comprueba si la tarea no ha sido completada antes de su fecha de entrega
            if (t.getDiasRestantes() < 0) {
                tvCantDias.setText("0");
                tvCantDias.setTextColor(Color.RED);
            } else {
                tvCantDias.setText(String.valueOf(t.getDiasRestantes()));
            }

            // Comprueba si la tarea ha sido terminada
            if (t.getProgreso() == 100) {
                tvCantDias.setText("0");

                // Esta flag tacha el TextView si el progreso es 100 (es decir, tarea terminada)
                tvNombreTarea.setPaintFlags(tvCantDias.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            // Asigno la tarea actual al adaptador para que al dar click se acceda a sus propiedades
            tareaActual = t;
        }

        public void onItemViewClick(View v) {
            // Consigo la posición del adaptador para acceder a la tarea clickeada y mostrar su descripción
            int pos = getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                String titulo = tareaActual.getTitulo();
                String descripcion = tareaActual.getDescripcion();

                HelperClass.showBasicAlertDialog(c, titulo, descripcion);
            }
        }
    }
}
