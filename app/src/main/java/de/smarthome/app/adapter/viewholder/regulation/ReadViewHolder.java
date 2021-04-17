package de.smarthome.app.adapter.viewholder.regulation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.model.Datapoint;
import de.smarthome.app.adapter.RegulationAdapter;

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
    public void onBindViewHolder(RegulationAdapter.ViewHolder holder, int position, Datapoint datapoint, Optional<String> value) {
        textView.setText(datapoint.getName());
        //textView.setText(adapter.getDataPointAt(position).getName());

        value.ifPresent(s -> textViewOutput.setText(s));
    }
}
