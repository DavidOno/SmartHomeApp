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
    private TextView textViewName;
    private TextView textViewOutput;

    public ReadViewHolder(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_read, parent, false));
        textViewName = itemView.findViewById(R.id.textView_item);

        textViewOutput = itemView.findViewById(R.id.textView_read_output_item);
    }

    @Override
    public void onBindViewHolder(RegulationAdapter.ViewHolder holder, int position, Datapoint datapoint, Optional<String> value) {
        textViewName.setText(datapoint.getName().replace("_", " "));

        value.ifPresent(s -> textViewOutput.setText(s));
    }
}
