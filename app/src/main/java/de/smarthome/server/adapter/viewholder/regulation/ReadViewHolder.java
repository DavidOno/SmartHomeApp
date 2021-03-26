package de.smarthome.server.adapter.viewholder.regulation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.smarthome.R;
import de.smarthome.model.configs.ChannelDatapoint;
import de.smarthome.model.impl.Datapoint;
import de.smarthome.model.impl.Function;
import de.smarthome.server.adapter.RegulationAdapter;

public class ReadViewHolder extends RegulationAdapter.ViewHolder{
    private TextView textView;
    private TextView textViewOutput;

    private RegulationAdapter adapter;
    private RegulationAdapter.OnItemClickListener onItemClickListener;

    public ReadViewHolder(@NonNull ViewGroup parent,
                            @NonNull RegulationAdapter.OnItemClickListener onItemClickListener,
                            @NonNull RegulationAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_read, parent, false));
        textView = itemView.findViewById(R.id.textView_item);

        textViewOutput = itemView.findViewById(R.id.textView_read_output_item);

        this.onItemClickListener = onItemClickListener;
        this.adapter = adapter;

        /*textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(adapter.getChannelDatapointAt(position));
                }
            }
        });*/
    }

    @Override
    public void onBindViewHolder(RegulationAdapter.ViewHolder holder, int position, Datapoint datapoint) {
        this.textView.setText(datapoint.getName());
    }
}
