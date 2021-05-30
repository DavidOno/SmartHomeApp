package de.smarthome.app.adapter.viewholder.regulation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.model.Datapoint;
import de.smarthome.app.adapter.RegulationAdapter;

public class StepViewHolder extends RegulationAdapter.ViewHolder{
    private TextView textViewName;
    private ImageButton plus;
    private ImageButton minus;

    public StepViewHolder(@NonNull ViewGroup parent,
                          @NonNull RegulationAdapter.OnItemClickListener onItemClickListener,
                          @NonNull RegulationAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step, parent, false));
        textViewName = itemView.findViewById(R.id.textView_item);

        plus = itemView.findViewById(R.id.imageView_plus);
        minus = itemView.findViewById(R.id.imageView_minus);

        plus.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                onItemClickListener.onItemClick(adapter.getDataPointAt(position), "1");
            }
        });

        minus.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                onItemClickListener.onItemClick(adapter.getDataPointAt(position), "0");
            }
        });
    }

    @Override
    public void onBindViewHolder(RegulationAdapter.ViewHolder holder, int position, Datapoint datapoint, Optional<String> value) {
        this.textViewName.setText(datapoint.getName().replace("_", " "));
    }
}