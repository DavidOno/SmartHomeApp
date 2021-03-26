package de.smarthome.server.adapter.viewholder.regulation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.smarthome.R;
import de.smarthome.model.configs.ChannelDatapoint;
import de.smarthome.model.impl.Datapoint;
import de.smarthome.model.impl.Function;
import de.smarthome.server.adapter.RegulationAdapter;

public class StepViewHolder extends RegulationAdapter.ViewHolder{
    private TextView textView;
    private ImageView plus;
    private ImageView minus;
    private RegulationAdapter adapter;
    private RegulationAdapter.OnItemClickListener onItemClickListener;

    public StepViewHolder(@NonNull ViewGroup parent,
                             @NonNull RegulationAdapter.OnItemClickListener onItemClickListener,
                             @NonNull RegulationAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step, parent, false));
        textView = itemView.findViewById(R.id.textView_item);

        plus = itemView.findViewById(R.id.imageView_plus);
        minus = itemView.findViewById(R.id.imageView_minus);

        this.onItemClickListener = onItemClickListener;
        this.adapter = adapter;

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(adapter.getDatapointAt(position), "1");
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(adapter.getDatapointAt(position), "0");
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RegulationAdapter.ViewHolder holder, int position, Datapoint datapoint) {
        this.textView.setText(datapoint.getName());
    }
}

