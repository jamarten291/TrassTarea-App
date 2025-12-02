package pmdm.jmh.app_gestion_tareas.controlador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        holder.setTarea(adaptadorTarea.get(position));
    }

    @Override
    public int getItemCount() {
        return adaptadorTarea.size();
    }

    static class TareaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombreTarea;
        private ProgressBar progressTarea;
        private TextView tvFechaLimite;
        private TextView tvCantDias;

        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreTarea = itemView.findViewById(R.id.tv_nombre_tarea);
            progressTarea = itemView.findViewById(R.id.progress_tarea);
            tvFechaLimite = itemView.findViewById(R.id.tv_fecha_limite);
            tvCantDias = itemView.findViewById(R.id.tv_cant_dias);
        }

        public void setTarea(Tarea t) {
            tvNombreTarea.setText(t.getTitulo());
            progressTarea.setProgress(t.getProgreso(), true);
            tvFechaLimite.setText(t.getFechaLimiteFormateada());
            tvCantDias.setText(String.valueOf(t.getDiasRestantes()));
        }
    }
}
